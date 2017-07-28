package app;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;

import models.DataCube;
import models.Definitions;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class DataCubeIzer {
	
	
public void cubizeCSVInternetHouseholds(){
		
		try{
			String datasetID = "internet_households_nuts2_less";
			CSVReader reader = new CSVReader(
					new FileReader("C:/Users/Marios/Documents/Projects/Multidimensional Similarity/internet nuts2/"+datasetID+".csv"), ',', '\"');
			String[] nextLine;
			
			int counter = -1, i=0;
			HashMap<Resource, String[]> map = new HashMap<Resource, String[]>();
			Model model = ModelFactory.createDefaultModel();
			
			Resource dataset = model.createResource(Definitions.namespace+"dataset/"+datasetID);
		 	while((nextLine = reader.readNext())!= null){
		 			counter++;
		 			if(counter==0) continue;
		 			//System.out.println(Arrays.toString(nextLine));
		 			String date = nextLine[0];
		 			date = date.replaceAll("M", "-");
		 			String nutsCode = nextLine[1];		 					 			
		 			String households = nextLine[2];
		 			Resource observation = model.createResource(Definitions.namespace+datasetID+"/observation/"+counter, DataCube.qbObservation);
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"refPeriod"), model.createResource("http://reference.data.gov.uk/id/gregorian-year/"+date));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"refArea"), model.createResource("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/"+nutsCode));		 			
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.imisMeasureNamespace+"internetHouseholds"), households);
		 			observation.addProperty(DataCube.qbdataSet, dataset);
		 	}
		 	try{
	 			String path = "C:/Users/Marios/Desktop/"+datasetID+".rdf";
				FileOutputStream fos = new FileOutputStream(path);
				model.write(fos, "RDF/XML-ABBREV", path);
				fos.close();
			}catch(Exception e){}
			model.close();
		}catch(Exception e){e.printStackTrace();}
		
	}
	
public void cubizeCSVAsylumCountry(){
		
		try{
			CSVReader reader = new CSVReader(
					new FileReader("C:/Users/Marios/Documents/Projects/Multidimensional Similarity/population/eurostat/asylum applicants by country/migr_asyappctzm_1_Data.csv"), ',', '\"');
			String[] nextLine;
			
			int counter = -1, i=0;
			HashMap<Resource, String[]> map = new HashMap<Resource, String[]>();
			Model model = ModelFactory.createDefaultModel();
			String datasetID = "migr_asyappctzm_1_Data";
			Resource dataset = model.createResource(Definitions.namespace+"dataset/"+datasetID);
		 	while((nextLine = reader.readNext())!= null){
		 			counter++;
		 			if(counter==0) continue;
		 			//System.out.println(Arrays.toString(nextLine));
		 			String date = nextLine[0];
		 			date = date.replaceAll("M", "-");
		 			String nutsCode = nextLine[1];
		 			String sex = nextLine[3];
		 			String age = nextLine[4];
		 			String applicants = nextLine[6];
		 			Resource observation = model.createResource(Definitions.namespace+datasetID+"/observation/"+counter, DataCube.qbObservation);
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"refPeriod"), model.createResource("http://reference.data.gov.uk/id/gregorian-month/"+date));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"refArea"), model.createResource("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/"+nutsCode));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"sex"), model.createResource("http://purl.org/linked-data/sdmx/2009/code#sex-"+sex));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"age"), age);
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.imisMeasureNamespace+"asylum-applicants"), applicants);
		 			observation.addProperty(DataCube.qbdataSet, dataset);
		 	}
		 	try{
	 			String path = "C:/Users/Marios/Desktop/"+datasetID+".rdf";
				FileOutputStream fos = new FileOutputStream(path);
				model.write(fos, "RDF/XML-ABBREV", path);
				fos.close();
			}catch(Exception e){}
			model.close();
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	public void cubizeCSVPopNuts3(){
		
		try{
			CSVReader reader = new CSVReader(
					new FileReader("C:/Users/Marios/Documents/Projects/Multidimensional Similarity/population/eurostat/population by sex nuts 3 codes/demo_r_pjanaggr3_1_Data.csv"), ',', '\"');
			String[] nextLine;
			
			int counter = -1, i=0;
			HashMap<Resource, String[]> map = new HashMap<Resource, String[]>();
			Model model = ModelFactory.createDefaultModel();
			String datasetID = "demo_r_pjanaggr3_1_Data";
			Resource dataset = model.createResource(Definitions.namespace+"dataset/"+datasetID);
		 	while((nextLine = reader.readNext())!= null){
		 			counter++;
		 			if(counter==0) continue;
		 			//System.out.println(Arrays.toString(nextLine));
		 			String year = nextLine[0];
		 			String nutsCode = nextLine[1];
		 			String sex = nextLine[2];
		 			String age = nextLine[3];
		 			String population = nextLine[4];
		 			Resource observation = model.createResource(Definitions.namespace+datasetID+"/observation/"+counter, DataCube.qbObservation);
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"refPeriod"), model.createResource("http://reference.data.gov.uk/id/gregorian-year/"+year));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"refArea"), model.createResource("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/"+nutsCode));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"sex"), model.createResource("http://purl.org/linked-data/sdmx/2009/code#sex-"+sex));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"age"), age);
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.imisMeasureNamespace+"population"), population);
		 			observation.addProperty(DataCube.qbdataSet, dataset);
		 	}
		 	try{
	 			String path = "C:/Users/Marios/Desktop/"+datasetID+".rdf";
				FileOutputStream fos = new FileOutputStream(path);
				model.write(fos, "RDF/XML-ABBREV", path);
				fos.close();
			}catch(Exception e){}
			model.close();
		}catch(Exception e){e.printStackTrace();}
		
	}
	
