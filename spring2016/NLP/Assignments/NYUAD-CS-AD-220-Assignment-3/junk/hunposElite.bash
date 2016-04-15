#!/bin/bash
if ![ "$#" -eq 4 ]; then
	echo "Please enter numbers that represent -t -e -s -f values respectively"; 
	exit 1;
else
	cat question3/train_double.txt | hunpos-1.0-linux/hunpos-train -t$1 -e$2 -s$3 -f$4 question3/models/t"$1"e"$2"s"$3"f"$4".model;

	cat en-universal-dev.conll | hunpos-1.0-linux/hunpos-tag question3/models/t"$1"e"$2"s"$3"f"$4".model > question3/tagResults/t"$1"e"$2"s"$3"f"$4".tag.conll;

	cut -f 2,11 question3/tagResults/t"$1"e"$2"s"$3"f"$4".tag.conll > question3/evalReadyTags/t"$1"e"$2"s"$3"f"$4".eval.conll;

	cat t"$1"e"$2"s"$3"f"$4" >> question3/results.txt;

	perl pos-eval.pl universal_dev_double.txt question3/tagResults/t"$1"e"$2"s"$3"f"$4".eval.conll >> question3/results.txt;

	cat "\n" >> question3/results.txt;
fi


