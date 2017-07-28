package models;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class DataCube {

		
		///This field defines the URI of data cube public domain 
		public static final String QBprefix = "http://purl.org/linked-data/cube#";
	
		///Data Cube DataSet class
		public static Resource qbDataSet;// ok
		///Data Cube Observation class
		public static Resource qbObservation;// ok
		///Data Cube Slice class
		public static Resource qbSlice; // ok
		///Data Cube ComponentProperty class. Instead of this class each component type
		///uses its own custom property.
		public static Resource qbComponentProperty;// ok
		///ComponentProperty for Dimensions
		public static Resource qbDimensionProperty; // einai ComponentProperty
		///ComponentProperty for Measures
		public static Resource qbCodedProperty; // einai ComponentProperty
		///ComponentProperty for Measures
		public static Resource qbMeasureProperty; // einai ComponentProperty
		///ComponentProperty for Attributes
		public static Resource qbAttributeProperty; // einai ComponentProperty
		///dimension for multiple measures
		public static Resource qbMeasureType;// ok
		
		
		
		
		
		///Data Cube Data Structure Definition
		///It is common, when publishing statistical data, to have a regular series of publications which all follow the same structure.
		///The notion of a Data Structure Definition (DSD) allows us to define that structure once and then reuse it for each publication in the series.
		///Consumers can then be confident that the structure of the data has not changed.
		public static Resource qbDataStructureDefinition;// ok
		///Data Cube ComponentSpecification Class. It relates its component
		///to the DSD.
		public static Resource qbComponentSpecification;// ok
		///Data Cube Slice Key Class
		public static Resource qbSliceKey; // ok

		// properties

		// sets
		///qb:observation ( qb:Slice -> qb:Observation )
		public static Property qbobservation; 
		/// qb:dataSet ( qb:Observation -> qb:DataSet)
		public static Property qbdataSet;
		/// qb:slice ( qb:DataSet -> qb:Observation )
		public static Property qbslice; 
		// qb:structure ( qb:DataSet -> qb:DataStructureDefinition )
		public static Property qbstructure; 

		// components
		/// qb:component ( qb:DataStructureDefinition -> qb:ComponentSpecification )
		public static Property qbcomponent; 
		/// qb:componentProperty ( qb:ComponentSet -> qb:ComponentProperty )
		public static Property qbcomponentProperty;
		/// componentproperty subclass
		public static Property qbdimension;
		/// componentproperty subclass
		public static Property qbmeasure;
		/// componentproperty subclass
		public static Property qbattribute;
		/// qb:measureType ( -> qb:MeasureProperty )
		public static Property qbmeasureType;
		/// qb:order( qb:ComponentSpecification -> xsd:int )
		public static Property qborder;
		/// qb:componentRequired ( qb:ComponentSpecification -> xsd:boolean )
		public static Property qbcomponentrequired;
		/// Indicates the level at which the component property should be attached, this might be an qb:DataSet, 
		/// qb:Slice or qb:Observation, or a qb:MeasureProperty.
		public static Property qbcomponentAttachment;
		/// qb:sliceKey ( qb:DataSet -> qb:SliceKey )
		public static Property qbsliceKey; 
		/// qb:sliceStructure ( qb:Slice -> qb:SliceKey )
		public static Property qbsliceStructure; 
		
		public static Property qbcodeList;
		

		// not included*---Class: qb:CodedProperty,Class: qb:ComponentSet,Property:
		// Property: qb:concept

		
		static {


			qbMeasureType = ResourceFactory.createResource(QBprefix+ "measureType");
			qbDataSet = ResourceFactory.createResource(QBprefix + "DataSet");
			qbObservation = ResourceFactory.createResource(QBprefix + "Observation");
			qbSlice = ResourceFactory.createResource(QBprefix + "Slice");
			qbComponentProperty = ResourceFactory.createResource(QBprefix	+ "ComponentProperty");
			qbCodedProperty = ResourceFactory.createResource(QBprefix		+ "CodedProperty");
			qbDimensionProperty = ResourceFactory.createResource(QBprefix	+ "DimensionProperty");// class dimension
			qbMeasureProperty = ResourceFactory	.createResource(QBprefix + "MeasureProperty");// class Measure
			qbAttributeProperty = ResourceFactory.createResource(QBprefix+ "AttributeProperty");// class ............
			qbDataStructureDefinition = ResourceFactory.createResource(QBprefix	+ "DataStructureDefinition");
			qbComponentSpecification = ResourceFactory.createResource(QBprefix+ "ComponentSpecification");
			qbSliceKey = ResourceFactory.createResource(QBprefix + "Slicekey");
			qborder = ResourceFactory.createProperty(QBprefix + "order");
			qbobservation = ResourceFactory.createProperty(QBprefix + "observation");
			qbdataSet = ResourceFactory.createProperty(QBprefix + "dataSet");
			qbslice = ResourceFactory.createProperty(QBprefix + "slice");
			qbstructure = ResourceFactory.createProperty(QBprefix + "structure");
			qbcomponentProperty = ResourceFactory.createProperty(QBprefix + "componentProperty");
			qbcomponent = ResourceFactory.createProperty(QBprefix + "component");
			qbcodeList = ResourceFactory.createProperty(QBprefix + "codeList");
			qbdimension = ResourceFactory.createProperty(QBprefix + "dimension");
			qbmeasure = ResourceFactory.createProperty(QBprefix + "measure");
			qbattribute = ResourceFactory.createProperty(QBprefix + "attribute");
			qbmeasureType = ResourceFactory.createProperty(QBprefix + "measureType");
			qborder = ResourceFactory.createProperty(QBprefix + "order");
			qbcomponentrequired = ResourceFactory.createProperty(QBprefix	+ "qbcomponentrequired");
			qbsliceKey = ResourceFactory.createProperty(QBprefix + "sliceKey");
			qbsliceStructure = ResourceFactory.createProperty(QBprefix + "sliceStructure");
			qbcomponentAttachment = ResourceFactory.createProperty(QBprefix	+ "componentAttachment");
		}
	
}
