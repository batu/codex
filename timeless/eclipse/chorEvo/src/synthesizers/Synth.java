package synthesizers;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.events.KillTrigger;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.LPRezFilter;
import net.beadsproject.beads.ugens.OnePoleFilter;
import net.beadsproject.beads.ugens.Static;
import net.beadsproject.beads.ugens.WavePlayer;

public class Synth extends UGen{
	public WavePlayer carrier = null;
	public WavePlayer modulator = null;
	public Envelope e = null;
	public Gain g = null;
	public int pitch = -1;
	
	// our filter and filter envelope
	LPRezFilter lowPassFilter;
	WavePlayer filterLFO;
	
	public Synth(AudioContext ac, int midiPitch)
	{
		super(ac,1,1);
		// get the midi pitch and create a couple holders for the
		// midi pitch
		pitch = midiPitch;
		float fundamentalFrequency = (float) (440.0 * Math.pow(2, ((float)midiPitch - 59.0f)/12.0f));
		Static ff = new Static(ac, fundamentalFrequency);
		// instantiate the modulator WavePlayer
		modulator = new WavePlayer(ac, 0.5f * fundamentalFrequency, Buffer.SINE);
		// create our frequency modulation function
		Function frequencyModulation = 
		new Function(modulator, ff) {
			public float calculate() {
				// the x[1] here is the value of a sine wave 
				// oscillating at the fundamental frequency
				return (x[0] * 1000) + x[1];
			}
		};
		// instantiate the carrier WavePlayer
		// set up the carrier to be controlled by the frequency 
		// of the modulator
		carrier = new WavePlayer(ac, frequencyModulation, Buffer.SINE);
		// set up the filter and LFO
		filterLFO = new WavePlayer(ac, 8, Buffer.SINE);
		Function filterCutoff = new Function(filterLFO)
		{
		public float calculate() {
			// set the filter cutoff to oscillate between 1500Hz 
			// and 2500Hz
			return ((x[0] * 500) + 2000);
		}
		};
		lowPassFilter = new LPRezFilter(ac, filterCutoff, 0.96f);
		lowPassFilter.addInput(carrier);
		// set up and connect the gains
		e = new Envelope(ac, 1f);
		g = new Gain(ac, 1);
		g.addInput(lowPassFilter);
		//MasterGain.addInput(g);
		// add a segment to our gain envelope
//		e.addSegment(0.5f, 300);
		
		addInput(g);
	}
	
	public void kill()
	{
		e.addSegment(0.0f, 300, new KillTrigger(g));
	}
	
	public void destroy()
	{
		carrier.kill();
		modulator.kill();
		lowPassFilter.kill();
		filterLFO.kill();
		e.kill(); g.kill();
		carrier = null;
		modulator = null;
		lowPassFilter = null;
		filterLFO = null;
		e = null; g = null;
	}
	
	@Override
	public void calculateBuffer() {
		// TODO Auto-generated method stub
		bufOut = bufIn;
	}
	
	public static void main(String[] args) {
		AudioContext ac;

		  ac = new AudioContext();
		  Synth wp = new Synth(ac, 61);
//		  WavePlayer wp = new WavePlayer(ac, 220, Buffer.SAW);
//		  OnePoleFilter filter = new OnePoleFilter(ac, 660);
//		  filter.addInput(wp);
		  Gain g = new Gain(ac, 1, 1f);
		  g.addInput(wp);
		  ac.out.addInput(g);
		  ac.start();
	}
}
