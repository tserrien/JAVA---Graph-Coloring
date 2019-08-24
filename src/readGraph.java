import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class readGraph {

	public final static String COMMENT = "//";
	public static int edges;
	public static int nodes;
	private static int disconnectedNodes = 0;

	public static int getDisconnected(){
		return disconnectedNodes;
	}

	public static ColEdge[] readFile(String inputfile) {

		boolean seen[] = null;
		nodes = -1;
		edges = -1;

		// ! edgeConnections will contain the edges of the graph
		ColEdge edgeConnections[] = null;

		try {
			FileReader fr = new FileReader(inputfile);
			BufferedReader br = new BufferedReader(fr);

			String record = new String();

			// ! THe first few lines of the file are allowed to be comments, staring with a
			// // symbol.
			// ! These comments are only allowed at the top of the file.

			while ((record = br.readLine()) != null) {
				if (record.startsWith("//"))
					continue;
				// Saw a line that did not start with a comment -- time to start reading the data in!
				break;
			}

			if (record.startsWith("VERTICES = ")) {
				nodes = Integer.parseInt(record.substring(11));
				if (Config.DEBUG)
					System.out.println(COMMENT + " Number of vertices = " + nodes);
			} else {
				throw new IOException();
			}

			seen = new boolean[nodes + 1];
			
			record = br.readLine();

			if (record.startsWith("EDGES = ")) {
				edges = Integer.parseInt(record.substring(8));
				if (Config.DEBUG)
					System.out.println(COMMENT + " Expected number of edges = " + edges);
			}

			edgeConnections = new ColEdge[edges];

			for (int d = 0; d < edges; d++) {
				if (Config.DEBUG)
					System.out.println(COMMENT + " Reading edge " + (d + 1));
				record = br.readLine();
				String data[] = record.split(" ");
				if (data.length != 2) {
					System.out.println("Error! Malformed edge line: " + record);
					System.exit(0);
				}
				edgeConnections[d] = new ColEdge();

				edgeConnections[d].u = Integer.parseInt(data[0]);
				edgeConnections[d].v = Integer.parseInt(data[1]);

				seen[edgeConnections[d].u] = true;
				seen[edgeConnections[d].v] = true;

				if (Config.DEBUG)
					System.out.println(COMMENT + " Edge: " + edgeConnections[d].u + " " + edgeConnections[d].v);

			}

			String surplus = br.readLine();
			if (surplus != null) {
				if (surplus.length() >= 2)
					if (Config.DEBUG)
						System.out.println(
								COMMENT + " Warning: there appeared to be data in your file after the last edge: '" + surplus + "'");
			}
			br.close();

		} catch (IOException ex) {
			// catch possible io errors from readLine()
			System.out.println("Error! Problem reading file " + inputfile);
			return null;
		}

		//return to 0, because the variable is static, in case more than one files are read in.
		// TODO make this independent
		disconnectedNodes = 0;

		for (int x = 1; x <= nodes; x++) {
			if (seen[x] == false) {
				if(Config.DEBUG)
					System.out.println(COMMENT + " Warning: vertex " + x
							+ " didn't appear in any edge : it will be considered a disconnected vertex on its own.");
				disconnectedNodes++;
			}
		}
		return edgeConnections;
	}
}