package cujae.inf.ic.om.heuristic.assignment.clustering.partitional;

import java.util.ArrayList;
import java.util.Random;

import cujae.inf.ic.om.factory.DistanceType;

import cujae.inf.ic.om.heuristic.assignment.clustering.Clustering;
import cujae.inf.ic.om.heuristic.assignment.clustering.SeedType;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Location;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

public abstract class Partitional extends Clustering {
	
	//todos
	protected ArrayList<Depot> createCentroids(ArrayList<Integer> idElements){
		ArrayList<Depot> centroids = new ArrayList<Depot>();
		
		for(int i = 0; i < idElements.size(); i++)
		{
			Depot centroid = new Depot();
			
			centroid.setIDDepot(idElements.get(i));
			
			Location location = new Location();
			location.setAxisX(Problem.getProblem().getCustomerByIDCustomer(idElements.get(i).intValue()).getLocationCustomer().getAxisX());
			location.setAxisY(Problem.getProblem().getCustomerByIDCustomer(idElements.get(i).intValue()).getLocationCustomer().getAxisY());
			centroid.setLocationDepot(location);
			
			centroids.add(centroid);
		}
		
		return centroids;
	}
	
	//todos
	protected void cleanClusters(ArrayList<Cluster> clusters) {
		for (Cluster c : clusters) 
			c.cleanCluster(); 
		
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
	
	//todos
	protected ArrayList<Cluster> stepAssignment(ArrayList<Cluster> clusters, ArrayList<Customer> customerToAssign, NumericMatrix costMatrix){
		int idDepot = -1;
		int posDepot = -1;				
		double capacityDepot = 0.0;			

		int idCustomer = -1;
		int posCustomer = -1;
		double requestCustomer = 0.0;

		int posCluster = -1;
		double requestCluster = 0.0;

		RowCol rcBestAll = new RowCol();
		
		ArrayList<Customer> listCustomers = new ArrayList<Customer>(customerToAssign);
		int totalCustomers = customerToAssign.size();
		int totalDepots = clusters.size();
		
		System.out.println("--------------------------------------------------------------------");
		System.out.println("PROCESO DE ASIGNACIÓN");
		
		while((!customerToAssign.isEmpty()) && (!costMatrix.fullMatrix(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1), Double.POSITIVE_INFINITY))) 
		{
			rcBestAll = costMatrix.indexLowerValue(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1));

			posCustomer = rcBestAll.getCol();
			idCustomer = listCustomers.get(posCustomer).getIDCustomer();				
			requestCustomer = listCustomers.get(posCustomer).getRequestCustomer();
		
			System.out.println("-----------------------------------------------------------");
			System.out.println("BestAllCol: " + rcBestAll.getCol());
			System.out.println("BestAllRow: " + rcBestAll.getRow());
			
			System.out.println("ID CLIENTE SELECCIONADO: " + idCustomer);
			System.out.println("POSICIÓN DEL CLIENTE SELECCIONADO: " + posCustomer);
			System.out.println("DEMANDA DEL CLIENTE SELECCIONADO: " + requestCustomer);
			
			posDepot = (rcBestAll.getRow() - totalCustomers); 
			idDepot = Problem.getProblem().getDepots().get(posDepot).getIDDepot();
			capacityDepot = Problem.getProblem().getTotalCapacityByDepot(idDepot);

			System.out.println("ID DEPOSITO SELECCIONADO: " + idDepot);
			System.out.println("POSICIÓN DEL DEPOSITO SELECCIONADO: " + posDepot);
			System.out.println("CAPACIDAD TOTAL DEL DEPOSITO SELECCIONADO: " + capacityDepot);				
		
			posCluster = findCluster(idDepot, clusters);	
			
			System.out.println("POSICION DEL CLUSTER: " + posCluster);
			
			if(posCluster != -1)
			{
				requestCluster = clusters.get(posCluster).getRequestCluster();

				System.out.println("DEMANDA DEL CLUSTER: " + requestCluster);

				if(capacityDepot >= (requestCluster + requestCustomer)) 
				{
					requestCluster += requestCustomer;

					clusters.get(posCluster).setRequestCluster(requestCluster);
					clusters.get(posCluster).getItemsOfCluster().add(idCustomer);		

					System.out.println("DEMANDA DEL CLUSTER ACTUALIZADA: " + requestCluster);
					System.out.println("ELEMENTOS DEL CLUSTER: " + clusters.get(posCluster).getItemsOfCluster());
					
					costMatrix.fillValue(totalCustomers, posCustomer, (totalCustomers + totalDepots - 1), posCustomer, Double.POSITIVE_INFINITY);
					customerToAssign.remove(Problem.getProblem().findPosCustomer(customerToAssign, idCustomer));
					
					System.out.println("CANTIDAD DE CLIENTES SIN ASIGNAR: " + customerToAssign.size());
				}
				else
					costMatrix.setItem(rcBestAll.getRow(), posCustomer, Double.POSITIVE_INFINITY);
				
				if(isFullDepot(customerToAssign, requestCluster, capacityDepot))
				{
					System.out.println("DEPOSITO LLENO");
					
					costMatrix.fillValue(rcBestAll.getRow(), 0, rcBestAll.getRow(), (totalCustomers + totalDepots - 1), Double.POSITIVE_INFINITY);
				}
			}
		}
		
