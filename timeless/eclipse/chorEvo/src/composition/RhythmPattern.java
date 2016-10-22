package composition;

import java.util.Random;

public class RhythmPattern implements java.io.Serializable {
	int k;
	int n;
	static int[] accPatternMaj0 = {0, 4, 7, 12};
	static int[] accPatternMin0 = {0, 3, 7, 12};
	
	//variables of class
	double[] mPattern;
	String mbasePattern;
	int[] mPatternMaj;
	int[] mPatternMin;
	String mCurrentPattern;
	
	public RhythmPattern(double[] pattern, String basePattern, int[] patternMaj, int[] patternMin, int k, int n){
		mPattern = pattern;
		mbasePattern = basePattern;
		mPatternMaj = patternMaj;
		mPatternMin = patternMin;
		
		this.k= k;
		this.n=n;
	}
	
	public static RhythmPattern randomPattern(){
		//choose rhytmic pattern
		double[] pattern;
		Random rand = new Random();
		//time divided either in 8, 16 or 32
		int n = (rand.nextInt(2)+1) * 8;
		int k = rand.nextInt(n/2)+4;
		String basePattern = EuclideanRhythms.Euclidean(k, n);
		pattern = EuclideanRhythms.translateToRhythm(basePattern, k, n);
		
		//choose harmonic pattern
		int[] patternMaj;
		int[] patternMin;
		switch (rand.nextInt(1)){
		case 0:
			patternMaj = accPatternMaj0;
			patternMin = accPatternMin0;
			break;
		default:
			patternMaj = accPatternMaj0;
			patternMin = accPatternMin0;	
		}
		
		//shuffle and increase patterns
		//pattern = shuffle(pattern);
		patternMaj = shuffle(patternMaj);
		patternMin = shuffle(patternMin);
		//pattern = increase(pattern);
		patternMaj = increaseTo(patternMaj, pattern.length);
		patternMin = increaseTo(patternMin, pattern.length);
		
		//create notes
		
		
 		return new RhythmPattern(pattern, basePattern, patternMaj, patternMin, k, n);
	}

	private static int[] increaseTo(int[] pattern, int length) {
		if (length <= 4)
			return pattern;
		else{
			while (pattern.length != length){
				pattern = newNote(pattern);
			}
			return pattern;
		}
	}

	private static int[] newNote(int[] pattern) {
		int[] tmp = new int[pattern.length+1];
		Random rand = new Random();
		int index = rand.nextInt(pattern.length);
		for (int i = 0; i < tmp.length; i++) {
			if(i<index){
				tmp[i]=pattern[i];
			}
			else{
				if (i==index){
					tmp[i]=pattern[rand.nextInt(pattern.length)];
				}
				else{
					tmp[i]=pattern[i-1];
				}
			}
		}
		return tmp;
	}

	private static double[] increase(double[] pattern) {
		return increase(pattern, 0.5d); //change to 0.5 after testing
	}

	private static double[] increase(double[] pattern, double chance) {
		Random rand = new Random();
		if (rand.nextDouble() < chance){
			if (rand.nextFloat()<0.5f)
				pattern = splitBiggest(pattern);
			else
				pattern = splitRandom(pattern);
			//return increase(pattern, chance/2);
		}
		return pattern;
	}

	private static double[] splitRandom(double[] pattern) {
		Random rand = new Random();
		int index = rand.nextInt(pattern.length);
		return split(pattern, index);
	}

	private static double[] splitBiggest(double[] pattern) {
//		double[] tmp = new double[pattern.length+1];
		double max = 0;
		int maxindex=-1;
		for (int i =0; i<pattern.length; i++) {
			if (pattern[i] > max){
				max = pattern[i];
				maxindex = i;
			}
		}
		return split(pattern, maxindex);
	}

	private static double[] split(double[] pattern, int index) {
		double[] tmp = new double[pattern.length+1];
		for (int i = 0; i < tmp.length; i++) {
			if(i<index){
				tmp[i]=pattern[i];
			}
			else{
				if (i==index){
					tmp[i]=pattern[i]/2;
				}
				else{
					if (i==index+1){
						tmp[i]=pattern[i-1]/2;
					}
					else{
						tmp[i]=pattern[i-1];
					}
				}
			}
		}
		return tmp;
	}

	private static double[] shuffle(double[] pattern) {
		Random rand = new Random();
		double[] tmp = new double[4];
		switch (rand.nextInt(4)){
		//nothing
		case 0:
			return pattern;
		//inversion
		case 1:
			tmp[0] = pattern[3];
			tmp[1] = pattern[2];
			tmp[2] = pattern[1];
			tmp[3] = pattern[0];
			return tmp;
		//1423
		case 2:
			tmp[0] = pattern[0];
			tmp[1] = pattern[3];
			tmp[2] = pattern[1];
			tmp[3] = pattern[2];
			return tmp;
		//3412
		case 3:
			tmp[0] = pattern[2];
			tmp[1] = pattern[3];
			tmp[2] = pattern[0];
			tmp[3] = pattern[1];
			return tmp;
		}
		return pattern;
	}
	
	private static int[] shuffle(int[] pattern) {
		Random rand = new Random();
		int[] tmp = new int[4];
		switch (rand.nextInt(4)){
		//nothing
		case 0:
			return pattern;
		//inversion
		case 1:
//			tmp[0] = pattern[3];
			tmp[1] = pattern[3];
			tmp[2] = pattern[2];
			tmp[3] = pattern[1];
			return tmp;
		//1423
		case 2:
//			tmp[0] = pattern[0];
			tmp[1] = pattern[3];
			tmp[2] = pattern[1];
			tmp[3] = pattern[2];
			return tmp;
		//3412
		case 3:
//			tmp[0] = pattern[2];
			tmp[1] = pattern[1];
			tmp[2] = pattern[3];
			tmp[3] = pattern[2];
			return tmp;
		}
		return pattern;
	}

	public double[] getmPattern() {
		return mPattern;
	}
	
	public int[] getmPatternMaj() {
		return mPatternMaj;
	}
	
	public int[] getmPatternMin() {
		return mPatternMin;
	}

	public void updateRhythm(float irregularity) {
		mCurrentPattern = EuclideanRhythms.irregularize(mbasePattern, irregularity);
//		System.out.println(mbasePattern + "\n" +newPattern + " " + k);
		mPattern = EuclideanRhythms.translateToRhythm(mCurrentPattern, k, n);
	}
}
