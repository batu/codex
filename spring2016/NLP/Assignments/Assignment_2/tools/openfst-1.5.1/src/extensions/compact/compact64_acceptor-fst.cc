// See www.openfst.org for extensive documentation on this weighted
// finite-state transducer library.

#include <fst/fst.h>
#include <fst/compact-fst.h>

using fst::FstRegisterer;
using fst::CompactFst;
using fst::LogArc;
using fst::StdArc;
using fst::AcceptorCompactor;

static FstRegisterer<CompactFst<StdArc, AcceptorCompactor<StdArc>, uint64>>
    CompactFst_StdArc_AcceptorCompactor_uint64_registerer;
static FstRegisterer<CompactFst<LogArc, AcceptorCompactor<LogArc>, uint64>>
    CompactFst_LogArc_AcceptorCompactor_uint64_registerer;