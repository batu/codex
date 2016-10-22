package melEvo;

import java.awt.Choice;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;        // for generating random numbers
import java.util.ArrayList;     // arrayLists are more versatile than arrays

import javax.sound.midi.InvalidMidiDataException;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import jm.music.data.Note;
import progression.ProgressionGraph;



public class GeneticAlgorithm {
    
	// --- constants
    static int CHROMOSOME_SIZE=0; //value depends on amount of chords
    static int POPULATION_SIZE=500;
    static int GENERATION_N=2500;
    static int ELITIST_FACTOR=4;
    static float MUTATION_CHANCE = 0.1f;
    static ArrayList<Integer> CHORDS;
    static float MIN_PERCENTAGE_SECONDS = 0.2f;
    static int[] NOTES_PER_CHORD;
    static boolean DEBUG = false;

    // --- variables:

    /**
     * The population contains an ArrayList of genes (the choice of arrayList over
     * a simple array is due to extra functionalities of the arrayList, such as sorting)
     */
    int currentPopulationSize = POPULATION_SIZE;
    ArrayList<Gene> mPopulation;
    ArrayList<Gene> mFeasable;

    // --- functions:

    /**
     * Creates the starting population of Gene classes, whose chromosome contents are random
     * @param size: The size of the popultion is passed as an argument from the main class
     */
    public GeneticAlgorithm(int size){

        // initialize the arraylist and each gene's initial weights HERE
        mPopulation = new ArrayList<Gene>();
        mFeasable = new ArrayList<Gene>();
        for(int i = 0; i < size; i++){
            Gene entry = new Gene();
            entry.randomizeChromosome();
            mPopulation.add(entry);
        }
    }
    /**
     * For all members of the population, runs a heuristic that evaluates their fitness
     * based on their phenotype. The evaluation of this problem's phenotype is fairly simple,
     * and can be done in a straightforward manner. In other cases, such as agent
     * behavior, the phenotype may need to be used in a full simulation before getting
     * evaluated (e.g based on its performance)
     */
    public void evaluateGeneration(){
        for(int i = 0; i < mPopulation.size(); i++){
            // evaluation of the fitness function for each gene in the population goes HERE
        	mPopulation.get(i).calculateFeasability();
        }
        
//        for(int i = 0; i < mFeasable.size(); i++){
//            // evaluation of the fitness function for each gene in the population goes HERE
//        	mFeasable.get(i).calculateFitness();
//        }
    }
    /**
     * With each gene's fitness as a guide, chooses which genes should mate and produce offspring.
     * The offspring are added to the population, replacing the previous generation's Genes either
     * partially or completely. The population size, however, should always remain the same.
     * If you want to use mutation, this function is where any mutation chances are rolled and mutation takes place.
     */
    
    public void produceNextGeneration(){
    	mPopulation = produceNextGenerationI(mPopulation);
    	mFeasable = feasibleNSGA(mFeasable);
    }
    
    public ArrayList<Gene> produceNextGenerationI(ArrayList<Gene> pop){
        // use one of the offspring techniques suggested in class (also applying any mutations) HERE
    	//QuickSort.quickSort(mPopulation, 0, mPopulation.size()-1);
    	ArrayList<Gene> tmp = new ArrayList<Gene>();
    	Random rand = new Random();
    	int population_size = pop.size();
    	int best = population_size/ELITIST_FACTOR;
    	int worst = population_size-best;
    	
    	//copy elite of total fitness
    	QuickSort.quickSort(pop, 0, pop.size()-1);
    	for (int i = worst; i < pop.size(); i++) {
			tmp.add(pop.get(i));
		}
    	
    	//fill rest of population with offsprings from randomly selected parents
    	while (tmp.size()< population_size){
    		Gene[] offspring = pop.get(rand.nextInt(population_size)).reproduce(pop.get(rand.nextInt(population_size)));
    		tmp.add(offspring[0]);
    		if (tmp.size()< population_size)
    			tmp.add(offspring[1]);
    	}
    	//introduce mutations in new population
    	for (int i = 0; i < tmp.size(); i++) {
    		if(rand.nextFloat()<MUTATION_CHANCE)
    			tmp.get(i).mutate();
		}
    	//exchange new population
    	return tmp;
    }
    
