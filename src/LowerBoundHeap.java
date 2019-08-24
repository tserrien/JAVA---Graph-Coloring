public class LowerBoundHeap implements Runnable{
	private static int lowerBound = 0;
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
			int columncounter = 1;			//each iteration has a new column counter set to 1
			
			neighbours[i][0] = i;			//every node is the neighbour of itself
			for(int j = 0; j < nodes; j++){ //for each record in the adjacency matrix
				if(adjacency[i][j] == 1){   //if it's connected the connected vertices get saved
				neighbours[i][columncounter] = j;
				columncounter++;
				}
			}
		}

		//debug printer for neighbours
		if(Config.DEBUG)
		for(int i = 0; i < neighbours.length; i++){		//debug only
			for(int j = 0; j < neighbours[i].length; j++){
				System.out.print(neighbours[i][j] + " ");
			}
			System.out.println();
		}
		
		int[][] executionOrder = new int[nodes][2];
		
		for(int i = 0; i < nodes; i++){
			executionOrder[i][0] = i;
			executionOrder[i][1] = neighbours[i].length;
			if(Config.DEBUG)	System.out.println(executionOrder[i][0] + " " + executionOrder[i][1]);
		}
		
		HeapSort.sort(executionOrder);

		if(Config.DEBUG) {
			System.out.println("Sorted executionOrder: ");
			for (int i = 0; i < nodes; i++) {
				System.out.println(executionOrder[i][0] + " " + executionOrder[i][1]);
			}
		}
	}

	//TODO ask help to understand sonarlint's problem with the function
	private static int recursiveSearch( ColEdge[] e, int[][] adjacent, int[] neighbours, int depth){
		if(neighbours.length > depth){
			int max = 0;	//to store the highest score
			int sum = 0;	//sums an adjacency matrix's values
			
			int [][] adjacentNeighbours = new int[neighbours.length][neighbours.length];
			if(Config.DEBUG){
				Util.PrintMatrix(adjacent);
				System.out.println("Neighbours: ");
				for(int i = 0; i < neighbours.length; i++){
					System.out.print(neighbours[i] + " ");
				}
				System.out.println();
			}
		
			//for each node in neighbours find connections to the other nodes (0 or 1)
			//save it to adjacentNeighbours
			for (int i = 0; i< adjacentNeighbours.length; i++){
				for( int j = 0; j < adjacentNeighbours[i].length; j++){
					adjacentNeighbours[i][j] = adjacent[ neighbours[i] ] [ neighbours [j] ]; //confirmed
					sum+=adjacent[ neighbours[i] ] [ neighbours [j] ]; 	//would make the next loop useless hopefully
				}
			}
		
			//basecase
			if(sum == ( (adjacentNeighbours.length) * (adjacentNeighbours.length-1) ) ){
			
				if(Config.DEBUG)System.out.println("Branch 1 Success with lowerbound of " + adjacentNeighbours.length);
				//found a lower bound
				return adjacentNeighbours.length;
			
			}else if( (sum + 2) == ( (adjacentNeighbours.length) * (adjacentNeighbours.length-1) ) ){
				/*
				if the sum of the connections is just 2 less than the expected value only one connection is missing
				hence the graph is nearly full. the chromatic number of the subgraph in these cases equals
				the number of nodes -1.
				*/
				if(Config.DEBUG)System.out.println("Branch 2 Success with lowerbound of " + (adjacentNeighbours.length - 1) );	//debug line
				return (adjacentNeighbours.length - 1);					//ethernal glory, found a lower bound

			}else{
				//if all pruning tricks fail, call recursion
				//only take out 1 node, with the least connections
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
			if(Config.DEBUG)System.out.println("Pruning step");
			return depth;
		}
	}
}