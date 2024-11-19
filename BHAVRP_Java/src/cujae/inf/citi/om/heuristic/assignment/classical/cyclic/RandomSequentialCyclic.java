package cujae.inf.citi.om.heuristic.assignment.classical.cyclic;

import java.util.ArrayList;
import java.util.Random;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;

/*Clase que modela como asignar clientes a los depósitos dn forma secuencial por depósitos escogiendo el depósito al azar*/

public class RandomSequentialCyclic extends Cyclic{
	
	public RandomSequentialCyclic() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(InfoProblem.getProblem().getListIDDepots());
		NumericMatrix costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		
		int totalItems = listCustomersToAssign.size();
		int totalClusters = listIDDepots.size();
		
		RowCol rcBestElement = null;
		boolean isFull = false;
		int countTry = 0;
		
		int posElementMatrix = -1;
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		Random random = new Random();
		int posRDMDepot = -1;
		double capacityDepot = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;
		
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()))
		{
			posRDMDepot = random.nextInt(listIDDepots.size());
			posElementMatrix = InfoProblem.getProblem().getPosElement(listIDDepots.get(posRDMDepot));
			capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(listIDDepots.get(posRDMDepot)));
			
			posCluster = posRDMDepot;
		
			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();

				while(!isFull) 
				{
					rcBestElement = costMatrix.indexLowerValue(posElementMatrix, 0, posElementMatrix, (totalItems - 1));
					
					idCustomer = InfoProblem.getProblem().getCustomers().get(rcBestElement.getCol()).getIDCustomer();
		        	requestCustomer = InfoProblem.getProblem().getCustomers().get(rcBestElement.getCol()).getRequestCustomer();

					if(capacityDepot >= (requestCluster + requestCustomer))
					{	
						requestCluster += requestCustomer;
						
						listClusters.get(posCluster).setRequestCluster(requestCluster);
						listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);
					
						listCustomersToAssign.remove(InfoProblem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));

						costMatrix.fillValue(rcBestElement.getRow(), 0, rcBestElement.getRow(), (totalItems - 1), Double.POSITIVE_INFINITY);
						costMatrix.fillValue(0, rcBestElement.getCol(), (totalItems + totalClusters - 1), rcBestElement.getCol(), Double.POSITIVE_INFINITY);

						posElementMatrix = InfoProblem.getProblem().getPosElement(listClusters.get(posCluster).getItemsOfCluster().get(listClusters.get(posCluster).getItemsOfCluster().size() - 1));
						
						if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
						{
							if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
								solution.getClusters().add(listClusters.remove(posCluster));
							else
								listClusters.remove(posCluster);
							
							listIDDepots.remove(posCluster);
							isFull = true;
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
							
							listIDDepots.remove(posCluster);
							isFull = true;
						}
					}
				}
				
				isFull = false;
				countTry = 0;				
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