		System.out.println("--------------------------------------------------");
		System.out.println("LISTA DE CLUSTERS");
		for(int i = 0; i < clusters.size(); i++)
		{
			System.out.println("ID CLUSTER: " + clusters.get(i).getIDCluster());
			System.out.println("DEMANDA DEL CLUSTER: " + clusters.get(i).getRequestCluster());
			System.out.println("CANTIDAD DE ELEMENTOS EN EL CLUSTER: " + clusters.get(i).getItemsOfCluster().size());
			System.out.println("ELEMENTOS DEL CLUSTER: " + clusters.get(i).getItemsOfCluster());
		}
		System.out.println("--------------------------------------------------");
		
		return clusters;
	}
	
	//todos
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
	
	//k-means y pam
	protected ArrayList<Integer> generateElements(SeedType seedType, DistanceType distanceType){
		ArrayList<Integer> idElements = new ArrayList<Integer>();
		
		int totalCustomers = Problem.getProblem().getTotalCustomers();
		int totalDepots = Problem.getProblem().getTotalDepots();
		int counter = totalDepots;
		
		NumericMatrix costMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		
		RowCol rcBestAll = new RowCol();
		int idElement = -1 ;
		
		switch(seedType.ordinal()) 
		{
			case 0:
			{
				for(int i = 0; i < counter; i++)
					idElements.add(-1);
				
				System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);
			
				while(counter > 0)
				{
					rcBestAll = costMatrix.indexBiggerValue(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1));
					
					System.out.println("ROW SELECCIONADA: " + rcBestAll.getRow());
					System.out.println("COL SELECCIONADA: " + rcBestAll.getCol());
					System.out.println("VALOR SELECCIONADO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));
					
					idElement = Problem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();	
					idElements.set((rcBestAll.getRow() - totalCustomers), idElement);	
					
					System.out.println("ELEMENTO: " + idElement); 
					System.out.println("LISTADO DE ELEMENTOS ACTUALIZADOS:" + idElements);
					
					costMatrix.fillValue(totalCustomers, rcBestAll.getCol(), (totalCustomers + totalDepots - 1), rcBestAll.getCol(), Double.NEGATIVE_INFINITY);
					costMatrix.fillValue(rcBestAll.getRow(), 0, rcBestAll.getRow(), (totalCustomers + totalDepots - 1), Double.NEGATIVE_INFINITY);
					counter--;
				}
				
				break;
			}
			case 1:
			{
				for(int i = 0; i < counter; i++)
					idElements.add(-1);
				
				System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);
				
				while(counter > 0)
				{
					rcBestAll = costMatrix.indexLowerValue(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1));
					
					System.out.println("ROW SELECCIONADA: " + rcBestAll.getRow());
					System.out.println("COL SELECCIONADA: " + rcBestAll.getCol());
					System.out.println("VALOR SELECCIONADO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));
					
					
					idElement = Problem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();	
					idElements.set((rcBestAll.getRow() - totalCustomers), idElement);	
					
					System.out.println("ELEMENTO: " + idElement); 
					System.out.println("LISTADO DE ELEMENTOS ACTUALIZADOS:" + idElements);
					
					costMatrix.fillValue(totalCustomers, rcBestAll.getCol(), (totalCustomers + totalDepots - 1), rcBestAll.getCol(), Double.POSITIVE_INFINITY);
					costMatrix.fillValue(rcBestAll.getRow(), 0, rcBestAll.getRow(), (totalCustomers + totalDepots - 1), Double.POSITIVE_INFINITY);
					counter--;
				}
				
				break;
			}
			case 2:
			{
				Random rdm = new Random();
				
				while(counter > 0)
				{
					idElement = rdm.nextInt(totalCustomers); // DUDA + 1
					idElements.add(Problem.getProblem().getCustomers().get(idElement).getIDCustomer());
					
					System.out.println("ELEMENTO: " + idElement); 
					System.out.println("LISTADO DE ELEMENTOS ACTUALIZADOS:" + idElements);
					
					counter--;
				}
				
				break;
			}
		}

		System.out.println("--------------------------------------------------");
		System.out.println("CENTROIDES/MEDOIDES INICIALES");
		System.out.println(idElements);
		System.out.println("--------------------------------------------------");
		
		return idElements;
	}
		
	//----------------------------------------------------------------------------------------------//
	
	/*
	protected ArrayList<Integer> generateElementsXXX(DistanceType distanceType){
		
		ArrayList<Integer> idElements = new ArrayList<Integer>();
		
		int totalCustomers = Problem.getProblem().getTotalCustomers();
		int totalDepots = Problem.getProblem().getTotalDepots();
		int counter = totalDepots;

		NumericMatrix costMatrix = null;
		RowCol rcBestAll = new RowCol();
		int posElement = -1 ;
		
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = Problem.getProblem().fillCostMatrixXXX(Problem.getProblem().getCustomers(), Problem.getProblem().getDepots(), distanceType);
				} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
	
				break;
			}
			case 4:
			{
				//costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix()); // NO FUNcIONA
	
				break;
			}
		}
		
		
		Random rdm = new Random();
		posElement = rdm.nextInt(totalCustomers);
		idElements.add(Problem.getProblem().getCustomers().get(posElement).getIDCustomer());
		
		System.out.println("POS_ELEMENTO: " + posElement); 
		System.out.println("LISTADO DE ELEMENTOS ACTUALIZADOS:" + idElements);
		
		while(counter > 1)
		{
			rcBestAll = costMatrix.indexBiggerValue(posElement, 0, posElement, (totalCustomers - 1));
			
			System.out.println("ROW SELECCIONADA: " + rcBestAll.getRow());
			System.out.println("COL SELECCIONADA: " + rcBestAll.getCol());
			System.out.println("VALOR SELECCIONADO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));
	
			idElements.add(Problem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer());	
			posElement = rcBestAll.getCol(); 
					
			System.out.println("POS_ELEMENTO: " + posElement); 
			System.out.println("LISTADO DE ELEMENTOS ACTUALIZADOS:" + idElements);

			costMatrix.fillValue(0, posElement, (totalCustomers - 1), posElement, Double.NEGATIVE_INFINITY); // no es necesario
			counter--;
		}
		
		
		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);
	
		return sortedElements(idElements, distanceType);
	}
	 */
	/*
	protected ArrayList<Integer> generateElementsXX(DistanceType distanceType){
		
		ArrayList<Integer> listCentroids = new ArrayList<Integer>();
		ArrayList<Customer> listCustomers = new ArrayList<Customer>(Problem.getProblem().getCustomers());

		int totalCustomers = listCustomers.size();
		int counter = Problem.getProblem().getTotalDepots();
		int idElement = -1;

		Random rdm = new Random();
		int posCentroid = rdm.nextInt(totalCustomers);
		
		idElement = Problem.getProblem().getCustomers().get(posCentroid).getIDCustomer();
		listCentroids.add(idElement);
		listCustomers.remove(posCentroid);

		counter--;
		
		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + listCentroids);

		NumericMatrix costMatrix = null;
	
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = Problem.getProblem().fillCostMatrixXXX(Problem.getProblem().getCustomers(), Problem.getProblem().getDepots(), distanceType);
				} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
				break;
			}
			case 4:
			{
				costMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
	
				break;
			}
		}

		RowCol rcBestAll = new RowCol();
		rcBestAll = costMatrix.indexBiggerValue(posCentroid, 0, posCentroid, (totalCustomers - 1));
		
		idElement = Problem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();
		listCentroids.add(idElement);
		listCustomers.remove(Problem.getProblem().getPosElement(idElement, listCustomers));
		
		counter--;
		
		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + listCentroids);
		
		ArrayList<Double> maxDistances = new ArrayList<Double>();
		int posCustomer = -1;

		while(counter > 0)
		{
			double maxDistance = -1;
			double currentDistance = -1;
			
			for(int i = 0; i < listCustomers.size(); i++)
			{
				posCustomer = Problem.getProblem().getPosElement(listCustomers.get(i).getIDCustomer());
				
				for(int j = 0; j < listCentroids.size(); j++)
				{
					posCentroid = Problem.getProblem().getPosElement(listCentroids.get(j).intValue());
					currentDistance = costMatrix.getItem(posCentroid, posCustomer);

					if(j == 0)
						maxDistance = currentDistance;
					else
						if(maxDistance < currentDistance)
							maxDistance = currentDistance;
				}
				
				maxDistances.add(maxDistance);
			}

			double minDistance = maxDistances.get(0);
			posCentroid = 0;
			
			for(int k = 1; k < maxDistances.size(); k++)
			{
				if(maxDistances.get(k) < minDistance)
				{
					minDistance = maxDistances.get(k);
					posCentroid = k;
				}
			}

			idElement = listCustomers.get(posCentroid).getIDCustomer();
			listCentroids.add(idElement);

			listCustomers.remove(posCentroid);
			counter--;

			System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + listCentroids);
		}

		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + listCentroids);

		return sortedElements(listCentroids, distanceType);
		}
	 */
	/*
	protected ArrayList<Integer> generateElements(ArrayList<Integer> pCustomers, int sampsize, DistanceType distanceType){

		ArrayList<Integer> idElements = new ArrayList<Integer>();

		int totalCustomers = Problem.getProblem().getTotalCustomers();
		int totalDepots = Problem.getProblem().getTotalDepots();
		int counter = totalDepots;

		//Generar matriz de costo con las distancias
		NumericMatrix costMatrix = null;
		RowCol rcBestAll = new RowCol();
		int idElement = -1;

		switch(distanceType.ordinal())
		{
		case 0: case 1: case 2: case 3:
		{
			/*	try {
						costMatrix = InfoProblem.getProblem().fillCostMatrix(generatePartitions(sampsize), InfoProblem.getProblem().getDepots(), distanceType);
					} 
					catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException 
							| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}*/
