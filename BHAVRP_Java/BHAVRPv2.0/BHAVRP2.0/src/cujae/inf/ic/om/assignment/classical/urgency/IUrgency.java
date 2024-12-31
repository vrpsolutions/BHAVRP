package cujae.inf.ic.om.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.problem.input.Customer;

public interface IUrgency {

	/*Retorna un listado con las urgencias de los clientes del listado entrado por par�metro*/
	ArrayList<Double> getListUrgencies(ArrayList<Customer> listCustomersToAssign, ArrayList<ArrayList<Integer>> listIDDepots, NumericMatrix urgencyMatrix);
	
	/*M�todo encargado de obtener la urgencia*/
	double getUrgency(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix urgencyMatrix);

}
