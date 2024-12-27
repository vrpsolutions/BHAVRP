package cujae.inf.ic.om.problem.input;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.factory.interfaces.IFactoryDistance;

import cujae.inf.ic.om.factory.methods.FactoryDistance;

import cujae.inf.ic.om.controller.utils.Tools;

import cujae.inf.ic.om.distance.IDistance;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.service.OSRMService;

public class Problem {

	private ArrayList<Customer> customers;
	private ArrayList<Depot> depots;
	private NumericMatrix costMatrix; //revisar si existe diferencias entre fill y calculate con igual parametros
	
	private static Problem problem = null;

	private Problem() {
		super();
		customers = new ArrayList<Customer>();
		depots = new ArrayList<Depot>();
		costMatrix = new NumericMatrix();
	}

	/* Método encargado de implementar el Patrón Singleton*/
	public static Problem getProblem () {
		if (problem == null) {
			problem = new Problem();
		}
		return problem;
	}

	public ArrayList<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(ArrayList<Customer> customers) {
		this.customers = customers;
	}

	public ArrayList<Depot> getDepots() {
		return depots;
	}

	public void setDepots(ArrayList<Depot> depots) {
		this.depots = depots;
	}

	public NumericMatrix getCostMatrix() {
		return costMatrix;
	}

	public void setCostMatrix(NumericMatrix costMatrix) {
		this.costMatrix = costMatrix;
	}

	public int getTotalCustomers(){
		return customers.size();
	}
	
	public int getTotalDepots(){
		return depots.size();
	}

	/*Método encargado de obtener la lista de id de los clientes*/
	public ArrayList<Integer> getListIDCustomers(){
		int totalCustomers = customers.size();
		ArrayList<Integer> listIDCustomers = new ArrayList<Integer>();

		for(int i = 0; i < totalCustomers; i++) 
			listIDCustomers.add(customers.get(i).getIDCustomer());

		return listIDCustomers;
	}

	/*Método encargado de obtener la lista de coordenadas de los clientes*/
	public ArrayList<Location> getListCoordinatesCustomers(){
		int totalCustomers = customers.size();
		ArrayList<Location> listCoordinatesCustomers = new ArrayList<Location>();
		
		for(int i = 0; i < totalCustomers; i++){
			listCoordinatesCustomers.add(customers.get(i).getLocationCustomer());
		}
		return listCoordinatesCustomers;
	}
	
	/*Método encargado de devolver la demanda total*/
	public double getTotalRequest(){
		double totalRequest = 0.0;
		int totalCustomers = customers.size();

		for(int i = 0; i < totalCustomers; i++)
			totalRequest += customers.get(i).getRequestCustomer();

		return totalRequest;
	}

	/*Método encargado de buscar un cliente dado su identificador*/
	public Customer getCustomerByIDCustomer(int idCustomer){
		Customer customer = null;
		int i = 0;
		boolean found = false;
		int totalCustomers = customers.size();

		while((i < totalCustomers) && (!found))
		{
			if(customers.get(i).getIDCustomer() == idCustomer)
			{
				customer = customers.get(i);
				found = true;
			}
			else
				i++;
		}
		return customer;
	}

	/*Método encargado de buscar las coordenadas de un cliente dado su identificador*/
	public Location getLocationByIDCustomer(int idCustomer){
		Location location = null;
		int i = 0;
		boolean found = false;
		int totalCustomers = customers.size();

		while((i < totalCustomers) && (!found))
		{
			if(customers.get(i).getIDCustomer() == idCustomer)
			{
				location = customers.get(i).getLocationCustomer();
				found = true;
			}
			else
				i++;
		}
		return location;
	}

	/*Método encargado de buscar un depósito dado su identificador*/
	public Depot getDepotByIDDepot(int idDepot){
		Depot depot = null;
		int i = 0;
		boolean found = false;
		int totalDepots = depots.size();

		while((i < totalDepots) && (!found))
		{
			if(depots.get(i).getIDDepot() == idDepot)
			{
				depot = depots.get(i);
				found = true;
			}
			else
				i++;
		}
		return depot;
	}

	/*Método encargado de buscar las coordenadas de un depósito dado su identificador*/
	public Location getLocationByIDDepot(int idDepot){
		Location location = null;
		int i = 0;
		boolean found = false;
		int totalDepots = depots.size();

		while((i < totalDepots) && (!found))
		{
			if(depots.get(i).getIDDepot() == idDepot)
			{
				location = depots.get(i).getLocationDepot();
				found = true;
			}
			else
				i++;
		}
		return location;
	}
	
