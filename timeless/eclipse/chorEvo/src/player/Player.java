package player;

import composition.Composition;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Envelope;

public class Player {
	
	Clock clock;
	public static float frequency = 1000;
	public static Envelope intervalEnvelope;
	final AudioContext ac;
	Composition currentlyPlaying;
	float irregularity = 0;
	public float tempo = 60;
	
	public MelodyUGen melody;
	public AccompanimentUGen accompaniment;
	public BassUGen bass;
	DrumsUGen drums;
	
	public Mixer mixer;
	
	public Player() {

		ac = new AudioContext();
		
		intervalEnvelope = new Envelope(ac, frequency);
		clock = new Clock(ac,intervalEnvelope);
		
		melody = new MelodyUGen(ac, currentlyPlaying, 25,0);
		accompaniment = new AccompanimentUGen(ac, currentlyPlaying, 1, 1);
		bass = new BassUGen(ac, currentlyPlaying, 33, 2);
		drums = new DrumsUGen(ac, currentlyPlaying);
		
		//TODO mixer has to change, as we are using midi sounds now
		mixer = new Mixer(melody, accompaniment, bass, drums);
		
		clock.addMessageListener(melody);
		clock.addMessageListener(accompaniment);
		clock.addMessageListener(bass);
		//clock.addMessageListener(drums);
		
		ac.out.addDependent(clock);
		
	}
	
	public void start(){
		ac.reset();
		ac.start();
	}
	
	public void stop(){
		ac.stop();
	}
	
	public void play(Composition composition) {
		currentlyPlaying = composition;
		melody.setComposition(currentlyPlaying);
		accompaniment.setComposition(currentlyPlaying);
		bass.setComposition(currentlyPlaying);
		drums.setComposition(currentlyPlaying);
	}
	
	public void setBPM(int bpm){
		float bps = (float)bpm / 60f;
		float spb = 1 / bps;
		float millispb = spb * 1000;
		tempo = bpm;
		frequency = millispb;
		intervalEnvelope.addSegment(millispb, 1000);
		System.out.println(millispb);;
	}
	
	public void setVolumes(float melody, float accompaniment, float bass, float drums){
		mixer.setMixerTo(melody, accompaniment, bass, drums);
	}
	
	public void setGeneralVolume(float volume){
		mixer.setGeneralVolume(volume);
	}
	
	public void setIrregularity(float irregularity){
		this.irregularity = irregularity;
		currentlyPlaying.calculateNewRhythm(irregularity);
		
//		accompaniment.setIrregularity(irregularity);
//		bass.setIrregularity(irregularity);
//		drums.setIrregularity(irregularity);
	}
	
	public void setTimbre(float cutoffFreq, float resonance){
		melody.setTimbre(cutoffFreq, resonance);
		accompaniment.setTimbre(cutoffFreq, resonance);
		bass.setTimbre(cutoffFreq, resonance);
	}
	
	public static void main(String[] args) {
		Composition composition = new Composition();
		Player p = new Player();
		p.start();
		p.play(composition);
		p.setBPM(150);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		p.setVolumes(0.5f, 0.05f, 0.1f, 0.8f);
		//p.setIrregularity(0.4f);
	}

	public void setRhythmVolume(float rhythm) {
		mixer.setRhythmVolume(rhythm);
	}

	public void updatePhrases() {
		// TODO Auto-generated method stub
		melody.updatePhrase();
		accompaniment.updatePhrase();
		bass.updatePhrase();
		drums.updatePhrase();
	}

	public void setDissonance(float dissonance) {
		melody.setDissonance(dissonance);
		accompaniment.setDissonance(dissonance);
		bass.setDissonance(dissonance);
//		drums.setDissonance(dissonance);
	}


	
}
