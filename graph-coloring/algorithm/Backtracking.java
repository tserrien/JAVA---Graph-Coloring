package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class Backtracking {
	public Backtracking(int n, int k, int lb, int[][] matrix, Node[] adjList, int[] _seq) {
		int[] colors = new int[n];
		ArrayList<Integer> seq = Util.listFromArray(_seq);
//		ArrayList<ArrayList<Integer>> graphs = Util.findSubGraphs(n, adjList);
//		System.out.println("total subgraphs: " + graphs.size());
//		for(int i = 0; i < graphs.size(); i++) {
//			System.out.println("subgraph " + (i + 1) + "(length = " + graphs.get(i).size() + "): ");
//			System.out.println(graphs.get(i));			
//		}
//		System.out.println("before filted " + seq.size() );
//		ArrayList<Integer> seqFiltered = filterSubsetNeighbors(seq, matrix);
//		System.out.println("after filted " + seqFiltered.size() );
//		ArrayList<Integer> seqFiltered = seq;
		updateSeq(k, seq, adjList);
		while(coloring(0, k, colors, adjList, seq) && k >= lb) {
			if(Config.DEBUG) {
				fillColor(k, colors, adjList);
				boolean corr = Util.checkColoring(colors, matrix);
				Util.printResult(k, colors);
				System.out.println("correct: " + corr);				
			}
			System.out.println("NEW BEST UPPER BOUND = " + k);
			k--;
			updateSeq(k, seq, adjList);
//			System.out.println(seq);
			colors = new int[n];
		}
		System.out.println("CHROMATIC NUMBER = " + (k+1));
	}
	public static ArrayList<Integer> filterSubsetNeighbors(ArrayList<Integer> seq, int[][] matrix) {
		ArrayList neighbors;
		ArrayList<Integer> _seq = new ArrayList(seq);
		for(int i = 0; i < seq.size(); i++) {
			int v = seq.get(i);
			for(int j = 0; j < seq.size(); j++) {
				int u = seq.get(j);
				if(v != u && isSubset(v, u, matrix)) {
					_seq.remove(Integer.valueOf(v));
				}
			}
		}
		return _seq;
	}
	
	
	static boolean isSubset(int v, int u, int[][] matrix) {
		for(int i = 0; i < matrix.length; i++) {
			if(matrix[v][i] == 1 && matrix[u][i] == 0) {
				return false;
			}
		}
		return true;
	}
	private void fillColor(int k, int[] colors, Node[] adjList) {
		for(int i = 0; i < colors.length; i++) {
			if(colors[i] == 0) {
				for(int c = 1; c <= k; c++) {
					if(isSafe(i, c, colors, adjList)) {
						colors[i] = c;
						break;
					}
				}
			}
		}
	}
	private void updateSeq(int k, ArrayList<Integer> seq, Node[] adjList) {
		Iterator iter = seq.iterator();
		while(iter.hasNext()) {
			int index = (int)iter.next();
			if(adjList[index].size() <= k-1) {
				iter.remove();
			}
		}
	}
	public boolean coloring(int index, int k, int[] colors, Node[] adjList, ArrayList<Integer> seq) {
		if(index == seq.size()) {
			return true;
		}
		for(int c = 1; c <= k; c++) {
			int v = seq.get(index);
			if(isSafe(v, c, colors, adjList)) {
				colors[v] = c;
				if(coloring(index+1, k, colors, adjList, seq)) {
					return true;
				}
				colors[v] = 0;
			}
		}
		return false;
	}
	
	private boolean isSafe(int v, int c, int[]colors, Node[] adjList) {
		Node nodeV = adjList[v];
		for(int i = 0; i < nodeV.size();i++) {
			Node nodeU = (Node)(nodeV.get(i));
			int u = nodeU.i;
			if(colors[u] == c) {
				return false;
			}
		}
		return true;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int n, k;
		int[] colors;
		Node[] adjList;
		int[] seq;
		

	}

}
