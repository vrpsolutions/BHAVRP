package cujae.inf.citi.om.heuristic.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.citi.om.heuristic.output.*;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;
import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;

public class ParallelPlus extends Parallel{

	public ParallelPlus() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Solution toClustering(){
		Solution solution = new Solution();		
		
		ArrayList<Cluster> clusters = initializeClusters();	

		ArrayList<Customer> customersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<>(InfoProblem.getProblem().getListIDDepots());
		
		NumericMatrix urgencyMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		NumericMatrix closestMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());

		ArrayList<ArrayList<Integer>> listDepotsOrdered = getDepotsOrdered(customersToAssign, listIDDepots, closestMatrix);
		ArrayList<Double> listUrgencies = getListUrgencies(customersToAssign, listDepotsOrdered, urgencyMatrix, -1);

		Customer customer = new Customer();
		double requestCustomer = 0.0;
		int posCustomer = -1;
		
		int idClosestDepot = -1;
		double capacityDepot = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;

		while((!customersToAssign.isEmpty()) && (!clusters.isEmpty())) 
		{
			posCustomer = getPosMaxValue(listUrgencies);
			customer = customersToAssign.get(posCustomer);
			requestCustomer = customer.getRequestCustomer();

			idClosestDepot = listDepotsOrdered.get(posCustomer).get(0).intValue(); 
			capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(idClosestDepot));

			posCluster = findCluster(idClosestDepot, clusters);

			if(posCluster != -1)
			{
				requestCluster = clusters.get(posCluster).getRequestCluster();

				if(capacityDepot >= (requestCluster + requestCustomer))
				{	
					requestCluster += requestCustomer;

					clusters.get(posCluster).setRequestCluster(requestCluster);
					clusters.get(posCluster).getItemsOfCluster().add(customer.getIDCustomer());

					customersToAssign.remove(posCustomer);
					listUrgencies.remove(posCustomer);
					listDepotsOrdered.remove(posCustomer);
				}	

				ArrayList<Integer> customersOutDepot = getCustomersOutDepot(customersToAssign, requestCluster, capacityDepot);
				int currentPosDepot = -1;

				if(!customersOutDepot.isEmpty())
				{
					if(customersOutDepot.size() ==  customersToAssign.size())
					{
						listIDDepots.remove(InfoProblem.getProblem().findPosElement(listIDDepots, idClosestDepot));

						if(!(clusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(clusters.remove(posCluster));
						else
							clusters.remove(posCluster);
					}

					for(int i = 0; i < customersOutDepot.size(); i++)
					{ 
						currentPosDepot = InfoProblem.getProblem().findPosElement(listDepotsOrdered.get(customersOutDepot.get(i)), idClosestDepot);

						if(currentPosDepot != -1)
						{
							listDepotsOrdered.get(customersOutDepot.get(i)).remove(currentPosDepot);

							if(listDepotsOrdered.get(customersOutDepot.get(i)).isEmpty())
							{
								solution.getUnassignedItems().add(customersToAssign.get(customersOutDepot.get(i)).getIDCustomer());

								customersToAssign.remove(customersOutDepot.get(i));
								listUrgencies.remove(customersOutDepot.get(i));
								listDepotsOrdered.remove(customersOutDepot.get(i));
							}
							else
								listUrgencies.set(customersOutDepot.get(i), getUrgency(customersToAssign.get(customersOutDepot.get(i)).getIDCustomer(), listDepotsOrdered.get(customersOutDepot.get(i)), urgencyMatrix, -1));
						}	
					}
				}
			}
		}
		
		if(!customersToAssign.isEmpty())					
			for(int j = 0; j < customersToAssign.size(); j++)	
				solution.getUnassignedItems().add(customersToAssign.get(j).getIDCustomer());

		if(!clusters.isEmpty())
			for(int k = 0; k < clusters.size(); k++)
				if(!(clusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(clusters.get(k));

		return solution;
	}
}