package cujae.inf.citi.om.heuristic.assignment.classical.cyclic;

import java.util.ArrayList;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;

/*Clase que modela como asignar el mejor cliente al último cliente - depósito asignando en forma paralela */

public class BestCyclicAssignment extends Cyclic{

	public BestCyclicAssignment() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	 public Solution toClustering() {
    	Solution solution = new Solution();		
		
    	ArrayList<Cluster> clusters = initializeClusters();
    	
		ArrayList<Customer> customersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		NumericMatrix costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
    	
		int totalItems = customersToAssign.size();
		int totalClusters = InfoProblem.getProblem().getDepots().size();
      
        ArrayList<Integer> itemsSelected = new ArrayList<Integer>();
        
        for(int i = 0; i < totalClusters; i++) 
        	itemsSelected.add((totalItems + i));
        
        RowCol rcBestAllSelected = null;
       
        int posElementMatrix = -1;
        double capacityDepot = 0.0;
       
        double requestCustomer = 0.0;
        
        double requestCluster = 0.0; 
        int posCluster = -1;
       
        while((!customersToAssign.isEmpty()) && (!clusters.isEmpty()))
        {
        	rcBestAllSelected = getBestValueOfSelected(itemsSelected, costMatrix, totalItems);
        	posElementMatrix = rcBestAllSelected.getRow();
        	
        	if(posElementMatrix >= totalItems) 
        		posCluster = posElementMatrix - totalItems;
        	else
        		posCluster = findElementInSelected(itemsSelected, posElementMatrix);

        	capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(clusters.get(posCluster).getIDCluster()));
        		    	
            requestCustomer = InfoProblem.getProblem().getCustomers().get(rcBestAllSelected.getCol()).getRequestCustomer();
        
			if(posCluster != -1)
			{
				requestCluster = clusters.get(posCluster).getRequestCluster();
				
				if (capacityDepot >= requestCluster + requestCustomer) 
				{	
					requestCluster += requestCustomer;
					
					clusters.get(posCluster).setRequestCluster(requestCluster);
					clusters.get(posCluster).getItemsOfCluster().add(InfoProblem.getProblem().getCustomers().get(rcBestAllSelected.getCol()).getIDCustomer());
					
					customersToAssign.remove(InfoProblem.getProblem().findPosCustomer(customersToAssign, InfoProblem.getProblem().getCustomers().get(rcBestAllSelected.getCol()).getIDCustomer()));
	
					costMatrix.fillValue(0, rcBestAllSelected.getCol(), (totalItems  + totalClusters - 1), rcBestAllSelected.getCol(), Double.POSITIVE_INFINITY);
					costMatrix.fillValue(posElementMatrix, 0, posElementMatrix, (totalItems - 1), Double.POSITIVE_INFINITY);
					
					itemsSelected.remove(posCluster);
					itemsSelected.add(posCluster, rcBestAllSelected.getCol());
				} 
				else 
					costMatrix.setItem(posElementMatrix, rcBestAllSelected.getCol(), Double.POSITIVE_INFINITY);
				
				if(isFullDepot(customersToAssign, requestCluster, capacityDepot))
				{
					/*if(!(clusters.get(posCluster).getItemsOfCluster().isEmpty()))
						solution.getClusters().add(clusters.remove(posCluster));
					else
						clusters.remove(posCluster);*/
				
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
        
		if(!customersToAssign.isEmpty())					
			for(int i = 0; i < customersToAssign.size(); i++)	
				solution.getUnassignedItems().add(customersToAssign.get(i).getIDCustomer());
				
		if(!clusters.isEmpty()) //no va a pasar
			for(int k = 0; k < clusters.size(); k++)
				if((clusters.get(k) != null) && (!clusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(clusters.get(k));
		
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