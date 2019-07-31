import java.util.LinkedList;

/**
 * Class to check whether the graph is bipartite or not.
 * Credit: https://www.geeksforgeeks.org/bipartite-graph/
 */

public class Bipartite {

    /**
     * Utility function for checking whether a graph is bipartite.
     *
     * @param G Adjacency matrix.
     * @param src Node index to start from.
     * @param colorArr Node colouring array.
     * @param V Number of nodes.
     * @return True if the graph is bipartite, otherwise - False.
     */
    public static boolean isBipartiteUtil(int G[][], int src,
                                          int colorArr[], int V)
    {
        colorArr[src] = 1;

        // Create a queue (FIFO) of vertex numbers and
        // enqueue source vertex for BFS traversal
        LinkedList<Integer> q = new LinkedList<Integer>();
        q.add(src);

        // Run while there are vertices in queue
        // (Similar to BFS)
        while (!q.isEmpty())
        {
            // Dequeue a vertex from queue
            // ( Refer http://goo.gl/35oz8 )
            int u = q.getFirst();
            q.pop();

            // Return false if there is a self-loop
            //if (G[u][u] == 1)
            //  return false;

            // Find all non-colored adjacent vertices
            for (int v = 0; v < V; ++v)
            {
                // An edge from u to v exists and
                // destination v is not colored
                if (G[u][v] ==1 && colorArr[v] == -1)
                {
                    // Assign alternate color to this
                    // adjacent v of u
                    colorArr[v] = 1 - colorArr[u];
                    q.push(v);
                }

                // An edge from u to v exists and
                // destination v is colored with same
                // color as u
                else if (G[u][v] ==1 && colorArr[v] ==
                        colorArr[u])
                    return false;
            }
        }

        // If we reach here, then all adjacent vertices
        // can be colored with alternate color
        return true;
    }

    /**
     * Method that says whether the given graph G bipartite or not.
     *
     * @param G Adjacency matrix.
     * @param V Number of nodes.
     * @return True if bipartite, otherwise - False.
     */
    public static boolean isBipartite(int G[][], int V)
    {
        // Create a color array to store colors assigned
        // to all veritces. Vertex/ number is used as
        // index in this array. The value '-1' of
        // colorArr[i] is used to indicate that no color
        // is assigned to vertex 'i'. The value 1 is used
        // to indicate first color is assigned and value
        // 0 indicates second color is assigned.
        int colorArr[] = new int[V];
        for (int i = 0; i < V; ++i)
            colorArr[i] = -1;

        // This code is to handle disconnected graoh
        for (int i = 0; i < V; i++)
            if (colorArr[i] == -1)
                if (!isBipartiteUtil(G, i, colorArr, V))
                    return false;

        return true;
    }

}
