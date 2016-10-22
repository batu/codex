package archive;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import jm.music.tools.ChordAnalysis;
import composition.Composition;

public class SQLiteJDBC
{
	
	public static Connection open(){
		Connection c = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      File file = new File ("archive.db");
	      
	      //initialize the db if first time accessed (create and add tables)
	      if (!file.exists()){
	    	  //create connection and new db if it doesn't exist
	    	  c = DriverManager.getConnection("jdbc:sqlite:archive.db");
	    	  Class.forName("org.sqlite.JDBC");
	          System.out.println("DB doesn't exist");
	          
	          Statement stmt = null;
	          stmt = c.createStatement();
	          String sql = "CREATE TABLE IF NOT EXISTS COMPOSITIONS " +
	                       "(ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
	                       " CHORDS           TEXT    NOT NULL, " + 
	                       " LEVEL            TEXT     NOT NULL )"; 
	          stmt.executeUpdate(sql);
	          stmt.close();
	      }
	      
	      else {
	    	//open connection
	    	  c = DriverManager.getConnection("jdbc:sqlite:archive.db");
	    	  Class.forName("org.sqlite.JDBC");
	          //System.out.println("Opened database successfully");
	      }
	    } catch ( Exception e ) {
	        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	        System.exit(0);
	      }
//	    System.out.println("Opened database successfully");
	      return c;
	}
	
	public static void close(Connection c){
		try {
			c.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	        System.exit(0);
		}
	}
	
	public static int add(Composition composition, String level){
		Connection c = null;
	    Statement stmt = null;
	    try {
	      c = open();

	      //insert data
	      stmt = c.createStatement();
	      String sql = "INSERT INTO COMPOSITIONS (CHORDS,LEVEL) " +
	                   "VALUES ( '"+composition.getChords()+"', '"+level+"' );"; 
	      stmt.executeUpdate(sql);
	      
	      //retrieve id of inserted data
	      ResultSet rs = stmt.executeQuery( "SELECT ID FROM COMPOSITIONS WHERE LEVEL='"+level+"' AND CHORDS='"+composition.getChords()+"';" );
	      int id = 0;
	      while ( rs.next() ) {
	         id = rs.getInt("id");
	      }
	      rs.close();

	      stmt.close();
	      //c.commit();
	      close(c);
	      
	      return id;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
//	    System.out.println("Records created successfully");
	    return 0;
	}
	
	public static void print(){
		Connection c = null;
	    Statement stmt = null;
	    try {
	      c = open();

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPOSITIONS;" );
	      while ( rs.next() ) {
	         int id = rs.getInt("id");
	         String  chords = rs.getString("chords");
	         String level  = rs.getString("level");
	         System.out.println( "ID = " + id );
	         System.out.println( "CHORDS = " + chords );
	         System.out.println( "LEVEL = " + level );
	         System.out.println();
	      }
	      rs.close();

	      stmt.close();
	      close(c);
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
//	    System.out.println("Records read successfully");
	}
	
	public static ArrayList<String> getChords(int id){
		Connection c = null;
	    Statement stmt = null;
	    try {
	      c = open();

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT CHORDS FROM COMPOSITIONS WHERE ID="+id+";" );
	      String  chords = null;
	      while ( rs.next() ) {
	         chords = rs.getString("chords");
	      }
	      rs.close();

	      stmt.close();
	      close(c);
	      
	      
	      //convert the chords list back to arraylist
	      String[] array = chords.split(" ");
	      ArrayList<String> list = new ArrayList<String>();
	      for (String s : array) {
	    	    list.add(s);
	    	}
	      return list;
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
//	    System.out.println("Records read successfully");
		return null;
	}
	
	public static int getComposition(String level){
		Connection c = null;
	    Statement stmt = null;
	    try {
	      c = open();

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT ID FROM COMPOSITIONS WHERE LEVEL='"+level+"';" );
	      int id = -1;
	      while ( rs.next() ) {
	         id = rs.getInt("id");
	      }
	      rs.close();

	      stmt.close();
	      close(c);
	      
	      return id;
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
//	    System.out.println("Records read successfully");
		return -1;
	}
	
  public static void main( String args[] )
  {
    Connection c = null;
    try {
      Composition comp = new Composition();
      System.out.println("adding");
      int id = add(comp, "level1");
      System.out.println("id="+id);
      System.out.println("printing");
      print();
      
//      ArrayList<String> chords = getChords(1);
//      System.out.println(chords);
      
//      int id = getComposition("level1");
//      System.out.println(id);
      
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    //System.out.println("Opened database successfully");
  }
}