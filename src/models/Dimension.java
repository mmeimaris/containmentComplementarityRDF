package models;

import java.util.HashSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Dimension implements Comparable{
	
	private String representative;
	private String codelist;
	private HashSet<String> members;
	
	public void setRepresentative(String representative){
		this.representative = representative;
	}
	
	public String getRepresentative(){
		return this.representative;
	}
	
	public void setCodelist(String codelist){
		this.codelist = codelist;
	}
	
	public String getCodelist(){
		return this.codelist;
	}
			
	public void setMembers(HashSet<String> members){
		this.members = members;
	}
	
	public boolean isMember(String toCheck){
		if(this.members.contains(toCheck)) return true;
		else return false;
	}
	
	public HashSet<String> getMembers(){
		return this.members;
	}
	
	 // Overriding the compareTo method
	   public int compareTo(Dimension d){
	      return (this.representative).compareTo(d.getRepresentative());
	   }

	   // Overriding the compare method to sort the age 
	   public int compare(Dimension d, Dimension d1){
	      return d.getRepresentative().compareTo(d1.getRepresentative());
	   }

	@Override
	public int compareTo(Object o) {
		Dimension d = (Dimension) o;
		return (this.representative).compareTo(d.getRepresentative());		
	}
	
	public String toString(){
		return this.representative;
	}
	
	@Override
    public int hashCode() {		
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            //append(properties).        	
        	append(representative).
            toHashCode();
    }
	
	@Override
    public boolean equals(Object obj) {
       if (!(obj instanceof Dimension))
            return false;
        if (obj == this)
            return true;

        Dimension rhs = (Dimension) obj;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            //append(properties, rhs.properties).        
        	append(representative, rhs.representative).
            isEquals();
    }

}
