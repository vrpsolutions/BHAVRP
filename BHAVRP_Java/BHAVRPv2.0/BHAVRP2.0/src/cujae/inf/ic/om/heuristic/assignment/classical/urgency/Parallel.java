package cujae.inf.ic.om.heuristic.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class Parallel extends ByUrgency implements IUrgency {

	private Solution solution = new Solution();	
	
	private ArrayList<Cluster> listClusters;	
	private ArrayList<Customer> listCustomersToAssign;
	private ArrayList<Integer> listIDDepots;
	
	private NumericMatrix urgencyMatrix;
	private NumericMatrix closestMatrix;
	
	private ArrayList<ArrayList<Integer>> listDepotsOrdered;
	private ArrayList<Double> listUrgencies;

	private int posCustomer = -1;
	private int idCustomer = -1;
	private double requestCustomer = 0.0;	
	private int idClosestDepot = -1;
	private double capacityDepot = 0.0;
	private int posCluster = -1;
	private double requestCluster = 0.0;
	
	public Parallel() {
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
		
		urgencyMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		closestMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		
		listDepotsOrdered = getDepotsOrdered(listCustomersToAssign, listIDDepots, closestMatrix);
		listUrgencies = getListUrgencies(listCustomersToAssign, listDepotsOrdered, urgencyMatrix);
	}	
		
	@Override
	public void assign() {
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
							listUrgencies.set(posCustomer, getUrgency(idCustomer, listDepotsOrdered.get(posCustomer), urgencyMatrix));
					}
					else 
					{ 
						int currentPosDepot = -1;
						int posDepot = Problem.getProblem().findPosElement(listIDDepots, idClosestDepot);
					
						for(int i = 0; i < listDepotsOrdered.size(); i++)
						{ 
							currentPosDepot = Problem.getProblem().findPosElement(listDepotsOrdered.get(i), idClosestDepot);

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
									listUrgencies.set(i, getUrgency(listCustomersToAssign.get(i).getIDCustomer(), listDepotsOrdered.get(i), urgencyMatrix));
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
	public double getUrgency(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix urgencyMatrix) {
		double urgency = 0.0;
		double closestDist = 0.0;
		double otherDist = 0.0;
		int posMatrixCustomer = -1;
		int posMatrixDepot = -1;
		
		posMatrixCustomer = Problem.getProblem().getPosElement(idCustomer);
		posMatrixDepot = Problem.getProblem().getPosElement(listIDDepots.get(0));
		
		closestDist = urgencyMatrix.getItem(posMatrixDepot, posMatrixCustomer);
		
		if(listIDDepots.size() == 1)
			urgency = closestDist;
		else
		{
			if(listIDDepots.size() > 1)
			{
				for(int i = 1; i < listIDDepots.size(); i++)
				{
					posMatrixDepot = Problem.getProblem().getPosElement(listIDDepots.get(i));		
					otherDist += urgencyMatrix.getItem(posMatrixDepot, posMatrixCustomer);
				}
				
				urgency = calculateUrgency(closestDist, otherDist);
			}
		}
		
		return urgency;	
	}
}