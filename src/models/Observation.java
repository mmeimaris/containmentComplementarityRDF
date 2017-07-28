package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import tests.CubeMasking2;

public class Observation {

		
	HashMap<Dimension, Integer> levels = new HashMap<Dimension, Integer>();
	HashMap<Dimension, String> values = new HashMap<Dimension, String>();	
	String uri;
	HashMap<Dimension, String> dewey = new HashMap<Dimension, String>();;
	String deweyString = "";
	ArrayList<Dimension> sortedList = new ArrayList<Dimension>();
	
	public Observation(String uri){
		this.uri = uri;
	}
	
	public void setDimensionLevel(Dimension dimension, int level){
		levels.put(dimension, level);
	}
	
	public void setDimensionValue(Dimension dimension, String value){
		values.put(dimension, value);
	}
	
	public String getDimensionValue(Dimension dimension){
		return values.get(dimension);
	}
	
	public int getDimensionLevel(Dimension dimension){
		return levels.get(dimension);
	}
	
	public Set<Dimension> getImplicitDimensions(){
		return levels.keySet();
	}
	
	public Set<Dimension> getDimensions(){
		//return levels.keySet();
		return new HashSet<Dimension>(DimensionFactory.getInstance().getDimensions());
	}
	
	public HashMap<Dimension, String> getDewey(){
				
		if(dewey.isEmpty()){			
			for(Dimension dim : getSortedDimensions()){
				if(!levels.keySet().contains(dim)){
					dewey.put(dim, "0");
					continue;
				}
				ArrayList<String> parents = CubeMasking2.hierarchy.getNode(getDimensionValue(dim)).getParents();
				String dimRep = "";
				if(parents != null){
					for(int i = parents.size()-1; i >= 0; i--){
						
						dimRep += (CubeMasking2.valueIndexMap.get(parents.get(i)) == null) ? "0." : (CubeMasking2.valueIndexMap.get(parents.get(i)) + ".");
					}
					dimRep += (CubeMasking2.valueIndexMap.get(getDimensionValue(dim)).equals("null")) ? "0" : CubeMasking2.valueIndexMap.get(getDimensionValue(dim));
							
					//System.out.println(dimRep);
				}
				dewey.put(dim, (dimRep.equals("")) ? "0" : dimRep);
			}
		}				
		return dewey;
		
	}
	public ArrayList<Dimension> getSortedDimensions(){
		if(sortedList.isEmpty()){
			ArrayList<Dimension> list = new ArrayList<Dimension>(getDimensions());
			Collections.sort(list);
			
			
			this.sortedList = list;
		}		
		return this.sortedList;
	}
	public String getDeweyString(){
		
		if(this.deweyString.equals("")){
			HashMap<Dimension, String> dewey = getDewey();
			
			String deweyString = "";
//			ArrayList<Dimension> list = new ArrayList<Dimension>(getDimensions());
//			Collections.sort(list);		
			for(Dimension dim : getSortedDimensions()){
				
				deweyString += dewey.get(dim)+"/";
				
			}
			this.deweyString = deweyString;
		}
		
		return this.deweyString;
		
	}
	
//	@Override
//	public boolean equals(Object other){		
//		return toString().equals(other.toString());
//	}
//	
//	@Override
//	public int hashCode(){
////		Set<Dimension> dimensions = levels.keySet();
////		ArrayList<Dimension> list = new ArrayList<Dimension>(dimensions);
////		Collections.sort(list);
////		StringBuilder builder = new StringBuilder();
////		for(Dimension dim : list){
////			try{
////				builder.append(dim.getRepresentative()).append("_").append(levels.get(dim));
////			}catch(NullPointerException e){
////				continue;
////			}
////		}
////		if(dimensions.isEmpty()) return 0;
////		return builder.toString().hashCode();
//		return toString().hashCode();
//		
//	}
	
	@Override
    public int hashCode() {		
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).            
        	append(uri).
            toHashCode();
    }
	
	@Override
    public boolean equals(Object obj) {
       if (!(obj instanceof Observation))
            return false;
        if (obj == this)
            return true;

        Observation rhs = (Observation) obj;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            //append(properties, rhs.properties).        	
        	append(uri, rhs.uri).
            isEquals();
    }
	
	public String toString(){
			
//		Set<Dimension> dimensions = levels.keySet();
//		ArrayList<Dimension> list = new ArrayList<Dimension>(getDimensions());
//		Collections.sort(list);
//		StringBuilder builder = new StringBuilder();
//		for(Dimension dim : list){
//			try{
//				builder.append(dim.getRepresentative()).append("_").append(levels.get(dim))
//						.append("(").append(values.get(dim)).append(")");
//				//builder.append(dim.getRepresentative()).append("(").append(values.get(dim)).append(")");
//			}catch(NullPointerException e){
//				continue;
//			}
//		}
//		if(dimensions.isEmpty()) return "";
//		return builder.toString();
		return getDeweyString();
		
	}
	
}
