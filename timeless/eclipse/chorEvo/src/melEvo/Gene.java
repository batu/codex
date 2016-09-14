package melEvo;

import java.awt.Choice;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Gene {
    // --- variables:

    /**
     * Fitness evaluates to how "close" the current gene is to the
     * optimal solution (i.e. contains only 1s in its chromosome)
     * A gene with higher fitness value from another signifies that
     * it has more 1s in its chromosome, and is thus a better solution
     * While it is common that fitness is a floating point between 0..1
     * this is not necessary: the only constraint is that a better solution
     * must have a strictly higher fitness than a worse solution
     */
    float mFitness;
    float mStepFitness; //fitness for counterstepwise approach to leaps
    float mChordFitness; //fitness for chord belonging of leap notes
    float mBeatFitness; //fitness for big leaps

    int range = 14;
    int mChromosome[];
    
    int rank;
    double crowdingDistance;

    // --- functions:
    /**
     * Allocates memory for the mChromosome array and initializes any other data, such as fitness
     * We chose to use a constant variable as the chromosome size, but it can also be
     * passed as a variable in the constructor
     */
    Gene() {
        // allocating memory for the chromosome array
        mChromosome = new int[GeneticAlgorithm.CHROMOSOME_SIZE];
        // initializing fitness
        mFitness = 0.f;
        randomizeChromosome();
    }

	/**
     * Randomizes the numbers on the mChromosome array to values 0 or 1
     */
    public void randomizeChromosome(){
        Random rand = new Random();
        for (int i = 0; i < mChromosome.length; i++) {
			mChromosome[i]= rand.nextInt(range);
		}
    }
    
    public void calculateFitness(){
    	mStepFitness = 0;
    	mChordFitness = 0;
    	mBeatFitness = 0;
    	
    	//we want prevalence of small intervals: mostly 2nd, 3rd and 4th
    	//smaller amount of 5ths (maybe also 6ths)
    	//leaps should be prepared by contrary stepwise motion to the leap (before and/or after)
    	//no double leaps (for now)
    	
    	//FI-2POP: feasability percentage of leaps, fitness (1: stepwise approach to leaps, 2: leap notes part of underlying chord)
    	
    	int leapNumber = 0;
    	for (int i = 0; i < mChromosome.length-1; i++) {
			if (Math.abs(mChromosome[i] - mChromosome[i+1]) > 1){
				leapNumber++;
				
				//check objective 2
				if (belongsToChord(mChromosome[i], GeneticAlgorithm.getChord(i))){
					mChordFitness++;
				}
				if (belongsToChord(mChromosome[i+1], GeneticAlgorithm.getChord(i+1))){
					mChordFitness++;
				}
				
				//check objective 1
				//if descending jump
				if (mChromosome[i]>mChromosome[i+1]){
					if (i!=0 && mChromosome[i-1] == mChromosome[i] -1 )
						mStepFitness++;
					if (i+1 != mChromosome.length-1 && mChromosome[i+2] == mChromosome[i+1] +1 )
						mStepFitness++;
				}
				//if ascending jump
				else {
					if (i!=0 && mChromosome[i-1] == mChromosome[i] +1 )
						mStepFitness++;
					if (i+1 != mChromosome.length-1 && mChromosome[i+2] == mChromosome[i+1] -1 )
						mStepFitness++;
				}
				
			}
			
			//check objective 3: beat notes on chord
			if (firstNoteInChord(i) && belongsToChord(mChromosome[i], GeneticAlgorithm.getChord(i))){
				mBeatFitness++;
			}
		}
    	
    	//normalize fitnesses
    	mStepFitness = mStepFitness / leapNumber;
    	mChordFitness = mChordFitness / leapNumber;
    	mBeatFitness = mBeatFitness / GeneticAlgorithm.CHORDS.size() ;
    	
    	mFitness = mStepFitness + mChordFitness + mBeatFitness;
//    	mFitness = mStepFitness + mChordFitness;
    }

	private boolean firstNoteInChord(int i) {
		int counter = 0;
		if (i==counter)
			return true;
		for (int j = 0; j < GeneticAlgorithm.NOTES_PER_CHORD.length; j++) {
			counter += GeneticAlgorithm.NOTES_PER_CHORD[j];
			if (i==counter)
				return true;
		}
		return false;
	}

	private boolean belongsToChord(int note, Integer degree) {
		if (note%7 == degree || note%7 == (degree+2)%7 || note%7 == (degree+4)%7)
			return true;
		else
			return false;
	}

	/**
     * Creates a number of offspring by combining (using crossover) the current
     * Gene's chromosome with another Gene's chromosome.
     * Usually two parents will produce an equal amount of offpsring, although
     * in other reproduction strategies the number of offspring produced depends
     * on the fitness of the parents.
     * @param other: the other parent we want to create offpsring from
     * @return Array of Gene offspring (default length of array is 2).
     * These offspring will need to be added to the next generation.
     */
    public Gene[] reproduce(Gene other){
        Gene[] result = new Gene[2];
        // initilization of offspring chromosome goes HERE
        Random rand = new Random();
        result[0] = new Gene();
        result[1] = new Gene();
        int cut = rand.nextInt(mChromosome.length-2)+1;
        for (int i = 0; i < mChromosome.length; i++) {
			if(i<cut){
				result[0].setChromosomeElement(i, mChromosome[i]);
				result[1].setChromosomeElement(i, other.getChromosomeElement(i));
			}
			else{
				result[1].setChromosomeElement(i, mChromosome[i]);
				result[0].setChromosomeElement(i, other.getChromosomeElement(i));
			}
		}
        return result;
    }

    /**
     * Mutates a gene using inversion, random mutation or other methods.
     * This function is called after the mutation chance is rolled.
     * Mutation can occur (depending on the designer's wishes) to a parent
     * before reproduction takes place, an offspring at the time it is created,
     * or (more often) on a gene which will not produce any offspring afterwards.
     */
    public void mutate(){
    	Random rand = new Random();
    	for (int i = 0; i < mChromosome.length; i++) {
			if (rand.nextFloat()<(1/(float) mChromosome.length)){
				//mChromosome[i] = rand.nextInt(range);
				if (rand.nextFloat()<0.5f && mChromosome[i]!=0){
					mChromosome[i] = mChromosome[i]--;
				}
				else{
					if (mChromosome[i] != range-1)
						mChromosome[i]++;
					else
						mChromosome[i]--;
				}
			}
		}
    }
    /**
     * Sets the fitness, after it is evaluated in the GeneticAlgorithm class.
     * @param value: the fitness value to be set
     */
    public void setFitness(float value) { mFitness = value; }
    /**
     * @return the gene's fitness value
     */
    public float getFitness() { return mFitness; }
    
    public float getmChordFitness() {
		return mChordFitness;
	}
    
    public float getmStepFitness() {
		return mStepFitness;
	}
    
    public float getmBeatFitness() {
		return mBeatFitness;
	}
    
    
    /**
     * Returns the element at position <b>index</b> of the mChromosome array
     * @param index: the position on the array of the element we want to access
     * @return the value of the element we want to access (0 or 1)
     */
    public int getChromosomeElement(int index){ return mChromosome[index]; }

    /**
     * Sets a <b>value</b> to the element at position <b>index</b> of the mChromosome array
     * @param index: the position on the array of the element we want to access
     * @param value: the value we want to set at position <b>index</b> of the mChromosome array (0 or 1)
     */
    public void setChromosomeElement(int index, int value){ mChromosome[index]=value; }
    /**
     * Returns the size of the chromosome (as provided in the Gene constructor)
     * @return the size of the mChromosome array
     */
    public int getChromosomeSize() { return mChromosome.length; }
    /**
     * Corresponds the chromosome encoding to the phenotype, which is a representation
     * that can be read, tested and evaluated by the main program.
     * @return a String with a length equal to the chromosome size, composed of A's
     * at the positions where the chromosome is 1 and a's at the posiitons
     * where the chromosme is 0
     */
    public String getPhenotype() {
        // create an empty string
        String result="";
        for(int i = 0; i < mChromosome.length; i++){
            result += mChromosome[i];
            result += "\t";
        }
        return result;
    }
    
    public void setChromosome(int chromosome[]){
    	mChromosome = chromosome;
    }
    
    public Gene clone(){
    	Gene newGene = new Gene();
    	newGene.setFitness(mFitness);
    	newGene.setChromosome(mChromosome);
    	return newGene;
    }

	public void calculateFeasability() {
		float second =0;
		float bigLeap = 0;
		float repeat = 0;
		for (int i = 0; i < mChromosome.length-1; i++) {
			if (Math.abs(mChromosome[i] - mChromosome[i+1]) == 1){
				second++;
			}
			if (Math.abs(mChromosome[i] - mChromosome[i+1]) > 4){
				bigLeap++;
			}
			if (Math.abs(mChromosome[i] - mChromosome[i+1]) ==0){
				repeat++;
			}
		}
		
		float secondLess = 0;
//		if (second/(float)(mChromosome.length-1) < GeneticAlgorithm.MIN_PERCENTAGE_SECONDS)
//			secondLess = GeneticAlgorithm.MIN_PERCENTAGE_SECONDS - second/(float)(mChromosome.length-1);
		secondLess = Math.abs(GeneticAlgorithm.MIN_PERCENTAGE_SECONDS - second/(float)(mChromosome.length-1));
		
		mFitness = -secondLess - bigLeap -repeat;
		
	}

	public void setRank(int i) {
		rank = i;
	}

	public void setCrowdingDistance(double value) {
		crowdingDistance = value;
	}

	public double getCrowdingDistance() {
		return crowdingDistance;
	}

	public int getRank() {
		return rank;
	}
    
}
