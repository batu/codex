// See www.openfst.org for extensive documentation on this weighted
// finite-state transducer library.
//

#include <fst/mapped-file.h>

#include <errno.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>

#include <algorithm>
#include <ios>

static const size_t kMaxReadChunk = 256 * 1024 * 1024;  // 256 MB

namespace fst {

// Alignment required for mapping structures (in bytes.)  Regions of memory
// that are not aligned upon a 128 bit boundary will be read from the file
// instead.  This is consistent with the alignment boundary set in the
// const and compact fst code.
const int MappedFile::kArchAlignment = 16;

MappedFile::MappedFile(const MemoryRegion& region) : region_(region) {}

MappedFile::~MappedFile() {
  if (region_.size != 0) {
    if (region_.mmap != NULL) {
      VLOG(1) << "munmap'ed " << region_.size << " bytes at " << region_.mmap;
      if (munmap(region_.mmap, region_.size) != 0) {
        LOG(ERROR) << "Failed to unmap region: " << strerror(errno);
      }
    } else {
      if (region_.data != 0) {
        operator delete(static_cast<char*>(region_.data) - region_.offset);
      }
    }
  }
}

MappedFile* MappedFile::Allocate(size_t size, int align) {
  MemoryRegion region;
  region.data = 0;
  region.offset = 0;
  if (size > 0) {
    char* buffer = static_cast<char*>(operator new(size + align));
    size_t address = reinterpret_cast<size_t>(buffer);
    region.offset = kArchAlignment - (address % align);
    region.data = buffer + region.offset;
  }
  region.mmap = 0;
  region.size = size;
  return new MappedFile(region);
}

MappedFile* MappedFile::Borrow(void* data) {
  MemoryRegion region;
  region.data = data;
  region.mmap = data;
  region.size = 0;
  region.offset = 0;
  return new MappedFile(region);
}

MappedFile* MappedFile::Map(std::istream* s, bool memorymap,
                            const string& source, size_t size) {
  std::streampos spos = s->tellg();
  VLOG(1) << "memorymap: " << (memorymap ? "true" : "false") << " source: \""
          << source << "\""
          << " size: " << size << " offset: " << spos;
  if (memorymap && spos >= 0 && spos % kArchAlignment == 0) {
    size_t pos = spos;
    int fd = open(source.c_str(), O_RDONLY);
    if (fd != -1) {
      int pagesize = sysconf(_SC_PAGESIZE);
      off_t offset = pos % pagesize;
      off_t upsize = size + offset;
      void* map = mmap(0, upsize, PROT_READ, MAP_SHARED, fd, pos - offset);
      char* data = reinterpret_cast<char*>(map);
      if (close(fd) == 0 && map != MAP_FAILED) {
        MemoryRegion region;
        region.mmap = map;
        region.size = upsize;
        region.data = reinterpret_cast<void*>(data + offset);
        region.offset = offset;
        MappedFile* mmf = new MappedFile(region);
        s->seekg(pos + size, std::ios::beg);
        if (s) {
          VLOG(1) << "mmap'ed region of " << size << " at offset " << pos
                  << " from " << source << " to addr " << map;
          return mmf;
        }
        delete mmf;
      } else {
        LOG(INFO) << "Mapping of file failed: " << strerror(errno);
      }
    }
  }
  // If all else fails resort to reading from file into allocated buffer.
  if (memorymap) {
    LOG(WARNING) << "File mapping at offset " << spos << " of file " << source
                 << " could not be honored, reading instead.";
  }

  // Read the file into the buffer in chunks not larger than kMaxReadChunk.
  MappedFile* mf = Allocate(size);
  char* buffer = reinterpret_cast<char*>(mf->mutable_data());
  while (size > 0) {
    const size_t next_size = std::min(size, kMaxReadChunk);
    std::streampos current_pos = s->tellg();
    if (!s->read(buffer, next_size)) {
      LOG(ERROR) << "Failed to read " << next_size << " bytes at offset "
                 << current_pos << "from \"" << source << "\".";
      delete mf;
      return NULL;
    }
    size -= next_size;
    buffer += next_size;
    VLOG(2) << "Read " << next_size << " bytes. " << size << " remaining.";
  }
  return mf;
}

}  // namespace fst