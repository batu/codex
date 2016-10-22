package control;

import java.util.ArrayList;

import composition.Composition;

public class CompositionCreatorThread extends Thread {
	
	ArrayList<Composition> buffer;
	int maxSize=5;
	
	public CompositionCreatorThread(ArrayList<Composition> buf) {
		buffer=buf;
	}
	
	public CompositionCreatorThread(ArrayList<Composition> buf, int max) {
		buffer=buf;
		maxSize = max;
	}

	public void run(){
		while (buffer.size() < maxSize){
			buffer.add(new Composition());
		}
	}
}
