package tests;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import models.Dimension;
import models.DimensionFactory;
import models.Observation;
import models.ValueBigram;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import au.com.bytecode.opencsv.CSVWriter;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class MainTest {

	/**
	 * @param args
	 */
	static ArrayList<String> featureList = new ArrayList<String>();
	static Integer[][] matrix;
	static String connectionString = "jdbc:virtuoso://83.212.121.252:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
	static VirtGraph graph = new VirtGraph (connectionString, "dba", "olv@psnet@openlink69");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//DataCubeIzer dc = new DataCubeIzer();
		//fixEurostatCube();
		//dc.cubizeCSVInternetHouseholds();
		//dc.cubizeCSVPopNuts2();
		//dc.cubizeCSVAsylumCountry();
		//createObservationMapsEurostatCube();
		//createObservationMapsMultiEurostatCube();
		//findRootLevels();
		ValueBigram vb = new ValueBigram();
		vb.setValues("abcd", "abcd.edwf.bdf", null, null);
		ValueBigram vb2 = new ValueBigram();
		vb2.setValues("abcd", "abcd.edwf.bdf", null, null);
		HashSet<ValueBigram> s = new HashSet<ValueBigram>();
		s.add(vb);
		s.add(vb2);
		System.out.println(s.toString());
		System.out.println(s.toString());
		if(true) return;
		
		
		Observation obs1 = new Observation("obs1");
		Observation obs2 = new Observation("obs2");
		obs1.setDimensionLevel(DimensionFactory.getInstance().getDimension("http://ontologycentral.com/2009/01/eurostat/ns#currency"), 3);
		obs1.setDimensionValue(DimensionFactory.getInstance().getDimension("http://ontologycentral.com/2009/01/eurostat/ns#currency"), "dssf");
		obs1.setDimensionLevel(DimensionFactory.getInstance().getDimension("http://ontologycentral.com/2009/01/eurostat/ns#unit"), 2);
		obs2.setDimensionLevel(DimensionFactory.getInstance().getDimension("http://ontologycentral.com/2009/01/eurostat/ns#currency"), 0);
		obs2.setDimensionLevel(DimensionFactory.getInstance().getDimension("http://ontologycentral.com/2009/01/eurostat/ns#unit"), 2);
		Dimension dimension = DimensionFactory.getInstance().getDimension("http://ontologycentral.com/2009/01/eurostat/ns#unit");
		/*TreeHierarchy h = new TreeHierarchy();
		TreeHierarchy.HierarchyNode root = h.new HierarchyNode("eimai o pateras olwn");
		h.setRoot(root);
		TreeHierarchy.HierarchyNode child = h.new HierarchyNode("eimai to paidi");
		child.setParent(root);
		TreeHierarchy.HierarchyNode grandchild = h.new HierarchyNode("eimai to eggoni");
		grandchild.setParent(child);
		TreeHierarchy.HierarchyNode grandchild2 = h.new HierarchyNode("eimai to 2o eggoni");
		grandchild2.setParent(child);
		TreeHierarchy.HierarchyNode grandchild3 = h.new HierarchyNode("eimai to 2o eggoni");
		grandchild3.setParent(root);
		
		Set<String> keySet = h.keySet();
		for(String key : keySet){
			System.out.println(h.getNode(key).toString());
		}*/
		/*System.out.println(child.isParentOf(root));
		System.out.println(h.size());*/
		//System.out.println(obs1.equals(obs2));
		//ArrayList<Dimension> list = new ArrayList<Dimension>(obs2.getDimensions());
		/*for(Dimension dim : list)
			System.out.println(dim.getRepresentative());
		Collections.sort(list);
		for(Dimension dim : list)
			System.out.println(dim.getRepresentative());*/
		//System.out.println(DimensionFactory.getInstance().getDimensions().toString());
		/*Set<Dimension> dims = obs1.getDimensions();
		int cont = 0, cont_rev = 0;;
		for(Dimension d : dims){
			if(obs1.getDimensionLevel(d)>=obs2.getDimensionLevel(d)) {				
				cont++;				
			}
			if(obs2.getDimensionLevel(d)>=obs1.getDimensionLevel(d)) {
				cont_rev++;
			}
		}
		System.out.println(cont+" " + cont_rev + " " + dims.size());
		if(cont==dims.size()) System.out.println("Candidate for containment: Obs2->Obs1");
		else if (cont_rev==dims.size()) System.out.println("Candidate for containment: Obs1->Obs2");
		else System.out.println("No containment comparisons to be done here.");*/
		//System.out.println(obs1.toString());
		
		
		
	}
	
	public static void findRootLevels(){
				
		String query = "SELECT DISTINCT ?s FROM <codelists.age> WHERE {?s ?p ?o}";
		
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();		
		while(results.hasNext()){			
			QuerySolution rs = results.next();
			String age = rs.get("s").toString();
			//System.out.println(age);
			//System.out.println("---------------------------------");
			if(age.contains("http://linked-statistics.gr/ontology/code/2011/age/")){				
				age = age.replace("http://linked-statistics.gr/ontology/code/2011/age/", "");
				if(age.contains("GE")){		
					System.out.println(age);
					//if(true) continue;
					String greaterThan = age.replaceAll("Y_GE", "");
					//String start = age.substring(1,age.indexOf('-'));
					//String end = age.substring(age.indexOf('-')+1);
					query = "SELECT DISTINCT ?s FROM <codelists.age> WHERE {?s ?p ?o }";
					VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (query, graph);
					ResultSet results2 = vqe2.execSelect();		
					while(results2.hasNext()){							
						QuerySolution rs2 = results2.next();						
						String age2 = rs2.get("s").toString().replace("http://linked-statistics.gr/ontology/code/2011/age/", "");						
						if(age2.contains("GE") || age2.contains("-")) continue;
						age2 = age2.replace("Y", "");
						try{
							int age2int = Integer.parseInt(age2);
							//if(age2int>= Integer.parseInt(start) && age2int <= Integer.parseInt(end))
							if(age2int>= Integer.parseInt(greaterThan) )
								{
									System.out.println("Match:" + rs.get("s").toString() + " with " + rs2.get("s").toString());
									/*String insert = " INSERT INTO <codelists.age> {" +
											" <"+rs2.get("s").toString()+ "> skos:broaderTransitive <"+rs.get("s").toString() + "> " +
													"}"; 
									VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(insert, graph);
									vur.exec();	*/								
								}
						}
						catch(NumberFormatException e){
							//e.printStackTrace();
						}
						
					}
					vqe2.close();
				}
			}
		}
		vqe.close();
		graph.close();
	}
	
	public static void createNewLinks(){
		String query = "SELECT ?obs1 ?obs2 WHERE {" +
				"graph <poverty_nuts2_1_less> {" +
						"?obs1 sdmx-dimension:refArea ?area1 ; sdmx-dimension:refPeriod ?period" +
						"}" + 
				"graph <poverty_nuts2_1_less> {" +
						"?obs2 sdmx-dimension:refArea ?area2 ; sdmx-dimension:refPeriod ?period" +
						"}" +
				"graph <qb-codelists> {" +
						"?area1 nuts-schema:hasParentRegion/nuts-schema:hasParentRegion* ?area2" +
						"} " +
				"}";
		
	}
	
	public static void fixEurostatCube(){
		
		String dataset = "poverty_nuts2_1";
		//String query = "SELECT ?obs ?date"
		String query = "SELECT DISTINCT ?observation ?p ?o " +
				//"FROM <qb-asylum> " +
				"FROM <"+dataset+"> " +	
				"FROM <qb-definitions> " +
				"WHERE {" +							
							" ?observation a qb:Observation ; ?p ?o " +
							"}";		
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model corModel = ModelFactory.createDefaultModel();
		while(results.hasNext()){				
			QuerySolution rs = results.next();
			RDFNode obs = rs.get("observation");
			RDFNode p = rs.get("p");
			RDFNode o = rs.get("o");			
			if(
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/DE") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/DK") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/ES") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/FI") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/IT") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/MT") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/LV") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/LU") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/NO") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/PL") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/PT") ||
					o.toString().contains("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/RO") 
			) continue;
			else{
				Resource obsRes = corModel.createResource(obs.toString());
				Property prop = ResourceFactory.createProperty(p.toString());
				if(o.isResource()) obsRes.addProperty(prop, o.asResource());
				else obsRes.addProperty(prop, o.asLiteral());
			}			
		}vqe.close();
		try{
 			String path = "C:/Users/Marios/Desktop/"+dataset+"_less.rdf";
			FileOutputStream fos = new FileOutputStream(path);
			corModel.write(fos, "RDF/XML-ABBREV", path);
			fos.close();
		}catch(Exception e){}
		corModel.close();	
	}
	
