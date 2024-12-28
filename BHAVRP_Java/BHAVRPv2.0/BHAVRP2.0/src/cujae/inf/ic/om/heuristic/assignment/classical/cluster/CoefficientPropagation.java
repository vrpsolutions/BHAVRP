package cujae.inf.ic.om.heuristic.assignment.classical.cluster;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;
import cujae.inf.ic.om.service.OSRMService;

import cujae.inf.ic.om.heuristic.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

public class CoefficientPropagation extends ByNotUrgency {

	public static double degradationCoefficient = 0.5;

	private Solution solution = new Solution();	
	
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	private NumericMatrix costMatrix;
	private ArrayList<Double> listCoefficients;
	private ArrayList<ArrayList<Double>> listScaledDistances;
	private NumericMatrix scaledMatrix; 

	public CoefficientPropagation() {
		super();
	}

	@Override
	public Solution toClustering() {
	
		if(degradationCoefficient > 1 || degradationCoefficient < 0)
			degradationCoefficient = 0.5;
		
		initialize();
		assign();
		return finish();
	}	
		
	@Override
	public void initialize() {
		listClusters = initializeClusters();
		listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		costMatrix = initializeCostMatrix(Problem.getProblem().getCustomers(), Problem.getProblem().getDepots(), distanceType);
		listCoefficients = initializeCoefficients();
		listScaledDistances = new ArrayList<ArrayList<Double>>(fillListScaledDistances());
		scaledMatrix = initializeScaledMatrix(listScaledDistances); 
	}

