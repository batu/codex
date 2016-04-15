fine = open("fine_train.txt","r")
coarse = open("coarse_train_tags.txt", "r+")
new = open("fine_train_double.txt", "w")
counter = 0

for line in fine:
    if line != "\n":
        writable = line.strip("\n") + "-";
        coarseLine = coarse.readline()
        writable += coarseLine
        new.write(writable)
        counter = counter + 1
    else: 
        coarseLine = coarse.readline()
        new.write("\n")
        
	
