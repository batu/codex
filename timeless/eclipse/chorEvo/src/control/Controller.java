package control;

import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import com.sun.tools.javadoc.Messager.ExitJavadoc;

import affective.AffectiveModule;
import archive.ArchiveManager;
import composition.Composition;
import player.Player;
import utils.TCPServer;

public class Controller {
	
	Player player;
	AffectiveModule affective;
	ArrayList<Composition> compositionBuffer = new ArrayList<Composition>();
	
	public Controller() {
		player = new Player();
		affective = new AffectiveModule(player);
		(new CompositionCreatorThread(compositionBuffer)).start();
		(new TCPServer(this)).start();
	}
	
	public void messageReceived(Command message, String[] values){
		while (compositionBuffer.size()==0){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		switch (message) {
		case START:
			player.start();
			break;
		case STOP:
			player.stop();
			//System.exit(0);
			break;
		case CHANGE: //new composition
			String level = values[0];
			if (ArchiveManager.hasComposition(level)){
				Composition c = ArchiveManager.retrieve(level);
				player.play(c);
			}
			else{
				Composition c = compositionBuffer.remove(0);
				ArchiveManager.store(c, level);
				player.play(c);
			}
			break;
		case NEWTEMPO: // new tempo
			player.setBPM(Integer.valueOf(values[0]));
			break;
		case NEWMOOD: // new mood
			affective.setAffectiveCoord(Integer.valueOf(values[0]), Integer.valueOf(values[1]));
			break;

		default:
			break;
		}	
	}
	
	public void setIntensity(int arousal){
		affective.setIntensity(arousal);
	}
	
	public void setTimbre(int valence){
		affective.setTimbre(valence);
	}
	
	public void setTempo(int arousal){
		affective.setTempo(arousal);
	}
	
	public void setStrength(int arousal){
		affective.setStrength(arousal);
	}
	
	public void setIrregularity(int valence){
		affective.setIrregularity(valence);
	}
	
	public void setDissonance(int valence){
		affective.setDissonance(valence);
	}
	
	public static void main(String[] args) {
		Synthesizer synth = null;
		try {
			synth = MidiSystem.getSynthesizer();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (!synth.isOpen())
			try {
				synth.open();
			} catch (MidiUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
		Controller controller = new Controller();
		Scanner user_input = new Scanner( System.in );
		while (true){
			System.out.print("Enter command: ");
			String commandS = user_input.next();
			int command = Integer.valueOf(commandS);
			String[] values = new String[2];
			if (command == 2){
				System.out.print("Enter value: ");
				String valueS = user_input.next();
				values[0] = valueS;
			}
			if (command == 3){
				System.out.print("Enter valence value: ");
				String valueS = user_input.next();
				values[0] = valueS;
				System.out.print("Enter arousal value: ");
				valueS = user_input.next();
				values[1] = valueS;
			}
			Command commandCode = Command.fromInt(command);
			controller.messageReceived(commandCode,values);
		}
	}
}
