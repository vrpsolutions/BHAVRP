package cujae.inf.ic.om.assignment.clustering.partitional;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Location;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.Cluster;
import cujae.inf.ic.om.problem.output.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class PAM extends ByMedoids {
	private ArrayList<Integer> listIDElements;
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	
	public PAM() {
		super();
	}

	@Override
	public Solution toClustering() {
		initialize();
		assign();
		return finish();
	}
	
	@Override	
	public void initialize() {	
		listIDElements = generateElements(seedType, distanceType);
		listClusters = initializeClusters(listIDElements);		
		listCustomersToAssign = new ArrayList<Customer>();
	}	
		
	@Override	
	public void assign() {	
		ArrayList<Depot> listMedoids = new ArrayList<Depot>();
		
		boolean change = true;
		boolean first = true;
		
		while((change) && (currentIteration < countMaxIterations))
		{
			listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
			updateCustomerToAssign(listCustomersToAssign, listIDElements);
			
			if(first)
			{
				listMedoids = createCentroids(listIDElements);	
				first = false;
			}
			else
				updateClusters(listClusters, listIDElements);
			
			NumericMatrix costMatrix = initializeCostMatrix(listCustomersToAssign, listMedoids, distanceType);
			NumericMatrix costMatrixCopy = new NumericMatrix(costMatrix); 
				
			stepAssignment(listClusters, listCustomersToAssign, costMatrix);
			ArrayList<Depot> oldMedoids = replicateDepots(listMedoids);
			
			double bestCost = calculateCost(listClusters, costMatrixCopy, listMedoids);
			
			stepSearchMedoids(listClusters, listMedoids, costMatrixCopy, bestCost);
			change = verifyMedoids(oldMedoids, listMedoids); 
			 
			if((change) && (currentIteration + 1 != countMaxIterations))
			{
				listIDElements.clear();
				listIDElements = getIDMedoids(listMedoids);
				cleanClusters(listClusters);
			}
			
			currentIteration++;
			
			System.out.println("ITERACIÓN: " + currentIteration);
		}
	}	

	@Override
	public Solution finish() {
		Solution solution = new Solution();
		
		if(!listCustomersToAssign.isEmpty())					
			for(int j = 0; j < listCustomersToAssign.size(); j++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(j).getIDCustomer());

		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));
		
		OSRMService.clearDistanceCache();
		
		return solution;
	}
	
	private void stepSearchMedoids(ArrayList<Cluster> clusters, ArrayList<Depot> medoids, NumericMatrix costMatrix, double bestCost){
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
				
				newMedoid.setIDCustomer(Problem.getProblem().getCustomerByIDCustomer(newIDMedoid).getIDCustomer());
				newMedoid.setRequestCustomer(Problem.getProblem().getCustomerByIDCustomer(newIDMedoid).getRequestCustomer());	
				
				Location location = new Location();
				location.setAxisX(Problem.getProblem().getCustomerByIDCustomer(newIDMedoid).getLocationCustomer().getAxisX());
				location.setAxisY(Problem.getProblem().getCustomerByIDCustomer(newIDMedoid).getLocationCustomer().getAxisY());
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

	private double calculateCost(ArrayList<Cluster> clusters, NumericMatrix costMatrix, ArrayList<Depot> medoids) {
		double cost = 0.0;
		
		int idCustomer =  -1;
		int posCustomer = -1;
		int posDepot = -1;
		ArrayList<Integer> listIDCustomers = new ArrayList<Integer>();

		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CALCULO DEL MEJOR COSTO");
		
		for(int i = 0; i < clusters.size(); i++) 
		{			
			posDepot = Problem.getProblem().getPosElement(medoids.get(i).getIDDepot());
			listIDCustomers = clusters.get(i).getItemsOfCluster();
		
			System.out.println("-------------------------------------------------------------------------------");		
			System.out.println("ID MEDOIDE: " + medoids.get(i).getIDDepot());
			System.out.println("POSICIÓN DEL MEDOIDE: " + posDepot);
			System.out.println("CLIENTES ASIGNADOS AL MEDOIDE: " + listIDCustomers);
			System.out.println("-------------------------------------------------------------------------------");
			
			for(int j = 0; j < listIDCustomers.size(); j++) 
			{	
				idCustomer =  listIDCustomers.get(j);				
				posCustomer = Problem.getProblem().getPosElement(idCustomer);

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
}