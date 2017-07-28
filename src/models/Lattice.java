package models;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class Lattice {
	
	public LatticeNode root;
	public HashSet<String> set = new HashSet<String>();
	
	public Lattice (LatticeNode root){
		this.root = root;
	}
	
	public void addNode(String obsURI){
		
		if(!set.contains(obsURI))
			set.add(obsURI);
		
	}
		
	public class LatticeNode  {
		
		Observation cube;
		LatticeNode parent;
	    List<LatticeNode> children;

	    public LatticeNode(Observation cube) {
	        this.cube = cube;
	        this.children = new LinkedList<LatticeNode>();
	    }

	    public LatticeNode addChild(Observation child) {
	    	LatticeNode childNode = new LatticeNode(child);
	        childNode.parent = this;
	        this.children.add(childNode);
	        return childNode;
	    }
	}
	    

	    // other features ...

	
}
