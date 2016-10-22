package utils;

import com.sun.tools.javac.tree.Tree.ForeachLoop;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Rest;
import jm.music.data.Score;
import jm.util.Write;

public class MakeTimbreAnalysisFiles {
	
	public static void main(String[] args) {
		//112 instruments
		for (int i = 0; i < 112; i++) {
			Rest r = new Rest(0.1);
			Note n = new Note(60, 1);
			Phrase ph = new Phrase();
			ph.add(r);
			ph.add(n);
			Part p = new Part(i);
			p.add(ph);
			Score s = new Score(p);
			Write.midi(s, i+".mid");
		}
	}
}
