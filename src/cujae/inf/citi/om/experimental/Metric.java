package cujae.inf.citi.om.experimental;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;
import cujae.inf.citi.om.factory.interfaces.DistanceType;
import cujae.inf.citi.om.factory.interfaces.IFactoryDistance;
import cujae.inf.citi.om.factory.methods.FactoryDistance;
import cujae.inf.citi.om.heuristic.distance.Distance;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.Depot;
import cujae.inf.citi.om.problem.input.InfoProblem;
import cujae.inf.citi.om.problem.input.Location;

public class Metric {

	public static DistanceType distanceType = DistanceType.Euclidean;
	
	public Metric() {
		super();
		// TODO Auto-generated constructor stub
	}

	public double SSE(Cluster cluster) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		double SSE = 0.0;
		
		ArrayList<Customer> listCustomersOfCluster = getCustomerOfCluster(cluster); 
		Location locationCentroid = calculateCentroid(listCustomersOfCluster);
		Depot depotCentroid = new Depot(cluster.getIDCluster(), locationCentroid, null);
		
		ArrayList<Depot> listDepotsCentroids = new ArrayList<Depot>();
		listDepotsCentroids.add(depotCentroid);
		
		NumericMatrix costMatrix = null;
		
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = InfoProblem.getProblem().fillCostMatrix(listCustomersOfCluster, listDepotsCentroids, distanceType);
				} catch (IllegalArgumentException | SecurityException
						| ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				break;
			}
			case 4:
			{// no funciona con la distancia real
				//costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
	
				break;
			}
		}
			 
		int countItems = cluster.getItemsOfCluster().size();
		int posCentroid = countItems;
		
		for(int i = 0; i < countItems; i++)
			SSE += Math.pow((costMatrix.getItem(posCentroid, i)), 2);
	
		return SSE; 
	}
