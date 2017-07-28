package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import models.CubeMask;
import models.Dimension;
import models.DimensionFactory;
import models.Lattice;
import models.ObjectSizeFetcher;
import models.Observation;
import models.TreeHierarchy;
import models.ValueBigram;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import app.DataConnection;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class CubeMasking2 {

	static HashSet<String> graphs = new HashSet<String>();
	static String connectionString = "jdbc:virtuoso://83.212.121.252:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
	static String prefix = "http://imis.athena-innovation.gr/def#";
	static String codePrefix ="http://imis.athena-innovation.gr/code#";
	static ArrayList<String> featureList;
	static HashMap<String, Integer> levelMap;
	static public HashMap<String, Integer> valueIndexMap;
	static public HashMap<Integer, String> revValueIndexMap;
	//static HashMap<String, Integer> levelMap = new HashMap<String, Integer>();
	static public TreeHierarchy hierarchy;
	static HashMap<String, Observation> obs;
	static Set<Dimension> dims; 
	static HashMap<Observation, ArrayList<String>> cubeBuckets;
	static HashMap<CubeMask, ArrayList<String>> cubeMaskBuckets;
	static HashMap<CubeMask, HashMap<String, ArrayList<String>>> cubeHashMaskBuckets;
	static long computed, computedPartial, nulls, start;
	static int nextIndex = 0;
	static Lattice lattice;
	//static int numberOfClusters = 4;
	static HashMap<Integer, HashSet<Observation>> valueObservationMap = new HashMap<Integer, HashSet<Observation>>();
	static HashMap<Observation, HashMap<CubeMask, HashSet<ValueBigram>>> observationFilterSet = new HashMap<Observation, HashMap<CubeMask,HashSet<ValueBigram>>>();
	static int totalCompar, totalCompar2 = 0;
	static HashMap<String, HashSet<Observation>> invertedIndex = new HashMap<String, HashSet<Observation>>(); 
	
public static long runTest(String[] args){
		
	System.out.println("dsfdsf");
		args[0] = "output_nonsense";
		hierarchy = new TreeHierarchy(new File(args[0]));
		
		start = System.nanoTime();    						
		populateGraphs();
		createLevelMap();
		createHierarchyMap();		
		createHashMap();				
		long elapsedTime = System.nanoTime() - start;
		//System.out.println("Elapsed time: " + elapsedTime);
		//System.out.println("Nulls: "+nulls);
		return elapsedTime;
	}
	
	public static void init(){
		computed = 0;
		nulls = 0;
		featureList = new ArrayList<String>();
		levelMap = new HashMap<String, Integer>();
		valueIndexMap = new HashMap<String, Integer>();
		revValueIndexMap = new HashMap<Integer, String>();
	}
	
	public static void main(String[] args) {
			
		init();
		runTest(args);	
	}
	
	public static void createHashMap(){
		
		VirtGraph graph = DataConnection.getConnection();
		
		String query;
		
		obs = new HashMap<String, Observation>();
		
		HashSet<Observation> cubes = new HashSet<Observation>();
		
		cubeBuckets = new HashMap<Observation, ArrayList<String>>();
		
		cubeMaskBuckets = new HashMap<CubeMask, ArrayList<String>>();
		
		cubeHashMaskBuckets = new HashMap<CubeMask, HashMap<String,ArrayList<String>>>();
		
		HashMap<Observation, Integer> cubeSizes = new HashMap<Observation, Integer>();
		int count = 0;
		for(String gra : graphs){
			//if(!gra.contains("citizenship")) continue;
			query = " DEFINE input:same-as \"yes\""
					+" SELECT DISTINCT ?observation ?dimension ?value" + 
					" FROM <"+gra+"> " +
					" FROM <codelists.age> " +
					" FROM <codelists.sex> " +
					" FROM <codelists.location> " +
					" FROM <codelists.admin.divisions> " +
					" FROM <codelists_edu> " +
					" FROM <codelists.sameas> " + 
					" WHERE {" + 
					"	?observation a qb:Observation ; ?dimension ?value . filter(?dimension!=rdf:type)" +
					//"   ?value imis:level ?level ." + 
					 "}"; // ORDER BY ASC(?observation)
			System.out.println(query);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();
			while(results.hasNext()) {
				//if(count > 1000000) break;
				count++;
				QuerySolution rs = results.next();
				Observation current;
				if(!obs.containsKey(rs.get("observation").toString())) {
					
					current = new Observation(rs.get("observation").toString());	
					
				}
				else{
					
					current = obs.get(rs.get("observation").toString());
					
				}
				
				Dimension dimension = DimensionFactory.getInstance().getDimensionByRepresentative(rs.get("dimension").toString());				
				if(dimension==null) {	
					//System.out.println(rs.get("dimension").toString());
					continue;
				}
//				if(dimension.getRepresentative().equals("citizenship")){
//					System.out.println("citizenship: " + rs.get("value").toString());
//				}
				String value = rs.get("value").toString();
				if(value.contains("/")){
					value = rs.get("value").toString().substring(rs.get("value").toString().lastIndexOf("/")+1);
				}
				if(value.contains("#")){
					value = rs.get("value").toString().substring(rs.get("value").toString().lastIndexOf("#")+1);
				}
				if(value.contains("EL"))
					value.replaceAll("EL", "GR");
				if(value.contains("Greece"))
					value = "GR";
//				if(dimension.getRepresentative().equals("education_level")){
//					System.out.println(value);
//					System.out.println(levelMap.get(value));
//				}
				
				//valueIndexMap.get(value)
				current.setDimensionValue(dimension, value);
				
				if(invertedIndex.containsKey(value)){
					HashSet<Observation> s = invertedIndex.get(value);
					s.add(current);
					invertedIndex.put(value, s);
				}
				else{
					HashSet<Observation> s = new HashSet<Observation>();
					s.add(current);
					invertedIndex.put(value, s);
				}
					
				
				try{				
				
					current.setDimensionLevel(dimension, levelMap.get(value));					
				
				}catch(NullPointerException e){				
					
					continue;					
				
				}
				
				obs.put(rs.get("observation").toString(), current);									
				
			}
			vqe.close();
		}
		
		System.out.println(count);
		System.out.println("Inverted Index size of keys: " + invertedIndex.size());
		Set<String> obsKeys = obs.keySet();
		start = System.nanoTime();
		ArrayList<Dimension> allDims = DimensionFactory.getInstance().getDimensions();
		
		dims = null;
		
		for(String obsKey : obsKeys){
			//next = "";
			Observation o = obs.get(obsKey);
			if(o.toString().equals("")) continue;
			//System.out.println(o.toString());
			dims = o.getImplicitDimensions();	
			//System.out.println("before: "  + dims.toString());
			for(Dimension curDim : allDims){
				if(!dims.contains(curDim)){					
					//dims.add(curDim);
					o.setDimensionLevel(curDim, -1);
					o.setDimensionValue(curDim, "http://www.imis.athena-innovation.gr/def#TopConcept");
				}
				//next += "_"+o.getDimensionValue(curDim);
				obs.put(obsKey, o);
			}
			
		
		}
		dims = new HashSet<Dimension>();
		for(Dimension curDim : allDims){
			dims.add(curDim);
		}
		System.out.println(allDims.toString());
	
		int previousCount = -1;
		for(String obsKey : obsKeys){
			Observation o = obs.get(obsKey);
			if(o.toString().equals("")) continue;			
				dims = o.getDimensions();
				if(previousCount==-1) previousCount = dims.size();
				else
				{
					if(previousCount!=dims.size()) System.out.println("Error in dims size. " + previousCount + " vs. " + dims.size());
					previousCount = dims.size();
				}						
		}
		ArrayList<String> cc, cc2;
		HashMap<String, ArrayList<String>> cc3;
		HashSet<CubeMask> new_cubes = new HashSet<CubeMask>();
		HashMap<Observation, CubeMask> obsCubeMap = new HashMap<Observation, CubeMask>();
		for(String obsKey : obsKeys){
			CubeMask new_cube = new CubeMask();
			//new_cube.se
			
			//System.out.println(obs.get(obsKey).getDeweyString());
			for(Dimension dim : obs.get(obsKey).getDimensions()){
				new_cube.setDimensionLevel(dim, obs.get(obsKey).getDimensionLevel(dim));
			}
			new_cubes.add(new_cube);
			try{									
				cubes.add(obs.get(obsKey));
				
				if(cubeBuckets.containsKey(obs.get(obsKey))) cc = cubeBuckets.get(obs.get(obsKey));
				
				else cc = new ArrayList<String>();
				
				cc.add(obsKey);
				
				cubeBuckets.put(obs.get(obsKey), cc);
				
				if(cubeMaskBuckets.containsKey(new_cube)) cc2 = cubeMaskBuckets.get(new_cube);
				
				else cc2 = new ArrayList<String>();
				
				cc2.add(obsKey);
								
				cubeMaskBuckets.put(new_cube, cc2);
				obsCubeMap.put(obs.get(obsKey), new_cube);
			
			
			}catch(NullPointerException e){				
				e.printStackTrace();
				break;
			}
			if(cubeSizes.containsKey(obs.get(obsKey))){
			
				cubeSizes.put(obs.get(obsKey), cubeSizes.get(obs.get(obsKey))+1);
			
			}
			else
				cubeSizes.put(obs.get(obsKey), 1);
					
		}
		
		System.out.println("Total observations: " + obs.size());
		System.out.println("Total unique cubes: " + cubes.size());
		System.out.println("Total new cubes: " + new_cubes.size());
		HashMap<CubeMask, HashSet<CubeMask>> adjacencyLists = new HashMap<CubeMask, HashSet<CubeMask>>();
		for(CubeMask mask : new_cubes){
			
			if(cubeHashMaskBuckets.containsKey(mask)) cc3 = cubeHashMaskBuckets.get(mask);
			
			else cc3 = new HashMap<String, ArrayList<String>>();
			
			for(String o : cubeMaskBuckets.get(mask)){
				if(cc3.containsKey(obs.get(o).getDeweyString())){
					ArrayList<String> cc4 = cc3.get(obs.get(o).getDeweyString());
					cc4.add(o);
					cc3.put(obs.get(o).getDeweyString(), cc4);
					
				}
				else{
					ArrayList<String> cc4 = new ArrayList<String>();
					cc4.add(o);
					cc3.put(obs.get(o).getDeweyString(), cc4);
				}
				//cc3.add(obs.get(o).getDeweyString());
			}
				
			
			cubeHashMaskBuckets.put(mask, cc3);
			//if(true) continue;
			boolean child = false;
			//System.out.println(mask.toString());
			//Set<Dimension> mask_dims = mask.getDimensions();
			for(CubeMask inner_mask : new_cubes){
				if(inner_mask == mask) continue;
				//Set<Dimension> inner_mask_dims = inner_mask.getDimensions();
				for(Dimension dim : mask.getDimensions()){
					if(mask.getDimensionLevel(dim) > inner_mask.getDimensionLevel(dim)){
						child = false;
						break;
					}
					else{
						child = true;
						continue;
					}
				}
				if(child){
					HashSet<CubeMask> children ;
					if(adjacencyLists.containsKey(mask)){
						children = adjacencyLists.get(mask);
					}
					else{
						children = new HashSet<CubeMask>();
					}
					children.add(inner_mask);
					adjacencyLists.put(mask, children);
					//add child
					//System.out.println("outer: " + mask.toString());
					//System.out.println("inner: " + inner_mask.toString());
				}
			}			
		}
		int childcount = 0;
		for(CubeMask mask : adjacencyLists.keySet()){
			childcount += adjacencyLists.get(mask).size();						
		}
		System.out.println("childcount: " + childcount);
		System.out.println("Total cube Buckets: " + cubeBuckets.size());
		System.out.println("Total cubeMask Buckets: " + cubeMaskBuckets.size());
		//HashSet<String> uniqueCombinations = new HashSet<String>();
//		String next ;
//		for(String observation : obs.keySet()){
//			Observation o = obs.get(observation);
//			next = "";
//			for(Dimension d : o.getDimensions()){
//				next += "_" + o.getDimensionValue(d);
//			}
//			uniqueCombinations.add(next);
//		}
//		System.out.println("unique combos: " + uniqueCombinations.size());
		long elapsedTime = System.nanoTime() - start;
		System.out.println("Preprocessing - Elapsed time: " + elapsedTime);
		start = System.nanoTime();
		int obsCount = 0;
		for(Observation ob : cubeBuckets.keySet()){
			//System.out.println(cubeBuckets.get(ob).toString());
			obsCount += cubeBuckets.get(ob).size();
		}
		System.out.println("Total observations in buckets: " + obsCount);	
		
		obsCount = 0;
//		HashSet<String> uniqueValues = new HashSet<String>();
//		for(CubeMask ob : cubeMaskBuckets.keySet()){
//			//System.out.println(cubeBuckets.get(ob).toString());
//			obsCount += cubeMaskBuckets.get(ob).size();
//			for(String os : cubeMaskBuckets.get(ob)){
//				Observation o = obs.get(os);
//				for(Dimension d : o.getDimensions()){
//					uniqueValues.add(o.getDimensionValue(d));
//				}
//				//System.out.println(o.getDeweyString());
//			}
//		}
		
		HashMap<ValueBigram, HashSet<CubeMask>> bigramCubeMap = new HashMap<ValueBigram, HashSet<CubeMask>>();
		HashSet<Observation> uniqueObservations = new HashSet<Observation>();
		HashSet<String> uniqueDewey = new HashSet<String>();
		for(String observation : obs.keySet()){
			uniqueObservations.add(obs.get(observation));
			uniqueDewey.add(obs.get(observation).getDeweyString());
		}
		System.out.println("UNIQUE OBSERVATIONS: " + uniqueObservations.size());
		System.out.println("UNIQUE DEWEY: " + uniqueDewey.size());
		Observation o;
		for(String observation : obs.keySet()){
			
			o = obs.get(observation);
			//System.out.println(o.toString());
			String deweyString = o.getDeweyString();
			//System.out.println(deweyString);
			String[] values = deweyString.substring(0,deweyString.length()-1).split("/");
			//System.out.println(values.length);
			for(int i = 1; i < values.length; i++){
				
				String v1 = values[i-1], v2 = values[i];
				
				//these are the combos for the current bigram				
				ValueBigram thisBigram = new ValueBigram();
				thisBigram.setValues(v1, v2, o.getSortedDimensions().get(i-1), o.getSortedDimensions().get(i));				
				HashSet<CubeMask> cubeSet ;
				if(bigramCubeMap.containsKey(thisBigram)){
					cubeSet = bigramCubeMap.get(thisBigram);
					bigramCubeMap.remove(thisBigram);
				}
				else{
					cubeSet = new HashSet<CubeMask>();
				}
				cubeSet.add(obsCubeMap.get(o));			
				
				bigramCubeMap.put(thisBigram, cubeSet);
			
			}
			
		}
		System.out.println("obs: " + obs.keySet().size());
		System.out.println("bigrams: " + bigramCubeMap.size());
		System.out.println("bigrams: " + bigramCubeMap.keySet().toString());
		
//		for(ValueBigram next_combo : bigramCubeMap.keySet()){
//			System.out.println(next_combo.toString());
//			System.out.println(bigramCubeMap.get(next_combo).toString());
//			System.out.println("-----------------");
//		}
		int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		HashMap<CubeMask, HashSet<CubeMask>> comparisonMap = new HashMap<CubeMask, HashSet<CubeMask>>();
		int maxd = Integer.MIN_VALUE, mind = Integer.MAX_VALUE;
		CubeMask mask ;
		HashMap<CubeMask, HashSet<ValueBigram>> dimFilterSet;
		for(String observation : obs.keySet()){
			if(true) break;
			o = obs.get(observation);
			
			mask = obsCubeMap.get(o);
				
			dimFilterSet = createFilterSet(o, mask, bigramCubeMap); 						
			//maxd = Math.max(maxd, observationFilterSet.get(o).size());
			//mind = Math.min(mind, observationFilterSet.get(o).size());
			for(CubeMask mm : dimFilterSet.keySet()){
				//let's filter out masks that pass the test but are not related
				
				//if(mask == mm) continue;
				boolean isChild = true;
				for(Dimension d : dims){
					if(mask.getDimensionLevel(d) < mm.getDimensionLevel(d)){
						isChild = false;
						break;
					}
				}
				if(!isChild) continue;
//				min = Math.min(min, dimFilterSet.get(mm).size());
//				max = Math.max(max, dimFilterSet.get(mm).size());

				if(dimFilterSet.get(mm).size() == 10 ){
					if(comparisonMap.containsKey(mask)){
						HashSet<CubeMask> temp = comparisonMap.get(mask);
						temp.add(mm);
						comparisonMap.put(mask, temp);
					}
					else{
						HashSet<CubeMask> temp = new HashSet<CubeMask>();
						temp.add(mm);
						comparisonMap.put(mask, temp);
					}
				}
			}		
		}
		
//		for(Observation o : observationFilterSet.keySet()){
//			//System.out.println()
//			maxd = Math.max(maxd, observationFilterSet.get(o).size());
//			mind = Math.min(mind, observationFilterSet.get(o).size());
//		}
//		System.out.println("MAX Size: " + maxd);
//		System.out.println("MIN Size: " + mind);
		int compCount = 0;
		for(CubeMask toCompare : comparisonMap.keySet()){
//			System.out.println("cube to compare:" + toCompare.toString());
//			System.out.println("compare with: ");
//			System.out.println("\t" + comparisonMap.get(toCompare));
			compCount += comparisonMap.get(toCompare).size();
		}
		
		System.out.println("compCount: " + compCount);
		System.out.println("max appearances: " + max);
		System.out.println("min appearances: " + min);
		//System.out.println("array length: " + length);
		
		//System.out.println("Total unique values in observations: " + uniqueValues.size());
		System.out.println("Total observations in mask buckets: " + obsCount);	
//		for(String v : uniqueValues){
//			System.out.println(v);
//		}
		
		int totes = 0;		
		
		int total = 0, c = 0, total2 = 0;
		computed = 0;
		HashMap<Observation, HashSet<Observation>> latticeMap = new HashMap<Observation, HashSet<Observation>>();
		//HashMap<Integer, Integer> addedValues = new HashMap<Integer, Integer>();
		HashSet<Integer> allTopValues = new HashSet<Integer>();

		long method1_full = elapsedTime;					
		//start = System.nanoTime();
		System.out.println("Iterating through cube children...");					
		
		HashMap<CubeMask, HashSet<Vector<CubeMask>>> vectorMap = new HashMap<CubeMask, HashSet<Vector<CubeMask>>>();
 		
 		HashSet<Vector<CubeMask>> vectors = new HashSet<Vector<CubeMask>>(); 
 		
 		for(CubeMask nextMask : adjacencyLists.keySet()){
 			//if(true) break;
 			HashSet<CubeMask> visited = new HashSet<CubeMask>();
 			
 			Stack<Vector<CubeMask>> stack = new Stack<>();
 			
 			Vector<CubeMask> v = new Vector<CubeMask>();
 			
 			v.add(nextMask);
 			
 			stack.push(v);
 			
 			while(!stack.empty()){
 				
 				v = stack.pop();
 				
 				CubeMask current = v.lastElement();
 				
 				visited.add(current);
 				
 				if(!adjacencyLists.containsKey(current)){
 				
 					if(vectorMap.containsKey(current))
 						vectorMap.get(current).add(v);
 					else{
 						HashSet<Vector<CubeMask>> d = new HashSet<Vector<CubeMask>>();
 						d.add(v);
 						vectorMap.put(current, d); 						
 					}
 					vectors.add(v);
 					continue;
 					
 				}
 				
 				for(CubeMask child : adjacencyLists.get(current)){
 					if(!visited.contains(child)){
 						Vector<CubeMask> _v = new Vector<CubeMask>();
 						_v.addAll(v);
 						_v.add(child);
 						stack.push(_v);
 					}
 				}
 				
 				
 			}
 		}
 		
 		System.out.println("total patterns: " + vectors.size());
		int spaceCalc = 0;
 		for(Vector<CubeMask> nextVector : vectors){
 			spaceCalc += nextVector.size();
 		}
 		System.out.println("spaceCalc: " + spaceCalc);
 		//System.out.println(ObjectSizeFetcher.getObjectSize(spaceCalc));
 		//DirectedGraph<Integer, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
 		//start = System.nanoTime();
		for(CubeMask cube1 : cubeMaskBuckets.keySet()){
			
			//total2 += latticeMap.get(cube1).size();
			//if(!adjacencyLists.containsKey(cube1)) continue;
			
			for(CubeMask cube2 : cubeMaskBuckets.keySet()){
				//if(cube1 == cube2) continue;
				boolean isChild = true;
				for(Dimension d : dims){
					if(cube1.getDimensionLevel(d) < cube2.getDimensionLevel(d)){
						isChild = false;
						break;
					}
				}
				if(!isChild) continue;
				total2 ++ ;
				totalCompar += cubeMaskBuckets.get(cube1).size()*cubeMaskBuckets.get(cube2).size();
				//totalCompar += cubeMaskBuckets.get(cube2).size();
				//computeContainmentMask(graph, cube1, cube2, bigramCubeMap, false);
				//computeContainmentHashMask(graph, cube1, cube2);
				computePartialContainmentHashMask(graph, cube1, cube2);
				//870
				//75
			}
			
		}
		System.out.println(allTopValues.toString());
		System.out.println(total + " containment comparisons between cubes:");
		System.out.println(total2 + " new containment comparisons between cubes:");
		System.out.println(totalCompar + " new containment comparisons between observations:");
		System.out.println(totalcc + " new containment comparisons between observations:");
		System.out.println("Computed full: " + computed);		
		//System.out.println("Total comparisons: " + totes);
		elapsedTime = System.nanoTime() - start;
		System.out.println("FULL CONTAINMENT method 2: Elapsed time: " + elapsedTime);
 		start = System.nanoTime();
 		totalCompar = 0;
 		computed = 0;
 		for(CubeMask cube1 : comparisonMap.keySet()){
 			for(CubeMask cube2 : comparisonMap.get(cube1)){
 				
 				//computeContainmentMask(graph, cube1, cube2, bigramCubeMap, true);
 				computeContainmentHashMask(graph, cube1, cube2);
 				totalCompar += cubeMaskBuckets.get(cube1).size()*cubeMaskBuckets.get(cube2).size();
 			}
 		}
 		System.out.println("totalCompar with new method: " + totalCompar);
 		System.out.println("totalCompar2 with new method: " + totalCompar2);
 		elapsedTime = System.nanoTime() - start;
		System.out.println("FULL CONTAINMENT method new new: Elapsed time: " + elapsedTime);
		System.out.println("Computed full: " + computed);		
		
		System.out.println("total comparisons:::: " + comparisonsTotal);
		System.out.println("total comparisons2:::: " + comparisonsTotal2);
		computed = 0;
		totalCompar = 0;
		totalcc = 0;
		
		
		
		
		long method2_full = elapsedTime;
		System.out.println("rate of execution time: " + (double) ((double) method2_full / (double)method1_full));
		method1_full = cubes.size()*cubes.size();
		method2_full = total;
		System.out.println("rate of comparisons: " + (double) ((double) method2_full / (double)method1_full));
		System.out.println("total comparisons:::: " + comparisonsTotal);
		if(true) return;
		start = System.nanoTime();
		total = 0;
		c = 0;
		totes = 0;
		computedPartial = 0;
		for(Observation obs1 : cubes){
			
			for(Observation obs2 : cubes){
				//if(obs1.equals(obs2)) continue;
				int cont = 0, cont_rev = 0;
				for(Dimension d : dims){
					
					if(obs1.getDimensionLevel(d)>obs2.getDimensionLevel(d)) {				
						cont++;						
						break;
					}
					/*if(obs2.getDimensionLevel(d)>=obs1.getDimensionLevel(d)) {
						cont_rev++;
					}*/
				}				
				if(cont>0){
					computePartialContainment(graph, obs1, obs2);
				}				
		}
		
		}
		System.out.println(total + " containment comparisons between cubes to be done.");
		System.out.println("Computed full: " + computed);
		System.out.println("Computed partial: " + computedPartial);
		System.out.println("Total comparisons: " + totes);
		graph.close();
		elapsedTime = System.nanoTime() - start;
		System.out.println("PARTIAL CONTAINMENT: Elapsed time: " + elapsedTime);
		
		
	}
	
	public static HashMap<CubeMask, HashSet<Observation>> overallCompMap = new HashMap<CubeMask, HashSet<Observation>>();
	
	private static HashMap<CubeMask, HashSet<ValueBigram>> createFilterSet(Observation o, CubeMask mask, HashMap<ValueBigram, HashSet<CubeMask>> bigramCubeMap) {
		
		if(!observationFilterSet.containsKey(o)){
			HashMap<CubeMask, HashSet<ValueBigram>> dimFilterSet = new HashMap<>();
			
			String deweyString = o.getDeweyString();
			
			String[] values = deweyString.substring(0,deweyString.length()-1).split("/");
			String v1, v2;
			ArrayList<String> parentsV1 = new ArrayList<String>(), parentsV2 = new ArrayList<String>();
			HashSet<ValueBigram> bigram_combinations;
			
			for(int i = 1; i < values.length; i++){
				
				//get next bigram values
				v1 = values[i-1];
				v2 = values[i];
				
				//init parent arrays
				parentsV1.clear();
				parentsV2.clear();;
				parentsV1.add(v1);
				parentsV2.add(v2);
				
				//find all parents
				while(v1.lastIndexOf('.') > -1){
					
					v1 = v1.substring(0, v1.lastIndexOf('.')); //get next parent
					
					parentsV1.add(v1); //add next parent
										
					
				}
				
				while(v2.lastIndexOf('.') > -1){
					
					v2 = v2.substring(0, v2.lastIndexOf('.')); //get next parent
					
					parentsV2.add(v2); //add next parent
										
					
				}
				bigram_combinations = new HashSet<ValueBigram>(); 

				//get parent bigram permutations
				ValueBigram next_combo ;
				for(int i1 = 0; i1 < parentsV1.size(); i1++){
					for(int i2 = 0; i2 < parentsV2.size(); i2++){
						
						//if(parentsV1.get(i1).equals("0") && parentsV2.get(i2).equals("0") && (i1+i2 > 0)) continue;
						next_combo = new ValueBigram();

						next_combo.setValues(parentsV1.get(i1), parentsV2.get(i2), 
								o.getSortedDimensions().get(i-1), o.getSortedDimensions().get(i));
						
						bigram_combinations.add(next_combo);
						
					}
				}
				HashSet<CubeMask> parentMasks ;
				for(ValueBigram bigram : bigram_combinations){
					
					parentMasks = bigramCubeMap.get(bigram);
					
					if(parentMasks == null) continue;					
					
					for(CubeMask nextParent : parentMasks){
						boolean isChild = true;
						for(Dimension d : dims){
							if(mask.getDimensionLevel(d) < nextParent.getDimensionLevel(d)){
								isChild = false;
								break;
							}
						}
						if(!isChild) continue;
//						HashSet<Observation> compObs ;
//						if(overallCompMap.containsKey(nextParent)){
//							compObs = overallCompMap.get(nextParent);
//						}
//						else{
//							compObs = new HashSet<Observation>();
//						}
//						compObs.add(o);
//						overallCompMap.put(nextParent, compObs);
						if(dimFilterSet.containsKey(nextParent)){
							HashSet<ValueBigram> dimPairs = dimFilterSet.get(nextParent);
							ValueBigram dimPair = new ValueBigram();
							dimPair.setValues("", "", bigram.dim1, bigram.dim2);
							dimPairs.add(dimPair);
							dimFilterSet.put(nextParent, dimPairs);
						}
						else{
							HashSet<ValueBigram> dimPairs = new HashSet<ValueBigram>();
							ValueBigram dimPair = new ValueBigram();
							dimPair.setValues("", "", bigram.dim1, bigram.dim2);
							dimPairs.add(dimPair);
							dimFilterSet.put(nextParent, dimPairs);
						}
					}
				}
				
			}
			observationFilterSet.put(o, dimFilterSet);
		}
		
		return observationFilterSet.get(o);
	}

	private static boolean worthComputing(Observation cube1, Observation cube2) {		
		return true;
	}

	public static boolean computePartialContainment(VirtGraph graph, Observation obs1, Observation obs2){
		
		//System.out.println(obs1.toString() + " with " + obs2.toString());
		int count;		
		boolean isPartial = false;
		ArrayList<String> o1 = cubeBuckets.get(obs1);
		ArrayList<String> o2 = cubeBuckets.get(obs2);
		TreeHierarchy.HierarchyNode node1, node2;
		for(String o1URI : o1){
			Observation o1Obs = obs.get(o1URI);
			for(String o2URI : o2){
				Observation o2Obs = obs.get(o2URI);
				count = 0;
				for(Dimension d : dims){
					//if(o1Obs.getDimensionLevel(d) < o2Obs.getDimensionLevel(d)) break; 
					node1 = hierarchy.getNode(o1Obs.getDimensionValue(d));
					node2 = hierarchy.getNode(o2Obs.getDimensionValue(d));	
					if(o2Obs.getDimensionValue(d)==null || o2Obs.getDimensionValue(d).equals("top")){
						//count++;
						//continue;
						nulls++;
						break;
					}
					if(o1Obs.getDimensionLevel(d) < o2Obs.getDimensionLevel(d)) break; 
					node1 = hierarchy.getNode(o1Obs.getDimensionValue(d));
					node2 = hierarchy.getNode(o2Obs.getDimensionValue(d));
					if(node1==null) {
						System.out.println("Null1 Dimension " + d + "value" + o1Obs.getDimensionValue(d));
						nulls++;
						break;
					}
					if(node2==null) {
						System.out.println("Null2 Dimension " + d + "value" + o2Obs.getDimensionValue(d));
						nulls++;
						break;
					}
					if(node2.isParentOf(node1)){
						isPartial = true;						
						//break;
					}
				}				
			}
		}			
		if(isPartial) computedPartial++;
		return isPartial;
		
	}
	
	public static boolean computeContainment(VirtGraph graph, Observation cube1, Observation cube2){
				
		int count;		
		ArrayList<String> o1 = cubeBuckets.get(cube1);
		ArrayList<String> o2 = cubeBuckets.get(cube2);
		TreeHierarchy.HierarchyNode node1, node2;
		for(String o1URI : o1){
			Observation o1Obs = obs.get(o1URI);
			for(String o2URI : o2){
				Observation o2Obs = obs.get(o2URI);
				count = 0;
				for(Dimension d : dims){
					/*if(o2Obs.getDimensionValue(d)==null || o2Obs.getDimensionValue(d).equals("top")){
						//count++;
						//continue;
						nulls++;
						break;
					}*/
					if(o1Obs.getDimensionLevel(d) < o2Obs.getDimensionLevel(d)) break; 
					node1 = hierarchy.getNode(o1Obs.getDimensionValue(d));
					node2 = hierarchy.getNode(o2Obs.getDimensionValue(d));
					/*if(node1==null) {
						System.out.println("Null1 Dimension " + d + "value" + o1Obs.getDimensionValue(d));
						nulls++;
						break;
					}
					if(node2==null) {
						System.out.println("Null2 Dimension " + d + "value" + o2Obs.getDimensionValue(d));
						nulls++;
						break;
					}*/
					if(node2.isParentOf(node1)){					
						count++;
					} else break;
				}
				if(count==dims.size()){
					
					computed++;					
				}
			}
		}
		
		//if(count>0)System.out.println(count);
		
		return false;
		
	}
	
	static int totalcc = 0;
	
	public static boolean computeContainmentDeweyMask(VirtGraph graph, CubeMask cube1, CubeMask cube2){
		
		int count;		
		ArrayList<String> o1 = cubeMaskBuckets.get(cube1);
		ArrayList<String> o2 = cubeMaskBuckets.get(cube2);
		for(String o1URI : o1){

			Observation o1Obs = obs.get(o1URI);
			String o1Dewey = o1Obs.getDeweyString();
			String[] o1DeweyArray = o1Dewey.split("/");
			
			for(String o2URI : o2){
				
				totalcc++;
				boolean isParent = true;
				Observation o2Obs = obs.get(o2URI);
				String o2Dewey = o2Obs.getDeweyString();
				String[] o2DeweyArray = o2Dewey.split("/");
				for(int i = 0; i < o1DeweyArray.length; i++){
					if(!o2DeweyArray[i].contains(o1DeweyArray[i])){
						isParent = false;
						break;
					}
				}
				if(isParent){
					computed++;
				}
				
			}
		}
		return false;
	}
	
	
	public static HashMap<String, HashSet<String>> containmentMappings = new HashMap<String, HashSet<String>>();
	public static long comparisonsTotal2 = 0;
	public static boolean computeContainmentHashMask(VirtGraph graph, CubeMask cube1, CubeMask cube2){
		
		int count;		
		ArrayList<String> o1 = cubeMaskBuckets.get(cube1);
		ArrayList<String> o2 = cubeMaskBuckets.get(cube2);
		HashMap<String, ArrayList<String>> d1 = cubeHashMaskBuckets.get(cube1);
		HashMap<String, ArrayList<String>> d2 = cubeHashMaskBuckets.get(cube2);
		int[] mask = new int[cube1.getSortedDimensions().size()];
		int index = 0;
		comparisonsTotal2 += o1.size()*o2.size();
		//if(true) return false;
		for(Dimension dim : cube1.getSortedDimensions()){
			int l1 = cube1.getDimensionLevel(dim);
			int l2 = cube2.getDimensionLevel(dim);
			mask[index] = l1-l2;
			if(index == 0 && mask[index] > 0 && l2 > -1){
				mask[index]++;
			}
			index++;			
		}
//		System.out.println(cube1.toString());
//		System.out.println(cube2.toString());
//		System.out.println(Arrays.toString(mask));
		//if(true) return true;
		for(String dewey1 : d1.keySet()){
			//apply mask
			//System.out.println("before: " + dewey1);
			String[] values = dewey1.substring(0,dewey1.length()-1).split("/");
			StringBuilder builder = new StringBuilder();
			for(int j = 0; j < mask.length; j++){
				if(mask[j] > 0){
					
					for(int j1 = 0; j1 < mask[j]; j1++){
						values[j] = values[j].substring(0, values[j].lastIndexOf('.'));
					}					
				}				
				builder.append(values[j]);
				builder.append("/");
			}
			
			//System.out.println("after: " + builder.toString());
			comparisonsTotal++;
			if(d2.containsKey(builder.toString())){
				//computed+= d2.get(builder.toString()).size()*d1.get(dewey1).size();
				for(String d1o : d1.get(dewey1)){
					for(String d2o : d2.get(builder.toString())){
						computed++;//119801418
						HashSet<String> set ;
						if(containmentMappings.containsKey(d1o)){
							set = containmentMappings.get(dewey1);
						}
						else{
							set = new HashSet<String>();
						}
						set.add(d2o);
					}
					
				}
			}
				
		}
//		for(String dewey2 : d2){
//			System.out.println("in d2 -------------------------------");
//			System.out.println(dewey2);	
//		}
		if(true) return true;
		for(String o1URI : o1){

			Observation o1Obs = obs.get(o1URI);
			String o1Dewey = o1Obs.getDeweyString();
			String[] o1DeweyArray = o1Dewey.split("/");
			
			for(String o2URI : o2){
				
				totalcc++;
				boolean isParent = true;
				Observation o2Obs = obs.get(o2URI);
				String o2Dewey = o2Obs.getDeweyString();
				String[] o2DeweyArray = o2Dewey.split("/");
				for(int i = 0; i < o1DeweyArray.length; i++){
					if(!o2DeweyArray[i].contains(o1DeweyArray[i])){
						isParent = false;
						break;
					}
				}
				if(isParent){
					computed++;
				}
				
			}
		}
		return false;
	}
	public static long comparisonsTotal = 0;
	public static boolean computePartialContainmentHashMask(VirtGraph graph, CubeMask cube1, CubeMask cube2){
		
		int count;		
		ArrayList<String> o1 = cubeMaskBuckets.get(cube1);
		ArrayList<String> o2 = cubeMaskBuckets.get(cube2);
		HashMap<String, ArrayList<String>> d1 = cubeHashMaskBuckets.get(cube1);
		HashMap<String, ArrayList<String>> d2 = cubeHashMaskBuckets.get(cube2);
		int[] mask = new int[cube1.getSortedDimensions().size()];
		int index = 0;
		comparisonsTotal2 += o1.size()*o2.size();
		for(Dimension dim : cube1.getSortedDimensions()){
			int l1 = cube1.getDimensionLevel(dim);
			int l2 = cube2.getDimensionLevel(dim);
			mask[index] = l1-l2;
			if(index == 0 && mask[index] > 0 && l2 > -1){
				mask[index]++;
			}
			index++;			
		}
//		System.out.println(cube1.toString());
//		System.out.println(cube2.toString());
//		System.out.println(Arrays.toString(mask));
		//if(true) return true;
		HashMap<String, HashSet<String>> outerSet = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> innerSet = new HashMap<String, HashSet<String>>();
		for(String dewey1 : d1.keySet()){
			//apply mask			
			comparisonsTotal++;
			String[] values = dewey1.substring(0,dewey1.length()-1).split("/");
			StringBuilder builder = new StringBuilder();
			for(int j = 0; j < mask.length; j++){
				if(mask[j] > 0){
					
					for(int j1 = 0; j1 < mask[j]; j1++){
						values[j] = values[j].substring(0, values[j].lastIndexOf('.'));
					}					
				}
				else{
					//generalize dimension
					values[j] = "0";
				}
				builder.append(values[j]);
				builder.append("/");
				//for(String obs1 : d1.get(dewey1)){
				if(outerSet.containsKey(builder.toString())){
					HashSet<String> sss = outerSet.get(builder.toString());
					sss.addAll(d1.get(dewey1));
					outerSet.put(builder.toString(), sss);
				}
				else{
					HashSet<String> sss = new HashSet<String>();
					sss.addAll(d1.get(dewey1));
					outerSet.put(builder.toString(), sss);
				}
				//}
					//outerSet.put(builder.toString());
			}
		}
		
		for(String dewey1 : d2.keySet()){
			//apply mask		
			comparisonsTotal++;
			String[] values = dewey1.substring(0,dewey1.length()-1).split("/");
			StringBuilder builder = new StringBuilder();
			for(int j = 0; j < mask.length; j++){
				if(mask[j] > 0){
													
				}
				else{
					//generalize dimension
					values[j] = "0";
				}
				builder.append(values[j]);
				builder.append("/");
				//innerSet.add(builder.toString());
				if(innerSet.containsKey(builder.toString())){
					HashSet<String> sss = innerSet.get(builder.toString());
					sss.addAll(d2.get(dewey1));
					innerSet.put(builder.toString(), sss);
				}
				else{
					HashSet<String> sss = new HashSet<String>();
					sss.addAll(d2.get(dewey1));
					innerSet.put(builder.toString(), sss);
				}
			}
			
			//System.out.println("after: " + builder.toString());			
		}
		
		for(String d1next : outerSet.keySet()){
			comparisonsTotal++;
			if(innerSet.containsKey(d1next)){
				for(String d1o : outerSet.get(d1next)){
					for(String d2o : innerSet.get(d1next)){
						computed++;//119801418
//						HashSet<String> set ;
//						if(containmentMappings.containsKey(d1o)){
//							set = containmentMappings.get(dewey1);
//						}
//						else{
//							set = new HashSet<String>();
//						}
//						set.add(d2o);
					}
					
				}
			}				
		}
		
//			if(d2.containsKey(builder.toString())){
//				//computed+= d2.get(builder.toString()).size()*d1.get(dewey1).size();
//				for(String d1o : d1.get(dewey1)){
//					for(String d2o : d2.get(builder.toString())){
//						computed++;//119801418
//						HashSet<String> set ;
//						if(containmentMappings.containsKey(d1o)){
//							set = containmentMappings.get(dewey1);
//						}
//						else{
//							set = new HashSet<String>();
//						}
//						set.add(d2o);
//					}
//					
//				}
//			}
				
		//}
//		for(String dewey2 : d2){
//			System.out.println("in d2 -------------------------------");
//			System.out.println(dewey2);	
//		}
		if(true) return true;
		for(String o1URI : o1){

			Observation o1Obs = obs.get(o1URI);
			String o1Dewey = o1Obs.getDeweyString();
			String[] o1DeweyArray = o1Dewey.split("/");
			
			for(String o2URI : o2){
				
				totalcc++;
				boolean isParent = true;
				Observation o2Obs = obs.get(o2URI);
				String o2Dewey = o2Obs.getDeweyString();
				String[] o2DeweyArray = o2Dewey.split("/");
				for(int i = 0; i < o1DeweyArray.length; i++){
					if(!o2DeweyArray[i].contains(o1DeweyArray[i])){
						isParent = false;
						break;
					}
				}
				if(isParent){
					computed++;
				}
				
			}
		}
		return false;
	}
	
	public static boolean computeContainmentMask(VirtGraph graph, CubeMask cube1, CubeMask cube2,
			HashMap<ValueBigram, HashSet<CubeMask>> bigramCubeMap, boolean filtered){
		
		int count;		
		ArrayList<String> o1 = cubeMaskBuckets.get(cube1);
		ArrayList<String> o2 = cubeMaskBuckets.get(cube2);		
		TreeHierarchy.HierarchyNode node1, node2;		
		HashMap<CubeMask, HashSet<ValueBigram>> dimFilterSet ;
		for(String o1URI : o1){
			Observation o1Obs = obs.get(o1URI);
			
			if(filtered){
				dimFilterSet = createFilterSet(o1Obs, cube1, bigramCubeMap);
				if(dimFilterSet.get(cube2).size() != 10){
					totalCompar2 += o2.size();
					continue;
				}				
						
			}
			
			for(String o2URI : o2){
				
				
				totalcc++;
				Observation o2Obs = obs.get(o2URI);
//				System.out.println("next pair");
//				System.out.println("o1: " + o1Obs.getDeweyString());
//				System.out.println("o2: " + o2Obs.getDeweyString());
				count = 0;
				for(Dimension d : dims){					
					
					if(o1Obs.getDimensionLevel(d) < o2Obs.getDimensionLevel(d)) 
						break;
					node1 = hierarchy.getNode(o1Obs.getDimensionValue(d));
					node2 = hierarchy.getNode(o2Obs.getDimensionValue(d));

//					System.out.println("node1: " + node1.toString());
//					System.out.println("node2: " + node2.toString());
//					System.out.println("------------");
					if(node2.isParentOf(node1) || node2.equals(node1)){ 						
						count++;
					} else break;
				}
				if(count==dims.size()){
					computed++;					
				}
				//System.out.println(count);
			}
		}
		
		//if(count>0)System.out.println(count);
		
		return false;
		
	}
	
	
	
	public static void populateGraphs(){
		String[] graphsS = new String[] {
			"http://linked-statistics.gr/res_pop_by_citizenship_sex_age.php",
			"http://linked-statistics.gr/households_hmembers_by_hsize.php",
			"http://linked-statistics.gr/res_pop_by_age_sex_education.php",
			"http://estatwrap.ontologycentral.com/page/demo_r_births",
			"http://estatwrap.ontologycentral.com/page/demo_r_magec",
			"http://estatwrap.ontologycentral.com/page/nama_r_e3gdp",
			"http://estatwrap.ontologycentral.com/page/nama_r_e2rem"
		};
		for(String graph : graphsS){
			graphs.add(graph);
		}
		
	}
	
	public static void createLevelMap(){		
		VirtGraph graph = DataConnection.getConnection();
		String query = " SELECT DISTINCT ?value ?level" + 					
					" FROM <codelists.age> " +
					" FROM <codelists.sex> " +
					" FROM <codelists.location> " +
					" FROM <codelists.admin.divisions> " +
					" FROM <codelists_edu> " +
					//" FROM <codelists.time> " +
					" FROM <codelists.sameas> " +
					" WHERE {" + 					
					"   {?value imis1:level ?level }" + 
					"UNION " +
					"   {?dummy owl:sameAs ?value . ?value imis1:level ?level }" + 
					 "}";
		System.out.println(query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();			
		while(results.hasNext()){				
			QuerySolution rs = results.next();
			String value = rs.get("value").toString();
			if(value.contains("/"))
				value = rs.get("value").toString().substring(rs.get("value").toString().lastIndexOf("/")+1);
			if(value.contains("#"))
				value = rs.get("value").toString().substring(rs.get("value").toString().lastIndexOf("#")+1);
			if(value.contains("EL"))
				value.replaceAll("EL", "GR");
			if(value.contains("Greece"))
				value = "GR";
			levelMap.put(value, rs.getLiteral("level").getInt());
			valueIndexMap.put(value, getNextInt());
			revValueIndexMap.put(valueIndexMap.get(value), value);
		}
		vqe.close();				
		graph.close();
//		for(String val : levelMap.keySet())
//			System.out.println(val + " " + levelMap.get(val));
	}
	
	public static int getNextInt(){
		return nextIndex++;
	}
	
	
	
	public static void createHierarchyMap(){
		
		VirtGraph graph = DataConnection.getConnection();		
		String query = " SELECT DISTINCT ?value ?parent" + 					
				" FROM <codelists.age> " +
				" FROM <codelists.sex> " +
				" FROM <codelists.location> " +
				" FROM <codelists.admin.divisions> " +
				" FROM <codelists_edu> " + 
				" FROM <codelists.sameas> " +
				//" FROM <codelists.time> " +
				" WHERE {" + 					
				"   {?value skos:broaderTransitive ?parent }" + 
				"UNION " +
				"   {?dummy owl:sameAs ?value . ?value skos:broaderTransitive ?parent }" + 
				"UNION " +
				"   {?value owl:sameAs ?dummy . ?dummy skos:broaderTransitive ?parent }" +
				 "}";
		//System.out.println(query);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();			
		while(results.hasNext()){				
			QuerySolution rs = results.next();
			//String value = ;
			if(rs.get("value").toString()==null || rs.get("parent").toString()==null) continue;
			String value = rs.get("value").toString();
			if(value.contains("/"))
				value = rs.get("value").toString().substring(rs.get("value").toString().lastIndexOf("/")+1);
			if(value.contains("#"))
				value = rs.get("value").toString().substring(rs.get("value").toString().lastIndexOf("#")+1);
			String parent = rs.get("parent").toString();
			if(parent.contains("/"))
				parent = rs.get("parent").toString().substring(rs.get("parent").toString().lastIndexOf("/")+1);
			if(parent.contains("#"))
				parent = rs.get("parent").toString().substring(rs.get("parent").toString().lastIndexOf("#")+1);
			//if(rs.get("parent").toString().contains("null")) System.out.println(rs.get("parent").toString());
			/*TreeHierarchy.HierarchyNode node = hierarchy.new HierarchyNode(rs.get("value").toString());
			TreeHierarchy.HierarchyNode parent = hierarchy.new HierarchyNode(rs.get("parent").toString());*/
			if(value.equals(parent)) continue;
			TreeHierarchy.HierarchyNode node = hierarchy.insertIntoSet(value);
			TreeHierarchy.HierarchyNode parentNode = hierarchy.insertIntoSet(parent);
			node.setParent(parentNode);
			//System.out.println(node.toString() + " has parent " + node.parent.toString());
		}
		vqe.close();
		/*query = " SELECT DISTINCT ?value ?parent" + 					
				" FROM <codelists.age> " +
				" FROM <codelists.sex> " +
				" FROM <codelists.location> " +
				" FROM <codelists.admin.divisions> " +
				" FROM <codelists.sameas> " +
				" WHERE {" + 					
				"   {?value skos:broaderTransitive ?parent }" + 
				"UNION " +
				"   {?dummy owl:sameAs ?value . ?value skos:broaderTransitive ?parent }" + 
				"UNION " +
				"   {?value owl:sameAs ?dummy . ?dummy skos:broaderTransitive ?parent }" +
				 "}";		
		vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		results = vqe.execSelect();			
		while(results.hasNext()){				
			QuerySolution rs = results.next();
			//String value = ;
			TreeHierarchy.HierarchyNode node = hierarchy.insertIntoSet(rs.get("value").toString());
			node.setParent(hierarchy.insertIntoSet(rs.get("parent").toString()));
		}
		vqe.close();*/
		/*Set<String> keySet = hierarchy.keySet();
		for(String key : keySet){
			if(hierarchy.getNode(key).parent == null) hierarchy.getNode(key).parent
		}*/
		TreeHierarchy.HierarchyNode topNode = hierarchy.insertIntoSet("http://www.imis.athena-innovation.gr/def#TopConcept");
		hierarchy.setRoot(topNode);
		for(String nodeString : hierarchy.keySet()){
			if(hierarchy.getNode(nodeString).parent == null){
				hierarchy.getNode(nodeString).setParent(hierarchy.root);
			}
		}
		
		System.out.println(hierarchy.size() + " values in hierarchy.");	
	}
	
	
	

}
