package cujae.inf.ic.om.heuristic.assignment.clustering.partitional;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class Farthest_First extends Partitional {

	
	public static DistanceType distanceType = DistanceType.Euclidean;
	private final int countMaxIterations = 10;
	private int currentIteration = 0;
	
	public Farthest_First() {
		super();
	}
	
	public int getCurrentIteration(){
		return currentIteration;
	}
	
	@Override
	public Solution toClustering(){
		Solution solution = new Solution();
		
		ArrayList<Integer> listIDElements = generateElements(distanceType);
		//ArrayList<Integer> listIDElements1 = generateElementsXXX(distanceType);
		//ArrayList<Integer> listIDElements2 = generateElementsXX(distanceType);
		
		ArrayList<Cluster> listClusters = initializeClusters(listIDElements);

		boolean change = false;
		boolean first = true;

		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>();
		ArrayList<Depot> listCentroids = new ArrayList<Depot>();

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
			
			NumericMatrix costMatrix = new NumericMatrix();
			
			try {
				costMatrix = Problem.getProblem().fillCostMatrix(listCustomersToAssign, listCentroids, distanceType);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
				
			stepAssignment(listClusters, listCustomersToAssign, costMatrix);
			change = verifyCentroids(listClusters, listCentroids, distanceType);

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