	@Override
	public void assign() {
		int posCustomer = -1;
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		int posDepot = -1;
		int idDepot = -1;
		double capacityDepot = 0.0;
		
		int posCluster = -1;
		double requestCluster = 0.0;
		
		int posElement = -1;
		RowCol rcBestAll = new RowCol();
		
		int totalItems = listCustomersToAssign.size();
		int totalClusters = Problem.getProblem().getDepots().size();
		
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()) && (!scaledMatrix.fullMatrix(Double.POSITIVE_INFINITY)))
		{
			rcBestAll = scaledMatrix.indexLowerValue();
			
			posCustomer = rcBestAll.getCol(); // puede devolver un deposito?
			idCustomer = Problem.getProblem().getListIDCustomers().get(posCustomer);
			requestCustomer = Problem.getProblem().getRequestByIDCustomer(idCustomer);

			posElement = rcBestAll.getRow();

			if(posElement >= totalItems)	
			{
				idDepot =  Problem.getProblem().getListIDDepots().get((posElement - totalItems)).intValue(); 
				
				posCluster = findCluster(idDepot, listClusters);
				posDepot = posElement;
			}
			else
			{
				posCluster = getPosCluster(posElement, listClusters);
				
				idDepot = listClusters.get(posCluster).getIDCluster();
				posDepot = Problem.getProblem().getPosElement(idDepot);
			}
			
			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();
				capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idDepot));

				if(capacityDepot >= (requestCluster + requestCustomer))
				{
					requestCluster += requestCustomer;
				
					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					calculateAttractionCoefficient(posCustomer, posElement, listCoefficients);
					scaledMatrix.fillValue(0, posCustomer, (totalItems + totalClusters - 1), posCustomer, Double.POSITIVE_INFINITY);

					listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
					updateScaledMatrix(listCustomersToAssign, posCustomer, scaledMatrix, listCoefficients);
				
					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
					{
						scaledMatrix.fillValue((posDepot), 0, (posDepot), (totalItems + totalClusters - 1), Double.POSITIVE_INFINITY);
						
						for(int i = 0; i < listClusters.get(posCluster).getItemsOfCluster().size(); i++)
						{
							int posElement1 = Problem.getProblem().getPosElement(listClusters.get(posCluster).getItemsOfCluster().get(i));
							scaledMatrix.fillValue(posElement1, 0, posElement1, (totalItems + totalClusters - 1), Double.POSITIVE_INFINITY);	
						}
						
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);
					}
				}
				else
				{	if(scaledMatrix.fullMatrix(0, posCustomer, totalItems + totalClusters - 1, posCustomer, Double.POSITIVE_INFINITY))
					{
						solution.getUnassignedItems().add(idCustomer);
						listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
					}
				
					scaledMatrix.setItem(posElement, posCustomer, Double.POSITIVE_INFINITY);
				}
			}
		}
	}
	
	@Override
	public Solution finish() {
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

	/*Este método calcula el coeficiente de atracción dados, el identificador del cliente asignado, y el cliente por el que se asignó*/
	private void calculateAttractionCoefficient(int posCustomer, int posElement, ArrayList<Double> coefficients){
		double currentAttCoeff = 1.0;
		double newAttCoeff = 1.0;

		if(posElement < coefficients.size())
		{
			currentAttCoeff = coefficients.get(posElement);
			newAttCoeff = Math.min(1, (currentAttCoeff + (currentAttCoeff * degradationCoefficient)));
		}
		else
			newAttCoeff = 1;
		
		coefficients.set(posCustomer, newAttCoeff);
	}
	
	/*Este método inicializa la lista de coeficientes de atracción de los clientes a 0. La lista que devuelve es paralela a la lista de clientes*/
	private ArrayList<Double> initializeCoefficients(){
		ArrayList<Double> coefficients = new ArrayList<Double>();

		int totalItems = Problem.getProblem().getTotalCustomers();

		for(int i = 0; i < totalItems; i++)
			coefficients.add(0.0);

		return coefficients;
	}
	
	/*Este método se encarga de llenar una lista de listas con las distancias escaladas, para luego crear la NumericMatrix con estos datos*/
	private ArrayList<ArrayList<Double>> fillListScaledDistances(){
		ArrayList<ArrayList<Double>> scaledDistances = new ArrayList<ArrayList<Double>>();

		ArrayList<Integer> listIDCustomers = new ArrayList<Integer>(Problem.getProblem().getListIDCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(Problem.getProblem().getListIDDepots());

		int totalItems = listIDCustomers.size();
		int totalClusters = listIDDepots.size();

		for(int i = 0; i < totalItems; i++)
		{	  
			ArrayList<Double> listDistances = new ArrayList<Double>();

			for(int j = 0; j < (totalItems + totalClusters); j++)
				listDistances.add(Double.POSITIVE_INFINITY);

			scaledDistances.add(listDistances);
		}
		int posCustomerMatrix;
		int posDepotMatrix;

		for(int i = 0; i < totalClusters; i++)
		{
			ArrayList<Double> listDistances = new ArrayList<Double>();
			posDepotMatrix = Problem.getProblem().getPosElement(listIDDepots.get(i));

			for(int j = 0; j < totalItems; j++)
			{
				posCustomerMatrix = Problem.getProblem().getPosElement(listIDCustomers.get(j));
				listDistances.add(costMatrix.getItem(posCustomerMatrix, posDepotMatrix));//ahora viendolo de nuevo, no estoy segura si es asi o fila posDepotMatrix, y la columna la del cliente
			}
			for(int k = totalItems; k < (totalItems + totalClusters); k++)
				listDistances.add(Double.POSITIVE_INFINITY);

			scaledDistances.add(listDistances);
		}

		return scaledDistances;
	}
	
	/* Método encargado de llenar la matriz de costo usando listas de distancias*/
	private NumericMatrix initializeScaledMatrix(ArrayList<ArrayList<Double>> scaledDistances) {
		int size = scaledDistances.size(); 
		NumericMatrix scaledMatrix = new NumericMatrix(size, size);

		int row = -1;
		int col = -1;
		double costInDistance = 0.0;

		for(int i = 0; i < scaledDistances.size(); i++)
		{
			row = i;

			for(int j = 0; j < scaledDistances.get(i).size(); j++)
			{
				col = j;	
				costInDistance = scaledDistances.get(i).get(j);
				scaledMatrix.setItem(row, col, costInDistance);	
			}
		}
		return scaledMatrix;
	}
	
	/*Este método se encarga de actualizar la matriz de distancias escaladas, dado el nuevo cliente asignado, y los clientes que quedan por asignar*/
	private void updateScaledMatrix(ArrayList<Customer> customersToAssign, int posCustomer, NumericMatrix scaledMatrix, ArrayList<Double> coefficients) {
		int posNewCustomer = -1;
		double scaledDistance = 0.0;
		double distance = 0.0;	

		for(int i = 0; i < customersToAssign.size(); i++)
		{
			posNewCustomer = Problem.getProblem().getPosElement(customersToAssign.get(i).getIDCustomer());
			distance = costMatrix.getItem(posNewCustomer, posCustomer);
			scaledDistance = distance * coefficients.get(posCustomer);

			scaledMatrix.setItem(posCustomer, posNewCustomer, scaledDistance);
		}
	}
	
	/*Este método devuelve la posición del cluster (en la lista de clusters) al que debe asignarse el cliente que se está analizando, dado el identificador del elemento por el que se va a asignar dicho cliente*/
	private int getPosCluster(int posCustomer, ArrayList<Cluster> clusters){
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
}