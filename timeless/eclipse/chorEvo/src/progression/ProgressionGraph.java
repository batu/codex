package progression;

import java.io.IOException;
import java.util.*;

public class ProgressionGraph {

	public ProgressionGraph() {
		
		//vertexes
		add("I");
		add("I/5");
        add("ii_1");
        add("ii_2");
        add("iii");
        add("IV");
        add("V_1");
        add("V_2");
        add("vi");

        //edges
        add("I/5", "V_2");
        add("ii_1", "V_1");
        add("ii_1", "iii");
        add("V_1", "iii");
        add("V_1", "vi");
        add("iii", "IV");
        add("iii", "vi");
        add("iii", "I");
        add("vi", "IV");
        add("vi", "ii_2");
        add("IV", "V_2");
        add("IV", "I");
        add("IV", "ii_2");
        add("IV", "I/5");
        add("ii_2", "V_2");
        add("ii_2", "I");
        add("ii_2", "I/5");
        add("V_2", "I");
        
        for (String key : neighbors.keySet()) {
        	if (key != "I/5")
        		add("I", key);
		}
        
        //variations
        addVar("I", "2");
        addVar("I", "6");
        addVar("I", "M7");
        addVar("I", "M9");
        //addVar("I", "sus");
        addVar("ii", "m7");
        addVar("ii", "m9");
        addVar("iii", "m7");
        addVar("IV", "6");
        addVar("IV", "M7");
        addVar("IV", "m");
        addVar("IV", "m6");
        addVar("V", "7");
        addVar("V", "9");
        addVar("V", "11");
        addVar("V", "13");
        addVar("V", "sus");
        addVar("vi", "m7");
        addVar("vi", "m9");
	}
	
    public static class Edge<String>{
        private String vertex;
        private int cost;

        public Edge(String String, int c){
            vertex = String; cost = c;
        }

        public String getVertex() {
            return vertex;
        }

        public int getCost() {
            return cost;
        }

    }

    /**
     * A Map is used to map each vertex to its list of adjacent vertices.
     */

    private Map<String, List<Edge<String>>> neighbors = new HashMap<String, List<Edge<String>>>();
    private static HashMap<String, List<String>> variations = new HashMap<String, List<String>>();

    private int nr_edges;

    /**
     * String representation of graph.
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (String String : neighbors.keySet())
            s.append("\n    " + String + " -> " + neighbors.get(String));
        return s.toString();
    }

    /**
     * Add a vertex to the graph. Nothing happens if vertex is already in graph.
     */
    public void add(String vertex) {
        if (neighbors.containsKey(vertex))
            return;
        neighbors.put(vertex, new ArrayList<Edge<String>>());
    }

    public int getNumberOfEdges(){
        int sum = 0;
        for(List<Edge<String>> outBounds : neighbors.values()){
            sum += outBounds.size();
        }
        return sum;
    }

    /**
     * True iff graph contains vertex.
     */
    public boolean contains(String vertex) {
        return neighbors.containsKey(vertex);
    }

    /**
     * Add an edge to the graph; if either vertex does not exist, it's added.
     * This implementation allows the creation of multi-edges and self-loops.
     */
    public void add(String from, String to, int cost) {
        this.add(from);
        this.add(to);
        neighbors.get(from).add(new Edge<String>(to, cost));
    }
    public void add(String from, String to) {
        this.add(from);
        this.add(to);
        neighbors.get(from).add(new Edge<String>(to, 0));
    }
    
    public void addVar(String chord) {
        if (variations.containsKey(chord))
            return;
        variations.put(chord, new ArrayList<String>());
    }
    
    public void addVar(String chord, String var) {
        this.addVar(chord);
        variations.get(chord).add(var);
    }

    public int outDegree(java.lang.String string) {
        return neighbors.get(string).size();
    }

    public int inDegree(String vertex) {
       return inboundNeighbors(vertex).size();
    }

    public List<String> outboundNeighbors(String vertex) {
        List<String> list = new ArrayList<String>();
        for(Edge<String> e: neighbors.get(vertex))
            list.add(e.vertex);
        return list;
    }

    public List<String> inboundNeighbors(String inboundVertex) {
        List<String> inList = new ArrayList<String>();
        for (String to : neighbors.keySet()) {
            for (Edge e : neighbors.get(to))
                if (e.vertex.equals(inboundVertex))
                    inList.add(to);
        }
        return inList;
    }
    
