import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class RLF implements Runnable {
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
	}

	@Override
	public void run() {
		while(!X.isEmpty()) {
			Si = new ArrayList<Integer>();
			while(!X.isEmpty()) {
				int v = X.get(0);
				Si.add(v);
				updateY(v, matrix);
				updateX(v);
				sortX();
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
}
