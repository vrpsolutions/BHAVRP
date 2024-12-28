package cujae.inf.ic.om.heuristic.assignment.classical.urgency;

import java.util.ArrayList;

import cujae.inf.ic.om.heuristic.assignment.classical.Heuristic;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.matrix.RowCol;

public abstract class ByUrgency extends Heuristic {
	
	 /**
     * @param  double distancia m�s cercana
     * @param  double distancia de referencia
     * @return double valor de la urgencia
     * Calcula la urgencia
     **/
	protected double calculateUrgency(double firstDistance, double otherDistance){		
		return otherDistance - firstDistance;	
	}
	
	 /**
     * @param  ArrayList<Customer> listado de clientes
     * @param  ArrayList<Integer> listado de identificadores de dep�sitos
     * @param  NumericMatrix matriz con las distancias
     * @return ArrayList<ArrayList<Integer>> listado de identificadores de dep�sitos ordenados para cada cliente
     * Para cada cliente crea un listado con los identificadores de los dep�sitos ordenados por cercan�a
     **/
	protected ArrayList<ArrayList<Integer>> getDepotsOrdered(ArrayList<Customer> listCustomersToAssign, ArrayList<Integer> listIDDepots, NumericMatrix costMatrix){
		ArrayList<ArrayList<Integer>> listNearestDepotsByCustomer = new ArrayList<ArrayList<Integer>>();

		for(int i = 0; i < listCustomersToAssign.size(); i++)
			listNearestDepotsByCustomer.add(getDepotsOrderedByCustomer(listCustomersToAssign.get(i).getIDCustomer(), listIDDepots, costMatrix, listIDDepots.size()));
		
		return listNearestDepotsByCustomer;
	}
		
	 /**
     * @param  int identificador del cliente
     * @param  ArrayList<Integer> listado de identificadores de dep�sitos
     * @param  int cantidad de dep�sitos actuales
     * @return ArrayList<Integer> listado de identificadores de dep�sitos ordenados
     * Para un cliente dado crea un listado con los identificadores de los dep�sitos ordenados por cercan�a
     **/
	private ArrayList<Integer> getDepotsOrderedByCustomer(int idCustomer, ArrayList<Integer> listIDDepots, NumericMatrix costMatrix, int currentDepots){
		ArrayList<Integer> listClosestDepotsByCustomer = new ArrayList<Integer>();
	
		int posCustomer = Problem.getProblem().getPosElement(idCustomer);		
		int totalCustomers = Problem.getProblem().getCustomers().size();
		int totalDepots = listIDDepots.size();
		
		RowCol rcCurrentBestDepot;
		int counter = 0;
		
		int idClosestDepot = -1;
		int posClosestDepot = -1;

		while(counter < currentDepots)
		{
			rcCurrentBestDepot = costMatrix.indexLowerValue(totalCustomers, posCustomer, (totalCustomers + totalDepots - 1),  posCustomer);	

			posClosestDepot = rcCurrentBestDepot.getRow() - totalCustomers;
			idClosestDepot = Problem.getProblem().getListIDDepots().get(posClosestDepot);
			listClosestDepotsByCustomer.add(idClosestDepot);
			
			costMatrix.setItem(rcCurrentBestDepot.getRow(), posCustomer, Double.POSITIVE_INFINITY);

			counter++;
		}

		return listClosestDepotsByCustomer;
	}
}