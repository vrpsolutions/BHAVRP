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

public class PAM extends Partitional{
	
	public static DistanceType distanceType = DistanceType.Euclidean;
	public static SeedType seedType = SeedType.Nearest_Depot;
	private final int countMaxIterations = 100; // UN VALOR APROPIADO CONFIGURABLE?
	private int currentIteration = 0;
	
	public PAM() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();
		
		ArrayList<Integer> listIDElements = generateElements(seedType, distanceType);
		ArrayList<Cluster> listClusters = initializeClusters(listIDElements);
		
		boolean change = true;
		boolean first = true;
		
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>();
		ArrayList<Depot> listMedoids = new ArrayList<Depot>();
		
		while((change) && (currentIteration < countMaxIterations))
		{
			listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
			updateCustomerToAssign(listCustomersToAssign, listIDElements);
			
			if(first)
			{
				listMedoids = createCentroids(listIDElements);	
				first = false;
			}
			else
				updateClusters(listClusters, listIDElements);
			
			NumericMatrix costMatrix = new NumericMatrix();
			NumericMatrix costMatrixCopy = null; 
			
			try { 
				//costMatrix1 = InfoProblem.getProblem().calculateCostMatrix(distanceType, listMedoids, listCustomersToAssign);
				costMatrix = InfoProblem.getProblem().fillCostMatrix(listCustomersToAssign, listMedoids, distanceType);
				costMatrixCopy = new NumericMatrix(costMatrix);
	
			} catch (IllegalArgumentException | SecurityException
					| ClassNotFoundException | InstantiationException
					| IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			stepAssignment(listClusters, listCustomersToAssign, costMatrix);
			ArrayList<Depot> oldMedoids = replicateDepots(listMedoids);
			
			double bestCost = calculateCost(listClusters, costMatrixCopy, listMedoids);
			
			stepSearchMedoids(listClusters, listMedoids, costMatrixCopy, bestCost);
			change = verifyMedoids(oldMedoids, listMedoids); 
			 
			if((change) && (currentIteration + 1 != countMaxIterations))
			{
				listIDElements.clear();
				listIDElements = getIDMedoids(listMedoids);
				cleanClusters(listClusters);
			}
			
			currentIteration++;
			
			System.out.println("ITERACIÓN: " + currentIteration);
		}
		
		if(!listCustomersToAssign.isEmpty())					
			for(int j = 0; j < listCustomersToAssign.size(); j++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(j).getIDCustomer());

		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));
		
		return solution;
	}

}
