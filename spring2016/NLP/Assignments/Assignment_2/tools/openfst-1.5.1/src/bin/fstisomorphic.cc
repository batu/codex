// See www.openfst.org for extensive documentation on this weighted
// finite-state transducer library.
//
// Two FSTS are isomorphic (equal up to state and arc re-ordering) iff their
// exit status is zero. FSTs should be deterministic when viewed as unweighted
// automata.

#include <fst/script/isomorphic.h>

DEFINE_double(delta, fst::kDelta, "Comparison/quantization delta");

int main(int argc, char **argv) {
  namespace s = fst::script;
  using fst::script::FstClass;

  string usage =
      "Two FSTs are isomorphic iff the exit status is zero.\n\n  Usage: ";
  usage += argv[0];
  usage += " in1.fst in2.fst\n";

  std::set_new_handler(FailedNewHandler);
  SET_FLAGS(usage.c_str(), &argc, &argv, true);
  if (argc != 3) {
    ShowUsage();
    return 1;
  }

  string in1_name = strcmp(argv[1], "-") == 0 ? "" : argv[1];
  string in2_name = strcmp(argv[2], "-") == 0 ? "" : argv[2];

  if (in1_name.empty() && in2_name.empty()) {
    LOG(ERROR) << argv[0] << ": Can't take both inputs from standard input.";
    return 1;
  }

  FstClass *ifst1 = FstClass::Read(in1_name);
  if (!ifst1) return 1;

  FstClass *ifst2 = FstClass::Read(in2_name);
  if (!ifst2) return 1;

  bool result = s::Isomorphic(*ifst1, *ifst2, FLAGS_delta);
  if (!result) VLOG(1) << "FSTs are not isomorphic.";

  return result ? 0 : 2;
}
