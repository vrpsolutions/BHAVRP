package tester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.factory.interfaces.AssignmentType;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Problem;

public class Main {
	
	public static void main(String arg[]) throws IOException, IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException
	{
		String pathFiles = "C-mdvrp//p"; 
		int totalInstances = 1;
		LoadFile load = new LoadFile();

			try {
			    load.loadFile(pathFiles + totalInstances);
			} catch (FileNotFoundException e) {
				System.err.println("Archivo no encontrado: " + pathFiles + totalInstances);
			}
			
			System.out.println("-------------------------------------------------------------------------------");
			System.out.println("INSTANCIA: P" + totalInstances);
			System.out.println("-------------------------------------------------------------------------------");
			
			ArrayList<Integer> idCustomers = new ArrayList<Integer>();
			ArrayList<Double> axisXCustomers = new ArrayList<Double>();
			ArrayList<Double> axisYCustomers = new ArrayList<Double>();
			ArrayList<Double> requestCustomers = new ArrayList<Double>();

			ArrayList<Integer> idDepots = new ArrayList<Integer>();
			ArrayList<Double> axisXDepots = new ArrayList<Double>();
			ArrayList<Double> axisYDepots = new ArrayList<Double>();
			ArrayList<ArrayList<Integer>> countVehicles = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Double>> capacityVehicles = new ArrayList<ArrayList<Double>>();
			
			load.loadCountVehiclesForDepot(countVehicles);
			load.loadCapacityVehicles(capacityVehicles);
			load.loadCustomers(idCustomers, axisXCustomers, axisYCustomers, requestCustomers);
			load.loadDepots(idDepots, axisXDepots, axisYDepots);

			if(cujae.inf.ic.om.controller.Controller.getController().loadProblem(idCustomers, requestCustomers, axisXCustomers, axisYCustomers, idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles))
			{
				double avgTime = 0.0;
				double runTime = 0.0;

				ArrayList<Double> listExTime = new ArrayList<Double>();
				
				int run = 1;

				for(int k = 0; k < run; k++)
				{
					double start = System.currentTimeMillis();
					
					int j = 0;
					
						switch(j)
						{
							case 0:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.SequentialCyclic);
								break;
							}
							
							case 1:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Farthest_First);
								break;
							}
							
							case 2:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.RandomByElement);
								break;
							}
							
							case 3:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Sweep);
								break;
							}
						}
						
					double end = System.currentTimeMillis();
					runTime = (end - start);
					avgTime += runTime;
					
					listExTime.add(runTime);
				}
				/*
				System.out.println("-------------------------------------------------------------------------------");
				System.out.println("RESUMEN DE LA CARGA DE DATOS:");
				int totalCustomers = Problem.getProblem().getTotalCustomers();
				System.out.println("CLIENTES: " + totalCustomers);
				System.out.println("DEMANDA TOTAL DE LOS CLIENTES: " + Problem.getProblem().getTotalRequest());
				int totalDepots = Problem.getProblem().getTotalDepots();
				System.out.println("DEP�SITOS: " + totalDepots);
				System.out.println("CAPACIDAD TOTAL DE LOS DEP�SITOS: " + Problem.getProblem().getTotalCapacity());
				System.out.println("-------------------------------------------------------------------------------");
				for(int i = 0; i < totalDepots; i++)
				{
					System.out.println("ID DEP�SITO: " + Problem.getProblem().getDepots().get(i).getIDDepot());
					System.out.println("X: " + Problem.getProblem().getDepots().get(i).getLocationDepot().getAxisX());
					System.out.println("Y: " + Problem.getProblem().getDepots().get(i).getLocationDepot().getAxisY());
					System.out.println("CAPACIDAD DEL DEP�SITO: " + Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepots().get(i).getIDDepot()));

					int totalFleets = Problem.getProblem().getDepots().get(i).getFleetDepot().size();
					System.out.println("FLOTAS DEL DEP�SITO: " + totalFleets);
					System.out.println("-------------------------------------------------------------------------------");

					for(int j = 0; j < totalFleets; j++)
					{
						System.out.println("CANTIDAD DE VEH�CULOS: " + Problem.getProblem().getDepots().get(i).getFleetDepot().get(j).getCountVehicles());
						System.out.println("CAPACIDAD DE LOS VEH�CULOS: " + Problem.getProblem().getDepots().get(i).getFleetDepot().get(j).getCapacityVehicle());
					}
				}
				
				ArrayList<Customer> selectedCustomers = new ArrayList<>(Problem.getProblem().getCustomers().subList(0, 10));
				ArrayList<Depot> selectedDepots = new ArrayList<>(Problem.getProblem().getDepots().subList(0, 2));

				NumericMatrix euclideanMatrix = Problem.getProblem().fillCostMatrix(selectedCustomers, selectedDepots, DistanceType.Euclidean);
				NumericMatrix haversineMatrix = Problem.getProblem().fillCostMatrix(selectedCustomers, selectedDepots, DistanceType.Haversine);
				NumericMatrix manhattanMatrix = Problem.getProblem().fillCostMatrix(selectedCustomers, selectedDepots, DistanceType.Manhattan);
				NumericMatrix chebyshevMatrix = Problem.getProblem().fillCostMatrix(selectedCustomers, selectedDepots, DistanceType.Chebyshev);
				NumericMatrix realMatrix = null;
				if(totalInstances == 21)
				{
					realMatrix = Problem.getProblem().fillCostMatrixReal(selectedCustomers, selectedDepots);
				}
				if(totalInstances == 1)
				{	
					System.out.println("-------------------------------------------------------------------------------");
					System.out.println("MATRIZ DE COSTOS - EUCLIDEAN:");
					printMatrix(euclideanMatrix);
					System.out.println("-------------------------------------------------------------------------------");
					System.out.println("MATRIZ DE COSTOS - HAVERSINE:");
					printMatrix(haversineMatrix);
					System.out.println("-------------------------------------------------------------------------------");
					System.out.println("MATRIZ DE COSTOS - MANHATTAN:");
					printMatrix(manhattanMatrix);
					System.out.println("-------------------------------------------------------------------------------");
					System.out.println("MATRIZ DE COSTOS - CHEBYSHEV:");
					printMatrix(chebyshevMatrix);
				}
				else
				{
					System.out.println("-------------------------------------------------------------------------------");
					System.out.println("MATRIZ DE COSTOS - REAL:");
					printMatrix(realMatrix);
				}
				*/
				System.out.println("-------------------------------------------------------------------------------");
				System.out.println("N�MERO DE EJECUCIONES: " + run);
				System.out.println();
				System.out.println("TIEMPO DE EJECUCI�N TOTAL: " + avgTime + " ms");
				System.out.println("TIEMPO DE EJECUCI�N TOTAL: " + avgTime/1000 + " s");
				System.out.println();
				System.out.println("TIEMPO DE EJECUCI�N PROMEDIO: " + (avgTime/run) + " ms");
				System.out.println("TIEMPO DE EJECUCI�N PROMEDIO: " + (avgTime/run)/1000 + " s");
				System.out.println("-------------------------------------------------------------------------------");
			}
			Problem.getProblem().cleanInfoProblem();
		}

	public static void printMatrix(NumericMatrix matrix) {
	    for (int i = 0; i < matrix.getRowLength(); i++) {
	        if (i < 10) {
	            System.out.print("C" + (i + 1) + " ");
	        } else {
	            System.out.print("D" + (i - 9) + " ");
	        }
	        for (int j = 0; j < matrix.getColLength(); j++) {
	            System.out.printf("%.2f ", matrix.getItem(i, j));
	        }
	        System.out.println();
	    }
	}
}