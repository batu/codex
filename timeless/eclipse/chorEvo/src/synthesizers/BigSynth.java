package synthesizers;

import com.sun.swing.internal.plaf.synth.resources.synth;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.events.KillTrigger;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.LPRezFilter;
import net.beadsproject.beads.ugens.Noise;
import net.beadsproject.beads.ugens.Panner;
import net.beadsproject.beads.ugens.RangeLimiter;
import net.beadsproject.beads.ugens.WavePlayer;
import net.beadsproject.beads.ugens.WaveShaper;
import player.JMToBeads;

public class BigSynth {

	// variables
	AudioContext ac;
	public float vibrato = 0.2f;
	Buffer baseShape = Buffer.SAW;
	// Buffer shaper = Buffer.SINE;
	public float[] shaper = { 0, 0, 1, 1, 1, 1, 0, 0 };
	public float wetMix = 0.75f;
	public float mixCheby3 = 0.25f;
	public float mixCheby4 = 0.25f;
	public float mixOriginal = 0.5f;
	public float attack = 10;
	public float decay = 12000;
	float cutoffFreq = 440;
	float resonance = 0.9f;

	public BigSynth() {

	}

	public BigSynth(float vibrato, float attack, float decay, float mixCheby3, float mixCheby4, float mixOriginal,
			float[] shaper) {
		this.vibrato = vibrato;
		this.attack = attack;
		this.decay = decay;
		this.mixCheby3 = mixCheby3;
		this.mixCheby4 = mixCheby4;
		this.mixOriginal = mixOriginal;
		this.shaper = shaper;
	}

	public UGen getUgen(AudioContext ac, float freq, float duration, float velocity, float offset) {
		// AD envelope
		Envelope attackDecayCheby = new Envelope(ac, 0);
		Gain adGainCheby = new Gain(ac, 1, attackDecayCheby);
		attackDecayCheby.addSegment(1, attack * velocity);
		attackDecayCheby.addSegment(0, duration - (attack * velocity));
		// vibrato freq
		Vibrato v = new Vibrato(ac, freq, vibrato);
		// basic synth
		WavePlayer w = new WavePlayer(ac, v, baseShape);
		// vibrato wave -> shaper
		WaveShaper s = new WaveShaper(ac, shaper);
		s.addInput(w);
		s.setWetMix(wetMix);
		
		//Low Pass Filter
		LPRezFilter lp = new LPRezFilter(ac, cutoffFreq, resonance);
//		LPRezFilter lp = new LPRezFilter(ac, 440, 0f);
		lp.addInput(s);

		// add attack/delay to shaper
		adGainCheby.addInput(lp);

		// shaper+AD -> 3rd degree Chebyshev
		Chebyshev c1 = new Chebyshev(ac, 3);
		c1.addInput(adGainCheby);
		// shaper+AD -> 4th degree Chebyshev
		Chebyshev c2 = new Chebyshev(ac, 5);
		c2.addInput(adGainCheby);

		Gain gs = new Gain(ac, 1, mixOriginal);
		Gain gc1 = new Gain(ac, 1, mixCheby3);
		Gain gc2 = new Gain(ac, 1, mixCheby4);
		gs.addInput(lp);
		gc1.addInput(c1);
		gc2.addInput(c2);

		Gain g = new Gain(ac, 3, 1f);
		g.addInput(gs);
		g.addInput(gc1);
		g.addInput(gc2);
		// adGain.addInput(s);
		// adGain.addInput(c1);
		// adGain.addInput(c2);

		RangeLimiter rl = new RangeLimiter(ac, 1);
		rl.addInput(g);

		// another AD envelope
		Envelope attackDecay = new Envelope(ac, 0);
		Gain adGain = new Gain(ac, 1, attackDecay);
		attackDecay.addSegment(0, offset);
		attackDecay.addSegment(0.1f, (attack * velocity));
		if (duration - (attack * velocity) < (decay * velocity))
			attackDecay.addSegment(0f, duration - (attack * velocity), new KillTrigger(adGain));
		else{
			attackDecay.addSegment(0.1f, duration - (attack * velocity)- (decay * velocity));
			attackDecay.addSegment(0f, (decay * velocity), new KillTrigger(adGain));
		}
		// mix -> ad env
		adGain.addInput(rl);

		return adGain;
	}

