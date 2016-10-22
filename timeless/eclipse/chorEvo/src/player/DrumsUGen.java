package player;

import java.util.ArrayList;
import java.util.Random;

import composition.Composition;
import composition.RhythmPattern;
import jm.music.data.Note;
import jm.music.data.Phrase;
import jm.music.data.Rest;
import jm.music.tools.Mod;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.events.KillTrigger;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Noise;
import net.beadsproject.beads.ugens.Panner;

public class DrumsUGen extends Bead {
	
	AudioContext ac;
	int pitch;
	Composition composition;
	int phraseIndex =0;
	Phrase phrase;
	long startTick=0;
	long duration = 0;
	float gain = 0.2f;
	float irregularity = 0;
	
	public DrumsUGen(AudioContext audiocontext, Composition currentlyPlaying) {
		ac = audiocontext;
		composition = currentlyPlaying;
	}
	
	public void messageReceived(Bead message) {
		if (composition!= null){
			Random random = new Random();
			Clock c = (Clock)message;
			if(c.getCount() >= startTick + duration) {
				Note note = phrase.getNote(phraseIndex);
				pitch = note.getPitch();
				float freq = Pitch.mtof(pitch -12);
				
				//Noise drum
				Noise n = new Noise(ac);
				Envelope decay = new Envelope(ac, 0.7f);
				Gain decayGain = new Gain(ac, 1, decay);
		        decayGain.addInput(n);
		        Panner p = new Panner(ac, random.nextFloat()/2 + 0.5f);
		        p.addInput(decayGain);
		        decay.addSegment(0, random.nextFloat()*150, new KillTrigger(p));
		        
				Gain g = new Gain(ac, 1, gain);
				g.addInput(p);
	        	ac.out.addInput(g);
				startTick = c.getCount();
				duration = JMToBeads.toTicks(note.getRhythmValue());
				//	          System.out.println(startTick + " " + duration);
				if (phraseIndex == phrase.getNoteArray().length-1){
//					System.out.println("drums reset at "+c.getCount()+duration);
					phraseIndex = 0;
					updatePhrase();
				}
				else
					phraseIndex++;
			}
		}
	}
	
	public void setGain(float gain){
		this.gain = gain;
	}
	
	public float getGain(){
		return gain;
	}

	public void setComposition(Composition currentlyPlaying) {
		composition = currentlyPlaying;
		updatePhrase();
		phraseIndex = 0;
	}
	
	public void updatePhrase(){
		phrase = makePhrase(irregularity);
	}
	
	public Phrase makePhrase(float irregularity){
		if (irregularity<0 || irregularity>1f)
			System.out.println("Irregularity value out of bounds: "+ irregularity);
		
		RhythmPattern rhythmP = composition.rhythmP;
		ArrayList<String> chords = composition.chords;
		int key = composition.key;
		ArrayList<String> chordsVar = composition.chordsVar;
		
		double[] accRhythm = rhythmP.getmPattern();
    	int[] accPatternMaj = rhythmP.getmPatternMaj();
    	int[] accPatternMin = rhythmP.getmPatternMin();
		Random rand = new Random();
		Phrase accPhrase = new Phrase();
    	for (int i =0; i < chords.size(); i++) {
			Note[] n = new Note[accRhythm.length*2];
			Phrase tmp = new Phrase();
			for (int j = 0; j < accRhythm.length; j++) {
					n[j*2] = new Note(0, accRhythm[j]/2);
					n[j*2 + 1] = new Note(0, accRhythm[j]/2);
					
					tmp.addNote(n[j*2]);
					tmp.addNote(n[j*2 + 1]);
					//accPhrase.addNote(n[j]);
			}
			
//			Mod.mutate(tmp, 0, (int)(tmp.getSize()*irregularity/2), null, 0, 0, rhythmP.getmPattern());
//			Mod.changeLength(tmp, 1);
			
			Phrase tmpWithRests = new Phrase();
			Note[] notes = tmp.getNoteArray();
			for (int j = 0; j < notes.length; j++) {
				if (rand.nextFloat() < irregularity){
//					double restDur = rand.nextDouble() * notes[j].getDuration();
					double restDur = notes[j].getDuration()/4;
					Rest rest = new Rest ( restDur );
					notes[j].setDuration(notes[j].getDuration()-restDur);
					tmpWithRests.add(rest);
					tmpWithRests.add(notes[j]);
				}
				else {
					tmpWithRests.add(notes[j]);
				}
			}
			tmp = tmpWithRests;
			
			Mod.append(accPhrase, tmp);
			
		}
    	return accPhrase;
	}
	
	public void setIrregularity(float irr){
		if (irr <0 || irr > 1)
			System.out.println("Irregularity value out of bounds: "+ irr);
		irregularity = irr;
	}
}
