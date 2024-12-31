package cujae.inf.ic.om.assignment.clustering.partitional;

import java.util.ArrayList;
import java.util.Random;

import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.assignment.clustering.SamplingType;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;
import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Fleet;
import cujae.inf.ic.om.problem.input.Location;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.solution.Cluster;

public abstract class ByMedoids extends Partitional {
	
	// generar particion secuencial y aleatoria
	protected ArrayList<ArrayList<Customer>> generatePartitions(int sampsize, SamplingType samplingType){
		ArrayList<ArrayList<Customer>> partitions = new ArrayList<ArrayList<Customer>>();
		ArrayList<Customer> partition = new ArrayList<Customer>();
		
		int totalCustomers = Problem.getProblem().getTotalCustomers();
		int totalPartitions = totalCustomers/sampsize;
		
		if(totalCustomers%sampsize != 0)
			totalPartitions += 1;
			
		System.out.println("TOTAL DE PARTICIONES: " + totalPartitions);
		System.out.println("---------------------------------------------------------------");
		
	
		switch(samplingType.ordinal())
		{
			case 0:
			{
				int j = 0;
				int posElement = -1;
				Random rdm = new Random();
				
				ArrayList<Customer> customers = new ArrayList<Customer>(Problem.getProblem().getCustomers());
				
				for(int i = 0; i < totalPartitions; i++)
				{
					while((j < (sampsize * (i + 1))) && (j < totalCustomers))
					{
						posElement = rdm.nextInt(customers.size());
						partition.add(customers.remove(posElement));
						
						j++;
					}
					
					System.out.println("PARTICIÓN " + (i + 1) + ": ");
					System.out.println("TOTAL DE ELEMENTOS DE LA PARTICIÓN " + (i + 1) + ": " + partition.size());
					for(int k = 0; k < partition.size(); k++)
						System.out.println("ELEMENTOS DE LA PARTICIÓN " + (i + 1) + ": " +  partition.get(k).getIDCustomer());
					System.out.println("---------------------------------------------------------------");
					
					partitions.add(partition);
					partition = new ArrayList<Customer>();
				}
				
				break;
			}
			case 1:
			{
				int j = 0;
				
				for(int i = 0; i < totalPartitions; i++)
				{
					while((j < (sampsize * (i + 1))) && (j < totalCustomers))
					{
						partition.add(Problem.getProblem().getCustomers().get(j));
						j++;
					}
					
					System.out.println("PARTICIÓN " + (i + 1) + ": ");
					System.out.println("TOTAL DE ELEMENTOS DE LA PARTICIÓN " + (i + 1) + ": " + partition.size());
					for(int k = 0; k < partition.size(); k++)
						System.out.println("ELEMENTOS DE LA PARTICIÓN " + (i + 1) + ": " +  partition.get(k).getIDCustomer());
					System.out.println("---------------------------------------------------------------");
					
					partitions.add(partition);
					partition = new ArrayList<Customer>();
				}
				
				break;
			}
		}
		
		return partitions;
	}
	
	protected ArrayList<Integer> generateElements(ArrayList<Customer> customers, DistanceType distanceType){
		ArrayList<Integer> idElements = new ArrayList<Integer>();
		
		int totalCustomers = customers.size();
		int totalDepots = Problem.getProblem().getTotalDepots();
		int counter = totalDepots;
		
		NumericMatrix costMatrix = initializeCostMatrix(customers, Problem.getProblem().getDepots(), distanceType);
		RowCol rcBestAll = new RowCol();
		int idElement = -1 ;
		
		for(int i = 0; i < counter; i++)
			idElements.add(-1);

		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);

		while(counter > 0)
		{
			rcBestAll = costMatrix.indexLowerValue(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1));

			System.out.println("ROW SELECCIONADA: " + rcBestAll.getRow());
			System.out.println("COL SELECCIONADA: " + rcBestAll.getCol());
			System.out.println("VALOR SELECCIONADO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));

			idElement = customers.get(rcBestAll.getCol()).getIDCustomer();	
			idElements.set((rcBestAll.getRow() - totalCustomers), idElement);	

			System.out.println("ELEMENTO: " + idElement); 
			System.out.println("LISTADO DE ELEMENTOS ACTUALIZADOS:" + idElements);

			costMatrix.fillValue(totalCustomers, rcBestAll.getCol(), (totalCustomers + totalDepots - 1), rcBestAll.getCol(), Double.POSITIVE_INFINITY);
			costMatrix.fillValue(rcBestAll.getRow(), 0, rcBestAll.getRow(), (totalCustomers + totalDepots - 1), Double.POSITIVE_INFINITY);
			counter--;
		}