    public boolean isNeighbor(String prevprev, String prev, String current){
    	List<String> neigh = outboundNeighbors(prev);
    	List<String> neighprev = outboundNeighbors(prevprev);
    	for (String string : neigh) {
    		String name = string.split("_")[0];
			if ( name.equals(current) ){
				for (String s : neighprev) {
					if (s.equals(prev))
						return true;
				}
			}
		}
    	return false;
    }
    
    public boolean isNeighbor(String prev, String current){
    	List<String> neigh = outboundNeighbors(prev);
    	for (String string : neigh) {
    		String name = string.split("_")[0];
			if ( name.equals(current) ){
				return true;
			}
		}
    	return false;
    }
    
    
    public String which(String name, String prev){
    	List<String> neigh = outboundNeighbors(prev);
    	for (String string : neigh) {
			if (string.split("_")[0] == name)
				return string;
		}
    	return null;
    }

    public boolean isEdge(String from, String to) {
      for(Edge<String> e :  neighbors.get(from)){
          if(e.vertex.equals(to))
              return true;
      }
      return false;
    }

    public int getCost(String from, String to) {
        for(Edge<String> e :  neighbors.get(from)){
            if(e.vertex.equals(to))
                return e.cost;
        }
        return -1;
    }
    
    public ArrayList<String> getSequenceClean(int lenght, String first){
    	Random rand = new Random();
        String[] vertices = new String[neighbors.keySet().size()];
        int i =0;
        for (String string : neighbors.keySet()) {
			vertices[i++] = string;
		}

        String start = first;
        String current = start;
        ArrayList<String> sequence = new ArrayList<>();
        int counter = 0;
        while (counter++ < lenght){
        	sequence.add(current);
        	List<String> next = outboundNeighbors(current);
        	current = next.get(rand.nextInt(next.size()));
        }
        //sequence.add(current);
        
        sequence = cleanSequence(sequence);
        
        return sequence;
    }
    
    public ArrayList<String> getSequence(int lenght, String first){
    	Random rand = new Random();
        String[] vertices = new String[neighbors.keySet().size()];
        int i =0;
        for (String string : neighbors.keySet()) {
			vertices[i++] = string;
		}

        String start = first;
        String current = start;
        ArrayList<String> sequence = new ArrayList<>();
        int counter = 0;
        while (counter < lenght){
        	sequence.add(current);
        	List<String> next = outboundNeighbors(current);
        	current = next.get(rand.nextInt(next.size()));
        	counter++;
        }
        //sequence.add(current);
        
        return sequence;
    }
    
    public ArrayList<String> getSequenceWithTension(int[] tensions, String first){
    	int lenght = tensions.length;
    	Random rand = new Random();
        String[] vertices = new String[neighbors.keySet().size()];
        int i =0;
        for (String string : neighbors.keySet()) {
			vertices[i++] = string;
		}

        String start = first;
        String current = start;
        ArrayList<String> sequence = new ArrayList<>();
        int counter = 0;
        int currentTension = tensions[counter];
        while (counter < lenght){
        	sequence.add(current);
        	List<String> next = outboundNeighbors(current);
        	if (counter+1 < lenght){
        		int nextTension = tensions[counter+1];
        		for (String string : next) {
        			if (nextTension > currentTension && isMoreTense(current, string)){
        				current = string;
        				break;
        			}
        			else {
        				if (isLessTense(current, string)){
        					current = string;
        					break;
        				}
        			}
				}
        	}
        	else{
	        	current = next.get(rand.nextInt(next.size()));
        	}
        	counter++;
        }
        //sequence.add(current);
        
        sequence = cleanSequence(sequence);
        
        return sequence;
    }
    
    public ArrayList<String> getSequence(int lenght, String first, String last){
    	ArrayList<String> sequence;
    	do {
    		sequence = getSequence(lenght, first);
    	} while ( !isEdge(sequence.get(sequence.size()-1), last) || sequence.get(sequence.size()-1)==last);
    	sequence = cleanSequence(sequence);
    	
    	sequence = expand(sequence);
    	return sequence;
    }
    
    boolean isLessTense(String chord1, String chord2){
    	String c1 = chord1.split("_")[0];
    	String c2 = chord2.split("_")[0];
    	int t1 = 0;
    	int t2 = 0;
    	switch(c1){
		case "I":
			t1 = 0;
			break;
		case "ii":
			t1 = 2;
			break;
		case "iii":
			t1 = 4;
			break;
		case "IV":
			t1 = 3;
			break;
		case "V":
			t1 = 5;
			break;
		case "vi":
			t1 = 1;
			break;
		}
    	switch(c2){
		case "I":
			t2 = 0;
			break;
		case "ii":
			t2 = 2;
			break;
		case "iii":
			t2 = 4;
			break;
		case "IV":
			t2 = 3;
			break;
		case "V":
			t2 = 5;
			break;
		case "vi":
			t2 = 1;
			break;
		}
    	if (t1 <= t2)
    		return true;
    	else return false;
    }
    
