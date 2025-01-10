package cujae.inf.ic.om.assignment.clustering.hierarchical;

import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Location;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.Cluster;
import cujae.inf.ic.om.problem.output.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

public class UPGMC extends Hierarchical {

	public static DistanceType distanceType = DistanceType.Manhattan;
	private static Solution solution = new Solution();
	
	private ArrayList<Integer> listIDElements;
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	private ArrayList<Depot> listDepots;
	private NumericMatrix costMatrix;
	
	public UPGMC() {
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
		listIDElements = Problem.getProblem().getListIDElements();
		listClusters = initializeClusters(listIDElements);
		listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		listDepots = new ArrayList<Depot>(Problem.getProblem().getDepots());
	}
	
	@Override
	public void assign() {
		int totalDepots = listDepots.size();
		
		int currentDepots =  -1;
		int currentCustomers = -1;

		int posCol = -1;
		int posRow = -1;

		int idCustomerOne = -1;
		int posCustomerOne = -1;
		int idCustomerTwo = -1;
		int posCustomerTwo = -1;

		int posClusterOne = -1;
		double requestClusterOne = 0.0;
		int posClusterTwo = -1;
		double requestClusterTwo = 0.0;

		int idDepot = -1;
		int posDepot = -1;		
		int posDepotMatrix = -1;
		double capacityDepot = 0.0;

		int posCluster = -1;
		double requestCluster = 0.0;
		double totalRequest = 0.0;

		int idDepotWithMU = -1;
		double capacityDepotWithMU = 0.0;
		RowCol rcBestAll = new RowCol();
		Location newLocation = new Location();
		
		boolean change = true;
		
		while(!(listCustomersToAssign.isEmpty()) && (totalDepots > 0))
		{
			currentDepots = listDepots.size();
			currentCustomers = listCustomersToAssign.size();

			System.out.println("Current Depots: " + currentDepots);
			System.out.println("Current Customers: " + currentCustomers);
			
			if(change)
			{
				try {
					costMatrix = initializeCostMatrix(listCustomersToAssign, listDepots, distanceType);
				} catch (IllegalArgumentException | SecurityException e) {
					e.printStackTrace();
				}
			}
			
			rcBestAll = costMatrix.indexLowerValue(0, 0, (currentCustomers + currentDepots - 1), (currentCustomers - 1));
			
			System.out.println("rcBestAll " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));
			System.out.println("bestAllRow" + rcBestAll.getRow());
			System.out.println("bestAllCol" + rcBestAll.getCol());
			
			posCol = rcBestAll.getCol();
			posRow = rcBestAll.getRow();

			if((posCol < currentCustomers) && (posRow < currentCustomers)) 
			{ 
				posCustomerOne = posCol;
				idCustomerOne = listCustomersToAssign.get(posCustomerOne).getIDCustomer();

				System.out.println("--------------------------------------");
				System.out.println("ID Customer One: " + idCustomerOne);
				
				posCustomerTwo = posRow;
				idCustomerTwo = listCustomersToAssign.get(posCustomerTwo).getIDCustomer();				

				System.out.println("idCustomerTwo" + idCustomerTwo);
				
				posClusterOne = findCluster(idCustomerOne, listClusters);	
				posClusterTwo = findCluster(idCustomerTwo, listClusters);
				
				System.out.println("--------------------------------------");
				System.out.println("Position Cluster One: " + posClusterOne);
				System.out.println("Position Cluster Two: " + posClusterTwo);
				
				if((posClusterOne != -1) && (posClusterTwo != -1)) 
				{
					requestClusterOne = listClusters.get(posClusterOne).getRequestCluster();
					requestClusterTwo = listClusters.get(posClusterTwo).getRequestCluster();
					totalRequest = requestClusterOne + requestClusterTwo;
					
					System.out.println("--------------------------------------");
					System.out.println("Request Cluster One: " + requestClusterOne);
					System.out.println("Request Cluster Two: " + requestClusterTwo);
					System.out.println("Total Request: " + totalRequest);
					
					idDepotWithMU = getIDClusterWithMU(listDepots, listClusters);
					posDepot = Problem.getProblem().findPosDepot(listDepots, idDepotWithMU);
					capacityDepotWithMU = Problem.getProblem().getTotalCapacityByDepot(listDepots.get(posDepot));
					posCluster = findCluster(idDepotWithMU, listClusters);
					requestCluster = listClusters.get(posCluster).getRequestCluster();

					System.out.println("--------------------------------------");
					System.out.println("ID Depot With MU: " + idDepotWithMU);
					System.out.println("Position Depot: " + posDepot);
					System.out.println("Capacity Depot With MU: " + capacityDepotWithMU);
					System.out.println("Position Cluster: " + posCluster);
					System.out.println("Request Cluster: " + requestCluster);
					
					if(capacityDepotWithMU >= (requestCluster + totalRequest)) 
					{
						listClusters.get(posClusterOne).setRequestCluster(totalRequest);

						for (int i = 0; i < listClusters.get(posClusterTwo).getItemsOfCluster().size(); i++)
							listClusters.get(posClusterOne).getItemsOfCluster().add(listClusters.get(posClusterTwo).getItemsOfCluster().get(i));			

						newLocation = recalculateCentroid(listClusters.get(posClusterOne));
						
						System.out.println("--------------------------------------");
						System.out.println("New Location:");
						System.out.println("Axis X: " + newLocation.getAxisX());
						System.out.println("Axis Y: " + newLocation.getAxisY());
						
						posCustomerOne = Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomerOne);
						listCustomersToAssign.get(posCustomerOne).setLocationCustomer(newLocation);
						
						System.out.println("--------------------------------------");
						System.out.println("New Location:");
						System.out.println("Axis X: " + newLocation.getAxisX());
						System.out.println("Axis Y:" + newLocation.getAxisY());
						System.out.println("Position Customer One: " + posCustomerOne);
						
						listClusters.remove(posClusterTwo);
						posCustomerTwo = Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomerTwo);
						listCustomersToAssign.remove(posCustomerTwo);
						
						System.out.println("--------------------------------------");
						System.out.println("List Clusters: " + listClusters.size());
						System.out.println("Position Customer Two: " + posCustomerTwo);
						System.out.println("List Customers To Assign: " + listCustomersToAssign.size());
						
						change = true;
					}
					else
					{
						costMatrix.setItem(rcBestAll.getRow(), rcBestAll.getCol(), Double.POSITIVE_INFINITY);
						change = false;
					}
				}	
			}
			else
			{				
				if(((posCol < currentCustomers) && (posRow >= currentCustomers)) || ((posRow < currentCustomers) && (posCol >= currentCustomers)))
				{
					if(posCol < currentCustomers)
					{
						posCustomerOne = rcBestAll.getCol();
						posDepotMatrix = rcBestAll.getRow();
					}
					else
					{
						posCustomerOne = rcBestAll.getRow();
						posDepotMatrix = rcBestAll.getCol();
					}
					
					System.out.println("--------------------------------------");
					System.out.println("Position Customer One: " + posCustomerOne);
					System.out.println("Position Depot Matrix: " + posDepotMatrix);

					idCustomerOne = listCustomersToAssign.get(posCustomerOne).getIDCustomer();			
					posClusterOne = findCluster(idCustomerOne, listClusters);
					
					System.out.println("--------------------------------------");
					System.out.println("ID Customer One: " + idCustomerOne);
					System.out.println("Position Cluster One: " + posClusterOne);

					posDepot = (posDepotMatrix - currentCustomers); 
					idDepot = listDepots.get(posDepot).getIDDepot();
					capacityDepot = Problem.getProblem().getTotalCapacityByDepot(listDepots.get(posDepot));
					posCluster = findCluster(idDepot, listClusters);

					System.out.println("--------------------------------------");
					System.out.println("Position Depot: " + posDepot);
					System.out.println("ID Depot: " + idDepot);
					System.out.println("Capacity Depot: " + capacityDepot);
					System.out.println("Position Cluster: " + posCluster);
					
					if((posClusterOne != -1) && (posCluster != -1))
					{
						requestClusterOne = listClusters.get(posClusterOne).getRequestCluster();
						requestCluster = listClusters.get(posCluster).getRequestCluster();

						System.out.println("--------------------------------------");
						System.out.println("Request Cluster One: " + requestClusterOne);
						System.out.println("Request Cluster: " + requestCluster);
						
						if(capacityDepot >= (requestCluster + requestClusterOne)) 
						{
							requestCluster += requestClusterOne;
							listClusters.get(posCluster).setRequestCluster(requestCluster);

							for (int i = 0; i < listClusters.get(posClusterOne).getItemsOfCluster().size(); i++)
								listClusters.get(posCluster).getItemsOfCluster().add(listClusters.get(posClusterOne).getItemsOfCluster().get(i));
					
							listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomerOne));
							
							listClusters.remove(posClusterOne);
							change = true;
						}
						else
						{
							costMatrix.setItem(rcBestAll.getRow(), rcBestAll.getCol(), Double.POSITIVE_INFINITY);
							change = false;		
						}
						
						//PENDIENTE ENTRADA CON LISTCUSTOMER VACIA EN EL ISFULLDEPOT TRADICIONAL
						if(isFullDepot(listClusters, requestCluster, capacityDepot, listCustomersToAssign.size()))
						{
							posCluster = findCluster(idDepot, listClusters);
							
							System.out.println("--------------------------------------");
							System.out.println("Position Cluster: " + posCluster);
							
							if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
								solution.getClusters().add(listClusters.remove(posCluster));
							else
								listClusters.remove(posCluster);

							listDepots.remove(posDepot);
							totalDepots--;
							change = true;
						}
					}
				}
			}
		}
	}
	
	@Override
	public Solution finish() {	
		finish(listClusters, solution);
		
		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));

		OSRMService.clearDistanceCache();
		
		return solution;
	}
	
	/*Método encargado de obtener el id del deposito con mayor capacidad de la lista*/
	private int getIDClusterWithMU(ArrayList<Depot> depots, ArrayList<Cluster> clusters) {
		int idDepotMU = depots.get(0).getIDDepot();
		int posCluster = findCluster(idDepotMU, clusters);
		double requestCluster = clusters.get(posCluster).getRequestCluster();
		double maxCapacityDepot = Problem.getProblem().getTotalCapacityByDepot(depots.get(0)); 
		maxCapacityDepot -= requestCluster;
		int totalDepots = depots.size();

		double currentCapacityDepot; 

		for(int i = 1; i < totalDepots; i++)
		{
			posCluster = findCluster(depots.get(i).getIDDepot(), clusters);
			requestCluster = clusters.get(posCluster).getRequestCluster();
			currentCapacityDepot = Problem.getProblem().getTotalCapacityByDepot(depots.get(i));
			currentCapacityDepot -= requestCluster;
			
			if(maxCapacityDepot < currentCapacityDepot)
			{
				maxCapacityDepot = currentCapacityDepot;
				idDepotMU = depots.get(i).getIDDepot(); 
			}
		}
		return idDepotMU;
	}
	
	private void finish(ArrayList<Cluster> clusters, Solution solution){
		int posElement = -1;

		for(int i = 0; i < clusters.size(); i++)
		{
			System.out.println();
			System.out.println("--------------------------------------");
			System.out.println("ID Cluster: " + clusters.get(i).getIDCluster());
			System.out.println("Position Cluster: " + clusters.get(i).getItemsOfCluster().toString());
			System.out.println("Request Cluster: " + clusters.get(i).getRequestCluster());
			
			posElement = Problem.getProblem().findPosCustomer(Problem.getProblem().getCustomers(), clusters.get(i).getIDCluster());

			if(posElement != -1)
			{
				for(int j = 0; j < clusters.get(i).getItemsOfCluster().size(); j++)
					solution.getUnassignedItems().add(clusters.get(i).getItemsOfCluster().get(j).intValue());

				clusters.remove(i);
			}
		}
	}
}