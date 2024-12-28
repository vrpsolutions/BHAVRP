package cujae.inf.ic.om.heuristic.assignment.others.distance;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.heuristic.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

/*Clase que modela como asignar los clientes a los depósitos partiendo del criterio de ser los mejores candidatos*/
public class BestNearest extends ByNotUrgency {

	private Solution solution = new Solution();
	
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	private ArrayList<Integer> listIDDepots;
	private NumericMatrix costMatrix;
	
    public BestNearest() {
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
		int idCustomer = -1;
		int posCustomer = -1;
		double requestCustomer = 0.0;
		
		int idDepot = -1;
		int posDepot = -1;
		double capacityDepot = 0.0;
		
		int posCluster = -1; 
		double requestCluster = 0.0;
		
		RowCol rcBestAll = null;
		
		int totalCustomers = listCustomersToAssign.size();
		int totalDepots = listIDDepots.size(); 
		
        while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()) && (!costMatrix.fullMatrix(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1), Double.POSITIVE_INFINITY))) 
        {					
        	rcBestAll = costMatrix.indexLowerValue(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1));

        	posDepot = rcBestAll.getRow() - totalCustomers;
        	idDepot = listIDDepots.get(posDepot);      	
        	capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idDepot));

           	posCustomer = rcBestAll.getCol();
           	idCustomer = Problem.getProblem().getCustomers().get(posCustomer).getIDCustomer();
        	requestCustomer = Problem.getProblem().getCustomers().get(posCustomer).getRequestCustomer();

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
        		} 
        		else
        			costMatrix.setItem(rcBestAll.getRow(), posCustomer, Double.POSITIVE_INFINITY);
        		
        		if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
    			{
					if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
						solution.getClusters().add(listClusters.remove(posCluster));
					else
						listClusters.remove(posCluster);
    			
    				costMatrix.fillValue(rcBestAll.getRow(), 0, rcBestAll.getRow(), (totalCustomers + totalDepots - 1), Double.POSITIVE_INFINITY);
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