    boolean isMoreTense(String chord1, String chord2){
    	String c1 = chord1.split("_")[0];
    	String c2 = chord2.split("_")[0];
    	int t1 = 0;
    	int t2 = 0;
    	switch(c1){
		case "I":
			t1 = 0;
			break;
		case "ii":
			t1 = 2;
			break;
		case "iii":
			t1 = 4;
			break;
		case "IV":
			t1 = 3;
			break;
		case "V":
			t1 = 5;
			break;
		case "vi":
			t1 = 1;
			break;
		}
    	switch(c2){
		case "I":
			t2 = 0;
			break;
		case "ii":
			t2 = 2;
			break;
		case "iii":
			t2 = 4;
			break;
		case "IV":
			t2 = 3;
			break;
		case "V":
			t2 = 5;
			break;
		case "vi":
			t2 = 1;
			break;
		}
    	if (t1 > t2)
    		return true;
    	else return false;
    }
    
  //expand an element with chance 1/n
    private ArrayList<java.lang.String> expand(ArrayList<java.lang.String> sequence) {
    	return expand(sequence, 1f/sequence.size());
	}
    
    //expand an element with given chance
    private ArrayList<java.lang.String> expand(ArrayList<java.lang.String> sequence, float chance) {
    	Random rand = new Random();
    	//find element to expand
    	int indexToExpand = -1;
    	for (int i = 0; i<sequence.size(); i++) {
			if (rand.nextFloat() < chance){
				indexToExpand = i;
			}
			if (indexToExpand != -1)
				break;
		}
    	//if didn't select anything return
    	if (indexToExpand == -1){
    		return sequence;
    	}
    	else{
	    	//expand the element
	    	String s = sequence.get(indexToExpand);
	    	sequence.add(indexToExpand, s);
	    	//call recursively with previous chance divided by 2
	    	sequence = expand(sequence, chance/2);
			return sequence;
    	}
	}

	public ArrayList<String> cleanSequence(ArrayList<String> sequence){
    	//clean sequence of multiple chords (e.g ii_2 -> ii)
        for (int i1 = 0; i1 < sequence.size(); i1++) {
			sequence.set(i1, sequence.get(i1).split("_")[0]);
		}
        return sequence;
    }
    
    public ArrayList<String> getSequence(int lenght){
    	Random rand = new Random();
    	String[] vertices = new String[neighbors.keySet().size()];
        int i =0;
        for (String string : neighbors.keySet()) {
			vertices[i++] = string;
		}
        return getSequence(lenght, vertices[rand.nextInt(vertices.length)]); 
    }
    
    public ArrayList<Integer> getSequence(int lenght, String first, int key){
    	ArrayList<String> degrees = getSequence(lenght, first);
    	for (int i = 0; i < degrees.size(); i++) {
			degrees.set(i, degrees.get(i).split("_")[0]);
		}
    	
    	ArrayList<Integer> midi = new ArrayList<>();
    	for (String degree : degrees) {
			switch(degree){
			case "I":
				midi.add(0 + key);
				break;
			case "ii":
				midi.add(2 + key);
				break;
			case "iii":
				midi.add(4 + key);
				break;
			case "IV":
				midi.add(5 + key);
				break;
			case "V":
				midi.add(7 + key);
				break;
			case "vi":
				midi.add(9 + key);
				break;
			}
		}
    	return midi;
    }
    
    //sequence in ints, missing major/minor info
    public ArrayList<Integer> getSequenceInt(int lenght, String first, String last){
    	ArrayList<String> degrees = getSequence(lenght, first, last);
    	for (int i = 0; i < degrees.size(); i++) {
			degrees.set(i, degrees.get(i).split("_")[0]);
		}
    	
    	ArrayList<Integer> midi = new ArrayList<>();
    	for (String degree : degrees) {
			switch(degree){
			case "I":
				midi.add(0);
				break;
			case "ii":
				midi.add(1);
				break;
			case "iii":
				midi.add(2);
				break;
			case "IV":
				midi.add(3);
				break;
			case "V":
				midi.add(4);
				break;
			case "vi":
				midi.add(5);
				break;
			}
		}
    	return midi;
    }
    
