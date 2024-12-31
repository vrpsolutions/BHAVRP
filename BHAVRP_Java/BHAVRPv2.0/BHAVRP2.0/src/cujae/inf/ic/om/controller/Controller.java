package cujae.inf.ic.om.controller;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import cujae.inf.ic.om.assignment.Assignment;

import cujae.inf.ic.om.controller.tools.OrderType;
import cujae.inf.ic.om.controller.tools.Tools;

import cujae.inf.ic.om.factory.interfaces.AssignmentType;
import cujae.inf.ic.om.factory.interfaces.IFactoryAssignment;
import cujae.inf.ic.om.factory.methods.FactoryAssignment;

import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.solution.Solution;

public class Controller {
	private static Controller controller = null;
	public static OrderType orderType = OrderType.Input;
	private Solution solution;

	private Controller() {
		super();
	}
	
	/* Método que implementa el Patrón Singleton.*/
	public static Controller getController(){
		if (controller == null)
			controller = new Controller();
		
		return controller;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}
	
	/* Método encargado de cargar los datos del problema (incluido las coordenadas).*/
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Double> axisXCustomers, ArrayList<Double> axisYCustomers, ArrayList<Integer> idDepots, ArrayList<Double> axisXDepots, ArrayList<Double> axisYDepots, ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	boolean loaded = false;

	System.out.println("ENTRADA A LA CARGA DE DATOS");
	System.out.println("-------------------------------------------------------------------------------");
	System.out.println("CANTIDAD DE CLIENTES: " + idCustomers.size());
	System.out.println("-------------------------------------------------------------------------------");
	for(int i = 0; i < idCustomers.size(); i++)
	{
		System.out.println("ID CLIENTE: " + idCustomers.get(i));
		System.out.println("DEMANDA : " + requestCustomers.get(i));
		System.out.println("X : " + axisXCustomers.get(i));
		System.out.println("Y : " + axisYCustomers.get(i));
	}
	System.out.println("CANTIDAD DE DEPÓSITOS: " + idDepots.size());
	System.out.println("-------------------------------------------------------------------------------");
	
	int totalVehicles = 0;
	double capacityVehicle = 0.0;
	
	for(int i = 0; i < idDepots.size(); i++)
	{
		System.out.println("ID DEPÓSITO: " + idDepots.get(i));
		System.out.println("X : " + axisXDepots.get(i));
		System.out.println("Y : " + axisYDepots.get(i));
		
		System.out.println("CANTIDAD DE FLOTAS DEL DEPÓSITO: " + countVehicles.get(i).size());
		for(int j = 0; j < countVehicles.get(i).size(); j++)
		{
			totalVehicles = countVehicles.get(i).get(j);
			capacityVehicle = capacityVehicles.get(i).get(j);
			
			System.out.println("CANTIDAD DE VEHÍCULOS: " + countVehicles.get(i).get(j));
			System.out.println("CAPACIDAD DE LOS VEHÍCULOS: " + capacityVehicles.get(i).get(j));
		}
		
	//	System.out.println("CAPACIDAD TOTAL DEL DEPÓSITO: " + Problem.getProblem().getTotalCapacityByDepot(idDepots.get(i)));
		System.out.println("CAPACIDAD TOTAL DEL DEPÓSITO: " + (totalVehicles * capacityVehicle));
		
		System.out.println("-------------------------------------------------------------------------------");
	}		
		
		if((idCustomers != null && !idCustomers.isEmpty()) && (requestCustomers != null && !requestCustomers.isEmpty()) && (axisXCustomers != null && !axisXCustomers.isEmpty()) && (axisYCustomers != null && !axisYCustomers.isEmpty()) && 
				(idDepots != null && !idDepots.isEmpty()) && (axisXDepots != null && !axisXDepots.isEmpty()) && (axisYDepots!= null && !axisYDepots.isEmpty()) && (countVehicles != null && !countVehicles.isEmpty()) && 
				(capacityVehicles != null && !capacityVehicles.isEmpty()))
		
