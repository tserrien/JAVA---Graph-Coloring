package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class IteratedGreedy{
	public static ArrayList<ArrayList<Integer>> colorSet;
	public static int maxLoop = 1000;
	public static int k;
	public static int[] seq;
	public IteratedGreedy(int n, int m, ColEdge[] e, int _k, LinkedList<Integer>[] adjList, ArrayList<ArrayList<Integer>> _colorSet) {
		colorSet = _colorSet;
		int loop = 0;
		k = _k;
		int[] ordering = genOrdering(n, colorSet);
		seq = ordering;
		while(loop < maxLoop){
			Greedy gr = new Greedy(n, m, e, k, adjList, ordering);
			if(gr.solution.size() < k) {
				if(Config.DEBUG) {
					System.out.println("Greedy: ");
					Util.printResult(gr.solution.size(), gr.colors);					
				}
				seq = ordering;
				k = gr.solution.size();
//				k = gr.up - 1;
			}
			colorSet = gr.solution;
			ordering = genOrdering(n, colorSet);
			loop++;
		}
	}
	
	public IteratedGreedy(int n, int m, ColEdge[] e, int _k, LinkedList<Integer>[] adjList, ArrayList<ArrayList<Integer>> _colorSet, boolean randomMode) {
		colorSet = _colorSet;
		int loop = 0;
		k = _k;
		int[] ordering = genOrdering(n, colorSet);
		seq = ordering;
		while(loop < maxLoop){
			Greedy gr = new Greedy(n, m, e, k, adjList, ordering);
			if(gr.solution.size() < k) {
				if(Config.DEBUG) {
					System.out.println("Greedy: ");
					Util.printResult(gr.solution.size(), gr.colors);					
				}
				seq = ordering;
				k = gr.solution.size();
//				k = gr.up - 1;
			}
			colorSet = gr.solution;
			ordering = randomWalk(n);
			loop++;
		}
	}
	
	public static IteratedGreedy Random(int n, int m, ColEdge[] e, int _k, LinkedList<Integer>[] adjList, ArrayList<ArrayList<Integer>> _colorSet) {
		return new IteratedGreedy(n, m, e, _k, adjList, _colorSet, true);
	}
	
	public static int[] randomWalk(int n) {
		int[] order = Util.createOrderingArray(n);
		Util.shuffle(order);
		return order;
	}
	
	public static void reverse(ArrayList<ArrayList<Integer>> colorSet) {
		if(Config.DEBUG)
			System.out.println("in reverse ordering");
		Collections.reverse(colorSet);
	}
	public static void random(ArrayList<ArrayList<Integer>> colorSet) {
		if(Config.DEBUG)
			System.out.println("in random ordering");
		Collections.shuffle(colorSet);
	}
	//when tie on size, break by reverse order
	public static void largestFirst(ArrayList<ArrayList<Integer>> colorSet) {
		if(Config.DEBUG)
			System.out.println("in largest first ordering");
		reverse(colorSet);
		Collections.sort(colorSet, new Comparator<ArrayList>() {
			@Override
			public int compare(ArrayList o1, ArrayList o2) {
				// TODO Auto-generated method stub
				return o2.size() - o1.size();
			}
		});
	}
	public static int[] genOrdering(int n, ArrayList<ArrayList<Integer>> colorSet) {
		//ratio selection: 50:50:30
		int largest = 50;
		int reverse = largest + 50;
		int random = reverse + 30;
		int[] ordering = new int[n];
		int p = (int)(Math.random() * random);
		if(p < largest) largestFirst(colorSet);
		else if(p < reverse) reverse(colorSet);
		else random(colorSet);
		if(Config.DEBUG) 
			System.out.println(colorSet);
		int cnt = 0;
		for(int i = 0; i < colorSet.size(); i++) {
			ArrayList<Integer> s = colorSet.get(i);
			for(int j = 0; j < s.size(); j++) {
				ordering[cnt] = s.get(j);
				cnt++;
			}
		}
		return ordering;
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
//		ArrayList list1 = new ArrayList();
//		list1.add(2);
//		list1.add(3);
//		ArrayList list2 = new ArrayList();
//		list2.add(1);
//		list2.add(5);
//		list2.add(6);
//		ArrayList list3 = new ArrayList();
//		list3.add(4);
//		list3.add(7);
//		list.add(list1);
//		list.add(list2);
//		list.add(list3);
//		IteratedGreedy.reverse(list);
//		System.out.println(list);
//		IteratedGreedy.random(list);
//		System.out.println(list);
//		IteratedGreedy.largestFirst(list);
//		System.out.println(list);
//		IteratedGreedy.genOrdering(7,list);
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
		int[] ordering = new int[n];
		for(int i = 0; i < n; i++) {
			ordering[i] = i;
		}
		Greedy gr = new Greedy(n, m, e, n, adjList, ordering);
		
		IteratedGreedy ig = new IteratedGreedy(n, m, e, n, adjList, gr.solution);
		System.out.println("----------");
		System.out.println(ig.colorSet.size());
		
		

	}

}
