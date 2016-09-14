package player;

import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import composition.Composition;
import jm.music.data.Note;
import jm.music.data.Phrase;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Gain;
import synthesizers.BigSynth;

public abstract class InstrumentUGen extends Bead{
	AudioContext ac;
	int pitch;
	Composition composition;
	int phraseIndex =0;
	long startTick=0;
	long duration = 0;
	float gain=87; //127-40 to account for max rhythm strenght variation
	int baseGain = 20; //to give rhythm strenght a bit of space to play
	float irregularity = 0;
	float dissonance = 0;
	Phrase phrase;
	
	int channel = 0;
	int instrument = 1;
	
	public BigSynth synth;
	
	public InstrumentUGen(AudioContext audiocontext, Composition currentlyPlaying, int instr, int chann) {
		ac = audiocontext;
		composition = currentlyPlaying;
		setIntrumentAndChannel(instr, chann);
	}
	
	public void messageReceived(Bead message) {
		if (composition!= null){
			Random random = new Random();
	        Clock c = (Clock)message;
	        if(c.getCount() >= startTick + duration) {
	        	//kill previous note
	        	try {
					sendMidiMessage(ShortMessage.NOTE_OFF, 0, 0);
				} catch (InvalidMidiDataException | MidiUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	Note note = phrase.getNote(phraseIndex);
	        	pitch = note.getPitch();
	        	//float freq = Pitch.mtof(pitch);
	        	//Gain g = new Gain(ac, 1, gain);
	        	//g.addInput(synth.getUgen(ac, freq, JMToBeads.toMillis(note.getRhythmValue()), (random.nextFloat()*0.3f)+0.7f, JMToBeads.toMillis(note.getOffset())));
	        	//ac.out.addInput(g);
	        	
	        	//midi start new note
	        	try {
					sendMidiMessage(ShortMessage.NOTE_ON, pitch, (int) gain + baseGain);
				} catch (InvalidMidiDataException | MidiUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	startTick = c.getCount();
	        	duration = JMToBeads.toTicks(note.getRhythmValue());
//	          	System.out.println(startTick + " " + duration);
		        if (phraseIndex == phrase.getNoteArray().length-1){
		        	//System.out.println("reset at "+c.getCount()+duration);
		        	phraseIndex = 0;
		        	updatePhrase();
		        }
		        else
		        	phraseIndex++;
	        }
		}
	}
	
	void sendMidiMessage(int message, int note, int velocity) throws InvalidMidiDataException, MidiUnavailableException{
    	ShortMessage myMsg = new ShortMessage();
        // Play the note Middle C (60) moderately loud
        // (velocity = 93)on channel 4 (zero-based).
        myMsg.setMessage(message, channel, note, velocity); 
//        Synthesizer synth = MidiSystem.getSynthesizer();
//        if (!synth.isOpen())
//        	synth.open();
        Receiver synthRcvr = MidiSystem.getReceiver();
        synthRcvr.send(myMsg, -1); // -1 means no time stamp
	}
	
	public void setGain(float gain){
		this.gain = gain;
	}
	
	public float getGain(){
		return gain;
	}
	
	public void setTimbre(float cutoffFreq, float resonance){
		synth.setLowPassFilter(cutoffFreq, resonance);
	}

	public void setComposition(Composition currentlyPlaying) {
		composition = currentlyPlaying;
		updatePhrase();
		phraseIndex = 0;
	}
	
	public Phrase makePhrase(float irregularity, float dissonance){
		Phrase phrase = makeBasePhrase();
		phrase = applyIrregularity(phrase, irregularity);
		phrase = applyDissonance(phrase, dissonance);
		return phrase;
	}
	
	Phrase applyDissonance(Phrase phrase, float dissonance){
		//not good, maybe create new phrase to play at the same time to introduce dissonances, but how?
		//otherwise force to alterations that can make dissonances while still be nice to listen to
//		Random rand = new Random();
//		for (int i = 0; i < phrase.size(); i++) {
//			if (rand.nextFloat() < dissonance/10f)
//				phrase.getNote(i).setPitch(phrase.getNote(i).getPitch()+1);;
//		}
		return phrase;
	}

	Phrase applyIrregularity(Phrase phrase, float irregularity){
		double delay = irregularity * 0.125;
		Random rand = new Random();
		for (int i = 0; i < phrase.size(); i++) {
			phrase.getNote(i).setOffset(rand.nextDouble()*delay);
		}
		return phrase;
	}

	abstract Phrase makeBasePhrase();

	public abstract void updatePhrase();
	
	public void setIrregularity(float irr){ 
		if (irr <0 || irr > 1)
			System.out.println("Irregularity value out of bounds: "+ irr);
		irregularity = irr;
	}
	
	public void setDissonance(float dissonance) {
		this.dissonance = dissonance;
		updatePhrase();
	}
	
	void setIntrumentAndChannel(int instrument, int channel){
		this.instrument = instrument;
		this.channel = channel;
		try {
			sendMidiMessage(ShortMessage.PROGRAM_CHANGE, instrument, 0);
			//MidiSystem.getSynthesizer().getChannels()[channel].programChange(instrument);
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void setInstrument(int instrument){
		setIntrumentAndChannel(instrument, channel);
	}
	
	public Phrase getPhrase() {
		return phrase;
	}
}
