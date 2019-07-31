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

		String inputfile = args[0];
		ColEdge[] e = readGraph.readFile(inputfile);
		
		int n = readGraph.n;
		int m = readGraph.m;
		int[][] matrix = Util.adjacencyMatrix(readGraph.n, e);
		LinkedList<Integer>[] adjList = Util.adjList(n, e);
		
		int maxDegree = 0;
		
		int[] d = new int[n];
		
		
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[0].length; j++) {
				if(matrix[i][j] == 1) {
					d[i]++;
				}				
			}
			if(d[i] > maxDegree) {
				maxDegree = d[i];
			}
		}
		
		ArrayList<Integer> degDescent = new ArrayList<Integer>();
		for(int i = 0; i < n; i++) {
			degDescent.add(i);
		}
		class DegComparator implements Comparator<Integer> {
			@Override
			public int compare(Integer v1, Integer v2) {
				return d[v2] - d[v1];
			}
		}
		DegComparator comp = new DegComparator();
		Collections.sort(degDescent, comp);
		
		if(Config.DEBUG) {
			Util.printOrderAndDegree(degDescent, adjList);
			System.out.println("Max degree: " + maxDegree);			
		}

        double density = (double) m / (n * (n - 1) / 2); // Calculate density of the graph

        int max;

        // Commence logical checks, afterwards - algorithms
        if (m == 0) { // Graph is completely disconnected
            System.out.println("CHROMATIC NUMBER = 1");
        } else if (density == 1.0) { // Graph is fully connected
            System.out.println("CHROMATIC NUMBER = " + n);
        } else if (Bipartite.isBipartite(matrix, n)) { // Graph is bipartite
            System.out.println("CHROMATIC NUMBER = 2");
        } else {
            // Instantiate Welsh Powell algorithm class and start its thread
            WelshPowell wp = new WelshPowell(degDescent, maxDegree + 1, matrix);
            Thread wpThread = new Thread(wp);
            wpThread.start();
            try {
                wpThread.join(10000); // Stop WP after at most 10 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            max = wp.colorSet.size();
            System.out.println("NEW BEST UPPER BOUND = " + max);

            // Instantiate RLF algorithm class and start its thread
            RLF rlf = new RLF(n, adjList, matrix, degDescent);
            Thread rlfThread = new Thread(rlf);
            rlfThread.start();
            try {
                rlfThread.join(45000); // Stop RLF after at most 45 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            if (rlf.S.size() < max) {
                max = rlf.S.size();
                System.out.println("NEW BEST UPPER BOUND = " + max);
            }

            // Instantiate DSATUR algorithm class and start its thread
            DSatur dst = new DSatur(n, maxDegree + 1, adjList, degDescent);
            Thread dstThread = new Thread(dst);
            dstThread.start();
            try {
                dstThread.join(20000); // Stop DSATUR after at most 20 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            if (dst.solution.size() < max) {
                max = dst.solution.size();
                System.out.println("NEW BEST UPPER BOUND = " + max);
            }

            // Instantiate Iterated Greedy algorithm class and start its thread
            IteratedGreedy igr3 = new IteratedGreedy(n, m, e, rlf.S.size(), adjList, rlf.S);
            Thread igr3Thread = new Thread(igr3);
            igr3Thread.start();
            try {
                igr3Thread.join(20000); // Stop Iterated Greedy after at most 20 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            if (igr3.k < max) {
                max = igr3.k;
                System.out.println("NEW BEST UPPER BOUND = " + max);
            }

            // Instantiate Iterated Greedy algorithm class and start its thread
            IteratedGreedy igr2 = new IteratedGreedy(n, m, e, dst.solution.size(), adjList, DSatur.solution);
            Thread igr2Thread = new Thread(igr2);
            igr2Thread.start();
            try {
                igr2Thread.join(20000); // Stop Iterated Greedy after at most 20 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            if (igr2.k < max) {
                max = igr2.k;
                System.out.println("NEW BEST UPPER BOUND = " + max);
            }

            // Instantiate Iterated Greedy algorithm class and start its thread
            IteratedGreedy igr = new IteratedGreedy(n, m, e, WelshPowell.colorSet.size(), adjList, WelshPowell.colorSet);
            Thread igrThread = new Thread(igr);
            igrThread.start();
            try {
                igrThread.join(20000); // Stop Iterated Greedy after at most 20 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            if (igr.k < max) {
                max = igr.k;
                System.out.println("NEW BEST UPPER BOUND = " + max);
            }

            LowerBoundTop lowerBoundTop = new LowerBoundTop(n, e, null);
            Thread lbThread = new Thread(lowerBoundTop);
            lbThread.start();
            try {
                lbThread.join(45000); // Stop LowerBound after at most 45 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            LowerBoundHeap lowerBoundHeap = new LowerBoundHeap(n, e, matrix);
            Thread lbhThread = new Thread(lowerBoundHeap);
            lbhThread.start();
            try {
                lbhThread.join(450000); // Stop LowerBoundHeap after at most 60 seconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // Backtracking doesn't need its separate thread, it can run as long as we have time
            Backtracking.start(n, max, adjList, matrix);
        }
	}

}

