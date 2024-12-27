package cujae.inf.ic.om.evaluations;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.factory.interfaces.IFactoryDistance;

import cujae.inf.ic.om.factory.methods.FactoryDistance;

import cujae.inf.ic.om.distance.IDistance;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Location;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

public class Evaluation {
	
	public static cujae.inf.ic.om.factory.DistanceType distanceType = DistanceType.Euclidean;
	
	public Evaluation() {
		super();
	}

	public double SSE(Cluster cluster) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double SSE = 0.0;
		
		ArrayList<Customer> listCustomersOfCluster = getCustomerOfCluster(cluster); 
		Location locationCentroid = calculateCentroid(listCustomersOfCluster);
		Depot depotCentroid = new Depot(cluster.getIDCluster(), locationCentroid, null);
		
		ArrayList<Depot> listDepotsCentroids = new ArrayList<Depot>();
		listDepotsCentroids.add(depotCentroid);
		
		NumericMatrix costMatrix = null;
		
		switch(distanceType.ordinal()) {
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = Problem.getProblem().fillCostMatrix(listCustomersOfCluster, listDepotsCentroids, distanceType);
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
	
	public double SSEGlobal(ArrayList<Cluster> clusters) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double SSEGlobal = 0.0;
		
		for(int i = 0; i < clusters.size(); i ++)
			SSEGlobal += SSE(clusters.get(i));
		
		SSEGlobal /= clusters.size();
		
		return SSEGlobal; 
	}
	
// ver si lista vacia
	private Location calculateCentroid(ArrayList<Customer> customers) {
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
			customerofCluster.add(Problem.getProblem().getCustomerByIDCustomer(cluster.getItemsOfCluster().get(i).intValue()));
	
		return customerofCluster;
	}
	
