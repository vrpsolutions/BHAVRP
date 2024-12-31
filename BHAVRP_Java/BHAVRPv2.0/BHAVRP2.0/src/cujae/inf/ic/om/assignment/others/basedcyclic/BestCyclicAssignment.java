package cujae.inf.ic.om.assignment.others.basedcyclic;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.solution.Cluster;
import cujae.inf.ic.om.problem.solution.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

/*Clase que modela como asignar el mejor cliente al último cliente - depósito asignando en forma paralela */
public class BestCyclicAssignment extends ByNotUrgency {

	private Solution solution = new Solution();
	
	private ArrayList<Cluster> clusters;
	private ArrayList<Customer> customersToAssign;
	private NumericMatrix costMatrix;
    	
	public BestCyclicAssignment() {
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
    	clusters = initializeClusters();
		customersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		costMatrix = initializeCostMatrix(customersToAssign, Problem.getProblem().getDepots(), distanceType);
	}
	
	@Override
    public void assign() {
		int posElementMatrix = -1;
	    
		double capacityDepot = 0.0;   
	    
	    double requestCustomer = 0.0;    
	    
	    double requestCluster = 0.0; 
	    int posCluster = -1;
	    
	    RowCol rcBestAllSelected = null;
		
		int totalItems = customersToAssign.size();
		int totalClusters = Problem.getProblem().getDepots().size();
		
		ArrayList<Integer> itemsSelected = new ArrayList<>();
		
		for(int i = 0; i < totalClusters; i++) 
        	itemsSelected.add((totalItems + i));
		
        while((!customersToAssign.isEmpty()) && (!clusters.isEmpty()))
        {
        	rcBestAllSelected = getBestValueOfSelected(itemsSelected, costMatrix, totalItems);
        	posElementMatrix = rcBestAllSelected.getRow();
        	
        	if(posElementMatrix >= totalItems) 
        		posCluster = posElementMatrix - totalItems;
        	else
        		posCluster = findElementInSelected(itemsSelected, posElementMatrix);

        	capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(clusters.get(posCluster).getIDCluster()));
        		    	
            requestCustomer = Problem.getProblem().getCustomers().get(rcBestAllSelected.getCol()).getRequestCustomer();
        
			if(posCluster != -1)
			{
				requestCluster = clusters.get(posCluster).getRequestCluster();
				
				if (capacityDepot >= requestCluster + requestCustomer) 
				{	
					requestCluster += requestCustomer;
					
					clusters.get(posCluster).setRequestCluster(requestCluster);
					clusters.get(posCluster).getItemsOfCluster().add(Problem.getProblem().getCustomers().get(rcBestAllSelected.getCol()).getIDCustomer());
					
					customersToAssign.remove(Problem.getProblem().findPosCustomer(customersToAssign, Problem.getProblem().getCustomers().get(rcBestAllSelected.getCol()).getIDCustomer()));
	
					costMatrix.fillValue(0, rcBestAllSelected.getCol(), (totalItems  + totalClusters - 1), rcBestAllSelected.getCol(), Double.POSITIVE_INFINITY);
					costMatrix.fillValue(posElementMatrix, 0, posElementMatrix, (totalItems - 1), Double.POSITIVE_INFINITY);
					
					itemsSelected.remove(posCluster);
					itemsSelected.add(posCluster, rcBestAllSelected.getCol());
				} 
				else 
					costMatrix.setItem(posElementMatrix, rcBestAllSelected.getCol(), Double.POSITIVE_INFINITY);
				
				if(isFullDepot(customersToAssign, requestCluster, capacityDepot))
				{
					if(!(clusters.get(posCluster).getItemsOfCluster().isEmpty()))
						solution.getClusters().add(clusters.get(posCluster));

					clusters.remove(posCluster);
					clusters.add(posCluster, null);
					
					itemsSelected.remove(posCluster);	
					itemsSelected.add(posCluster, -1);
    				costMatrix.fillValue(posElementMatrix, 0, rcBestAllSelected.getRow(), (totalItems - 1), Double.POSITIVE_INFINITY);
				}
			}
        }
	}
	
    @Override
    public Solution finish() {   	
		if(!customersToAssign.isEmpty())					
			for(int i = 0; i < customersToAssign.size(); i++)	
				solution.getUnassignedItems().add(customersToAssign.get(i).getIDCustomer());
				
		if(!clusters.isEmpty()) //no va a pasar
			for(int k = 0; k < clusters.size(); k++)
				if((clusters.get(k) != null) && (!clusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(clusters.get(k));
		
		OSRMService.clearDistanceCache();
		
        return solution;
    }    
	
	private RowCol getBestValueOfSelected(ArrayList<Integer> itemsSelected, NumericMatrix costMatrix, int totalItems){
		RowCol rcBestAll = null;
		
		if((itemsSelected != null) && (!itemsSelected.isEmpty())) //esto no pasa
		{	// verificar que no tome valores de la diagonal principal
			
			RowCol rcCurrent = null;
			boolean isFirst = true;
			
			//rcBestAll = costMatrix.indexLowerValue(itemsSelected.get(0), 0, itemsSelected.get(0), (totalItems - 1)); 
			//función que busque la primera posicion que no es -1 en itemsSelected
			for(int i = 0; i < itemsSelected.size(); i++)
			{
				if(itemsSelected.get(i) != -1)
				{
					if(isFirst)
					{
						rcBestAll = costMatrix.indexLowerValue(itemsSelected.get(i), 0, itemsSelected.get(i), (totalItems - 1));
						rcCurrent = rcBestAll;
						
						isFirst = false;
					}		
					else
						rcCurrent = costMatrix.indexLowerValue(itemsSelected.get(i), 0, itemsSelected.get(i), (totalItems - 1));
					
					if(costMatrix.getItem(rcCurrent.getRow(), rcCurrent.getCol()) < costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()))
						rcBestAll = rcCurrent;
				}			
			}
		}
		return rcBestAll;
	}
	
	private int findElementInSelected(ArrayList<Integer> itemsSelected, Integer idItem){
		int posCluster = -1;
		
		int i = 0;
		boolean found = false;
		
		while((i < itemsSelected.size()) && (!found))
		{
			if(itemsSelected.get(i).intValue() == idItem.intValue())
			{
				posCluster = i;
				found = true;
			}
			else
				i++;
		}
		return posCluster;
	}
}