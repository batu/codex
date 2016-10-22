package utils;

import java.io.FileNotFoundException;
import java.util.Random;

import affective.AffectiveModule;
import composition.Composition;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Write;
import player.Player;

public class MakeMidiIrregular {
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		
		for (int i=0; i<1; i++) {
			
			
			Composition test = new Composition();
			System.out.println(test.chords.toString());
			Player player = new Player();
			AffectiveModule affective = new AffectiveModule(player);
			player.play(test);
			//make midi with no irregularity
			affective.setIrregularity(100);
			
			Score s = new Score(); 
			
			Part melody = new Part("Melody",25,0);
			melody.add(player.melody.getPhrase());
			for (Note n : melody.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(melody);
			
			Part acc = new Part("Piano", 1, 1);
			acc.add(player.accompaniment.getPhrase());
			for (Note n : acc.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(acc);
			
			Part bass = new Part("bass", 44, 2);
			bass.add(player.bass.getPhrase());
			for (Note n : bass.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(bass);
			
			//transpose piece to different octave
			//Mod.transpose(s, 12*octaveMod);
			//Mod.transpose(onlyMelody, 12*octaveMod);
			
			//set tempo from player
			s.setTempo(player.tempo);
			
			//save midi
			Write.midi(s, "Regular"+i+".mid");
			
			//make midi with max irregularity
			affective.setIrregularity(0);
			
			s = new Score(); 
			
			melody = new Part("Melody",25,0);
			melody.add(player.melody.getPhrase());
			for (Note n : melody.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(melody);
			
			acc = new Part("Piano", 1, 1);
			acc.add(player.accompaniment.getPhrase());
			for (Note n : acc.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(acc);
			
			bass = new Part("bass", 44, 2);
			bass.add(player.bass.getPhrase());
			for (Note n : bass.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(bass);
			
			//transpose piece to different octave
			//Mod.transpose(s, 12*octaveMod);
			//Mod.transpose(onlyMelody, 12*octaveMod);
			
			//set tempo from player
			s.setTempo(player.tempo);
			
			//save midi
			Write.midi(s, "Irregular"+i+".mid");
		}
	}
}
