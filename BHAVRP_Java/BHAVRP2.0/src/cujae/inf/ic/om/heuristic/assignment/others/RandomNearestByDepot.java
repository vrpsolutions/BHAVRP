package cujae.inf.ic.om.heuristic.assignment.others;

import java.util.ArrayList;
import java.util.Random;

import cujae.inf.ic.om.heuristic.assignment.classical.other.distance.ByDistance;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

public class RandomNearestByDepot extends ByDistance {

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(Problem.getProblem().getListIDDepots());
		NumericMatrix costMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		int totalCustomers = listCustomersToAssign.size();
		int totalDepots = listIDDepots.size();

		RowCol rcBestCustomer = null;  
		Random random = new Random();
		int idDepot = -1;
		int posDepotMatrix = -1;
		int posRDMDepot = -1;
		double capacityDepot = 0.0;
		int posCustomer = -1;
		int idCustomer = -1;
		double requestCustomer = 0.0;
		double requestCluster = 0.0;
		int posCluster = -1;
		boolean isNext = true;

		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()) && (!costMatrix.fullMatrix(Double.POSITIVE_INFINITY)))
		{	
			if(isNext)
			{
				posRDMDepot = random.nextInt(listIDDepots.size());
				idDepot = listIDDepots.get(posRDMDepot);
				capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idDepot));
				posDepotMatrix = Problem.getProblem().getPosElement(idDepot);
			}

			rcBestCustomer = costMatrix.indexLowerValue(posDepotMatrix, 0, posDepotMatrix, (totalCustomers - 1));

			posCustomer = rcBestCustomer.getCol();
			idCustomer = Problem.getProblem().getCustomers().get(posCustomer).getIDCustomer();
			requestCustomer = Problem.getProblem().getCustomers().get(posCustomer).getRequestCustomer();

			costMatrix.setItem(posDepotMatrix, posCustomer, Double.POSITIVE_INFINITY);

			posCluster = findCluster(idDepot, listClusters);

			if(posCluster != -1) 
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();

				if(capacityDepot >= (requestCluster + requestCustomer)) 
				{
					requestCluster += requestCustomer;

					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					costMatrix.fillValue(totalCustomers, posCustomer, (totalCustomers + totalDepots - 1), posCustomer, Double.POSITIVE_INFINITY);

					listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));

					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
					{
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);

						listIDDepots.remove(posRDMDepot);
					}

					isNext = true;
				}	
				else
				{
					isNext = false;

					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
					{
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);

						listIDDepots.remove(posRDMDepot);
						isNext = true;
					}
				}
			}
		}	

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