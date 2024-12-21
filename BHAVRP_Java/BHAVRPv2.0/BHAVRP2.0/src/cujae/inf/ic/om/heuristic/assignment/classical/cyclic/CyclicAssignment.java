package cujae.inf.ic.om.heuristic.assignment.classical.cyclic;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.heuristic.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

/*Clase que modela como asignar el mejor cliente al último cliente - depósito asignado en forma paralela por depósitos*/
public class CyclicAssignment extends ByNotUrgency { 

	public CyclicAssignment() {
		super();
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		NumericMatrix costMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		
		int totalClusters = Problem.getProblem().getDepots().size();
		int totalItems = listCustomersToAssign.size();

		ArrayList<Integer> itemsSelected = new ArrayList<Integer>();

		for (int i = 0; i < totalClusters; i++) 
			itemsSelected.add((totalItems + i));

		RowCol rcBestElement = null;
		int j = 0;
		boolean isNext = true;
		
		int posElementMatrix = -1;
		double capacityDepot = 0.0;
		
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0; 


		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()))
		{
			if(isNext)
			{
				capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(listClusters.get(j).getIDCluster()));
				posElementMatrix = itemsSelected.get(j);
				posCluster = j;
			}

			rcBestElement = costMatrix.indexLowerValue(posElementMatrix, 0, posElementMatrix, (totalItems - 1));

			idCustomer = Problem.getProblem().getCustomers().get(rcBestElement.getCol()).getIDCustomer(); 
			requestCustomer = Problem.getProblem().getCustomers().get(rcBestElement.getCol()).getRequestCustomer();

			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();

				if(capacityDepot >= requestCluster + requestCustomer) 
				{	
					requestCluster += requestCustomer;

					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));

					costMatrix.fillValue(0, rcBestElement.getCol(), (totalItems  + totalClusters - 1), rcBestElement.getCol(), Double.POSITIVE_INFINITY);
					costMatrix.fillValue(posElementMatrix, 0, posElementMatrix, (totalItems - 1), Double.POSITIVE_INFINITY);
					
					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
					{
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);
						
						itemsSelected.remove(j);	
					}
					else 
						j++;
					
					isNext = true;
				} 
				else 
				{
					isNext = false;
					costMatrix.setItem(posElementMatrix, rcBestElement.getCol(), Double.POSITIVE_INFINITY);

					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
					{
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);
						
						itemsSelected.remove(j);	
						isNext = true;
					}
					else
					{  // codigo repetido puede optimizarse revisar
						if(costMatrix.fullMatrix(posElementMatrix, 0, posElementMatrix, (totalItems - 1), Double.POSITIVE_INFINITY))
						{
							if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
								solution.getClusters().add(listClusters.remove(posCluster));
							else
								listClusters.remove(posCluster);

							listClusters.remove(posCluster);
							itemsSelected.remove(j);
							isNext = true;
						}
					}	
				}

				if (j == listClusters.size())
				{
					if((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()))
					{
						j = 0;
						itemsSelected.clear();

						for(int k = 0; k < listClusters.size(); k++) 
						{
							if(!listClusters.get(k).getItemsOfCluster().isEmpty())
								posElementMatrix = Problem.getProblem().getPosElement(listClusters.get(k).getItemsOfCluster().get(listClusters.get(k).getItemsOfCluster().size() - 1));	

							itemsSelected.add(posElementMatrix);
						}

						isNext = true;
					}	
				}
			}
		}

		if(!listCustomersToAssign.isEmpty())					
			for(int i = 0; i < listCustomersToAssign.size(); i++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(i).getIDCustomer());

		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));

		return solution;
	}    
}