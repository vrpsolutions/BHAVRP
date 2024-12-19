package cujae.inf.ic.om.heuristic.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class ParallelPlus extends Parallel {

	public ParallelPlus() {
		super();
	}
	public Solution toClustering(){
		Solution solution = new Solution();		
		
		ArrayList<Cluster> clusters = initializeClusters();	

		ArrayList<Customer> customersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<>(Problem.getProblem().getListIDDepots());
		
		NumericMatrix urgencyMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		NumericMatrix closestMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());

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
			capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idClosestDepot));

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
						listIDDepots.remove(Problem.getProblem().findPosElement(listIDDepots, idClosestDepot));

						if(!(clusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(clusters.remove(posCluster));
						else
							clusters.remove(posCluster);
					}

					for(int i = 0; i < customersOutDepot.size(); i++)
					{ 
						currentPosDepot = Problem.getProblem().findPosElement(listDepotsOrdered.get(customersOutDepot.get(i)), idClosestDepot);

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