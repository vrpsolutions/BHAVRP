package tester;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cujae.inf.ic.om.factory.interfaces.AssignmentType;
import cujae.inf.ic.om.problem.input.Problem;

/* Clase encargada de gurdar la solucion obtenida */
public class SaveFile {
	
	private static SaveFile instanceSaveFile = null;
	private static HSSFWorkbook workBook;
	
	public static SaveFile getSaveFile(){
		if(instanceSaveFile == null)
			instanceSaveFile = new SaveFile();
		return instanceSaveFile;
	}

	private SaveFile() {
		super();
		workBook = new HSSFWorkbook();
	}
	
	public static void saveResultsToExcel(int run, String pathFileEnd, AssignmentType heuristic, ArrayList<Double> executionTimes, ArrayList<Integer> unassignedCustomers, ArrayList<Integer> idDepWithOutCust) {
		String sheetName = "Resultados_" + heuristic.name();
		
		HSSFSheet sheet = workBook.getSheet(sheetName);
		
		if (sheet == null) {
	        sheet = workBook.createSheet(sheetName);
	    }

		HSSFRow header = sheet.createRow(0);
		
		header.createCell(0).setCellValue("RUN");
		header.createCell(1).setCellValue("TIME(ms)");
		header.createCell(2).setCellValue("TIME(s)");
		header.createCell(3).setCellValue("UNA_CTS");
		header.createCell(4).setCellValue("DPTS_WO");
		
		for (int i = 0; i < run; i++) {
			HSSFRow row = sheet.createRow(i + 1);
			row.createCell(0).setCellValue(i + 1);
			row.createCell(1).setCellValue(executionTimes.get(i));
			row.createCell(2).setCellValue(executionTimes.get(i) / 1000.0);
			row.createCell(3).setCellValue(unassignedCustomers.get(i));
			row.createCell(4).setCellValue(i < idDepWithOutCust.size() ? idDepWithOutCust.get(i) : 0);
		}

	    double sumTime = 0.0, maxTime = Double.MIN_VALUE, minTime = Double.MAX_VALUE;
	    int sumUnassigned = 0, maxUnassigned = Integer.MIN_VALUE, minUnassigned = Integer.MAX_VALUE;
	    int sumDepWithoutCust = 0, maxDepWithoutCust = Integer.MIN_VALUE, minDepWithoutCust = Integer.MAX_VALUE;

		for (int i = 0; i < run; i++) {
	        double time = executionTimes.get(i);
	        int unassigned = unassignedCustomers.get(i);
	        int depWithoutCust = idDepWithOutCust.size();

	        sumTime += time;
	        if (time > maxTime) maxTime = time;
	        if (time < minTime) minTime = time;

	        sumUnassigned += unassigned;
	        if (unassigned > maxUnassigned) maxUnassigned = unassigned;
	        if (unassigned < minUnassigned) minUnassigned = unassigned;

	        sumDepWithoutCust += depWithoutCust;
	        if (depWithoutCust > maxDepWithoutCust) maxDepWithoutCust = depWithoutCust;
	        if (depWithoutCust < minDepWithoutCust) minDepWithoutCust = depWithoutCust;
	    }

		double avgTime = executionTimes.isEmpty() ? 0.0 : sumTime / executionTimes.size();
		double avgTimeSeconds = avgTime / 1000.0;
		double avgUnassigned = unassignedCustomers.isEmpty() ? 0.0 : (double) sumUnassigned / unassignedCustomers.size();
		double avgDepWithoutCust = idDepWithOutCust.isEmpty() ? 0.0 : (double) sumDepWithoutCust / idDepWithOutCust.size();
		
		int lastRow = run + 2;

		HSSFRow maxRow = sheet.createRow(lastRow);
		maxRow.createCell(0).setCellValue("MAX:");
		maxRow.createCell(1).setCellValue(maxTime);
		maxRow.createCell(2).setCellValue(maxTime / 1000.0);
		maxRow.createCell(3).setCellValue(maxUnassigned);
		maxRow.createCell(4).setCellValue(maxDepWithoutCust);

		HSSFRow minRow = sheet.createRow(lastRow + 1);
		minRow.createCell(0).setCellValue("MIN:");
		minRow.createCell(1).setCellValue(minTime);
		minRow.createCell(2).setCellValue(minTime / 1000.0);
		minRow.createCell(3).setCellValue(minUnassigned);
		minRow.createCell(4).setCellValue(minDepWithoutCust);

		HSSFRow avgRow = sheet.createRow(lastRow + 2);
		avgRow.createCell(0).setCellValue("AVG:");
		avgRow.createCell(1).setCellValue(avgTime);
		avgRow.createCell(2).setCellValue(avgTimeSeconds);
		avgRow.createCell(3).setCellValue(avgUnassigned);
		avgRow.createCell(4).setCellValue(avgDepWithoutCust);
		
		try (FileOutputStream fileOut = new FileOutputStream(pathFileEnd)) {
			workBook.write(fileOut);
			System.out.println("Resultados guardados en: " + pathFileEnd);
		} catch (IOException e) {
			System.err.println("Error al guardar los resultados en Excel: " + e.getMessage());
		}
	}
	