	System.out.println("--------------------------------------------------");
	System.out.println("CENTROIDES/MEDOIDES INICIALES");
	System.out.println(idElements);
	System.out.println("--------------------------------------------------");

	return idElements;
}
	
	protected ArrayList<Integer> getIDMedoids(ArrayList<Depot> medoids){
		ArrayList<Integer> idMedoids = new ArrayList<Integer>();
		
		for(int i = 0; i < medoids.size(); i++)
			idMedoids.add(medoids.get(i).getIDDepot());
		
		System.out.println("--------------------------------------------------");
		System.out.println("ID MEDOIDES ACTUALES");
		System.out.println("--------------------------------------------------");
		System.out.println(idMedoids);
		
		return idMedoids;			
	}
	
	protected void updateClusters(ArrayList<Cluster> clusters, ArrayList<Integer> idElements) {
		
		for(int i = 0; i < clusters.size(); i++)
		{
			clusters.get(i).getItemsOfCluster().add(idElements.get(i));
			clusters.get(i).setRequestCluster(Problem.getProblem().getRequestByIDCustomer(idElements.get(i)));
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
	}
	
	protected boolean verifyMedoids(ArrayList<Depot> oldMedoids, ArrayList<Depot> currentMedoids) {
		boolean change = false;
		int i = 0;
		
		while((!change) && (i < currentMedoids.size()))
		{
			if((oldMedoids.get(i).getLocationDepot().getAxisX() != currentMedoids.get(i).getLocationDepot().getAxisX()) || (oldMedoids.get(i).getLocationDepot().getAxisY() != currentMedoids.get(i).getLocationDepot().getAxisY()))
				change = true;
			else
				i++;
		}
		
		System.out.println("change:  " + change);
		
		return change;
	}
	
	protected ArrayList<Depot> replicateDepots(ArrayList<Depot> depots){
		ArrayList<Depot> newDepots = new ArrayList<Depot>();
		
		System.out.println("--------------------------------------------------");
		System.out.println("MEDOIDES/CENTROIDES ACTUALES");
		
		for(int i = 0; i < depots.size(); i++)
		{
			Depot depot = new Depot();
			
			depot.setIDDepot(depots.get(i).getIDDepot());
			
			double axisX = 0.0; 
			double axisY = 0.0; 
			axisX = depots.get(i).getLocationDepot().getAxisX();
			axisY = depots.get(i).getLocationDepot().getAxisY();
			
			Location location = new Location();
			location.setAxisX(axisX);
			location.setAxisY(axisY);
			depot.setLocationDepot(location);
			
			ArrayList<Fleet> fleet = new ArrayList<Fleet>();
			fleet.addAll(Problem.getProblem().getDepots().get(i).getFleetDepot());
			depot.setFleetDepot(fleet);
			
			newDepots.add(depot);
			
			System.out.println("--------------------------------------------------");
			System.out.println("ID MEDOIDE/CENTROIDE: " + newDepots.get(i).getIDDepot());
			System.out.println("X: " + newDepots.get(i).getLocationDepot().getAxisX());
			System.out.println("Y: " + newDepots.get(i).getLocationDepot().getAxisY());
			System.out.println("CAPACIDAD DE VEHICULO: " + newDepots.get(i).getFleetDepot().get(0).getCapacityVehicle());
			System.out.println("CANTIDAD DE VEHICULOS: " + newDepots.get(i).getFleetDepot().get(0).getCountVehicles());
		}
		
		return newDepots;
	}

	protected double calculateDissimilarity(DistanceType distanceType, ArrayList<Cluster> clusters) {
		double currentDissimilarity = 0.0;
		NumericMatrix dissimilarityMatrix = initializeCostMatrix(Problem.getProblem().getCustomers(), Problem.getProblem().getDepots(), distanceType);
		
		int posFirstItem = -1;
		int posSecondItem = -1;
		int totalClusters = clusters.size(); 
		int totalItems = 0; 
		
		for(int i = 0; i < totalClusters; i++)
		{
			totalItems = clusters.get(i).getItemsOfCluster().size(); 
			
			for(int j = 0; j < totalItems; j++)
			{
				posFirstItem = Problem.getProblem().getPosElement(clusters.get(i).getItemsOfCluster().get(j));
				
				for(int k = (j + 1); k < totalItems; k++)
				{
					posSecondItem = Problem.getProblem().getPosElement(clusters.get(i).getItemsOfCluster().get(k));
					currentDissimilarity += dissimilarityMatrix.getItem(posFirstItem, posSecondItem);
				}
			}
		}
		
		currentDissimilarity /= totalClusters;
		
		System.out.println("COEFICIENTE DE DISIMILITUD ACTUAL: " + currentDissimilarity);	
		
		return currentDissimilarity;
	}
}