    public ArrayList<Gene> feasibleNSGA(ArrayList<Gene> pop){
    	
    	int populationSize = pop.size();
    	BinaryTournament selectionOperator = new BinaryTournament();
    	
    	if (populationSize < 2)
    		return pop;
    	
    	for (Gene gene : pop) {
			gene.calculateFitness();
		}
    	
    	// Create the offSpring solutionSet      
        ArrayList<Gene> offspringPopulation = new ArrayList<Gene>(populationSize);
        Gene[] parents = new Gene[2];
        for (int i = 0; i < (populationSize / 2); i++) {
            //obtain parents (if population too small just choose randomly)
        	if (populationSize > 10){
	            parents[0] = selectionOperator.execute(pop);
	            parents[1] = selectionOperator.execute(pop);
        	}
        	else{
        		Random rand = new Random();
        		parents[0] = pop.get(rand.nextInt(populationSize));
	            parents[1] = pop.get(rand.nextInt(populationSize));
        	}
            Gene[] offSpring = parents[0].reproduce(parents[1]);
            offSpring[0].mutate();
            offSpring[1].mutate();
            offSpring[0].calculateFitness();
            offSpring[1].calculateFitness();
            offspringPopulation.add(offSpring[0]);
            offspringPopulation.add(offSpring[1]);                        
        } // for
        
        //check if offsprings are infeasible, if so kill them
        ArrayList<Gene> toRemove = new ArrayList<>();
        if (!offspringPopulation.isEmpty()){
	        for (int i = 0; i < offspringPopulation.size(); i++) {
				if (!feasible(offspringPopulation.get(i)))
					toRemove.add(offspringPopulation.get(i));
			}
	        for (Gene gene : toRemove) {
				offspringPopulation.remove(gene);
			}
        }

        // Create the solutionSet union of solutionSet and offSpring
        ArrayList<Gene> union = new ArrayList<>();
        for (Gene gene : pop) {
			union.add(gene);
		}
        for (Gene gene : offspringPopulation) {
			union.add(gene);
		}

        // Ranking the union
        Ranking ranking = new Ranking(union);
        if(ranking.getSubfront(0).size()== 1){
        	System.out.print("");
        	ranking = new Ranking(union);
        }

        int remain = populationSize;
        int index = 0;
        ArrayList<Gene> front = null;
        pop.clear();

        // Obtain the next front
        front = ranking.getSubfront(index);

        while ((remain > 0) && (remain >= front.size())) {
          //Assign crowding distance to individuals
          crowdingDistanceAssignment(front);
          //Add the individuals of this front
          for (int k = 0; k < front.size(); k++) {
            pop.add(front.get(k));
          } // for

          //Decrement remain
          remain = remain - front.size();

          //Obtain the next front
          index++;
          if (remain > 0) {
            front = ranking.getSubfront(index);
          } // if 
        }
        
     // Remain is less than front(index).size, insert only the best one
        if (remain > 0) {  // front contains individuals to insert                        
          crowdingDistanceAssignment(front);
          front.sort(new CrowdingComparator());
          for (int k = 0; k < remain; k++) {
            pop.add(front.get(k));
          } // for
          remain = 0;
        } // if  
        
        return pop;
    	
    }

    private boolean dominates(Gene gene, Gene nonDom) {
		if (gene.mChordFitness > nonDom.mChordFitness && gene.mStepFitness > nonDom.mStepFitness && gene.mBeatFitness > nonDom.mBeatFitness)
			return true;
		else
			return false;
	}
	// accessors
    /**
     * @return the size of the population
     */
    public int size(){ return mPopulation.size(); }
    /**
     * Returns the Gene at position <b>index</b> of the mPopulation arrayList
     * @param index: the position in the population of the Gene we want to retrieve
     * @return the Gene at position <b>index</b> of the mPopulation arrayList
     */
    public Gene getGene(int index){ return mFeasable.get(index); }
    
    public Gene getGeneInfeasable(int index){ return mPopulation.get(index); }
    
