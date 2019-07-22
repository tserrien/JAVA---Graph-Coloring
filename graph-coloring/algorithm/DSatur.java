package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class DSatur {
	public static ArrayList<ArrayList<Integer>> solution;
	public DSatur(int n, int k, LinkedList<Integer>[] _adjList, ArrayList<Integer> _ordering) {
		ArrayList<Integer>ordering = (ArrayList<Integer>) _ordering.clone();
		if(Config.DEBUG)
			System.out.println("start DSatur: ");
		int[] colors = new int[n];
		int[] s = new  int[n];
		solution = new ArrayList();
		Node[] adjList = new Node[(_adjList.length)];
		for(int i = 0; i < _adjList.length; i++) {
			LinkedList<Integer> list = _adjList[i];
			adjList[i] = new Node(i);
			for(int j = 0; j < _adjList[i].size();j++)
			adjList[i].add(_adjList[i].get(j));
		}
		
		ComparatorNode comp = new ComparatorNode(adjList);
		
		while(ordering.size() > 0) {
//			System.out.println(ordering);
			int v = ordering.get(0);
			for(int c = 1; c <= k; c++) {
				if(isSafe(v, c, _adjList, colors)) {
					colors[v] = c;
					if(solution.size() >= c) {
						solution.get(c - 1).add(v);
					} else {
						ArrayList<Integer>list = new ArrayList();
						list.add(v);
						solution.add(list);
					}
					updateSaturation(v, adjList);
					ordering.remove((Integer)(v));
					break;
				}
			}
			Collections.sort(ordering, comp);
			if(Config.DEBUG) {
				Util.printOrderAndSaturation(ordering, adjList);
//				System.out.println(ordering.size());
			}
		}
		
		if(Config.DEBUG)
			Util.printResult(solution.size(), colors);
	}
	
	public void updateSaturation(int v, Node[] adjList) {
		Node list = adjList[v];
		for(int i = 0; i < list.size(); i++) {
			Node node = adjList[(int)list.get(i)];
			node.s++;
		}
	}
	public boolean isSafe (int v, int c, LinkedList<Integer>[] adjList, int[] colors) {
		LinkedList list = adjList[v];
		for(int i = 0; i < list.size(); i++) {
			int u = (int)list.get(i);
			if(colors[u] == c) {
				return false;
			}
		}
		return true;
	}
	public static void main(String[] args) {
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
		if(Config.DEBUG)
			System.out.println("max Degree: " + maxDegree);
		
		
		
//		ArrayIndexComparator _comparator = new ArrayIndexComparatorDeg(d);
//		Integer[] degDescent = comparator.createIndexArray(n);
//		Arrays.sort(degDescent, comparator);
		
		
		ArrayList<Integer> degDescent = Util.genOrdering(n);
		class DegComparator implements Comparator<Integer> {
			@Override
			public int compare(Integer v1, Integer v2) {
				return d[v2] - d[v1];
			}
		}
		DegComparator comp = new DegComparator();
		Collections.sort(degDescent, comp);
		Util.printOrderAndDegree(degDescent, adjList);
		
		new DSatur(n, maxDegree + 1, adjList, degDescent);

	}

}

class ComparatorNode implements Comparator<Integer> {
	Node[] adjList;
	public ComparatorNode(Node[] list) {
		this.adjList = list;
	}
	@Override
	public int compare(Integer u, Integer v) {
		return adjList[v].s - adjList[u].s;
	}
}

