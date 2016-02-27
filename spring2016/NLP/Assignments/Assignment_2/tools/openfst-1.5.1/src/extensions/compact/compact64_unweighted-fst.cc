// See www.openfst.org for extensive documentation on this weighted
// finite-state transducer library.

#include <fst/fst.h>
#include <fst/compact-fst.h>

using fst::FstRegisterer;
using fst::CompactFst;
using fst::LogArc;
using fst::StdArc;
using fst::UnweightedCompactor;

static FstRegisterer<CompactFst<StdArc, UnweightedCompactor<StdArc>, uint64>>
    CompactFst_StdArc_UnweightedCompactor_uint64_registerer;
static FstRegisterer<CompactFst<LogArc, UnweightedCompactor<LogArc>, uint64>>
    CompactFst_LogArc_UnweightedCompactor_uint64_registerer;