package progression;

import java.util.ArrayList;
import java.util.Random;

import jm.JMC;
import jm.music.data.CPhrase;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jm.util.View;
import jm.util.Write;

public class ProgressionPlayer implements JMC{

	private  Score s = new Score("Progression example");
	private  Part p = new Part("Piano", 0, 0);
	private  Part p1 = new Part("Piano1", 0, 0);
	private  Part p2 = new Part("Piano2", 0, 0);
	private  Part p3 = new Part("Piano3", 0, 0);
	private  Part p4 = new Part("Piano4", 0, 0);
	private Part snare = new Part("Snare", 0, 9);
    //private Part bassPart = new Part("left hand", 0, 1);
	private double[] rhythms = new double[] {0.25, 0.5, 1.0, 2.0, 4.0};
	//private double[] rhythms = new double[] {1.0};
	double[] pattern = new double[5];
	double[] pattern0 = {1.0, 0.5, 0.5, 1.5, 0.5};
	double[] pattern1 = {0.5, 0.5, 1.5, 0.5, 1.0};
	double[] pattern2 = {2.0, 0.5, 0.5, 0.5, 0.5};
	double[] pattern3 = {1.5, 0.5, 1.0, 0.5, 0.5};
	private int prev1=-1;
	private int prev2=-1;
	private int prev3=-1;
	private int prev4=-1;
		
	public static void main(String[] args){	
        new ProgressionPlayer(8);
    }
    
    public ProgressionPlayer(int lenght){
    	
    	ProgressionGraph graph = new ProgressionGraph();
    	//ArrayList<String> chords = graph.getSequenceClean(lenght, "I");
    	ArrayList<String> chords = graph.getSequence(lenght, "I", "I");
    	int key = 0;
    	
    	System.out.println(chords.toString());
    	ArrayList<String> variations = graph.variate(chords);
    	System.out.println(variations.toString());
    	
        for (int i =0; i < chords.size(); i++) {
			//chord(chords.get(i), variations.get(i), key, 48); //48 is default C
			createWalk(chords.get(i), variations.get(i), key, 48); //48 is default C
		}
        
        //closing chord C major
        int[] pitchArray = new int[3];
	 	pitchArray[0] = 48 + 0;
        pitchArray[1] = 48 + 4; // major
	 	pitchArray[2] = 48 + 7;
	 	//add chord to the part
		CPhrase chord = new CPhrase();
		chord.addChord(pitchArray, 4.0);
		p1.addCPhrase(chord);
		
		//pack the part into a score
		s.addPart(p);
		Mod.transpose(p1, 12);
		s.addPart(p1);
		Mod.transpose(p2, 12);
		s.addPart(p2);
		s.addPart(p3);
		s.addPart(p4);
        //s.addPart(bassPart);
		
		Phrase beats = new Phrase();
		for (int i = 0; i < chords.size(); i++) {
			Note note = new Note((int)(49), 4.0 );
	       	beats.addNote(note);
		}
		snare.add(beats);
		s.add(snare);
		
		//display the music
		View.show(s);
		
		// write the score to a MIDIfile
		Write.midi(s, "Progression.mid");
	}	
    
