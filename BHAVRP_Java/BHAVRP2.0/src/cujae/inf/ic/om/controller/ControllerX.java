package cujae.inf.ic.om.controller;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import cujae.inf.ic.om.controller.utils.OrderType;
import cujae.inf.ic.om.factory.interfaces.*;

import cujae.inf.ic.om.factory.methods.FactoryAssignment;

import cujae.inf.ic.om.heuristic.assignment.Assignment;

import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Solution;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class ControllerX {

	private static ControllerX controller = null;
	private Solution solution;
	public static OrderType orderType = OrderType.Input;
	
	private ControllerX() {
		super();
	}
	
	/* Método que implementa el Patrón Singleton*/
	public static ControllerX getController(){
		if (controller == null)
			controller = new ControllerX();
		
		return controller;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}
		
	/* Método encargado de cargar los datos del problema usando listas de distancias*/
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Integer> idDepots, ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles,  ArrayList<ArrayList<Double>> listDistances)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		boolean loaded = false;
		
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
		return loaded;
	}
	
	/* Método encargado de cargar los datos del problema usando matriz de costo*/
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Integer> idDepots, ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles, NumericMatrix costMatrix)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		boolean loaded = false;

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
		return loaded;
	}
	
	/* Método encargado de cargar los datos del problema (incluido las coordenadas) usando listas de distancias*/
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Double> axisXCustomers, ArrayList<Double> axisYCustomers, ArrayList<Integer> idDepots, ArrayList<Double> axisXDepots, ArrayList<Double> axisYDepots, ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles,
			ArrayList<ArrayList<Double>> listDistances)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		boolean loaded = false;

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
		return loaded;
	}
	
	/* Método encargado de cargar los datos del problema (incluido las coordenadas) usando listas de distancias*/
	public boolean loadProblem(ArrayList<Integer> idCustomers, ArrayList<Double> requestCustomers, ArrayList<Double> axisXCustomers, ArrayList<Double> axisYCustomers, ArrayList<Integer> idDepots, ArrayList<Double> axisXDepots, ArrayList<Double> axisYDepots, ArrayList<ArrayList<Integer>> countVehicles, ArrayList<ArrayList<Double>> capacityVehicles,
			NumericMatrix costMatrix)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		boolean loaded = false;

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
		return loaded;
	}

	/* Método encargado de ejecutar la heurística de asignación*/
	public void executeAssignment(AssignmentType assignmentType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Assignment assignment = newAssignment(assignmentType);
		solution = assignment.toClustering();
		cleanController();
	}
	
	/* Método encargado de crear una método de asignación*/
	private Assignment newAssignment(AssignmentType typeAssignment) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		IFactoryAssignment iFactoryAssignment = new FactoryAssignment();
		Assignment assignment = iFactoryAssignment.createAssignment(typeAssignment);
		return assignment;
	}
	
	/*Método encargado de devolver la demanda cubierta para un depósito dado en la solución*/
	public double requestForDepot(int idDepot){
		double requestDepot = 0.0;
		
		int i = 0;
		boolean found = false;
		
		while((i < solution.getClusters().size()) && (!found))
		{
			if(solution.getClusters().get(i).getIDCluster() == idDepot)
			{
				requestDepot = solution.getClusters().get(i).getRequestCluster();
				found = true;
			}
			else
				i++;			
		}
		return requestDepot;
	}
	
	/*Método encargado de devolver los depósitos a los que no se les asigno ningún cliente en la solución*/
	public ArrayList<Integer> getDepotsWithOutCustomers(){
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
	
	/*Método encargado de restaurar los parámetros globales de la clase Controller*/
	public void cleanController() {
		solution.getClusters().clear();
		solution.getUnassignedItems().clear();
		solution = null;
	}
	
	/* Método encargado de destruir la instancia de la controladora */
	public static void destroyController() {
		controller = null;
	}
}