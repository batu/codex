countFile = open("parsethis.conll", "r");
writeFile = open("column.txt","w");

for line in countFile:
    if (line != "\n"):
        writeFile.write("_\n");
    else:
        writeFile.write("\n");
