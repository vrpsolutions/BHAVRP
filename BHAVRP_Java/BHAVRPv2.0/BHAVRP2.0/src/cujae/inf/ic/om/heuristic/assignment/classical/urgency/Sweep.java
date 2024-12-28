package cujae.inf.ic.om.heuristic.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.matrix.NumericMatrix;

public class Sweep extends ByUrgency implements IUrgencyWithMU {
	
	public static DistanceType distanceType = DistanceType.Real;
	private Solution solution = new Solution();
	
	private ArrayList<Cluster> listClusters;	
	private ArrayList<Customer> listCustomersToAssign;
	private ArrayList<Integer> listIDDepots;
	
	private NumericMatrix urgencyMatrix;
	private NumericMatrix closestMatrix;
	
	private ArrayList<ArrayList<Integer>> listDepotsOrdered;
	private int muIDDepot;
	private ArrayList<Double> listUrgencies;

	public Sweep() {
		super();
	}

	@Override
	public Solution toClustering() {
		initialize();
		assign();
		return finish();
	}
	
	@Override
	public void initialize() {
		listClusters = initializeClusters();	
		listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		
		listIDDepots = new ArrayList<Integer>(Problem.getProblem().getListIDDepots());
		
		urgencyMatrix = initializeCostMatrix(listCustomersToAssign, Problem.getProblem().getDepots(), distanceType);
		closestMatrix = new NumericMatrix(urgencyMatrix);
		
		listDepotsOrdered = getDepotsOrdered(listCustomersToAssign, listIDDepots, closestMatrix);
		muIDDepot = findClusterWithMU(listClusters);
		listUrgencies = getListUrgencies(listCustomersToAssign, listDepotsOrdered, urgencyMatrix, muIDDepot);	
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
							urgencyMatrix = initializeCostMatrix(Problem.getProblem().getCustomers(), Problem.getProblem().getDepots(), distanceType);
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
     * @param  ArrayList<Customer> listado de clientes
     * @param  ArrayList<Integer> listado de identificadores de depósitos
     * @param  NumericMatrix matriz con las distancias
     * @param  int identificador del depósito de mayor demanda insatisfecha
     * @return ArrayList<Double> listado de urgencia
     * Retorna un listado con las urgencias de los clientes del listado entrado por parámetro
     **/
	public ArrayList<Double> getListUrgencies(ArrayList<Customer> listCustomersToAssign, ArrayList<ArrayList<Integer>> listIDDepots, NumericMatrix urgencyMatrix, int muIDDepot){
		ArrayList<Double> urgencies = new ArrayList<Double>();
		
		if(listIDDepots.size() > 1)
			for(int i = 0; i < listCustomersToAssign.size(); i++)
				urgencies.add(getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listIDDepots.get(i), urgencyMatrix, muIDDepot));
		else
			for(int i = 0; i < listCustomersToAssign.size(); i++)
				urgencies.add(getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listIDDepots.get(0), urgencyMatrix, muIDDepot));

		return urgencies;
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
	public double getUrgency(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix urgencyMatrix, int muIDDepot) {
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