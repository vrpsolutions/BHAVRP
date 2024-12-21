package cujae.inf.ic.om.heuristic.assignment.clustering.hierarchical;

import java.util.ArrayList;

import cujae.inf.ic.om.heuristic.assignment.clustering.Clustering;

import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.solution.Cluster;

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
}