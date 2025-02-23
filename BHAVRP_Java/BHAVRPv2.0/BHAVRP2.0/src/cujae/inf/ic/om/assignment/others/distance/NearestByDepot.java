package cujae.inf.ic.om.assignment.others.distance;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.Cluster;
import cujae.inf.ic.om.problem.output.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

/*Clase que modela como asignar los clientes a los dep�sitos en el orden de la lista de dep�sitos partiendo del criterio de cercan�a a los clientes*/
public class NearestByDepot extends ByNotUrgency {

	private Solution solution = new Solution();	

	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	private ArrayList<Integer> listIDDepots;
	private NumericMatrix costMatrix;
		
	public NearestByDepot() {
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
		int idDepot = -1;
		int posDepot = 0;
		double capacityDepot = 0.0;	
		int posDepotMatrix = -1;
		
		int idCustomer = -1;
		int posCustomer = -1;
		double requestCustomer = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;
		
		RowCol rcBestCustomer = null; 

		boolean isNext = true;
		
		int totalCustomers = listCustomersToAssign.size();
		int totalDepots = listIDDepots.size();
		
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()) && (!costMatrix.fullMatrix(Double.POSITIVE_INFINITY)))
		{	
			if(isNext)
			{
				idDepot = listIDDepots.get(posDepot);
				capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idDepot));
				posDepotMatrix = Problem.getProblem().getPosElement(idDepot);
			}
			
			rcBestCustomer = costMatrix.indexLowerValue(posDepotMatrix, 0, posDepotMatrix, (totalCustomers - 1));

			posCustomer = rcBestCustomer.getCol();
			idCustomer = Problem.getProblem().getCustomers().get(posCustomer).getIDCustomer();
			requestCustomer = Problem.getProblem().getCustomers().get(posCustomer).getRequestCustomer();

			costMatrix.setItem(posDepotMatrix, posCustomer, Double.POSITIVE_INFINITY);
			
			posCluster = findCluster(idDepot, listClusters);

			if(posCluster != -1) 
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();
				
				if(capacityDepot >= (requestCluster + requestCustomer)) 
				{
					requestCluster += requestCustomer;

					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					costMatrix.fillValue(totalCustomers, posCustomer, (totalCustomers + totalDepots - 1), posCustomer, Double.POSITIVE_INFINITY);

					listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));

					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
					{
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);
						
						listIDDepots.remove(posDepot);
						posDepot--;
					}
					
					posDepot++;

					if(posDepot >= listIDDepots.size())
						posDepot = 0;
					
					isNext = true;
				}	
				else
				{
					isNext = false;
					
					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
					{
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);
						
						listIDDepots.remove(posDepot);
						posDepot--;
						
						posDepot++;

						if(posDepot >= listIDDepots.size())
							posDepot = 0;
						
						isNext = true;
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
				if(!listClusters.get(k).getItemsOfCluster().isEmpty())
					solution.getClusters().add(listClusters.get(k));
		
		OSRMService.clearDistanceCache();
		
		return solution;
	}
}