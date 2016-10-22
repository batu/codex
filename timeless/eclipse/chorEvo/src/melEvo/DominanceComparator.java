package melEvo;

public class DominanceComparator {
 
  /**
   * Constructor
   */
  public DominanceComparator() {
  }
 
 /**
  * Compares two solutions.
  * @param object1 Object representing the first <code>Solution</code>.
  * @param object2 Object representing the second <code>Solution</code>.
  * @return -1, or 0, or 1 if solution1 dominates solution2, both are 
  * non-dominated, or solution1  is dominated by solution22, respectively.
  */
  public int compare(Gene object1, Gene object2) {
    if (object1==null)
      return 1;
    else if (object2 == null)
      return -1;
    
    Gene solution1 = object1;
    Gene solution2 = object2;

    int dominate1 ; // dominate1 indicates if some objective of solution1 
                    // dominates the same objective in solution2. dominate2
    int dominate2 ; // is the complementary of dominate1.

    dominate1 = 0 ; 
    dominate2 = 0 ;
    
    int flag; //stores the result of the comparison
    
    // Applying a dominance Test then
    double value1, value2;
    //check first objective
    value1 = solution1.getmBeatFitness();
    value2 = solution2.getmBeatFitness();
	if (value1 < value2) {
		flag = -1;
	} else if (value1 > value2) {
		flag = 1;
	} else {
		flag = 0;
	}
	if (flag == -1) {
	    dominate2 = 1;
	  }
	  
	  if (flag == 1) {
	    dominate1 = 1;           
	  }
	
	//check second objective
    value1 = solution1.getmChordFitness();
    value2 = solution2.getmChordFitness();
	if (value1 < value2) {
		flag = -1;
	} else if (value1 > value2) {
		flag = 1;
	} else {
		flag = 0;
	}
	if (flag == -1) {
        dominate2 = 1;
      }
      
      if (flag == 1) {
        dominate1 = 1;           
      }
	
	//check third objective
    value1 = solution1.getmStepFitness();
    value2 = solution2.getmStepFitness();
	if (value1 < value2) {
		flag = -1;
	} else if (value1 > value2) {
		flag = 1;
	} else {
		flag = 0;
	}
	if (flag == -1) {
        dominate2 = 1;
      }
      
      if (flag == 1) {
        dominate1 = 1;           
      }
  
            
    if (dominate1 == dominate2) {            
      return 0; //No one dominate the other
    }
    if (dominate1 == 1) {
      return -1; // solution1 dominate
    }
    return 1;    // solution2 dominate   
  } // compare
} // DominanceComparator