	/*public int getPosElement(int idElement) {
		int posElement = -1;

		Iterator<Integer> iterator = depots.iterator();
		int totalElements = getTotalCustomers() + getTotalDepots();

		boolean found = false;
		int i = 0;
		
		while ((iterator.hasNext()) && (!found)) 
		{
			if (iterator.next().intValue() == idElement) 
			{
				posElement = i + totalElements;
				found = true;
			} 
			else
				i++;
		}

		i = 0;
		int j = 0;

		while ((j < listIDElementsForDepots.size()) && (!found)) 
		{
			iterator = listIDElementsForDepots.get(j).iterator();

			while ((iterator.hasNext()) && (!found)) 
			{
				if (iterator.next().intValue() == idElement) 
				{
					found = true;
					posElement = i;
				} 
				else
					i++;
			}
			
			j++;
		}

		return posElement;
	}*/
	
	/*Método encargado de devolver la posición que ocupa un depósito en la lista de depósitos pasada por parámetro*/
	public int findPosElement(ArrayList<Integer> listID, int idElement){
		int posElement = -1;

		int i = 0;
		boolean found = false;
		int totalElements = listID.size();

		while((i < totalElements) && (!found))
		{
			if(listID.get(i).intValue() == idElement)
			{
				found = true;
				posElement = i;
			}
			else
				i++;
		}
		return posElement;	
	}


	/*Método encargado de devolver la posición que ocupa un cliente en la lista pasada por parámetro*/
	public int findPosCustomer(ArrayList<Customer> customers, int idCustomer){
		int posCustomer = -1;

		int i = 0;
		boolean found = false;
		int totalElements = customers.size();
		
		while((i < totalElements) && (!found))
		{
			if(customers.get(i).getIDCustomer() == idCustomer)
			{
				found = true;
				posCustomer = i;
			}
			else
				i++;
		}

		return posCustomer;	
	}

	/*Método encargado de devolver la posición que ocupa un depósito en la lista pasada por parámetro*/
	public int findPosDepot(ArrayList<Depot> depots, int idElement){
		int posElement = -1;

		int i = 0;
		boolean found = false;
		int totalDepots = depots.size();
		
		while((i < totalDepots) && (!found))
		{
			if(depots.get(i).getIDDepot() == idElement)
			{
				found = true;
				posElement = i;
			}
			else
				i++;
		}
		return posElement;	
	}

	/*Método encargado de devolver la demanda de un cliente dado su identificador*/
	public double getRequestByIDCustomer(int idCustomer){
		double requestCustomer = 0.0;

		int i = 0;
		boolean found = false;
		int totalCustomers = customers.size();

		while((i < totalCustomers) && (!found))
		{
			if(customers.get(i).getIDCustomer() == idCustomer)
			{
				requestCustomer = customers.get(i).getRequestCustomer();
				found = true;
			}
			else
				i++;
		}
		return requestCustomer;
	}

	 /**
     * @param  int identificador del elemento
     * @return int posición en la matriz de costo del elemento
     * Retorna la posición del elemento en la matriz de costo
     */
	public int getPosElement(int idElement){
		int posElement = -1;
		
		int totalCustomers = customers.size();
		int totalDepots = depots.size();

		int i = 0;
		boolean found = false;

		while((i < totalDepots) && (!found)) 
		{
			if(depots.get(i).getIDDepot() == idElement) 
			{
				posElement = i + totalCustomers;
				found = true;
			} 
			else
				i++;
		}

		if(!found)
		{
			i = 0;
			while ((i < totalCustomers) && (!found)) 
			{
				if (customers.get(i).getIDCustomer() == idElement) 
				{
					posElement = i;
					found = true;
				} 
				else
					i++;
			}
		}
		return posElement;
	}
	
	public int getPosElement(int idElement, ArrayList<Customer> listCustomers){
		int posElement = -1;
		
		int totalCustomers = listCustomers.size();
		int totalDepots = depots.size();

		int i = 0;
		boolean found = false;

		while((i < totalDepots) && (!found)) 
		{
			if(depots.get(i).getIDDepot() == idElement) 
			{
				posElement = i + totalCustomers;
				found = true;
			} 
			else
				i++;
		}

		if(!found)
		{
			i = 0;
			while ((i < totalCustomers) && (!found)) 
			{
				if (listCustomers.get(i).getIDCustomer() == idElement) 
				{
					posElement = i;
					found = true;
				} 
				else
					i++;
			}
		}
		return posElement;
	}
	
