package tester;

import java.io.*;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import cujae.inf.ic.om.factory.interfaces.AssignmentType;

import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.Solution;

public class MainDePrueba
{
	public static void main(String arg[]) throws IOException, IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{	
		Solution solution = null;

		String pathFileEnd = "result//INSTANCIA_P01_1.xls";
		SaveFile.getSaveFile().createResultFile(pathFileEnd);

		String pathFiles = "C-mdvrp//p"; 
		int totalInstances = 1;
		LoadFile load = new LoadFile();

		AssignmentType heuristic = null;

		//for(int i = 0; i < totalInstances; i++)
		//{
		try {
			//load.loadFile(pathFiles + (i + 1));
			load.loadFile(pathFiles + totalInstances);
		} catch (FileNotFoundException e) {
			//System.err.println("Archivo no encontrado: " + pathFiles + (i + 1));
			System.err.println("Archivo no encontrado: " + pathFiles + totalInstances);
		}

		System.out.println("-------------------------------------------------------------------------------");
		//System.out.println("INSTANCIA: P" + (i + 1));
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

		ArrayList<ArrayList<Double>> listDistances = new ArrayList<ArrayList<Double>>();

		load.loadCountVehiclesForDepot(countVehicles);
		load.loadCapacityVehicles(capacityVehicles);
		load.loadCustomers(idCustomers, axisXCustomers, axisYCustomers, requestCustomers);
		load.loadDepots(idDepots, axisXDepots, axisYDepots);

		load.fillListDistances(idCustomers, axisXCustomers, axisYCustomers, idDepots, axisXDepots, axisYDepots, listDistances);

		if(cujae.inf.ic.om.controller.Controller.getController().loadProblem(idCustomers, requestCustomers, axisXCustomers, axisYCustomers, idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles))
		{
			double avgTime = 0.0;
			double runTime = 0.0;

			int run = 20;
			AssignmentType[] heuristics = AssignmentType.values();
			ArrayList<Double> executionTimes = new ArrayList<>();
			ArrayList<Integer> unassignedCustomers = new ArrayList<>();
			ArrayList<Integer> depotsWithOutCust = new ArrayList<>();
			
			//for(int k = 0; k < heuristics.length; k++)
			//{
				double heuristicTotalTime = 0.0;

				int k = 4;
				
				for(int j = 0; j < run; j++)
				{
					double start = System.currentTimeMillis();

		            heuristic = heuristics[k];
		            solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(heuristic);

		            long end = System.currentTimeMillis();
		            runTime = end - start;
		            heuristicTotalTime += runTime;
	
					executionTimes.add(runTime);
					unassignedCustomers.add(solution.getTotalUnassignedItems());
					depotsWithOutCust = cujae.inf.ic.om.controller.Controller.getController().getDepotsWithOutCustomers();
	
					System.out.println();
					System.out.println("Tiempo de Ejecuci�n " + (j + 1) + ": " + runTime + " ms");
					System.out.println();
					cujae.inf.ic.om.controller.Controller.destroyController();
					System.out.println("Controller limpio.");
					System.out.println();

				}
				avgTime += heuristicTotalTime;
				
				SaveFile.saveResultsToExcel(run, pathFileEnd, heuristic, executionTimes, unassignedCustomers, depotsWithOutCust);
				
				executionTimes.clear();
				unassignedCustomers.clear();
				depotsWithOutCust.clear();
				
				
				
			//}

			System.out.println("-------------------------------------------------------------------------------");
			System.out.println("Instancia ejecutada: " + "p" + totalInstances);
			System.out.println("Heur�stica: " + heuristic.name());
			System.out.println("Resultados:");
			System.out.println();
			System.out.println("N�mero de Ejecuciones: " + run);
			System.out.println();
			System.out.println("Tiempo de Ejecuci�n Total: " + avgTime + " ms");
			System.out.println("Tiempo de Ejecuci�n Total: " + avgTime/1000 + " s");
			System.out.println();
			System.out.println("Tiempo de Ejecuci�n Promedio: " + (avgTime/run) + " ms");
			System.out.println("Tiempo de Ejecuci�n Promedio: " + (avgTime/run)/1000 + " s");
			System.out.println("-------------------------------------------------------------------------------");
		}

		Problem.getProblem().cleanInfoProblem();
	}
}


/*
switch(k)
{
case 0:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.BestCyclicAssignment);
	break;
}
case 1:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.BestNearest);
	break;
}
case 2:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.CLARA);
	break;
}
case 3:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.CoefficientPropagation);
	break;
}
case 4:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.CyclicAssignment);
	break;
}
case 5:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Farthest_First);
	break;
}
case 6:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.KMEANS);
	break;
}
case 7:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.NearestByCustomer);
	break;
}
case 8:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.NearestByDepot);
	break;
}
case 9:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.PAM);
	break;
}
case 10:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Parallel);
	break;
}
case 11:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.RandomByElement);
	break;
}
case 12:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.RandomSequentialCyclic);
	break;
}
case 13:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.SequentialCyclic);
	break;
}
case 14:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Simplified);
	break;
}
case 15:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Sweep);
	break;
}
case 16:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.ThreeCriteriaClustering);
	break;
}
case 17:
{
	solution = cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.UPGMC);
	break;
}
}
*/