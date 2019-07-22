package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class RLF {
	public static ArrayList<Integer> X;
	public static ArrayList<Integer> Y;
	ArrayList<ArrayList<Integer>> S;
	ArrayList<Integer> Si;
	int[][] matrix;
	public RLF(int n, LinkedList<Integer>[] _adjList, int[][] matrix, ArrayList<Integer> ordering) {
		
		S = new ArrayList<ArrayList<Integer>>();
		Y = new ArrayList<Integer>();
		X = new ArrayList();
		this.matrix = matrix;
		Node[] adjList = new Node[(_adjList.length)];
		for(int i = 0; i < _adjList.length; i++) {
			LinkedList<Integer> list = _adjList[i];
			adjList[i] = new Node(i);
			for(int j = 0; j < _adjList[i].size();j++)
			adjList[i].add(new Node(_adjList[i].get(j)));
		}
		for(int i = 0; i < ordering.size(); i++) {
			X.add(ordering.get(i));			
		}
		if(Config.DEBUG)
			System.out.println(X);
		int cnt = 0;
		while(!X.isEmpty()) {
			Si = new ArrayList<Integer>(); 
			while(!X.isEmpty()) {
				int v = X.get(0);
				Si.add(v);
				updateY(v, matrix);
				updateX(v);
				sortX();
				cnt++;
//				if(cnt < 20) {
//					System.out.print("X: ");
//					System.out.println(X);
//					System.out.print("Y: ");
//					System.out.println(Y);
//				}
			}
			S.add(Si);
			X = Y;
			Y = new ArrayList<Integer>(); 
		}
		if(Config.DEBUG) {
			System.out.println("feasible with " + S.size() + " colors");
			System.out.println(S);			
		}
		
		
	}
	private void sortX() {
		Collections.sort(X, new Comparator<Integer>() {
			
			@Override
			public int compare(Integer v1, Integer v2) {
				int s1 = 0;
				int s2 = 0;
				for(int i = 0; i < Y.size(); i++) {
					int u = Y.get(i);
					if(matrix[v1][u] == 1) {
						s1++;
					}
					if(matrix[v2][u] == 1) {
						s2++;
					}
				}
				return s2 - s1;
			}
			
		});
		
	}
	private static void updateX(int v) {
		X.remove((Integer)v);
		for(int i = 0; i < Y.size(); i++) {
			int u = Y.get(i);
			if(X.contains(u)) {
				X.remove((Integer)u);
			}
		}
		
	}
	private static void updateY(int v, int[][] matrix) {
		for(int u = 0; u < matrix.length; u++) {
			if(matrix[v][u] == 1 && X.contains(u)) {
				Y.add(u);				
			}
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				if (args.length < 1) {
					System.out.println("Error! No filename specified.");
					System.exit(0);
				}

				String inputfile = args[0];
				ColEdge[] e = readGraph.readFile(inputfile);
				int n = readGraph.n;
				LinkedList<Integer>[] adjList = Util.adjList(n, e);
				int[][] matrix = Util.adjacencyMatrix(readGraph.n, e);
				int maxDegree = 0;
				
				for(int i = 0; i < adjList.length; i++) {
					if(maxDegree < adjList[i].size()) {
						maxDegree = adjList[i].size();
					}
				}
				System.out.println("max Degree: " + maxDegree);
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
				System.out.println("Deg descent order: ");
				for(int i = 0; i < degDescent.size(); i++) {
					int index = degDescent.get(i);
					System.out.print("(" +  + index + ": ");
					System.out.print(adjList[index].size() + "), ");
				}
				new RLF(n, adjList, matrix, degDescent);

		
	}

}
