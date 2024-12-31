package cujae.inf.ic.om.assignment.clustering.partitional;

import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.solution.Cluster;
import cujae.inf.ic.om.problem.solution.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class Kmeans extends ByCentroids {	
	private ArrayList<Integer> listIDElements;
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	private ArrayList<Depot> listCentroids;
	
	public Kmeans() {
		super();
	}

	public int getCurrentIteration() {
		return currentIteration;
	}

	@Override
	public Solution toClustering() {
		initialize();
		assign();
		return finish();
	}
	
	@Override		
	public void initialize() {
		listIDElements = generateElements(seedType, distanceType);
		listClusters = initializeClusters(listIDElements);
		listCustomersToAssign = new ArrayList<Customer>();
		listCentroids = new ArrayList<Depot>();
	}	
		
	@Override	
	public void assign() {	
		
		boolean change = false;
		boolean first = true;
		
		do 
		{	
			listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());

			if(first)
			{
				updateCustomerToAssign(listCustomersToAssign, listIDElements);
				listCentroids = createCentroids(listIDElements);

				first = false;
			}
			else
				cleanClusters(listClusters);

			NumericMatrix costMatrix = initializeCostMatrix(listCustomersToAssign, listCentroids, distanceType);
			
			stepAssignment(listClusters, listCustomersToAssign, costMatrix);
			
			if (distanceType == DistanceType.Real)
			{
				change = verifyCentroids(listClusters, listCentroids);
			}
			else
			{
				change = verifyCentroids(listClusters, listCentroids, distanceType);
			}
			
			currentIteration ++;

			System.out.println("ITERACIÓN ACTUAL: " + currentIteration);

		} while((change) && (currentIteration < countMaxIterations));
	}	
		
	@Override
	public Solution finish() {
		Solution solution = new Solution();
		
		if(!listCustomersToAssign.isEmpty())					
			for(int j = 0; j < listCustomersToAssign.size(); j++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(j).getIDCustomer());

		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!listClusters.get(k).getItemsOfCluster().isEmpty())
					solution.getClusters().add(listClusters.get(k));
	
		OSRMService.clearDistanceCache();
	
		return solution;
	}
}