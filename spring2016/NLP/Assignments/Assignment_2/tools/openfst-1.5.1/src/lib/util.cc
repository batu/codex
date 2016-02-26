// See www.openfst.org for extensive documentation on this weighted
// finite-state transducer library.
//
// FST utility definitions.

#include <cctype>
#include <sstream>
#include <string>
#include <fst/mapped-file.h>
#include <fst/util.h>

// Utility flag definitions

DEFINE_bool(fst_error_fatal, true,
            "FST errors are fatal; o.w. return objects flagged as bad: "
            " e.g., FSTs - kError prop. true, FST weights - not  a Member()");

namespace fst {

void SplitToVector(char *full, const char *delim, std::vector<char *> *vec,
                   bool omit_empty_strings) {
  char *p = full;
  while (p) {
    if ((p = strpbrk(full, delim))) p[0] = '\0';
    if (!omit_empty_strings || full[0] != '\0') vec->push_back(full);
    if (p) full = p + 1;
  }
}

int64 StrToInt64(const string &s, const string &src, size_t nline,
                 bool allow_negative, bool *error) {
  int64 n;
  const char *cs = s.c_str();
  char *p;
  if (error) *error = false;
  n = strtoll(cs, &p, 10);
  if (p < cs + s.size() || (!allow_negative && n < 0)) {
    FSTERROR() << "StrToInt64: Bad integer = " << s << "\", source = " << src
               << ", line = " << nline;
    if (error) *error = true;
    return 0;
  }
  return n;
}

void Int64ToStr(int64 n, string *s) {
  std::ostringstream nstr;
  nstr << n;
  s->append(nstr.str().data(), nstr.str().size());
}

void ConvertToLegalCSymbol(string *s) {
  for (auto it = s->begin(); it != s->end(); ++it)
    if (!isalnum(*it)) *it = '_';
}

// Skips over input characters to align to 'align' bytes. Returns
// false if can't align.
bool AlignInput(std::istream &strm) {
  char c;
  for (int i = 0; i < MappedFile::kArchAlignment; ++i) {
    int64 pos = strm.tellg();
    if (pos < 0) {
      LOG(ERROR) << "AlignInput: Can't determine stream position";
      return false;
    }
    if (pos % MappedFile::kArchAlignment == 0) break;
    strm.read(&c, 1);
  }
  return true;
}

// Write null output characters to align to 'align' bytes. Returns
// false if can't align.
bool AlignOutput(std::ostream &strm) {
  for (int i = 0; i < MappedFile::kArchAlignment; ++i) {
    int64 pos = strm.tellp();
    if (pos < 0) {
      LOG(ERROR) << "AlignOutput: Can't determine stream position";
      return false;
    }
    if (pos % MappedFile::kArchAlignment == 0) break;
    strm.write("", 1);
  }
  return true;
}

}  // namespace fst
