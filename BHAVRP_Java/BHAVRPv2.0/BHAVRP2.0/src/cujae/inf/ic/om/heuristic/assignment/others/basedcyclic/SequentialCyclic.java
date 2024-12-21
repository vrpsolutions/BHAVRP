package cujae.inf.ic.om.heuristic.assignment.others.basedcyclic;

import java.util.ArrayList;

import cujae.inf.ic.om.heuristic.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

/*Clase que modela como asignar elementos en forma secuencial por depósitos*/
public class SequentialCyclic extends ByNotUrgency {

	public SequentialCyclic() {
		super();
	}
	
	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(Problem.getProblem().getListIDDepots());
		NumericMatrix costMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		
		int totalItems = listCustomersToAssign.size();
		int totalClusters = listIDDepots.size();
		
		RowCol rcBestElement = null;
		boolean isFull = false;
		int i = 0;
		int countTry = 0;
		
		int posElementMatrix = -1;
		double capacityDepot = 0.0;
		
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;
	
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()))
		{
			posElementMatrix = totalItems + i;
			capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(listIDDepots.get(0)));	
			posCluster = 0;
				
			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();
			
				while(!isFull) 
				{
					rcBestElement = costMatrix.indexLowerValue(posElementMatrix, 0, posElementMatrix, (totalItems - 1));
				
					idCustomer = Problem.getProblem().getCustomers().get(rcBestElement.getCol()).getIDCustomer();
					requestCustomer = Problem.getProblem().getCustomers().get(rcBestElement.getCol()).getRequestCustomer();
					
					if(capacityDepot >= (requestCluster + requestCustomer))
					{	
						requestCluster += requestCustomer;
						
						listClusters.get(posCluster).setRequestCluster(requestCluster);
						listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

						listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
						
						costMatrix.fillValue(rcBestElement.getRow(), 0, rcBestElement.getRow(), (totalItems - 1), Double.POSITIVE_INFINITY);
						costMatrix.fillValue(0, rcBestElement.getCol(), (totalItems + totalClusters - 1), rcBestElement.getCol(), Double.POSITIVE_INFINITY);

						posElementMatrix = Problem.getProblem().getPosElement(listClusters.get(posCluster).getItemsOfCluster().get(listClusters.get(posCluster).getItemsOfCluster().size() - 1));
						
						if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
						{
							if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
								solution.getClusters().add(listClusters.remove(posCluster));
							else
								listClusters.remove(posCluster);
							
							isFull = true;
							listIDDepots.remove(0);
						}
					} 
					else 
					{				
						costMatrix.setItem(rcBestElement.getRow(), rcBestElement.getCol(), Double.POSITIVE_INFINITY);
						countTry++;
						
						if((countTry >= listCustomersToAssign.size()))
						{
							if(!listClusters.get(posCluster).getItemsOfCluster().isEmpty())
								solution.getClusters().add(listClusters.get(posCluster));

							listClusters.remove(posCluster);
													
							listIDDepots.remove(0);
							isFull = true;
						}
					}
				}
				
				isFull = false;
				countTry = 0;	
				i++;
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