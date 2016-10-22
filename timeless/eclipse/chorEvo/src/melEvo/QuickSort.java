package melEvo;

import java.util.ArrayList;

public class QuickSort {
	 
	/*public static void main(String[] args) {
		int[] x = { 9, 2, 4, 7, 3, 7, 10 };
		printArray(x);
 
		int low = 0;
		int high = x.length - 1;
 
		quickSort(x, low, high);
		printArray(x);
	}*/
 
	public static void quickSort(ArrayList<Gene> arr, int low, int high) {
 
		if (arr == null || arr.size() == 0)
			return;
 
		if (low >= high)
			return;
 
		//pick the pivot
		int middle = low + (high - low) / 2;
		float pivot = arr.get(middle).getFitness();
 
		//make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (arr.get(i).getFitness() < pivot) {
				i++;
			}
 
			while (arr.get(j).getFitness() > pivot) {
				j--;
			}
 
			if (i <= j) {
				Gene temp = arr.get(i);
				arr.set(i, arr.get(j));
				arr.set(j, temp);
				i++;
				j--;
			}
		}
 
		//recursively sort two sub parts
		if (low < j)
			quickSort(arr, low, j);
 
		if (high > i)
			quickSort(arr, i, high);
	}
 
	public static void printArray(int[] x) {
		for (int a : x)
			System.out.print(a + " ");
		System.out.println();
	}
	
	public static void quickSortStep(ArrayList<Gene> arr, int low, int high) {
		 
		if (arr == null || arr.size() == 0)
			return;
 
		if (low >= high)
			return;
 
		//pick the pivot
		int middle = low + (high - low) / 2;
		float pivot = arr.get(middle).getmStepFitness();
 
		//make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (arr.get(i).getmStepFitness() < pivot) {
				i++;
			}
 
			while (arr.get(j).getmStepFitness() > pivot) {
				j--;
			}
 
			if (i <= j) {
				Gene temp = arr.get(i);
				arr.set(i, arr.get(j));
				arr.set(j, temp);
				i++;
				j--;
			}
		}
 
		//recursively sort two sub parts
		if (low < j)
			quickSortStep(arr, low, j);
 
		if (high > i)
			quickSortStep(arr, i, high);
	}
	
	public static void quickSortChord(ArrayList<Gene> arr, int low, int high) {
		 
		if (arr == null || arr.size() == 0)
			return;
 
		if (low >= high)
			return;
 
		//pick the pivot
		int middle = low + (high - low) / 2;
		float pivot = arr.get(middle).getmChordFitness();
 
		//make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (arr.get(i).getmChordFitness() < pivot) {
				i++;
			}
 
			while (arr.get(j).getmChordFitness() > pivot) {
				j--;
			}
 
			if (i <= j) {
				Gene temp = arr.get(i);
				arr.set(i, arr.get(j));
				arr.set(j, temp);
				i++;
				j--;
			}
		}
 
		//recursively sort two sub parts
		if (low < j)
			quickSortChord(arr, low, j);
 
		if (high > i)
			quickSortChord(arr, i, high);
	}
	
	public static void quickSortBeat(ArrayList<Gene> arr, int low, int high) {
		 
		if (arr == null || arr.size() == 0)
			return;
 
		if (low >= high)
			return;
 
		//pick the pivot
		int middle = low + (high - low) / 2;
		float pivot = arr.get(middle).getmBeatFitness();
 
		//make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (arr.get(i).getmBeatFitness() < pivot) {
				i++;
			}
 
			while (arr.get(j).getmBeatFitness() > pivot) {
				j--;
			}
 
			if (i <= j) {
				Gene temp = arr.get(i);
				arr.set(i, arr.get(j));
				arr.set(j, temp);
				i++;
				j--;
			}
		}
 
		//recursively sort two sub parts
		if (low < j)
			quickSortBeat(arr, low, j);
 
		if (high > i)
			quickSortBeat(arr, i, high);
	}

}