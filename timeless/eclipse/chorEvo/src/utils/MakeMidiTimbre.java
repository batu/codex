package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import affective.AffectiveModule;
import composition.Composition;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jm.util.Write;
import player.Player;

public class MakeMidiTimbre {

public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		
		int[] instruments = {45, 0, 71, 40, 42, 56, 19, 59, 23, 65};
		
		int i=0;
//		for (String string : lines) {
		for (i=0; i<instruments.length; i++) { 
			
			Composition test = new Composition();
			System.out.println(test.chords.toString());
			Player player = new Player();
			AffectiveModule affective = new AffectiveModule(player);
			player.play(test);
			//affective.setDissonance(100);
			
			Score s = new Score();   
			
			Part melody = new Part("Melody",instruments[i],0);
			melody.add(player.melody.getPhrase());
			for (Note n : melody.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(melody);
			
			Part acc = new Part("Piano", instruments[i], 1);
			acc.add(player.accompaniment.getPhrase());
			for (Note n : acc.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(acc);
			
			Part bass = new Part("bass", 0, 2);
			bass.add(player.bass.getPhrase());
			for (Note n : bass.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(bass);

			
			//set tempo from player
			s.setTempo(player.tempo);
			
			//save midi
			Write.midi(s, "Timbre"+instruments[i]+".mid");	
		}
	}
}
