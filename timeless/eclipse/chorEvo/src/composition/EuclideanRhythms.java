package composition;

import java.awt.JobAttributes.SidesType;
import java.util.ArrayList;
import java.util.Random;

public class EuclideanRhythms {

	static double[] EuclideanRhythm(int k, int n){
		String rhythmS = Euclidean(k, n);
		double[] array = new double[k];
		int index = 0;
		double tmp = 1;
		for (int i = 1; i < rhythmS.length(); i++) {
			if (rhythmS.charAt(i) == '0'){
				tmp++;
			}
			if (rhythmS.charAt(i) == '1'){
				array[index] = tmp / n;
				tmp = 1;
				index++;
			}
		}
		array[index] = tmp / n;
		
		return array;
	}
	
	static double[] translateToRhythm(String rhythmS, int k, int n){
		//check if first char is 0, if so add extra duration for rest, needs to be interpreted by instrument(not very good)
//		int rest = 0;
//		if (rhythmS.charAt(0)=='0')
//			rest = 1;
		System.out.println(rhythmS + " " + k + " "+ n);
		double[] array = new double[countOnes(rhythmS)];
		int index = 0;
		double tmp = 1;
		for (int i = 1; i < rhythmS.length(); i++) {
			if (rhythmS.charAt(i) == '0'){
				tmp++;
			}
			if (rhythmS.charAt(i) == '1'){
				array[index] = tmp / n;
				tmp = 1;
				index++;
			}
		}
		array[index] = tmp / n;
		
		return array;
	}
	
	static int countOnes(String rhythm){
		int ones = 0;
		for (int i = 0; i < rhythm.length(); i++) {
			if (rhythm.charAt(i) == '1')
				ones++;
		}
		return ones;
	}
	
	static String Euclidean(int k, int n){
		//initialize list
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < k; i++) {
			list.add("1");
		}
		for (int i = 0; i < n-k; i++) {
			list.add("0");
		}
		
		//stop when remainder is only one sequence
		while (list.get(list.size()-1).equals(list.get(list.size()-2)) && notAllSame(list)){
			//find element to add
			String tmp = list.get(list.size()-1);
			int currentIndex = list.size()-1;
			int addIndex = 0;
			while (list.get(currentIndex).equals(tmp) && !list.get(addIndex).equals(tmp)){
				list.remove(currentIndex);
				list.set(addIndex, list.get(addIndex).concat(tmp));
				currentIndex--;
				addIndex++;
			}
		}
		
		//make string
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result = result.concat(list.get(i));
		}
		
		return result;
	}
	
	private static boolean notAllSame(ArrayList<String> list) {
		String tmp = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			if (!tmp.equals(list.get(i)))
				return true;
		}
		return false;
	}
	
	public static String irregularize(String rhythm, float irregularity){
		Random rand = new Random();
		int size = rhythm.length();
		int beat = size/4; //assuming 4/4, for now TODO maybe generalize this better
		while (rand.nextFloat() < irregularity){
			irregularity /= 2;
			rhythm = moveFromBeat(rhythm, beat);
		}
		return rhythm;
	}

	private static String moveFromBeat(String rhythm, int beat) {
		char[] array = rhythm.toCharArray();
		ArrayList<Integer> candidates = new ArrayList<>();
		Random rand = new Random();
		//find where we have note on beat
		for (int i = 1; i < array.length; i+=beat) { //TODO currently never moves from first position
			if (array[i] == '1')
				candidates.add(i);
		}
		
		int a;
		int b;
		//if no note on beats chose random one TODO never move from first position, fix function to convert in double[] to allow it
		if (candidates.isEmpty()){
			do{
				a = rand.nextInt(array.length); 
			}while (a==0);
			do {
				b = rand.nextInt(array.length);
			} while (array[b]=='1' && b==0);
		}
		//else chose an element and move it to a random position (not containing a 1)
		else{
			a = candidates.get(rand.nextInt(candidates.size()));
			do {
				b = rand.nextInt(array.length);
			} while (array[b]=='1');
		}
		//swap elements
		array[a]='0';
		array[b]='1';
		return String.copyValueOf(array);
	}

	public static void main(String[] args) {
		System.out.println(Euclidean(5, 8));
		double[] rhythm = EuclideanRhythm(5, 8);
		for (int i = 0; i < rhythm.length; i++) {
			System.out.print(rhythm[i]+" ");
		}
		System.out.println();
		System.out.println();
		
		int k = 5;
		int n = 8;
		String rhythmS = Euclidean(k, n);
		System.out.println(rhythmS);
		rhythmS = irregularize(rhythmS, 1f);
		System.out.println(rhythmS);
		double[] durations = translateToRhythm(rhythmS, k, n);
		for (int i = 0; i < durations.length; i++) {
			System.out.print(durations[i]+" ");
		}
	}
}
