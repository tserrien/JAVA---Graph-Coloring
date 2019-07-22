package algorithm; 

import java.io.BufferedReader; 
import java.io.File; 
import java.io.FileReader; 
import java.io.StringReader; 
import java.util.ArrayList; 
import java.util.Collections; 


public class MaxClique {
	int k;
    int nodesCount; 
    public static ArrayList<Vertex> X;  
    public static ArrayList<Vertex> R; 
    public static ArrayList<Vertex> P;
    public static int maxClique = 0;
    public static ArrayList<Vertex> clique;
    public static ArrayList<Vertex> graph = new ArrayList<Vertex>(); 
    
    public MaxClique(int n, int k, ColEdge[] e) {
		this.k = k;
		maxClique = 0;
		X = null;
		R = null;
		P = null;
		clique = null;
		graph = new ArrayList<Vertex>();

        if(Config.DEBUG)
    		System.out.println("Max Cliques Without Pivot"); 
        createGraph(n, e);
        Bron_KerboschPivotExecute();
        System.out.println("NEW BEST LOWER BOUND = " + maxClique);
	}
    public MaxClique() {
    	
    }

    class Vertex implements Comparable<Vertex> {
        int x; 

        int degree; 
        ArrayList<Vertex> nbrs = new ArrayList<Vertex>(); 

        public int getX() {
            return x; 
        } 

        public void setX(int x) {
            this.x = x; 
        } 

        public int getDegree() {
            return degree; 
        } 

        public void setDegree(int degree) {
            this.degree = degree; 
        } 

        public ArrayList<Vertex> getNbrs() { 
            return nbrs; 
        } 

        public void setNbrs(ArrayList<Vertex> nbrs) {
            this.nbrs = nbrs; 
        } 

        public void addNbr(Vertex y) {
            this.nbrs.add(y); 
            if (!y.getNbrs().contains(this)) { 
                y.getNbrs().add(this); 
                y.degree++; 
            } 
            this.degree++; 

        } 

        public void removeNbr(Vertex y) {
            this.nbrs.remove(y); 
            if (y.getNbrs().contains(this)) { 
                y.getNbrs().remove(this); 
                y.degree--; 
            } 
            this.degree--; 

        } 

        @Override 
        public int compareTo(Vertex o) {
          return this.degree - o.degree;
        } 
    } 

    void initGraph() { 
        graph.clear(); 
        for (int i = 0; i < nodesCount; i++) {
            Vertex V = new Vertex(); 
            V.setX(i); 
            graph.add(V); 
        } 
    } 

    int readTotalGraphCount(BufferedReader bufReader) throws Exception {

        return Integer.parseInt(bufReader.readLine()); 
    } 

    public void createGraph(int n, ColEdge[] e) {
    	nodesCount = n;
    	initGraph(); 
    	for(int i = 0; i < e.length; i++) {
    		int u = e[i].u - 1;
    		int v = e[i].v - 1;
    		Vertex vertU = graph.get(u); 
            Vertex vertV = graph.get(v); 
            vertU.addNbr(vertV);
    	}
    }
    // Reads Input 
    void readNextGraph(BufferedReader bufReader) throws Exception {
        try { 
        	String record = bufReader.readLine();
        	nodesCount = Integer.parseInt(record.substring(11));
        	record = bufReader.readLine();
            int edgesCount = Integer.parseInt(record.substring(8));
            initGraph(); 

            for (int k = 0; k < edgesCount; k++) {
                String[] strArr = bufReader.readLine().split(" "); 
                int u = Integer.parseInt(strArr[0]) - 1;
                int v = Integer.parseInt(strArr[1]) - 1;
                Vertex vertU = graph.get(u); 
                Vertex vertV = graph.get(v); 
                vertU.addNbr(vertV); 

            } 

        } catch (Exception e) { 
            e.printStackTrace(); 
            throw e; 
        } 
    } 

    // Finds nbr of vertex i 
    ArrayList<Vertex> getNbrs(Vertex v) { 
        int i = v.getX(); 
        return graph.get(i).nbrs; 
    } 

    // Intersection of two sets 
    ArrayList<Vertex> intersect(ArrayList<Vertex> arlFirst, 
            ArrayList<Vertex> arlSecond) { 
        ArrayList<Vertex> arlHold = new ArrayList<Vertex>(arlFirst); 
        arlHold.retainAll(arlSecond); 
        return arlHold; 
    } 