    public static Note[] generate(ArrayList<String> chords, int key, boolean major, int[] amountOfNotes){
    	CHORDS = ProgressionGraph.convertSequenceInt(chords);
    	NOTES_PER_CHORD = amountOfNotes;

    	CHROMOSOME_SIZE = 0;
    	for (int i = 0; i < amountOfNotes.length; i++) {
			CHROMOSOME_SIZE += amountOfNotes[i];
		}
    	
    	PrintWriter out = null;
    	PrintWriter paretoOut = null;
    	if (DEBUG){
	    	//initialize log output
	    	try {
	    		int i = 0;
	    		File f;
	    		do{
	    			f = new File("evo_"+i+"_output.txt");
	    			i++;
	    		}while(f.exists());
	    		
				out = new PrintWriter(f);
				paretoOut = new PrintWriter("evo_"+(i-1)+"_pareto.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	out.println("inSize, fSize, paretoSize, maxBeat, avgBeat, maxChord, avgChord, maxStep, avgStep");
    	}

    	System.out.println("Generating starting population");
        GeneticAlgorithm population = new GeneticAlgorithm(POPULATION_SIZE);
        int generationCount = 0;

        float avgFitness=0.f;
        //while(population.mFeasable.size() < POPULATION_SIZE/5){
        while(generationCount < GENERATION_N){
        	//find feasable individuals and move them (and opposite)
        	population.moveFeasable();
            // --- evaluate current generation:
//        	System.out.println("Evaluating population");
            population.evaluateGeneration();
            // produce next generation:
//            System.out.println("Producing next generation");
            population.produceNextGeneration();
            generationCount++;
            
            if (DEBUG){
	            //output format: inSize, fSize, paretoSize, maxBeat, avgBeat, maxChord, avgChord, maxStep, avgStep
	            out.print(population.mPopulation.size()+","+population.mFeasable.size()+",");
	            //print first element of pareto front
	            if (!population.mFeasable.isEmpty()){
	            	double maxBeat, avgBeat, maxChord, avgChord, maxStep, avgStep;
	            	maxBeat = avgBeat = maxChord = avgChord = maxStep = avgStep = 0;
	            	
	            	for (Gene gene : population.mFeasable) {
						if (maxBeat < gene.mBeatFitness){
							maxBeat = gene.mBeatFitness;
						}
						if (maxChord < gene.mChordFitness){
							maxChord = gene.mChordFitness;
						}
						if (maxStep < gene.mStepFitness){
							maxStep = gene.mStepFitness;
						}
						avgBeat += gene.mBeatFitness;
						avgChord += gene.mChordFitness;
						avgStep += gene.mStepFitness;
					}
	            	avgBeat /= population.mFeasable.size();
	            	avgChord /= population.mFeasable.size();
	            	avgStep /= population.mFeasable.size();
	            	
	            	
		            Ranking ranking = new Ranking(population.mFeasable);
		            ArrayList<Gene> front = ranking.getSubfront(0);
		            out.println(front.size()+ "," +maxBeat+","+ avgBeat+","+ maxChord+","+ avgChord+","+ maxStep+","+ avgStep);
		            int count = 0;
		            for (Gene gene : front) {
						if (gene.mStepFitness !=0)
							count++;
					}
		            Random rand = new Random();
		            Gene nonDom = front.get(rand.nextInt(front.size()));
		            System.out.println("Gen:"+generationCount +"\tsize=" + population.mFeasable.size() +"\tpareto=" + front.size() );
		            paretoOut.println(generationCount);
		            for (Gene gene : front) {
		            	paretoOut.println(gene.mBeatFitness + "," + gene.mChordFitness + "," + gene.mStepFitness);
		            	System.out.println("\to1=" + gene.mBeatFitness + "\to2=" + gene.mChordFitness + "\to3=" + gene.mStepFitness);
					}  
	            }
	            else{
	            	out.println("0,0,0,0,0,0,0");
	            }
            }
        }
        
        if(DEBUG){
	        //close writers
	        out.close();
	        paretoOut.close();
        }
        
//        System.out.println(CHORDS.toString());
        
        //Create melody with one of the best individuals
        Ranking ranking = new Ranking(population.mFeasable);
        Random rand = new Random();
        ArrayList<Gene> front = ranking.getSubfront(0);
        Gene nonDom = front.get(rand.nextInt(front.size()));
//        System.out.println("\to1=" + nonDom.mBeatFitness + "\to2=" + nonDom.mChordFitness + "\to3=" + nonDom.mStepFitness);
		String sequence = Arrays.toString(nonDom.mChromosome);
		sequence = sequence.replace(",", "");
//		System.out.println(sequence);
		
		Note[]  notes = new Note[CHROMOSOME_SIZE];
		for (int i = 0; i<notes.length; i++){ 
			int tmp = population.mFeasable.get(population.mFeasable.size()-1).mChromosome[i];
			int n = 0;
			switch(tmp%7){
			case 0:
				n = 0;
				break;
			case 1:
				n = 2;
				break;
			case 2:
				if (major)
					n = 4;
				else
					n = 3;
				break;
			case 3:
				n = 5;
				break;
			case 4:
				n = 7;
				break;
			case 5:
				n = 9;
				break;
			case 6:
				n = 11;
				break;
			}
			n+=key;
			if (tmp >= 7)
				n += 12;
			notes[i] = new Note(n, 0.5);
		}
    	
		return notes;
    	
    }

    private void moveFeasable() {
    	ArrayList<Gene> toRemove = new ArrayList<Gene>();
		for (Gene g : mPopulation) {
			if (feasible(g)){
				g.calculateFitness();
				mFeasable.add(g);
				toRemove.add(g);
			}
		}
		for (Gene gene : toRemove) {
			mPopulation.remove(gene);
		}
		
		toRemove = new ArrayList<Gene>();
		for (Gene g : mFeasable) {
			if (!feasible(g)){
				mPopulation.add(g);
				toRemove.add(g);
			}
		}
		for (Gene gene : toRemove) {
			mFeasable.remove(gene);
		}
	}
    
	private boolean feasible(Gene g) {
		float second =0;
		boolean bigLeap = false;
		boolean repeat = false;
		for (int i = 0; i < g.mChromosome.length-1; i++) {
			if (Math.abs(g.mChromosome[i] - g.mChromosome[i+1]) == 1){
				second++;
			}
			if (Math.abs(g.mChromosome[i] - g.mChromosome[i+1]) > 4){
				bigLeap = true;
			}
			if (Math.abs(g.mChromosome[i] - g.mChromosome[i+1]) == 0){
				repeat = true;
			}
		}
		if (repeat || bigLeap || second/(float)(g.mChromosome.length-1) < MIN_PERCENTAGE_SECONDS)
			return false;
		else
			return true;
	}
	
	//return the chord under the specified note
	public static int getChord(int genomeIndex){
		int index = 0;
		int tmp = 0;
		do {
			tmp += NOTES_PER_CHORD[index];
			index++;
		} while(genomeIndex>=tmp);
		return index-1;
	}
	
	//calculate crowding distance for a set of solutions
	public void crowdingDistanceAssignment(ArrayList<Gene> solutionSet) {
	    int size = solutionSet.size();

	    if (size == 0)
	      return;

	    if (size == 1) {
	      solutionSet.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
	      return;
	    } // if

	    if (size == 2) {
	      solutionSet.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
	      solutionSet.get(1).setCrowdingDistance(Double.POSITIVE_INFINITY);
	      return;
	    } // if       

	    //Use a new SolutionSet to evite alter original solutionSet
	    ArrayList<Gene> front = new ArrayList<Gene>(size);
	    for (int i = 0; i < size; i++){
	      front.add(solutionSet.get(i));
	    }

	    for (int i = 0; i < size; i++)
	      front.get(i).setCrowdingDistance(0.0);

	    double objetiveMaxn;
	    double objetiveMinn;
	    double distance;


	      // Sort the population by the first objective           
	      QuickSort.quickSortBeat(front, 0, front.size()-1);
	      objetiveMinn = front.get(0).getmBeatFitness();
	      objetiveMaxn = front.get(front.size()-1).getmBeatFitness();
	
	      //Set de crowding distance            
	      front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
	      front.get(size-1).setCrowdingDistance(Double.POSITIVE_INFINITY);
	
	      for (int j = 1; j < size-1; j++) {
	        distance = front.get(j+1).getmBeatFitness() - front.get(j-1).getmBeatFitness();
	        if (objetiveMaxn - objetiveMinn != 0)
	        	distance = distance / (objetiveMaxn - objetiveMinn);
	        else distance = 0;
	        distance += front.get(j).getCrowdingDistance();
	        front.get(j).setCrowdingDistance(distance);
	      } // for
	      
	   // Sort the population by the second objective           
	      QuickSort.quickSortChord(front, 0, front.size()-1);
	      objetiveMinn = front.get(0).getmChordFitness();
	      objetiveMaxn = front.get(front.size()-1).getmChordFitness();
	
	      //Set de crowding distance            
	      front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
	      front.get(size-1).setCrowdingDistance(Double.POSITIVE_INFINITY);
	
	      for (int j = 1; j < size-1; j++) {
	        distance = front.get(j+1).getmChordFitness() - front.get(j-1).getmChordFitness();
	        if (objetiveMaxn - objetiveMinn != 0)
	        	distance = distance / (objetiveMaxn - objetiveMinn);
	        else distance = 0;
	        distance += front.get(j).getCrowdingDistance();
	        front.get(j).setCrowdingDistance(distance);
	      } // for
	      
	   // Sort the population by the third objective           
	      QuickSort.quickSortStep(front, 0, front.size()-1);
	      objetiveMinn = front.get(0).getmStepFitness();
	      objetiveMaxn = front.get(front.size()-1).getmStepFitness();
	
	      //Set de crowding distance            
	      front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
	      front.get(size-1).setCrowdingDistance(Double.POSITIVE_INFINITY);
	
	      for (int j = 1; j < size-1; j++) {
	        distance = front.get(j+1).getmStepFitness() - front.get(j-1).getmStepFitness();
	        if (objetiveMaxn - objetiveMinn != 0)
	        	distance = distance / (objetiveMaxn - objetiveMinn);
	        else distance = 0;
	        distance += front.get(j).getCrowdingDistance();
	        front.get(j).setCrowdingDistance(distance);
	      } // for
       
	  } // crowdingDistanceAssing 
	
	// Genetic Algorithm maxA testing method
    public static void main( String[] args ) throws NumberFormatException, InvalidMidiDataException, IOException{
    	
    	ProgressionGraph graph = new ProgressionGraph();
    	//ArrayList<String> chords = graph.getSequenceClean(lenght, "I");
    	CHORDS = graph.getSequenceInt(4, "I", "I");
    	
    	System.out.println(CHORDS.toString());
    	
        // Initializing the population (we chose 500 genes for the population,
        // but you can play with the population size to try different approaches)
    	
    	System.out.println("Generating starting population");
        GeneticAlgorithm population = new GeneticAlgorithm(POPULATION_SIZE);
        int generationCount = 0;
        // For the sake of this sample, evolution goes on forever.
        // If you wish the evolution to halt (for instance, after a number of
        //   generations is reached or the maximum fitness has been achieved),
        //   this is the place to make any such checks
        float avgFitness=0.f;
        while(generationCount < GENERATION_N){
            // --- evaluate current generation:
        	System.out.println("Evaluating population");
            population.evaluateGeneration();
            // --- print results here:
            // we choose to print the average fitness,
            // as well as the maximum and minimum fitness
            // as part of our progress monitoring
            avgFitness=0.f;
            float minFitness=Float.POSITIVE_INFINITY;
            float maxFitness=Float.NEGATIVE_INFINITY;
            //Gene bestIndividual = null;
//            Gene worstIndividual;
            for(int i = 0; i < population.size(); i++){
                float currFitness = population.getGene(i).getFitness();
                avgFitness += currFitness;
                if(currFitness < minFitness){
                    minFitness = currFitness;
//                    worstIndividual = population.getGene(i);
                }
                if(currFitness > maxFitness){
                    maxFitness = currFitness;
                    //bestIndividual = population.getGene(i);
                }
            }
            if(population.size()>0){ avgFitness = avgFitness/population.size(); }
            String output = "Generation: " + generationCount;
            output += "\t AvgFitness: " + avgFitness;
            output += "\t MinFitness: " + minFitness;
            output += "\t MaxFitness: " + maxFitness;
            System.out.println(output);

            // produce next generation:
            System.out.println("Producing next generation");
            population.produceNextGeneration();
            generationCount++;
        }
        
        System.out.println(CHORDS.toString());
        
        //Create melody with best individual
        QuickSort.quickSort(population.mPopulation, 0, population.mPopulation.size()-1);
        //population.mPopulation.get(population.mPopulation.size()-1).printGene();
	    String sequence = population.mPopulation.get(population.mPopulation.size()-1).getPhenotype();
		System.out.println(sequence);
		sequence = Arrays.toString(population.mPopulation.get(population.mPopulation.size()-1).mChromosome);
		sequence = sequence.replace(",", "");
		System.out.println(sequence);
//		String subSeq = population.mPopulation.get(population.mPopulation.size()-1).debugSubSeq();
//		System.out.println(subSeq);
    }
};

