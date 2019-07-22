package algorithm;

import algorithm.MaxClique.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class CompareMany {

	public static void main(String[] args) {
		try {
			compare(getList(args), true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void compare(String[] paths, boolean writeToFile) throws IOException {

		try {
			//PrintWriter writer = new PrintWriter("data_to_plot_40graphs.csv");
			//PrintWriter writer = new PrintWriter("data_to_plot_dimacs.csv");
			File file = new File("40graph_tests.csv");
			FileWriter writer = new FileWriter(file);
			writer.write("Graph name,nodes,edges,density,DSATUR,DSATUR_time,RLF,RLF_time,IGR2,IGR2_time," +
					"IGR3,IGR3_time,IGR,IGR_time,Hoffman,Hoffman_time,BronKerboshHeapsort,BronKerboshHeapsort_time,BronKerboschCLQ," +
                    "BronKerboschCLQ_time, Backtracking, Backtracking_time\n");


			long start;
			long end;

			for (int p = 0; p < paths.length; p++) {
				//for(int i = 0; i < 2; i++){		//debug constraint

				String inputfile = paths[p];
				ColEdge[] e = readGraph.readFile(inputfile);

				int k;
				int n = readGraph.n;
				int m = readGraph.m;
				int[][] matrix = Util.adjacencyMatrix(readGraph.n, e);
				LinkedList<Integer>[] adjList = Util.adjList(n, e);

				int maxDegree = 0;

				final int[] d = new int[n];

				System.out.println(paths[p]);
				if (writeToFile) {
					writer.write(paths[p] + "," + n + "," + m + ",");
					writer.flush();
				}

				for (int i = 0; i < matrix.length; i++) {
					for (int j = 0; j < matrix[0].length; j++) {
						if (matrix[i][j] == 1) {
							d[i]++;
						}
					}
					if (d[i] > maxDegree) {
						maxDegree = d[i];
					}
				}

				ArrayList<Integer> degDescent = new ArrayList<Integer>();
				for (int i = 0; i < n; i++) {
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

				if (Config.DEBUG) {
					Util.printOrderAndDegree(degDescent, adjList);
					System.out.println("Max degree: " + maxDegree);
				}

				double density = (double) m / (n * (n - 1) / 2); // Calculate density of the graph

				System.out.printf("Density: %.5f \n", density);

				if (writeToFile) {
					writer.write(density + ",");
					writer.flush();
				}

				// ------------------------------DSATUR-------------------------------------

				start = System.nanoTime();
				DSatur dst = new DSatur(n, maxDegree + 1, adjList, degDescent);
				System.out.println("NEW BEST UPPER BOUND = " + dst.solution.size());
				if (dst.solution.size() == 2) {
					System.out.println("CHROMATIC NUMBER = 2");
					return;
				}

				end = System.nanoTime();
				if (writeToFile) {
					writer.write(dst.solution.size() + ",");
					writer.flush();
				}
				timePrinter(start, end, writeToFile, writer);

				// ------------------------------------------RLF--------------------------------------------------

				start = System.nanoTime();
				RLF rlf = new RLF(n, adjList, matrix, degDescent);
				System.out.println("NEW BEST UPPER BOUND = " + rlf.S.size());
				if (rlf.S.size() == 2) {
					System.out.println("CHROMATIC NUMBER = 2");
					return;
				}
				end = System.nanoTime();
				if (writeToFile) {
					writer.write(rlf.S.size() + ",");
					writer.flush();
				}
				timePrinter(start, end, writeToFile, writer);

				k = Math.min(dst.solution.size(), rlf.S.size());

				// -------------------------------------------IGR2-------------------------------------------------

				start = System.nanoTime();
				IteratedGreedy igr2 = new IteratedGreedy(n, m, e, dst.solution.size(), adjList, DSatur.solution);
				System.out.println("NEW BEST UPPER BOUND = " + igr2.k);
				end = System.nanoTime();
				if (writeToFile) {
					writer.write(igr2.k + ",");
					writer.flush();
				}
				timePrinter(start, end, writeToFile, writer);

				// -------------------------------------------IGR3-------------------------------------------------

				start = System.nanoTime();
				IteratedGreedy igr3 = new IteratedGreedy(n, m, e, rlf.S.size(), adjList, rlf.S);
				System.out.println("NEW BEST UPPER BOUND = " + igr3.k);
				end = System.nanoTime();
				if (writeToFile) {
					writer.write(igr3.k + ",");
					writer.flush();
				}
				timePrinter(start, end, writeToFile, writer);

				// --------------------------------------------IGR-------------------------------------------------

				start = System.nanoTime();
				IteratedGreedy best = Util.optimized(new IteratedGreedy[]{igr2, igr3});
				if (writeToFile) {
					writer.write(best.colorSet.size() + ",");
					writer.flush();
				}
				end = System.nanoTime();
				timePrinter(start, end, writeToFile, writer);

				igr2 = null;
				igr3 = null;
				k = best.colorSet.size();

				// -------------------------------------------Hoffman----------------------------------------------

				start = System.nanoTime();
				HoffmansLB hoffLB = new HoffmansLB(n, m, e);
				Thread hoffThread = new Thread(hoffLB);
				hoffThread.start();
				try {
					hoffThread.join(60000); // terminate after at most 30 seconds
				} catch (Exception ex) {
					System.out.println("Hoffman's LB thread interrupted.");
				}
				end = System.nanoTime();
				int lb = hoffLB.getLowerBound();
				if (writeToFile) {
					if (lb != 0) {
						writer.write(lb + ",");
						writer.flush();
					} else {
						writer.write( "X,");
						writer.flush();
					}
				}
				timePrinter(start, end, writeToFile, writer);

				// ------------------------------Bron Kerbosch with heapsort-------------------------------------

				start = System.nanoTime();
				LowerBoundHeap lbh = new LowerBoundHeap(n, e, matrix);
				Thread lbhThread = new Thread(lbh);
				lbhThread.start();
				try {
					lbhThread.join(60000); // terminate after at most 15 seconds
				} catch (Exception ex) {
					System.out.println("Bron Kerbosch's LB thread interrupted.");
				}

				end = System.nanoTime();
				if (writeToFile) {
					writer.write( lbh.getLowerBound() + ",");
					writer.flush();
				}
				timePrinter(start, end, writeToFile, writer);

				// ----------------------------------------Benny's BK--------------------------------------------

				start = System.nanoTime();
                MaxClique ff = new MaxClique(n, k, e);

				end = System.nanoTime();
				if (writeToFile) {
					writer.write( ff.maxClique + ",");
					writer.flush();
				}
				timePrinter(start, end, writeToFile, writer);

				ff.graph.removeAll(ff.clique);

				ArrayList cliqueOrder = new ArrayList();
				ff.clique.sort(new Comparator<Vertex>() {
					public int compare(Vertex o1, Vertex o2) {
						return o2.degree - o1.degree;
					}
				});

				for (int i = 0; i < ff.clique.size(); i++) {
					cliqueOrder.add(ff.clique.get(i).x);
				}

				ArrayList _order = new ArrayList();
				int[] colors = new int[n];
				for (int c = 1; c <= ff.clique.size(); c++) {
					colors[ff.clique.get(c - 1).x] = c;
					_order.add(ff.clique.get(c - 1).x);
				}

				for (int i = 0; i < ff.graph.size(); i++) {
					_order.add(ff.graph.get(i).x);
				}

				// ----------------------------------------Backtracking------------------------------------------

				// DON'T FORGET TO TAKE OUT THE WRITERS FROM BACKTRACKING AFTER

				start = System.nanoTime();
				new Backtracking3(n, k - 1, ff.maxClique, matrix, Util.adjListNode(n, e), _order, cliqueOrder, colors, writeToFile, writer);

				end = System.nanoTime();
				timePrinter(start, end, writeToFile, writer);

				System.out.println();
                System.out.println();

				writer.write("\n");
				writer.flush();
			}

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method for outputting runtime easily.
	 *
	 * @param start Start of the execution (in nanoseconds).
	 * @param end End of the execution (in nanoseconds).
	 */
	public static void timePrinter(long start, long end, boolean writeToFile, FileWriter writer) throws IOException{
		long time = end - start;
		System.out.println("Runtime: " + time + " nanosec");

		if (writeToFile) {
			writer.write(time + ",");
			writer.flush();
		}
		//writer.println("Runtime: " + time + " nanosec");

		time = (long) (time / 1000000);//conversion from nanosec to milisec
		byte hour = (byte)((time/1000/60/60)%60);
		byte minute = (byte) (((time/1000/60) - (hour*60))%60);
		byte second = (byte) ((time/1000) - (minute*60));
		int mili = (int) ((time) - (second*1000));
		System.out.println(hour + ":" + minute + ":" + second + "." + mili);
	}

	/**
	 * Method to store all of the paths of graphs to compare.
	 *
	 * @param a User input as String array.
	 * @return String array that holds the paths of all graphs to check.
	 */
	private static String[] getList(String[] a){

		//File[] files = new File("../Graphs/").listFiles();
		//File[] files = new File("../Graphs/graph_color").listFiles();
		File[] files = new File("c:/final/Graphs/").listFiles();

		//files = null; // debug

		if (files != null) {
			ArrayList<String> graphList = new ArrayList<>();
			for (File file: files) {
				if (getFileExtension(file).equals("txt") || getFileExtension(file).equals("col"))
					graphList.add(file.getPath());
			}
			String[] graphArr = new String[graphList.size()];
			graphArr = graphList.toArray(graphArr);
			return graphArr;
		}

		// Shouldn't arrive here
		return null;
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
