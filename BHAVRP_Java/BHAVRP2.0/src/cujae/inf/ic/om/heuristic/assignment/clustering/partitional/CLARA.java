package cujae.inf.ic.om.heuristic.assignment.clustering.partitional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;

import cujae.inf.ic.om.heuristic.assignment.clustering.SamplingType;
import cujae.inf.ic.om.heuristic.assignment.clustering.SeedType;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class CLARA extends Partitional {
	
	public static DistanceType distanceType = DistanceType.Euclidean;
	public static SeedType seedType = SeedType.Nearest_Depot; 
	public static int countMaxIterations = 2;
	public static int sampsize = 10;
	public static SamplingType samplingType = SamplingType.Random_Sampling;
	private int currentIteration = 0;

	public CLARA() {
		super();
	}
	
	public int getCurrentIteration(){
		return currentIteration;
	}
	
	@Override
	public Solution toClustering() {
		Solution solution = new Solution();
		
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>();
		ArrayList<Cluster> bestCluster = new ArrayList<Cluster>();
		ArrayList<Integer> listUnassignedCustomers = new ArrayList<Integer>();
		ArrayList<Integer> unassignedItemsInPartition = new ArrayList<Integer>();
		
		ArrayList<ArrayList<Customer>> listPartitions = generatePartitions(sampsize, samplingType);
		//tratamiento para cuando no es exacto la cantidad de particiones y me quedan menos que la cant de depositos
		
		for(int i = 0; i < listPartitions.size(); i++)
		{
			currentIteration = 0;

			ArrayList<Integer> listIDElements = generateElements(listPartitions.get(i), distanceType);
			ArrayList<Cluster> listClusters = initializeClusters(listIDElements);

			boolean change = true;
			boolean first = true;

			ArrayList<Depot> listMedoids = new ArrayList<Depot>();

			while((change) && (currentIteration < countMaxIterations))
			{
				listCustomersToAssign = new ArrayList<Customer>(listPartitions.get(i));
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
					costMatrix = Problem.getProblem().fillCostMatrix(listCustomersToAssign, listMedoids, distanceType);
					costMatrixCopy = new NumericMatrix(costMatrix);

				} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				stepAssignment(listClusters, listCustomersToAssign, costMatrix);
				ArrayList<Depot> oldMedoids = replicateDepots(listMedoids);

				double bestCost = calculateCost(listClusters, costMatrixCopy, listMedoids, listPartitions.get(i));

				stepSearchMedoids(listClusters, listMedoids, costMatrixCopy, bestCost, listPartitions.get(i));
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

			System.out.println("POR SI HAY NO ASIGNADOS");
			if(!listCustomersToAssign.isEmpty())					
				for(int j = 0; j < listCustomersToAssign.size(); j++)	
					unassignedItemsInPartition.add(listCustomersToAssign.get(j).getIDCustomer());
		
			// AQUI NO ESTAN LOS NO ASIGNADOS
			ArrayList<Integer> elementsInPartition = Problem.getProblem().getListID(listPartitions.get(i));
			listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
			updateCustomerToAssign(listCustomersToAssign, elementsInPartition);

			NumericMatrix costMatrix = new NumericMatrix(); 

			try { 
				//costMatrix1 = InfoProblem.getProblem().calculateCostMatrix(distanceType, listMedoids, listCustomersToAssign);
				costMatrix = Problem.getProblem().fillCostMatrix(listCustomersToAssign, listMedoids, distanceType);

			} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
					| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			stepAssignment(listClusters, listCustomersToAssign, costMatrix);

			double bestDissimilarity = 0.0;
			double currentDissimilarity = calculateDissimilarity(distanceType, listClusters);

			if(i == 0)
			{
				bestDissimilarity = currentDissimilarity;
				bestCluster = listClusters;
				
				if(!unassignedItemsInPartition.isEmpty())
					listUnassignedCustomers = unassignedItemsInPartition; // chequear que asigna bien
			}
			else 
			{
				if(currentDissimilarity < bestDissimilarity)
				{
					bestDissimilarity = currentDissimilarity;
					bestCluster = listClusters;
					
					if(!unassignedItemsInPartition.isEmpty())
						listUnassignedCustomers = unassignedItemsInPartition;
				}
			}
			
			unassignedItemsInPartition.clear();
		}

		if(!listUnassignedCustomers.isEmpty())					
			for(int j = 0; j < listUnassignedCustomers.size(); j++)	
				solution.getUnassignedItems().add(listUnassignedCustomers.get(j));

		if(!bestCluster.isEmpty())
			for(int k = 0; k < bestCluster.size(); k++)
				if(!(bestCluster.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(bestCluster.get(k));
		
		return solution;
	}
}	