	/*Método encargado de devolver la capacidad total de los depósitos*/
	public double getTotalCapacity(){
		double totalCapacity = 0.0; 
		int totalDepots = depots.size();

		for(int i = 0; i < totalDepots; i++)
			totalCapacity += getTotalCapacityByDepot(depots.get(i));

		return totalCapacity;
	}

	/*Método encargado de devolver la capacidad total de un depósito dado el depósito*/
	public double getTotalCapacityByDepot(Depot depot){
		double totalCapacity = 0.0;

		double capacityVehicle = 0.0;
		int countVehicles = 0;
		
		int totalFleets = depot.getFleetDepot().size();
		
		for(int i = 0; i < totalFleets; i++)
		{
			capacityVehicle = depot.getFleetDepot().get(i).getCapacityVehicle();
			countVehicles = depot.getFleetDepot().get(i).getCountVehicles();

			totalCapacity += capacityVehicle * countVehicles;
		}
		return totalCapacity;
	}
	
	/*Método encargado de devolver la capacidad total de un depósito dado su identificador*/
	public double getTotalCapacityByDepot(int idDepot){
		double totalCapacity = 0.0;
		double capacityVehicle = 0.0;
		int countVehicles = 0;
		
		int posDepot = findPosDepot(depots, idDepot);
		Depot depot = depots.get(posDepot);
		int totalFleets = depot.getFleetDepot().size();
		
		
		for(int i = 0; i < totalFleets; i++)
		{
			capacityVehicle = depot.getFleetDepot().get(i).getCapacityVehicle();
			countVehicles = depot.getFleetDepot().get(i).getCountVehicles();

			totalCapacity += capacityVehicle * countVehicles;
		}
		return totalCapacity;
	}

	/*Método encargado de obtener la lista de las capcidades de los depositos*/
	public ArrayList<Double> getCapacitiesDepot(){
		ArrayList<Double> capacities = new ArrayList<Double>();
		double capacityDepot = 0.0; 
		int totalDepots = depots.size();

		for(int i = 0; i < totalDepots; i++)
		{
			capacityDepot = getTotalCapacityByDepot(depots.get(i));
			capacities.add(capacityDepot);
		}

		return capacities;
	}

	/*Método encargado de obtener el id del deposito con mayor capacidad*/
	public int getDepotWithMU(){
		int idDepotMU = depots.get(0).getIDDepot();
		double maxCapacityDepot = getTotalCapacityByDepot(depots.get(0)); 
		int totalDepots = depots.size();

		double currentCapacityDepot; 

		for(int i = 1; i < totalDepots; i++)
		{
			currentCapacityDepot = getTotalCapacityByDepot(depots.get(i));
			
			if(maxCapacityDepot < currentCapacityDepot)
			{
				maxCapacityDepot = currentCapacityDepot;
				idDepotMU = depots.get(i).getIDDepot(); 
			}
		}
		return idDepotMU;
	}
	
	/*Método encargado de obtener el id del deposito con mayor capacidad de la lista*/
	public int getDepotWithMU(ArrayList<Depot> depots){
		int idDepotMU = depots.get(0).getIDDepot();
		double maxCapacityDepot = getTotalCapacityByDepot(depots.get(0)); 
		int totalDepots = depots.size();

		double currentCapacityDepot; 

		for(int i = 1; i < totalDepots; i++)
		{
			currentCapacityDepot = getTotalCapacityByDepot(depots.get(i));
			
			if(maxCapacityDepot < currentCapacityDepot)
			{
				maxCapacityDepot = currentCapacityDepot;
				idDepotMU = depots.get(i).getIDDepot(); 
			}
		}
		return idDepotMU;
	}
	
	/*Método encargado de obtener la capacidad del deposito con mayor capacidad de la lista*/
	public double getCapacityDepotWithMU(ArrayList<Depot> depots){
		double maxCapacityDepot = getTotalCapacityByDepot(depots.get(0)); 
		int totalDepots = depots.size();

		double currentCapacityDepot; 

		for(int i = 1; i < totalDepots; i++)
		{
			currentCapacityDepot = getTotalCapacityByDepot(depots.get(i));
			
			if(maxCapacityDepot < currentCapacityDepot)
				maxCapacityDepot = currentCapacityDepot;
		}
		return maxCapacityDepot;
	}