    // Union of two sets 
    ArrayList<Vertex> union(ArrayList<Vertex> arlFirst, 
            ArrayList<Vertex> arlSecond) { 
        ArrayList<Vertex> arlHold = new ArrayList<Vertex>(arlFirst); 
        arlHold.addAll(arlSecond); 
        return arlHold; 
    } 

    // Removes the neigbours 
    ArrayList<Vertex> removeNbrs(ArrayList<Vertex> arlFirst, Vertex v) { 
        ArrayList<Vertex> arlHold = new ArrayList<Vertex>(arlFirst); 
        arlHold.removeAll(v.getNbrs()); 
        return arlHold; 
    } 

    // Version without a Pivot 
    boolean Bron_KerboschWithoutPivot(ArrayList<Vertex> R, ArrayList<Vertex> P,
            ArrayList<Vertex> X, String pre) { 
    	if(Config.DEBUG)
    		System.out.print(pre + " " + printSet(R) + ", " + printSet(P) + ", " 
                + printSet(X)); 
        if ((P.size() == 0) && (X.size() == 0)) {
        	if(Config.DEBUG)
        		printClique(R); 
            if(R.size() > maxClique) {
            	maxClique = R.size();
            	clique = (ArrayList<Vertex>) R.clone();
            	System.out.println("NEW BEST LOWER BOUND = " + maxClique);
            	if(this.k == maxClique) {
            		return true;            		
            	}
            }
        } 
        if(Config.DEBUG)
        	System.out.println(); 
        //copy p as all vertices need to be considered
        ArrayList<Vertex> P1 = new ArrayList<Vertex>(P); 

        for (Vertex v : P) { 
            R.add(v); 
            if(Bron_KerboschWithoutPivot(R, intersect(P1, getNbrs(v)), 
                    intersect(X, getNbrs(v)), pre + "\t")) {
            	return true; 
            }
            R.remove(v); 
            P1.remove(v); 
            X.add(v); 
        }
        return false;
    } 

    void Bron_KerboschPivotExecute() { 

        X = new ArrayList<Vertex>(); 
        R = new ArrayList<Vertex>(); 
        P = new ArrayList<Vertex>(graph); 
        Bron_KerboschWithoutPivot(R, P, X, ""); 
    } 

    void printClique(ArrayList<Vertex> R) {
        System.out.print("  -------------- Maximal Clique : "); 
        for (Vertex v : R) { 
            System.out.print(" " + (v.getX())); 
        } 
        System.out.println(); 
    } 

    String printSet(ArrayList<Vertex> Y) { 
        StringBuilder strBuild = new StringBuilder(); 

        strBuild.append("{"); 
        for (Vertex v : Y) { 
            strBuild.append("" + (v.getX()) + ","); 
        } 
        if (strBuild.length() != 1) {
            strBuild.setLength(strBuild.length() - 1); 
        } 
        strBuild.append("}"); 
        return strBuild.toString(); 
    } 
    

    public static void main(String[] args) {
        BufferedReader bufReader = null; 
//        if (args.length > 0) {
            // Unit Test Mode 
//            bufReader = new BufferedReader(new StringReader(
//                    "1\n4\n4\n0 1\n0 2\n1 2\n1 3\n")); 
//        } else { 
//            File file = new File("C:\\graphAlgos\\mc2.txt"); 
//            try { 
//                bufReader = new BufferedReader(new FileReader(file));
//            } catch (Exception e) { 
//                e.printStackTrace(); 
//                return; 
//            } 
//        } 
        if (args.length < 1) {
			System.out.println("Error! No filename specified.");
			System.exit(0);
		}
        String inputfile = args[0];
        
        try { 
          bufReader = new BufferedReader(new FileReader(inputfile));
      } catch (Exception e) { 
          e.printStackTrace(); 
          return; 
      }
        
        if(Config.DEBUG)
        	System.out.println("Max Cliques Without Pivot"); 
        try { 
//            int totalGraphs = ff.readTotalGraphCount(bufReader);
//            for (int i = 0; i < totalGraphs; i++) {
//                System.out.println("************** Start Graph " + (i + 1) 
//                        + "******************************");
        	MaxClique ff = new MaxClique();
                ff.readNextGraph(bufReader);
                ff.Bron_KerboschPivotExecute();
                System.out.println("NEW BEST LOWER BOUND = " + ff.maxClique);

//            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
            System.err.println("Exiting : " + e); 
        } finally { 
            try { 
                bufReader.close(); 
            } catch (Exception f) { 

            } 
        } 
    } 
} 