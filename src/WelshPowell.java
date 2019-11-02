/**
 * Welsh-Powell lower bound algorithm. Currently not used for the project.
 * Documentation will NOT be completed, as it is not my work.
 * @author Paulius Skaisgriris
 */

import java.util.ArrayList;

public class WelshPowell implements Runnable {
	public static int[] colors;
	public static int c;
	public static ArrayList<ArrayList<Integer>> colorSet;
	public ArrayList list;
	private int[][] matrix;

	public WelshPowell(ArrayList<Integer> order, int maxDeg, int[][] matrix) {
		colorSet = new ArrayList<ArrayList<Integer>>();
		colors  = new int[matrix.length];
		list = new ArrayList();
		for(int i = 0; i < order.size(); i++) {
			list.add(order.get(i));
		}
		this.matrix = matrix;
	}

	@Override
	public void run() {
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
}
