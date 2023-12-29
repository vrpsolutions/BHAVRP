package cujae.inf.citi.om.heuristic.assignment.classical.cluster;

import java.util.ArrayList;

import cujae.inf.citi.om.heuristic.output.Cluster;
import cujae.inf.citi.om.heuristic.output.Solution;
import cujae.inf.citi.om.problem.input.Customer;
import cujae.inf.citi.om.problem.input.InfoProblem;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;
import libmatrix.cujae.inf.citi.om.matrix.RowCol;

public class CoefficientPropagation extends ByCluster{

	public static double degradationCoefficient = 0.5;

	public CoefficientPropagation() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Solution toClustering() {
	
		if(degradationCoefficient > 1 || degradationCoefficient < 0)
			degradationCoefficient = 0.5;

		Solution solution = new Solution();		
		
		ArrayList<Cluster> listClusters = initializeClusters();
		ArrayList<Double> listCoefficients = initializeCoefficients();

		ArrayList<ArrayList<Double>> listScaledDistances = new ArrayList<ArrayList<Double>>(fillListScaledDistances());
		NumericMatrix scaledMatrix = initializeScaledMatrix(listScaledDistances); 

		ArrayList<Customer> listCustomersToAssign = new ArrayList<Customer>(InfoProblem.getProblem().getCustomers());
		
		int totalItems = listCustomersToAssign.size();
		int totalClusters = InfoProblem.getProblem().getDepots().size();

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

		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()) && (!scaledMatrix.fullMatrix(Double.POSITIVE_INFINITY)))
		{
			rcBestAll = scaledMatrix.indexLowerValue();
			
			posCustomer = rcBestAll.getCol(); // puede devolver un deposito?
			idCustomer = InfoProblem.getProblem().getListIDCustomers().get(posCustomer);
			requestCustomer = InfoProblem.getProblem().getRequestByIDCustomer(idCustomer);

			posElement = rcBestAll.getRow();

			if(posElement >= totalItems)	
			{
				idDepot =  InfoProblem.getProblem().getListIDDepots().get((posElement - totalItems)).intValue(); 
				
				posCluster = findCluster(idDepot, listClusters);
				posDepot = posElement;
			}
			else
			{
				posCluster = getPosCluster(posElement, listClusters);
				
				idDepot = listClusters.get(posCluster).getIDCluster();
				posDepot = InfoProblem.getProblem().getPosElement(idDepot);
			}
			
			if(posCluster != -1)
			{
				requestCluster = listClusters.get(posCluster).getRequestCluster();
				capacityDepot = InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepotByIDDepot(idDepot));

				if(capacityDepot >= (requestCluster + requestCustomer))
				{
					requestCluster += requestCustomer;
				
					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					calculateAttractionCoefficient(posCustomer, posElement, listCoefficients);
					scaledMatrix.fillValue(0, posCustomer, (totalItems + totalClusters - 1), posCustomer, Double.POSITIVE_INFINITY);

					listCustomersToAssign.remove(InfoProblem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
					updateScaledMatrix(listCustomersToAssign, posCustomer, scaledMatrix, listCoefficients);
				
					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
					{
						scaledMatrix.fillValue((posDepot), 0, (posDepot), (totalItems + totalClusters - 1), Double.POSITIVE_INFINITY);
						
						for(int i = 0; i < listClusters.get(posCluster).getItemsOfCluster().size(); i++)
						{
							int posElement1 = InfoProblem.getProblem().getPosElement(listClusters.get(posCluster).getItemsOfCluster().get(i));
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
						listCustomersToAssign.remove(InfoProblem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
					}
				
					scaledMatrix.setItem(posElement, posCustomer, Double.POSITIVE_INFINITY);
				}
			}
		}

		if(!listCustomersToAssign.isEmpty())					
			for(int j = 0; j < listCustomersToAssign.size(); j++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(j).getIDCustomer());

		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));
		
		return solution;
	}


	/*Este m�todo calcula el coeficiente de atracci�n dados, el identificador del cliente asignado, y el cliente por el que se asign�*/
	public void calculateAttractionCoefficient(int posCustomer, int posElement, ArrayList<Double> coefficients){
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
	
	/*Este m�todo inicializa la lista de coeficientes de atracci�n de los clientes a 0. La lista que devuelve es paralela a la lista de clientes*/
	private ArrayList<Double> initializeCoefficients(){
		ArrayList<Double> coefficients = new ArrayList<Double>();

		int totalItems = InfoProblem.getProblem().getTotalCustomers();

		for(int i = 0; i < totalItems; i++)
			coefficients.add(0.0);

		return coefficients;
	}
	
	/*Este m�todo se encarga de llenar una lista de listas con las distancias escaladas, para luego crear la NumericMatrix con estos datos*/
	private ArrayList<ArrayList<Double>> fillListScaledDistances(){
		ArrayList<ArrayList<Double>> scaledDistances = new ArrayList<ArrayList<Double>>();

		NumericMatrix costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		ArrayList<Integer> listIDCustomers = new ArrayList<Integer>(InfoProblem.getProblem().getListIDCustomers());
		ArrayList<Integer> listIDDepots = new ArrayList<Integer>(InfoProblem.getProblem().getListIDDepots());

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
			posDepotMatrix = InfoProblem.getProblem().getPosElement(listIDDepots.get(i));

			for(int j = 0; j < totalItems; j++)
			{
				posCustomerMatrix = InfoProblem.getProblem().getPosElement(listIDCustomers.get(j));
				listDistances.add(costMatrix.getItem(posCustomerMatrix, posDepotMatrix));//ahora viendolo de nuevo, no estoy segura si es asi o fila posDepotMatrix, y la columna la del cliente
			}

			for(int k = totalItems; k < (totalItems + totalClusters); k++)
				listDistances.add(Double.POSITIVE_INFINITY);

			scaledDistances.add(listDistances);
		}

		return scaledDistances;
	}
	
	/* M�todo encargado de llenar la matriz de costo usando listas de distancias*/
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
	
	/*Este m�todo se encarga de actualizar la matriz de distancias escaladas, dado el nuevo cliente asignado, y los clientes que quedan por asignar*/
	private void updateScaledMatrix(ArrayList<Customer> customersToAssign, int posCustomer, NumericMatrix scaledMatrix, ArrayList<Double> coefficients) {
		int posNewCustomer = -1;
		double scaledDistance = 0.0;
		double distance = 0.0;
		NumericMatrix costMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());	

		for(int i = 0; i < customersToAssign.size(); i++)
		{
			posNewCustomer = InfoProblem.getProblem().getPosElement(customersToAssign.get(i).getIDCustomer());
			distance = costMatrix.getItem(posNewCustomer, posCustomer);
			scaledDistance = distance * coefficients.get(posCustomer);

			scaledMatrix.setItem(posCustomer, posNewCustomer, scaledDistance);
		}
	}
}
