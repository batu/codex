package player;

import java.util.ArrayList;

public class Mixer {
	
	int instrumentNumber = 4;
	
	MelodyUGen melody;
	AccompanimentUGen accompaniment;
	BassUGen bass;
	DrumsUGen drums;
	
	public float masterMix = 1;
	public float melMix;
	public float accMix;
	public float bassMix;
	public float drumsMix;
	float rhythmMod = 0;
	
	Mixer(MelodyUGen mel, AccompanimentUGen acc, BassUGen bas, DrumsUGen drums){
		melody = mel;
		melMix = melody.getGain();
		accompaniment = acc;
		accMix = accompaniment.getGain();
		bass = bas;
		bassMix = bass.getGain();
		this.drums = drums;
		drumsMix = drums.getGain();
	}
	
	public void setMixerTo(float melGain, float accGain, float basGain, float drumGain){
		melMix = melGain;
		accMix = accGain;
		bassMix = basGain;
		drumsMix = drumGain;
		updateVolumes();
	}
	
	public void updateVolumes(){
		melody.setGain(melMix * masterMix);
		accompaniment.setGain(accMix * masterMix);
		bass.setGain(bassMix * masterMix + rhythmMod);
		drums.setGain(drumsMix * masterMix + rhythmMod);
	}

	public void setGeneralVolume(float volume) {
		masterMix = volume;
		updateVolumes();
	}

	public void setRhythmVolume(float rhythm) {
		rhythmMod = rhythm*20; //if you change this make sure to account for it in InstrumentUGen gain and baseGain
		updateVolumes();
	}
}
