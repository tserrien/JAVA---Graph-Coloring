import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Class to easily compare accuracy and runtime of
 * different versions of the Bron-Kerbosch lower bound algorithm
 */
public class BulkComparison {

    public static void main(String[] args){
        if(args.length == 0){
            System.out.println("Specify folder or file path");
        }else if(args.length == 1) {
            compare(getList(args[0]));
        }else {
        System.out.println("Too many arguments");
        }
    }

    /**
     * Method that compares the results of different lower-bound algorithms.
     *
     * The comparing works as follows. Graph is read, if the graph is completely disconnected, the chromatic
     * number (CN) is 1, if the graph is fully connected, CN = number of nodes, then graph is checked
     * if it's a bipartite graph, because if it is, CN = 2. If none of these cases occurred, the actual algorithms
     * are started in this order:
     * Bron-Kerbosch,with heapsorting
     *
     * The algorithms can be limited in runtime in the settings file. If they have exceeded this limit, their current
     * best result is taken.
     *
     * @param paths String array of all the paths of the graphs to compare. (is broken atm)
     */
    private static void compare(String[] paths){
        try {
            //TODO make filename timestamped and dynamic
            PrintWriter writer = new PrintWriter("testrun.csv");
            if (Config.writeToFile)
                writer.print("Path,Nodes,Edges,Density,Baselb,NodeLoc,RuntimeNano,Rankedlb,NodeLoc,RuntimeNano\n");

            for (int i = 0; i < paths.length; i++) {
                System.out.println("\n" + paths[i]);
                ColEdge[] e = readGraph.readFile(paths[i]);

                int nodes = readGraph.nodes;
                int edges = readGraph.edges;
                int[][] matrix = Util.adjacencyMatrix(readGraph.nodes, e);

                int maxDegree = 0;

                int[] d = new int[nodes];

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
                for (int k = 0; k < nodes; k++) {
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

                if (Config.writeToFile)
                    writer.print(paths[i] + "," + nodes + "," + edges + ",");

                //System.out.println("\n" + paths[i] + "\nDisconnected: " + readGraph.getDisconnected() + "/" + n);
                //if (Config.writeToFile)
                    //writer.print(readGraph.getDisconnected()+",");
                // Calculate density of the graph
                // TODO move this to Util (or somewhere...)
                double density = (double) edges / (nodes * (nodes - 1) / 2);

                System.out.printf("Density: %.5f \n", density);

                if (Config.writeToFile)
                    writer.print(density+",");

                // Commence logical checks, afterwards - algorithms
                if (edges == 0) {
                    System.out.println("Graph is completely disconnected");
                    if (Config.writeToFile)
                        writer.print(1);
                } else if (density == 1.0) {
                    System.out.println("Graph is fully connected");

                    if (Config.writeToFile)
                        writer.print(nodes);
                } else if (Bipartite.isBipartite(matrix, nodes)) {
                    System.out.println("Graph is bipartite");

                    if (Config.writeToFile)
                        writer.print(2);
                } else {
                    long start;
                    long end;

                    //TODO: examine if this could be turned into a method call to shorten code

                    start = System.nanoTime();
                    LowerBoundHeap lowerBoundHeap = new LowerBoundHeap(nodes, e, matrix);
                    Thread lbhThread = new Thread(lowerBoundHeap);
                    lbhThread.start();
                    try {
                        lbhThread.join(Config.killtime);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (Config.writeToFile) {
                        writer.print(lowerBoundHeap.getLowerBound() + "," + lowerBoundHeap.getNode() + ",");
                        System.out.println("BaseLB is " + lowerBoundHeap.getLowerBound() + " on node " + lowerBoundHeap.getNode());
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writer);

                    start = System.nanoTime();
                    LowerBoundHeapRanked lowerBoundHeapRanked = new LowerBoundHeapRanked(nodes, e, matrix);
                    Thread lbhThreadRanked = new Thread(lowerBoundHeapRanked);
                    lbhThreadRanked.start();
                    try {
                        lbhThreadRanked.join(Config.killtime);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if (Config.writeToFile) {
                        writer.print(lowerBoundHeapRanked.getLowerBound() + "," + lowerBoundHeapRanked.getNode() + ",");
                        System.out.println("Ranked LB is " + lowerBoundHeapRanked.getLowerBound() + " on node " + lowerBoundHeapRanked.getNode());
                    }
                    end = System.nanoTime();
                    timePrinter(start, end, writer);
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
    public static void timePrinter(long start, long end, PrintWriter writer){
        long time = end - start;
        System.out.println("Runtime: " + time + " nanosec");

        if (Config.writeToFile)
            writer.print(time + ",");

        time = (long) (time / 1000000);
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
    private static String[] getList(String pathString){

        //TODO remove hardcoded path
        File[] files = new File("Graphs/Kelk").listFiles();
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