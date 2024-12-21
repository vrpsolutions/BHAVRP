package cujae.inf.ic.om.heuristic.assignment.clustering.partitional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;
import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Fleet;
import cujae.inf.ic.om.problem.input.Location;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.solution.Cluster;

public abstract class ByCentroids extends Partitional {
	
	protected ArrayList<Integer> generateElements(DistanceType distanceType){
		
		ArrayList<Integer> idElements = new ArrayList<Integer>();
		
		int totalCustomers = Problem.getProblem().getTotalCustomers();
		int totalDepots = Problem.getProblem().getTotalDepots();
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
					costMatrix = Problem.getProblem().fillCostMatrix(Problem.getProblem().getCustomers(), listDepot, distanceType);
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
		
		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);

		while(counter > 0)
		{
			rcBestAll = costMatrix.indexBiggerValue(totalCustomers, 0, totalCustomers, (totalCustomers - 1));

			System.out.println("ROW SELECCIONADA: " + rcBestAll.getRow());
			System.out.println("COL SELECCIONADA: " + rcBestAll.getCol());
			System.out.println("VALOR SELECCIONADO: " + costMatrix.getItem(rcBestAll.getRow(), rcBestAll.getCol()));

			idElement = Problem.getProblem().getCustomers().get(rcBestAll.getCol()).getIDCustomer();	
			idElements.add(idElement);	

			System.out.println("ELEMENTO: " + idElement); 
			System.out.println("LISTADO DE ELEMENTOS ACTUALIZADOS:" + idElements);

			costMatrix.setItem(rcBestAll.getRow(), rcBestAll.getCol(), Double.NEGATIVE_INFINITY);
			counter--;
		}
		
		System.out.println("LISTADO DE ELEMENTOS SELECCIONADOS:" + idElements);
	
		return sortedElements(idElements, distanceType);
	}
	
	private ArrayList<Integer> sortedElements (ArrayList<Integer> idElements, DistanceType distanceType){
		
		int totalDepots = Problem.getProblem().getTotalDepots();
		int j = 0;
		
		ArrayList<Integer> sortedElements = new ArrayList<Integer>();
		ArrayList<Customer> customers = new ArrayList<Customer>();

		for(int i = 0; i < idElements.size(); i++)
		{
			sortedElements.add(-1);
			customers.add(Problem.getProblem().getCustomerByIDCustomer(idElements.get(i)));
		}
		
		NumericMatrix costMatrix = null;
		RowCol rcBestAll = new RowCol();
		
		switch(distanceType.ordinal())
		{
			case 0: case 1: case 2: case 3:
			{
				try {
					costMatrix = Problem.getProblem().fillCostMatrix(customers, Problem.getProblem().getDepots(), distanceType);
				} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
						| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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
	
	private Location calculateMeanCoordinate(){
		
		double AxisX = 0.0;
		double AxisY = 0.0;

		ArrayList<Location> listCoordinatesCustomers = Problem.getProblem().getListCoordinatesCustomers();

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
	
	private void updateCentroids(ArrayList<Cluster> clusters, ArrayList<Depot> centroids, DistanceType distanceType) {		
		NumericMatrix costMatrix = new NumericMatrix();
		try {
			costMatrix = Problem.getProblem().calculateCostMatrix(centroids, Problem.getProblem().getDepots(), distanceType);
		} catch (IllegalArgumentException | SecurityException
				| ClassNotFoundException | InstantiationException
				| IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
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
}