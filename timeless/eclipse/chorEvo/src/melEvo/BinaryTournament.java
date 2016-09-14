package melEvo;

import java.util.ArrayList;
import java.util.Random;

public class BinaryTournament {
	  
	  /**
	   * dominance_ store the <code>Comparator</code> for check dominance_
	   */
	  private DominanceComparator dominance_ = new DominanceComparator();
	  
	  /**
	   * a_ stores a permutation of the solutions in the solutionSet used
	   */
	  private int a_[];
	  
	  /**
	   *  index_ stores the actual index for selection
	   */
	  private int index_ = 0;
	    
	  /**
	   * Constructor
	   * Creates a new instance of the Binary tournament operator (Deb's
	   * NSGA-II implementation version)
	   */
	  public BinaryTournament()
	  {
	  	          
	  } 
	    
	  /**
	  * Performs the operation
	  * @param pop Object representing a SolutionSet
	  * @return the selected solution
	  */
	  public Gene execute(ArrayList<Gene> pop)    
	  {
		ArrayList<Gene> population = pop;
		if (index_+1 >= population.size())
			index_ = 0;
	    if (index_ == 0) //Create the permutation
	    {
	      a_= (new PermutationUtility()).intPermutation(population.size());
	    }
	            
	        
	    Gene solution1,solution2;
	    solution1 = population.get(a_[index_]);
	    solution2 = population.get(a_[index_+1]);
	        
	    index_ = (index_ + 2) % population.size();
	    Random rand = new Random();
	        
	    int flag = 0;
	    flag = dominance_.compare(solution1,solution2);
	    if (flag == -1)
	      return solution1;
	    else if (flag == 1)
	      return solution2;
	    else if (solution1.getCrowdingDistance() > solution2.getCrowdingDistance())
	      return solution1;
	    else if (solution2.getCrowdingDistance() > solution1.getCrowdingDistance())
	      return solution2;
	    else
	      if (rand.nextDouble()<0.5d)
	        return solution1;
	      else
	        return solution2;        
	  } // execute
	} // BinaryTournament2