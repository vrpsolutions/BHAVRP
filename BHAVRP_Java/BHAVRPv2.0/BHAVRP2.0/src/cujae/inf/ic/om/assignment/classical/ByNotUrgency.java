package cujae.inf.ic.om.assignment.classical;

import java.util.ArrayList;

import cujae.inf.ic.om.problem.input.Customer;

public abstract class ByNotUrgency extends Heuristic {
	
	/**
	 * @param  ArrayList<Customer>
	 * @param  double demanda cubierta del cluster
	 * @param  double capacidad del depósito
	 * @return boolean
	 * Determina si existen clientes que puedan ser asignados al depósito a partir de su demanda
	 */
	protected boolean isFullDepot(ArrayList<Customer> customers, double requestCluster, double capacityDepot){
		boolean isFull = true;

		double currentRequest = capacityDepot - requestCluster;

		if(currentRequest > 0)
		{
			int i = 0;

			while((i < customers.size()) && (isFull))
			{
				if(customers.get(i).getRequestCustomer() <= currentRequest)
					isFull = false;
				else
					i++;
			}
		}	

		return isFull;
		
		// cuando no quedan cliente lo saca como lleno ver si comviene en todos los casos
	}

}
