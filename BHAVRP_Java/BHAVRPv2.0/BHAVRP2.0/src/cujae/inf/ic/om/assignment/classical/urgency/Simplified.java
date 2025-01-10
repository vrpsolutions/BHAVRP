package cujae.inf.ic.om.assignment.classical.urgency;

import java.util.ArrayList;
import java.util.Collections;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.Cluster;
import cujae.inf.ic.om.problem.output.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

public class Simplified extends ByUrgency implements IUrgency {
	private Solution solution = new Solution();	
	
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;

	private NumericMatrix urgencyMatrix;
	private NumericMatrix closestMatrix;
	
	private ArrayList<ArrayList<Integer>> listIDDepots;
	private ArrayList<ArrayList<Integer>> listDepotsOrdered;
	private ArrayList<Double> listUrgencies;
	
	public Simplified() {
		super();
	}
	
	@Override
	public Solution toClustering(){
		initialize();
		assign();
		return finish();
	}	
	
	@Override
	public void initialize() {
		listClusters = initializeClusters();
		listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		
		urgencyMatrix = initializeCostMatrix(listCustomersToAssign, Problem.getProblem().getDepots(), distanceType);
		closestMatrix = new NumericMatrix(urgencyMatrix);
		
		listIDDepots = new ArrayList<ArrayList<Integer>>();
		listIDDepots.add(Problem.getProblem().getListIDDepots());
		
		listDepotsOrdered = getDepotsOrdered(listCustomersToAssign, listIDDepots.get(0), closestMatrix);
		listUrgencies = getListUrgencies(listCustomersToAssign, listIDDepots, urgencyMatrix);
	}
	
	@Override
	public void assign() {
		int posCustomer = -1;
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		int idClosestDepot = -1;
		double capacityDepot = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;
		
		int totalItems = listCustomersToAssign.size();

		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty())) 
		{
			posCustomer = listUrgencies.indexOf(Collections.max(listUrgencies));
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
							listUrgencies.set(posCustomer, getUrgency(idCustomer, listIDDepots.get(0), urgencyMatrix));
					}
					else
					{							
						int posDepot = Problem.getProblem().findPosElement(listIDDepots.get(0), idClosestDepot);
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
										listUrgencies.set(i, getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listIDDepots.get(0), urgencyMatrix));
									else 
									{
										if(currentPosDepot == 1)
										{
											double currentUrgency = listUrgencies.get(i); 
											int posElement = Problem.getProblem().getPosElement(listCustomersToAssign.get(i).getIDCustomer());

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
	}
	
	@Override
	public Solution finish() {
		if(!listCustomersToAssign.isEmpty())					
			for(int j = 0; j < listCustomersToAssign.size(); j++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(j).getIDCustomer());

		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));
		
		OSRMService.clearDistanceCache();
		
		return solution;
	}
	
	 /**
     * @param  ArrayList<Customer> listado de clientes
     * @param  ArrayList<Integer> listado de identificadores de depósitos
     * @param  NumericMatrix matriz con las distancias
     * @param  int identificador del depósito de mayor demanda insatisfecha
     * @return ArrayList<Double> listado de urgencia
     * Retorna un listado con las urgencias de los clientes del listado entrado por parámetro
     **/
	@Override
	public ArrayList<Double> getListUrgencies(ArrayList<Customer> listCustomersToAssign, ArrayList<ArrayList<Integer>> listIDDepots, NumericMatrix urgencyMatrix){
		ArrayList<Double> urgencies = new ArrayList<Double>();
		
		if(listIDDepots.size() > 1)
			for(int i = 0; i < listCustomersToAssign.size(); i++)
				urgencies.add(getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listIDDepots.get(i), urgencyMatrix));
		else
			for(int i = 0; i < listCustomersToAssign.size(); i++)
				urgencies.add(getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listIDDepots.get(0), urgencyMatrix));

		return urgencies;
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
	public double getUrgency(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix urgencyMatrix){
		double urgency = 0.0;
		double closestDist = 0.0;
		double otherDist = 0.0;
		RowCol rcClosetDepot = new RowCol();
		int posMatrixCustomer = -1;

		int totalCustomers = Problem.getProblem().getCustomers().size();
		posMatrixCustomer = Problem.getProblem().getPosElement(idCustomer);
			
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