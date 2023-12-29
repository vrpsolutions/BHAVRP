package cujae.inf.citi.om.heuristic.assignment.clustering.partitional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;

import cujae.inf.citi.om.factory.interfaces.DistanceType;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.Depot;
import cujae.inf.citi.om.problem.input.InfoProblem;

public class Modified_KMEANS extends Partitional{

	public static DistanceType distanceType = DistanceType.Euclidean;
	private final int countMaxIterations = 3; // UN VALOR APROPIADO CONFIGURABLA?
	private int currentIteration = 0;
	
	public Modified_KMEANS() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public int getCurrentIteration() {
		return currentIteration;
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();	
		ArrayList<Depot> centroids = replicateDepots(InfoProblem.getProblem().getDepots());
		
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>();
		boolean change = false;
		
		do {	
			listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
			
			NumericMatrix costMatrix = new NumericMatrix();
			try 
			{
				costMatrix = InfoProblem.getProblem().fillCostMatrix(listCustomersToAssign, centroids, distanceType);
			} 
			catch (IllegalArgumentException | SecurityException
					| ClassNotFoundException | InstantiationException
					| IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			stepAssignment(listClusters, listCustomersToAssign, costMatrix);
			change = verifyCentroids(listClusters, centroids, distanceType);
			
			if(change)
			{
				cleanClusters(listClusters);
				
				try				
				{
					costMatrix = InfoProblem.getProblem().fillCostMatrix(InfoProblem.getProblem().getCustomers(), centroids, distanceType);
				} 
				catch (IllegalArgumentException | SecurityException
						| ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		currentIteration ++;
		
		System.out.println("ITERACIÓN ACTUAL: " + currentIteration);
		
		} while((change) && (currentIteration < countMaxIterations)); 

		if(!listCustomersToAssign.isEmpty())					
			for(int j = 0; j < listCustomersToAssign.size(); j++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(j).getIDCustomer());

		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!listClusters.get(k).getItemsOfCluster().isEmpty())
					solution.getClusters().add(listClusters.get(k));

		return solution;
	}
}