/*
			break;
		}

		case 4:
		{
			costMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
			break;
		}
		}

		//Asignar elementos a los medoides
		for(int i = 0; i < counter; i++)
			idElements.add(-1);

		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);

		while(counter > 0)
		{
			rcBestAll = costMatrix.indexLowerValue(totalCustomers, 0, (totalCustomers + totalDepots - 1), (totalCustomers - 1));

			System.out.println("ROW SELECCIONADA: " + rcBestAll.getRow());
			System.out.println("COL SELECCIONADA: " + rcBestAll.getCol());
			System.out.println("VALOR SELECCIONADO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));


			idElement = Problem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();	
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
	*/
	/*
	protected double calculateCost(ArrayList<Cluster> clusters, NumericMatrix costMatrix ) {
		double cost = 0.0;

		int idCustomer =  -1;
		int posCustomer = -1;
		int posDepot = -1;
		ArrayList<Integer> listIDCustomers;

		for(int i = 0; i < clusters.size(); i++) 
		{
			listIDCustomers = clusters.get(i).getItemsOfCluster();
			posDepot = Problem.getProblem().getPosElement(clusters.get(i).getIDCluster());
			
			System.out.println("CLIENTES: " + listIDCustomers);
			System.out.println("POSICIÓN DEPOSITO: " + posDepot);
			
			for(int j = 0; j < listIDCustomers.size(); j++) 
			{	
				idCustomer =  listIDCustomers.get(j);
				posCustomer = Problem.getProblem().getPosElement(idCustomer);

				cost += costMatrix.getItem(posDepot, posCustomer);	
				
				System.out.println("ID CLIENTE: " + idCustomer);
				System.out.println("POSICIÓN CLIENTE: " + posCustomer);
				System.out.println("COSTO: " + costMatrix.getItem(posDepot, posCustomer));
				System.out.println("COSTO ACUMULADOS: " + cost);	
			}
		}

		return cost;
	}
	*/
}