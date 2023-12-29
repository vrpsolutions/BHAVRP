package cujae.inf.citi.om.heuristic.assignment.classical;

import java.util.ArrayList;
import java.util.Random;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;
import cujae.inf.citi.om.heuristic.assignment.classical.distance.ByDistance;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;

public class RandomNearestByDepot extends ByDistance{

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(InfoProblem.getProblem().getListIDDepots());
		NumericMatrix costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
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
				capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(idDepot));
				posDepotMatrix = InfoProblem.getProblem().getPosElement(idDepot);
			}

			rcBestCustomer = costMatrix.indexLowerValue(posDepotMatrix, 0, posDepotMatrix, (totalCustomers - 1));

			posCustomer = rcBestCustomer.getCol();
			idCustomer = InfoProblem.getProblem().getCustomers().get(posCustomer).getIDCustomer();
			requestCustomer = InfoProblem.getProblem().getCustomers().get(posCustomer).getRequestCustomer();

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

					listCustomersToAssign.remove(InfoProblem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));

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
