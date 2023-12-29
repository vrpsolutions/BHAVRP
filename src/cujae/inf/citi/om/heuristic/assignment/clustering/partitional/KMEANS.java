package cujae.inf.citi.om.heuristic.assignment.clustering.partitional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;

import cujae.inf.citi.om.factory.interfaces.DistanceType;
import cujae.inf.citi.om.heuristic.assignment.clustering.SeedType;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.Depot;
import cujae.inf.citi.om.problem.input.InfoProblem;

public class KMEANS extends Partitional{

	public static DistanceType distanceType = DistanceType.Euclidean;
	public static SeedType seedType = SeedType.Nearest_Depot;  
	private final int countMaxIterations = 100; // UN VALOR APROPIADO COMFIGURABLE ?
	private int currentIteration = 0;

	// IMPRIMIR ITERACIONES EN LOS EXPERIMENTOS
	
	public KMEANS() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getCurrentIteration() {
		return currentIteration;
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		

		ArrayList<Integer> listIDElements = generateElements(seedType, distanceType);

		ArrayList<Cluster> listClusters = initializeClusters(listIDElements);

		boolean change = false;
		boolean first = true;

		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>();
		ArrayList<Depot> listCentroids = new ArrayList<Depot>();

		do 
		{	
			listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());

			if(first)
			{
				updateCustomerToAssign(listCustomersToAssign, listIDElements);
				listCentroids = createCentroids(listIDElements);

				first = false;
			}
			else
				cleanClusters(listClusters);

			NumericMatrix costMatrix = new NumericMatrix();
			try 
			{ 
				//valorar si es conveniente usar calculateCostMatrix
				costMatrix = InfoProblem.getProblem().fillCostMatrix(listCustomersToAssign, listCentroids, distanceType);
			} 
			catch (IllegalArgumentException | SecurityException
					| ClassNotFoundException | InstantiationException
					| IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {
				// TODO Auto-generated catch block
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