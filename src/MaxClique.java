import java.io.*;
import java.util.ArrayList;

public class MaxClique {

    int nodesCount; 
    public static ArrayList<Vertex> X;  
    public static ArrayList<Vertex> R; 
    public static ArrayList<Vertex> P;
    public int maxClique = 0;
    public static ArrayList<Vertex> graph = new ArrayList<Vertex>();

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
            if (!y.getNbrs().contains(y)) { 
                y.getNbrs().add(this); 
                y.degree++; 
            } 
            this.degree++; 

        } 

        public void removeNbr(Vertex y) {
            this.nbrs.remove(y); 
            if (y.getNbrs().contains(y)) { 
                y.getNbrs().remove(this); 
                y.degree--; 
            } 
            this.degree--; 

        } 

        @Override 
        public int compareTo(Vertex o) {
            if (this.degree < o.degree) {
                return -1; 
            } 
            if (this.degree > o.degree) {
                return 1;
            } 
            return 0; 
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
//            nodesCount = Integer.parseInt(bufReader.readLine());
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
    void Bron_KerboschWithoutPivot(ArrayList<Vertex> R, ArrayList<Vertex> P,
            ArrayList<Vertex> X, String pre) { 
    	if(Config.DEBUG)
    		System.out.print(pre + " " + printSet(R) + ", " + printSet(P) + ", " 
                + printSet(X)); 
        if ((P.size() == 0) && (X.size() == 0)) {
        	if(Config.DEBUG)
        		printClique(R); 
            if(R.size() > maxClique) {
            	maxClique = R.size();
            	System.out.println("NEW BEST LOWER BOUND = " + maxClique);
            }
            return; 
        } 
        if(Config.DEBUG)
        	System.out.println(); 

        ArrayList<Vertex> P1 = new ArrayList<Vertex>(P); 

        for (Vertex v : P) { 
            R.add(v); 
            Bron_KerboschWithoutPivot(R, intersect(P1, getNbrs(v)), 
                    intersect(X, getNbrs(v)), pre + "\t"); 
            R.remove(v); 
            P1.remove(v); 
            X.add(v); 
        } 
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
    
    public void start(int n, ColEdge[] e, PrintWriter writer) {
    	MaxClique ff = new MaxClique();
    	if(Config.DEBUG)
    		System.out.println("Max Cliques Without Pivot"); 
        ff.createGraph(n, e);
        ff.Bron_KerboschPivotExecute();
        System.out.println("NEW BEST LOWER BOUND = " + ff.maxClique);

        if (writer != null)
            writer.print(ff.maxClique + ",");
    }
} 