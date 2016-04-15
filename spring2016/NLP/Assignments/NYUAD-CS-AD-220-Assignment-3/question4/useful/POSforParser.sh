cat ../question3/train_double.txt | ../hunpos-1.0-linux/hunpos-train -t$1 -e$2 -s$3 -f$4 ../question3/models/t"$1"e"$2"s"$3"f"$4".model;
echo "!!Model trained with "'t'"$1"'e'"$2"'s'"$3"'f'"$4"
echo

cat ../question3/universal_dev_double.txt | ../hunpos-1.0-linux/hunpos-tag ../question3/models/t"$1"e"$2"s"$3"f"$4".model > t"$1"e"$2"s"$3"f"$4".tag.conll;
echo "!!Tagging is done!!"
echo



