package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

public class MakeMidiSonification {
	
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		
		int[][] instrumentSets = {
				{45, 8, 36},
				{28, 5, 60},
				{64, 25, 38},
				{40, 2, 37},
				{66, 44, 57},
				{1, 104, 19},
		};
		
		Path file = FileSystems.getDefault().getPath("_brightness_nrm_quant_maps.txt");
		PrintWriter out = new PrintWriter("_sonification_chords.txt");
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i=0;
//		for (String string : lines) {
		for (i=0; i<lines.size(); ) { //in case something goes wrong, to not start from scratch
			String string = lines.get(i);
			System.out.println(string);
			
			String[] split = string.split(";");
			
			double arousal = Double.valueOf(split[0]);
			double valence = Double.valueOf(split[1]);
			double octave = Double.valueOf(split[2]);
			
			//make tension graph array
			int[] tensionValues = new int[split.length-5];
			for (int j = 5; j < split.length; j++) {
				tensionValues[j-5] = Integer.valueOf(split[j]);
			}
			
			//calculate how many octaves to transpose
			int octaveMod = 0;
			if (octave < 1d/3d)
				octaveMod = -1;
//			if (octave > 1d/3d*2d)
//				octaveMod = 1;
			System.out.println("octaveMod="+octaveMod);
			
			//choose instrument set
			double interval = 1d/instrumentSets.length;
			double tmp = interval;
			int index = 0;
			double v = (valence + 1)/2d; //represent valence in 0-1
			if (v>1)
				v=1;
			while (v-0.01>=tmp){
//				System.out.println(v + " " + tmp + " " + index);
				index++;
				tmp+=interval;
			}
			int[] instruments = instrumentSets[index];
			System.out.println("instrument set "+index);
			
			Composition test = new Composition(tensionValues);
			System.out.println(test.chords.toString());
			Player player = new Player();
			AffectiveModule affective = new AffectiveModule(player);
			player.play(test);
			//affective.setDissonance(100);
			affective.setAffectiveCoord(valence, arousal);
			
			Score s = new Score();   
			
			Part melody = new Part("Melody",instruments[0],0);
			melody.add(player.melody.getPhrase());
			for (Note n : melody.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(melody);
			
			Part acc = new Part("Piano", instruments[1], 1);
			acc.add(player.accompaniment.getPhrase());
			for (Note n : acc.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(acc);
			
			Part bass = new Part("bass", instruments[2], 2);
			bass.add(player.bass.getPhrase());
			for (Note n : bass.getPhrase(0).getNoteArray()) {
				n.setRhythmValue(n.getRhythmValue()*4, true);
			}
			s.add(bass);
			
			//transpose piece to different octave
			Mod.transpose(s, 12*octaveMod);
			
			//set tempo from player
			s.setTempo(player.tempo);
			
			//save midi
			Write.midi(s, "Picture"+i+".mid");
			i++;
			
			//save tension graph and chord sequence
			out.println("Picture"+i);
			for (int j : tensionValues) {
				out.print(j+";");
			}
			out.println();
			out.println(test.chords.toString());
			out.println("valence="+ valence + "; arousal="+arousal);
			out.println();
		}
		out.close();
	}
}
