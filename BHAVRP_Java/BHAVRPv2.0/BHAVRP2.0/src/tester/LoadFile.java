package tester;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.StringTokenizer;

import cujae.inf.ic.om.controller.tools.Tools;

public class LoadFile {

	private ArrayList<String> instanceFile;

	public LoadFile() {
		super();
		instanceFile = new ArrayList<String>();
	}

	public ArrayList<String> getInstanceFile() {
		return instanceFile;
	}

	public void setInstanceFile(ArrayList<String> instanceFile) {
		this.instanceFile = instanceFile;
	}

	public boolean findEndElement(String lines){
		return lines.indexOf("EOF") != -1;

	}

	public boolean loadFile(String pathFile) throws IOException{
		boolean load = false;
		LineNumberReader line = new LineNumberReader(new FileReader(pathFile));
		String cad = new String();
		instanceFile = new ArrayList<String>();
		instanceFile.clear();

		while(!findEndElement(cad))
		{
			cad = line.readLine();
			if(cad != null){
				instanceFile.add(cad);
				load = true;
			}
			else{
				load = false;
				break;
			}
		}
		line.close();

		return load;
	}

	public void loadCountVehiclesForDepot(ArrayList<ArrayList<Integer>> countVehicles){
		StringTokenizer tool = new StringTokenizer(instanceFile.get(0), " ");	
		int totalVehicles = Integer.valueOf(tool.nextToken());
		int totalDepots = loadTotalDepots();
		
		ArrayList<Integer> countFleet = new ArrayList<Integer>();
		countFleet.add(totalVehicles);
		
		for(int i = 0; i < totalDepots; i++)
			countVehicles.add(countFleet);
	}
	
	public void loadCountVehiclesForDepotX(ArrayList<ArrayList<Integer>> countVehicles) {
	    int totalDepots = loadTotalDepots();
	    int startingIndex = instanceFile.size() - totalDepots;

	    for (int i = startingIndex; i < instanceFile.size(); i++) {
	        StringTokenizer tool = new StringTokenizer(instanceFile.get(i), " ");
	        int totalVehicles = Integer.valueOf(tool.nextToken());

	        ArrayList<Integer> countFleet = new ArrayList<Integer>();
	        countFleet.add(totalVehicles);
	        countVehicles.add(countFleet);
	    }
	}
	
	public int loadTotalCustomers(){
		StringTokenizer tool = new StringTokenizer(instanceFile.get(0), " ");
		tool.nextToken();
		return Integer.valueOf(tool.nextToken());
	}
	
	public int loadTotalDepots(){
		StringTokenizer tool = new StringTokenizer(instanceFile.get(0), " ");
		tool.nextToken();
		tool.nextToken();
		return Integer.valueOf(tool.nextToken());
	}
	
	public void loadCapacityVehicles(ArrayList<ArrayList<Double>> capacityVehicles){
		int totalDepots = loadTotalDepots();
		
		for(int i = 1; i < (totalDepots + 1); i++)		
		{
			StringTokenizer tool = new StringTokenizer(instanceFile.get(i), " ");
			ArrayList<Double> capacityFleet = new ArrayList<Double>();
			capacityFleet.add(Double.valueOf(tool.nextToken()));
			capacityVehicles.add(capacityFleet);
		}
	}

	public void loadCustomers(ArrayList<Integer> idCustomers, ArrayList<Double> axisXCustomers, ArrayList<Double> axisYCustomers, ArrayList<Double> requestCustomers){		
		int totalCustomers = loadTotalCustomers();
		int totalDepots = loadTotalDepots();
	
		for(int i = (totalDepots + 1); i < (totalCustomers + totalDepots + 1); i++)		
		{
			StringTokenizer tool = new StringTokenizer(instanceFile.get(i), " ");
			idCustomers.add(Integer.valueOf(tool.nextToken()));
			axisXCustomers.add(Double.valueOf(tool.nextToken()));
			axisYCustomers.add(Double.valueOf(tool.nextToken()));
			requestCustomers.add(Double.valueOf(tool.nextToken()));
			//requestCustomers.add(1.0);
		}
	}

