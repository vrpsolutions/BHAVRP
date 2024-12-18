package cujae.inf.ic.om.service;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import osrm.model.Point;
import osrm.model.TableModel;

import osrm.services.table.TableService;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;

import cujae.inf.ic.om.matrix.NumericMatrix;

public class ServiceCostMatrix {

	public NumericMatrix createInitialCostMatrix(ArrayList<Customer> customersList, ArrayList<Depot> depotsList, String url) {

		TableService newMatrix = new TableService(url);

		List<Point> pointDepots = new ArrayList<>();
		
	    List<Point> pointCustomers = new ArrayList<>();

	    for (Customer cus : customersList) {
	        Point point = new Point(cus.getLocationCustomer().getAxisX(), cus.getLocationCustomer().getAxisY());
	        pointCustomers.add(point);
	    }
	    
	    for (Depot dep : depotsList) {
	        Point point = new Point(dep.getLocationDepot().getAxisX(), dep.getLocationDepot().getAxisY());
	        pointDepots.add(point);
        }

		try {
			TableModel tableModelMatriz = newMatrix.getCostMatrixLocations(pointDepots, pointCustomers, 
					TableService.AnnotationTypes.DISTANCE);
			double[][] matriz = tableModelMatriz.getDistances();
			int row = matriz.length;
			int col = matriz[0].length;
			NumericMatrix matrix = new NumericMatrix(matriz, row, col);
			return matrix;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}