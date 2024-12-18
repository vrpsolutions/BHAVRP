package cujae.inf.ic.om.heuristic.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class Sweep extends ByUrgency {

	public Sweep() {
		super();
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();	
		
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(Problem.getProblem().getListIDDepots());
		
		NumericMatrix urgencyMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		NumericMatrix closestMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		
		ArrayList<ArrayList<Integer>> listDepotsOrdered = getDepotsOrdered(listCustomersToAssign, listIDDepots, closestMatrix);
		int muIDDepot = findClusterWithMU(listClusters);
		ArrayList<Double> listUrgencies = getListUrgencies(listCustomersToAssign, listDepotsOrdered, urgencyMatrix, muIDDepot);	

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
			capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idClosestDepot));
			
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
					
					if(idClosestDepot == muIDDepot)
					{
						muIDDepot = findClusterWithMU(listClusters);

						if(idClosestDepot != muIDDepot)
						{
							urgencyMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
							listUrgencies = getListUrgencies(listCustomersToAssign, listDepotsOrdered, urgencyMatrix, muIDDepot);
						}
					}
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
							listUrgencies.set(posCustomer, getUrgency(idCustomer, listDepotsOrdered.get(posCustomer), urgencyMatrix, muIDDepot));
					}						
					else
					{					
						int posDepot = Problem.getProblem().findPosElement(listIDDepots, idClosestDepot);
						int currentPosDepot = -1;
						int totalAvailableDepots = 0;
						
						for(int i = 0; i < listDepotsOrdered.size(); i++)
						{ 
							currentPosDepot = Problem.getProblem().findPosElement(listDepotsOrdered.get(i), idClosestDepot);

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
										listUrgencies.set(i, getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listDepotsOrdered.get(i), urgencyMatrix, muIDDepot));
									
									listDepotsOrdered.get(i).remove(currentPosDepot);
								}	
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
     * @param ArrayList<Cluster> listado de clusteres
     * @return int posición del cluster de mayor demanda insatisfecha
     * Retorna el identificador del depósito cuyo cluster es el de mayor demanada insatisfecha 
     **/
	private int findClusterWithMU(ArrayList<Cluster> clusters){
		int idDepot = clusters.get(0).getIDCluster();
		double muRequest = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idDepot)) - clusters.get(0).getRequestCluster();
		
		double cuRequest = 0.0;
		
		for(int i = 1; i < clusters.size(); i++)
		{
			cuRequest = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(clusters.get(i).getIDCluster())) - clusters.get(i).getRequestCluster();
			
			if(cuRequest > muRequest)
			{
				muRequest = cuRequest;
				idDepot = clusters.get(i).getIDCluster();
			}						
		}

		return idDepot;
	}

	/**
     * @param  int identificador del cliente
     * @param  ArrayList<Integer> listado de identificadores de depósitos
     * @param  NumericMatrix matriz con las distancias
     * @param int identificador del depósito con mayor demanada insatisfecha
     * @return double urgencia del cliente
     * Retorna la urgencia del cliente 
     **/
	@Override
	protected double getUrgency(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix urgencyMatrix, int muIDDepot) {
		double urgency = 0.0;
		int posCustomerMatrix = -1;
		double muDist = 0.0;
		double closestDist = 0.0;
		int posMUDepotMatrix = -1;
		int posDepotMatrixClosest = -1;
		
		posCustomerMatrix = Problem.getProblem().getPosElement(idCustomer);
		posDepotMatrixClosest = Problem.getProblem().getPosElement(listIDDepots.get(0));
		closestDist = urgencyMatrix.getItem(posDepotMatrixClosest, posCustomerMatrix);
		
		posMUDepotMatrix = Problem.getProblem().getPosElement(muIDDepot);
		muDist = urgencyMatrix.getItem(posMUDepotMatrix, posCustomerMatrix);

		if(muDist == Double.POSITIVE_INFINITY) // y la otra distancia no puede ser posiinf
			urgency = closestDist;
		else
			urgency = calculateUrgency(closestDist, muDist);
		
		return urgency;
	}
}