    public static ArrayList<Integer> convertSequenceInt(ArrayList<String> seq){
    	
    	ArrayList<Integer> midi = new ArrayList<>();
    	for (String degree : seq) {
			switch(degree){
			case "I":
				midi.add(0);
				break;
			case "I/5":
				midi.add(0);
				break;
			case "ii":
				midi.add(1);
				break;
			case "iii":
				midi.add(2);
				break;
			case "IV":
				midi.add(3);
				break;
			case "V":
				midi.add(4);
				break;
			case "vi":
				midi.add(5);
				break;
			}
		}
    	return midi;
    }
    
public static ArrayList<String> convertStringToSequence(String string){
    	
		char[] stringArray = string.toCharArray();
		
    	ArrayList<String> chords = new ArrayList<>();
    	for (char c : stringArray) {
			switch(c){
			case '0':
				chords.add("I");
				break;
			case '1':
				chords.add("ii");
				break;
			case '2':
				chords.add("iii");
				break;
			case '3':
				chords.add("IV");
				break;
			case '4':
				chords.add("V");
				break;
			case '5':
				chords.add("vi");
				break;
			default:
				break;
			}
		}
    	return chords;
    }

public static ArrayList<String> convertIntArrayToSequence(int[] array){
	
	ArrayList<String> chords = new ArrayList<>();
	for (int i : array) {
		switch(i){
		case 0:
			chords.add("I");
			break;
		case 1:
			chords.add("ii");
			break;
		case 2:
			chords.add("iii");
			break;
		case 3:
			chords.add("IV");
			break;
		case 4:
			chords.add("V");
			break;
		case 5:
			chords.add("vi");
			break;
		default:
			break;
		}
	}
	return chords;
}
    
    static public ArrayList<String> variate(ArrayList<String> sequence){
    	ArrayList<String> variated = new ArrayList<String>();
    	Random rand = new Random();
    	
    	for (String chord : sequence) {
			if (rand.nextFloat() > 0.7 && variations.containsKey(chord)){
				List<String> vars = variations.get(chord);
				variated.add(  vars.get(rand.nextInt(vars.size())) );
			}
			else
				variated.add("");
		}
    	return variated;
    }
    
    public static int rootNote(String chord){
    	switch(chord){
		case "I":
			return 0;
		case "I/5":
			return 0;
		case "ii":
			return 2;
		case "iii":
			return 4;
		case "IV":
			return 5;
		case "V":
			return 7;
		case "vi":
			return 9;
		}
    	return -1;
    }

    public static void main(String[] args) throws IOException {

        ProgressionGraph graph = new ProgressionGraph();

        


        System.out.println("The nr. of vertices is: " + graph.neighbors.keySet().size());
        System.out.println("The nr. of edges is: " + graph.getNumberOfEdges()); // to be fixed
        System.out.println("The current graph: " + graph);
        System.out.println("In-degrees for IV: " + graph.inDegree("IV"));
        System.out.println("Out-degrees for IV: " + graph.outDegree("IV"));
        System.out.println("In-degrees for ii_2: " + graph.inDegree("ii_2"));
        System.out.println("Out-degrees for ii_2: " + graph.outDegree("ii_2"));
        System.out.println("Outbounds for V_1: "+ graph.outboundNeighbors("V_1"));
        System.out.println("Inbounds for V_1: "+ graph.inboundNeighbors("V_1"));
        System.out.println("(0,2)? " + (graph.isEdge("ii_1", "V_1") ? "It's an edge" : "It's not an edge"));
        System.out.println("(1,3)? " + (graph.isEdge("ii_1", "V") ? "It's an edge" : "It's not an edge"));

        System.out.println(graph.getSequence(6, "I").toString());
        System.out.println(graph.getSequence(6, "I", 0).toString());
        
        if (graph.isNeighbor("ii_2", "IV", "V"))
        	System.out.println("good");

    }

    //returns how to much to move a note to make it fit with the variation
	public static int noteTranslationOnVariation(int pitch, String variation, int key, boolean major) {
		
		//calculate what degree the pitch corresponds to
		int degree = pitch-key;
		degree = degree%12;
		
		
		//see if it requires changes
		switch (variation){
		case "M7":
			if (degree == 10)
				return 1;
			break;
		case "M9":
			if (degree == 1)
				return 1;
			if (degree == 3)
				return -1;
			break;
		case "m7":
			if (degree == 11)
				return -1;
			break;
		case "m9":
			if (degree == 2)
				return -1;
			break;
		case "m":
			if (major && degree == 4)
				return -1;
			break;
		case "sus":
			if (major && degree == 4)
				return +1;
			if (!major && degree == 3)
				return -1;
			break;
		case "m6":
			if (degree == 9)
				return -1;
			break;
		default:
				break;
		}
		return 0;
	}
}