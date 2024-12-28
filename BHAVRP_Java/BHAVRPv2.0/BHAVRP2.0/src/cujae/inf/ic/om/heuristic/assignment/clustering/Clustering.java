package cujae.inf.ic.om.heuristic.assignment.clustering;

import java.util.ArrayList;

import cujae.inf.ic.om.controller.tools.Tools;
import cujae.inf.ic.om.heuristic.assignment.Assignment;
import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Location;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.solution.Cluster;

public abstract class Clustering extends Assignment {
	
	/**
	 * @param  ArrayList<Integer> listado de identificadores
	 * @return ArrayList<Cluster> listado de clusteres
	 * Inicializa los clusters de la solución con los identificadores de listIDElements (clientes y depósitos)
	 **/
	protected ArrayList<Cluster> initializeClusters(ArrayList<Integer> listIDElements){
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();

		Cluster cluster;
		ArrayList<Integer> listIDCustomers;

		int totalElements = listIDElements.size();

		if(totalElements == Problem.getProblem().getDepots().size())
		{
			for(int i = 0; i < totalElements; i++)
			{
				listIDCustomers = new ArrayList<Integer>();
				listIDCustomers.add(listIDElements.get(i).intValue());
				cluster = new Cluster(Problem.getProblem().getDepots().get(i).getIDDepot(), Problem.getProblem().getRequestByIDCustomer(listIDElements.get(i).intValue()), listIDCustomers);
				//cluster = new Cluster(listIDElements.get(i).intValue(), InfoProblem.getProblem().getRequestByIDCustomer(listIDElements.get(i).intValue()), listIDCustomers);
				clusters.add(cluster);
			}
		}
		else // UPGMC
		{
			for(int i = 0; i < totalElements; i++)
			{
				listIDCustomers = new ArrayList<Integer>();

				if(Problem.getProblem().findPosDepot(Problem.getProblem().getDepots(), listIDElements.get(i).intValue()) == -1)
				{
					listIDCustomers.add(listIDElements.get(i).intValue());
					cluster = new Cluster(listIDElements.get(i).intValue(), Problem.getProblem().getRequestByIDCustomer(listIDElements.get(i).intValue()), listIDCustomers);
				}
				else
					cluster = new Cluster(listIDElements.get(i).intValue(), 0.0, listIDCustomers);	// en upgmc no lo agrega al cliente en el cluster

				clusters.add(cluster);	
			}
		}
		
		System.out.println("--------------------------------------------------");
		System.out.println("LISTA DE CLUSTERS");
		for(int i = 0; i < clusters.size(); i++)
		{
			System.out.println("ID CLUSTER: " + clusters.get(i).getIDCluster());
			System.out.println("DEMANDA DEL CLUSTER: " + clusters.get(i).getRequestCluster());
			System.out.println("ELEMENTOS DEL CLUSTER: " + clusters.get(i).getItemsOfCluster());
		}
		System.out.println("--------------------------------------------------");

		return clusters;
	}
	
	/**
	 * @param  ArrayList<Customer>
	 * @param  double demanda cubierta del cluster
	 * @param  double capacidad del depósito
	 * @return boolean
	 * Determina si existen clientes que puedan ser asignados al depósito a partir de su demanda
	 */
	protected boolean isFullDepot(ArrayList<Customer> customers, double requestCluster, double capacityDepot){
		boolean isFull = true;

		double currentRequest = capacityDepot - requestCluster;

		if(currentRequest > 0)
		{
			int i = 0;

			while((i < customers.size()) && (isFull))
			{
				if(customers.get(i).getRequestCustomer() <= currentRequest)
					isFull = false;
				else
					i++;
			}
		}	

		return isFull;
		
		// cuando no quedan cliente lo saca como lleno ver si comviene en todos los casos
	}

	protected Location recalculateCentroid(Cluster cluster) {
		double aveAxisX = 0.0;
		double aveAxisY = 0.0;

		int countCustomers = cluster.getItemsOfCluster().size();

		for(int i = 0; i < countCustomers; i++) 
		{	
			Location location = new Location();
			location = Problem.getProblem().getLocationByIDCustomer(cluster.getItemsOfCluster().get(i)); 

			aveAxisX += location.getAxisX();
			aveAxisY += location.getAxisY();
		}

		aveAxisX = (aveAxisX / countCustomers);
		aveAxisY = (aveAxisY / countCustomers);

		Location locationCentroid = new Location();
		locationCentroid.setAxisX(Tools.truncateDouble(aveAxisX, 6));
		locationCentroid.setAxisY(Tools.truncateDouble(aveAxisY, 6));

		return locationCentroid;
	}
}
