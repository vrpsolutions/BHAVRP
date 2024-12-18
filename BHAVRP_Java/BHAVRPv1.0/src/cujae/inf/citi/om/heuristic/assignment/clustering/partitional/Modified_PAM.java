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

public class Modified_PAM extends Partitional{

	public static DistanceType distanceType = DistanceType.Euclidean;
	private final int countMaxIterations = 100; // UN VALOR APROPIADO CONFIGURABLE?
	private int currentIteration = 0;
	
	
	public Modified_PAM() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();	
		ArrayList<Depot> listMedoids = replicateDepots(InfoProblem.getProblem().getDepots());	
		
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>();
		boolean change = true;

		while((change) && (currentIteration < countMaxIterations))
		{
			listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
			
			System.out.println("CLIENTES A ASIGNAR");
			
			for(int i = 0; i < listCustomersToAssign.size(); i++)
			{
				System.out.println("--------------------------------------------------");
				System.out.println("ID CLIENTE: " + listCustomersToAssign.get(i).getIDCustomer());
				System.out.println("X: " + listCustomersToAssign.get(i).getLocationCustomer().getAxisX());
				System.out.println("Y: " + listCustomersToAssign.get(i).getLocationCustomer().getAxisY());
				System.out.println("DEMANDA: " + listCustomersToAssign.get(i).getRequestCustomer());
			}

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
				
			if((change) && ((currentIteration + 1) != countMaxIterations))
				cleanClusters(listClusters);
			
			//valorar si se puede asignar los medoides a los depot y quitarlos de la lista ver si aplica aqui
			
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
