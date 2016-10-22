package utils;

import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import com.sun.tools.javac.tree.Tree.ForeachLoop;

public class MidiSynthesisTest {
	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException {
		Synthesizer synth = MidiSystem.getSynthesizer();
		MidiChannel[] channels = synth.getChannels();
		for (MidiChannel midiChannel : channels) {
			System.out.println(midiChannel.toString());
		}
		
		// Obtain information about all the installed synthesizers.
		Vector<Info> synthInfos = new Vector<>();
		MidiDevice device = null;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
		    try {
		        device = MidiSystem.getMidiDevice(infos[i]);
		    } catch (MidiUnavailableException e) {
		          // Handle or throw exception...
		    }
		    if (device instanceof Synthesizer) {
		        synthInfos.add(infos[i]);
		    }
		}
		// Now, display strings from synthInfos list in GUI.
		for (Info info : infos) {
			System.out.println(info.toString());
		}
		
		ShortMessage myMsg = new ShortMessage();
		  // Start playing the note Middle C (60), 
		  // moderately loud (velocity = 93).
		  myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
		  long timeStamp = -1;
		  Receiver       rcvr = MidiSystem.getReceiver();
		  rcvr.send(myMsg, timeStamp);
		
//		ShortMessage myMsg = new ShortMessage();
//        // Play the note Middle C (60) moderately loud
//        // (velocity = 93)on channel 4 (zero-based).
//        myMsg.setMessage(ShortMessage.NOTE_ON, 4, 60, 93); 
//        //Synthesizer synth = MidiSystem.getSynthesizer();
//        if (!synth.isOpen())
//        	synth.open();
//        Receiver synthRcvr = synth.getReceiver();
//        synthRcvr.send(myMsg, -1); // -1 means no time stamp
//        synthRcvr.close();
        
        //just in case the program doesn't synthesise the sound if it terminates
        while(true){
        	
        }
	}
}
