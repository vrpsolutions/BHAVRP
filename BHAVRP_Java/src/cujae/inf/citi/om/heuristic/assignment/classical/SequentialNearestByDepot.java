package cujae.inf.citi.om.heuristic.assignment.classical;

import java.util.ArrayList;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;
import cujae.inf.citi.om.heuristic.assignment.classical.distance.ByDistance;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;

public class SequentialNearestByDepot extends ByDistance{

	public SequentialNearestByDepot() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();		
		ArrayList<Cluster> clusters = initializeClusters();

		ArrayList<Customer> customersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		ArrayList<Integer> idDepots = new ArrayList<Integer>(InfoProblem.getProblem().getListIDDepots());
		NumericMatrix costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		int totalCustomers = customersToAssign.size();
		int totalDepots = idDepots.size();

		RowCol rcBestCustomer = null;  
		int idDepot = -1;
		int posDepotMatrix = -1;
		int posDepot = 0;
		double capacityDepot = 0.0;
		int posCustomer = -1;
		int idCustomer = -1;
		double requestCustomer = 0.0;
		double requestCluster = 0.0;
		int posCluster = -1;
		boolean isFull = false;

		while((!customersToAssign.isEmpty()) && (!clusters.isEmpty()) && (!costMatrix.fullMatrix(Double.POSITIVE_INFINITY)))
		{	
			idDepot = idDepots.get(posDepot);
			capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(idDepot));
			posDepotMatrix = posDepot + totalCustomers;
			
			posCluster = findCluster(idDepot, clusters);

			if(posCluster != -1)
			{
				while(!isFull)
				{
					rcBestCustomer = costMatrix.indexLowerValue(posDepotMatrix, 0, posDepotMatrix, (totalCustomers - 1));

					posCustomer = rcBestCustomer.getCol();
					idCustomer = InfoProblem.getProblem().getCustomers().get(posCustomer).getIDCustomer();
					requestCustomer = InfoProblem.getProblem().getCustomers().get(posCustomer).getRequestCustomer();

					costMatrix.setItem(posDepotMatrix, posCustomer, Double.POSITIVE_INFINITY);

					requestCluster = clusters.get(posCluster).getRequestCluster();

					if(capacityDepot >= (requestCluster + requestCustomer)) 
					{
						requestCluster += requestCustomer;

						clusters.get(posCluster).setRequestCluster(requestCluster);
						clusters.get(posCluster).getItemsOfCluster().add(idCustomer);

						costMatrix.fillValue(totalCustomers, posCustomer, (totalCustomers + totalDepots - 1), posCustomer, Double.POSITIVE_INFINITY);

						customersToAssign.remove(InfoProblem.getProblem().findPosCustomer(customersToAssign, idCustomer));
						
						if(isFullDepot(customersToAssign, requestCluster, capacityDepot))
						{
							if(!(clusters.get(posCluster).getItemsOfCluster().isEmpty()))
								solution.getClusters().add(clusters.remove(posCluster));
							else
								clusters.remove(posCluster);

							isFull = true;
							posDepot++;
						}
					}								
				}
				
				isFull = false;
			}
		}	

		if(!customersToAssign.isEmpty())					
			for(int j = 0; j < customersToAssign.size(); j++)	
				solution.getUnassignedItems().add(customersToAssign.get(j).getIDCustomer());

		if(!clusters.isEmpty())
			for(int k = 0; k < clusters.size(); k++)
				if(!clusters.get(k).getItemsOfCluster().isEmpty())
					solution.getClusters().add(clusters.get(k));

		return solution;
	}
}
