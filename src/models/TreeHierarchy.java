package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TreeHierarchy {

	public HierarchyNode root;
	private HashMap<String, HierarchyNode> theSet = new HashMap<String, HierarchyNode>();
	private OutputStream fos ;//
	private PrintWriter writer;
	
	public TreeHierarchy(File output){
		try {
			fos = new FileOutputStream(output);
			//fos = new FileOutputStream(new File("/home/mmeimaris/jars/output.txt"));
			writer = new PrintWriter(new OutputStreamWriter(fos));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setRoot(HierarchyNode root){		
		this.root = insertIntoSet(root.value);
	}
	
	
	public HierarchyNode insertIntoSet(String value){
		if(!theSet.containsKey(value)) {		
			HierarchyNode newNode = new HierarchyNode();
			newNode.value = value;
			theSet.put(value, newNode);			
		}
		return theSet.get(value);
	}
	
	public int size(){
		return theSet.size();
	}
	
	public HierarchyNode getNode(String value){
		return theSet.get(value);
	}
	
	public Set<String> keySet(){
		return theSet.keySet();
	}
	
	public class HierarchyNode{
		
		public HierarchyNode parent = null;
		public String value;
		
		/*public HierarchyNode(String value){
			this.value = value;
			if(!theSet.containsKey(value)) {		
				//HierarchyNode newNode = new HierarchyNode(value);						
				
				theSet.put(value, this);
				writer.println("Inserting value " + value);
			}			
		}*/
		
		public void setParent(HierarchyNode parent){
			writer.println("Attempting to set parent " + parent.value);
			if(equals(parent)) return ;
			if(this.parent==null){
				this.parent = insertIntoSet(parent.value);
				writer.println(toString() + " has Parent " + this.parent.value);
			}		
		}
		
		@Override
		public int hashCode(){
			return value.hashCode();
		}
		
		@Override
		public boolean equals(Object other){
			try{
			return toString().equals(other.toString());
			}catch(NullPointerException e){
				System.out.println(other.toString());
				System.out.println(toString());
				return false;
			}
		}
		
		public String toString(){
			return value;
		}
		
		public boolean isParentOf(HierarchyNode child){
			
			HierarchyNode current = child;
			
			while(current!=null){
				//System.out.println("Current: "+ current.toString());
				if(current.parent == null){
					//System.out.println("null parent: " + current.toString());
					//System.out.println("this: " + toString());
					if(equals(root) || value.contains("TopConcept") || 
							value.equals("http://imis.athena-innovation.gr/code#TOTAL") || 
							value.equals("http://purl.org/linked-data/sdmx/2009/code#sex-T") ||
							value.equals("http://linked-statistics.gr/ontology/code/2011/education-level/TOTAL")) 
						return true;
					else return false;
					
				}
				
				if(equals(current.parent)) return true;
				current = current.parent;
			}
			return false;
			
		}
		
		public ArrayList<String> getParents(){
			ArrayList<String> parents = new ArrayList<String>();
			/*System.out.println("uri: " + toString());
			System.out.println("parent: " + this.parent);*/
			if(this.parent==null) return null;
			HierarchyNode current = this;
			//System.out.println("----------");
			while(current.parent!=null){
				//System.out.println(current.toString());
				parents.add(current.parent.value);
				current = current.parent;
			}
			//System.out.println("----------");
			return parents;
		}
		
	}
}