	public void closeResultFile(String pathFile) throws IOException{
		FileOutputStream fileOut = new FileOutputStream(pathFile);
		workBook.write(fileOut);
		fileOut.close();
	}

	public void createResultFile(String pathFile) throws IOException{
		workBook = new HSSFWorkbook();
		closeResultFile(pathFile);
	}
	
	public void createSheet(String  pathFile, String idInstance, int executioncount, double requestTotal, int countVehicles, double capacityVehicle) throws IOException{
		HSSFSheet sheet = workBook.createSheet("INSTANCIA_" + idInstance); //crea las hojas del libro

		HSSFRow rowSheet = sheet.createRow(0);
		rowSheet.createCell(0).setCellValue("INSTANCIA:");
		rowSheet.createCell(2).setCellValue("CLIENTES:");
		rowSheet.createCell(4).setCellValue("DEP�SITOS:");

		rowSheet = sheet.createRow(1);
		rowSheet.createCell(0).setCellValue("EJECUCIONES:");
		rowSheet.createCell(2).setCellValue("DEMANDA TOTAL:");
		rowSheet.createCell(4).setCellValue("CANTIDAD DE VEH�CULOS x DEP�SITO:");
		
		rowSheet = sheet.createRow(2);
		rowSheet.createCell(4).setCellValue("CAPACIDAD DEL VEH�CULO:");
		
		rowSheet = sheet.createRow(6);
		rowSheet.createCell(0).setCellValue("RUN");

		rowSheet.createCell(1).setCellValue("AGRUPAMIENTO");
		rowSheet.createCell(2).setCellValue("SSE");
		rowSheet.createCell(3).setCellValue("SSB");
		rowSheet.createCell(4).setCellValue("DUNN INDEX");
		rowSheet.createCell(5).setCellValue("COEFFICIENT SILOHUETE");
		rowSheet.createCell(6).setCellValue("TIEMPO");
		rowSheet.createCell(7).setCellValue("CLIENTES SIN ASIGNAR");
		rowSheet.createCell(8).setCellValue("DEP�SITOS SIN ASIGNACIONES");
		
		rowSheet.createCell(9).setCellValue("M�NIMA DISTANCIA");
		rowSheet.createCell(10).setCellValue("M�XIMA DISTANCIA");
		rowSheet.createCell(11).setCellValue("DISTANCIA PROMEDIO");
		
		for(int j = 0; j < executioncount; j++) 
		{
			rowSheet = sheet.createRow(7 + j);
			rowSheet.createCell(0).setCellValue(j + 1);
		}

		rowSheet = sheet.createRow(12 + executioncount);
		rowSheet.createCell(0).setCellValue("MIN");

		rowSheet = sheet.createRow(13 + executioncount);
		rowSheet.createCell(0).setCellValue("MAX");
		
		rowSheet = sheet.createRow(14 + executioncount);
		rowSheet.createCell(0).setCellValue("AVG");

		closeResultFile(pathFile);
	}
	
