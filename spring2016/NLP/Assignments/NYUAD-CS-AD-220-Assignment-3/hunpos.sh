#!/bin/sh
cat question3/train_double.txt | hunpos-1.0-linux/hunpos-train -t$1 -e$2 -s$3 -f$4 question3/models/t"$1"e"$2"s"$3"f"$4".model;
echo "!!Model created with "'t'"$1"'e'"$2"'s'"$3"'f'"$4"
echo

cat question3/universal_dev_just_words.txt | hunpos-1.0-linux/hunpos-tag question3/models/t"$1"e"$2"s"$3"f"$4".model > question3/evalReadyTags/t"$1"e"$2"s"$3"f"$4".eval.conll;
echo "!!Tagging is done!!"
echo

echo 't'"$1"'e'"$2"'s'"$3"'f'"$4" >> question3/results.txt;

perl pos-eval.pl universal_dev_double.txt question3/evalReadyTags/t"$1"e"$2"s"$3"f"$4".eval.conll >> question3/results.txt;
echo  >> question3/results.txt;

perl pos-eval.pl universal_dev_double.txt question3/evalReadyTags/t"$1"e"$2"s"$3"f"$4".eval.conll





