import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Class to easily compare accuracy and runtime of
 * different versions of the Bron-Kerbosch lower bound algorithm
 */
public class SingleTest {

    public static void main(String[] args){
        compare(args[0]);
    }

    /**
     * Method that runs a test on a single sample file for debugs
     *
     * @param fname String array of all the paths of the graphs to compare.
     */
    private static void compare(String fname){
            System.out.println("\n" + fname);
            ColEdge[] e = readGraph.readFile(fname);

            int nodes = readGraph.nodes;
            int edges = readGraph.edges;
            double density = (double) edges / (nodes * (nodes - 1) / 2);
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

            // Commence logical checks, afterwards - algorithms
            if (edges == 0) {
                System.out.println("Graph is completely disconnected");
            } else if (density == 1.0) {
                System.out.println("Graph is fully connected");
            } else if (Bipartite.isBipartite(matrix, nodes)) {
                System.out.println("Graph is bipartite");
            } else {
                long start;
                long end;

                start = System.nanoTime();
                LowerBoundHeapRanked lowerBoundHeapRanked = new LowerBoundHeapRanked(nodes, e, matrix);
                Thread lbhThreadRanked = new Thread(lowerBoundHeapRanked);
                lbhThreadRanked.start();
                try {
                    lbhThreadRanked.join(Config.killtime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.out.println("Ranked LB is " + lowerBoundHeapRanked.getLowerBound() + " on node " + lowerBoundHeapRanked.getNode());
                end = System.nanoTime();
                timePrinter(start, end);

                start = System.nanoTime();
                LowerBoundHeap lowerBoundHeap = new LowerBoundHeap(nodes, e, matrix);
                Thread lbhThread = new Thread(lowerBoundHeap);
                lbhThread.start();
                try {
                    lbhThread.join(Config.killtime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.out.println("BaseLB is " + lowerBoundHeap.getLowerBound() + " on node " + lowerBoundHeap.getNode());
                end = System.nanoTime();
                timePrinter(start, end);
            }
    }

    /**
     * Method for outputting runtime easily.
     *
     * @param start Start of the execution (in nanoseconds).
     * @param end End of the execution (in nanoseconds).
     */
    public static void timePrinter(long start, long end){
        long time = end - start;
        System.out.println("Runtime: " + time + " nanosec");

        time = (long) (time / 1000000);
        byte hour = (byte)((time/1000/60/60)%60);
        byte minute = (byte) (((time/1000/60) - (hour*60))%60);
        byte second = (byte) ((time/1000) - (minute*60));
        int mili = (int) ((time) - (second*1000));
        System.out.println(hour + ":" + minute + ":" + second + "." + mili);
    }
}