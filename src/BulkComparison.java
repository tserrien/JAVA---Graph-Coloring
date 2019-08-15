import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Class to easily compare accuracy and runtime of
 * different lower and upper bound algorithms on bulks of graphs.
 */
public class BulkComparison {

    public static void main(String[] args){
        //compare(getList(args), false);

        String[] asd = new String[1];
        asd[0] = "Graphs/graph_color/homer.col";
        compare(asd, false);
    }

    /**
     * Method that compares the results of different graph colouring algorithms.
     *
     * The comparing works as follows. Graph is read, if the graph is completely disconnected, the chromatic
     * number (CN) is 1, if the graph is fully connected, CN = number of nodes, then graph is checked
     * if it's a bipartite graph, because if it is, CN = 2. If none of these cases occurred, the actual algorithms
     * are started in this order: Upper bounds: Welsh Powell, RLF, DSATUR, Iterated Greedy with 3 different parameters;
     * Lower bounds: Bron Kerbosch, Largest Subgraph with heapsort.
     *
     * The algorithms are allowed to run no more than 60 seconds. If they have exceeded this limit, their current
     * best result is taken for comparison.
     *
     * @param paths String array of all the paths of the graphs to compare.
     * @param writeToFile True - write the statistics of this comparison to a file, False - don't write.
     */
    private static void compare(String[] paths, boolean writeToFile){
        try {
            //PrintWriter writer = new PrintWriter("data_to_plot_40graphs.csv");
            PrintWriter writer = new PrintWriter("data_to_plot_dimacs.csv");

            // Data format:
            // Graphname, nodes, edges, density, wp_ub, nanosec_wp, rlf_ub, nanosec_rlf, dst_ub, nanosec_dst, igr3_ub,
            // nanosec_igr3, igr2_ub, nanosec_igr2, igr_ub, nanosec_igr, lb_ben, nanosec_lb_ben, lb_and, nanosec_lb_and

            for (int i = 0; i < paths.length; i++) {
                //for(int i = 0; i < 2; i++){		//debug constraint

                System.out.println("\n" + paths[i]);
                ColEdge[] e = readGraph.readFile(paths[i]);

                int n = readGraph.n;
                int m = readGraph.m;
                int[][] matrix = Util.adjacencyMatrix(readGraph.n, e);
                LinkedList<Integer>[] adjList = Util.adjList(n, e);

                int maxDegree = 0;

                int[] d = new int[n];

                for (int k = 0; k < matrix.length; k++) {
                    for (int j = 0; j < matrix[0].length; j++) {
                        if (matrix[k][j] == 1) {
                            d[k]++;
                        }
                    }
                    if (d[k] > maxDegree) {
                        maxDegree = d[k];
                    }
                }

                ArrayList<Integer> degDescent = new ArrayList<Integer>();
                for (int k = 0; k < n; k++) {
                    degDescent.add(k);
                }
                class DegComparator implements Comparator<Integer> {
                    @Override
                    public int compare(Integer v1, Integer v2) {
                        return d[v2] - d[v1];
                    }
                }
                DegComparator comp = new DegComparator();
                Collections.sort(degDescent, comp);

                if (writeToFile)
                    writer.print(paths[i] + "," + n + "," + m + ",");

                //System.out.println("\n" + paths[i] + "\nDisconnected: " + readGraph.getDisconnected() + "/" + n);
                //if (writeToFile)
                  //  writer.print(readGraph.getDisconnected()+",");

                double density = (double) m / (n * (n - 1) / 2); // Calculate density of the graph

                System.out.printf("Density: %.5f \n", density);

                if (writeToFile)
                    writer.print(density+",");

                // Commence logical checks, afterwards - algorithms
                if (m == 0) {
                    System.out.println("Graph is completely disconnected");
                    if (writeToFile)
                        writer.print(1);
                } else if (density == 1.0) {
                    System.out.println("Graph is fully connected");

                    if (writeToFile)
                        writer.print(n);
                } else if (Bipartite.isBipartite(matrix, n)) {
                    System.out.println("Graph is bipartite");

                    if (writeToFile)
                        writer.print(2);
                } else {
                    long start;
                    long end;

                    start = System.nanoTime();
                    LowerBoundHeap lowerBoundHeap = new LowerBoundHeap(n, e, matrix);
                    Thread lbhThread = new Thread(lowerBoundHeap);
                    lbhThread.start();
                    try {
                        lbhThread.join(3600000); // Stop LowerBoundHeap after at most 1 hour
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (writeToFile) {
                        writer.print(lowerBoundHeap.getLowerBound() + ",");
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writeToFile, writer);
                }
                writer.print("\n");
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
    public static void timePrinter(long start, long end, boolean writeToFile, PrintWriter writer){
        long time = end - start;
        System.out.println("Runtime: " + time + " nanosec");

        if (writeToFile)
            writer.print(time + ",");
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

        //File[] files = new File("C:/compare/Graphs/").listFiles();
        File[] files = new File("C:/compare/Graphs/graph_color").listFiles();

        //files = null; // debug
		String[] graphArr = null;
        if (files != null) {
            ArrayList<String> graphList = new ArrayList<>();
            for (File file: files) {
                if (Util.getFileExtension(file).equals("txt") || Util.getFileExtension(file).equals("col"))
                    graphList.add(file.getPath());
            }
            //String[] graphArr = new String[graphList.size()];
			graphArr = new String[graphList.size()];
            graphArr = graphList.toArray(graphArr);
            //return graphArr;
        }

        // Shouldn't arrive here
        //return null;
		return graphArr;
    }

}

