package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import algorithm.MaxClique.Vertex;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		/*if (args.length < 1) {
			System.out.println("Error! No filename specified.");
			System.exit(0);
		}

		String inputfile = args[0];*/
		String inputfile = "Graphs/graph18.txt";
		ColEdge[] e = readGraph.readFile(inputfile);
		
		int k;
		int n = readGraph.n;
		int m = readGraph.m;
		int[][] matrix = Util.adjacencyMatrix(readGraph.n, e);
		LinkedList<Integer>[] adjList = Util.adjList(n, e);
		
		int maxDegree = 0;
		
		final int[] d = new int[n];
		
		
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
		
//		System.out.println("start WelshPower");
//		WelshPower wp = new WelshPower(degDescent, maxDegree + 1, matrix);
//		System.out.println("NEW BEST UPPER BOUND = " + wp.colorSet.size());
//		System.out.println("Welsh Power result: ");
		
//		Util.printResult(wp.c, wp.colors);
//		int k = wp.colorSet.size();
		
//		System.out.println("start RLF");
//		System.out.println("RLF result: ");

        double density = (double) m / (n * (n - 1) / 2); // Calculate density of the graph

        if (m == 0) { // Graph is completely disconnected
            System.out.println("CHROMATIC NUMBER = 1");
        } else if (density == 1.0) { // Graph is fully connected
            System.out.println("CHROMATIC NUMBER = " + n);
        } else {

            DSatur dst = new DSatur(n, maxDegree + 1, adjList, degDescent);
            System.out.println("NEW BEST UPPER BOUND = " + dst.solution.size());
            if (dst.solution.size() == 2) {
                System.out.println("CHROMATIC NUMBER = 2");
                return;
            }

            RLF rlf = new RLF(n, adjList, matrix, degDescent);
            System.out.println("NEW BEST UPPER BOUND = " + rlf.S.size());
            if (rlf.S.size() == 2) {
                System.out.println("CHROMATIC NUMBER = 2");
                return;
            }
//		int loop = 0;
//		Greedy gr;
//		int[] ordering = new int[n];
//		for(int i = 0; i < n; i++) {
//			ordering[i] = i;
//		}

//		System.out.println("deg descent size: "+ degDescent.size());


//		WelshPower.colorSet.size(),
            k = Math.min(dst.solution.size(), rlf.S.size());
            //no possibility of one-color after greedy algorithm


//		IteratedGreedy igr4 = IteratedGreedy.Random(n, m, e, WelshPower.colorSet.size(), adjList, WelshPower.colorSet);
//		System.out.println("NEW BEST UPPER BOUND = " + igr4.k);
//		IteratedGreedy igr = new IteratedGreedy(n, m, e, WelshPower.colorSet.size(), adjList, WelshPower.colorSet);
//		System.out.println("NEW BEST UPPER BOUND = " + igr.k);

            IteratedGreedy igr2 = new IteratedGreedy(n, m, e, dst.solution.size(), adjList, DSatur.solution);
            System.out.println("NEW BEST UPPER BOUND = " + igr2.k);

            IteratedGreedy igr3 = new IteratedGreedy(n, m, e, rlf.S.size(), adjList, rlf.S);
            System.out.println("NEW BEST UPPER BOUND = " + igr3.k);
//		System.out.println("start IG with WelshPower coloring");
//		System.out.println("end IG with WelshPower");
//		System.out.println("start IG with DSatur coloring");
//		System.out.println("end IG with DSatur");
//		System.out.println("start IG with RLF coloring");
//		System.out.println("end IG with RLF");

//		System.out.println("Is tree: " + Util.isTree(n, adjList));


            IteratedGreedy best = Util.optimized(new IteratedGreedy[]{igr2, igr3});
//		igr = null;
            igr2 = null;
            igr3 = null;
//		igr4 = null;
            k = best.colorSet.size();
            System.out.println("NEW BEST UPPER BOUND = " + k);
            if (k == 2) {
                System.out.println("CHROMATIC NUMBER = 2");
                return;
            }
            //MaxClique ff = new MaxClique(n, k, e);

            HoffmansLB hoffLB = new HoffmansLB(n, m, e);
            Thread hoffThread = new Thread(hoffLB);
            hoffThread.start();
            try {
                hoffThread.join(30000); // terminate after at most 30 seconds
            } catch (Exception ex) {
                System.out.println("Hoffman's LB thread interrupted.");
            }

            int lb = hoffLB.getLowerBound();

            LowerBoundHeap lbh = new LowerBoundHeap(n, e, matrix);
            Thread lbhThread = new Thread(lbh);
            lbhThread.start();
            try {
                lbhThread.join(15000); // terminate after at most 15 seconds
            } catch (Exception ex) {
                System.out.println("Bron Kerbosch's LB thread interrupted.");
            }

            if (lb < lbh.getLowerBound()) {
                lb = lbh.getLowerBound();
            }
            System.out.println("NEW BEST LOWER BOUND = " + lb);

            if (k == lb) {
                System.out.println("CHROMATIC NUMBER = " + k);
                return;
            }

//		ArrayList<Vertex> clique = ff.clique;
//		Collections.sort(clique, new Comparator() {
//
//			@Override
//			public int compare(Object o1, Object o2) {
//				Vertex v1 = (Vertex)o1;
//				Vertex v2 = (Vertex)o2;
//				return v2.degree - v1.degree;
//			}
//		});

//		System.out.println(ff.graph.size());
//		System.out.println(ff.clique.size());

//		clique.addAll(ff.graph);
//		int[] _order = new int[ff.graph.size()];
//		for(int i = 0; i < ff.graph.size(); i++) {
//			_order[i] = ff.graph.get(i).x;
//		}
//		int[] _cliqueOrder = new int[clique.size()];
//		for(int i = 0; i < clique.size(); i++) {
//			_cliqueOrder[i] = clique.get(i).x;
//		}


//		System.out.println(ff.graph.size());
//		new Backtracking3(n, 3, 2, matrix, Util.adjListNode(n, e), best.seq);
//		new Backtracking(n, k-1, ff.maxClique, matrix, Util.adjListNode(n, e), best.seq);
//		new Backtracking(n, k, ff.maxClique, matrix, Util.adjListNode(n, e), _order);
//		System.out.println(n);
//		System.out.println(k);
//		System.out.println(ff.maxClique);
//		


//		System.out.println("deg descent size " + degDescent.size());

//		degDescent = new ArrayList<Integer>();
//		for(int i = 0; i < n; i++) {
//			degDescent.add(i);
//		}
//		
//		comp = new DegComparator();
//		Collections.sort(degDescent, comp);
            MaxClique ff = new MaxClique(n, k, e);
            ff.graph.removeAll(ff.clique);

            ArrayList cliqueOrder = new ArrayList();
            ff.clique.sort(new Comparator<Vertex>() {
                public int compare(Vertex o1, Vertex o2) {
                    return o2.degree - o1.degree;
                }
            });

            for (int i = 0; i < ff.clique.size(); i++) {
                cliqueOrder.add(ff.clique.get(i).x);
            }

            ArrayList _order = new ArrayList();
            int[] colors = new int[n];
            for (int c = 1; c <= ff.clique.size(); c++) {
                colors[ff.clique.get(c - 1).x] = c;
                _order.add(ff.clique.get(c - 1).x);
            }

            for (int i = 0; i < ff.graph.size(); i++) {
                _order.add(ff.graph.get(i).x);
            }
//		System.out.println(degDescent.size());
//		System.out.println(_order.size());


//		new Backtracking3(n, k-1, ff.maxClique, matrix, Util.adjListNode(n, e), _order, cliqueOrder, colors);
            new Backtracking3(n, k - 1, lbh.getLowerBound(), matrix, Util.adjListNode(n, e), _order, cliqueOrder, colors, false, null);
//		n, maxDegree, 0, matrix, Util.adjListNode(n, e), degDescent
//		new Backtracking2(n, k-1, ff.maxClique, matrix, Util.adjListNode(n, e), degDescent);
        }
	}

}

