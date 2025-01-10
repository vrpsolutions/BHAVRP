package cujae.inf.ic.om.assignment.clustering.hierarchical;

import java.util.ArrayList;

import cujae.inf.ic.om.assignment.clustering.Clustering;

import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.Cluster;

public abstract class Hierarchical extends Clustering {

	protected boolean findDepotOfCluster(ArrayList<Cluster> clusters){
		int i = 0;
		boolean found = false;

		while((i < clusters.size()) && (!found))
		{
			int j = 0; 
			
			while((j < Problem.getProblem().getDepots().size()) && (!found))
			{
				if(clusters.get(i).getIDCluster() == Problem.getProblem().getDepots().get(j).getIDDepot())
					found = true;
				else 
					j++;
			}
			
			i++;
		}
		return found;
	}
	
	protected boolean isFullDepot(ArrayList<Cluster> clusters, double requestCluster, double capacityDepot, int currentCustomer){
		boolean isFull = true;

		double currentRequest = capacityDepot - requestCluster;

		if(currentRequest > 0)
		{
			int i = 0;

			while(((i < clusters.size()) && (i < currentCustomer)) && (isFull))
			{
				if(clusters.get(i).getRequestCluster() <= currentRequest)
					isFull = false;
				else
					i++;
			}
		}	
		return isFull;
	}
}