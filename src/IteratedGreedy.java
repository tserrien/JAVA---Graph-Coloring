/**
 * Iterated greedy coloring algorithm. Currently not used for the project.
 * Documentation will NOT be completed, as it is not my work.
 * @author Huang "Benny" Yiping
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class IteratedGreedy implements Runnable{
	public static ArrayList<ArrayList<Integer>> colorSet;
	public static int maxLoop = 1000;
	public static int k;
	private int n;
	private int m;
	private ColEdge[] e;
	private LinkedList<Integer>[] adjList;
	public IteratedGreedy(int n, int m, ColEdge[] e, int _k, LinkedList<Integer>[] adjList, ArrayList<ArrayList<Integer>> _colorSet) {
		colorSet = _colorSet;
		k = _k;
		this.n = n;
		this.m = m;
		this.e = e;
		this.adjList = adjList;
	}

	@Override
	public void run() {
		int loop = 0;
		while(loop < maxLoop){
			int[] ordering = genOrdering(n, colorSet);
			Greedy gr = new Greedy(n, m, e, k, adjList, ordering);
			if(gr.solution.size() < k) {
				if(Config.DEBUG) {
					System.out.println("Greedy: ");
					Util.printResult(gr.solution.size(), gr.colors);
				}
				k = gr.solution.size();
//				k = gr.up - 1;
			}
			colorSet = gr.solution;
			loop++;
		}
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

	public static void largestFirst(ArrayList<ArrayList<Integer>> colorSet) {
		if(Config.DEBUG)
			System.out.println("in largest first ordering");
		Collections.sort(colorSet, new Comparator<ArrayList>() {
			@Override
			public int compare(ArrayList o1, ArrayList o2) {
				// TODO Auto-generated method stub
				int n = o2.size() - o1.size();
				if(n != 0) {
					return n;
				}
				else {
					return -1;
				}
			}
		});
	}

	public static int[] genOrdering(int n, ArrayList<ArrayList<Integer>> colorSet) {
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
}
