package player;

import synthesizers.BigSynth;
import composition.Composition;
import jm.music.data.Note;
import jm.music.data.Phrase;
import net.beadsproject.beads.core.AudioContext;
import progression.ProgressionGraph;

public class MelodyUGen extends InstrumentUGen {
	
	public MelodyUGen(AudioContext audiocontext, Composition currentlyPlaying, int instrument, int channel) {
		super(audiocontext, currentlyPlaying, instrument, channel);
		synth = new BigSynth();
		float[] shaper2 = { 1, 1, 1, 0, 0, 0, 0, 0 };
		synth.setParams(0.2, 10, 12000, 0.2, 0.5, 0.3, shaper2);
		//gain = 120;
	}
	
	@Override
	public void updatePhrase() {
		phrase = makePhrase(0, dissonance);
	}

	@Override
	public Phrase makeBasePhrase() {
		composition.variateChords();
		System.out.println(composition.chordsVar);
		
		//variate melody to fit chords
		for (Note n : composition.mel.getNoteArray()) {
			//System.out.println(n.getPitch() + " " + n.getDuration());
		}
		int noteCounter = 0;
		
		Note[] notes = new Note[composition.mel.getNoteArray().length];
		Note[] originalMelodyArray = composition.mel.getNoteArray();
		for (int i = 0; i < originalMelodyArray.length; i++){
			notes[i] = originalMelodyArray[i].copy();
		}
		
		int[] amountOfNotes = composition.melody.amountOfNotes;
    	for (int i = 0; i < amountOfNotes.length; i++) {
    		//if chord have been variated check for bad notes, else keep them
			if (composition.chordsVar.get(i) != ""){
				for (int j = noteCounter; j < noteCounter+amountOfNotes[i]; j++) {	
					//check if current note is not a good note on the chord variation
					int pitch = notes[j].getPitch();
					pitch = composition.adjustForDissonance(pitch, dissonance);
					int mod = ProgressionGraph.noteTranslationOnVariation(pitch, composition.chordsVar.get(i), composition.key, composition.major(composition.chords.get(i)));
					notes[j].setPitch(pitch+mod);
				}		
			}
			noteCounter += amountOfNotes[i];
		}
		return new Phrase(notes);
	}
	
}
