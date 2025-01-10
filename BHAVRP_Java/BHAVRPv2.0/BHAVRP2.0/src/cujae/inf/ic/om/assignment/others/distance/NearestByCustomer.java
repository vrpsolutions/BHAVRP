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

/*Clase que modela como asignar los clientes a los depósitos considerando el orden de la lista de clientes y se parte del criterio de cercanía a los depósitos*/
public class NearestByCustomer extends ByNotUrgency {

	private Solution solution = new Solution();	
	
	private ArrayList<Cluster> listClusters; 
	private ArrayList<Customer> listCustomersToAssign;
	private NumericMatrix costMatrix;
	
	public NearestByCustomer() {
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
		costMatrix = initializeCostMatrix(listCustomersToAssign, Problem.getProblem().getDepots(), distanceType);
	}

	@Override
	public void assign() {
		int idDepot = -1;
		int posDepot = -1;
		double capacityDepot = 0.0;

		int posCustomer = 0;
		double requestCustomer = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;
		
		RowCol rcBestDepot = null;
		int countTry = 0;  
		
		int totalCustomers = listCustomersToAssign.size();
		int totalDepots = Problem.getProblem().getDepots().size();
		
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()) && (!costMatrix.fullMatrix(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1), Double.POSITIVE_INFINITY)))
		{	
			rcBestDepot = costMatrix.indexLowerValue(totalCustomers, posCustomer, (totalCustomers + totalDepots - 1), posCustomer);
			
			posDepot = (rcBestDepot.getRow() - totalCustomers);
			idDepot = Problem.getProblem().getListIDDepots().get(posDepot);
			capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idDepot));
			
			requestCustomer = listCustomersToAssign.get(0).getRequestCustomer();
			
			costMatrix.setItem(rcBestDepot.getRow(), rcBestDepot.getCol(), Double.POSITIVE_INFINITY);
			
			posCluster = findCluster(idDepot, listClusters);
		
			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();
				
				if(capacityDepot >= (requestCluster + requestCustomer)) 
				{
					requestCluster += requestCustomer;

					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(listCustomersToAssign.get(0).getIDCustomer());

					listCustomersToAssign.remove(0);
					posCustomer++;

					if(countTry != 0)
						countTry = 0;
				} 
				else 
				{
					countTry++;

					if(countTry >= listClusters.size())
					{
						countTry = 0;
						
						solution.getUnassignedItems().add(listCustomersToAssign.get(0).getIDCustomer());
						listCustomersToAssign.remove(0);
						posCustomer++;
					}
				}
				
				if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
				{
					if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
						solution.getClusters().add(listClusters.remove(posCluster));
					else
						listClusters.remove(posCluster);

					costMatrix.fillValue(rcBestDepot.getRow(), 0, rcBestDepot.getRow(), (totalCustomers - 1), Double.POSITIVE_INFINITY);
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
}