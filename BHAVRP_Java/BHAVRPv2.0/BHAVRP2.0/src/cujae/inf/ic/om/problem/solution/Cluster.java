package cujae.inf.ic.om.problem.solution;

import java.util.ArrayList;

public class Cluster {

	private int idCluster;
	private double requestCluster;
	private ArrayList<Integer> itemsOfCluster;
	
	public Cluster() {
		super();
		idCluster = -1;
		this.requestCluster = 0.0;
		this.itemsOfCluster = new ArrayList<Integer>();	
	}

	public Cluster(int idCluster, double requestCluster,
			ArrayList<Integer> itemsOfCluster) {
		super();
		this.idCluster = idCluster;
		this.requestCluster = requestCluster;
		this.itemsOfCluster = itemsOfCluster;
	}

	public int getIDCluster() {
		return idCluster;
	}

	public void setIDCluster(int idCluster) {
		this.idCluster = idCluster;
	}

	public ArrayList<Integer> getItemsOfCluster() {
		return itemsOfCluster;
	}

	public void setItemsOfCluster(ArrayList<Integer> itemsOfCluster) {
		this.itemsOfCluster = itemsOfCluster;
	}

	public double getRequestCluster() {
		return requestCluster;
	}

	public void setRequestCluster(double requestCluster) {
		this.requestCluster = requestCluster;
	}

	 public void cleanCluster() {
		 requestCluster = 0.0;	
		 itemsOfCluster.clear();	 
    }
}