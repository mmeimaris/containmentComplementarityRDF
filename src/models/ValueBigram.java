package models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ValueBigram {

	public String v1, v2;
	public Dimension dim1, dim2;
	
	public void setValues(String v1, String v2, Dimension dim1, Dimension dim2){
		this.v1 = v1;
		this.v2 = v2;		
		this.dim1 = dim1;
		this.dim2 = dim2;
	}
	
	@Override
    public int hashCode() {		
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            //append(properties).
        	append(v1).
        	append(v2).
        	append(dim1).
        	append(dim2).
            toHashCode();
    }
	
	@Override
    public boolean equals(Object obj) {
       if (!(obj instanceof ValueBigram))
            return false;
        if (obj == this)
            return true;

        ValueBigram rhs = (ValueBigram) obj;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            //append(properties, rhs.properties).
        	append(v1, rhs.v1).
        	append(v2, rhs.v2).
        	append(dim1, rhs.dim1).
        	append(dim2, rhs.dim2).
            isEquals();
    }
	
	public String toString(){
		
		return "["+v1+", " + v2+"]";
		
	}
	
	public String toDimString(){
		
		return "["+dim1.getRepresentative()+", " + dim2.getRepresentative()+"]";
		
	}
	
}
