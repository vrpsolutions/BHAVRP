package cujae.inf.ic.om.heuristic.assignment;

import java.util.ArrayList;

import cujae.inf.ic.om.controller.utils.Tools;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Location;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

/*Clase que modela un algoritmo de asignación para problemas con múltiples depósitos*/
public abstract class Assignment {

	public abstract Solution toClustering();

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

	protected int getPosMaxValue(ArrayList<Double> list){
		int posMaxValue = -1;

		if((list != null) && (!list.isEmpty()))
		{
			posMaxValue = 0;
			Double maxValue = list.get(0);

			for(int i = 1; i < list.size(); i++)
			{
				if(list.get(i).doubleValue() > maxValue.doubleValue())
				{
					maxValue = list.get(i);
					posMaxValue = i;
				}
			}	
		}

		return posMaxValue;
	}

	protected int getPosMinValue(ArrayList<Double> list){
		int posMinValue = -1;

		if((list != null) && (!list.isEmpty()))
		{
			posMinValue = 0;
			Double minValue = list.get(0);

			for(int i = 1; i < list.size(); i++)
			{
				if(list.get(i).doubleValue() < minValue.doubleValue())
				{
					minValue = list.get(i);
					posMinValue = i;
				}
			}
		}
		return posMinValue;
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

	/*Método que determina si existen clientes que puedan ser asignado al depósito*/
	protected ArrayList<Integer> getCustomersOutDepot(ArrayList<Customer> customers, double requestCluster, double capacityDepot){
		ArrayList<Integer> customersOutDepot = new ArrayList<Integer>();

		double currentRequest = capacityDepot - requestCluster;

		if(currentRequest != 0)
		{
			for(int i = 0; i < customers.size(); i++)
			{
				if(customers.get(i).getRequestCustomer() > currentRequest)
					customersOutDepot.add(i);
			}
		}	

		return customersOutDepot;
	}

	/*Este método devuelve la posición del cluster (en la lista de clusters) al que debe asignarse el cliente que se está analizando, dado el identificador del elemento por el que se va a asignar dicho cliente*/
	protected int getPosCluster(int posCustomer, ArrayList<Cluster> clusters){
		int posCluster = -1;
		int idCustomer = Problem.getProblem().getListIDCustomers().get(posCustomer);
		int i = 0;
		int j;
		boolean found = false;

		while((i < clusters.size()) && (!found))
		{
			j = 0;

			if(!clusters.get(i).getItemsOfCluster().isEmpty())
			{
				while((j < clusters.get(i).getItemsOfCluster().size()) && (!found))
				{
					if(clusters.get(i).getItemsOfCluster().get(j) == idCustomer)
					{
						posCluster = i;
						found = true;
					}

					else
						j++;
				}
			}

			i++;
		}

		return posCluster;
	}

	protected Location recalculateTest(Cluster clusterOne, Cluster clusterTwo, ArrayList<Customer> customersToAssign) {
		Location locationOne = new Location();
		Location locationTwo = new Location();
		Location location = new Location();

		double aveAxisX = 0.0;
		double aveAxisY = 0.0;

		locationOne = customersToAssign.get(Problem.getProblem().findPosCustomer(customersToAssign, clusterOne.getIDCluster())).getLocationCustomer();
		locationTwo = customersToAssign.get(Problem.getProblem().findPosCustomer(customersToAssign, clusterTwo.getIDCluster())).getLocationCustomer();

		aveAxisX = locationOne.getAxisX() + locationTwo.getAxisX();
		aveAxisY = locationOne.getAxisY() + locationTwo.getAxisY();

		aveAxisX /= 2;
		aveAxisY /= 2;

		location.setAxisX(aveAxisX);
		location.setAxisY(aveAxisY);

		return location;
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
	
	protected void updateCustomerToAssign(ArrayList<Customer> customerToAssign, ArrayList<Integer> idElements){
		
		for(int i = 0; i < idElements.size(); i++)
		{
			boolean found = false;
			int j = 0;
			
			while ((!found) && j < customerToAssign.size())
			{
				if(customerToAssign.get(j).getIDCustomer() == idElements.get(i).intValue())
				{
					found = true;
					customerToAssign.remove(j);
				}
				else
					j++;
			}
		}
		
		System.out.println("CLIENTES A ASIGNAR");
		
		for(int i = 0; i < customerToAssign.size(); i++)
		{
			System.out.println("--------------------------------------------------");
			System.out.println("ID CLIENTE: " + customerToAssign.get(i).getIDCustomer());
			System.out.println("X: " + customerToAssign.get(i).getLocationCustomer().getAxisX());
			System.out.println("Y: " + customerToAssign.get(i).getLocationCustomer().getAxisY());
			System.out.println("DEMANDA: " + customerToAssign.get(i).getRequestCustomer());
		}
	}
}