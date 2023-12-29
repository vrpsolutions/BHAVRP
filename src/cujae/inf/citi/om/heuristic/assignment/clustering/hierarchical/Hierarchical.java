package cujae.inf.citi.om.heuristic.assignment.clustering.hierarchical;

import java.util.ArrayList;

import cujae.inf.citi.om.heuristic.assignment.Assignment;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.problem.input.InfoProblem;

public abstract class Hierarchical extends Assignment {

	protected boolean findDepotOfCluster(ArrayList<Cluster> clusters){
		int i = 0;
		boolean found = false;

		while((i < clusters.size()) && (!found))
		{
			int j = 0; 
			
			while((j < InfoProblem.getProblem().getDepots().size()) && (!found))
			{
				if(clusters.get(i).getIDCluster() == InfoProblem.getProblem().getDepots().get(j).getIDDepot())
					found = true;
				else 
					j++;
			}
			
			i++;
		}
		
		return found;
	}
}