    private void createWalk(String chord, String variation, int key, int mod) {
    	ArrayList<Integer> possibleNotes = new ArrayList<Integer>();
    	int rootPitch = 0;
    	@SuppressWarnings("unused")
		int forcedBass;
    	switch(chord){
		case "I":
			rootPitch = 0 + key + mod;
			possibleNotes.add(rootPitch);
	        possibleNotes.add(rootPitch + 4); // major
		 	possibleNotes.add(rootPitch + 7);
			break;
		case "I/5":
			rootPitch = 0 + key + mod;
			possibleNotes.add(rootPitch);
	        possibleNotes.add(rootPitch + 4); // major
		 	possibleNotes.add(rootPitch + 7);
		 	forcedBass = rootPitch + 7;
			break;
		case "ii":
			rootPitch = 2 + key + mod;
			possibleNotes.add(rootPitch);
	        possibleNotes.add(rootPitch + 3); // minor
		 	possibleNotes.add(rootPitch + 7);
			break;
		case "iii":
			rootPitch = 4 + key + mod;
			possibleNotes.add(rootPitch);
	        possibleNotes.add(rootPitch + 3); // minor
		 	possibleNotes.add(rootPitch + 7);
			break;
		case "IV":
			rootPitch = 5 + key + mod;
			possibleNotes.add(rootPitch);
	        possibleNotes.add(rootPitch + 4); // major
		 	possibleNotes.add(rootPitch + 7);
			break;
		case "V":
			rootPitch = 7 + key + mod;
			possibleNotes.add(rootPitch);
	        possibleNotes.add(rootPitch + 4); // major
		 	possibleNotes.add(rootPitch + 7);
			break;
		case "vi":
			rootPitch = 9 + key + mod;
			possibleNotes.add(rootPitch);
	        possibleNotes.add(rootPitch + 3); // minor
		 	possibleNotes.add(rootPitch + 7);
			break;
		}
    	
    	//deal with variations
	 	switch(variation){
    	case "7":
    		possibleNotes.add(rootPitch + 10);
    		break;
    	case "m7":
    		possibleNotes.add(rootPitch + 10);
    		break;
    	case "M7":
    		possibleNotes.add(rootPitch + 11);
    		break;
    	case "2":
    		possibleNotes.add(rootPitch + 2);
    		break;
    	case "6":
    		possibleNotes.add(rootPitch + 9);
    		break;
    	case "m6":
    		possibleNotes.add(rootPitch + 9);
    		break;
    	case "9":
    		possibleNotes.add(rootPitch + 14);
    		break;
    	case "m9":
    		possibleNotes.add(rootPitch + 14);
    		break;
    	case "M9":
    		possibleNotes.add(rootPitch + 14);
    		break;
    	case "11":
    		possibleNotes.add(rootPitch + 17);
    		break;
    	case "13":
    		possibleNotes.add(rootPitch + 21);
    		break;
    	case "sus":
    		possibleNotes.remove((Integer) 3);
    		possibleNotes.remove((Integer) 4);
    		possibleNotes.add(rootPitch + 5);
    		break;
    	case "m":
    		possibleNotes.remove((Integer) 4);
    		possibleNotes.add(rootPitch + 3);
    		break;
    	case "":
    		break;
    	default:
    		System.out.println("cannot parse variation: " + variation);
    		break;
    	}
	 	
	 	Phrase lastP;
	 	p1.add(makeWalkPhrase(possibleNotes, prev1));
	 	lastP = p1.getPhraseArray()[p1.getPhraseArray().length-1];
	 	prev1 = lastP.getNoteArray()[lastP.getNoteArray().length-1].getPitch();

	 	p2.add(makeWalkPhrase(possibleNotes, prev2));
	 	lastP = p2.getPhraseArray()[p2.getPhraseArray().length-1];
	 	prev2 = lastP.getNoteArray()[lastP.getNoteArray().length-1].getPitch();
	 	
	 	p3.add(makeWalkPhrase(possibleNotes, prev3));
	 	lastP = p3.getPhraseArray()[p3.getPhraseArray().length-1];
	 	prev3 = lastP.getNoteArray()[lastP.getNoteArray().length-1].getPitch();
	 	
	 	p4.add(makeWalkPhrase(possibleNotes, prev4));
	 	lastP = p4.getPhraseArray()[p4.getPhraseArray().length-1];
	 	prev4 = lastP.getNoteArray()[lastP.getNoteArray().length-1].getPitch();
	 	
	}

	private Phrase makeWalkPhrase(ArrayList<Integer> possibleNotes, int prev) {
		Random rand = new Random();
		Phrase p = new Phrase();
		
		switch (rand.nextInt(4)) {
        
        case 0:
                pattern = pattern0;
                break;
        case 1:
                pattern = pattern1;
                break;
        case 2:
                pattern = pattern2;
                break;
        case 3:
                pattern = pattern3;
                break;
        default:
                System.out.println("Random number out of range");
        }
		
		for (int i = 0; i < pattern.length; i++) {
			int n;
			int count = 0;
			do {
				n = possibleNotes.get(rand.nextInt(possibleNotes.size()));
				count++;
			} while ((prev != -1 && Math.abs(n-prev) > 7) && count < 100);
			prev = n;
			Note note = new Note(n, pattern[i]);
			p.add(note);
		}
		
		return p;
	}

	private void chord(String chord, String variation, int key, int mod){
    	Random rand = new Random();
    	if (rand.nextFloat()<0.5){
    		mod = mod +12;
    	}
    	switch(chord){
		case "I":
			randomInversion(0 + key + mod, true, variation);
			break;
		case "I/5":
			secondInversion(0 + key + mod, true, variation);
			break;
		case "ii":
			randomInversion(2 + key + mod, false, variation);
			break;
		case "iii":
			randomInversion(4 + key + mod, false, variation);
			break;
		case "IV":
			randomInversion(5 + key + mod, true, variation);
			break;
		case "V":
			randomInversion(7 + key + mod, true, variation);
			break;
		case "vi":
			randomInversion(9 + key + mod, false, variation);
			break;
		}
    }
    
    private void randomInversion(int rootPitch, boolean major, String variation){
    	Random rand = new Random();
    	float roll = rand.nextFloat();
    	if (roll < (0.5) || variation!="")
			triad(rootPitch, major, variation);
		else {
			if (roll < 0.75)
				secondInversion(rootPitch, major, variation);
			else firstInversion(rootPitch, major, variation);
		}
    }
	
	private void triad(int rootPitch, boolean major, String variation) {
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
		chord.addChord(pitchArray, rhythms[(int)(Math.random() * rhythms.length)]);
		p.addCPhrase(chord);
	}
	
	private void firstInversion(int rootPitch, boolean major, String variation) {
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
		 chord.addChord(pitchArray, C);
		 p.addCPhrase(chord);
	}
	
	private void secondInversion(int rootPitch, boolean major, String variation) {
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
		 chord.addChord(pitchArray, C);
		 p.addCPhrase(chord);
	}
}
