package cujae.inf.citi.om.heuristic.assignment.clustering.hierarchical;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;

import cujae.inf.citi.om.factory.interfaces.DistanceType;
import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.Depot;
import cujae.inf.citi.om.problem.input.InfoProblem;
import cujae.inf.citi.om.problem.input.Location;

public class UPGMC extends Hierarchical{

	public static DistanceType distanceType = DistanceType.Euclidean;

	public UPGMC() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Solution toClustering() {
		Solution solution = new Solution();	

		ArrayList<Integer> listIDElements = InfoProblem.getProblem().getListIDElements();
		ArrayList<Cluster> listClusters = initializeClusters(listIDElements);

		NumericMatrix costMatrix = new NumericMatrix();

		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		ArrayList<Depot> listDepots = new ArrayList<Depot>(InfoProblem.getProblem().getDepots());
		
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
			
			System.out.println("currentDepots" + currentDepots);
			System.out.println("currentCustomers" + currentCustomers);
			
			//System.out.println("currentCustomers" + InfoProblem.getProblem().getCustomers().size());

			if(change)
			{
				try {
					costMatrix = InfoProblem.getProblem().fillCostMatrix(listCustomersToAssign, listDepots, distanceType);
				} catch (IllegalArgumentException | SecurityException
						| ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					// TODO Auto-generated catch block
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

				System.out.println("idCustomerOne" + idCustomerOne);
				
				posCustomerTwo = posRow;
				idCustomerTwo = listCustomersToAssign.get(posCustomerTwo).getIDCustomer();				

				System.out.println("idCustomerTwo" + idCustomerTwo);
				
				posClusterOne = findCluster(idCustomerOne, listClusters);	
				posClusterTwo = findCluster(idCustomerTwo, listClusters);
				
				System.out.println("posClusterOne" + posClusterOne);
				System.out.println("posClusterTwo" + posClusterTwo);

				if((posClusterOne != -1) && (posClusterTwo != -1)) 
				{
					requestClusterOne = listClusters.get(posClusterOne).getRequestCluster();
					requestClusterTwo = listClusters.get(posClusterTwo).getRequestCluster();
					totalRequest = requestClusterOne + requestClusterTwo;
					
					System.out.println("requestClusterOne" + requestClusterOne);
					System.out.println("requestClusterTwo" + requestClusterTwo);
					System.out.println("totalRequest" + totalRequest);
					
					idDepotWithMU = getIDClusterWithMU(listDepots, listClusters);
					posDepot = InfoProblem.getProblem().findPosDepot(listDepots, idDepotWithMU);
					capacityDepotWithMU = InfoProblem.getProblem().getTotalCapacityByDepot(listDepots.get(posDepot));
					posCluster = findCluster(idDepotWithMU, listClusters);
					requestCluster = listClusters.get(posCluster).getRequestCluster();

					System.out.println("idDepotWithMU" + idDepotWithMU);
					System.out.println("posDepot" + posDepot);
					System.out.println("capacityDepotWithMU" + capacityDepotWithMU);
					System.out.println("posCluster" + posCluster);
					System.out.println("requestCluster" + requestCluster);
					
					if(capacityDepotWithMU >= (requestCluster + totalRequest)) 
					{
						listClusters.get(posClusterOne).setRequestCluster(totalRequest);

						for (int i = 0; i < listClusters.get(posClusterTwo).getItemsOfCluster().size(); i++)
							listClusters.get(posClusterOne).getItemsOfCluster().add(listClusters.get(posClusterTwo).getItemsOfCluster().get(i));			

						newLocation = recalculateCentroid(listClusters.get(posClusterOne));
						System.out.println("newLocationX" + newLocation.getAxisX());
						System.out.println("newLocationY" + newLocation.getAxisY());
						/*newLocation = recalculatePonderado(listClusters.get(posClusterOne), listClusters.get(posClusterTwo), listCustomersToAssign);
						System.out.println("newLocationX" + newLocation.getAxisX());
						System.out.println("newLocationY" + newLocation.getAxisY());*/
						/*newLocation = recalculateTest(listClusters.get(posClusterOne), listClusters.get(posClusterTwo), listCustomersToAssign);
						System.out.println("newLocationX" + newLocation.getAxisX());
						System.out.println("newLocationY" + newLocation.getAxisY());*/
						posCustomerOne = InfoProblem.getProblem().findPosCustomer(listCustomersToAssign, idCustomerOne);
						listCustomersToAssign.get(posCustomerOne).setLocationCustomer(newLocation);
						
						System.out.println("newLocationX" + newLocation.getAxisX());
						System.out.println("newLocationY" + newLocation.getAxisY());
						System.out.println("posCustomerOne" + posCustomerOne);
						
						listClusters.remove(posClusterTwo);
						posCustomerTwo = InfoProblem.getProblem().findPosCustomer(listCustomersToAssign, idCustomerTwo);
						listCustomersToAssign.remove(posCustomerTwo);
						
						System.out.println("listClusters" + listClusters.size());
						System.out.println("posCustomerTwo" + posCustomerTwo);
						System.out.println("listCustomersToAssign" + listCustomersToAssign.size());
	
						change = true;
					}
					else
					{
						costMatrix.setItem(rcBestAll.getRow(), rcBestAll.getCol(), Double.POSITIVE_INFINITY);
						change = false;
						
						/*System.out.println();
						System.out.println("IDClusterOne" + listClusters.get(posClusterOne).getIDCluster());
						System.out.println("ElementClusterOne" + listClusters.get(posClusterOne).getItemsOfCluster().toString());
						System.out.println("requestClusterOne" + listClusters.get(posClusterTwo).getRequestCluster());
						System.out.println("IDClusterTwo" + listClusters.get(posClusterTwo).getIDCluster());
						System.out.println("ElementClusterTwo" + listClusters.get(posClusterOne).getItemsOfCluster().toString());
						System.out.println("requestClusterTwo" + listClusters.get(posClusterTwo).getRequestCluster());
						*/
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
					
					System.out.println("posCustomerOne" + posCustomerOne);
					System.out.println("posDepotMatrix" + posDepotMatrix);

					idCustomerOne = listCustomersToAssign.get(posCustomerOne).getIDCustomer();			
					posClusterOne = findCluster(idCustomerOne, listClusters);
					
					System.out.println("idCustomerOne" + idCustomerOne);
					System.out.println("posClusterOne" + posClusterOne);

					posDepot = (posDepotMatrix - currentCustomers); 
					idDepot = listDepots.get(posDepot).getIDDepot();
					capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(listDepots.get(posDepot));
					posCluster = findCluster(idDepot, listClusters);

					System.out.println("posDepot" + posDepot);
					System.out.println("idDepot" + idDepot);
					System.out.println("capacityDepot" + capacityDepot);
					System.out.println("posCluster" + posCluster);
					
					if((posClusterOne != -1) && (posCluster != -1))
					{
						requestClusterOne = listClusters.get(posClusterOne).getRequestCluster();
						requestCluster = listClusters.get(posCluster).getRequestCluster();

						System.out.println("requestClusterOne" + requestClusterOne);
						System.out.println("requestCluster" + requestCluster);
						
						if(capacityDepot >= (requestCluster + requestClusterOne)) 
						{
							requestCluster += requestClusterOne;
							listClusters.get(posCluster).setRequestCluster(requestCluster);

							for (int i = 0; i < listClusters.get(posClusterOne).getItemsOfCluster().size(); i++)
								listClusters.get(posCluster).getItemsOfCluster().add(listClusters.get(posClusterOne).getItemsOfCluster().get(i));
					
							listCustomersToAssign.remove(InfoProblem.getProblem().findPosCustomer(listCustomersToAssign, idCustomerOne));
							
							listClusters.remove(posClusterOne);
							change = true;
						}
						else
						{
							costMatrix.setItem(rcBestAll.getRow(), rcBestAll.getCol(), Double.POSITIVE_INFINITY);
							change = false;	
							
						/*	System.out.println();
							System.out.println("IDClusterOne" + listClusters.get(posClusterOne).getIDCluster());
							System.out.println("ElementClusterOne" + listClusters.get(posClusterOne).getItemsOfCluster().toString());
							System.out.println("requestClusterOne" + listClusters.get(posClusterTwo).getRequestCluster());*/
						}
						
						//PENDIENTE ENTRADA CON LISTCUSTOMER VACIA EN EL ISFULLDEPOT TRADICIONA
						if(isFullDepot(listClusters, requestCluster, capacityDepot, listCustomersToAssign.size()))
						{
							posCluster = findCluster(idDepot, listClusters);
							System.out.println("posCluster" + posCluster);
							
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
		
		finish(listClusters, solution);
		
		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));

		return solution;
	}
	
	/*Método encargado de obtener el id del deposito con mayor capacidad de la lista*/
	private int getIDClusterWithMU(ArrayList<Depot> depots, ArrayList<Cluster> clusters){
		int idDepotMU = depots.get(0).getIDDepot();
		int posCluster = findCluster(idDepotMU, clusters);
		double requestCluster = clusters.get(posCluster).getRequestCluster();
		double maxCapacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(depots.get(0)); 
		maxCapacityDepot -= requestCluster;
		int totalDepots = depots.size();

		double currentCapacityDepot; 

		for(int i = 1; i < totalDepots; i++)
		{
			posCluster = findCluster(depots.get(i).getIDDepot(), clusters);
			requestCluster = clusters.get(posCluster).getRequestCluster();
			currentCapacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(depots.get(i));
			currentCapacityDepot -= requestCluster;
			
			if(maxCapacityDepot < currentCapacityDepot)
			{
				maxCapacityDepot = currentCapacityDepot;
				idDepotMU = depots.get(i).getIDDepot(); 
			}
		}

		return idDepotMU;
	}
	
	private boolean isFullDepot(ArrayList<Cluster> clusters, double requestCluster, double capacityDepot, int currentCustomer){
		boolean isFull = true;

		double currentRequest = capacityDepot - requestCluster;

		if(currentRequest > 0)
		{
			int i = 0;

			while(((i < clusters.size()) && (i < currentCustomer)) && (isFull))
			{
				if(clusters.get(i).getRequestCluster() <= currentRequest)
					isFull = false;
				else
					i++;
			}
		}	

		return isFull;
	}
	
	private void finish(ArrayList<Cluster> clusters, Solution solution){
		int posElement = -1;

		for(int i = 0; i < clusters.size(); i++)
		{
			System.out.println();
			System.out.println("IDCluster" + clusters.get(i).getIDCluster());
			System.out.println("posCluster" + clusters.get(i).getItemsOfCluster().toString());
			System.out.println("requestCluster" + clusters.get(i).getRequestCluster());
			
			posElement = InfoProblem.getProblem().findPosCustomer(InfoProblem.getProblem().getCustomers(), clusters.get(i).getIDCluster());

			if(posElement != -1)
			{
				for(int j = 0; j < clusters.get(i).getItemsOfCluster().size(); j++)
					solution.getUnassignedItems().add(clusters.get(i).getItemsOfCluster().get(j).intValue());

				clusters.remove(i);
			}
		}
	}
}