	public void setParams(float vibrato, float attack, float decay, float mixCheby3, float mixCheby4, float mixOriginal,
			float[] shaper) {
		this.vibrato = vibrato;
		this.attack = attack;
		this.decay = decay;
		this.mixCheby3 = mixCheby3;
		this.mixCheby4 = mixCheby4;
		this.mixOriginal = mixOriginal;
		this.shaper = shaper;
	}
	
	public void setParams(double d, double attack2, double decay2, double mixOrig, double mixCheby3, double mixCheby4, float[] shaper2) {
		setParams((float) d, (float) attack2, (float) decay2, (float) mixOrig, (float) mixCheby3, (float) mixCheby4,  shaper2);
	}

	public void setAttack(float attack) {
		this.attack = attack;
	}

	public void setBaseShape(Buffer baseShape) {
		this.baseShape = baseShape;
	}

	public void setDecay(float decay) {
		this.decay = decay;
	}

	public void setMixCheby3(float mixCheby3) {
		this.mixCheby3 = mixCheby3;
	}

	public void setMixCheby4(float mixCheby4) {
		this.mixCheby4 = mixCheby4;
	}

	public void setMixOriginal(float mixOriginal) {
		this.mixOriginal = mixOriginal;
	}

	public void setShaper(float[] shaper) {
		this.shaper = shaper;
	}

	public void setVibrato(float vibrato) {
		this.vibrato = vibrato;
	}

	public void setWetMix(float wetMix) {
		this.wetMix = wetMix;
	}
	
	public void setLowPassFilter(float cutoffFreq, float resonance){
		this.cutoffFreq = cutoffFreq;
		this.resonance = resonance;
	}

	public static void main(String[] args) {
		
		AudioContext ac;
		ac = new AudioContext();
		BigSynth synth = new BigSynth();
		Clock clock = new Clock(ac, 700);
		clock.addMessageListener(
		// this is the on-the-fly bead
		new Bead() {
			// this is the method that we override to make the Bead do
			// something
			int pitch;
			int cycle = 0; 

			public void messageReceived(Bead message) {
				Clock c = (Clock) message;
				if (c.isBeat()) {
					// choose some nice frequencies
					if (random(1) < 0.5)
						return;
					pitch = Pitch.forceToScale((int) random(12), Pitch.dorian);
					float freq = Pitch.mtof(pitch + (int) random(4) * 12 + 32);
					Gain g = new Gain(ac, 1, 3);
					g.addInput(synth.getUgen(ac, freq, random(6500) + 500, 1,0));
					ac.out.addInput(g);
				}
				if (c.isBeat(20) && c.getBeatCount()!=0){
					switch(cycle){
					case 0:
						System.out.println("contrabass-like");
						float[] shaper = {1,1,0,1,0,1,0,0};
						synth.setParams(0.2, 5, 12000, 0.2, 0.4, 0.6, shaper);
						break;
					case 1:
						System.out.println("string thingie");
						float[] shaper1 = {1,1,1,0,0,0,0,0};
						synth.setParams(0.7, 1000, 5000, 0.3, 0.1, 0.1, shaper1);
						break;
					case 2:
						System.out.println("saxy synth");
						float[] shaper2 = {1,1,0,1,1,1,0,1};
						synth.setParams(0.4, 100, 300, 0.4, 0.6, 0, shaper2);
						break;
					case 3:
						System.out.println("spaceship");
						float[] shaper3 = {0,0,0,0,0.5f,1,1,1};
						synth.setParams(0.4, 20, 500, 0, 0.6, 0.5, shaper3);
						break;
					}
					cycle = (cycle+1)%4;
				}
			}
		});
		ac.out.addDependent(clock);
		ac.start();

	}

	public static float random(double x) {
		return (float) (Math.random() * x);
	}
}