	/*Método encargado de obtener la lista de los id de los clientes y los depositos*/
	public ArrayList<Integer> getListIDElements(){
		int totalCustomers = customers.size();
		int totalDepots = depots.size();
		ArrayList<Integer> listIDElements = new ArrayList<Integer>();

		for(int i = 0; i < totalCustomers; i++) 
			listIDElements.add(customers.get(i).getIDCustomer());
		
		for(int j = 0; j < totalDepots; j++) 
			listIDElements.add(depots.get(j).getIDDepot());

		return listIDElements;	
	}
	
	/*Método encargado de obtener la lista de los id de los depositos*/
	public ArrayList<Integer> getListIDDepots(){
		int totalDepots = depots.size();
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>();

		for(int i = 0; i < totalDepots; i++) 
			listIDDepots.add(depots.get(i).getIDDepot());

		return listIDDepots;
	}

	/*Método encargado de obtener los identificadores de los elementos en la lista pasada por parámetros*/
	public ArrayList<Integer> getListID(ArrayList<Customer> customers){
		ArrayList<Integer> listID = new ArrayList<Integer>();

		for(int i = 0; i < customers.size(); i++) 
			listID.add(customers.get(i).getIDCustomer());

		return listID;
	}
	
	/* Método encargado de cargar los datos de los clientes sin coordenadas*/
	public void loadCustomer(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers){
		Customer customer;
		int totalCustomers = idCustomers.size();
		
		for (int i = 0; i < totalCustomers; i++) 
		{	
			customer = new Customer();
			customer.setIDCustomer(idCustomers.get(i));
			customer.setRequestCustomer(requestCustomers.get(i));

			customers.add(customer);
		}
	}

	/* Método encargado de cargar los datos de los clientes con coordenadas*/
	public void loadCustomer(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Double> axisXCustomers, ArrayList<Double> axisYCustomers){
		Customer customer;
		Location location;

		int totalCustomers = idCustomers.size();
		
		for (int i = 0; i < totalCustomers; i++) 
		{	
			customer = new Customer();
			customer.setIDCustomer(idCustomers.get(i));
			customer.setRequestCustomer(requestCustomers.get(i));
			
			location = new Location(Tools.truncateDouble(axisXCustomers.get(i), 6), Tools.truncateDouble(axisYCustomers.get(i), 6));
			customer.setLocationCustomer(location);

			customers.add(customer);
		}
	}
	
	/* Método encargado de cargar los datos de los depósitos (con coordenadas) y las flotas*/
	public void loadDepot(ArrayList<Integer> idDepots, ArrayList<Double> axisXDepots, ArrayList<Double> axisYDepots, ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles){
		Depot depot;
		Location location;
		Fleet fleet;
		ArrayList<Fleet> fleets;
		
		int totalFleets;
		int totalDepots = idDepots.size(); 
		
		for(int i = 0; i < totalDepots; i++)
		{
			depot = new Depot();
			depot.setIDDepot(idDepots.get(i));
			
			location = new Location(Tools.truncateDouble(axisXDepots.get(i), 6), Tools.truncateDouble(axisYDepots.get(i), 6));
			depot.setLocationDepot(location);

			fleets = new ArrayList<Fleet>();
			totalFleets = countVehicles.get(i).size(); 
			
			for(int j = 0; j < totalFleets; j++)
			{
				fleet = new Fleet();
				fleet.setCountVehicles(countVehicles.get(i).get(j));
				fleet.setCapacityVehicle(capacityVehicles.get(i).get(j));

				fleets.add(fleet);
			}

			depot.setFleetDepot(fleets);
			depots.add(depot);
		}
	}

	/* Método encargado de cargar los datos de los depósitos (sin coordenadas) y las flotas*/
	public void loadDepot(ArrayList<Integer> idDepots, ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles){
		Depot depot;
		Fleet fleet;
		ArrayList<Fleet> fleets;
		
		int totalFleets;
		int totalDepots = idDepots.size(); 
		
		for(int i = 0; i < totalDepots; i++)
		{
			depot = new Depot();
			depot.setIDDepot(idDepots.get(i));

			fleets = new ArrayList<Fleet>();
			totalFleets = countVehicles.get(i).size(); 
			
			for(int j = 0; j < totalFleets; j++)
			{
				fleet = new Fleet();
				fleet.setCountVehicles(countVehicles.get(i).get(j));
				fleet.setCapacityVehicle(capacityVehicles.get(i).get(j));

				fleets.add(fleet);
			}

			depot.setFleetDepot(fleets);
			depots.add(depot);
		}
	}

