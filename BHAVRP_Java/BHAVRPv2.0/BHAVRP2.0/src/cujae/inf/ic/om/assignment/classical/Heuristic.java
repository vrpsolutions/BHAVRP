package cujae.inf.ic.om.assignment.classical;

import java.util.ArrayList;

import cujae.inf.ic.om.assignment.Assignment;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.solution.Cluster;

public abstract class Heuristic extends Assignment {
	
	/**
	 * @param  void
	 * @return ArrayList<Cluster> listado de clusters
	 * Inicializa los clusters de la solución con los identificadores de los depósitos
	 **/
	protected ArrayList<Cluster> initializeClusters(){
		ArrayList<Cluster> listClusters = new ArrayList<Cluster>();

		Cluster cluster;
		ArrayList<Integer> listIDItems;

		int totalClusters = Problem.getProblem().getDepots().size();

		for(int i = 0; i < totalClusters; i++)
		{
			listIDItems = new ArrayList<Integer>();
			cluster = new Cluster(Problem.getProblem().getListIDDepots().get(i).intValue(), 0.0, listIDItems); 
			listClusters.add(cluster);
		}
		
		System.out.println("--------------------------------------------------");
		System.out.println("LISTA DE CLUSTERS");
		for(int i = 0; i < listClusters.size(); i++)
		{
			System.out.println("ID CLUSTER: " + listClusters.get(i).getIDCluster());
			System.out.println("DEMANDA DEL CLUSTER: " + listClusters.get(i).getRequestCluster());
			System.out.println("ELEMENTOS DEL CLUSTER: " + listClusters.get(i).getItemsOfCluster());
		}
		System.out.println("--------------------------------------------------");

		return listClusters;
	}

}
