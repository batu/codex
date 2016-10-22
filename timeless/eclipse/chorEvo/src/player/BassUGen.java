package player;

import synthesizers.BigSynth;

import java.util.ArrayList;
import java.util.Random;

import composition.Composition;
import composition.RhythmPattern;
import jm.music.data.Note;
import jm.music.data.Phrase;
import jm.music.tools.Mod;
import net.beadsproject.beads.core.AudioContext;
import progression.ProgressionGraph;

public class BassUGen extends InstrumentUGen {
	
	public BassUGen(AudioContext audiocontext, Composition currentlyPlaying, int instrument, int channel) {
		super(audiocontext, currentlyPlaying, instrument, channel);
		synth = new BigSynth();
		float[] shaper = {1,1,0,1,0,1,0,0};
		synth.setParams(0.2, 5, 12000, 0.2, 0.3, 0.5, shaper);
		//gain = 97;
	}

	@Override
	public void updatePhrase() {
		phrase = makePhrase(irregularity, dissonance);
		
	}

	@Override
	public Phrase makeBasePhrase() {
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
		Phrase bassPhrase = new Phrase();
    	for (int i =0; i < chords.size(); i++) {
			Note[] n = new Note[3];
			Phrase tmp = new Phrase();
			for (int j = 0; j < n.length; j++) {
				if (j==0){
					if (composition.major(chords.get(i))){
						int pitch = composition.root(chords.get(i), key, 36);
						pitch = composition.adjustForDissonance(pitch, dissonance);
						int mod = ProgressionGraph.noteTranslationOnVariation(pitch, chordsVar.get(i), key, true);
						n[j] = new Note(pitch+mod, accRhythm[j]);
					}
					else{
						int pitch = composition.root(chords.get(i), key, 36);
						pitch = composition.adjustForDissonance(pitch, dissonance);
						int mod = ProgressionGraph.noteTranslationOnVariation(pitch, chordsVar.get(i), key, true);
						n[j] = new Note(pitch+mod, accRhythm[j]);
					}
				}
				else{
					if (composition.major(chords.get(i))){
						int pitch = composition.root(chords.get(i), key, 36);
						pitch = composition.adjustForDissonance(pitch, dissonance);
						int mod = ProgressionGraph.noteTranslationOnVariation(pitch, chordsVar.get(i), key, true);
						n[j] = new Note(pitch+mod, (1 - accRhythm[0])/2);
					}
					else{
						int pitch = composition.root(chords.get(i), key, 36);
						pitch = composition.adjustForDissonance(pitch, dissonance);
						int mod = ProgressionGraph.noteTranslationOnVariation(pitch, chordsVar.get(i), key, true);
						n[j] = new Note(pitch+mod, (1 - accRhythm[0])/2);
					}
				}
					
					tmp.addNote(n[j]);
					//bassPhrase.addNote(n[j]);
			}
			
			
			Mod.append(bassPhrase, tmp);
			
		}
    	return bassPhrase;
	}
	
}
