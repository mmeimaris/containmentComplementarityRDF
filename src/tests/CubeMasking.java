package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import models.CubeMask;
import models.Dimension;
import models.DimensionFactory;
import models.Lattice;
import models.Observation;
import models.TreeHierarchy;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import app.DataConnection;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class CubeMasking {

	static HashSet<String> graphs = new HashSet<String>();
	static String connectionString = "jdbc:virtuoso://83.212.121.252:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
	static String prefix = "http://imis.athena-innovation.gr/def#";
	static String codePrefix ="http://imis.athena-innovation.gr/code#";
	static ArrayList<String> featureList;
	static HashMap<String, Integer> levelMap;
	static public HashMap<String, Integer> valueIndexMap;
	//static HashMap<String, Integer> levelMap = new HashMap<String, Integer>();
	static public TreeHierarchy hierarchy;
	static HashMap<String, Observation> obs;
	static Set<Dimension> dims; 
	static HashMap<Observation, ArrayList<String>> cubeBuckets;
	static HashMap<CubeMask, ArrayList<String>> cubeMaskBuckets;
	static long computed, computedPartial, nulls, start;
	static int nextIndex = 0;
	static Lattice lattice;
	//static int numberOfClusters = 4;
	
	
public static long runTest(String[] args){
		
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
					 "}";
			System.out.println(query);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();
			while(results.hasNext()&& count < 200000 ){//&& count < 100000
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
//				if(dimension.getRepresentative().equals("education_level")){
//					System.out.println(value);
//					System.out.println(levelMap.get(value));
//				}
				current.setDimensionValue(dimension, value);
				
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
		Set<String> obsKeys = obs.keySet();
		
		ArrayList<Dimension> allDims = DimensionFactory.getInstance().getDimensions();
		
		dims = null;
		
		for(String obsKey : obsKeys){
			//next = "";
			Observation o = obs.get(obsKey);
			if(o.toString().equals("")) continue;
			//System.out.println(o.toString());
			dims = o.getDimensions();	
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
			//System.out.println("after " + dims.toString());
			//if(!uniqueCombinations.contains(next)){
				//uniqueCombinations.add(next);
				//unique++; 
			//}
		
		}
		dims = new HashSet<Dimension>();
		for(Dimension curDim : allDims){
			dims.add(curDim);
		}
		System.out.println(allDims.toString());
		//System.out.println("Unique combinations of values: " + uniqueCombinations.size());
//		for(String curnext : uniqueCombinations){
//			System.out.println(curnext);
//		}
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
		HashSet<CubeMask> new_cubes = new HashSet<CubeMask>();
		for(String obsKey : obsKeys){
			CubeMask new_cube = new CubeMask();
			//new_cube.se
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
			//System.out.println("father: " + mask.toString());
			//System.out.println("children: " + adjacencyLists.get(mask).toString());
			
		}
		System.out.println("childcount: " + childcount);
		System.out.println("Total cube Buckets: " + cubeBuckets.size());
		System.out.println("Total cubeMask Buckets: " + cubeMaskBuckets.size());
		HashSet<String> uniqueCombinations = new HashSet<String>();
		String next ;
		for(String observation : obs.keySet()){
			Observation o = obs.get(observation);
			next = "";
			for(Dimension d : o.getDimensions()){
				next += "_" + o.getDimensionValue(d);
			}
			uniqueCombinations.add(next);
		}
		System.out.println("unique combos: " + uniqueCombinations.size());
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
		HashSet<String> uniqueValues = new HashSet<String>();
		for(CubeMask ob : cubeMaskBuckets.keySet()){
			//System.out.println(cubeBuckets.get(ob).toString());
			obsCount += cubeMaskBuckets.get(ob).size();
			for(String os : cubeMaskBuckets.get(ob)){
				Observation o = obs.get(os);
				for(Dimension d : o.getDimensions()){
					uniqueValues.add(o.getDimensionValue(d));
				}
				System.out.println(o.getDewey());
			}
		}
		System.out.println("Total unique values in observations: " + uniqueValues.size());
		System.out.println("Total observations in mask buckets: " + obsCount);	
//		for(String v : uniqueValues){
//			System.out.println(v);
//		}
		
		int totes = 0;		
		
		int total = 0, c = 0;
		computed = 0;
		HashMap<Observation, HashSet<Observation>> latticeMap = new HashMap<Observation, HashSet<Observation>>();
		//HashMap<Integer, Integer> addedValues = new HashMap<Integer, Integer>();
		HashSet<Integer> allTopValues = new HashSet<Integer>();
		
		for(Observation cube1 : cubes){
				if(true) break;
			
				HashSet<Observation> children = null;	
				
				if(!latticeMap.containsKey(cube1)){
					children = new HashSet<Observation>();
					latticeMap.put(cube1, children);
				}
				else{
					children = latticeMap.get(cube1);
				}
				for(Observation cube2 : cubes){
					//if(obs1.equals(obs2)) continue;
					//if(!worthComputing(cube1, cube2)) continue;
					int cont = 0, alltop = 0;
					for(Dimension d : dims){
						//int sum = cube1.getDimensionLevel(d)+cube2.getDimensionLevel(d);
						if(cube1.getDimensionLevel(d)>=cube2.getDimensionLevel(d) ) {
							/*if(addedValues.containsKey(cube1.getDimensionLevel(d)+cube2.getDimensionLevel(d)))
								addedValues.put(cube1.getDimensionLevel(d)+cube2.getDimensionLevel(d), addedValues.get(cube1.getDimensionLevel(d)+cube2.getDimensionLevel(d)+1));*/
							if(cube1.getDimensionLevel(d)==-1 && cube2.getDimensionLevel(d) == -1){
								alltop++;							
							}
							cont++;
						}						
					}
					//full containment + complementarity
					if(cont==dims.size() && alltop != dims.size()) {
						
						children.add(cube2);
						total++;
						allTopValues.add(alltop);
						totes += cubeBuckets.get(cube1).size() * cubeBuckets.get(cube2).size();
						computeContainment(graph, cube1, cube2);
					}
			}
			
		}
		
		elapsedTime = System.nanoTime() - start;
		System.out.println("FULL CONTAINMENT method 1: Elapsed time: " + elapsedTime);
		long method1_full = elapsedTime;
		System.out.println("Computed full: " + computed);	
		computed = 0;
		start = System.nanoTime();
		System.out.println("Iterating through cube children...");
		int total2 = 0;
		//HashMap<Observation, HashSet<Observation>> comparisonMap = new HashMap<Observation, HashSet<Observation>>();
		for(Observation cube1 : latticeMap.keySet()){
			if(true) break;		
			//total2 += latticeMap.get(cube1).size();
			
			for(Observation cube2 : latticeMap.get(cube1)){
				
				total2 ++ ;
				computeContainment(graph, cube1, cube2);
			}
			
		}
		
//		for(CubeMask cube1 : cubeMaskBuckets.keySet()){
//			
//			//total2 += latticeMap.get(cube1).size();
//			if(!adjacencyLists.containsKey(cube1)) continue;
//			for(CubeMask cube2 : adjacencyLists.get(cube1)){
//				
//				total2 ++ ;
//				computeContainmentMask(graph, cube1, cube2);
//			}
//			
//		}
		
		HashSet<CubeMask> visitedNodes = new HashSet<CubeMask>();
			
		for(CubeMask outerNode : adjacencyLists.keySet()){
			
			//Start a pre-order traversal from outerNode
			Stack<CubeMask> traversalStack = new Stack<CubeMask>();
			
			traversalStack.push(outerNode);
			
			System.out.println("starting from: " + outerNode.toString());
			
			while(!traversalStack.empty()){
				
				CubeMask nextMask = traversalStack.pop();
												
				visitedNodes.add(nextMask);
				
				System.out.println("next mask: " + nextMask.toString());
				
				if(!adjacencyLists.containsKey(nextMask)){
					
					System.out.println("chain ended");
					continue;
					
				}
				
				for(CubeMask child : adjacencyLists.get(nextMask)){
					
					if(!visitedNodes.contains(child))
						traversalStack.push(child);
					else
						System.out.println("node already visited");
					
				}
				
			}
			
		}
		
		HashMap<CubeMask, HashSet<Vector<CubeMask>>> ecsVectorMap = new HashMap<CubeMask, HashSet<Vector<CubeMask>>>();
 		
 		HashSet<Vector<CubeMask>> ecsVectors = new HashSet<Vector<CubeMask>>(); 
 		
 		for(CubeMask ecs : adjacencyLists.keySet()){
 			 			
 			HashSet<CubeMask> visited = new HashSet<CubeMask>();
 			
 			Stack<Vector<CubeMask>> stack = new Stack<>();
 			
 			Vector<CubeMask> v = new Vector<CubeMask>();
 			
 			v.add(ecs);
 			
 			stack.push(v);
 			
 			while(!stack.empty()){
 				
 				v = stack.pop();
 				
 				CubeMask current = v.lastElement();
 				
 				visited.add(current);
 				
 				if(!adjacencyLists.containsKey(current)){
 				
 					if(ecsVectorMap.containsKey(current))
 						ecsVectorMap.get(current).add(v);
 					else{
 						HashSet<Vector<CubeMask>> d = new HashSet<Vector<CubeMask>>();
 						d.add(v);
 						ecsVectorMap.put(current, d); 						
 					}
 					ecsVectors.add(v);
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
 		
 		System.out.println("total patterns: " + ecsVectors.size());
		
		int totalCompar = 0;
		for(CubeMask cube1 : cubeMaskBuckets.keySet()){
			
			//total2 += latticeMap.get(cube1).size();
			//if(!adjacencyLists.containsKey(cube1)) continue;
			
			for(CubeMask cube2 : cubeMaskBuckets.keySet()){
				if(cube1 == cube2) continue;
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
				computeContainmentMask(graph, cube1, cube2);
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
		long method2_full = elapsedTime;
		System.out.println("rate of execution time: " + (double) ((double) method2_full / (double)method1_full));
		method1_full = cubes.size()*cubes.size();
		method2_full = total;
		System.out.println("rate of comparisons: " + (double) ((double) method2_full / (double)method1_full));
		
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
	
	public static boolean computeContainmentMask(VirtGraph graph, CubeMask cube1, CubeMask cube2){
		
		int count;		
		ArrayList<String> o1 = cubeMaskBuckets.get(cube1);
		ArrayList<String> o2 = cubeMaskBuckets.get(cube2);		
		TreeHierarchy.HierarchyNode node1, node2;
		String prev1 = "", prev2 = "", prevURI1 = "";
		for(String o1URI : o1){
			Observation o1Obs = obs.get(o1URI);
			//System.out.println(o1Obs.getDewey());
//			if(o1Obs.toString().equals(prev1)) {
//				System.out.println("same");
//				System.out.println("prev URI: " + prevURI1);
//				System.out.println("URI: " + o1URI);
//			}
//			prev1 = o1Obs.toString();
//			prevURI1 = o1URI;
			for(String o2URI : o2){
				//System.out.println("next pair");
				totalcc++;
				Observation o2Obs = obs.get(o2URI);
				count = 0;
				for(Dimension d : dims){
					/*if(o2Obs.getDimensionValue(d)==null || o2Obs.getDimensionValue(d).equals("top")){
						//count++;
						//continue;
						nulls++;
						break;
					}*/
					
					if(o1Obs.getDimensionLevel(d) < o2Obs.getDimensionLevel(d)) 
						break;
					node1 = hierarchy.getNode(o1Obs.getDimensionValue(d));
					node2 = hierarchy.getNode(o2Obs.getDimensionValue(d));

//					if(d.getRepresentative().equals("education_level") && !node1.toString().equals("http://www.imis.athena-innovation.gr/def#TopConcept")){
//						System.out.println("node1: " + node1.toString());
//						System.out.println("node2: " + node2.toString());
//						System.out.println("------------");
//					}
					//if(o1Obs.getDimensionValue(d).equals("AT") || o2Obs.getDimensionValue(d).equals("AT"))
//					{
//						System.out.println("node1: " + node1.toString());
//						System.out.println("node2: " + node2.toString());
//						System.out.println("------------");
//					}
//					if(!node1.toString().contains("TopConcept") && !node1.toString().contains("TOTAL")){
//						System.out.println("node1: " + node1.toString());
//						System.out.println("node2: " + node2.toString());
//						System.out.println("------------");
//					}
//					System.out.println("node1: " + node1.toString());
//					System.out.println("node2: " + node2.toString());
//					System.out.println("------------");
					if(node2.isParentOf(node1) ){ //|| node2.value.equals(node1.value)
//						if(!node2.value.equals(node1.value)){
//							System.out.println("node1: " + node1.toString());
//							System.out.println("node2: " + node2.toString());
//							System.out.println("------------");
//						}
						
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
			levelMap.put(value, rs.getLiteral("level").getInt());
			valueIndexMap.put(value, getNextInt());
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
