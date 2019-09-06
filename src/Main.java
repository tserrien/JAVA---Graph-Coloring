import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Main {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Error! No filename specified.");
			System.exit(0);
		}

		String inputFile = args[0];
		ColEdge[] edgeConnections = readGraph.readFile(inputFile);
		
		int nodes = readGraph.nodes;
		int edges = readGraph.edges;
		int[][] matrix = Util.adjacencyMatrix(readGraph.nodes, edgeConnections);
		LinkedList<Integer>[] adjList = Util.adjList(nodes, edgeConnections);
		
		int maxDegree = 0;
		
		int[] degreeArray = new int[nodes];
		
		
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[0].length; j++) {
				if(matrix[i][j] == 1) {
					degreeArray[i]++;
				}				
			}
			if(degreeArray[i] > maxDegree) {
				maxDegree = degreeArray[i];
			}
		}
		
		ArrayList<Integer> degDescent = new ArrayList<Integer>();
		for(int i = 0; i < nodes; i++) {
			degDescent.add(i);
		}
		class DegComparator implements Comparator<Integer> {
			@Override
			public int compare(Integer v1, Integer v2) {
				return degreeArray[v2] - degreeArray[v1];
			}
		}
		DegComparator comp = new DegComparator();
		Collections.sort(degDescent, comp);
		
		if(Config.DEBUG) {
			Util.printOrderAndDegree(degDescent, adjList);
			System.out.println("Max degree: " + maxDegree);			
		}

        double density = (double) edges / (nodes * (nodes - 1) / 2); // Calculate density of the graph

        // Commence logical checks, afterwards - algorithms
        // TODO refactor to avoid using this big else/if
        if (edges == 0) { // Graph is completely disconnected
            System.out.println("CHROMATIC NUMBER = 1");
        } else if (density == 1.0) { // Graph is fully connected
            System.out.println("CHROMATIC NUMBER = " + nodes);
        } else if (Bipartite.isBipartite(matrix, nodes)) { // Graph is bipartite
            System.out.println("CHROMATIC NUMBER = 2");
        } else {
            LowerBoundHeap lowerBoundHeap = new LowerBoundHeap(nodes, edgeConnections, matrix);
            Thread lbhThread = new Thread(lowerBoundHeap);
            lbhThread.start();
            try {
                lbhThread.join(Config.killtime); // Stop LowerBoundHeap after at most 60 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
	}
}