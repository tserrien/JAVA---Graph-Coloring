import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Util {
	
	public static int max(int [] arr) {
		int _max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if(arr[i] != -1)
				_max = Math.max(arr[i], _max);
			else
				break;
		}
		return _max;
	}
	
	public static int min(int [] arr) {
		int _min = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] != -1)
				_min = Math.min(arr[i], _min);
			else
				break;
		}
		return _min;
	}
	
	public static int countConflicts(int[][] solution, ColEdge[] e) {
		int conf = 0;
		for(int i = 0; i < e.length; i++) {
			int u = e[i].u - 1;
			int v = e[i].v - 1;
			for(int j = 0; j < solution.length; j++) {
				if(solution[j][u] == 1 && solution[j][v] == 1) {
					conf++;
					break;
				}
			}
		}
		return conf;
	} 
	
	public static void shuffle(int [] arr) {
		Random rnd = new Random();
		int index;
		int temp;
		for(int i = arr.length - 1; i > 0; i--) {
			index = rnd.nextInt(i + 1);
			if(index != i) {
				temp = arr[i];
				arr[i] = arr[index];
				arr[index] = temp;
			}
		}
	}
	
	public static int[] createOrderingArray(int n) {
		int[] arr = new int[n];
		for(int i = 0; i < arr.length; i++) {
			arr[i] = i;
		}
		shuffle(arr);
		return arr;
	}
	
	public static ArrayList listFromArray(int[] arr) {
		ArrayList al = new ArrayList();
		for(int i = 0; i < arr.length; i++) {
			al.add(arr[i]);
		}
		return al;
	}

	public static void adjacencyMatrix(int n, ArrayList<Integer> g) {
		for(int i = 0; i < g.size(); i++) {
			g.get(i);
		}
	}
	
	public static int[][] adjacencyMatrix(int n, ColEdge[] e) {
		int[][] result = new int[n][n];
		for(int i = 0; i < e.length; i++) {
			int u = e[i].u - 1;
			int v = e[i].v - 1;
			result[u][v] = 1;
			result[v][u] = 1;
		}
		return result;
	}
	
	public static void printResult(int k, int[] colors) {
		System.out.println("feasible with " + k + " colors.");
		for(int i = 0; i < colors.length; i++) {
			System.out.print(colors[i] + " ");
			if((i + 1) % 20 == 0) {
				System.out.print("\n");
			}
		}
		System.out.println();
	}
	
	
	public static boolean isTree(int n, LinkedList<Integer>[] adjList) {
		boolean[] visited = new boolean[n]; 
		return !isCycle(0, adjList, visited, -1);
	}
	
	public static LinkedList<Integer>[] adjList(int n, ColEdge[] e) {
		LinkedList<Integer>[] list = new LinkedList[n];
		for(int i = 0; i < n; i++) {
			list[i] = new LinkedList();
		}
		for(int i = 0; i < e.length; i++) {
			int u = e[i].u - 1;
			int v = e[i].v - 1;
			list[u].add(v);
			list[v].add(u);
		}
		return list;
	}
	
	
	
	public static ArrayList<ArrayList<Integer>> findSubGraphs(int n, LinkedList<Integer>[] adjList) {
		ArrayList subGs = new ArrayList();
		boolean[] visited = new boolean[n];
		for(int i = 0; i < adjList.length; i++) {
			if(!visited[i]) {
				visited[i] = true;
				ArrayList list = new ArrayList();
				subGs.add(list);
				list.add(i);
				for(int j = 0; j < adjList[i].size(); j++) {
					int u = adjList[i].get(j);
					if(!visited[u]) {
						dfs(u, visited, adjList, list);						
					}
				}
			}
		}
		return subGs;
	}
	public static void dfs(int v, boolean[] visited, LinkedList<Integer>[] adjList, ArrayList subG) {
		visited[v] = true;
		subG.add(v);
		for(int i = 0; i < adjList[v].size(); i++) {
			int u = adjList[v].get(i);
			if(!visited[u]) {
				dfs(u, visited, adjList, subG);
			}
		}
	} 
	
	public static boolean isCycle(int v,LinkedList<Integer>[] adjList, boolean[] visited, int parent) {
		visited[v] = true;
		Iterator<Integer> iter = adjList[v].iterator();
		while(iter.hasNext()) {
			int u = iter.next();
			if(!visited[u]) {
				if(isCycle(u, adjList, visited, v)) {
					return true;
				}
			} else if(u != parent) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkColoring(int[] colors, int[][] matrix) {
		for(int v = 0; v < matrix.length; v++) {
			for(int u = 0; u < matrix.length; u++) {
				if(matrix[v][u] == 1 && colors[v] == colors[u]) {
					return false;
				}				
			}
		}
		return true;
	}
	
	public static ArrayList<Integer> genOrdering(int n) {
		ArrayList<Integer> ordering = new ArrayList<Integer>();
		for(int i = 0; i < n; i++) {
			ordering.add(i);
		}
		return ordering;
	}
	
	public static void printOrderAndDegree(ArrayList<Integer> degDescent, LinkedList[] adjList) {
		System.out.println("Deg descent order: ");
		for(int i = 0; i < degDescent.size(); i++) {
			int index = degDescent.get(i);
			System.out.print("(" + index + ": ");
			System.out.print(adjList[index].size() + "), ");
		}
		System.out.println();
	}
	public static void printOrderAndSaturation(ArrayList<Integer> ordering, Node[] adjList) {
		System.out.println("Saturation descent order: ");
		for(int i = 0; i < ordering.size(); i++) {
			int index = ordering.get(i);
			System.out.print("(" + index + ": ");
			System.out.print(adjList[index].s + "), ");
		}
		System.out.println();
	}

	/**
	 * Utility method to get the extension of the graph.
	 *
	 * @param file
	 * @return Returns the extension of the file, eg. txt, col, iso.
	 */
	public static String getFileExtension(File file) {
		String fileName = file.getName();
		if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".")+1);
		else return "";
	}
}