	public void loadDepots(ArrayList<Integer> idDepots, ArrayList<Double> axisXDepots, ArrayList<Double> axisYDepots){		
		int totalCustomers = loadTotalCustomers();
		int totalDepots = loadTotalDepots();
		
		for(int i = (totalDepots + totalCustomers + 1); i < instanceFile.size(); i++)		
		{
			StringTokenizer tool = new StringTokenizer(instanceFile.get(i), " ");
			idDepots.add(Integer.valueOf(tool.nextToken()));
			axisXDepots.add(Double.valueOf(tool.nextToken()));
			axisYDepots.add(Double.valueOf(tool.nextToken()));
		}
	}	
	
    public Double calculateDistance(double axisXStart, double axisYStart, double axisXEnd, double axisYEnd) {
    	double distance = 0.0;
    	double axisX = 0.0;
    	double axisY = 0.0;

    	axisX = Math.pow((axisXStart - axisXEnd), 2);
    	axisY = Math.pow((axisYStart - axisYEnd), 2);
    	distance = Math.sqrt((axisX + axisY));

    	//Math.sqrt((Math.pow((axisXPointOne - axisXPointTwo), 2)) + Math.pow((axisYPointOne - axisYPointTwo), 2));
    	
    	return distance;
    }
    
    public void fillListDistances(ArrayList<Integer> idCustomers, ArrayList<Double> axisXCustomers, ArrayList<Double> axisYCustomers, ArrayList<Integer> idDepots, ArrayList<Double> axisXDepots, ArrayList<Double> axisYDepots, ArrayList<ArrayList<Double>> listDistances) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	int totalCustomers = idCustomers.size();
    	int totalDepots = idDepots.size();
    	
    	for(int i = 0; i < totalCustomers; i++)
    	{	    	
    		ArrayList<Double> distancesFromCustomers = new ArrayList<Double>();

    		for(int j = 0; j < totalCustomers; j++)
    			distancesFromCustomers.add(calculateDistance(Tools.truncateDouble(axisXCustomers.get(j), 6), Tools.truncateDouble(axisYCustomers.get(j), 6), Tools.truncateDouble(axisXCustomers.get(i), 6), Tools.truncateDouble(axisYCustomers.get(i), 6)));
    			//distancesFromCustomers.add(calculateDistance(axisXCustomers.get(j), axisYCustomers.get(j), axisXCustomers.get(i), axisYCustomers.get(i)));

    		for(int k = 0; k < totalDepots; k++)
    			distancesFromCustomers.add(calculateDistance(Tools.truncateDouble(axisXDepots.get(k), 6), Tools.truncateDouble(axisYDepots.get(k), 6), Tools.truncateDouble(axisXCustomers.get(i), 6), Tools.truncateDouble(axisYCustomers.get(i), 6)));
    			//distancesFromCustomers.add(calculateDistance(axisXDepots.get(k), axisYDepots.get(k), axisXCustomers.get(i), axisYCustomers.get(i)));

    		listDistances.add(distancesFromCustomers);//hasta aqui voy a tener la lista de distancias llena de cada cliente y deposito a los clientes
    	}

    	for(int i = 0; i < totalDepots; i++)
    	{
    		ArrayList<Double> distancesFromCustomers = new ArrayList<Double>();

    		for(int j = 0; j < totalCustomers; j++)
    			distancesFromCustomers.add(calculateDistance(axisXCustomers.get(j), axisYCustomers.get(j), axisXDepots.get(i), axisYDepots.get(i)));

    		for(int k = 0; k < totalDepots; k++)
    			distancesFromCustomers.add(calculateDistance(axisXDepots.get(k), axisYDepots.get(k), axisXDepots.get(i), axisYDepots.get(i)));

    		listDistances.add(distancesFromCustomers);//ya aqui la voy a tener llena completa
    	}
    }
}