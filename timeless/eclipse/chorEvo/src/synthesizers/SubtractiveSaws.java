package synthesizers;

import jm.audio.Audio;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.OnePoleFilter;
import net.beadsproject.beads.ugens.WavePlayer;
import net.beadsproject.beads.ugens.WaveShaper;

public class SubtractiveSaws extends UGen{
	
	WavePlayer w1;
	WavePlayer w2;
	AudioContext ac;
	float gain = 0.1f;
	
	public SubtractiveSaws(AudioContext ac, float frequency) {
		super(ac,2,1);
		this.ac = ac;
		w1 = new WavePlayer(ac, frequency, Buffer.SAW);
		w2 = new WavePlayer(ac, frequency+1, Buffer.SAW);
//		System.out.println(getIns());
		addInput(w1);
//		System.out.println(getIns());
		addInput(w2);
	}

	@Override
	public void calculateBuffer() {
		// TODO Auto-generated method stub
		for (int i = 0; i < bufferSize; i++) {
			bufOut[0][i] = (bufIn[0][i]+bufIn[1][i])*gain;
		}
	}

	public static void main(String[] args) {
		AudioContext ac;

		  ac = new AudioContext();
//		  SubtractiveSaws wp = new SubtractiveSaws(ac, 220);
//		  WavePlayer wp = new WavePlayer(ac, 220, Buffer.SAW);
//		  OnePoleFilter filter = new OnePoleFilter(ac, 660);
//		  filter.addInput(wp);
		  
		  Gain g = new Gain(ac, 3, 0.5f);
		  Vibrato v = new Vibrato(ac,220, 0.5f);
		  WavePlayer w = new WavePlayer(ac, v, Buffer.SINE);
		  WaveShaper s = new WaveShaper(ac, Buffer.TRIANGLE);
		  s.addInput(w);
		  s.setWetMix(0.3f);
		  Chebyshev c = new Chebyshev(ac, 3);
		  c.addInput(s);
		  Chebyshev c2 = new Chebyshev(ac, 5);
		  c2.addInput(s);
//		  v.addInput(c);
		  g.addInput(s);
		  g.addInput(c);
		  g.addInput(c2);
//		  g.addInput(wp);
		  ac.out.addInput(g);
		  ac.start();
		 
	}
}
