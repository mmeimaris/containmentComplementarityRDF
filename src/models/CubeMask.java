package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CubeMask {

		
	HashMap<Dimension, Integer> levels = new HashMap<Dimension, Integer>();
	//HashMap<Dimension, String> values = new HashMap<Dimension, String>();	
	//String uri;
	String rep = null;
	ArrayList<Dimension> list = new ArrayList<Dimension>();
	
	public CubeMask(){
		
	}
	
	public void setDimensionLevel(Dimension dimension, int level){
		levels.put(dimension, level);
	}
		
	public int getDimensionLevel(Dimension dimension){
		return levels.get(dimension);
	}
	
	public Set<Dimension> getDimensions(){
		return levels.keySet();
	}
	
	public List<Dimension> getSortedDimensions(){
		if(list.isEmpty()){
			Set<Dimension> dimensions = levels.keySet();
			ArrayList<Dimension> list = new ArrayList<Dimension>(dimensions);
			Collections.sort(list);
			this.list = list;
		}
		
		return list;
	}
	
	@Override
	public boolean equals(Object other){		
		return toString().equals(other.toString());
	}
	
	@Override
	public int hashCode(){
		return toString().hashCode();
		
	}
	
	public String toString(){
			
		if(rep == null){
			
			StringBuilder builder = new StringBuilder();
			for(Dimension dim : getSortedDimensions()){
				try{
					builder.append(dim.getRepresentative()).append("_").append(levels.get(dim));
				}catch(NullPointerException e){
					continue;
				}
			}
			if(getSortedDimensions().isEmpty()) return "";
			rep = builder.toString();
		}
		
		return rep;
		
		
	}
	
}
