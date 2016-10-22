package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CollateSonificationOutputs {
	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter out = new PrintWriter("output.txt");
		out.println("inSize, fSize, paretoSize, maxBeat, avgBeat, maxChord, avgChord, maxStep, avgStep");
		for (int i = 0; i < 26; i++) {
			Path file = FileSystems.getDefault().getPath("evo_"+i+"_output.txt");
			List<String> lines = null;
			try {
				lines = Files.readAllLines(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			out.println(lines.get(lines.size()-1));
		}
		out.close();
	}
}
