package cujae.inf.citi.om.heuristic.output;

import java.util.ArrayList;

public class Solution { 
	
	private ArrayList<Cluster> clusters;
	private ArrayList<Integer> unassignedItems;
	
	public Solution() {
		super();
		clusters = new ArrayList<Cluster>();
		unassignedItems = new ArrayList<Integer>();
		
		// TODO Auto-generated constructor stub
	}

	public Solution(ArrayList<Cluster> listClusters) {
		super();
		this.clusters = listClusters;
		unassignedItems = new ArrayList<Integer>();
	}

	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(ArrayList<Cluster> clusters) {
		this.clusters = clusters;
	}

	public ArrayList<Integer> getUnassignedItems() {
		return unassignedItems;
	}

	public void setUnassignedItems(ArrayList<Integer> unassignedItems) {
		this.unassignedItems = unassignedItems;
	}

	/*Método que devuelve true o false en dependencia de si existen clientes que no fueron asignados*/
	public boolean existUnassignedItems(){
		return unassignedItems.isEmpty()? true : false;				
	}
	
	/*Método encarargado de devolver cuantos clientes no asignados hay*/
	public int getTotalUnassignedItems(){
		return unassignedItems.size(); 		
	}	
	
	public int elementsClustering(){
		int totalElement = 0;
		
		for(int i = 0; i < clusters.size(); i++)
			totalElement += clusters.get(i).getItemsOfCluster().size();
		
		return totalElement;
	}
}