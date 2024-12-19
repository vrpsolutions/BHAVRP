package cujae.inf.ic.om.heuristic.assignment.others;

import java.util.ArrayList;
import java.util.Random;

import cujae.inf.ic.om.heuristic.assignment.classical.other.distance.ByDistance;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

public class RandomNearestByCustomer extends ByDistance {

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
        NumericMatrix costMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		int totalCustomers = listCustomersToAssign.size();
        int totalDepots = Problem.getProblem().getDepots().size();
		
        Random random = new Random();
        RowCol rcBestDepot = null;
        int countTry = 0;  
        boolean isNext = true;
        
        int idDepot = -1;
        int posDepot = -1;
        double capacityDepot = 0.0;
        
        int posRDMCustomer = -1;
        int idCustomer = -1;
        int posCustomer = -1;
        double requestCustomer = 0.0;
        
        int posCluster = -1;
        double requestCluster = 0.0;
		
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()) && (!costMatrix.fullMatrix(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1), Double.POSITIVE_INFINITY)))
		{	
			if(isNext)
			{
				posRDMCustomer = random.nextInt(listCustomersToAssign.size());
				idCustomer = listCustomersToAssign.get(posRDMCustomer).getIDCustomer();
				requestCustomer = listCustomersToAssign.get(posRDMCustomer).getRequestCustomer();
				posCustomer = Problem.getProblem().findPosCustomer(Problem.getProblem().getCustomers(), idCustomer);
			}
			
			rcBestDepot = costMatrix.indexLowerValue(totalCustomers, posCustomer, (totalCustomers + totalDepots - 1), posCustomer);
			
			posDepot = rcBestDepot.getRow() - totalCustomers;
			idDepot = Problem.getProblem().getListIDDepots().get(posDepot);
			capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idDepot));
			
			costMatrix.setItem(rcBestDepot.getRow(), posCustomer, Double.POSITIVE_INFINITY);
			
			posCluster = findCluster(idDepot, listClusters);

			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();
				
				if(capacityDepot >= (requestCluster + requestCustomer)) 
				{
					requestCluster += requestCustomer;

					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					listCustomersToAssign.remove(posRDMCustomer);
					isNext = true;

					if(countTry != 0)
						countTry = 0;
				} 
				else 
				{
					countTry++;
					isNext = false;

					if(countTry >= listClusters.size())
					{
						countTry = 0;
						
						solution.getUnassignedItems().add(idCustomer);
						listCustomersToAssign.remove(posRDMCustomer);
						isNext = true;
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