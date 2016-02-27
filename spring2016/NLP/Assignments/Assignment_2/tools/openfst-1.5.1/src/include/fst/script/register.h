// See www.openfst.org for extensive documentation on this weighted
// finite-state transducer library.

#ifndef FST_SCRIPT_REGISTER_H_
#define FST_SCRIPT_REGISTER_H_

#include <istream>
#include <string>

#include <fst/generic-register.h>
#include <fst/script/fst-class.h>
#include <fst/script/weight-class.h>

// Holds methods and classes responsible for maintaining
// the register for FstClass arc types.

namespace fst {
namespace script {

// Registers for reading and converting various kinds of FST classes.

// This class definition is to avoid a nested class definition inside
// the IORegistration struct.

template <class Reader, class Creator, class Converter>
struct FstClassRegEntry {
  Reader reader;
  Creator creator;
  Converter converter;

  FstClassRegEntry(Reader r, Creator cr, Converter co)
      : reader(r), creator(cr), converter(co) {}

  FstClassRegEntry()
      : reader(NullReader), creator(NullCreator), converter(NullConverter) {}

  // Null-returning reader, creator, and converter, used when registry lookup
  // fails.

  template <class FstClassType>
  static FstClassType *NullReader(std::istream &strm,
                                  const FstReadOptions &opts) {
    return nullptr;
  }

  static FstClassImplBase *NullCreator() { return nullptr; }

  static FstClassImplBase *NullConverter(const FstClass &other) {
    return nullptr;
  }
};

template <class Reader, class Creator, class Converter>
class FstClassIORegister
    : public GenericRegister<string,
                             FstClassRegEntry<Reader, Creator, Converter>,
                             FstClassIORegister<Reader, Creator, Converter>> {
 public:
  Reader GetReader(const string &arc_type) const {
    return this->GetEntry(arc_type).reader;
  }

  Creator GetCreator(const string &arc_type) const {
    return this->GetEntry(arc_type).creator;
  }

  Converter GetConverter(const string &arc_type) const {
    return this->GetEntry(arc_type).converter;
  }

 protected:
  string ConvertKeyToSoFilename(const string &key) const override {
    string legal_type(key);
    ConvertToLegalCSymbol(&legal_type);

    return legal_type + "-arc.so";
  }
};

//
// Struct containing everything needed to register a particular type
// of FST class (e.g. a plain FstClass, or a MutableFstClass, etc)
//
template <class FstClassType>
struct IORegistration {
  typedef FstClassType *(*Reader)(std::istream &stream,
                                  const FstReadOptions &opts);

  typedef FstClassImplBase *(*Creator)();

  typedef FstClassImplBase *(*Converter)(const FstClass &other);

  typedef FstClassRegEntry<Reader, Creator, Converter> Entry;

  // FST class Register
  typedef FstClassIORegister<Reader, Creator, Converter> Register;

  // FST class Register-er
  typedef GenericRegisterer<FstClassIORegister<Reader, Creator, Converter>>
      Registerer;
};

//
// REGISTRATION MACROS
//

#define REGISTER_FST_CLASS(Class, Arc)                                   \
  static IORegistration<Class>::Registerer Class##_##Arc##_registerer(   \
      Arc::Type(),                                                       \
      IORegistration<Class>::Entry(Class::Read<Arc>, Class::Create<Arc>, \
                                   Class::Convert<Arc>))

#define REGISTER_FST_CLASSES(Arc)           \
  REGISTER_FST_CLASS(FstClass, Arc);        \
  REGISTER_FST_CLASS(MutableFstClass, Arc); \
  REGISTER_FST_CLASS(VectorFstClass, Arc);

}  // namespace script
}  // namespace fst

#endif  // FST_SCRIPT_REGISTER_H_