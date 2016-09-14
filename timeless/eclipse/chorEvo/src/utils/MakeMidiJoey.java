package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.sun.tools.javac.resources.compiler;

import affective.AffectiveModule;
import composition.Composition;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jm.util.Write;
import player.Player;

public class MakeMidiJoey {
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		
		for (int i=0; i<150; i++) {
			
			Random rand = new Random();
			double arousal = rand.nextDouble();
			double valence = rand.nextDouble();
			double octave = rand.nextDouble();
			
			//calculate how many octaves to transpose
			int octaveMod = 0;
			if (octave < 1d/3d)
				octaveMod = -1;
			if (octave > 1d/3d*2d)
				octaveMod = 1;
			System.out.println("octaveMod="+octaveMod);
			
			Composition test = new Composition();
			System.out.println(test.chords.toString());
			Player player = new Player();
			AffectiveModule affective = new AffectiveModule(player);
			player.play(test);
			//affective.setDissonance(100);
			affective.setAffectiveCoord(valence, arousal);
			
			Score s = new Score(); 
			Score onlyMelody = new Score();
			
			Part melody = new Part("Melody",25,0);
			melody.add(player.melody.getPhrase());
			for (Note n : melody.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(melody);
			onlyMelody.add(melody);
			
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
			onlyMelody.setTempo(player.tempo);
			
			//save midi
			Write.midi(s, "Complete"+i+".mid");
			Write.midi(onlyMelody, "Melody"+i+".mid");
		}
	}
}
