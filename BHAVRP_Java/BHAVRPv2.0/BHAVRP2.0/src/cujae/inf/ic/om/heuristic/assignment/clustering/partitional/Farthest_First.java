package cujae.inf.ic.om.heuristic.assignment.clustering.partitional;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class Farthest_First extends ByCentroids {
	
	public static DistanceType distanceType = DistanceType.Real;
	private final int countMaxIterations = 1;
	private int currentIteration = 0;
	
	private ArrayList<Integer> listIDElements;
	//private ArrayList<Integer> listIDElements1;
	//private ArrayList<Integer> listIDElements2;
	
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	private ArrayList<Depot> listCentroids;

	private boolean first;
	
	public Farthest_First() {
		super();
	}
	
	public int getCurrentIteration(){
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
		listIDElements = generateElements(distanceType);
		//listIDElements1 = generateElementsXXX(distanceType);
		//listIDElements2 = generateElementsXX(distanceType);
		
		listClusters = initializeClusters(listIDElements);
		listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		listCentroids = new ArrayList<Depot>();
		
		currentIteration = 0;
		first = true;
	}
	
	@Override
	public void assign() {
		boolean change = false;
		do 
		{
			if(first)
			{
				updateCustomerToAssign(listCustomersToAssign, listIDElements);
				listCentroids = createCentroids(listIDElements);

				first = false;
			}
			else
				cleanClusters(listClusters);
			
			NumericMatrix costMatrix = new NumericMatrix();
			
			switch (distanceType.ordinal())
			{
				case 0: case 1: case 2: case 3:
				{
					try {
						costMatrix = Problem.getProblem().fillCostMatrix(listCustomersToAssign, listCentroids, distanceType);
					} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
							| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			if (distanceType == DistanceType.Real)
			{
				try {
					costMatrix = Problem.getProblem().fillCostMatrixReal(listCustomersToAssign, listCentroids);
				} catch (IOException | InterruptedException | IllegalArgumentException | SecurityException
						| ClassNotFoundException | InstantiationException | IllegalAccessException
						| InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}

			stepAssignment(listClusters, listCustomersToAssign, costMatrix);
			
			switch (distanceType.ordinal())
			{
				case 0: case 1: case 2: case 3:
				{
					change = verifyCentroids(listClusters, listCentroids, distanceType);
					break;
				}
			}
			if (distanceType == DistanceType.Real)
			{
				change = verifyCentroids(listClusters, listCentroids);
			}
			
			currentIteration ++;

			System.out.println("ITERACIÓN ACTUAL: " + currentIteration);
			
			OSRMService.clearDistanceCache();

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

		return solution;
	}
}