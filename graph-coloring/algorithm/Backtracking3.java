package algorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;


public class Backtracking3 {
	final private int[][] matrix;
	final Node[] adjList;
	int k;
	int lb;
	int n;
	boolean stop = false;
	/*@param _seq with sequence vertices of maximal clique first in descending order of degree, 
	 * followed by non maximal clique vertices in descending order of degree
	 * @param _clique sequence vertices of maximal clique in descending order of degree  
	 *  
	 */
	ComparatorNode comp;
	class ComparatorNode implements Comparator<Node> {
		@Override
		public int compare(Node o1, Node o2) {
			return o2.s - o1.s;
		}
	} 
	public Backtracking3(int n, int k, int lb, int[][] matrix, Node[] adjList, ArrayList<Integer> _seq, ArrayList<Integer> _clique, int[] colors, boolean writeToFile, FileWriter writer) {
//		int[] colors = new int[n];
		this.comp = new ComparatorNode();
		this.n = n;
		if(Config.DEBUG) {
			System.out.println("before remove clique: " + _seq.size());			
		}
		_seq.removeAll(_clique);
		if(Config.DEBUG) {
			System.out.println("after remove clique: " + _seq.size());			
		}
		ArrayList<Node> clique = toNodeList(_clique, adjList);
		ArrayList<Node> seq = toNodeList(_seq, adjList);
		
		for(int i = 0; i < _clique.size(); i++) {
			int u = _clique.get(i);
			for(int j = 0; j < _seq.size(); j++) {
				int v = _seq.get(j);
				if(matrix[v][u] == 1) {
					seq.get(j).s++;
				}
			}
		}
		Collections.sort(seq, new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				return o2.s - o1.s;
			}
		});
//		for(int i = 0; i < clique.size(); i++) {
//			Node node = clique.get(i);
//			Node neighbor = adjList[node.i];
//			for(int j = 0; j < neighbor.size(); j++) {
//				
//			}
////			seq = this.genNextSeq(node, seq, matrix);
//		}
		this.matrix = matrix;
		this.adjList = adjList;
		this.k = k;
		this.lb = lb;
		
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
		
//		updateSeq(k, seq, adjList);
		
		while(coloring(colors, adjList, seq, lb)) {
			
//			colors = new int[n];
//			seq = toNodeList(_seq, adjList);
		}
		System.out.println("CHROMATIC NUMBER = " + (this.k + 1));

		try {
			if (writeToFile) {
				writer.write(this.k + 1  + ",");
				writer.flush();
			}
		} catch (IOException ex) {

		}

//		Util.printResult(this.k+1, colors);
	}
	private ArrayList<Node> toNodeList(ArrayList<Integer> _seq, Node[] adjList) {
		ArrayList<Node> seq = new ArrayList<Node>();
		for(int i = 0; i < _seq.size(); i++) {
			seq.add(adjList[_seq.get(i)].clone());
		}
		return seq;
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
	//recursive function
	public boolean coloring(int[] colors, Node[] list, ArrayList<Node> seq, int maxC) {
		if(stop) {
			return false;
		}
		//when we have colored all nodes, we return true
		if(seq.size() == 0) {
			if(Config.DEBUG) {
				boolean corr = Util.checkColoring(colors, matrix);
				Util.printResult(k, colors);
				System.out.println(" btk correct: " + corr);
				}
			System.out.println("NEW BEST UPPER BOUND = " + k);
			this.k--;
			return true;
		}
		
		
		Node node = seq.get(0);
		int v = node.i;
		ArrayList<Node> _seq = genNextSeq(node, seq, matrix);
		for(int c = 1; c <= this.k; c++) {
			if(isSafe(v, c, colors, list)) {
				int _maxC = Math.max(c, maxC);
				colors[v] = c;
				//coloring failed
				if(!coloring(colors, list, _seq, _maxC)) {
					//if the cn had been reduced to lb, or we have rewinded back to the last node of the clique
					if(this.k <= this.lb || seq.size() > this.n - this.lb) {
						if(Config.DEBUG) {
							System.out.println("k : " +this.k);
							System.out.println("seq size : " +seq.size());							
						}
						return false;
					}
					colors[v] = 0;
				} 
				 //solution found, and if the branch is in a impossible branch, we need to uncolor the child node and return immediately					
				else if(maxC > this.k) {
					//need to remove color
					colors[v] = 0;
//					System.out.println("k : " +this.k);
//					System.out.println("max  C: " +maxC);
//					System.out.println("current size: " + seq.size());
					return true;
				} else { //we can continue to try different color at this node onwards
					colors[v] = 0;
				}
				
//				maxC > this.k || 
				
			}
//			if(isSafe(v, c, colors, list) && lookup(c, colors, _seq, k)) {
//				colors[v] = c;
//				if(coloring(k, colors, list, _seq)) {
//					return true;
//				} else {
//					colors[v] = 0;
//				}
//			} 
		}
		return false;
	}
	//remove the current node from the sequence, update the saturation for the uncolored neighbor vertices
	//and sort the sequence based on the saturation in descending order
	private ArrayList<Node> genNextSeq( Node node, ArrayList<Node> seq, int[][] matrix) {
		int v = node.i; // the index of the current node
		ArrayList<Node> ret = new ArrayList<Node>();
		for(int i = 1; i < seq.size(); i++) {
			
			Node clone = seq.get(i).clone();
			ret.add(clone);
			int u = clone.i;
			if(matrix[v][u] == 1) {
				clone.s++;
			}				
		}
		
		Collections.sort(ret, comp);
		return ret;
	}
	
	
	private boolean lookup(int c, int[] colors, ArrayList<Node> seq, int k) {
		if(seq.isEmpty()) {
			return true;
		}
		Node nextNode = seq.get(0);
		int v = nextNode.i;
		//simulate coloring
		colors[v] = c;
		for(int cc = 1; cc <= k; cc++) {
			if(isSafe(v, cc, colors, adjList)) {
				colors[v] = 0;
				return true;
			}
		}
		colors[v] = 0;
		return false;
	} 
	private boolean isSafe(int v, int c, int[]colors, Node[] adjList) {
		Node nodeV = adjList[v];
		for(int i = 0; i < nodeV.size();i++) {
			int u = ((Node)(nodeV.get(i))).i;
			if(colors[u] == c) {
				return false;
			}
		}
		return true;
		
	}

}
