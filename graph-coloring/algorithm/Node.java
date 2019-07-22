package algorithm;

import java.util.LinkedList;

class Node extends LinkedList implements Cloneable{
	public int i;
	public int s;
	public Node(int _i) {
		i = _i;
		s = 0;
	}
	public Node(int _i, int _s) {
		i = _i;
		s = _s;
	}	
	public Node clone() {
		return new Node(i, s);
	}
	
	public static void main(String[] args) {
		Node n = new Node(1,2);
		Node clon = n.clone();
		clon.s++;
		System.out.println(n.s);
		System.out.println(clon.s);
		
	}

}