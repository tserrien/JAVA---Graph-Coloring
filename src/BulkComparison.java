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
        compare(getList(args), true);

        //String[] asd = new String[1];
        //asd[0] = "Graphs/graph_color/homer.col";
        //compare(asd, true);
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
            PrintWriter writer = new PrintWriter("data_to_plot_40graphs.csv");
            //PrintWriter writer = new PrintWriter("data_to_plot_dimacs.csv");

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

                if (Config.DEBUG) {
                    Util.printOrderAndDegree(degDescent, adjList);
                    System.out.println("Max degree: " + maxDegree);
                }

                if (writeToFile)
                    writer.print(paths[i] + "," + n + "," + m + ",");

                //penalty = 0;

                //System.out.println("\n" + paths[i] + "\nDisconnected: " + readGraph.getDisconnected() + "/" + n);
                //if (writeToFile)
                  //  writer.print(readGraph.getDisconnected()+",");

                double density = (double) m / (n * (n - 1) / 2); // Calculate density of the graph

                System.out.printf("Density: %.5f \n", density);

                if (writeToFile)
                    writer.print(density+",");

                // Commence logical checks, afterwards - algorithms
                if (m == 0) { // Graph is completely disconnected
                    System.out.println("CHROMATIC NUMBER = 1");
                    if (writeToFile)
                        writer.print(1);
                } else if (density == 1.0) { // Graph is fully connected
                    System.out.println("CHROMATIC NUMBER = " + n);

                    if (writeToFile)
                        writer.print(n);
                } else if (Bipartite.isBipartite(matrix, n)) { // Graph is bipartite
                    System.out.println("CHROMATIC NUMBER = 2");

                    if (writeToFile)
                        writer.print(2);
                } else {
                    long start;
                    long end;

                    start = System.nanoTime();
                    // Instantiate Welsh Powell algorithm class and start its thread
                    WelshPowell wp = new WelshPowell(degDescent, maxDegree + 1, matrix);
                    Thread wpThread = new Thread(wp);
                    wpThread.start();
                    try {
                        wpThread.join(3600000); // Stop WP after at most 1 hour
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (writeToFile) {
                        writer.print(wp.colorSet.size() + ",");
                    }

                    end = System.nanoTime();
                    timePrinter(start, end, writeToFile, writer);

                    start = System.nanoTime();
                    // Instantiate RLF algorithm class and start its thread
                    RLF rlf = new RLF(n, adjList, matrix, degDescent);
                    Thread rlfThread = new Thread(rlf);
                    rlfThread.start();
                    try {
                        rlfThread.join(3600000); // Stop RLF after at most 1 hour
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (writeToFile) {
                        writer.print(rlf.S.size() + ",");
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writeToFile, writer);

                    start = System.nanoTime();
                    // Instantiate DSATUR algorithm class and start its thread
                    DSatur dst = new DSatur(n, maxDegree + 1, adjList, degDescent);
                    Thread dstThread = new Thread(dst);
                    dstThread.start();
                    try {
                        dstThread.join(3600000); // Stop DSATUR after at most 1 hour
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (writeToFile) {
                        writer.print(dst.solution.size() + ",");
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writeToFile, writer);

                    start = System.nanoTime();
                    // Instantiate Iterated Greedy algorithm class and start its thread
                    IteratedGreedy igr3 = new IteratedGreedy(n, m, e, rlf.S.size(), adjList, rlf.S);
                    Thread igr3Thread = new Thread(igr3);
                    igr3Thread.start();
                    try {
                        igr3Thread.join(3600000); // Stop Iterated Greedy after at most 1 hour
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (writeToFile) {
                        writer.print(igr3.k + ",");
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writeToFile, writer);

                    start = System.nanoTime();
                    // Instantiate Iterated Greedy algorithm class and start its thread
                    IteratedGreedy igr2 = new IteratedGreedy(n, m, e, dst.solution.size(), adjList, DSatur.solution);
                    Thread igr2Thread = new Thread(igr2);
                    igr2Thread.start();
                    try {
                        igr2Thread.join(3600000); // Stop Iterated Greedy after at most 1 hour
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (writeToFile) {
                        writer.print(igr2.k + ",");
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writeToFile, writer);

                    start = System.nanoTime();
                    // Instantiate Iterated Greedy algorithm class and start its thread
                    IteratedGreedy igr = new IteratedGreedy(n, m, e, WelshPowell.colorSet.size(), adjList, WelshPowell.colorSet);
                    Thread igrThread = new Thread(igr);
                    igrThread.start();
                    try {
                        igrThread.join(3600000); // Stop Iterated Greedy after at most 1 hour
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (writeToFile) {
                        writer.print(igr.k + ",");
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writeToFile, writer);

                    start = System.nanoTime();
                    LowerBoundTop lowerBoundTop = new LowerBoundTop(n, e, writer);
                    Thread lbThread = new Thread(lowerBoundTop);
                    lbThread.start();
                    try {
                        lbThread.join(3600000); // Stop LowerBound after at most 1 hour
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writeToFile, writer);

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

        File[] files = new File("Graphs/").listFiles();
        //File[] files = new File("Graphs/graph_color").listFiles();

        //files = null; // debug

        if (files != null) {
            ArrayList<String> graphList = new ArrayList<>();
            for (File file: files) {
                if (Util.getFileExtension(file).equals("txt") || Util.getFileExtension(file).equals("col"))
                    graphList.add(file.getPath());
            }
            String[] graphArr = new String[graphList.size()];
            graphArr = graphList.toArray(graphArr);
            return graphArr;
        }

        // Shouldn't arrive here
        return null;
    }

}

