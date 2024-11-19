package cujae.inf.citi.om.heuristic.assignment.classical.distance;

import java.util.ArrayList;

import cujae.inf.citi.om.heuristic.output.*;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;
import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;

/*Clase que modela como asignar los clientes a los depósitos partiendo del criterio de ser los mejores candidatos*/

public class BestNearest extends ByDistance{
	
    public BestNearest() {
        super();
    }
    
    @Override
    public Solution toClustering() {
    	Solution solution = new Solution();		
		
    	ArrayList<Cluster> listClusters = initializeClusters();	
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(InfoProblem.getProblem().getListIDDepots());  
		NumericMatrix costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		int totalCustomers = listCustomersToAssign.size();
		int totalDepots = listIDDepots.size(); 
			
    	RowCol rcBestAll = null;
    	
		int idCustomer = -1;
		int posCustomer = -1;
		double requestCustomer = 0.0;
		
    	int idDepot = -1;
    	int posDepot = -1;
    	double capacityDepot = 0.0;

    	int posCluster = -1; 
		double requestCluster = 0.0;
		
        while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()) && (!costMatrix.fullMatrix(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1), Double.POSITIVE_INFINITY))) 
        {					
        	rcBestAll = costMatrix.indexLowerValue(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1));

        	posDepot = rcBestAll.getRow() - totalCustomers;
        	idDepot = listIDDepots.get(posDepot);      	
        	capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(idDepot));

           	posCustomer = rcBestAll.getCol();
           	idCustomer = InfoProblem.getProblem().getCustomers().get(posCustomer).getIDCustomer();
        	requestCustomer = InfoProblem.getProblem().getCustomers().get(posCustomer).getRequestCustomer();

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
        			listCustomersToAssign.remove(InfoProblem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
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
        
		if(!listCustomersToAssign.isEmpty())					
			for(int j = 0; j < listCustomersToAssign.size(); j++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(j).getIDCustomer());

		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));
		
		return solution;
    }
}
