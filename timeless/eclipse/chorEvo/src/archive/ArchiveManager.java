package archive;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import composition.Composition;

public class ArchiveManager {
	
	public static void store(Composition composition, String level){
		//add data in database and get unique name
		int id = SQLiteJDBC.add(composition, level);
		
		//serialize composition using unique name
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream("archive/"+String.valueOf(id)+".ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(composition);
	         out.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in archive/"+String.valueOf(id)+".ser");
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	public static boolean hasComposition(String level){
		//retrieve id of composition
		int id = SQLiteJDBC.getComposition(level);
		if (id<0)
			return false;
		else
			return true;
	}
	
	public static Composition retrieve(String level){
		Composition composition = null;
		
		//retrieve id of composition
		int id = SQLiteJDBC.getComposition(level);
		
		//unserialize and return
	      try
	      {
	         FileInputStream fileIn = new FileInputStream("archive/"+String.valueOf(id)+".ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         composition = (Composition) in.readObject();
	         in.close();
	         fileIn.close();
	         return composition;
	      }catch(IOException i)
	      {
	         i.printStackTrace();
	         return null;
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("Serialized class not found");
	         c.printStackTrace();
	         return null;
	      }
	}

	public static void main(String[] args) {
//		Composition c = new Composition();
//		store(c, "level2");
//		SQLiteJDBC.print();
		
		if (hasComposition("level2")){
			Composition c = retrieve("level2");
			System.out.println(c.getChords());
		}
		SQLiteJDBC.print();
		
	}
}