public void cubizeCSVPopNuts2(){
		
		try{
			CSVReader reader = new CSVReader(
					new FileReader("C:/Users/Marios/Documents/Projects/Multidimensional Similarity/population/eurostat/population by sex nuts 2 codes/greece_belgium_ch_poplulation_2009-2013_nuts2.csv"), ',', '\"');
			String[] nextLine;
			
			int counter = -1, i=0;
			HashMap<Resource, String[]> map = new HashMap<Resource, String[]>();
			Model model = ModelFactory.createDefaultModel();
			String datasetID = "greece_belgium_ch_poplulation_2009-2013_nuts2";
			Resource dataset = model.createResource(Definitions.namespace+"dataset/"+datasetID);
		 	while((nextLine = reader.readNext())!= null){
		 			counter++;
		 			if(counter==0) continue;
		 			//System.out.println(Arrays.toString(nextLine));
		 			String year = nextLine[0];
		 			String nutsCode = nextLine[1];
		 			String sex = nextLine[2];
		 			//String age = nextLine[3];
		 			String population = nextLine[3];
		 			Resource observation = model.createResource(Definitions.namespace+datasetID+"/observation/"+counter, DataCube.qbObservation);
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"refPeriod"), model.createResource("http://reference.data.gov.uk/id/gregorian-year/"+year));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"refArea"), model.createResource("http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/" + nutsCode));
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"sex"), model.createResource("http://purl.org/linked-data/sdmx/2009/code#sex-"+sex));
		 			//observation.addProperty(ResourceFactory.createProperty(Definitions.sdmxDimensionNamespace+"age"), age);
		 			observation.addProperty(ResourceFactory.createProperty(Definitions.imisMeasureNamespace+"population"), population);
		 			observation.addProperty(DataCube.qbdataSet, dataset);
		 	}
		 	try{
	 			String path = "C:/Users/Marios/Desktop/"+datasetID+".rdf";
				FileOutputStream fos = new FileOutputStream(path);
				model.write(fos, "RDF/XML-ABBREV", path);
				fos.close();
			}catch(Exception e){}
			model.close();
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	
}
