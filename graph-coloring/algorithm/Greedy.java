package algorithm;

import java.util.ArrayList;
import java.util.LinkedList;

public class Greedy {
	public static ArrayList<ArrayList<Integer>> solution;
	public int[] colors;
	public Greedy(int n, int m, ColEdge[] e, int k, LinkedList<Integer>[] adjList, int[] ordering) {
		colors = new int[n];
		solution = new ArrayList<ArrayList<Integer>>();
//		System.out.println(Util.listFromArray(ordering));
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
//		System.out.println("up: " + up);
		
//		System.out.println("Complete: " + complete + ", use color: " + up);
		
//		for(int i = 0; i < solution.length; i++) {
//			for(int j = 0; j < solution[0].length; j++) {
//				System.out.print(solution[i][j] + " ");
//			}
//			System.out.println();
//		}
			
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
	
//	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		if (args.length < 1) {
//			System.out.println("Error! No filename specified.");
//			System.exit(0);
//		}
//
//		String inputfile = args[0];
//		ColEdge[] e = readGraph.readFile(inputfile);
//		
//		int n = readGraph.n;
//		int m = readGraph.m;
//		
//		int[][] matrix = Util.adjacencyMatrix(readGraph.n, e);
//		
//		
//		//fill rest for improper k coloring
//		Greedy gr;
//		int k = n;
//		do {
//			gr = new Greedy(n, m, e, k, matrix);
//			k = gr.up - 1;
//		}while(gr.complete); 
//		
//		k = gr.up;
//		
//		int[][] solution = gr.solution;
//		
//		for(int i = 0; i < solution[0].length; i++) {
//			boolean notFilled = true; 
//			for(int j = 0; j < solution.length; j++) {
//				if(solution[j][i] == 1) {
//					notFilled = false;				
//					break;
//				}
//			}
//			if(notFilled) {
//				int c = (int)Math.random() * solution.length;
//				solution[c][i] = 1;
//			}
//		}
//		
////		System.out.println("filled solution: C(k,n)");
////		for(int i = 0; i < solution.length; i++) {
////			for(int j = 0; j < solution[0].length; j++) {
////				System.out.print(solution[i][j] + " ");
////			}
////			System.out.println();
////		}
//		
//		
//
////		System.out.println("transposed: C(n,k)");
////		int[][] transpose = new int[readGraph.n][k];
////		for(int i = 0; i < solution.length; i++) {
////			for(int j = 0; j < solution[0].length; j++) {
////				transpose[j][i] = solution[i][j];
////			}
////		}
////		for(int i = 0; i < transpose.length; i++) {
////			for(int j = 0; j < transpose[0].length; j++) {
////				System.out.print(transpose[i][j] +  " ");
////			}
////			System.out.println();
////		}
////		
//		//calculate conf;
//		int conf = Util.countConflicts(gr.solution, e);
//		
//		System.out.println("initial conf: " + conf);
//		
//		
//		//build N(n,c)
//		int[][] colNeighbours = new int[n][k];
//		for(int i = 0; i < matrix.length; i++) {
//			for(int j = 0; j < matrix[0].length; j++) {
//				if(matrix[i][j] == 1) {
//					//find color for j
//					int c = 0;
//					for(c = 0; c < solution.length; c++) {
//						if(solution[c][j] == 1) {
//							break;
//						}
//					}
//					//update ith neighbours which are colored c
//					colNeighbours[i][c]++;
//				}
//			}
//		}
//		
////		System.out.println("ColNeighbours: N(n, c)");
////		for(int i = 0; i < colNeighbours.length; i++) {
////			for(int j = 0; j < colNeighbours[0].length; j++) {
////				System.out.print(colNeighbours[i][j] + " ");
////			}
////			System.out.println();
////		}
//		
////		for(int i = 0; i < e.length; i++) {
////			int u = e[i].u - 1;
////			int v = e[i].v - 1;
////			for(int i )
////		}
//		
//		
//		//improve solution
//		
//		int fs = conf;
//		boolean improved;
//		int[][] tabu = new int[n][k];
//		int tenure = 3;
//		int loop = 0;
//		double t = 0.9;
//		int z = 5;
//		double a = 0.8;
//		do {
//			int ii = 0;
//			int jj = 0;
//			int v = 0;
//			int _fs = fs;
//			int NSmin;
//			ArrayList<Integer> clashing = countClashing(n, solution, e);
//			System.out.println("clashing vertices: " + clashing);
//			for(int i = 0; i < clashing.size(); i++) {
//				//incumbent color for clashing node
//				int index = (int)(Math.random() * n);
//				int c = 0;
//				for(; c < k; c++) {
//					if(solution[c][index] == 1) {
//						break;
//					} 
//				}
//				
//				int cc = (int)(Math.random() * k);
//				int[][] s2 = solution.clone();
//				
//				if(cc != c) {
//					s2[c][index] = 0;
//					s2[cc][index] = 1;
//					int _conf = Util.countConflicts(s2, e);
//					if(_conf < conf) {
//						conf = _conf;
//						solution = s2;
//					}
//					double p = Math.exp(Math.abs(_conf - conf) / t);
//					System.out.println(p);
//					if(Math.random() <= p) {
//						solution = s2;
//					}
//					if(loop % z == 0) {
//						t = a * z;
//					}
//				}
//			}
//			System.out.println(conf);
//			loop++;
//		} while(loop < 100);
//		
//		
//		System.out.println("check final conf: " + Util.countConflicts(solution, e));
////		System.out.println("\nlocal optimized solution: ");
////		for(int i = 0; i < solution.length; i++) {
////			for(int j = 0; j < solution[0].length; j++) {
////				System.out.print(solution[i][j] + " ");				
////			}
////			System.out.println();
////		}
//		
//		
//		
//		}
	
		
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
//		System.out.println("clashing vertices: ");
//		for(int i = 0; i < clashing.length; i++) {
//			System.out.print(clashing[i] + " ");
//		}
		return clashing;
	}

}