		{
			Problem.getProblem().loadCustomer(idCustomers, requestCustomers, axisXCustomers, axisYCustomers);
			Problem.getProblem().loadDepot(idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles);
			loaded = true;
		}

		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("DEMANDA TOTAL DE LOS CLIENTES: " + Problem.getProblem().getTotalRequest());
		System.out.println("CAPACIDAD TOTAL DE LOS DEPÓSITOS: " + Problem.getProblem().getTotalCapacity());
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CARGA EXITOSA: " + loaded);
		System.out.println("FIN DE LA CARGA DE DATOS");
		System.out.println("-------------------------------------------------------------------------------");
		
		return loaded;
	}
	
	/* Método encargado de ejecutar la heurística de asignación.*/
	public void executeAssignment(AssignmentType assignmentType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Assignment assignment = newAssignment(assignmentType);
		
		System.out.println("EJECUCIÓN DE LA HEURÍSTICA");
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("HEURÍSTICA: " + assignmentType.name());
		System.out.println("-------------------------------------------------------------------------------");
			
		if(assignmentType.equals(AssignmentType.NearestByCustomer) || assignmentType.equals(AssignmentType.SequentialCyclic) || assignmentType.equals(AssignmentType.CyclicAssignment) || assignmentType.equals(AssignmentType.KMEANS) ||
		   assignmentType.equals(AssignmentType.CoefficientPropagation) || assignmentType.equals(AssignmentType.NearestByDepot))
		{ 
			switch(orderType.ordinal())
			{
				case 0:
				{
					Tools.ascendentOrdenate();
					break;
				}
				case 1:
				{
					Tools.descendentOrdenate();
					break;
				}
				case 4:
				{
					Tools.randomOrdenate();
					break;
				}
			}
		}

		solution = assignment.toClustering();
		
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("SOLUTION: ");
		System.out.println("CANTIDAD DE CLUSTERS: " + solution.getClusters().size());
		System.out.println("-------------------------------------------------------------------------------");
		for(int i = 0; i < solution.getClusters().size(); i++)
		{
			System.out.println("ID CLUSTER: " + solution.getClusters().get(i).getIDCluster());
			System.out.println("DEMANDA DEL CLUSTER: " + solution.getClusters().get(i).getRequestCluster());
			System.out.println("TOTAL DE ELEMENTOS DEL CLUSTER : " + solution.getClusters().get(i).getItemsOfCluster().size());
			System.out.println("-------------------------------------------------------------------------------");
			for(int j = 0; j < solution.getClusters().get(i).getItemsOfCluster().size(); j++)
			{
				System.out.println("ID DEL ELEMENTO: " + solution.getClusters().get(i).getItemsOfCluster().get(j).intValue());
			}	
			System.out.println("-------------------------------------------------------------------------------");
		}
		System.out.println("TOTAL DE CLIENTES NO ASIGNADOS: " + solution.getTotalUnassignedItems());
		if(solution.getTotalUnassignedItems() > 0)
		{
			System.out.println("CLIENTES NO ASIGNADOS: ");
			System.out.println("-------------------------------------------------------------------------------");
			for(int i = 0; i < solution.getUnassignedItems().size(); i++)
				System.out.println("ID DEL ELEMENTO NO ASIGNADO: " + solution.getUnassignedItems().get(i).intValue());
			System.out.println("-------------------------------------------------------------------------------");
		}
		ArrayList<Integer> idDepWithOutCust = new ArrayList<Integer>();
		idDepWithOutCust = getDepotsWithOutCustomers(); // here
		System.out.println("TOTAL DE DEPÓSITOS SIN ASIGNACIÓN DE CLIENTES: " + idDepWithOutCust.size());
		if(!idDepWithOutCust.isEmpty())
		{
			System.out.println("DEPÓSITOS SIN ASIGNACIÓN DE CLIENTES: ");
			System.out.println("-------------------------------------------------------------------------------");
			for(int i = 0; i < idDepWithOutCust.size(); i++)
				System.out.println("ID DEL DEPÓSITO: " + idDepWithOutCust.get(i).intValue());
		}
		
		//cleanController();
	}
	
	/* Método encargado de crear una método de asignación.*/
	private Assignment newAssignment(AssignmentType typeAssignment) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		IFactoryAssignment iFactoryAssignment = new FactoryAssignment();
		Assignment assignment = iFactoryAssignment.createAssignment(typeAssignment);
		return assignment;
	}
	
	/*Método encargado de devolver los depósitos a los que no se les asigno ningún cliente en la solución.*/
	private ArrayList<Integer> getDepotsWithOutCustomers(){
		ArrayList<Integer> idDepots = new ArrayList<Integer>();
		
		int totalDepots = Problem.getProblem().getDepots().size();
		int totalClusters = solution.getClusters().size();
		
		if(totalClusters < totalDepots)
		{
			for(int i = 0; i < totalDepots; i++)
			{
				int j = 0;
				boolean found = false;
				
				int idDepot = Problem.getProblem().getDepots().get(i).getIDDepot();
				
				while((j < totalClusters) && (!found))
				{
					if(solution.getClusters().get(j).getIDCluster() == idDepot)
						found = true;
					else
						j++;
				}
				
				if(!found)
					idDepots.add(idDepot);
			}
		}	
		return idDepots;
	}
	
	/*Método encargado de restaurar los parámetros globales de la clase Controller.*/
	public void cleanController() {
		solution.getClusters().clear();
		solution.getUnassignedItems().clear();
		solution = null;
	}
	
	/* Método encargado de destruir la instancia de la controladora.*/
	public static void destroyController() {
		controller = null;
	}
}
/*
	// Método encargado de cargar los datos del problema usando listas de distancias y el tipo de distancia
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Integer> idDepots, 
			ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles,  
			ArrayList<ArrayList<Double>> listDistances)throws IllegalArgumentException, SecurityException, 
			ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		boolean loaded = false;

		System.out.println("ENTRADA A LA CARGA DE DATOS");
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CANTIDAD DE CLIENTES: " + idCustomers.size());
		System.out.println("-------------------------------------------------------------------------------");
		for(int i = 0; i < idCustomers.size(); i++)
		{
			System.out.println("ID CLIENTE: " + idCustomers.get(i));
			System.out.println("DEMANDA : " + requestCustomers.get(i));
		}
		System.out.println("CANTIDAD DE DEPÓSITOS: " + idDepots.size());
		System.out.println("-------------------------------------------------------------------------------");
		for(int i = 0; i < idDepots.size(); i++)
		{
			System.out.println("ID DEPÓSITO: " + idDepots.get(i));
			//System.out.println("CAPACIDAD DEL DEPÓSITO: " + Problem.getProblem().getTotalCapacityByDepot(depot));
			System.out.println("CANTIDAD DE FLOTAS DEL DEPÓSITO: " + countVehicles.get(i).size());
			for(int j = 0; j < countVehicles.get(i).size(); j++)
			{
				System.out.println("CANTIDAD DE VEHÍCULOS: " + countVehicles.get(i).get(j));
				System.out.println("CAPACIDAD DE LOS VEHÍCULOS: " + capacityVehicles.get(i).get(j));
			}
			System.out.println("-------------------------------------------------------------------------------");
		}
		
		if((idCustomers != null && !idCustomers.isEmpty()) && (requestCustomers != null && !requestCustomers.isEmpty()) && 
				(idCustomers.size() == requestCustomers.size()) && (idDepots != null && !idDepots.isEmpty()) && 
				(countVehicles != null && !countVehicles.isEmpty()) && (capacityVehicles != null && !capacityVehicles.isEmpty()) && 
				(idDepots.size() == countVehicles.size()) && (idDepots.size() == capacityVehicles.size()) && (countVehicles.size() == capacityVehicles.size()) && 
				(listDistances != null && !listDistances.isEmpty()) && (listDistances.size() == (idCustomers.size() + idDepots.size())))
		
		{
			Problem.getProblem().loadCustomer(idCustomers, requestCustomers);
			Problem.getProblem().loadDepot(idDepots, countVehicles, capacityVehicles);

			if((Problem.getProblem().getTotalCapacity() >= Problem.getProblem().getTotalRequest()))
			{
				loaded = true;
				Problem.getProblem().fillCostMatrix(listDistances);
			}
		}
		
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("DEMANDA TOTAL DE LOS CLIENTES: " + Problem.getProblem().getTotalRequest());
		System.out.println("CAPACIDAD TOTAL DE LOS DEPÓSITOS: " + Problem.getProblem().getTotalCapacity());
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CARGA EXITOSA: " + loaded);
		System.out.println("FIN DE LA CARGA DE DATOS");
		System.out.println("-------------------------------------------------------------------------------");
		
		return loaded;
	}

	// Método encargado de cargar los datos del problema usando matriz de costo.
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Integer> idDepots, 
			ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles, 
			NumericMatrix costMatrix)throws IllegalArgumentException, SecurityException, ClassNotFoundException, 
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		boolean loaded = false;

		System.out.println("ENTRADA A LA CARGA DE DATOS");
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CANTIDAD DE CLIENTES: " + idCustomers.size());
		System.out.println("-------------------------------------------------------------------------------");
		for(int i = 0; i < idCustomers.size(); i++)
		{
			System.out.println("ID CLIENTE: " + idCustomers.get(i));
			System.out.println("DEMANDA : " + requestCustomers.get(i));
		}
		System.out.println("CANTIDAD DE DEPÓSITOS: " + idDepots.size());
		System.out.println("-------------------------------------------------------------------------------");
		for(int i = 0; i < idDepots.size(); i++)
		{
			System.out.println("ID DEPÓSITO: " + idDepots.get(i));
			System.out.println("CANTIDAD DE FLOTAS DEL DEPÓSITO: " + countVehicles.get(i).size());
			System.out.println("-------------------------------------------------------------------------------");
			for(int j = 0; j < countVehicles.get(i).size(); i++)
			{
				System.out.println("CANTIDAD DE VEHÍCULOS: " + countVehicles.get(i).get(j));
				System.out.println("CAPACIDAD DE LOS VEHÍCULOS: " + capacityVehicles.get(i).get(j));
			}
			System.out.println("-------------------------------------------------------------------------------");
		}
		
		if((idCustomers != null && !idCustomers.isEmpty()) && (requestCustomers != null && !requestCustomers.isEmpty()) && 
				(idCustomers.size() == requestCustomers.size()) && (idDepots != null && !idDepots.isEmpty()) && 
				(countVehicles != null && !countVehicles.isEmpty()) && (capacityVehicles != null && !capacityVehicles.isEmpty()) && 
				(idDepots.size() == countVehicles.size()) && (idDepots.size() == capacityVehicles.size()) && (countVehicles.size() == capacityVehicles.size()) && 
				(costMatrix.getColCount() == (idCustomers.size() + idDepots.size())) && (costMatrix.getRowCount() == (idCustomers.size() + idDepots.size())))
		{
			Problem.getProblem().loadCustomer(idCustomers, requestCustomers);
			Problem.getProblem().loadDepot(idDepots, countVehicles, capacityVehicles);

			if((Problem.getProblem().getTotalCapacity() >= Problem.getProblem().getTotalRequest()))
			{
				loaded = true;
				Problem.getProblem().setCostMatrix(costMatrix);
			}
		}

		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("DEMANDA TOTAL DE LOS CLIENTES: " + Problem.getProblem().getTotalRequest());
		System.out.println("CAPACIDAD TOTAL DE LOS DEPÓSITOS: " + Problem.getProblem().getTotalCapacity());
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CARGA EXITOSA: " + loaded);
		System.out.println("FIN DE LA CARGA DE DATOS");
		System.out.println("-------------------------------------------------------------------------------");
		
		return loaded;
	}
	
	// Método encargado de cargar los datos del problema (incluido las coordenadas) usando listas de distancias
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Double> axisXCustomers, 
			ArrayList<Double> axisYCustomers, ArrayList<Integer> idDepots, ArrayList<Double> axisXDepots, ArrayList<Double> axisYDepots, 
			ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles, 
			ArrayList<ArrayList<Double>> listDistances)throws IllegalArgumentException, SecurityException, 
			ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	boolean loaded = false;

	System.out.println("ENTRADA A LA CARGA DE DATOS");
	System.out.println("-------------------------------------------------------------------------------");
	System.out.println("CANTIDAD DE CLIENTES: " + idCustomers.size());
	System.out.println("-------------------------------------------------------------------------------");
	for(int i = 0; i < idCustomers.size(); i++)
	{
		System.out.println("ID CLIENTE: " + idCustomers.get(i));
		System.out.println("DEMANDA : " + requestCustomers.get(i));
		System.out.println("X : " + axisXCustomers.get(i));
		System.out.println("Y : " + axisYCustomers.get(i));
	}
	System.out.println("CANTIDAD DE DEPÓSITOS: " + idDepots.size());
	System.out.println("-------------------------------------------------------------------------------");
	
	int totalVehicles = 0;
	double capacityVehicle = 0.0;
	
	for(int i = 0; i < idDepots.size(); i++)
	{
		System.out.println("ID DEPÓSITO: " + idDepots.get(i));
		System.out.println("X : " + axisXDepots.get(i));
		System.out.println("Y : " + axisYDepots.get(i));
		
		System.out.println("CANTIDAD DE FLOTAS DEL DEPÓSITO: " + countVehicles.get(i).size());
		for(int j = 0; j < countVehicles.get(i).size(); j++)
		{
			totalVehicles = countVehicles.get(i).get(j);
			capacityVehicle = capacityVehicles.get(i).get(j);
			
			System.out.println("CANTIDAD DE VEHÍCULOS: " + countVehicles.get(i).get(j));
			System.out.println("CAPACIDAD DE LOS VEHÍCULOS: " + capacityVehicles.get(i).get(j));
		}
		
	//	System.out.println("CAPACIDAD TOTAL DEL DEPÓSITO: " + Problem.getProblem().getTotalCapacityByDepot(idDepots.get(i)));
		System.out.println("CAPACIDAD TOTAL DEL DEPÓSITO: " + (totalVehicles * capacityVehicle));
		
		System.out.println("-------------------------------------------------------------------------------");
	}		
		
		if((idCustomers != null && !idCustomers.isEmpty()) && (requestCustomers != null && !requestCustomers.isEmpty()) && (axisXCustomers != null && !axisXCustomers.isEmpty()) && (axisYCustomers != null && !axisYCustomers.isEmpty()) && 
				(idDepots != null && !idDepots.isEmpty()) && (axisXDepots != null && !axisXDepots.isEmpty()) && (axisYDepots!= null && !axisYDepots.isEmpty()) && (countVehicles != null && !countVehicles.isEmpty()) && 
				(capacityVehicles != null && !capacityVehicles.isEmpty()) && (listDistances != null && !listDistances.isEmpty()))
		
		{
			Problem.getProblem().loadCustomer(idCustomers, requestCustomers, axisXCustomers, axisYCustomers);
			Problem.getProblem().loadDepot(idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles);
								
			if((Problem.getProblem().getTotalCapacity() >= Problem.getProblem().getTotalRequest()))
			{
				loaded = true;
				Problem.getProblem().fillCostMatrix(listDistances);
			}
		}

		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("DEMANDA TOTAL DE LOS CLIENTES: " + Problem.getProblem().getTotalRequest());
		System.out.println("CAPACIDAD TOTAL DE LOS DEPÓSITOS: " + Problem.getProblem().getTotalCapacity());
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CARGA EXITOSA: " + loaded);
		System.out.println("FIN DE LA CARGA DE DATOS");
		System.out.println("-------------------------------------------------------------------------------");
		
		return loaded;
	}
		
	// Método encargado de cargar los datos del problema (incluido las coordenadas) usando listas de distancias
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Double> axisXCustomers, ArrayList<Double> axisYCustomers, ArrayList<Integer> idDepots, ArrayList<Double> axisXDepots, ArrayList<Double> axisYDepots, ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles,
			NumericMatrix costMatrix)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	boolean loaded = false;

	System.out.println("ENTRADA A LA CARGA DE DATOS");
	System.out.println("-------------------------------------------------------------------------------");
	System.out.println("CANTIDAD DE CLIENTES: " + idCustomers.size());
	System.out.println("-------------------------------------------------------------------------------");
	for(int i = 0; i < idCustomers.size(); i++)
	{
		System.out.println("ID CLIENTE: " + idCustomers.get(i));
		System.out.println("DEMANDA : " + requestCustomers.get(i));
		System.out.println("X : " + axisXCustomers.get(i));
		System.out.println("Y : " + axisYCustomers.get(i));
	}
	System.out.println("CANTIDAD DE DEPÓSITOS: " + idDepots.size());
	System.out.println("-------------------------------------------------------------------------------");
	for(int i = 0; i < idDepots.size(); i++)
	{
		System.out.println("ID DEPÓSITO: " + idDepots.get(i));
		System.out.println("X : " + axisXDepots.get(i));
		System.out.println("Y : " + axisYDepots.get(i));
		//System.out.println("CAPACIDAD DEL DEPÓSITO: " + Problem.getProblem().getTotalCapacityByDepot(depot));
		System.out.println("CANTIDAD DE FLOTAS DEL DEPÓSITO: " + countVehicles.get(i).size());
		for(int j = 0; j < countVehicles.get(i).size(); j++)
		{
			System.out.println("CANTIDAD DE VEHÍCULOS: " + countVehicles.get(i).get(j));
			System.out.println("CAPACIDAD DE LOS VEHÍCULOS: " + capacityVehicles.get(i).get(j));
		}
		System.out.println("-------------------------------------------------------------------------------");
	}		
		
		if((idCustomers != null && !idCustomers.isEmpty()) && (requestCustomers != null && !requestCustomers.isEmpty()) && (axisXCustomers != null && !axisXCustomers.isEmpty()) && (axisYCustomers != null && !axisYCustomers.isEmpty()) && 
				(idDepots != null && !idDepots.isEmpty()) && (axisXDepots != null && !axisXDepots.isEmpty()) && (axisYDepots!= null && !axisYDepots.isEmpty()) && (countVehicles != null && !countVehicles.isEmpty()) && 
				(capacityVehicles != null && !capacityVehicles.isEmpty()) && (costMatrix.getColCount() == (idCustomers.size() + idDepots.size())) && (costMatrix.getRowCount() == (idCustomers.size() + idDepots.size())))
		
		{
			Problem.getProblem().loadCustomer(idCustomers, requestCustomers, axisXCustomers, axisYCustomers);
			Problem.getProblem().loadDepot(idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles);
								
			if((Problem.getProblem().getTotalCapacity() >= Problem.getProblem().getTotalRequest()))
			{
				loaded = true;
				Problem.getProblem().setCostMatrix(costMatrix);
			}
		}

		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("DEMANDA TOTAL DE LOS CLIENTES: " + Problem.getProblem().getTotalRequest());
		System.out.println("CAPACIDAD TOTAL DE LOS DEPÓSITOS: " + Problem.getProblem().getTotalCapacity());
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println("CARGA EXITOSA: " + loaded);
		System.out.println("FIN DE LA CARGA DE DATOS");
		System.out.println("-------------------------------------------------------------------------------");
		
		return loaded;
	}
}
*/