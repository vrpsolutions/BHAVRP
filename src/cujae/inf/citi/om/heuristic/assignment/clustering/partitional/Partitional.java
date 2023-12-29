package cujae.inf.citi.om.heuristic.assignment.clustering.partitional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;

import cujae.inf.citi.om.factory.interfaces.DistanceType;
import cujae.inf.citi.om.heuristic.assignment.Assignment;
import cujae.inf.citi.om.heuristic.assignment.clustering.SamplingType;
import cujae.inf.citi.om.heuristic.assignment.clustering.SeedType;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.Depot;
import cujae.inf.citi.om.problem.input.Fleet;
import cujae.inf.citi.om.problem.input.InfoProblem;
import cujae.inf.citi.om.problem.input.Location;

public abstract class Partitional extends Assignment{

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
	
	protected void updateClusters(ArrayList<Cluster> clusters, ArrayList<Integer> idElements) {
		
		for(int i = 0; i < clusters.size(); i++)
		{
			clusters.get(i).getItemsOfCluster().add(idElements.get(i));
			clusters.get(i).setRequestCluster(InfoProblem.getProblem().getRequestByIDCustomer(idElements.get(i)));
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
	
	protected ArrayList<Integer> generateElements(SeedType seedType, DistanceType distanceType){
		ArrayList<Integer> idElements = new ArrayList<Integer>();
		
		int totalCustomers = InfoProblem.getProblem().getTotalCustomers();
		int totalDepots = InfoProblem.getProblem().getTotalDepots();
		int counter = totalDepots;
		
		NumericMatrix costMatrix = null;
		RowCol rcBestAll = new RowCol();
		int idElement = -1 ;
		
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = InfoProblem.getProblem().fillCostMatrix(InfoProblem.getProblem().getCustomers(), InfoProblem.getProblem().getDepots(), distanceType);
				} catch (IllegalArgumentException | SecurityException
						| ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				break;
			}
			case 4:
			{
				costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
	
				break;
			}
		}
		
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
					
					idElement = InfoProblem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();	
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
					
					
					idElement = InfoProblem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();	
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
					idElement = rdm.nextInt(totalCustomers) + 1;
					idElements.add(idElement);
					
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

	protected ArrayList<Integer> generateElements(ArrayList<Customer> customers, DistanceType distanceType){
		ArrayList<Integer> idElements = new ArrayList<Integer>();
		
		int totalCustomers = customers.size();
		int totalDepots = InfoProblem.getProblem().getTotalDepots();
		int counter = totalDepots;
		
		NumericMatrix costMatrix = null;
		RowCol rcBestAll = new RowCol();
		int idElement = -1 ;
		
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = InfoProblem.getProblem().fillCostMatrix(customers, InfoProblem.getProblem().getDepots(), distanceType);
				} catch (IllegalArgumentException | SecurityException
						| ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				break;
			}
			case 4:
			{
				//costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
	
				break;
			}
		}

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
	