// ver si lista vacia
	private Location calculateCentroid(ArrayList<Customer> customers){
		double aveAxisX = 0.0;
		double aveAxisY = 0.0;
		Location locationCentroid = new Location();
		
		int countCustomers = customers.size(); 
		
		for(int i = 0; i < countCustomers; i++)
		{
			aveAxisX += customers.get(i).getLocationCustomer().getAxisX();
			aveAxisY += customers.get(i).getLocationCustomer().getAxisY();			
		}
		
		aveAxisX = (aveAxisX / countCustomers);
		aveAxisY = (aveAxisY / countCustomers);

		locationCentroid.setAxisX(aveAxisX);
		locationCentroid.setAxisY(aveAxisY);
		
		return locationCentroid;
	}
	
	private ArrayList<Customer> getCustomerOfCluster(Cluster cluster){
		ArrayList<Customer> customerofCluster = new ArrayList<Customer>();
		
		for(int i = 0; i < cluster.getItemsOfCluster().size(); i++)
			customerofCluster.add(InfoProblem.getProblem().getCustomerByIDCustomer(cluster.getItemsOfCluster().get(i).intValue()));
	
		return customerofCluster;
	}
	
	public double SSW(Cluster cluster)
	{
		double SSW = 0.0;
		NumericMatrix costMatrix = InfoProblem.getProblem().getCostMatrix();
		int posItemOne =  -1;
		int idItemOne =  -1;
		int posItemTwo =  -1;
		int idItemTwo =  -1;

		for(int i = 0; i < cluster.getItemsOfCluster().size(); i++)
		{
			idItemOne = cluster.getItemsOfCluster().get(i).intValue();
			posItemOne = InfoProblem.getProblem().findPosCustomer(InfoProblem.getProblem().getCustomers(), idItemOne);

			for(int j = 0; j < cluster.getItemsOfCluster().size(); j++)
			{
				if(i != j)
				{	
					idItemTwo = cluster.getItemsOfCluster().get(j).intValue();
					posItemTwo = InfoProblem.getProblem().findPosCustomer(InfoProblem.getProblem().getCustomers(), idItemTwo);

					SSW += Math.pow((costMatrix.getItem(posItemOne, posItemTwo)), 2);
				}		 
			} 
		}

		SSW = (1.0/(2.0 * (cluster.getItemsOfCluster().size()))) * SSW;

		return SSW; 
	}

	private int getTotalItemsInClusters(ArrayList<Cluster> clusters){
		int totalElement = 0;
		
		for(int i = 0; i < clusters.size(); i++)
			totalElement += clusters.get(i).getItemsOfCluster().size();
		
		return totalElement;
	}
	
	public double SSB1(ArrayList<Cluster> clusters)
	{
		double SSB = 0.0;
		double dist = 0.0; 
		double K = clusters.size(); 
		
		int totalItems =  getTotalItemsInClusters(clusters); // InfoProblem.getProblem().getTotalCustomers() si asigna a todos;
				
		for(int i = 0; i < K; i++)
		{
			Location centroidOne = calculateCentroid(getCustomerOfCluster(clusters.get(i)));
			
			for(int j = 0; j < K; j++)
			{
				if(i != j)
				{
					Location centroidTwo = calculateCentroid(getCustomerOfCluster(clusters.get(j)));
					
					IFactoryDistance iFactoryDistance = new FactoryDistance();
					Distance distance = iFactoryDistance.createDistance(distanceType);
					
					dist = distance.calculateDistance(centroidOne.getAxisX(), centroidOne.getAxisY(), centroidTwo.getAxisX(), centroidTwo.getAxisY());
					
					SSB += (totalItems/K) * (Math.pow(dist, 2));
				}
			}
		}
		
		SSB = (1.0/(2.0 * K)) * SSB ;
		
		return SSB;
	}
	
	public double SSB(ArrayList<Cluster> clusters)
	{
		double SSB = 0.0; 
		int totalItems = 0; 
	
		Location locationOverallMean = calculateCentroid(InfoProblem.getProblem().getCustomers());
		double dist = 0.0; 
		
		for(int i = 0; i < clusters.size(); i++)
		{
			Location locationMean = calculateCentroid(getCustomerOfCluster(clusters.get(i)));
			
			IFactoryDistance iFactoryDistance = new FactoryDistance();
			Distance distance = iFactoryDistance.createDistance(distanceType);
			
			dist = distance.calculateDistance(locationMean.getAxisX(), locationMean.getAxisY(), locationOverallMean.getAxisX(), locationOverallMean.getAxisY());
			totalItems = clusters.get(i).getItemsOfCluster().size();
			
			SSB += Math.pow(dist, 2) * totalItems;
		}
		
		return SSB; 
	}
	
	public double dunnIndex(ArrayList<Cluster> clusters)
	{
		double DI = Double.NaN; 
		
		if(!clusters.isEmpty()) // cada agrupamiento no puede tener un solo cluster
			DI = delta_Small(clusters);
		
		return DI;
	}
	
	private double delta_Capital(ArrayList<Cluster> clusters)
	{
		double biggerDist = 0.0;
		double currentDist = 0.0;
		RowCol rcBiggerDist = new RowCol();
		ArrayList<Depot> listCentroidOfCluster = new ArrayList<Depot>();
		
		for(int i = 0; i < clusters.size(); i++)
		{
			NumericMatrix costMatrix = null;
			ArrayList<Customer> listCustomersOfCluster = getCustomerOfCluster(clusters.get(i));
				
			try {
				costMatrix = InfoProblem.getProblem().fillCostMatrixXXX(listCustomersOfCluster, listCentroidOfCluster, distanceType);
			} catch (IllegalArgumentException | SecurityException
					| ClassNotFoundException | InstantiationException
					| IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			rcBiggerDist = costMatrix.indexBiggerValue();
			currentDist = costMatrix.getItem(rcBiggerDist.getRow(), rcBiggerDist.getCol());
			
			if(i == 0)
				biggerDist = currentDist;
			else
				if(biggerDist < currentDist)
					biggerDist = currentDist;		
		}
		
		return biggerDist;
	}
	
	private double delta_Small (ArrayList<Cluster> clusters)
	{
		double valueDC = delta_Capital(clusters);
		
		double valueDS = 0.0; 
		RowCol rcSmallDist = null;	
		double bestDI = 0.0; 
		double currentDI = 0.0; 
		
		for(int i = 0; i < (clusters.size() - 1); i++)
		{
			NumericMatrix costMatrix = null;
			ArrayList<Customer> listCustomersOfClusterOne = getCustomerOfCluster(clusters.get(i));
			
			for(int j = (i + 1); j < clusters.size(); j++)
			{
				ArrayList<Customer> listCustomersOfClusterTwo = getCustomerOfCluster(clusters.get(j));
				
				try {
					costMatrix = InfoProblem.getProblem().createCostMatrix(listCustomersOfClusterOne, listCustomersOfClusterTwo, distanceType);
				} catch (IllegalArgumentException | SecurityException
						| ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				rcSmallDist = costMatrix.indexLowerValue(listCustomersOfClusterOne.size(), 0, (listCustomersOfClusterOne.size() + listCustomersOfClusterTwo.size() - 1), (listCustomersOfClusterOne.size() - 1));
				valueDS = costMatrix.getItem(rcSmallDist.getRow(), rcSmallDist.getCol());
				
				currentDI = valueDS/valueDC;
				
				if(i == 0)
					bestDI = currentDI;
				else
					if(bestDI  > currentDI)
						bestDI = currentDI;
			}	
		}
		
		return bestDI;
	}
	
	public double CH (ArrayList<Cluster> clusters) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		double CH = 0.0;
		
		double num = SSB(clusters)/(clusters.size() - 1); 
		//double den = SSE(cluster)/clusters.size(); 
		
		CH = num;
		return CH;
	}
	
	private double firstStepSC()
	{
		return 0.0; 
	}
}
