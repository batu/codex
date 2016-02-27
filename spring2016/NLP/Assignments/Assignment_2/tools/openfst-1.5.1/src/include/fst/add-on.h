// See www.openfst.org for extensive documentation on this weighted
// finite-state transducer library.
//
// FST implementation class to attach an arbitrary object with a read/write
// method to an FST and its file representation. The FST is given a new type
// name.

#ifndef FST_LIB_ADD_ON_H_
#define FST_LIB_ADD_ON_H_

#include <stddef.h>
#include <memory>
#include <string>

#include <fst/fst.h>


namespace fst {

// Identifies stream data as an add-on fst.
static const int32 kAddOnMagicNumber = 446681434;

//
// Some useful add-on objects.
//

// Nothing to save.
class NullAddOn {
 public:
  NullAddOn() {}

  static NullAddOn *Read(std::istream &strm, const FstReadOptions &opts) {
    return new NullAddOn();
  }

  bool Write(std::ostream &ostrm, const FstWriteOptions &opts) const {
    return true;
  }

 private:
  DISALLOW_COPY_AND_ASSIGN(NullAddOn);
};

// Create a new add-on from a pair of add-ons.
template <class A1, class A2>
class AddOnPair {
 public:
  // Argument reference count incremented.
  AddOnPair(std::shared_ptr<A1> a1, std::shared_ptr<A2> a2)
      : a1_(a1), a2_(a2) {}

  const A1 *First() const { return a1_.get(); }
  const A2 *Second() const { return a2_.get(); }

  std::shared_ptr<A1> SharedFirst() const { return a1_; }
  std::shared_ptr<A2> SharedSecond() const { return a2_; }

  static AddOnPair<A1, A2> *Read(std::istream &istrm,
                                 const FstReadOptions &opts) {
    A1 *a1 = nullptr;
    bool have_addon1 = false;
    ReadType(istrm, &have_addon1);
    if (have_addon1) a1 = A1::Read(istrm, opts);

    A2 *a2 = nullptr;
    bool have_addon2 = false;
    ReadType(istrm, &have_addon2);
    if (have_addon2) a2 = A2::Read(istrm, opts);

    return new AddOnPair<A1, A2>(std::shared_ptr<A1>(a1),
                                 std::shared_ptr<A2>(a2));
  }

  bool Write(std::ostream &ostrm, const FstWriteOptions &opts) const {
    bool have_addon1 = a1_ != nullptr;
    WriteType(ostrm, have_addon1);
    if (have_addon1) a1_->Write(ostrm, opts);
    bool have_addon2 = a2_ != nullptr;
    WriteType(ostrm, have_addon2);
    if (have_addon2) a2_->Write(ostrm, opts);
    return true;
  }

 private:
  std::shared_ptr<A1> a1_;
  std::shared_ptr<A2> a2_;

  DISALLOW_COPY_AND_ASSIGN(AddOnPair);
};

// Add to an Fst F a type T object. T must have a 'T* Read(std::istream &)',
// a 'bool Write(std::ostream &)' method.
// The result is a new Fst implemenation with type name 'type'.
template <class F, class T>
class AddOnImpl : public FstImpl<typename F::Arc> {
 public:
  typedef typename F::Arc Arc;
  typedef typename Arc::Label Label;
  typedef typename Arc::Weight Weight;
  typedef typename Arc::StateId StateId;

  using FstImpl<Arc>::SetType;
  using FstImpl<Arc>::SetInputSymbols;
  using FstImpl<Arc>::SetOutputSymbols;
  using FstImpl<Arc>::SetProperties;
  using FstImpl<Arc>::WriteHeader;

  // We make a thread-safe copy of the FST by default since an FST
  // implementation is expected to not share mutable data between objects.
  AddOnImpl(const F &fst, const string &type,
            std::shared_ptr<T> t = std::shared_ptr<T>())
      : fst_(fst, true), t_(t) {
    SetType(type);
    SetProperties(fst_.Properties(kFstProperties, false));
    SetInputSymbols(fst_.InputSymbols());
    SetOutputSymbols(fst_.OutputSymbols());
  }

  // Conversion from const Fst<Arc> & to F always copies the underlying
  // implementation.
  AddOnImpl(const Fst<Arc> &fst, const string &type,
            std::shared_ptr<T> t = std::shared_ptr<T>())
      : fst_(fst), t_(t) {
    SetType(type);
    SetProperties(fst_.Properties(kFstProperties, false));
    SetInputSymbols(fst_.InputSymbols());
    SetOutputSymbols(fst_.OutputSymbols());
  }

