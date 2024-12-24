package cujae.inf.ic.om.heuristic.assignment;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.output.solution.Cluster;

//public abstract class Assignment implements IAssignment {
public abstract class Assignment extends AssignmentTemplate {
	
	/**
	 * @param  int identificador del cluster
	 * @param  ArrayList<Cluster> listado de clusters 
	 * @return int posición del cluster en la clusters
	 * Busca la posición de un cluster en el listado de clusters
	 **/
	protected int findCluster(int idCluster, ArrayList<Cluster> clusters){
		int posCluster = -1;

		int i = 0;
		boolean found = false;

		while((i < clusters.size()) && (!found))
		{
			if(clusters.get(i).getIDCluster() == idCluster)
			{
				found = true;
				posCluster = i;
			}
			else 
				i++;
		}

		return posCluster;
	}

}
