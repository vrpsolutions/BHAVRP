package tester;

import java.io.*;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import cujae.inf.ic.om.factory.interfaces.AssignmentType;

import cujae.inf.ic.om.problem.input.Problem;

public class MainDePrueba
{
	public static void main(String arg[]) throws IOException, IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{	
		/*
		String pathFileEnd = "result//";
		String nameFile = "INSTANCIA_1.xls"; 
		pathFileEnd += nameFile;
		SaveFile.getSaveFile().createResultFile(pathFileEnd);
		*/
		
		String pathFiles = "C-mdvrp//p"; 
		int totalInstances = 1; // instancia 6, 10, 15 y 19
		LoadFile load = new LoadFile();

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

			//cujae.inf.citi.om.heuristic.controller.Controller.getController().loadProblem(idCustomers, requestCustomers, idDepots, countVehicles, capacityVehicles, listDistances)
			//cujae.inf.citi.om.heuristic.controller.Controller.getController().loadProblem(idCustomers, requestCustomers, axisXCustomers, axisYCustomers, idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles, listDistances)
			//cujae.inf.ic.om.controller.Controller.getController().loadProblem(idCustomers, requestCustomers, axisXCustomers, axisYCustomers, idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles, listDistances)
			
			if(cujae.inf.ic.om.controller.Controller.getController().loadProblem(idCustomers, requestCustomers, axisXCustomers, axisYCustomers, idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles))
			{
				double avgTime = 0.0;
				double runTime = 0.0;
				
				/*
				Evaluation metric = new Evaluation();
				ArrayList<Double> listDI = new ArrayList<Double>();
				ArrayList<Double> listSSE = new ArrayList<Double>(); 
				ArrayList<Double> listSSW = new ArrayList<Double>();
				ArrayList<Double> listSSB = new ArrayList<Double>();
				*/
				ArrayList<Double> listExTime = new ArrayList<Double>();
				
				int run = 1;
				
				//SaveFile.getSaveFile().createSheet(pathFileEnd, "p1", run, Problem.getProblem().getTotalRequest(), 0, 0);
				//SaveFile.getSaveFile().writeData(idFile, countExecution, nameHeuristic, variant, pathFileEnd);

				
				for(int k = 0; k < run; k++)
				{
					double start = System.currentTimeMillis();
					
					int j = 5;
					
					//for(int j = 1; j < AssignmentType.values().length; j++)
					//{
						switch(j)
						{
							case 0:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.BestCyclicAssignment);
								break;
							}
							case 1:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.BestNearest);
								break;
							}
							case 2:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.CLARA);
								break;
							}
							case 3:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.CoefficientPropagation);
								break;
							}
							case 4:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.CyclicAssignment);
								break;
							}
							case 5:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Farthest_First);
								break;
							}
							case 6:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.KMEANS);
								break;
							}/*
							case 7:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Modified_KMEANS);
								break;
							}
							case 8:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Modified_PAM);
								break;
							}*/
							case 9:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.NearestByCustomer);
								break;
							}
							case 10:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.NearestByDepot);
								break;
							}
							case 11:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.PAM);
								break;
							}
							case 12:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Parallel);
								//cujae.inf.citi.om.heuristic.controller.Controller.getController().executeAssignment(AssignmentType.ParallelPlus);
								break;
							}
							case 13:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.RandomByElement);
								break;
							}
							case 14:
							{
								//cujae.inf.citi.om.controller.Controller.getController().executeAssignment(AssignmentType.RandomNearestByCustomer);
								break;
							}
							case 15:
							{
								//cujae.inf.citi.om.controller.Controller.getController().executeAssignment(AssignmentType.RandomNearestByDepot);
								break;
							}
							case 16:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.RandomSequentialCyclic);
								break;
							}
							case 17:
							{
								//cujae.inf.citi.om.controller.Controller.getController().executeAssignment(AssignmentType.RandomSequentialNearestByDepot);
								break;
							}
							case 18:
							{
								//cujae.inf.citi.om.controller.Controller.getController().executeAssignment(AssignmentType.ROCK);
								break;
							}
							case 19:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.SequentialCyclic);
								break;
							}
							case 20:
							{
								//cujae.inf.citi.om.controller.Controller.getController().executeAssignment(AssignmentType.SequentialNearestByDepot);
								break;
							}
							case 21:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Simplified);
								break;
							}
							case 22:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.Sweep);
								break;
							}
							case 23:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.ThreeCriteriaClustering);
								break;
							}
							case 24:
							{
								cujae.inf.ic.om.controller.Controller.getController().executeAssignment(AssignmentType.UPGMC);
								break;
							}
						}
						//}
						
					double end = System.currentTimeMillis();
					runTime = (end - start);
					avgTime += runTime;
					
					listExTime.add(runTime);
					System.out.println();
					System.out.println("Tiempo de Ejecución " + (k + 1) + ": " + runTime + " ms");
					System.out.println("Tiempo de Ejecución " + (k + 1) + ": " + listExTime.get(k) + " ms");
					System.out.println();
					
					//int totalClusters = Controller.getController().getSolution().getClusters().size(); 
					
					/*
					double SSE = 0.0;
					double SSW = 0.0;
					double SSB = 0.0;
					double DI = 0.0;
					
					for(int l = 0; l < totalClusters; l++)
					{
						SSE += metric.SSE(Controller.getController().getSolution().getClusters().get(l));
						SSW += metric.SSW(Controller.getController().getSolution().getClusters().get(l));
						
						listSSE.add(metric.SSE(Controller.getController().getSolution().getClusters().get(l)));
						listSSW.add(metric.SSW(Controller.getController().getSolution().getClusters().get(l)));
					}
					
					listSSE.add(SSE/totalClusters);
					listSSW.add(SSW/totalClusters);
					
					System.out.println("SSE: " + listSSE.get(listSSE.size() - 1)); 
					System.out.println("SSW: " + listSSW.get(listSSW.size() - 1)); 
					
					System.out.println("SSE_Global: " + metric.SSEGlobal(Controller.getController().getSolution().getClusters()));
					
					SSB = metric.SSB(Controller.getController().getSolution().getClusters());
					listSSB.add(SSB);
					
					System.out.println("SSB: " + listSSB.get(listSSB.size() - 1));
					
					DI = metric.dunnIndex(Controller.getController().getSolution().getClusters());
					listDI.add(DI);
					
					System.out.println("DI: " + listDI.get(listDI.size() - 1));
					
					
/*					double result1 = metric.SSE(Controller.getController().getSolution().getClusters().get(0));
					double result2 = metric.SSW(Controller.getController().getSolution().getClusters().get(0));

					System.out.println("SSE " + result1); 
					System.out.println("SSW " + result2);
					
					double result3 = metric.SSB(Controller.getController().getSolution().getClusters());
					double result4 = metric.SSB1(Controller.getController().getSolution().getClusters());
					
					System.out.println("SSB " + result3);
					System.out.println("SSB1 " + result4); */
				}
				System.out.println("-------------------------------------------------------------------------------");
				System.out.println("Instancia ejecutada: " + "p" + totalInstances);
				System.out.println("Resultados:");
				System.out.println();
				System.out.println("Número de Ejecuciones: " + run);
				System.out.println();
				System.out.println("Tiempo de Ejecución Total: " + avgTime + " ms");
				System.out.println("Tiempo de Ejecución Total: " + avgTime/1000 + " s");
				System.out.println();
				System.out.println("Tiempo de Ejecución Promedio: " + (avgTime/run) + " ms");
				System.out.println("Tiempo de Ejecución Promedio: " + (avgTime/run)/1000 + " s");
				System.out.println("-------------------------------------------------------------------------------");
			}

			/*Metric metric = new Metric();
			double result1 = metric.SSE(Controller.getController().getSolution().getClusters().get(0));
			double result2 = metric.SSW(Controller.getController().getSolution().getClusters().get(0));

			System.out.println("SSE " + result1); 
			System.out.println("SSW " + result2);
			
			double result3 = metric.SSB(Controller.getController().getSolution().getClusters());
			double result4 = metric.SSB1(Controller.getController().getSolution().getClusters());
			
			System.out.println("SSB " + result3);
			System.out.println("SSB1 " + result4);*/

			Problem.getProblem().cleanInfoProblem();
		}
	//}
}