  // We make a thread-safe copy of the FST by default since an FST
  // implementation is expected to not share mutable data between objects.
  AddOnImpl(const AddOnImpl<F, T> &impl) : fst_(impl.fst_, true), t_(impl.t_) {
    SetType(impl.Type());
    SetProperties(fst_.Properties(kCopyProperties, false));
    SetInputSymbols(fst_.InputSymbols());
    SetOutputSymbols(fst_.OutputSymbols());
  }

  StateId Start() const { return fst_.Start(); }
  Weight Final(StateId s) const { return fst_.Final(s); }
  size_t NumArcs(StateId s) const { return fst_.NumArcs(s); }

  size_t NumInputEpsilons(StateId s) const { return fst_.NumInputEpsilons(s); }

  size_t NumOutputEpsilons(StateId s) const {
    return fst_.NumOutputEpsilons(s);
  }

  size_t NumStates() const { return fst_.NumStates(); }

  static AddOnImpl<F, T> *Read(std::istream &strm, const FstReadOptions &opts) {
    FstReadOptions nopts(opts);
    FstHeader hdr;
    if (!nopts.header) {
      hdr.Read(strm, nopts.source);
      nopts.header = &hdr;
    }
    AddOnImpl<F, T> *impl = new AddOnImpl<F, T>(nopts.header->FstType());
    if (!impl->ReadHeader(strm, nopts, kMinFileVersion, &hdr)) return nullptr;
    delete impl;  // Used here only for checking types.

    int32 magic_number = 0;
    ReadType(strm, &magic_number);  // Ensures this is an add-on Fst.
    if (magic_number != kAddOnMagicNumber) {
      LOG(ERROR) << "AddOnImpl::Read: Bad add-on header: " << nopts.source;
      return nullptr;
    }

    FstReadOptions fopts(opts);
    fopts.header = 0;  // Contained header was written out.
    std::unique_ptr<F> fst(F::Read(strm, fopts));
    if (!fst) return nullptr;

    std::shared_ptr<T> t;
    bool have_addon = false;
    ReadType(strm, &have_addon);
    if (have_addon) {  // Read add-on object if present.
      t = std::shared_ptr<T>(T::Read(strm, fopts));
      if (!t) return nullptr;
    }
    impl = new AddOnImpl<F, T>(*fst, nopts.header->FstType(), t);
    return impl;
  }

  bool Write(std::ostream &strm, const FstWriteOptions &opts) const {
    FstHeader hdr;
    FstWriteOptions nopts(opts);
    nopts.write_isymbols = false;  // Let contained FST hold any symbols.
    nopts.write_osymbols = false;
    WriteHeader(strm, nopts, kFileVersion, &hdr);
    WriteType(strm, kAddOnMagicNumber);  // Ensures this is an add-on Fst.
    FstWriteOptions fopts(opts);
    fopts.write_header = true;  // Force writing contained header.
    if (!fst_.Write(strm, fopts)) return false;
    bool have_addon = !!t_;
    WriteType(strm, have_addon);
    if (have_addon)  // Write add-on object if present.
      t_->Write(strm, opts);
    return true;
  }

  void InitStateIterator(StateIteratorData<Arc> *data) const {
    fst_.InitStateIterator(data);
  }

  void InitArcIterator(StateId s, ArcIteratorData<Arc> *data) const {
    fst_.InitArcIterator(s, data);
  }

  F &GetFst() { return fst_; }

  const F &GetFst() const { return fst_; }

  const T *GetAddOn() const { return t_.get(); }

  std::shared_ptr<T> GetSharedAddOn() const { return t_; }

  void SetAddOn(std::shared_ptr<T> t) { t_ = t; }

 private:
  explicit AddOnImpl(const string &type) : t_() {
    SetType(type);
    SetProperties(kExpanded);
  }

  // Current file format version
  static const int kFileVersion = 1;
  // Minimum file format version supported
  static const int kMinFileVersion = 1;

  F fst_;
  std::shared_ptr<T> t_;

  void operator=(const AddOnImpl<F, T> &fst);  // Disallow
};

template <class F, class T>
const int AddOnImpl<F, T>::kFileVersion;
template <class F, class T>
const int AddOnImpl<F, T>::kMinFileVersion;

}  // namespace fst

#endif  // FST_LIB_ADD_ON_H_