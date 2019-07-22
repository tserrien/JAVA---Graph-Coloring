package algorithm;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.text.html.HTMLDocument.Iterator;

public class WelshPower {
	public static int[] colors;
	public static int c;
	public static ArrayList<ArrayList<Integer>> colorSet;
	public WelshPower(ArrayList<Integer> order, int maxDeg, int[][] matrix) {
		colorSet = new ArrayList<ArrayList<Integer>>();
		colors  = new int[matrix.length];
		ArrayList list = new ArrayList();
		for(int i = 0; i < order.size(); i++) {
			list.add(order.get(i));
		}
		
		c = 1; 
		java.util.Iterator iter = list.iterator();
		while(iter.hasNext()) {
			ArrayList<Integer> coloredList = new ArrayList(); 
			int index = (int)iter.next();
			colors[index] = c;
			coloredList.add(index);
			while(iter.hasNext()) {
				int v = (int) iter.next();
				if(notConnected(v, coloredList, matrix)) {
					colors[v] = c;
					coloredList.add(v);
				}
			}
			
			for(Integer v: coloredList) {
				list.remove(v);
			}
			iter = list.iterator();
			colorSet.add(coloredList);
			if(Config.DEBUG) {
				System.out.println("indepedent color set:");
				System.out.println(coloredList);				
			}
			c++;
		}
		c--;
		
	}
	
	public static boolean notConnected(int v, ArrayList<Integer> list, int[][] matrix) {
		boolean notConnected = true;
		for(int i = 0; i < list.size(); i++) {
			int u = list.get(i);
			if(matrix[v][u] == 1) {
				notConnected = false;
				break;
			}
		}
		return notConnected;
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
		
		System.out.println("Deg descent order: ");
		for(int i = 0; i < degDescent.size(); i++) {
			int index = degDescent.get(i);
			System.out.print("(" +  + index + ": ");
			System.out.print(adjList[index].size() + "), ");
		}
		System.out.println();
		System.out.println("Max degree: " + maxDegree);
		System.out.println("start WelshPower");
		WelshPower wp = new WelshPower(degDescent, maxDegree, matrix);
		System.out.println("Welsh Power result: ");
		Util.printResult(wp.c, wp.colors);
		int k = wp.colorSet.size();
		

	}

}
