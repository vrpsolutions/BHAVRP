package cujae.inf.citi.om.heuristic.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.citi.om.heuristic.output.*;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;
import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;

public class Simplified extends ByUrgency {

	public Simplified() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Solution toClustering(){
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		int totalItems = listCustomersToAssign.size();
		ArrayList<ArrayList<Integer>> listIDDepots = new ArrayList<ArrayList<Integer>>();
		listIDDepots.add(InfoProblem.getProblem().getListIDDepots());

		NumericMatrix urgencyMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		NumericMatrix closestMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		
		ArrayList<ArrayList<Integer>> listDepotsOrdered = getDepotsOrdered(listCustomersToAssign, listIDDepots.get(0), closestMatrix);
		ArrayList<Double> listUrgencies = getListUrgencies(listCustomersToAssign, listIDDepots, urgencyMatrix, -1);
		
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
							listUrgencies.set(posCustomer, getUrgency(idCustomer, listIDDepots.get(0), urgencyMatrix, -1));
					}
					else
					{							
						int posDepot = InfoProblem.getProblem().findPosElement(listIDDepots.get(0), idClosestDepot);
						int currentPosDepot = -1;
						int totalAvailableDepots = 0;
						
						for(int i = 0; i < listDepotsOrdered.size(); i++)
						{ 
							currentPosDepot = InfoProblem.getProblem().findPosElement(listDepotsOrdered.get(i), idClosestDepot);

							if(currentPosDepot != -1)
							{
								totalAvailableDepots = listDepotsOrdered.get(i).size();

								if(totalAvailableDepots == 1)
								{
									solution.getUnassignedItems().add(listCustomersToAssign.get(i).getIDCustomer());
									
									listCustomersToAssign.remove(i);
									listUrgencies.remove(i);
									listDepotsOrdered.remove(i);
								}
								else
								{
									if(currentPosDepot == 0)
										listUrgencies.set(i, getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listIDDepots.get(0), urgencyMatrix, -1));
									else 
									{
										if(currentPosDepot == 1)
										{
											double currentUrgency = listUrgencies.get(i); 
											int posElement = InfoProblem.getProblem().getPosElement(listCustomersToAssign.get(i).getIDCustomer());

											RowCol rcClosestDepot = urgencyMatrix.indexLowerValue(totalItems, posElement, (totalItems + listIDDepots.size() - 1), posElement);
											double secondDist = urgencyMatrix.getItem(rcClosestDepot.getRow(), rcClosestDepot.getCol());	
											urgencyMatrix.setItem(rcClosestDepot.getRow(), rcClosestDepot.getCol(), Double.POSITIVE_INFINITY);

											rcClosestDepot = urgencyMatrix.indexLowerValue(totalItems, posElement, (totalItems + listIDDepots.size() - 1),  posElement);
											double thirdDist = urgencyMatrix.getItem(rcClosestDepot.getRow(), rcClosestDepot.getCol());

											double firstDist = currentUrgency - secondDist;
											currentUrgency = calculateUrgency(firstDist, thirdDist);

											listUrgencies.set(i, currentUrgency);
										}
									}
									
									listDepotsOrdered.get(i).remove(currentPosDepot);
								}									
							}
						}
						
						listIDDepots.get(0).remove(posDepot);	
						
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
	protected double getUrgency(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix urgencyMatrix, int muIDDepot){
		double urgency = 0.0;
		double closestDist = 0.0;
		double otherDist = 0.0;
		RowCol rcClosetDepot = new RowCol();
		int posMatrixCustomer = -1;

		int totalCustomers = InfoProblem.getProblem().getCustomers().size();
		posMatrixCustomer = InfoProblem.getProblem().getPosElement(idCustomer);
			
		rcClosetDepot = urgencyMatrix.indexLowerValue(totalCustomers, posMatrixCustomer, (totalCustomers + listIDDepots.size() - 1), posMatrixCustomer);
		closestDist = urgencyMatrix.getItem(rcClosetDepot.getRow(), rcClosetDepot.getCol());	
		urgencyMatrix.setItem(rcClosetDepot.getRow(), rcClosetDepot.getCol(), Double.POSITIVE_INFINITY);

		rcClosetDepot = urgencyMatrix.indexLowerValue(totalCustomers, posMatrixCustomer, (totalCustomers + listIDDepots.size() - 1),  posMatrixCustomer);
		otherDist = urgencyMatrix.getItem(rcClosetDepot.getRow(), rcClosetDepot.getCol());
		
		if(otherDist == Double.POSITIVE_INFINITY)
			urgency = closestDist;
		else
			urgency = calculateUrgency(closestDist, otherDist);

		return urgency;
	}
}