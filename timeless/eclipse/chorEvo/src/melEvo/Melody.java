package melEvo;

import java.util.ArrayList;
import java.util.Random;

import javax.sound.midi.Instrument;

import composition.Composition;

import jm.music.data.CPhrase;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jm.util.Play;
import jm.util.View;
import jm.util.Write;
import progression.ProgressionGraph;

public class Melody implements java.io.Serializable{
	
	private  Score s = new Score("Progression example");
	private  Part p = new Part("Piano", 0, 0);
	private Part melody = new Part("Melody",1,0);
	private Part snare = new Part("Snare", 2, 9);
	double bar = 1.0;
	boolean loop = false;
	Score score;
	static Composition composition;
	
	public Phrase mel;
	public int[] amountOfNotes;
	
	double[] pattern0 = {1.0, 1.0, 0.5, 1.5};
	double[] pattern1 = {0.5, 1.0, 1.5, 1.0};
	double[] pattern2 = {2.0, 0.5, 1.0, 0.5};
	double[] pattern3 = {1.5, 0.5, 1.0, 1.0};
	double[][] patterns = {pattern0, pattern1, pattern2, pattern3};

	public static void main(String[] args) throws InterruptedException {
//		Melody m = new Melody(8);
//		Test t = new Test(m.score);
////		t.playMidi(m.score);
//		Player pl = new Player();
//		pl.play(composition);
	}
	
	public Melody(ArrayList<String> chords, int key) {
		
    	//int key = 0;

    	Random rand = new Random();
    	
//    	System.out.println(chords.toString());
//    	ArrayList<String> variations = graph.variate(chords);
//    	System.out.println(variations.toString());
    	
    	amountOfNotes = new int[chords.size()];
    	for (int i = 0; i < amountOfNotes.length; i++) {
			amountOfNotes[i] = rand.nextInt(5)+1;
		}
    	
    	//evolve the melody notes
    	Note[] notes = GeneticAlgorithm.generate(chords, key, true, amountOfNotes);
    	
    	//give notes a rhythmic pattern
//    	double[] pattern = null;
//    	for (int i = 0; i < notes.length; i++) {
//    		if (i%4==0)
//    			pattern = patterns[rand.nextInt(4)];
//    		notes[i].setLength(pattern[i%4]/2);
//		}
    	
    	//calculate note durations
//    	System.out.print("Amount of Notes: ");
//    	for (int i = 0; i < amountOfNotes.length; i++) {
//			System.out.print(amountOfNotes[i] + " ");
//		}
//    	System.out.println();
    	int noteCounter = 0;
    	for (int i = 0; i < amountOfNotes.length; i++) {
    		double timeLeft = 1d;
			for (int j = noteCounter; j < noteCounter+amountOfNotes[i]; j++) {
				//if last note of the chord add remaining time
				if (j == noteCounter+amountOfNotes[i]-1){
					notes[j].setLength(timeLeft*bar);
				}
				//otherwise get random duration
				else{
					double newDuration = randomDuration(timeLeft);
					timeLeft -= newDuration;
					notes[j].setLength(newDuration*bar);
				}
			}
			noteCounter += amountOfNotes[i];
		}
    	
    	mel = new Phrase(notes);
    	Mod.transpose(mel, 48);
    	melody.add(mel);
    	s.add(melody);
    	
//    	double[] accRhythm = {0.25, 0.5, 0.125, 0.125};
////    	double[] accRhythm = {0.25, 0.125, 0.5, 0.125};
//    	int[] accPatternMaj = {0, 4, 7, 12};
//    	int[] accPatternMin = {0, 3, 7, 12};
//    	Phrase accPhrase = new Phrase();
//    	CPhrase accCPhrase = new CPhrase();
//    	Part accompaniment = new Part(3);
//    	for (int i =0; i < chords.size(); i++) {
//			Note[] n = new Note[accPatternMaj.length];
//			for (int j = 0; j < n.length; j++) {
////				if (rand.nextDouble()>0.7d){
////					Note[] c = new Note[3];
////					if (major(chords.get(i))){
////						c[0] = new Note(root(chords.get(i), key, 48), accRhythm[j]*bar);
////						c[1] = new Note(root(chords.get(i), key, 48) + 4, accRhythm[j]*bar);
////						c[2] = new Note(root(chords.get(i), key, 48) + 7, accRhythm[j]*bar);
////					}
////					else {
////						c[0] = new Note(root(chords.get(i), key, 48), accRhythm[j]*bar);
////						c[1] = new Note(root(chords.get(i), key, 48) + 3, accRhythm[j]*bar);
////						c[2] = new Note(root(chords.get(i), key, 48) + 7, accRhythm[j]*bar);
////					}
////					accompaniment.add(accPhrase);
////					accPhrase = new Phrase();
////					accCPhrase.addChord(c);
////					accompaniment.addCPhrase(accCPhrase);
////					accCPhrase = new CPhrase();
////				}
////				else {
//					if (major(chords.get(i)))
//						n[j] = new Note(root(chords.get(i), key, 48) + accPatternMaj[j], accRhythm[j]*bar);
//					else
//						n[j] = new Note(root(chords.get(i), key, 48) + accPatternMin[j], accRhythm[j]*bar);
//					
//					accPhrase.addNote(n[j]);
////				}
//			}
//			
//		}
//    	accompaniment.add(accPhrase);
//    	s.add(accompaniment);
    	
//    	for (int i =0; i < chords.size(); i++) {
//			chord(chords.get(i), variations.get(i), key, 48, bar); //48 is default C
//		}
        
//    	if (!loop){
//	        //closing chord C major
//	        int[] pitchArray = new int[3];
//		 	pitchArray[0] = 48 + 0;
//	        pitchArray[1] = 48 + 4; // major
//		 	pitchArray[2] = 48 + 7;
//		 	//add chord to the part
//			CPhrase chord = new CPhrase();
//			chord.addChord(pitchArray, bar);
//			p.addCPhrase(chord);
//    	}
		
		//pack the part into a score
//		s.addPart(p);
        //s.addPart(bassPart);
		
    	//snare at each bar
//		Phrase beats = new Phrase();
//		for (int i = 0; i < chords.size(); i++) {
//			Note note = new Note((int)(49), bar );
//	       	beats.addNote(note);
//		}
//		snare.add(beats);
//		s.add(snare);
		
		//loop
//		if (loop)
//			Mod.repeat(s);
		
		//display the music
//		View.show(s);
		
		// write the score to a MIDIfile
//		Write.midi(s, "Progression.mid");
		
//		score = s;
		
//		composition = new Composition(key, true, chords, mel, accRhythm, accPatternMaj, accPatternMin);
//		Player player = new Player();
//		player.play(composition);
		
//		Test t = new Test(s);
	}	
    
