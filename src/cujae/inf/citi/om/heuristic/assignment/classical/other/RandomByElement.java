package cujae.inf.citi.om.heuristic.assignment.classical.other;

import java.util.ArrayList;
import java.util.Random;

import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;

/*Clase que modela como asignar los clientes a los depósitos de forma aleatoria la selección de ambos*/

public class RandomByElement extends Other{

	public RandomByElement() {
		super();
	}

	@Override
	public Solution toClustering() {		
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(InfoProblem.getProblem().getListIDDepots());
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());

		Random random = new Random();
		int posRDMDepot = -1;
		int posRDMCustomer = -1;
		
		int idDepot = -1;
		double capacityDepot = 0.0;
		
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		double requestCluster = 0.0;
		int posCluster = -1;
		
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()))
		{		
			posRDMDepot = random.nextInt(listIDDepots.size());
			idDepot = listIDDepots.get(posRDMDepot);
			capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(idDepot));
				
			posRDMCustomer = random.nextInt(listCustomersToAssign.size());
			idCustomer = listCustomersToAssign.get(posRDMCustomer).getIDCustomer();
			requestCustomer = listCustomersToAssign.get(posRDMCustomer).getRequestCustomer();
			
			posCluster = findCluster(idDepot, listClusters);
			
			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();

				if(capacityDepot >= (requestCluster + requestCustomer)) 
				{
					requestCluster += requestCustomer;

					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);
					
					listCustomersToAssign.remove(posRDMCustomer);
				}
				
				if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
				{
					listIDDepots.remove(posRDMDepot);
					
					if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
						solution.getClusters().add(listClusters.remove(posCluster));
					else
						listClusters.remove(posCluster);
				}
			}
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