	@SuppressWarnings("unused")
	public void writeData(int problem, int execution, String idInstance, String  pathFile) throws IOException{
		int countVehicles = 0;
		
		HSSFSheet sheet = workBook.getSheet("INSTANCIA_" + idInstance); //obtener la hoja del libro

		HSSFRow rowSheet = sheet.getRow(0);
		rowSheet.createCell(1).setCellValue(idInstance);
		rowSheet.createCell(3).setCellValue(Problem.getProblem().getTotalCustomers());
		rowSheet.createCell(5).setCellValue(Problem.getProblem().getTotalDepots());
		
		rowSheet = sheet.getRow(1);
		rowSheet.createCell(1).setCellValue(execution);
		rowSheet.createCell(3).setCellValue(Problem.getProblem().getTotalRequest());
		rowSheet.createCell(5).setCellValue(000);
		
		rowSheet = sheet.getRow(2);
		rowSheet.createCell(5).setCellValue(000);
		
		rowSheet.createCell(1).setCellValue("AGRUPAMIENTO");
		rowSheet.createCell(2).setCellValue("SSE");
		rowSheet.createCell(3).setCellValue("SSB");
		rowSheet.createCell(4).setCellValue("DUNN INDEX");
		rowSheet.createCell(5).setCellValue("COEFFICIENT SILOHUETE");
		rowSheet.createCell(6).setCellValue("TIEMPO");
		rowSheet.createCell(7).setCellValue("CLIENTES SIN ASIGNAR");
		rowSheet.createCell(8).setCellValue("DEP�SITOS SIN ASIGNACIONES");
		
		rowSheet.createCell(9).setCellValue("M�NIMA DISTANCIA");
		rowSheet.createCell(10).setCellValue("M�XIMA DISTANCIA");
		rowSheet.createCell(11).setCellValue("DISTANCIA PROMEDIO");

		/*rowSheet.createCell(1).setCellValue(problem);
		rowSheet.createCell(3).setCellValue(Problem.getProblem().getListCustomers().size());
		
		switch(Problem.getProblem().getTypeProblem().ordinal())
		{
	    	case 0:
	    	{
	    		rowSheet.createCell(5).setCellValue(Problem.getProblem().getListDepots().get(0).getListFleets().get(0).getCountVehicles());
	    		break;
	    	}
	    	case 1:
	    	{
	    		countVehicles = Problem.getProblem().getListDepots().get(0).getListFleets().size();
	    		rowSheet.createCell(5).setCellValue(countVehicles);
	    		break;
	    	}
	    	case 2:
	    	{
	    		for(int i = 0; i < Problem.getProblem().getListDepots().size(); i++)
					countVehicles += Problem.getProblem().getListDepots().get(i).getListFleets().get(0).getCountVehicles();
				
				rowSheet.createCell(5).setCellValue(countVehicles);
	    		
	    		break;
	    	}
	    	case 3:
	    	{
	    		rowSheet.createCell(5).setCellValue(Problem.getProblem().getListDepots().get(0).getListFleets().get(0).getCountVehicles());
	    		break;
	    	}
		}*/
		
		///rowSheet.createCell(5).setCellValue(StrategyHeuristic.getStrategyHeuristic().getProblem().getListDepots().get(0).getListFleet().get(0).getCountTrucks());
		
		/*rowSheet.createCell(7).setCellValue(nameHeuristic);

		rowSheet = sheet.getRow(1);
		rowSheet.createCell(1).setCellValue(Problem.getProblem().getTypeProblem().name());
		rowSheet.createCell(3).setCellValue(Problem.getProblem().getCountVC());
		rowSheet.createCell(5).setCellValue(Problem.getProblem().getListDepots().get(0).getListFleets().get(0).getCapacityVehicle());
		rowSheet.createCell(7).setCellValue(execution);*/

		rowSheet = sheet.getRow(2);
		//rowSheet.createCell(3).setCellValue(Problem.getProblem().getCountTC());
		//rowSheet.createCell(5).setCellValue(((FleetTTRP)Problem.getProblem().getListDepots().get(0).getListFleets().get(0)).getCountTrailers());

		rowSheet = sheet.getRow(3);
		//rowSheet.createCell(3).setCellValue(Problem.getProblem().getTotalRequest());
		//rowSheet.createCell(5).setCellValue(((FleetTTRP)Problem.getProblem().getListDepots().get(0).getListFleets().get(0)).getCapacityTrailer());

		closeResultFile(pathFile);
	}
	
