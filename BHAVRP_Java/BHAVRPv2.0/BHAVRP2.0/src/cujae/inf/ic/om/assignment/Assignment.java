package cujae.inf.ic.om.assignment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.matrix.NumericMatrix;
import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Depot;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.output.Cluster;

public abstract class Assignment extends AssignmentTemplate {
	
	public static DistanceType distanceType = DistanceType.Euclidean;
	
	/**
	 * @param  int identificador del cluster
	 * @param  ArrayList<Cluster> listado de clusters 
	 * @return int posici�n del cluster en la clusters
	 * Busca la posici�n de un cluster en el listado de clusters
	 **/
	protected int findCluster(int idCluster, ArrayList<Cluster> clusters){
		int posCluster = -1;

		int i = 0;
		boolean found = false;

		while((i < clusters.size()) && (!found))
		{
			if(clusters.get(i).getIDCluster() == idCluster)
			{
				found = true;
				posCluster = i;
			}
			else 
				i++;
		}
		return posCluster;
	}
	
	/**
	 * @param  ArrayList<Customer> listado de clientes
	 * @param  ArrayList<Depot> listado de los depositos/centroides/medoides
	 * @param  DistanceType tipo de distancia a utilizar 
	 * @return NumericMatrix matriz de costos
	 * Crea la matriz de costos a partir del tipo de distancia
	 **/
	protected NumericMatrix initializeCostMatrix(ArrayList<Customer> listCustomers, ArrayList<Depot> listDepots, DistanceType distanceType) {
		NumericMatrix costMatrix = null;

		if (distanceType == DistanceType.Real)
		{
			try {
				costMatrix = Problem.getProblem().fillCostMatrixReal(listCustomers, listDepots);
			} catch (IOException | InterruptedException | IllegalArgumentException | SecurityException
					| ClassNotFoundException | InstantiationException | IllegalAccessException
					| InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				costMatrix = Problem.getProblem().fillCostMatrix(listCustomers, listDepots, distanceType);
			} catch (IllegalArgumentException | SecurityException | ClassNotFoundException | InstantiationException
					| IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return costMatrix;
	}	
}