	/* Método encargado de llenar la matriz de costo usando listas de distancias !!LOAD del Controller!!*/
	public void fillCostMatrix(ArrayList<ArrayList<Double>> distances) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		int totalDistances = distances.size(); 
		int totalElements;
		costMatrix = new NumericMatrix(totalDistances, totalDistances);

		int row = -1;
		int col = -1;
		double costInDistance = 0.0;

		for(int i = 0; i < totalDistances; i++)
		{
			row = i;
			totalElements = distances.get(i).size();
			
			for(int j = 0; j < totalElements; j++)
			{
				col = j;	
				costInDistance = distances.get(i).get(j);
				costMatrix.setItem(row, col, costInDistance);	
			}
		}
	}

	/* Método encargado de llenar la matriz de costo usando la distancia deseada !!Algoritmos!!*/
	public NumericMatrix fillCostMatrix(ArrayList<Customer> customers, ArrayList<Depot> depots, DistanceType distanceType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int totalCustomers = customers.size();
		int totalDepots = depots.size();

		NumericMatrix costMatrix = new NumericMatrix((totalCustomers + totalDepots), (totalCustomers + totalDepots));
		IDistance distance = newDistance(distanceType);

		double axisXIni = 0.0;
		double axisYIni = 0.0;
		double axisXEnd = 0.0;
		double axisYEnd = 0.0;
		int lastPointOne = 0;
		int lastPointTwo = 0;
		double cost = 0.0;

		for (int i = 0; i < (totalCustomers + totalDepots); i++)  
		{
			if (i <= (totalCustomers - 1)) 
			{
				axisXIni = customers.get(i).getLocationCustomer().getAxisX();
				axisYIni = customers.get(i).getLocationCustomer().getAxisY();
			} 
			else 
			{
				axisXIni = depots.get(lastPointOne).getLocationDepot().getAxisX();
				axisYIni = depots.get(lastPointOne).getLocationDepot().getAxisY();
				lastPointOne++;
			}

			lastPointTwo = 0;

			for (int j = 0; j < (totalCustomers + totalDepots); j++)  // eficiencia
			{
				if (j <= (totalCustomers - 1)) 
				{
					axisXEnd = customers.get(j).getLocationCustomer().getAxisX();
					axisYEnd = customers.get(j).getLocationCustomer().getAxisY();
				} 
				else 
				{
					axisXEnd = depots.get(lastPointTwo).getLocationDepot().getAxisX();
					axisYEnd = depots.get(lastPointTwo).getLocationDepot().getAxisY();
					lastPointTwo++;
				}

				if (i == j)
					costMatrix.setItem(i, j, Double.POSITIVE_INFINITY);
				else 
				{
					try {
						cost = distance.calculateDistance(axisXIni, axisYIni, axisXEnd, axisYEnd);
					} catch (Exception e) {
						e.printStackTrace();
					}
					costMatrix.setItem(i, j, cost);
					costMatrix.setItem(j, i, cost);
				}
			}
		}
		return costMatrix;
	}
	
	/*Método encargado de llenar la matriz de costo usando el tipo de distancia deseada*/
	public NumericMatrix fillCostMatrix(DistanceType distanceType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		int totalCustomers = customers.size();
		int totalDepots = depots.size();

		NumericMatrix costMatrix = new NumericMatrix((totalCustomers + totalDepots), (totalCustomers + totalDepots));
		
		if(distanceType.equals("OSRMService")){
			
		}
		else{
			IDistance distance = newDistance(distanceType);

			double axisXIni = 0.0;
			double axisYIni = 0.0;
			double axisXEnd = 0.0;
			double axisYEnd = 0.0;
			int lastPointOne = 0;
			int lastPointTwo = 0;
			double cost = 0.0;

			for (int i = 0; i < (totalCustomers + totalDepots); i++)  
			{
				if (i <= (totalCustomers - 1)) 
				{
					axisXIni = customers.get(i).getLocationCustomer().getAxisX();
					axisYIni = customers.get(i).getLocationCustomer().getAxisY();
				} 
				else 
				{
					axisXIni = depots.get(lastPointOne).getLocationDepot().getAxisX();
					axisYIni = depots.get(lastPointOne).getLocationDepot().getAxisY();
					lastPointOne++;
				}

				lastPointTwo = 0;

				for (int j = 0; j < (totalCustomers + totalDepots); j++)  // eficiencia
				{
					if (j <= (totalCustomers - 1)) 
					{
						axisXEnd = customers.get(j).getLocationCustomer().getAxisX();
						axisYEnd = customers.get(j).getLocationCustomer().getAxisY();
					} 
					else 
					{
						axisXEnd = depots.get(lastPointTwo).getLocationDepot().getAxisX();
						axisYEnd = depots.get(lastPointTwo).getLocationDepot().getAxisY();
						lastPointTwo++;
					}

					if (i == j)
						costMatrix.setItem(i, j, Double.POSITIVE_INFINITY);
					else 
					{
						try {
							cost = distance.calculateDistance(axisXIni, axisYIni, axisXEnd, axisYEnd);
						} catch (Exception e) {
							e.printStackTrace();
						}
						costMatrix.setItem(i, j, cost);
						costMatrix.setItem(j, i, cost);
					}
				}
			}
		}
		return costMatrix;
	}
	
	public NumericMatrix fillCostMatrixXXX(ArrayList<Customer> customers, ArrayList<Depot> depots, DistanceType distanceType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int totalCustomers = customers.size();
		int totalDepots = depots.size();

		NumericMatrix costMatrix = new NumericMatrix((totalCustomers + totalDepots), (totalCustomers + totalDepots));
		IDistance distance = newDistance(distanceType);

		double axisXIni = 0.0;
		double axisYIni = 0.0;
		double axisXEnd = 0.0;
		double axisYEnd = 0.0;
		int lastPointOne = 0;
		int lastPointTwo = 0;
		double cost = 0.0;

		for (int i = 0; i < (totalCustomers + totalDepots); i++)  
		{
			if (i <= (totalCustomers - 1)) 
			{
				axisXIni = customers.get(i).getLocationCustomer().getAxisX();
				axisYIni = customers.get(i).getLocationCustomer().getAxisY();
			} 
			else 
			{
				axisXIni = depots.get(lastPointOne).getLocationDepot().getAxisX();
				axisYIni = depots.get(lastPointOne).getLocationDepot().getAxisY();
				lastPointOne++;
			}

			lastPointTwo = 0;

			for (int j = 0; j < (totalCustomers + totalDepots); j++)  // eficiencia
			{
				if (j <= (totalCustomers - 1)) 
				{
					axisXEnd = customers.get(j).getLocationCustomer().getAxisX();
					axisYEnd = customers.get(j).getLocationCustomer().getAxisY();
				} 
				else 
				{
					axisXEnd = depots.get(lastPointTwo).getLocationDepot().getAxisX();
					axisYEnd = depots.get(lastPointTwo).getLocationDepot().getAxisY();
					lastPointTwo++;
				}

				if (i == j)
					costMatrix.setItem(i, j, Double.NEGATIVE_INFINITY);
				else 
				{
					try {
						cost = distance.calculateDistance(axisXIni, axisYIni, axisXEnd, axisYEnd);
					} catch (Exception e) {
						e.printStackTrace();
					}
					costMatrix.setItem(i, j, cost);
					costMatrix.setItem(j, i, cost);
				}
			}
		}
		return costMatrix;
	}
	
	public NumericMatrix fillCostMatrixReal(ArrayList<Customer> customers, ArrayList<Depot> depots) throws IOException, InterruptedException, IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int totalCustomers = customers.size();
		int totalDepots = depots.size();
		NumericMatrix costMatrix = new NumericMatrix(totalCustomers + totalDepots, totalCustomers + totalDepots);
		double cost = 0.0;

		// Llenar la matriz con distancias obtenidas de la API OSRM
		for (int i = 0; i < (totalCustomers + totalDepots); i++) {
			double axisXIni = 0.0;
			double axisYIni = 0.0;

			// Obtener las coordenadas del punto inicial (cliente o depósito)
			if (i < totalCustomers) {
				axisXIni = customers.get(i).getLocationCustomer().getAxisX();
				axisYIni = customers.get(i).getLocationCustomer().getAxisY();
			} else {
				axisXIni = depots.get(i - totalCustomers).getLocationDepot().getAxisX();
				axisYIni = depots.get(i - totalCustomers).getLocationDepot().getAxisY();
			}

			for (int j = 0; j < (totalCustomers + totalDepots); j++) {
				double axisXEnd = 0.0;
				double axisYEnd = 0.0;

				// Obtener las coordenadas del punto final (cliente o depósito)
				if (j < totalCustomers) {
					axisXEnd = customers.get(j).getLocationCustomer().getAxisX();
					axisYEnd = customers.get(j).getLocationCustomer().getAxisY();
				} else {
					axisXEnd = depots.get(j - totalCustomers).getLocationDepot().getAxisX();
					axisYEnd = depots.get(j - totalCustomers).getLocationDepot().getAxisY();
				}

				// Evitar calcular la distancia de un punto consigo mismo
				if (i == j) {
					costMatrix.setItem(i, j, Double.POSITIVE_INFINITY);
				} else {
					// Llamar al servicio OSRM para obtener la distancia entre los puntos
					try {
						cost = OSRMService.calculateDistance(axisXIni, axisYIni, axisXEnd, axisYEnd);
					} catch (Exception e) {
						e.printStackTrace();
					}
					costMatrix.setItem(i, j, cost);
					costMatrix.setItem(j, i, cost); // Como la distancia es simétrica
				}
			}
		}
		return costMatrix;
	}
	
	/* Método encargado de llenar la matriz de costo usando la distancia deseada*/
	public NumericMatrix createCostMatrix(ArrayList<Customer> customers, ArrayList<Customer> depots, DistanceType distanceType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int totalCustomers = customers.size();
		int totalDepots = depots.size();

		NumericMatrix costMatrix = new NumericMatrix((totalCustomers + totalDepots), (totalCustomers + totalDepots));
		IDistance distance = newDistance(distanceType);

		double axisXIni = 0.0;
		double axisYIni = 0.0;
		double axisXEnd = 0.0;
		double axisYEnd = 0.0;
		int lastPointOne = 0;
		int lastPointTwo = 0;
		double cost = 0.0;

		for (int i = 0; i < (totalCustomers + totalDepots); i++)  
		{
			if (i <= (totalCustomers - 1)) 
			{
				axisXIni = customers.get(i).getLocationCustomer().getAxisX();
				axisYIni = customers.get(i).getLocationCustomer().getAxisY();
			} 
			else 
			{
				axisXIni = depots.get(lastPointOne).getLocationCustomer().getAxisX();
				axisYIni = depots.get(lastPointOne).getLocationCustomer().getAxisY();
				lastPointOne++;
			}

			lastPointTwo = 0;

			for (int j = 0; j < (totalCustomers + totalDepots); j++)  // eficiencia
			{
				if (j <= (totalCustomers - 1)) 
				{
					axisXEnd = customers.get(j).getLocationCustomer().getAxisX();
					axisYEnd = customers.get(j).getLocationCustomer().getAxisY();
				} 
				else 
				{
					axisXEnd = depots.get(lastPointTwo).getLocationCustomer().getAxisX();
					axisYEnd = depots.get(lastPointTwo).getLocationCustomer().getAxisY();
					lastPointTwo++;
				}

				if (i == j)
					costMatrix.setItem(i, j, Double.POSITIVE_INFINITY);
				else 
				{
					try {
						cost = distance.calculateDistance(axisXIni, axisYIni, axisXEnd, axisYEnd);
					} catch (Exception e) {
						e.printStackTrace();
					}
					costMatrix.setItem(i, j, cost);
					costMatrix.setItem(j, i, cost);
				}
			}
		}
		return costMatrix;
	}
	
	public NumericMatrix calculateCostMatrix(ArrayList<Depot> centroids, ArrayList<Depot> depots, DistanceType typeDistance) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int totalDepots = depots.size();
		NumericMatrix costMatrix = new NumericMatrix(totalDepots, totalDepots);
		IDistance distance = newDistance(typeDistance);

		double axisXPointOne = 0.0;
		double axisYPointOne = 0.0;
		double axisXPointTwo = 0.0;
		double axisYPointTwo = 0.0;
		double cost = 0.0;

		System.out.println("----------------------------------------------------");
		
		for(int i = 0; i < totalDepots; i++) 
		{
			axisXPointOne = centroids.get(i).getLocationDepot().getAxisX();
			axisYPointOne = centroids.get(i).getLocationDepot().getAxisY();

			System.out.println("CENTROIDE" + i + " X: " + axisXPointOne);
			System.out.println("CENTROIDE" + i + " Y: " + axisYPointOne);
			System.out.println("----------------------------------------------------");

			for(int j = 0; j < totalDepots; j++) 
			{
				axisXPointTwo = depots.get(j).getLocationDepot().getAxisX();
				axisYPointTwo = depots.get(j).getLocationDepot().getAxisY();
				
				System.out.println("DEPOSITO" + j + " X: " + axisXPointTwo);
				System.out.println("DEPOSITO" + j + " Y: " + axisYPointTwo);

				try {
					cost = distance.calculateDistance(axisXPointOne, axisYPointOne, axisXPointTwo, axisYPointTwo);
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("COSTO: " + cost);

				costMatrix.setItem(i, j, cost);
			}
			
			System.out.println("----------------------------------------------------");
		}
		return costMatrix;
	}
	
	public NumericMatrix calculateCostMatrixReal(ArrayList<Depot> centroids, ArrayList<Depot> depots) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int totalDepots = depots.size();
		NumericMatrix costMatrix = new NumericMatrix(totalDepots, totalDepots);

		double axisXPointOne = 0.0;
		double axisYPointOne = 0.0;
		double axisXPointTwo = 0.0;
		double axisYPointTwo = 0.0;
		double cost = 0.0;

		System.out.println("----------------------------------------------------");
		
		for(int i = 0; i < totalDepots; i++) 
		{
			axisXPointOne = centroids.get(i).getLocationDepot().getAxisX();
			axisYPointOne = centroids.get(i).getLocationDepot().getAxisY();

			System.out.println("CENTROIDE" + i + " X: " + axisXPointOne);
			System.out.println("CENTROIDE" + i + " Y: " + axisYPointOne);
			System.out.println("----------------------------------------------------");

			for(int j = 0; j < totalDepots; j++) 
			{
				axisXPointTwo = depots.get(j).getLocationDepot().getAxisX();
				axisYPointTwo = depots.get(j).getLocationDepot().getAxisY();
				
				System.out.println("DEPOSITO" + j + " X: " + axisXPointTwo);
				System.out.println("DEPOSITO" + j + " Y: " + axisYPointTwo);

				try {
					cost = OSRMService.calculateDistance(axisXPointOne, axisYPointOne, axisXPointTwo, axisYPointTwo);
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("COSTO: " + cost);

				costMatrix.setItem(i, j, cost);
			}
			
			System.out.println("----------------------------------------------------");
		}
		return costMatrix;
	}
	
	public NumericMatrix calculateCostMatrix(DistanceType typeDistance, ArrayList<Depot> medoids, ArrayList<Customer> customers) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int totalDepots = medoids.size();
		int totalCustomers = customers.size();
		
		NumericMatrix costMatrix = new NumericMatrix((totalCustomers + totalDepots), (totalCustomers + totalDepots));
		IDistance distance = newDistance(typeDistance);

		double axisXIni = 0.0;
		double axisYIni = 0.0;
		double axisXEnd = 0.0;
		double axisYEnd = 0.0;
		double cost = 0.0;

		// PORCION DE LOS DEPOSITOS O MEDOIDES A LOS CLIENTES NO TENGO DISTANCIAS DE CLIENTES A CLIENTES COMO EN EL FILL
		for(int i = 0; i < totalDepots; i++) 
		{
			axisXIni = medoids.get(i).getLocationDepot().getAxisX();
			axisYIni = medoids.get(i).getLocationDepot().getAxisY();

			for (int j = 0; j < totalCustomers; j++) 
			{
				axisXEnd = customers.get(j).getLocationCustomer().getAxisX();
				axisYEnd = customers.get(j).getLocationCustomer().getAxisY();

				try {
					cost = distance.calculateDistance(axisXIni, axisYIni, axisXEnd, axisYEnd);
				} catch (Exception e) {
					e.printStackTrace();
				}

				costMatrix.setItem(i, j, cost);
			}
		}
		return costMatrix;
	}

	/* Método encargado de crear una distancia*/
	private IDistance newDistance(DistanceType distanceType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		IFactoryDistance iFactoryDistance = new FactoryDistance();
		IDistance distance = (IDistance) iFactoryDistance.createDistance(distanceType);
		return distance;
	}

	public void cleanInfoProblem() {
		customers.clear();
		depots.clear();
		costMatrix.clear();
		problem = null;
	}
}