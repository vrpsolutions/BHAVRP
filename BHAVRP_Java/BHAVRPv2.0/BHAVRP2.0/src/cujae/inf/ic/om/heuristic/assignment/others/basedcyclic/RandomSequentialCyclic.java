package cujae.inf.ic.om.heuristic.assignment.others.basedcyclic;

import java.util.ArrayList;
import java.util.Random;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.heuristic.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

/*Clase que modela como asignar clientes a los depósitos dn forma secuencial por depósitos escogiendo el depósito al azar*/
public class RandomSequentialCyclic extends ByNotUrgency {
	
	Random random = new Random();
	
	public static DistanceType distanceType = DistanceType.Real;
	private Solution solution = new Solution();	
	
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	private ArrayList<Integer> listIDDepots;
	private NumericMatrix costMatrix;
		
	public RandomSequentialCyclic() {
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
		costMatrix = initializeCostMatrix(listCustomersToAssign, Problem.getProblem().getDepots(), distanceType);
	}	
	
	@Override
	public void assign() {
		int posElementMatrix = -1;
		
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		int posRDMDepot = -1;
		double capacityDepot = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;
		
		RowCol rcBestElement = null;
		
		int countTry = 0;
		
		boolean isFull = false;
		
		int totalItems = listCustomersToAssign.size();
		int totalClusters = listIDDepots.size();
		
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()))
		{
			posRDMDepot = random.nextInt(listIDDepots.size());
			posElementMatrix = Problem.getProblem().getPosElement(listIDDepots.get(posRDMDepot));
			capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(listIDDepots.get(posRDMDepot)));
			
			posCluster = posRDMDepot;
		
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
}