	protected ArrayList<Integer> generateElements(DistanceType distanceType){
		
		ArrayList<Integer> idElements = new ArrayList<Integer>();
		
		int totalCustomers = InfoProblem.getProblem().getTotalCustomers();
		int totalDepots = InfoProblem.getProblem().getTotalDepots();
		int counter = totalDepots;

		NumericMatrix costMatrix = null;
		RowCol rcBestAll = new RowCol();
		int idElement = -1 ;
			
		Depot depot = new Depot();
		depot.setIDDepot(-1);
		depot.setLocationDepot(calculateMeanCoordinate());
		
		ArrayList<Depot> listDepot = new ArrayList<Depot>();
		listDepot.add(depot);
		
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = InfoProblem.getProblem().fillCostMatrix(InfoProblem.getProblem().getCustomers(), listDepot, distanceType);
				} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				break;
			}
			case 4:
			{
				//costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix()); // NO FUNIONA
	
				break;
			}
		}
		
		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);

		while(counter > 0)
		{
			rcBestAll = costMatrix.indexBiggerValue(totalCustomers, 0, totalCustomers, (totalCustomers - 1));

			System.out.println("ROW SELECCIONADA: " + rcBestAll.getRow());
			System.out.println("COL SELECCIONADA: " + rcBestAll.getCol());
			System.out.println("VALOR SELECCIONADO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));

			idElement = InfoProblem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();	
			idElements.add(idElement);	

			System.out.println("ELEMENTO: " + idElement); 
			System.out.println("LISTADO DE ELEMENTOS ACTUALIZADOS:" + idElements);

			costMatrix.setItem(rcBestAll.getRow(), rcBestAll.getCol(), Double.NEGATIVE_INFINITY);
			counter--;
		}
		
		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);
	
		return sortedElements(idElements, distanceType);
	}

	protected boolean verifyCentroids(ArrayList<Cluster> clusters, ArrayList<Depot> centroids, DistanceType distanceType) {
		boolean change = false;
		Location dummyDepot;

		System.out.println("change: " + change);
		
		for(int i = 0; i < clusters.size(); i++) 
		{
			if(!clusters.get(i).getItemsOfCluster().isEmpty())
				dummyDepot = recalculateCentroid(clusters.get(i));
			else
				dummyDepot = centroids.get(i).getLocationDepot(); 
			
			System.out.println("------------------------------------------------------------------");
			System.out.println("DUMMY_DEPOT" + i + " X: " + dummyDepot.getAxisX());
			System.out.println("DUMMY_DEPOT" + i + " Y: " + dummyDepot.getAxisY());
	
			System.out.println("CENTROIDE" + i + " X: " + centroids.get(i).getLocationDepot().getAxisX());
			System.out.println("CENTROIDE" + i + " Y: " + centroids.get(i).getLocationDepot().getAxisY());
			
			if((centroids.get(i).getLocationDepot().getAxisX() != dummyDepot.getAxisX()) || (centroids.get(i).getLocationDepot().getAxisY() != dummyDepot.getAxisY())) 
			{
				change = true;
				
				centroids.get(i).setIDDepot(-1);
				
				Location location = new Location();
				location.setAxisX(dummyDepot.getAxisX());
				location.setAxisY(dummyDepot.getAxisY());
				centroids.get(i).setLocationDepot(location);	
				
				System.out.println("change: " + change);
				System.out.println("CENTROIDE" + i + " X: " + centroids.get(i).getLocationDepot().getAxisX());
				System.out.println("CENTROIDE" + i + " Y: " + centroids.get(i).getLocationDepot().getAxisY());
			}
			else
			{
				System.out.println("CENTROIDE" + i + " X: " + centroids.get(i).getLocationDepot().getAxisX());
				System.out.println("CENTROIDE" + i + " Y: " + centroids.get(i).getLocationDepot().getAxisY());
			}
		}	

		if(change)
			updateCentroids(clusters, centroids, distanceType);

		System.out.println("CAMBIO LOS CENTROIDES: " + change);
		
		return change;
	}

	protected void updateCentroids(ArrayList<Cluster> clusters, ArrayList<Depot> centroids, DistanceType distanceType) {		
		NumericMatrix costMatrix = new NumericMatrix();
		try {
			costMatrix = InfoProblem.getProblem().calculateCostMatrix(centroids, InfoProblem.getProblem().getDepots(), distanceType);
		} catch (IllegalArgumentException | SecurityException
				| ClassNotFoundException | InstantiationException
				| IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Depot> tempCentroids = new ArrayList<Depot>(centroids);
		
		int totalCentroids = centroids.size();
		RowCol rcBestAll = new RowCol();
		int posCentroid = -1;
		int posDepot = -1;

		System.out.println("-------------------------------------" );
		for(int i = 0; i < centroids.size(); i++)
		{
			System.out.println("CENTROIDE ID: " + centroids.get(i).getIDDepot());
			System.out.println("CENTROIDE X: " + centroids.get(i).getLocationDepot().getAxisX());
			System.out.println("CENTROIDE Y: " + centroids.get(i).getLocationDepot().getAxisY());		
		}
		
		for(int i = 0; i < costMatrix.getRowCount(); i++)
		{
			for(int j = 0; j < costMatrix.getColCount(); j++){
				System.out.println("Row: " + i + " Col: " + j + " VALUE: " + costMatrix.getItem(i, j));
			}
			System.out.println("---------------------------------------------");
		}		
		
		while(!costMatrix.fullMatrix(0, 0, (totalCentroids  - 1), (totalCentroids - 1), Double.POSITIVE_INFINITY))
		{
			rcBestAll = costMatrix.indexLowerValue();

			System.out.println("BestAllRow: " + rcBestAll.getRow());
			System.out.println("BestAllCol: " + rcBestAll.getCol());
			System.out.println("COSTO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));
			
			posCentroid = rcBestAll.getRow();
			posDepot = rcBestAll.getCol();
			
			System.out.println("POSICIÓN DEL CENTROIDE: " + posCentroid);
			System.out.println("POSICIÓN DEL DEPOSITO: " + posDepot);

			if(posCentroid != posDepot)
			{
				Depot depot = new Depot();

				depot.setIDDepot(tempCentroids.get(posCentroid).getIDDepot());
				System.out.println("ID CENTROIDE: " + tempCentroids.get(posCentroid).getIDDepot());
				
				double axisX = 0.0; 
				double axisY = 0.0; 
				axisX = tempCentroids.get(posCentroid).getLocationDepot().getAxisX();
				axisY = tempCentroids.get(posCentroid).getLocationDepot().getAxisY();
				
				Location location = new Location();
				location.setAxisX(axisX);
				location.setAxisY(axisY);
				depot.setLocationDepot(location);
				
				ArrayList<Fleet> fleet = new ArrayList<Fleet>();
				fleet.addAll(tempCentroids.get(posCentroid).getFleetDepot());
				depot.setFleetDepot(fleet);
			
				centroids.set(posDepot, depot);
			}

			costMatrix.fillValue(0, posDepot, (totalCentroids - 1), posDepot, Double.POSITIVE_INFINITY);
			costMatrix.fillValue(posCentroid, 0, posCentroid, (totalCentroids - 1), Double.POSITIVE_INFINITY);
			
			for(int i = 0; i < costMatrix.getRowCount(); i++)
			{
				for(int j = 0; j < costMatrix.getColCount(); j++){
					System.out.println("Row: " + i + " Col: " + j + " VALUE: " + costMatrix.getItem(i, j));
				}
				System.out.println("---------------------------------------------");
			}		
			
		}
	}

	protected double calculateCost(ArrayList<Cluster> clusters, NumericMatrix costMatrix ) {
		double cost = 0.0;

		int idCustomer =  -1;
		int posCustomer = -1;
		int posDepot = -1;
		ArrayList<Integer> listIDCustomers;

		for(int i = 0; i < clusters.size(); i++) 
		{
			listIDCustomers = clusters.get(i).getItemsOfCluster();
			posDepot = InfoProblem.getProblem().getPosElement(clusters.get(i).getIDCluster());
			
			System.out.println("CLIENTES: " + listIDCustomers);
			System.out.println("POSICIÓN DEPOSITO: " + posDepot);
			
			for(int j = 0; j < listIDCustomers.size(); j++) 
			{	
				idCustomer =  listIDCustomers.get(j);
				posCustomer = InfoProblem.getProblem().getPosElement(idCustomer);

				cost += costMatrix.getItem(posDepot, posCustomer);	
				
				System.out.println("ID CLIENTE: " + idCustomer);
				System.out.println("POSICIÓN CLIENTE: " + posCustomer);
				System.out.println("COSTO: " + costMatrix.getItem(posDepot, posCustomer));
				System.out.println("COSTO ACUMULADOS: " + cost);	
			}
		}

		return cost;
	}
	
	protected double calculateCost(ArrayList<Cluster> clusters, NumericMatrix costMatrix, ArrayList<Depot> medoids) {
		double cost = 0.0;
		
		int idCustomer =  -1;
		int posCustomer = -1;
		int posDepot = -1;
		ArrayList<Integer> listIDCustomers = new ArrayList<Integer>();

		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CALCULO DEL MEJOR COSTO");
		
		for(int i = 0; i < clusters.size(); i++) 
		{			
			posDepot = InfoProblem.getProblem().getPosElement(medoids.get(i).getIDDepot());
			listIDCustomers = clusters.get(i).getItemsOfCluster();
		
			System.out.println("-------------------------------------------------------------------------------");		
			System.out.println("ID MEDOIDE: " + medoids.get(i).getIDDepot());
			System.out.println("POSICIÓN DEL MEDOIDE: " + posDepot);
			System.out.println("CLIENTES ASIGNADOS AL MEDOIDE: " + listIDCustomers);
			System.out.println("-------------------------------------------------------------------------------");
			
			for(int j = 0; j < listIDCustomers.size(); j++) 
			{	
				idCustomer =  listIDCustomers.get(j);				
				posCustomer = InfoProblem.getProblem().getPosElement(idCustomer);

				if(posDepot == posCustomer)
					cost += 0.0;
				else
					cost += costMatrix.getItem(posDepot, posCustomer);	
				
				System.out.println("ID CLIENTE: " + idCustomer);
				System.out.println("POSICIÓN DEL CLIENTE: " + posCustomer);
				System.out.println("COSTO: " + costMatrix.getItem(posDepot, posCustomer));
				System.out.println("-------------------------------------------------------------------------------");
				System.out.println("COSTO ACUMULADO: " + cost);	
				System.out.println("-------------------------------------------------------------------------------");
			}
		}

		System.out.println("MEJOR COSTO TOTAL: " + cost);	
		System.out.println("-------------------------------------------------------------------------------");
		
		return cost;
	}
	
	protected double calculateCost(ArrayList<Cluster> clusters, NumericMatrix costMatrix, ArrayList<Depot> medoids, ArrayList<Customer> listPartition) {
		double cost = 0.0;
		
		int idCustomer =  -1;
		int posCustomer = -1;
		int posDepot = -1;
		ArrayList<Integer> listIDCustomers = new ArrayList<Integer>();

		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CALCULO DEL MEJOR COSTO");
		
		for(int i = 0; i < clusters.size(); i++) 
		{			
			posDepot = InfoProblem.getProblem().getPosElement(medoids.get(i).getIDDepot(), listPartition);
			listIDCustomers = clusters.get(i).getItemsOfCluster();
		
			System.out.println("-------------------------------------------------------------------------------");		
			System.out.println("ID MEDOIDE: " + medoids.get(i).getIDDepot());
			System.out.println("POSICIÓN DEL MEDOIDE: " + posDepot);
			System.out.println("CLIENTES ASIGNADOS AL MEDOIDE: " + listIDCustomers);
			System.out.println("-------------------------------------------------------------------------------");
			
			for(int j = 0; j < listIDCustomers.size(); j++) 
			{	
				idCustomer =  listIDCustomers.get(j);				
				posCustomer = InfoProblem.getProblem().getPosElement(idCustomer, listPartition);

				if(posDepot == posCustomer)
					cost += 0.0;
				else
					cost += costMatrix.getItem(posDepot, posCustomer);	
				
				System.out.println("ID CLIENTE: " + idCustomer);
				System.out.println("POSICIÓN DEL CLIENTE: " + posCustomer);
				System.out.println("COSTO: " + costMatrix.getItem(posDepot, posCustomer));
				System.out.println("-------------------------------------------------------------------------------");
				System.out.println("COSTO ACUMULADO: " + cost);	
				System.out.println("-------------------------------------------------------------------------------");
			}
		}

		System.out.println("MEJOR COSTO TOTAL: " + cost);	
		System.out.println("-------------------------------------------------------------------------------");
		
		return cost;
	}
	
	protected double calculateDissimilarity()
	{
		double currentDissimilarity = 0.0;
		
		/*Función para calcular el promedio de disimilitud*/
		
		return currentDissimilarity;
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
			fleet.addAll(InfoProblem.getProblem().getDepots().get(i).getFleetDepot());
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
			idDepot = InfoProblem.getProblem().getDepots().get(posDepot).getIDDepot();
			capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(idDepot);

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
					customerToAssign.remove(InfoProblem.getProblem().findPosCustomer(customerToAssign, idCustomer));
					
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
	
	protected ArrayList<Depot> createCentroids(ArrayList<Integer> idElements){
		ArrayList<Depot> centroids = new ArrayList<Depot>();
		
		for(int i = 0; i < idElements.size(); i++)
		{
			Depot centroid = new Depot();
			
			centroid.setIDDepot(idElements.get(i));
			
			Location location = new Location();
			location.setAxisX(InfoProblem.getProblem().getCustomerByIDCustomer(idElements.get(i).intValue()).getLocationCustomer().getAxisX());
			location.setAxisY(InfoProblem.getProblem().getCustomerByIDCustomer(idElements.get(i).intValue()).getLocationCustomer().getAxisY());
			centroid.setLocationDepot(location);
			
			centroids.add(centroid);
		}
		
		return centroids;
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
	
	protected void stepSearchMedoids(ArrayList<Cluster> clusters, ArrayList<Depot> medoids, NumericMatrix costMatrix, double bestCost){
		double currentCost = 0.0;
		
		ArrayList<Depot> oldMedoids = replicateDepots(medoids);
		
		System.out.println("--------------------------------------------------------------------");
		System.out.println("PROCESO DE BÚSQUEDA");
		
		for(int i = 0; i < clusters.size(); i++) 
		{	
			Location bestLocMedoid = new Location(medoids.get(i).getLocationDepot().getAxisX(), medoids.get(i).getLocationDepot().getAxisY());
			
			System.out.println("--------------------------------------------------");
			System.out.println("MEJOR MEDOIDE ID: " + medoids.get(i).getIDDepot());
			System.out.println("MEJOR MEDOIDE LOCATION X: " + bestLocMedoid.getAxisX());
			System.out.println("MEJOR MEDOIDE LOCATION Y: " + bestLocMedoid.getAxisY());
			System.out.println("--------------------------------------------------");
			
			for(int j = 1; j < clusters.get(i).getItemsOfCluster().size(); j++) 
			{
				int newIDMedoid = clusters.get(i).getItemsOfCluster().get(j);
				Customer newMedoid = new Customer();
				
				newMedoid.setIDCustomer(InfoProblem.getProblem().getCustomerByIDCustomer(newIDMedoid).getIDCustomer());
				newMedoid.setRequestCustomer(InfoProblem.getProblem().getCustomerByIDCustomer(newIDMedoid).getRequestCustomer());	
				
				Location location = new Location();
				location.setAxisX(InfoProblem.getProblem().getCustomerByIDCustomer(newIDMedoid).getLocationCustomer().getAxisX());
				location.setAxisY(InfoProblem.getProblem().getCustomerByIDCustomer(newIDMedoid).getLocationCustomer().getAxisY());
				newMedoid.setLocationCustomer(location);
				
				medoids.get(i).setIDDepot(newIDMedoid);
				medoids.get(i).setLocationDepot(newMedoid.getLocationCustomer());
				
				System.out.println("ID DEL NUEVO MEDOIDE: " + newIDMedoid);
				System.out.println("X DEL NUEVO MEDOIDE: " + newMedoid.getLocationCustomer().getAxisX());
				System.out.println("Y DEL NUEVO MEDOIDE: " + newMedoid.getLocationCustomer().getAxisY());
				
				System.out.println("--------------------------------------------------");
				System.out.println("LISTA DE MEDOIDES");
				System.out.println("ID: " + medoids.get(i).getIDDepot());
				System.out.println("X: " + medoids.get(i).getLocationDepot().getAxisX());
				System.out.println("Y: " + medoids.get(i).getLocationDepot().getAxisY());					
			
				System.out.println("LISTA DE ANTERIORES MEDOIDES");
				System.out.println("ID: " + oldMedoids.get(i).getIDDepot());
				System.out.println("X: " + oldMedoids.get(i).getLocationDepot().getAxisX());
				System.out.println("Y: " + oldMedoids.get(i).getLocationDepot().getAxisY());
				System.out.println("--------------------------------------------------");
								
				currentCost = calculateCost(clusters, costMatrix, medoids);
				
				System.out.println("---------------------------------------------");
				System.out.println("ACTUAL COSTO TOTAL: " + currentCost);
				System.out.println("---------------------------------------------");
				
				if(currentCost < bestCost) 
				{
					bestCost = currentCost;	
					bestLocMedoid = medoids.get(i).getLocationDepot();
					
					System.out.println("NUEVO MEJOR COSTO TOTAL: " + bestCost);
					System.out.println("NUEVO MEDOIDE ID: " + medoids.get(i).getIDDepot());
					System.out.println("NUEVO MEDOIDE LOCATION X: " + bestLocMedoid.getAxisX());
					System.out.println("NUEVO MEDOIDE LOCATION Y: " + bestLocMedoid.getAxisY());
					System.out.println("---------------------------------------------");
					
					oldMedoids.get(i).setIDDepot(medoids.get(i).getIDDepot());
					oldMedoids.get(i).getLocationDepot().setAxisX(medoids.get(i).getLocationDepot().getAxisX());
					oldMedoids.get(i).getLocationDepot().setAxisY(medoids.get(i).getLocationDepot().getAxisY());
				}
				else
				{
					medoids.get(i).setIDDepot(oldMedoids.get(i).getIDDepot());
					medoids.get(i).getLocationDepot().setAxisX(oldMedoids.get(i).getLocationDepot().getAxisX());					
					medoids.get(i).getLocationDepot().setAxisY(oldMedoids.get(i).getLocationDepot().getAxisY());	
				}
				
				System.out.println("ID MEDOIDE: " + medoids.get(i).getIDDepot());
				System.out.println("LISTA DE MEDOIDES X: " + medoids.get(i).getLocationDepot().getAxisX());
				System.out.println("LISTA DE MEDOIDES Y: " + medoids.get(i).getLocationDepot().getAxisY());
				System.out.println("---------------------------------------------");
			}
			
			medoids.get(i).setLocationDepot(bestLocMedoid);
		}
	}
	
	protected void stepSearchMedoids(ArrayList<Cluster> clusters, ArrayList<Depot> medoids, NumericMatrix costMatrix, double bestCost, ArrayList<Customer> listPartition){
		double currentCost = 0.0;
		
		ArrayList<Depot> oldMedoids = replicateDepots(medoids);
		
		System.out.println("--------------------------------------------------------------------");
		System.out.println("PROCESO DE BÚSQUEDA");
		
		for(int i = 0; i < clusters.size(); i++) 
		{	
			Location bestLocMedoid = new Location(medoids.get(i).getLocationDepot().getAxisX(), medoids.get(i).getLocationDepot().getAxisY());
			
			System.out.println("--------------------------------------------------");
			System.out.println("MEJOR MEDOIDE ID: " + medoids.get(i).getIDDepot());
			System.out.println("MEJOR MEDOIDE LOCATION X: " + bestLocMedoid.getAxisX());
			System.out.println("MEJOR MEDOIDE LOCATION Y: " + bestLocMedoid.getAxisY());
			System.out.println("--------------------------------------------------");
			
			for(int j = 1; j < clusters.get(i).getItemsOfCluster().size(); j++) 
			{
				int newIDMedoid = clusters.get(i).getItemsOfCluster().get(j);
				Customer newMedoid = new Customer();
				
				newMedoid.setIDCustomer(InfoProblem.getProblem().getCustomerByIDCustomer(newIDMedoid).getIDCustomer());
				newMedoid.setRequestCustomer(InfoProblem.getProblem().getCustomerByIDCustomer(newIDMedoid).getRequestCustomer());	
				
				Location location = new Location();
				location.setAxisX(InfoProblem.getProblem().getCustomerByIDCustomer(newIDMedoid).getLocationCustomer().getAxisX());
				location.setAxisY(InfoProblem.getProblem().getCustomerByIDCustomer(newIDMedoid).getLocationCustomer().getAxisY());
				newMedoid.setLocationCustomer(location);
				
				medoids.get(i).setIDDepot(newIDMedoid);
				medoids.get(i).setLocationDepot(newMedoid.getLocationCustomer());
				
				System.out.println("ID DEL NUEVO MEDOIDE: " + newIDMedoid);
				System.out.println("X DEL NUEVO MEDOIDE: " + newMedoid.getLocationCustomer().getAxisX());
				System.out.println("Y DEL NUEVO MEDOIDE: " + newMedoid.getLocationCustomer().getAxisY());
				
				System.out.println("--------------------------------------------------");
				System.out.println("LISTA DE MEDOIDES");
				System.out.println("ID: " + medoids.get(i).getIDDepot());
				System.out.println("X: " + medoids.get(i).getLocationDepot().getAxisX());
				System.out.println("Y: " + medoids.get(i).getLocationDepot().getAxisY());					
			
				System.out.println("LISTA DE ANTERIORES MEDOIDES");
				System.out.println("ID: " + oldMedoids.get(i).getIDDepot());
				System.out.println("X: " + oldMedoids.get(i).getLocationDepot().getAxisX());
				System.out.println("Y: " + oldMedoids.get(i).getLocationDepot().getAxisY());
				System.out.println("--------------------------------------------------");
								
				currentCost = calculateCost(clusters, costMatrix, medoids, listPartition);
				
				System.out.println("---------------------------------------------");
				System.out.println("ACTUAL COSTO TOTAL: " + currentCost);
				System.out.println("---------------------------------------------");
				
				if(currentCost < bestCost) 
				{
					bestCost = currentCost;	
					bestLocMedoid = medoids.get(i).getLocationDepot();
					
					System.out.println("NUEVO MEJOR COSTO TOTAL: " + bestCost);
					System.out.println("NUEVO MEDOIDE ID: " + medoids.get(i).getIDDepot());
					System.out.println("NUEVO MEDOIDE LOCATION X: " + bestLocMedoid.getAxisX());
					System.out.println("NUEVO MEDOIDE LOCATION Y: " + bestLocMedoid.getAxisY());
					System.out.println("---------------------------------------------");
					
					oldMedoids.get(i).setIDDepot(medoids.get(i).getIDDepot());
					oldMedoids.get(i).getLocationDepot().setAxisX(medoids.get(i).getLocationDepot().getAxisX());
					oldMedoids.get(i).getLocationDepot().setAxisY(medoids.get(i).getLocationDepot().getAxisY());
				}
				else
				{
					medoids.get(i).setIDDepot(oldMedoids.get(i).getIDDepot());
					medoids.get(i).getLocationDepot().setAxisX(oldMedoids.get(i).getLocationDepot().getAxisX());					
					medoids.get(i).getLocationDepot().setAxisY(oldMedoids.get(i).getLocationDepot().getAxisY());	
				}
				
				System.out.println("ID MEDOIDE: " + medoids.get(i).getIDDepot());
				System.out.println("LISTA DE MEDOIDES X: " + medoids.get(i).getLocationDepot().getAxisX());
				System.out.println("LISTA DE MEDOIDES Y: " + medoids.get(i).getLocationDepot().getAxisY());
				System.out.println("---------------------------------------------");
			}
			
			medoids.get(i).setLocationDepot(bestLocMedoid);
		}
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
	
	private Location calculateMeanCoordinate(){
		double AxisX = 0.0;
		double AxisY = 0.0;

		ArrayList<Location> listCoordinatesCustomers = InfoProblem.getProblem().getListCoordinatesCustomers();

		for(int i = 0; i < listCoordinatesCustomers.size(); i++) 
		{
			AxisX += listCoordinatesCustomers.get(i).getAxisX();
			AxisY += listCoordinatesCustomers.get(i).getAxisY();
		}

		AxisX /= listCoordinatesCustomers.size();
		AxisY /= listCoordinatesCustomers.size();

		Location meanLocation = new Location(AxisX, AxisY);
		
		return meanLocation;
	}
	
	protected ArrayList<Integer> sortedElements (ArrayList<Integer> idElements, DistanceType distanceType){
		
		int totalDepots = InfoProblem.getProblem().getTotalDepots();
		int j = 0;
		
		ArrayList<Integer> sortedElements = new ArrayList<Integer>();
		ArrayList<Customer> customers = new ArrayList<Customer>();

		for(int i = 0; i < idElements.size(); i++)
		{
			sortedElements.add(-1);
			customers.add(InfoProblem.getProblem().getCustomerByIDCustomer(idElements.get(i)));
		}
		
		NumericMatrix costMatrix = null;
		RowCol rcBestAll = new RowCol();
		
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = InfoProblem.getProblem().fillCostMatrix(customers, InfoProblem.getProblem().getDepots(), distanceType);
				} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				break;
			}
			case 4:
			{
				//costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());

				break;
			}
		}
		
//		System.out.println("----ORGANIZAR ELEMENTOS SELECCIONADOS-----------------------------------------------------");
		
		while(j < idElements.size())
		{
			rcBestAll = costMatrix.indexLowerValue(0, idElements.size(), (idElements.size() - 1), (idElements.size() + totalDepots - 1));
			
			System.out.println("ROW SELECCIONADA: " + rcBestAll.getRow());
			System.out.println("COL SELECCIONADA: " + rcBestAll.getCol());
			System.out.println("VALOR SELECCIONADO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));
			
			costMatrix.fillValue(0, rcBestAll.getCol(), (idElements.size() - 1), rcBestAll.getCol(), Double.POSITIVE_INFINITY);
			costMatrix.fillValue(rcBestAll.getRow(), (idElements.size() - 1), rcBestAll.getRow(), (idElements.size() + totalDepots - 1), Double.POSITIVE_INFINITY);
			
			sortedElements.set((rcBestAll.getCol() - idElements.size()), idElements.get(rcBestAll.getRow()));
			
			System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS ORDENADOS ACTUALIZADA:" + sortedElements);
			
			j++;
		}
		
		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS ORDENADOS:" + sortedElements);
		
		return sortedElements;
	}
	// generar particion secuencial y aleatoria
	protected ArrayList<ArrayList<Customer>> generatePartitions(int sampsize, SamplingType samplingType){
		ArrayList<ArrayList<Customer>> partitions = new ArrayList<ArrayList<Customer>>();
		ArrayList<Customer> partition = new ArrayList<Customer>();
		
		int totalCustomers = InfoProblem.getProblem().getTotalCustomers();
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
				
				ArrayList<Customer> customers = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
				
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
						partition.add(InfoProblem.getProblem().getCustomers().get(j));
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

	protected ArrayList<Integer> generateElements(ArrayList<Integer> pCustomers, int sampsize, DistanceType distanceType){
		
		ArrayList<Integer> idElements = new ArrayList<Integer>();

		int totalCustomers = InfoProblem.getProblem().getTotalCustomers();
		int totalDepots = InfoProblem.getProblem().getTotalDepots();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/	
				
			    break;
			}
			
			case 4:
			{
				costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
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


			idElement = InfoProblem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();	
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
}