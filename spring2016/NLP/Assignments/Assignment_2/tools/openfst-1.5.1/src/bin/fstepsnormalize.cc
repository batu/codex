// See www.openfst.org for extensive documentation on this weighted
// finite-state transducer library.
//
// Epsilon-normalizes an FST.

#include <fst/script/epsnormalize.h>

DEFINE_bool(eps_norm_output, false, "Normalize output epsilons");

int main(int argc, char **argv) {
  namespace s = fst::script;
  using fst::script::FstClass;
  using fst::script::VectorFstClass;

  string usage = "Epsilon normalizes an FST.\n\n  Usage: ";
  usage += argv[0];
  usage += " [in.fst [out.fst]]\n";

  std::set_new_handler(FailedNewHandler);
  SET_FLAGS(usage.c_str(), &argc, &argv, true);
  if (argc > 3) {
    ShowUsage();
    return 1;
  }

  string in_name = (argc > 1 && strcmp(argv[1], "-") != 0) ? argv[1] : "";
  string out_name = argc > 2 ? argv[2] : "";

  FstClass *ifst = FstClass::Read(in_name);
  if (!ifst) return 1;

  fst::EpsNormalizeType eps_norm_type = FLAGS_eps_norm_output
                                                ? fst::EPS_NORM_OUTPUT
                                                : fst::EPS_NORM_INPUT;

  VectorFstClass ofst(ifst->ArcType());
  s::EpsNormalize(*ifst, &ofst, eps_norm_type);
  ofst.Write(out_name);

  return 0;
}
