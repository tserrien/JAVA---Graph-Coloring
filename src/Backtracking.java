import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class Backtracking {
	public static void start(int n, int k, LinkedList<Integer>[] adjList, int[][] matrix) {
		ArrayList<ArrayList<Integer>> subGs = Util.findSubGraphs(n, adjList);
		if(Config.DEBUG) {
			System.out.println("total subgraphs: " + subGs.size());
			for(int i = 0; i < subGs.size(); i++) {
				System.out.println("subgraph " + (i + 1) + "(length = " + subGs.get(i).size() + "): ");
				System.out.println(subGs.get(i));			
			}			
		}
		boolean feasible = true;
		while(feasible) {
			int[] colors = new int[n];
			for(int i = 0; i < subGs.size(); i++) {
				ArrayList subG = subGs.get(i);
				if(!btk(n, k, adjList, subG, colors)) {
					feasible = false;
					if(Config.DEBUG) {
						System.out.println("not feasible with " + k + " colors." );						
					}
					break;
				}
//			while(coloring(adjList, k, colors, 0, ordering)) {
//				colors = new int[n];
//				k--;
//			}
			}
			if(feasible) {
				for(int i = 0; i < colors.length; i++) {
					if(colors[i] == 0) {
						for(int c = 1; c <= k; c++) {
							if(isSafe(i,c,colors, adjList)) {
								colors[i] = c;
							}
						}
					}
				}
				
				boolean correct = Util.checkColoring(colors, matrix);
				if(Config.DEBUG) {
					Util.printResult(k, colors);
					System.out.println("check correct: " + correct);					
				}
				k--;				
			}
		}
		System.out.println("CHROMATIC NUMBER = " + (k + 1));
	}
	public static boolean btk(int n, int k, LinkedList<Integer>[] adjList, ArrayList<Integer> subG, int[] colors) {
		if(Config.DEBUG) {
			System.out.println("Start backtracking from " + k + " colors:");
			System.out.println("subgraph size: " + subG.size());			
		}
		Integer[] ordering = genOrdering(k, adjList, subG);
//		degDescent
		return coloring(adjList, k, colors, 0, ordering);
//		ordering = genOrdering(k, adjList, subG);
	}
	
	public static Integer[] genOrdering(int k, LinkedList<Integer>[] adjList, ArrayList<Integer> subG) {
		//deg>= k array list 
		ArrayList<Integer> maxDeg = new ArrayList();
		//deg>= k array index list
		ArrayList<Integer> maxDegIndex = new ArrayList();
		
		for(int i = 0; i < subG.size(); i++) {
			int u = subG.get(i);
			if(adjList[u].size() >= k) {
				maxDeg.add(adjList[u].size());
				maxDegIndex.add(u);
			}
		}
		int[] _maxDegArr = new int[maxDeg.size()];
		for(int i = 0; i < maxDeg.size(); i++) {
			_maxDegArr[i] = maxDeg.get(i);
		}
		
		ArrayIndexComparator comp = new ArrayIndexComparator(_maxDegArr);
		Integer[] index = comp.createIndexArray(_maxDegArr.length);
		Arrays.sort(index, comp);
		Integer[] ordering = new Integer[index.length];
		for(int i = 0; i < index.length; i++) {
			ordering[i] = maxDegIndex.get(index[i]);
		}
		if(Config.DEBUG) {
			System.out.println("ordering (deg >= " + k + "), (length = " + ordering.length + "): ");			
			for(int i = 0; i < ordering.length; i++)
				System.out.print(ordering[i] + " ");
			System.out.println();
		}
		return ordering;
	}

	public static boolean isSafe(int i, int c, int[] colors, LinkedList<Integer>[] adjList) {
		LinkedList<Integer> list = adjList[i];
		for(int j = 0; j < list.size(); j++) {
			if(colors[list.get(j)] == c) {
				return false;
			}
		}
		return true;
	}
	public static boolean coloring(LinkedList<Integer>[] adjList, int k, int[] colors, int i, Integer[] ordering) {
		if(i == ordering.length) {
			return true;
		}
		int v = ordering[i];
		for(int c = 1; c <= k; c++) {
			if(isSafe(v, c, colors, adjList)) {
				colors[v] = c;
				if(coloring(adjList, k, colors, i+1, ordering)) {
					return true;
				}
				colors[v] = 0;
			}
		}
		return false;
	}

}

class ArrayIndexComparator implements Comparator<Integer>{
	private int[] array;
	public ArrayIndexComparator(int[] array) {
		this.array = array;
	}
	public Integer[] createIndexArray(int n) {
		Integer[] ret = new Integer[n];
		for(int i = 0; i < n; i++) {
			ret[i] = i;
		}
		return ret;
	}
	@Override
	public int compare(Integer o1, Integer o2) {
		// TODO Auto-generated method stub
//		System.out.println(array[o1]);
//		System.out.println(array[o2]);
//		System.out.println(o2);
		return array[o2] - array[o1];
	}
	
}