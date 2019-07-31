public class LowerBoundHeap implements Runnable{
	private static long iterationcounter = 0;
	private static int lowerBound = 0;
	private static boolean debug = false;
	private int nodes;
	private ColEdge[] e;
	private int[][] adjacency;

	public LowerBoundHeap(int n, ColEdge[] e, int[][] adjacency) {
		this.nodes = n;
		this.e = e;
		this.adjacency = adjacency;
	}

	public int getLowerBound() { //returns the lowerbound of a graph
		return lowerBound;
	}

	@Override
	public void run() {
		lowerBound = 0;		//reset

		int max = 0;
		
		int[][] neighbours = new int[nodes][max+1];
		
		for(int i = 0; i < nodes; i++){		//finding the densest node to set the size of the neighbour array
			int tempMax = 0;
			max = 0;
			for(int j = 0; j < nodes; j++){
				if(adjacency[i][j] == 1) tempMax++;
			}
			if (tempMax > max) max = tempMax;//maximum number (highest density) of neighbours in the graph
			neighbours[i]=new int[max+1];
		}
		
		for( int i = 0; i < nodes; i++){	//finds all neighbours and saves them
			int columncounter = 1;			//each iteration has a new columncounter set to 1
			
			neighbours[i][0] = i;//every node is the neighbour of itself
			for(int j = 0; j < nodes; j++){//for each record in the adjecency matrix 
				if(adjacency[i][j] == 1){//if it's connected the connected vortices get saved
				neighbours[i][columncounter] = j;
				columncounter++;
				}
			}
		}

		//debug printer for neighbours
		if(debug)
		for(int i = 0; i < neighbours.length; i++){		//debug only
			for(int j = 0; j < neighbours[i].length; j++){
				System.out.print(neighbours[i][j] + " ");
			}
			System.out.println();
		}
		
		int[][] executionOrder = new int[nodes][2];
		//System.out.println("Raw executionOrder: ");
		
		for(int i = 0; i < nodes; i++){
			executionOrder[i][0] = i;
			executionOrder[i][1] = neighbours[i].length;
			if(debug)	System.out.println(executionOrder[i][0] + " " + executionOrder[i][1]);
		}
		
		HeapSort.sort(executionOrder);
		
		//System.out.println("Sorted executionOrder: ");
		if(debug)
		for(int i = 0; i < nodes; i++){
			System.out.println(executionOrder[i][0] + " " + executionOrder[i][1]);
		}
		
		for(int i = 0; i < neighbours.length; i++){
			max = 0;
			//the recursion is only called if the chosen node's density is higher than the current lowerbound 
			//max is reused to save a bit of memory. protection from accidental override of lowerBound
			//if(neighbours[i].length > lowerBound + 1){	//+1 for the i=j 0s
			//System.out.println(executionOrder[i][1]);
			if(executionOrder[i][1] > lowerBound + 1){	//+1 for the i=j 0s
				//System.out.println("Recursion called for node " + i);
				//max = recursiveSearch(e, adjacency, neighbours[i], lowerBound);
				max = recursiveSearch(e, adjacency, neighbours[executionOrder[i][0]], lowerBound);
			}
			if(max > lowerBound){
				lowerBound = max;
				//System.out.println("Lowerbound based on node " + i + " is  " + lowerBound);
			}
		}
		//return lowerBound;
	}
	
	private static int recursiveSearch( ColEdge[] e, int[][] adjacent, int[] neighbours, int depth){
		iterationcounter++;
		if(neighbours.length > depth){
			int max = 0;	//to store the highest score
			int sum = 0;	//sums an adjacency matrix's values
			
			int [][] adjacentNeighbours = new int[neighbours.length][neighbours.length];
			//debug
			/*if(debug){
				printMatrix(adjacent);
				System.out.println("Neighbours: ");
				for(int i = 0; i < neighbours.length; i++){
					System.out.print(neighbours[i] + " ");
				}
				System.out.println();
			}*/
		
			//for each node in neighbours find connections to the other nodes (0 or 1)
			//save it to adjacentNeighbours
			for (int i = 0; i< adjacentNeighbours.length; i++){
				for( int j = 0; j < adjacentNeighbours[i].length; j++){
				//	if(i!=j)System.out.println(neighbours[i] + " spies with it's " + neighbours[j] + " eyes a " + adjacent[ neighbours[i] ] [ neighbours [j] ]);//debug
					adjacentNeighbours[i][j] = adjacent[ neighbours[i] ] [ neighbours [j] ]; //confirmed
					sum+=adjacent[ neighbours[i] ] [ neighbours [j] ]; 	//would make the next loop useless hopefully
				}
			}
			if(debug)
				System.out.println("\nSum of edges in submatrix: " + sum + "(with internal loop) max value: " + ((adjacentNeighbours.length) * (adjacentNeighbours.length-1)));
		
			/*for(int i = 0; i < adjacentNeighbours.length; i++){		//sums the row values of the matrix
				for(int j = 0; j < adjacentNeighbours[i].length; j++){
					sum += adjacentNeighbours[i][j];				//fixed
				}
			}
			System.out.println("\nSum of edges in submatrix: " + sum + "(with external loop)");*/
			//debug
			/*if(debug){
				System.out.println("\nAdjacency matrix of neighbours:\n");
				printMatrix(adjacentNeighbours);		//debug baby
			}*/
		
			//basecase
			if(sum == ( (adjacentNeighbours.length) * (adjacentNeighbours.length-1) ) ){
			
				if(debug)System.out.println("Branch 1 Success with lowerbound of " + adjacentNeighbours.length);	//debug line
				//System.out.println("Iterations: " + iterationcounter );
				return adjacentNeighbours.length;					//ethernal glory, found a lower bound
			
			}else /*if(sum == 0){
				if(debug) System.out.println("Isolated node");
				return depth;
			}else */if( (sum + 2) == ( (adjacentNeighbours.length) * (adjacentNeighbours.length-1) ) ){
				/*
				if the sum of the connections is just 2 less than the expected value only one connection is missing
				hence the graph is nearly full. the chromatic number of the subgraph in these cases equals
				the number of nodes -1.
				*/
				//System.out.println("Iterations: " + iterationcounter );
				if(debug)System.out.println("Branch 2 Success with lowerbound of " + (adjacentNeighbours.length - 1) );	//debug line
				return (adjacentNeighbours.length - 1);					//ethernal glory, found a lower bound

			}else{		//when we really need recursion. ethernally flowing tears of chromatic kitten
			
			//System.out.println("New iteration required");		//debug line
			
			//we only take out 1 node, with the least connections
			
				int[] reducedNeighbours = new int[neighbours.length - 1];
				int min = adjacentNeighbours.length - 1;
				int minimumLocation = 0;
			
			
				for(int i = 0; i < adjacentNeighbours.length; i++){
					sum = 0; 		//reusing variable
					for( int j = 0; j < adjacentNeighbours[i].length; j++){
						sum += adjacentNeighbours[i][j];
					}
					if(sum < min){
						min = sum;
						minimumLocation = i;
					}
				}
			
				int a = 0;
				int b = 0;
				while(a < reducedNeighbours.length){
					if(b != minimumLocation){
						reducedNeighbours[a] = neighbours[b];
						a++;
						b++;
					}else{
						b++;
					}
				}		
			return	recursiveSearch(e, adjacent, reducedNeighbours, depth);
			}
		}else{
			if(debug)System.out.println("Pruning step");
			return depth;
		}
	}
}