	private double randomDuration(double timeLeft) {
		//WARNING!!! might be a problem here
				if (timeLeft==0)
					return 0;
				if (timeLeft <= 1d/4d){
					return timeLeft/2;
				}
				Random rand = new Random();
				double res;
				do {
					res = 0;
					double value = rand.nextDouble();
					if (value < 1d/3d)
						res = 1d/8d;
					else {
						if (value < 2d/3d)
							res = 1d/4d;
						else {
							res = 1d/2d;
						}
					}
				} while (res > timeLeft/2);
				return res;
	}

	private int root(String chord, int key, int mod){
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
	
	private boolean major(String chord){
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
	
	private void chord(String chord, String variation, int key, int mod, double lenght){
    	Random rand = new Random();
//    	if (rand.nextFloat()<0.5){
//    		mod = mod +12;
//    	}
    	switch(chord){
		case "I":
			randomInversion(0 + key + mod, true, variation, lenght);
			break;
		case "I/5":
			secondInversion(0 + key + mod, true, variation, lenght);
			break;
		case "ii":
			randomInversion(2 + key + mod, false, variation, lenght);
			break;
		case "iii":
			randomInversion(4 + key + mod, false, variation, lenght);
			break;
		case "IV":
			randomInversion(5 + key + mod, true, variation, lenght);
			break;
		case "V":
			randomInversion(7 + key + mod, true, variation, lenght);
			break;
		case "vi":
			randomInversion(9 + key + mod, false, variation, lenght);
			break;
		}
    }
    
    private void randomInversion(int rootPitch, boolean major, String variation, double lenght){
    	Random rand = new Random();
    	float roll = rand.nextFloat();
    	if (roll < (0.5) || variation!="")
			triad(rootPitch, major, variation, lenght);
		else {
			if (roll < 0.75)
				secondInversion(rootPitch, major, variation, lenght);
			else firstInversion(rootPitch, major, variation, lenght);
		}
    }
	
	private void triad(int rootPitch, boolean major, String variation, double lenght) {
		// build the chord from the rootPitch 
		int[] pitchArray;
		if (variation != "" && variation !="sus" && variation!="m")
			pitchArray = new int[4];
		else pitchArray = new int[3];
	 	pitchArray[0] = rootPitch;
        if(major) { 
            pitchArray[1] = rootPitch + 4; // major
        } else pitchArray[1] = rootPitch + 3; // minor
	 	pitchArray[2] = rootPitch + 7;
	 	
	 	//deal with variations
	 	switch(variation){
    	case "7":
    		pitchArray[3] = rootPitch + 10;
    		break;
    	case "m7":
    		pitchArray[3] = rootPitch + 10;
    		break;
    	case "M7":
    		pitchArray[3] = rootPitch + 11;
    		break;
    	case "2":
    		pitchArray[3] = rootPitch + 2;
    		break;
    	case "6":
    		pitchArray[3] = rootPitch + 9;
    		break;
    	case "m6":
    		pitchArray[3] = rootPitch + 9;
    		break;
    	case "9":
    		pitchArray[3] = rootPitch + 14;
    		break;
    	case "m9":
    		pitchArray[3] = rootPitch + 14;
    		break;
    	case "M9":
    		pitchArray[3] = rootPitch + 14;
    		break;
    	case "11":
    		pitchArray[3] = rootPitch + 17;
    		break;
    	case "13":
    		pitchArray[3] = rootPitch + 21;
    		break;
    	case "sus":
    		pitchArray[1] = rootPitch + 5;
    		break;
    	case "m":
    		pitchArray[1] = rootPitch + 3;
    		break;
    	default:
    		System.out.println("cannot parse variation: " + variation);
    		break;
    	}
	 	
	 	//add chord to the part
		CPhrase chord = new CPhrase();
		chord.addChord(pitchArray, lenght);
		p.addCPhrase(chord);
	}
	
	private void firstInversion(int rootPitch, boolean major, String variation, double lenght) {
		 // build the chord from the rootPitch 
		int[] pitchArray;
		if (variation != "" && variation !="sus" && variation!="m")
			pitchArray = new int[4];
		else pitchArray = new int[3];
		 pitchArray[0] = rootPitch;
		 if(major) { 
			 pitchArray[1] = rootPitch -8; // major
	     } else pitchArray[1] = rootPitch -9; // minor
		 //pitchArray[2] = rootPitch - 2;
		 pitchArray[2] = rootPitch - 5;
		 
		//deal with variations
		 	switch(variation){
	    	case "7":
	    		pitchArray[3] = rootPitch + 10;
	    		break;
	    	case "m7":
	    		pitchArray[3] = rootPitch + 10;
	    		break;
	    	case "M7":
	    		pitchArray[3] = rootPitch + 11;
	    		break;
	    	case "2":
	    		pitchArray[3] = rootPitch + 2;
	    		break;
	    	case "6":
	    		pitchArray[3] = rootPitch + 9;
	    		break;
	    	case "m6":
	    		pitchArray[3] = rootPitch + 9;
	    		break;
	    	case "9":
	    		pitchArray[3] = rootPitch + 14;
	    		break;
	    	case "m9":
	    		pitchArray[3] = rootPitch + 14;
	    		break;
	    	case "M9":
	    		pitchArray[3] = rootPitch + 14;
	    		break;
	    	case "11":
	    		pitchArray[3] = rootPitch + 17;
	    		break;
	    	case "13":
	    		pitchArray[3] = rootPitch + 21;
	    		break;
	    	case "sus":
	    		pitchArray[1] = rootPitch + 5;
	    		break;
	    	case "m":
	    		pitchArray[1] = rootPitch + 3;
	    		break;
	    	default:
	    		System.out.println("cannot parse variation: " + variation);
	    		break;
	    	}
		 	
		 //add chord to the part
		 CPhrase chord = new CPhrase();
		 chord.addChord(pitchArray, lenght);
		 p.addCPhrase(chord);
	}
	
	private void secondInversion(int rootPitch, boolean major, String variation, double lenght) {
		 // build the chord from the rootPitch 
		int[] pitchArray;
		if (variation != "" && variation !="sus" && variation!="m")
			pitchArray = new int[4];
		else pitchArray = new int[3];
		 pitchArray[0] = rootPitch;
		 if(major) { 
			 pitchArray[1] = rootPitch + 4; // major
	     } else pitchArray[1] = rootPitch + 3; // minor
		 //pitchArray[2] = rootPitch - 2;
		 pitchArray[2] = rootPitch - 5;
		 
		//deal with variations
		 	switch(variation){
	    	case "7":
	    		pitchArray[3] = rootPitch + 10;
	    		break;
	    	case "m7":
	    		pitchArray[3] = rootPitch + 10;
	    		break;
	    	case "M7":
	    		pitchArray[3] = rootPitch + 11;
	    		break;
	    	case "2":
	    		pitchArray[3] = rootPitch + 2;
	    		break;
	    	case "6":
	    		pitchArray[3] = rootPitch + 9;
	    		break;
	    	case "m6":
	    		pitchArray[3] = rootPitch + 9;
	    		break;
	    	case "9":
	    		pitchArray[3] = rootPitch + 14;
	    		break;
	    	case "m9":
	    		pitchArray[3] = rootPitch + 14;
	    		break;
	    	case "M9":
	    		pitchArray[3] = rootPitch + 14;
	    		break;
	    	case "11":
	    		pitchArray[3] = rootPitch + 17;
	    		break;
	    	case "13":
	    		pitchArray[3] = rootPitch + 21;
	    		break;
	    	case "sus":
	    		pitchArray[1] = rootPitch + 5;
	    		break;
	    	case "m":
	    		pitchArray[1] = rootPitch + 3;
	    		break;
	    	case "":
	    		break;
	    	default:
	    		System.out.println("cannot parse variation: "+ variation);
	    		break;
	    	}
		 	
		 //add chord to the part
		 CPhrase chord = new CPhrase();
		 chord.addChord(pitchArray, lenght);
		 p.addCPhrase(chord);
	}
}
