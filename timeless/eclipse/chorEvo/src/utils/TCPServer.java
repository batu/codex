package utils;

import java.io.*;
import java.net.*;

import control.Command;
import control.Controller;

public class TCPServer extends Thread {

	final int SERVER_PORT = 7778;
	Controller CONTROLLER;
	
	ServerSocket welcomeSocket;
	
	public TCPServer(Controller c) {
		CONTROLLER = c;
		try {
			welcomeSocket = new ServerSocket(7778);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		String clientSentence = null;
		while (true) {
			Socket connectionSocket = null;
			try {
				connectionSocket = welcomeSocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader inFromClient = null;
			try {
				inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			try {
				clientSentence = inFromClient.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Received: " + clientSentence);
			//CONTROLLER.messageReceived(1, null);
			interpretMessage(clientSentence);
			//capitalizedSentence = clientSentence.toUpperCase() + '\n';
			//outToClient.writeBytes(capitalizedSentence);
		}
	}
	
	void interpretMessage(String message){
		//message structures: COMMAND,ARG1,ARG2
		String[] words = message.split(",");
		String[] values;
		switch (words[0]) {
		case "start": // start
			CONTROLLER.messageReceived(Command.START, null);
			break;
		case "stop": // stop
			CONTROLLER.messageReceived(Command.STOP, null);
			break;
		case "changeComposition": // changeComposition,scene
			values = new String[]{words[1]};
			CONTROLLER.messageReceived(Command.CHANGE, values);
			break;
		case "setCoord": // setCoord,12,90
			values = new String[]{words[1], words[2]};
			CONTROLLER.messageReceived(Command.NEWMOOD, values);
			break;

		default:
			break;
		}
	}

	public static void main(String argv[]) throws Exception {
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(7778);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			System.out.println("Received: " + clientSentence);
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			outToClient.writeBytes(capitalizedSentence);
		}
	}
}