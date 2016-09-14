package player;

import java.util.ArrayList;
import java.util.Random;

import composition.Composition;
import composition.RhythmPattern;
import jm.music.data.Note;
import jm.music.data.Phrase;
import jm.music.tools.Mod;
import net.beadsproject.beads.core.AudioContext;
import progression.ProgressionGraph;
import synthesizers.BigSynth;

public class AccompanimentUGen extends InstrumentUGen {

	
	public AccompanimentUGen(AudioContext audiocontext, Composition currentlyPlaying, int instrument, int channel) {
		super(audiocontext, currentlyPlaying, instrument, channel);
		synth = new BigSynth();
		float[] shaper1 = {1,1,1,0,0,0,0,0};
		synth.setParams(0.7, 50, 500, 0.3, 0.1, 0.1, shaper1);
		//gain = 97;
	}

	@Override
	public void updatePhrase() {
		phrase = makePhrase(irregularity, dissonance);
	}

	@Override
	Phrase makeBasePhrase() {
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
			Note[] n = new Note[accPatternMaj.length];
			Phrase tmp = new Phrase();
			for (int j = 0; j < n.length; j++) {
					if (composition.major(chords.get(i))){
						int pitch = composition.root(chords.get(i), key, 48) + accPatternMaj[j];
						pitch = composition.adjustForDissonance(pitch, dissonance);
						int mod = ProgressionGraph.noteTranslationOnVariation(pitch, chordsVar.get(i), key, true);
						if (!(j >= accRhythm.length)) // TODO there is something wrong going on here
							n[j] = new Note(pitch+mod, accRhythm[j]);
					}
					else {
						int pitch = composition.root(chords.get(i), key, 48) + accPatternMin[j];
						pitch = composition.adjustForDissonance(pitch, dissonance);
						int mod = ProgressionGraph.noteTranslationOnVariation(pitch, chordsVar.get(i), key, true);
						n[j] = new Note(pitch+mod, accRhythm[j]);
					}
					
					tmp.addNote(n[j]);
					//accPhrase.addNote(n[j]);
			}
			Mod.append(accPhrase, tmp);
			
		}
    	return accPhrase;
	}
	

}
