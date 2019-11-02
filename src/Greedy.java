/**
 * Greedy coloring algorithm. Currently not used for the project.
 * Documentation will NOT be completed, as it is not my work.
 * @author Huang "Benny" Yiping
 */

import java.util.ArrayList;
import java.util.LinkedList;

public class Greedy {
	public static ArrayList<ArrayList<Integer>> solution;
	public int[] colors;
	public Greedy(int n, int m, ColEdge[] e, int k, LinkedList<Integer>[] adjList, int[] ordering) {
		colors = new int[n];
		solution = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < ordering.length; i++) {
			int index = ordering[i];
			for(int c = 1; c <= k; c++) {
				if(isSafe(index, c, colors, adjList)) {
					colors[index] = c;
					if(solution.size() >= c) {
						solution.get(c - 1).add(index);
					} else {
						ArrayList<Integer>list = new ArrayList();
						list.add(index);
						solution.add(list);
					}
					break;
				}
			}
		}
			
	}
	
	public static void printResult(int[] colors) {
		for(int i = 0; i < colors.length; i++) {
			System.out.print(colors[i] + " ");
			if((i + 1) % 20 == 0) {
				System.out.print("\n");
			}
		}
		System.out.println();
	}
	
	public static boolean isSafe(int v, int c, int[] colors, LinkedList<Integer>[] adjList) {
		LinkedList<Integer>list = adjList[v];
		for(int j = 0; j < list.size(); j++) {
			int u = list.get(j);
			if(colors[u] == c) {
				return false;
			}
		}
		return true;
	}
		
	public static ArrayList countClashing(int n, int[][] solution, ColEdge[] e) {
		//count clashing
		ArrayList clashing = new ArrayList();
		
		for(int i = 0; i < e.length; i++) {
			int u = e[i].u - 1;
			int v = e[i].v - 1;
			for(int j = 0; j < solution.length; j++) {
				if(solution[j][u] == 1 && solution[j][v] == 1) {
					if(!clashing.contains(u))
						clashing.add(u);
					if(!clashing.contains(v))
						clashing.add(v);
					break;
				}
			}
		}
		return clashing;
	}

}