	/*public void writeLocalMetrics(String  pathFile, String  heuristic, int actExecution, Solution solution) throws IOException{
		HSSFSheet sheet = workBook.getSheet("HEURISTICA_" + heuristic); //obtener la hoja del libro

		HSSFRow rowSheet = sheet.getRow(7 + actExecution); 
		
    	rowSheet.createCell(1).setCellValue(Tools.roundDouble(solution.getTotalCost(), 2)); 
    	rowSheet.createCell(2).setCellValue(solution.getOrdenVisit().toString());
    	rowSheet.createCell(3).setCellValue(solution.getListRoute().size());
    	rowSheet.createCell(4).setCellValue(solution.countRoutesForType(2));
    	rowSheet.createCell(5).setCellValue(solution.countRoutesForType(1));
    	rowSheet.createCell(6).setCellValue(solution.countRoutesForType(0));
    	
    	switch(Problem.getProblem().getTypeProblem().ordinal())
		{
	    	case 0:
	    	{
	    		if(solution.getListRoute().size() <= Problem.getProblem().getListDepots().get(0).getListFleets().get(0).getCountTrucks())
	        		rowSheet.createCell(7).setCellValue("SI");
	    		else
	        		rowSheet.createCell(7).setCellValue("NO");
	    		break;
	    	}
	    	case 1:
	    	{
	    		for(int i = 0; i < Problem.getProblem().getListDepots().get(0).getListFleets().size(); i++){
		    		if(solution.getListRoute().size() <= Problem.getListCapacity().size())
		        		rowSheet.createCell(7).setCellValue("SI");
		    		else
		        		rowSheet.createCell(7).setCellValue("NO");
	    		}
	    		break;
	    	}
	    	case 2:
	    	{
	    		for(int i = 0; i < Problem.getProblem().getListDepots().size(); i++){
	    			if(StrategyHeuristic.getStrategyHeuristic().countRoutesForDepot(Problem.getProblem().getListDepots().get(i).getIdDepot()) <= Problem.getProblem().getListDepots().get(i).getListFleet().get(0).getCountTrucks())
		        		rowSheet.createCell(7).setCellValue("SI");
	    			else
		        		rowSheet.createCell(7).setCellValue("NO");
	    		}
	    		break;
	    	}
	    	case 3:
	    	{
	    		if(solution.getListRoute().size() <= Problem.getProblem().getListDepots().get(0).getListFleets().get(0).getCountTrucks() && ((solution.countRoutesForType(2) + solution.countRoutesForType(1)) <= StrategyHeuristic.getStrategyHeuristic().getProblem().getListDepots().get(0).getListFleet().get(0).getCountTrailers()))
	        		rowSheet.createCell(7).setCellValue("SI");
	    		else
	        		rowSheet.createCell(7).setCellValue("NO");
	    		break;
	    	}
		} 	
    	
    	closeResultFile(pathFile);
	}*/
	
	/*public void writeGlobalMetrics(String  pathFile, String nameHeuristic, int executioncount) throws IOException{  
		HSSFSheet sheet = workBook.getSheet("HEURISTICA_" + nameHeuristic); //obtener la hoja del libro

		HSSFRow rowSheet = sheet.getRow(12 + executioncount);
		rowSheet.createCell(1).setCellValue(Tools.roundDouble(StrategyHeuristic.getStrategyHeuristic().getBestSolution().getTotalCost(), 2));

		rowSheet = sheet.getRow(13 + executioncount);
		rowSheet.createCell(1).setCellValue(Tools.roundDouble((StrategyHeuristic.getStrategyHeuristic().getCostAll()/executioncount), 2));

		rowSheet = sheet.getRow(14 + executioncount);
		rowSheet.createCell(1).setCellValue(Tools.roundDouble(StrategyHeuristic.getStrategyHeuristic().getTotalRoute()/executioncount, 1)); 

		rowSheet = sheet.getRow(15 + executioncount);
		rowSheet.createCell(1).setCellValue(Tools.roundDouble(StrategyHeuristic.getStrategyHeuristic().getTotalRouteForType(2)/executioncount, 1));

		rowSheet = sheet.getRow(16 + executioncount);
		rowSheet.createCell(1).setCellValue(Tools.roundDouble(StrategyHeuristic.getStrategyHeuristic().getTotalRouteForType(1)/executioncount, 1));

		rowSheet = sheet.getRow(17 + executioncount);
		rowSheet.createCell(1).setCellValue(Tools.roundDouble(StrategyHeuristic.getStrategyHeuristic().getTotalRouteForType(0)/executioncount, 1));      	

		rowSheet = sheet.getRow(18 + executioncount);
		rowSheet.createCell(1).setCellValue(StrategyHeuristic.getStrategyHeuristic().getTimeExecute()/1000.0);
		
		closeResultFile(pathFile);
	}*/
}