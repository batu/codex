package synthesizers;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.WavePlayer;

public class Vibrato extends UGen{
	
	Envelope base;
	WavePlayer vib;
	float vibrato;

	public Vibrato(AudioContext arg0, int arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}
	
	public Vibrato(AudioContext ac, float freq, float vibratoAmount) {
		super(ac, 1, 1);
		base = new Envelope(ac, freq);
		vib = new WavePlayer(ac, 8, Buffer.SINE);
		addInput(vib);
		vibrato = vibratoAmount;
	}
	
	public Vibrato(AudioContext ac, Envelope freq, float vibratoAmount) {
		super(ac, 1, 1);
		base = freq;
		vib = new WavePlayer(ac, 8, Buffer.SINE);
		addInput(vib);
		vibrato = vibratoAmount;
	}

	@Override
	public void calculateBuffer() {
		for (int i = 0; i < bufferSize; i++) {
			bufOut[0][i] = (base.getCurrentValue()+(bufIn[0][i]*vibrato));
		}
	}

}
