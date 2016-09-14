package composition;

import java.util.ArrayList;
import java.util.Random;

import melEvo.Melody;
import progression.ProgressionGraph;
import jm.music.data.Phrase;

public class Composition implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410612124768428287L;
	boolean major;
	public int key;
	public ArrayList<String> chords;
	public ArrayList<String> chordsVar;
	public Phrase mel;
	public RhythmPattern rhythmP;
	public Melody melody;
	
	public Composition() {
		//decide random key 
		Random rand = new Random();
		key = rand.nextInt(12);
		
		major = true;
		
		//create chord progression; WARNING: STATIC AMOUNT OF CHORDS
		ProgressionGraph graph = new ProgressionGraph();
		//ArrayList<String> chords = graph.getSequenceClean(lenght, "I");
		chords = graph.getSequence(8, "I", "I");

		//get melody Phrase
		melody = new Melody(chords, key);
		mel = melody.mel;
		
		//create rhythm
		rhythmP = RhythmPattern.randomPattern();
	}
	
	public Composition(int[] chordsValues) {
		//decide random key 
				Random rand = new Random();
				key = rand.nextInt(12);
				
				major = true;
				
				chords = ProgressionGraph.convertIntArrayToSequence(chordsValues);
				
				//create chord progression;
//				ProgressionGraph graph = new ProgressionGraph();
//				chords = graph.getSequenceWithTension(chordsValues, "I");
				
				//get melody Phrase
				melody = new Melody(chords, key);
				mel = melody.mel;
				
				//create rhythm
				rhythmP = RhythmPattern.randomPattern();
	}
	
	public String getChords() {
		String single = "";
		for(int i=0; i<chords.size(); i++){
		  single += chords.get(i)+" ";
		}
		return single;
	}
	
	public void variateChords(){
		//WARN: need to make variations less drastic
//		chordsVar = ProgressionGraph.variate(chords);
		chordsVar = chords;
	}

	public int root(String chord, int key, int mod){
    	switch(chord){
		case "I":
			return 0 + key + mod;
		case "I/5":
			return 0 + key + mod;
		case "ii":
			return 2 + key + mod;
		case "iii":
			return 4 + key + mod;
		case "IV":
			return 5 + key + mod;
		case "V":
			return 7 + key + mod;
		case "vi":
			return 9 + key + mod;
		}
		return -1;
    }
	
	public boolean major(String chord){
    	switch(chord){
		case "I":
		case "I/5":
		case "IV":
		case "V":
			return true;
		case "ii":
		case "iii":
		case "vi":
			return false;
		}
		return false;
    }
	
	public int adjustForDissonance(int note, float dissonance){
		int n = note%12 - key;
		int octaves = note / 12;
		if (n<0)
			n += 12;
		//in order of introduction: 11, 4, 9, 2, (7)
		if (dissonance >= 0.5f){
			//gradual
//			if (n == 11)
//				return n-1 + (12*octaves);
//			if (dissonance >= 0.625f){
//				if (n == 4)
//					return n-1 + (12*octaves);
//				if (dissonance >= 0.75f){
//					if (n == 9)
//						return n-1 + (12*octaves);
//					if (dissonance >= 0.875f){
//						if (n == 2)
//							return n-1 + (12*octaves);
//					}
//				}
//			}
			//minor after 0.5
			if (n == 4 || n == 9 || n == 11 || n == 2)
				return n-1 + (12*octaves);
		} 
		if (note > 0)
			return note;
		else return 0;
	}

	public void calculateNewRhythm(float irregularity) {
		rhythmP.updateRhythm(irregularity);
	}
}
