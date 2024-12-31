package cujae.inf.ic.om.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.problem.input.Customer;

public interface IUrgencyWithMU {
	
	/*Retorna un listado con las urgencias de los clientes del listado entrado por parámetro*/
	ArrayList<Double> getListUrgencies(ArrayList<Customer> listCustomersToAssign, ArrayList<ArrayList<Integer>> listIDDepots, NumericMatrix urgencyMatrix, int muIDDepot);
	
	/*Método encargado de obtener la urgencia*/
	double getUrgency(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix urgencyMatrix, int muIDDepot);

}
