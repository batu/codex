import subprocess
import random
from time import sleep
counter = 0;
while(1):

    for t in range(1,6):
        for e in range (1,6):
            for s in range (1, 50, 10):
                for f in range (1, 50, 10):
                    a = subprocess.check_call(['./hunpos.sh', str(t), str(e), str(s), str(f)])
                    counter = counter + 1
                    print(counter)


   
   #t = random.randint(1,6)
   # e = random.randint(1,6)
   # s = random.randint(1,70)
   # f = random.randint(1,70)