public static void createObservationMapsMultiEurostatCube(){
		
	
		
		CSVWriter writer = null, labels = null;
		String[] datasets = new String[]{"poverty_nuts2_1_less", "population-nuts2-more", "internet_households_nuts2_less", "poverty-risk-nuts2"};
		try {
			  writer = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/data.csv"));
			  labels = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/labels.csv"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		int obsCount = 0;
		int featCount = 0;
		ArrayList<String> observationList = new ArrayList<String>();
		for(String dataset : datasets){
			String fromString = "FROM <qb-definitions> ";			
			String dimensionQuery = "SELECT distinct ?dimension " +								
					fromString +
					"WHERE {" +
						"{?dimension a qb:DimensionProperty}" +
						"UNION" +
						"{?dsd qb:component [qb:dimension ?dimension]}" +
						"filter(?dimension!=sdmx-dimension:age)" +
						"filter(?dimension!=eurostat:unit)" +
					"}";
			System.out.println(dimensionQuery);
			VirtuosoQueryExecution vqeD = VirtuosoQueryExecutionFactory.create (dimensionQuery, graph);
			ResultSet resultsD = vqeD.execSelect();
			
			while(resultsD.hasNext()){
				QuerySolution rsD = resultsD.next();	
				RDFNode dimension = rsD.get("dimension");
				String dimQuery = "SELECT DISTINCT ?feature " +
										//"FROM <qb-asylum> " +
										"FROM <"+dataset+"> " +
										"WHERE {" +
											"?obs a qb:Observation ; <"+dimension.toString()+"> ?feature " +
										"}";
				VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (dimQuery, graph);
				ResultSet results = vqe.execSelect();			
				while(results.hasNext()){
					QuerySolution rs = results.next();
					String feature = rs.get("feature").toString();
					/*if(dimension.toString().contains("http://purl.org/dc/terms/date"))
						featureList.add("http://reference.data.gov.uk/id/gregorian-year/"+feature);
					else*/
					if(!featureList.contains(feature)){
						featureList.add(feature);
						//System.out.println(featureList.toString());
						featCount++;
					}
					
				}vqe.close();
				
			}vqeD.close();			
			String[] featArr = new String[featureList.size()];
			for(int k=0; k<featureList.size(); k++ ) {
				String featLabel = featureList.get(k).substring(featureList.get(k).lastIndexOf("/")+1);
				if(featureList.get(k).lastIndexOf("#")>-1)
				featLabel = featureList.get(k).substring(featureList.get(k).lastIndexOf("#")+1);
				featArr[k] = featLabel;
			}
			labels.writeNext(featArr);
			String[] featNum = new String[featureList.size()];
			for(Integer num = 0; num<featNum.length; num++){
				featNum[num] = num.toString();
			}
			labels.writeNext(featNum);
			String meta = "SELECT DISTINCT ?observation " +
					//"FROM <qb-asylum> " +
					"FROM <"+dataset+"> " +
					"WHERE {" +
						"?observation a qb:Observation }";
			VirtuosoQueryExecution vqeMeta = VirtuosoQueryExecutionFactory.create (meta, graph);
			ResultSet resultsMeta = vqeMeta.execSelect();	
			
			while(resultsMeta.hasNext()){
				QuerySolution rsMeta = resultsMeta.next();
				observationList.add(rsMeta.get("observation").toString());
				obsCount++;
			}vqeMeta.close();
			
			
			
		}
		System.out.println(featureList.size());
		System.out.println(featureList.toString());
		System.out.println(obsCount);
								
		Integer[][] matrix = new Integer[obsCount][featCount];
		for(int i = 0 ; i <obsCount ; i++ ){
			for(int j = 0 ; j <featCount; j++ ){
				matrix[i][j] = 0;
			}
		}
		
		HashMap<String, String> obsLabels = new HashMap<String, String>();
		for(String dataset : datasets ){
			String query = "SELECT DISTINCT ?observation ?p ?o " +
					//"FROM <qb-asylum> " +
					"FROM <"+dataset+"> " +	
					"FROM <qb-definitions> " +
					"WHERE {" +
								"{{?p a qb:DimensionProperty }" +
								"UNION" +
								"{?dsd qb:component [qb:dimension ?p]}} ." +
								" ?observation a qb:Observation ; ?p ?o " +
								"FILTER(?p!=sdmx-dimension:age)" +
								"FILTER(?p!=eurostat:unit)" +
								"}";
			System.out.println(query);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();						
			while(results.hasNext()){				
				QuerySolution rs = results.next();
				String splitter = "/";
				if(rs.get("o").toString().contains("#")) splitter = "#";
				String obsLabel = rs.get("o").toString().substring(rs.get("o").toString().lastIndexOf(splitter)+1);
				
				String old = obsLabels.get(rs.get("observation").toString());
				if(old==null) {
					obsLabels.put(rs.get("observation").toString(), dataset+", " + obsLabel);
				}
				else obsLabels.put(rs.get("observation").toString(), old+", "+obsLabel);
					
								
				String p = rs.get("p").toString();			
										
				String feature = rs.get("o").toString();
				//if(p.contains("http://purl.org/dc/terms/date")) feature = "http://reference.data.gov.uk/id/gregorian-year/"+feature;
				int index = featureList.indexOf(feature);
				//System.out.println(feature + " " + index);
				//System.out.println(rs.get("observation").toString());
				//System.out.println(obsCounter);
				matrix[observationList.indexOf(rs.get("observation").toString())][index] = 1;
				//observationRow[index] = "1";
				ArrayList<String> parents = inHierarchy(graph, feature);
				for(String parent : parents){
					//System.out.println(parents.toString());
					int parentIndex = featureList.indexOf(parent);
					if(parentIndex>-1){
						//System.out.println("Got parent " + parent + " of " + feature);
						//matrix[obsCount][parentIndex] = 1;
						//observationRow[parentIndex] = "1";
						matrix[observationList.indexOf(rs.get("observation").toString())][parentIndex] = 1;
					}
				}			
				
				
				//System.out.println(obsCount);
			}vqe.close();
		}
		
		for(int i=0 ; i<obsCount ; i++){
			String[] row = new String[featCount];
			labels.writeNext(new String[] {"["+obsLabels.get(observationList.get(i))+"]"});
			for(int j =0; j<featCount ; j++){
				row[j] = matrix[i][j].toString();
			}
			writer.writeNext(row);
			
		}
		try {
			writer.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		try {
			labels.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		
		
		
		//System.out.println(featureList.get(0));
		
		
		
		
	}
	

public static void createObservationMapsMultiEurostatCube2(){
	
	
	
	CSVWriter writer = null, labels = null;
	String[] datasets = new String[]{"poverty_nuts2_1_less", "greece_belgium_ch_nuts2_2013", "internet_households_nuts2_less"};
	try {
		  writer = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/data.csv"));
		  labels = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/labels.csv"));
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		
	}
	int obsCount = 0;
	int featCount = 0;
	ArrayList<String> observationList = new ArrayList<String>();
	String fromDatasets = "";
	for(String dataset : datasets){
		fromDatasets += "FROM <"+dataset+"> ";
	}
		String fromString = "FROM <qb-definitions> ";			
		String dimensionQuery = "SELECT distinct ?dimension " +								
				fromString +
				"WHERE {" +
					"{?dimension a qb:DimensionProperty}" +
					"UNION" +
					"{?dsd qb:component [qb:dimension ?dimension]}" +
					"filter(?dimension!=sdmx-dimension:age)" +
					"filter(?dimension!=eurostat:unit)" +
				"}";
		System.out.println(dimensionQuery);
		VirtuosoQueryExecution vqeD = VirtuosoQueryExecutionFactory.create (dimensionQuery, graph);
		ResultSet resultsD = vqeD.execSelect();
		
		while(resultsD.hasNext()){
			QuerySolution rsD = resultsD.next();	
			RDFNode dimension = rsD.get("dimension");
			String dimQuery = "SELECT DISTINCT ?feature " +
									//"FROM <qb-asylum> " +
									fromDatasets + 
									"WHERE {" +
										"?obs a qb:Observation ; <"+dimension.toString()+"> ?feature " +
									"}";
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (dimQuery, graph);
			ResultSet results = vqe.execSelect();			
			while(results.hasNext()){
				QuerySolution rs = results.next();
				String feature = rs.get("feature").toString();
				/*if(dimension.toString().contains("http://purl.org/dc/terms/date"))
					featureList.add("http://reference.data.gov.uk/id/gregorian-year/"+feature);
				else*/
					featureList.add(feature);
				featCount++;
			}vqe.close();
			
		}vqeD.close();			
		String[] featArr = new String[featureList.size()];
		for(int k=0; k<featureList.size(); k++ ) {
			String featLabel = featureList.get(k).substring(featureList.get(k).lastIndexOf("/")+1);
			if(featureList.get(k).lastIndexOf("#")>-1)
			featLabel = featureList.get(k).substring(featureList.get(k).lastIndexOf("#")+1);
			featArr[k] = featLabel;
		}
		labels.writeNext(featArr);
		
		String meta = "SELECT DISTINCT ?observation " +
				//"FROM <qb-asylum> " +
				fromDatasets + 
				"WHERE {" +
					"?observation a qb:Observation }";
		VirtuosoQueryExecution vqeMeta = VirtuosoQueryExecutionFactory.create (meta, graph);
		ResultSet resultsMeta = vqeMeta.execSelect();	
		
		while(resultsMeta.hasNext()){
			QuerySolution rsMeta = resultsMeta.next();
			observationList.add(rsMeta.get("observation").toString());
			obsCount++;
		}vqeMeta.close();
		
		
		
	
	System.out.println(featureList.size());
	System.out.println(featureList.toString());
	System.out.println(obsCount);
							
	Integer[][] matrix = new Integer[obsCount][featCount];
	for(int i = 0 ; i <obsCount ; i++ ){
		for(int j = 0 ; j <featCount; j++ ){
			matrix[i][j] = 0;
		}
	}
	
	HashMap<String, String> obsLabels = new HashMap<String, String>();	
		String query = "SELECT DISTINCT ?dataset ?observation ?p ?o " +
				//"FROM <qb-asylum> " +
				fromDatasets + 
				"FROM <qb-definitions> " +
				"WHERE {" +
							"{{?p a qb:DimensionProperty }" +
							"UNION" +
							"{?dsd qb:component [qb:dimension ?p]}} ." +
							" graph ?dataset {" + 
								" ?observation a qb:Observation ; ?p ?o " +
								"FILTER(?p!=sdmx-dimension:age)" +
								"FILTER(?p!=eurostat:unit)" +
							"}" + 
							"}";
		System.out.println(query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();						
		while(results.hasNext()){				
			QuerySolution rs = results.next();
			String dataset = rs.get("dataset").toString();
			String splitter = "/";
			if(rs.get("o").toString().contains("#")) splitter = "#";
			String obsLabel = rs.get("o").toString().substring(rs.get("o").toString().lastIndexOf(splitter)+1);
			
			String old = obsLabels.get(rs.get("observation").toString());
			if(old==null) {
				obsLabels.put(rs.get("observation").toString(), dataset+", " + obsLabel);
			}
			else obsLabels.put(rs.get("observation").toString(), old+", "+obsLabel);
				
							
			String p = rs.get("p").toString();			
									
			String feature = rs.get("o").toString();
			//if(p.contains("http://purl.org/dc/terms/date")) feature = "http://reference.data.gov.uk/id/gregorian-year/"+feature;
			int index = featureList.indexOf(feature);
			System.out.println(feature + " " + index);
			System.out.println(rs.get("observation").toString());
			//System.out.println(obsCounter);
			matrix[observationList.indexOf(rs.get("observation").toString())][index] = 1;
			//observationRow[index] = "1";
			ArrayList<String> parents = inHierarchy(graph, feature);
			for(String parent : parents){
				//System.out.println(parents.toString());
				int parentIndex = featureList.indexOf(parent);
				if(parentIndex>-1){
					//System.out.println("Got parent " + parent + " of " + feature);
					//matrix[obsCount][parentIndex] = 1;
					//observationRow[parentIndex] = "1";
					matrix[observationList.indexOf(rs.get("observation").toString())][parentIndex] = 1;
				}
			}			
			
			
			//System.out.println(obsCount);
		}vqe.close();
	
	
	for(int i=0 ; i<obsCount ; i++){
		String[] row = new String[featCount];
		labels.writeNext(new String[] {"["+obsLabels.get(observationList.get(i))+"]"});
		for(int j =0; j<featCount ; j++){
			row[j] = matrix[i][j].toString();
		}
		writer.writeNext(row);
		
	}
	try {
		writer.close();
	} catch (IOException e) {			
		e.printStackTrace();
	}
	try {
		labels.close();
	} catch (IOException e) {			
		e.printStackTrace();
	}
		
}

	
	
	
public static void createObservationMapsEurostatCube(){
		
		CSVWriter writer = null, labels = null;
		String dataset = "poverty-nuts2";
		try {
			  writer = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/data_"+dataset+"_nuts2.csv"));
			  labels = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/labels_"+dataset+"_nuts2.csv"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		
		String connectionString = "jdbc:virtuoso://83.212.121.252:1111/autoReconnect=true/charset=UTF-8/log_enable=2";				
		VirtGraph graph = new VirtGraph (connectionString, "dba", "olv@psnet@openlink69");
		String dimensionQuery = "SELECT distinct ?dimension " +								
								"FROM <"+dataset+"> " +
								"WHERE {" +
									"{?dimension a qb:DimensionProperty}" +
									"UNION" +
									"{?dsd qb:component [qb:dimension ?dimension]}" +
									"filter(?dimension!=sdmx-dimension:age)" +
									"filter(?dimension!=eurostat:unit)" +
								"}";
		System.out.println(dimensionQuery);
		VirtuosoQueryExecution vqeD = VirtuosoQueryExecutionFactory.create (dimensionQuery, graph);
		ResultSet resultsD = vqeD.execSelect();
		
		int featCount = 0;
		while(resultsD.hasNext()){
			QuerySolution rsD = resultsD.next();	
			RDFNode dimension = rsD.get("dimension");
			String dimQuery = "SELECT DISTINCT ?feature " +
									//"FROM <qb-asylum> " +
									"FROM <"+dataset+"> " +
									"WHERE {" +
										"?obs a qb:Observation ; <"+dimension.toString()+"> ?feature " +
									"}";
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (dimQuery, graph);
			ResultSet results = vqe.execSelect();			
			while(results.hasNext()){
				QuerySolution rs = results.next();
				String feature = rs.get("feature").toString();
				if(dimension.toString().contains("http://purl.org/dc/terms/date"))
					featureList.add("http://reference.data.gov.uk/id/gregorian-year/"+feature);
				else
					featureList.add(feature);
				featCount++;
			}vqe.close();
			
		}vqeD.close();
		System.out.println(featureList.size());
		System.out.println(featureList.toString());
		String[] featArr = new String[featureList.size()];
		for(int k=0; k<featureList.size(); k++ ) {
			String featLabel = featureList.get(k).substring(featureList.get(k).lastIndexOf("/")+1);
			if(featureList.get(k).lastIndexOf("#")>-1)
			featLabel = featureList.get(k).substring(featureList.get(k).lastIndexOf("#")+1);
			featArr[k] = featLabel;
		}
		labels.writeNext(featArr);
		String meta = "SELECT DISTINCT ?observation " +
						//"FROM <qb-asylum> " +
						"FROM <"+dataset+"> " +
						"WHERE {" +
							"?observation a qb:Observation }";
		VirtuosoQueryExecution vqeMeta = VirtuosoQueryExecutionFactory.create (meta, graph);
		ResultSet resultsMeta = vqeMeta.execSelect();	
		int obsCount = 0;
		ArrayList<String> observationList = new ArrayList<String>();
		while(resultsMeta.hasNext()){
			QuerySolution rsMeta = resultsMeta.next();
			observationList.add(rsMeta.get("observation").toString());
			obsCount++;
		}vqeMeta.close();
		System.out.println(obsCount);
		
		Integer[][] matrix = new Integer[obsCount][featCount];
		for(int i = 0 ; i <obsCount ; i++ ){
			for(int j = 0 ; j <featCount; j++ ){
				matrix[i][j] = 0;
			}
		}
		
		String query = "SELECT DISTINCT ?observation ?p ?o " +
				//"FROM <qb-asylum> " +
				"FROM <"+dataset+"> " +				
				"WHERE {" +
							"{{?p a qb:DimensionProperty }" +
							"UNION" +
							"{?dsd qb:component [qb:dimension ?p]}} ." +
							" ?observation a qb:Observation ; ?p ?o " +
							"FILTER(?p!=sdmx-dimension:age)" +
							"FILTER(?p!=eurostat:unit)" +
							"}";
		System.out.println(query);
		//System.out.println(featureList.get(0));
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		HashMap<String, String> obsLabels = new HashMap<String, String>();
		int obsCounter = 0;
		while(results.hasNext()){
			obsCounter++;
			QuerySolution rs = results.next();
			String splitter = "/";
			if(rs.get("o").toString().contains("#")) splitter = "#";
			String obsLabel = rs.get("o").toString().substring(rs.get("o").toString().lastIndexOf(splitter)+1);
			
			String old = obsLabels.get(rs.get("observation").toString());
			if(old==null) {
				obsLabels.put(rs.get("observation").toString(), obsLabel);
			}
			else obsLabels.put(rs.get("observation").toString(), old+", "+obsLabel);
				
							
			String p = rs.get("p").toString();			
									
			String feature = rs.get("o").toString();
			if(p.contains("http://purl.org/dc/terms/date")) feature = "http://reference.data.gov.uk/id/gregorian-year/"+feature;
			int index = featureList.indexOf(feature);
			//System.out.println(feature + " " + index);
			//System.out.println(rs.get("observation").toString());
			//System.out.println(obsCounter);
			matrix[observationList.indexOf(rs.get("observation").toString())][index] = 1;
			//observationRow[index] = "1";
			ArrayList<String> parents = inHierarchy(graph, feature);
			for(String parent : parents){
				//System.out.println(parents.toString());
				int parentIndex = featureList.indexOf(parent);
				if(parentIndex>-1){
					//System.out.println("Got parent " + parent + " of " + feature);
					//matrix[obsCount][parentIndex] = 1;
					//observationRow[parentIndex] = "1";
					matrix[observationList.indexOf(rs.get("observation").toString())][parentIndex] = 1;
				}
			}			
			
			
			//System.out.println(obsCount);
		}vqe.close();
		
		
		for(int i=0 ; i<obsCount ; i++){
			String[] row = new String[featCount];
			labels.writeNext(new String[] {"["+obsLabels.get(observationList.get(i))+"]"});
			for(int j =0; j<featCount ; j++){
				row[j] = matrix[i][j].toString();
			}
			writer.writeNext(row);
			
		}
		try {
			writer.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		try {
			labels.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public static void createObservationMaps(){
		
		CSVWriter writer = null, labels = null;
		try {
			  writer = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/obsMatrix_nuts2.csv"));
			  labels = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/obsLabels_nuts2.csv"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		
		String connectionString = "jdbc:virtuoso://83.212.121.252:1111/autoReconnect=true/charset=UTF-8/log_enable=2";				
		VirtGraph graph = new VirtGraph (connectionString, "dba", "olv@psnet@openlink69");
		String dimensionQuery = "SELECT distinct ?dimension " +								
								"FROM <qb-definitions> " +
								"WHERE {" +
									"?dimension a qb:DimensionProperty filter(?dimension!=sdmx-dimension:age)" +
								"}";
		VirtuosoQueryExecution vqeD = VirtuosoQueryExecutionFactory.create (dimensionQuery, graph);
		ResultSet resultsD = vqeD.execSelect();
		ArrayList<String> featureList = new ArrayList<String>();
		int featCount = 0;
		while(resultsD.hasNext()){
			QuerySolution rsD = resultsD.next();	
			RDFNode dimension = rsD.get("dimension");
			String dimQuery = "SELECT DISTINCT ?feature " +
									//"FROM <qb-asylum> " +
									"FROM <greece_belgium_ch_nuts2_2013> " +
									"WHERE {" +
										"?obs <"+dimension.toString()+"> ?feature " +
									"}";
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (dimQuery, graph);
			ResultSet results = vqe.execSelect();			
			while(results.hasNext()){
				QuerySolution rs = results.next();
				featureList.add(rs.get("feature").toString());
				featCount++;
			}vqe.close();
			
		}vqeD.close();
		System.out.println(featureList.size());
		String[] featArr = new String[featureList.size()];
		for(int k=0; k<featureList.size(); k++ ) {
			String featLabel = featureList.get(k).substring(featureList.get(k).lastIndexOf("/")+1);
			if(featureList.get(k).lastIndexOf("#")>-1)
			featLabel = featureList.get(k).substring(featureList.get(k).lastIndexOf("#")+1);
			featArr[k] = featLabel;
		}
		labels.writeNext(featArr);
		String meta = "SELECT DISTINCT ?observation " +
						//"FROM <qb-asylum> " +
						"FROM <greece_belgium_ch_nuts2_2013> " +
						"WHERE {" +
							"?observation a qb:Observation }";
		VirtuosoQueryExecution vqeMeta = VirtuosoQueryExecutionFactory.create (meta, graph);
		ResultSet resultsMeta = vqeMeta.execSelect();	
		int obsCount = 0;
		ArrayList<String> observationList = new ArrayList<String>();
		while(resultsMeta.hasNext()){
			QuerySolution rsMeta = resultsMeta.next();
			observationList.add(rsMeta.get("observation").toString());
			obsCount++;
		}vqeMeta.close();
		System.out.println(obsCount);
		
		Integer[][] matrix = new Integer[obsCount][featCount];
		for(int i = 0 ; i <obsCount ; i++ ){
			for(int j = 0 ; j <featCount; j++ ){
				matrix[i][j] = 0;
			}
		}
		
		String query = "SELECT DISTINCT ?observation ?p ?o " +
				//"FROM <qb-asylum> " +
				"FROM <greece_belgium_ch_nuts2_2013> " +
				"FROM <qb-definitions> " +
				"WHERE {" +
							"?p a qb:DimensionProperty . ?observation ?p ?o FILTER(?p!=sdmx-dimension:age)}";
		System.out.println(query);
		//System.out.println(featureList.get(0));
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		HashMap<String, String> obsLabels = new HashMap<String, String>();
		while(results.hasNext()){
			QuerySolution rs = results.next();
			String splitter = "/";
			if(rs.get("o").toString().contains("#")) splitter = "#";
			String obsLabel = rs.get("o").toString().substring(rs.get("o").toString().lastIndexOf(splitter)+1);
			
			String old = obsLabels.get(rs.get("observation").toString());
			if(old==null) {
				obsLabels.put(rs.get("observation").toString(), obsLabel);
			}
			else obsLabels.put(rs.get("observation").toString(), old+", "+obsLabel);
				
							
								
									
			String feature = rs.get("o").toString();
			int index = featureList.indexOf(feature);
			matrix[observationList.indexOf(rs.get("observation").toString())][index] = 1;
			//observationRow[index] = "1";
			ArrayList<String> parents = inHierarchy(graph, feature);
			for(String parent : parents){
				//System.out.println(parents.toString());
				int parentIndex = featureList.indexOf(parent);
				if(parentIndex>-1){
					//System.out.println("Got parent " + parent + " of " + feature);
					//matrix[obsCount][parentIndex] = 1;
					//observationRow[parentIndex] = "1";
					matrix[observationList.indexOf(rs.get("observation").toString())][parentIndex] = 1;
				}
			}			
			
			
			//System.out.println(obsCount);
		}vqe.close();
		
		
		for(int i=0 ; i<obsCount ; i++){
			String[] row = new String[featCount];
			labels.writeNext(new String[] {"["+obsLabels.get(observationList.get(i))+"]"});
			for(int j =0; j<featCount ; j++){
				row[j] = matrix[i][j].toString();
			}
			writer.writeNext(row);
			
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			labels.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> inHierarchy(VirtGraph graph, String feature){
		
		ArrayList<String> parents = new ArrayList<String>();
		if(feature.contains("/EL")) feature = feature.replace("/EL", "/GR");
		if(feature.contains("/dic/geo#")) feature = feature.replace("/dic/geo#", "http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/");
		String query = "SELECT ?parent FROM <qb-codelists> WHERE {" +
							"<"+feature+">  " +
									"nuts-schema:hasParentRegion/nuts-schema:hasParentRegion* ?parent" +
							"}";
		//System.out.println(query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();		
		while(results.hasNext()){
			QuerySolution rs = results.next();
			parents.add(rs.get("parent").toString().replace("/GR", "/EL"));
			//System.out.println(feature+" has parent " + rs.get("parent").toString());
		}vqe.close();
		if(feature.contains("gregorian")){
			//System.out.println(feature);
			String date = feature.substring(feature.lastIndexOf("/")+1);
			if(date.contains("M")) parents.add("http://reference.data.gov.uk/id/gregorian-year/"+date.substring(0,3));
		}		
		if(feature.contains("sex-M") || feature.contains("sex-F")){
			parents.add(feature.replaceAll("sex-M", "sex-T").replaceAll("sex-F", "sex-T"));			
			
		}
		return parents;
	}
	


}
