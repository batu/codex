package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;

public class TimbreXMLParse {
	private String role1 = null;
	private String role2 = null;
	private String role3 = null;
	private String role4 = null;
	private ArrayList<String> rolev;
	
	public static void main(String[] args) throws FileNotFoundException {
//		TimbreXMLParse parse = new TimbreXMLParse();
//		parse.readXML("feature_values_1.xml");
		
		Path file = FileSystems.getDefault().getPath("feature_values_1.csv");
		PrintWriter out = new PrintWriter("_instrumentsAnalysis.csv");
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<Double, ArrayList<Integer>> spectralCentroid = new HashMap<Double, ArrayList<Integer>>();
		Map<Integer, Double> spectralCentroidStdev = new HashMap<Integer, Double>();
		
		int i=0;
		for (String string : lines) {
			System.out.println(string);
			
			String[] split = string.split(",");
			
			int clip = Integer.valueOf(split[0]);
			double stdev = Double.valueOf(split[1]);
			double avg = Double.valueOf(split[2]);
			
			if (!spectralCentroid.containsKey(avg)){
				ArrayList<Integer> tmp = new ArrayList<>();
				tmp.add(clip);
				spectralCentroid.put(avg, tmp);
			}
			else
				spectralCentroid.get(avg).add(clip);
			spectralCentroidStdev.put(clip, stdev);
			
			System.out.println(spectralCentroid.get(avg));
		}
		
		SortedSet<Double> values = new TreeSet<Double>(spectralCentroid.keySet());
		System.out.println(values.toString());
		for (Double double1 : values) {
			System.out.print(spectralCentroid.get(double1) + " ");;
		}
		
		file = FileSystems.getDefault().getPath("midi_instruments.txt");
		HashMap<Integer, String> instrumentNames = new HashMap<>();
		lines = null;
		try {
			lines = Files.readAllLines(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String string : lines) {
			System.out.println(string);
			
			String[] split = string.split(",");
			
			int instr = Integer.valueOf(split[0]);
			String name = split[1];
			
			instrumentNames.put(instr, name);
		}
		
		for (Double double1 : values) {
			ArrayList<Integer> ids = spectralCentroid.get(double1);
			for (Integer integer : ids) {
				out.println(integer + "," + instrumentNames.get(integer+1) + "," + double1 + ","+ spectralCentroidStdev.get(integer));
				System.out.print(instrumentNames.get(integer+1) + " ");;
			}
			
		}
		out.close();
		
		System.out.println(spectralCentroid.size() + " " + values.size());
	}
	
}