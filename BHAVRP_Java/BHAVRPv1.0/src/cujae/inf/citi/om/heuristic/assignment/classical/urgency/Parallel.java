package cujae.inf.citi.om.heuristic.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.citi.om.heuristic.output.*;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;
import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;

public class Parallel extends ByUrgency{

	public Parallel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Solution toClustering(){
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();	
		
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(InfoProblem.getProblem().getListIDDepots());
		
		NumericMatrix urgencyMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		NumericMatrix closestMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		
		ArrayList<ArrayList<Integer>> listDepotsOrdered = getDepotsOrdered(listCustomersToAssign, listIDDepots, closestMatrix);
		ArrayList<Double> listUrgencies = getListUrgencies(listCustomersToAssign, listDepotsOrdered, urgencyMatrix, -1);

		int posCustomer = -1;
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		int idClosestDepot = -1;
		double capacityDepot = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;

		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty())) 
		{
			posCustomer = getPosMaxValue(listUrgencies);
			idCustomer = listCustomersToAssign.get(posCustomer).getIDCustomer();
			requestCustomer = listCustomersToAssign.get(posCustomer).getRequestCustomer();

			idClosestDepot = listDepotsOrdered.get(posCustomer).get(0).intValue(); 
			capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(idClosestDepot));

			posCluster = findCluster(idClosestDepot, listClusters);

			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();

				if(capacityDepot >= (requestCluster + requestCustomer))
				{	
					requestCluster += requestCustomer;
					
					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					listCustomersToAssign.remove(posCustomer);
					listUrgencies.remove(posCustomer);
					listDepotsOrdered.remove(posCustomer);
				}
				else
				{
					if(capacityDepot > requestCluster)
					{
						listDepotsOrdered.get(posCustomer).remove(0);

						if(listDepotsOrdered.get(posCustomer).isEmpty())	
						{
							solution.getUnassignedItems().add(idCustomer);

							listCustomersToAssign.remove(posCustomer);
							listUrgencies.remove(posCustomer);
							listDepotsOrdered.remove(posCustomer);							
						}
						else	
							listUrgencies.set(posCustomer, getUrgency(idCustomer, listDepotsOrdered.get(posCustomer), urgencyMatrix, -1));
					}
					else 
					{ 
						int currentPosDepot = -1;
						int posDepot = InfoProblem.getProblem().findPosElement(listIDDepots, idClosestDepot);
					
						for(int i = 0; i < listDepotsOrdered.size(); i++)
						{ 
							currentPosDepot = InfoProblem.getProblem().findPosElement(listDepotsOrdered.get(i), idClosestDepot);

							if(currentPosDepot != -1)
							{
								listDepotsOrdered.get(i).remove(currentPosDepot);
								
								if(listDepotsOrdered.get(i).isEmpty())
								{
									solution.getUnassignedItems().add(listCustomersToAssign.get(i).getIDCustomer());

									listCustomersToAssign.remove(i);
									listUrgencies.remove(i);
									listDepotsOrdered.remove(i);
								}
								else
									listUrgencies.set(i, getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listDepotsOrdered.get(i), urgencyMatrix, -1));
							}
						}

						listIDDepots.remove(posDepot);
						
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);
					}
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
	
	/**
     * @param  int identificador del cliente
     * @param  ArrayList<Integer> listado de identificadores de depósitos
     * @param  NumericMatrix matriz con las distancias
     * @param int -1 identificador del depósito con mayor demanda insatisfecha
     * @return double urgencia del cliente
     * Retorna la urgencia del cliente 
     **/
	@Override
	protected double getUrgency(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix urgencyMatrix, int muIDDepot) {
		double urgency = 0.0;
		double closestDist = 0.0;
		double otherDist = 0.0;
		int posMatrixCustomer = -1;
		int posMatrixDepot = -1;
		
		posMatrixCustomer = InfoProblem.getProblem().getPosElement(idCustomer);
		posMatrixDepot = InfoProblem.getProblem().getPosElement(listIDDepots.get(0));
		
		closestDist = urgencyMatrix.getItem(posMatrixDepot, posMatrixCustomer);
		
		if(listIDDepots.size() == 1)
			urgency = closestDist;
		else
		{
			if(listIDDepots.size() > 1)
			{
				for(int i = 1; i < listIDDepots.size(); i++)
				{
					posMatrixDepot = InfoProblem.getProblem().getPosElement(listIDDepots.get(i));		
					otherDist += urgencyMatrix.getItem(posMatrixDepot, posMatrixCustomer);
				}
				
				urgency = calculateUrgency(closestDist, otherDist);
			}
		}
		
		return urgency;	
	}
}