	public double SSW(Cluster cluster) {
		double SSW = 0.0;
		NumericMatrix costMatrix = Problem.getProblem().getCostMatrix();
		int posItemOne =  -1;
		int idItemOne =  -1;
		int posItemTwo =  -1;
		int idItemTwo =  -1;

		for(int i = 0; i < cluster.getItemsOfCluster().size(); i++)
		{
			idItemOne = cluster.getItemsOfCluster().get(i).intValue();
			posItemOne = Problem.getProblem().findPosCustomer(Problem.getProblem().getCustomers(), idItemOne);

			for(int j = 0; j < cluster.getItemsOfCluster().size(); j++)
			{
				if(i != j)
				{	
					idItemTwo = cluster.getItemsOfCluster().get(j).intValue();
					posItemTwo = Problem.getProblem().findPosCustomer(Problem.getProblem().getCustomers(), idItemTwo);

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
	
	public double SSB1(ArrayList<Cluster> clusters) {
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
					IDistance distance = (IDistance) iFactoryDistance.createDistance(distanceType);
					
					try {
						dist = distance.calculateDistance(centroidOne.getAxisX(), centroidOne.getAxisY(), centroidTwo.getAxisX(), centroidTwo.getAxisY());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					SSB += (totalItems/K) * (Math.pow(dist, 2));
				}
			}
		}
		
		SSB = (1.0/(2.0 * K)) * SSB ;
		
		return SSB;
	}
	
	public double SSB(ArrayList<Cluster> clusters) {
		double SSB = 0.0; 
		int totalItems = 0; 
	
		Location locationOverallMean = calculateCentroid(Problem.getProblem().getCustomers());
		double dist = 0.0; 
		
		for(int i = 0; i < clusters.size(); i++)
		{
			Location locationMean = calculateCentroid(getCustomerOfCluster(clusters.get(i)));
			
			IFactoryDistance iFactoryDistance = new FactoryDistance();
			IDistance distance = (IDistance) iFactoryDistance.createDistance(distanceType);
			
			try {
				dist = distance.calculateDistance(locationMean.getAxisX(), locationMean.getAxisY(), locationOverallMean.getAxisX(), locationOverallMean.getAxisY());
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	
	private double delta_Capital(ArrayList<Cluster> clusters) {
		double biggerDist = 0.0;
		double currentDist = 0.0;
		RowCol rcBiggerDist = new RowCol();
		ArrayList<Depot> listCentroidOfCluster = new ArrayList<Depot>();
		
		for(int i = 0; i < clusters.size(); i++)
		{
			NumericMatrix costMatrix = null;
			ArrayList<Customer> listCustomersOfCluster = getCustomerOfCluster(clusters.get(i));
				
			try {
				costMatrix = Problem.getProblem().fillCostMatrixXXX(listCustomersOfCluster, listCentroidOfCluster, distanceType);
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
	
	private double delta_Small (ArrayList<Cluster> clusters) {
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
					costMatrix = Problem.getProblem().createCostMatrix(listCustomersOfClusterOne, listCustomersOfClusterTwo, distanceType);
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
	
	/*
	public double CH (ArrayList<Cluster> clusters) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double CH = 0.0;
		ArrayList<Double> lists_i = new ArrayList<Double>();
		double a_i = 0.0;
		double b_i = 0.0;
		
		NumericMatrix costMatrix = null;
		
		for(int i = 0; i < clusters.size(); i++)
		{
			for(int j = 0; j < clusters.get(i).getItemsOfCluster().size(); i++)
			{
				//lists_i.add(s_i());
			}
		}
		
		for(int i = 0; i < clusters.size(); i++)
		{
			for(int j = 0; j < clusters.get(i).getItemsOfCluster().size(); j++)
			{
				a_i = a_i(clusters.get(i).getItemsOfCluster().get(j), clusters.get(i), costMatrix);
				b_i = b_i(j, i, clusters, costMatrix);
			}
		}
		
		//double num = SSB(clusters)/(clusters.size() - 1); 
		//double den = SSE(cluster)/clusters.size(); 
		
		//CH = num;
		return CH;
	}*/
	
	private double a_i(int itemRef, Cluster cluster, NumericMatrix costMatrix) {
		double a_i = 0.0;
		int posItemRef = Problem.getProblem().getPosElement(cluster.getItemsOfCluster().get(itemRef));
		int posSecondItem = -1;
		
		for(int i = 0; i < cluster.getItemsOfCluster().size(); i++)
		{
			if(itemRef != i)
			{
				posSecondItem = Problem.getProblem().getPosElement(cluster.getItemsOfCluster().get(i));
				a_i += costMatrix.getItem(posItemRef, posSecondItem);
			}
		}
		
		a_i /= cluster.getItemsOfCluster().size();
				
		return a_i; 
	}
	
	private double b_i(int itemRef, int posClusterRef, ArrayList<Cluster> clusters, NumericMatrix costMatrix) {
		double b_i = 0.0;
		double b_iMin = Double.POSITIVE_INFINITY;
		int posSecondItem = -1;
		
		int posItemRef = Problem.getProblem().getPosElement(clusters.get(posClusterRef).getItemsOfCluster().get(itemRef));
		
		for(int i = 0; i < clusters.size(); i++)
		{
			if(posClusterRef != i)
			{
				for(int j = 0; j < clusters.get(i).getItemsOfCluster().size(); j++)
				{
					posSecondItem = Problem.getProblem().getPosElement(clusters.get(i).getItemsOfCluster().get(j));
					b_i += costMatrix.getItem(posItemRef, posSecondItem);
				}
				
				b_i /= clusters.get(i).getItemsOfCluster().size();
				
				if(b_i < b_iMin)
					b_iMin = b_i;
			}	
		}
		
		return b_i; 
	}
	
	public double s_i(int itemRef, int posClusterRef, ArrayList<Cluster> clusters) {
		double a_i = 0.0;
		double b_i = 0.0;
		double s_i = 0.0;
		
		ArrayList<Customer> listCustomersOfCluster = new ArrayList<Customer>();
		
		for(int i = 0; i < clusters.size(); i++)
			listCustomersOfCluster.addAll(getCustomerOfCluster(clusters.get(i)));
	
		Depot depotDummy = new Depot(-1);
		ArrayList<Depot> listDepot = new ArrayList<Depot>();
		listDepot.add(depotDummy);
				
		NumericMatrix costMatrix = null; 
			
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = Problem.getProblem().fillCostMatrix(listCustomersOfCluster, listDepot, distanceType);
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
		
		a_i = a_i(itemRef, clusters.get(posClusterRef), costMatrix);
		b_i = b_i(itemRef, posClusterRef, clusters, costMatrix);
		
		s_i = (b_i - a_i) / Math.max(a_i, b_i);
		
		return s_i; 
	}
	
	public double CS(ArrayList<Cluster> clusters) {
		double CS = 0.0;
		
		for(int i = 0; i < clusters.size(); i++)	
			for(int j = 0; j < clusters.get(i).getItemsOfCluster().size(); j++)
				CS += s_i(clusters.get(i).getItemsOfCluster().get(j), i, clusters);
		
		CS /= getTotalItemsInClusters(clusters);
		
		return CS; 
	}
}