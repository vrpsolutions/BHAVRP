package cujae.inf.ic.om.assignment.classical.cluster;

import java.util.ArrayList;
import java.util.Collections;

import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;
import cujae.inf.ic.om.problem.solution.Cluster;
import cujae.inf.ic.om.problem.solution.Solution;

import cujae.inf.ic.om.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.matrix.NumericMatrix;

public class ThreeCriteriaClustering extends ByNotUrgency {
	private Solution solution = new Solution();
	
	private ArrayList<Cluster> listClusters;
	private ArrayList<Customer> listCustomersToAssign;
	NumericMatrix costMatrix;

	public ThreeCriteriaClustering() {
		super();
	}
	
	@Override
	public Solution toClustering(){
		initialize();
		assign();
		return finish();
	}
	
	@Override
	public void initialize() {
		listClusters = initializeClusters();
		listCustomersToAssign = new ArrayList<Customer>(Problem.getProblem().getCustomers());
		costMatrix = initializeCostMatrix(Problem.getProblem().getCustomers(), Problem.getProblem().getDepots(), distanceType);
	}
	
	@Override
	public void assign() {
		int idCustomer = -1;
		double requestCustomer = 0.0;
		
		double capacityDepot = 0;
		
		int posCluster = -1;
		double requestCluster = 0.0;
		
		// METODO DETERMINAR CANDIDATOS Y OTRO METODO ASIGNAR CANDIDATOS
		double difference = -1.0; 
		double percent = -1.0;
		int posMinValue = -1;
		int posCustomerRef = -1;
		
		ArrayList<Double> listAverages = null;
		ArrayList<Double> listVariances = null;
		
		ArrayList<Integer> listIDCandidates = new ArrayList<Integer>();
		ArrayList<ArrayList<Double>> listValuesCandidates = new ArrayList<ArrayList<Double>>(); 
		ArrayList<Double> listDifferences = new ArrayList<Double>();
		
		bucleInit:
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()))
		{
			listIDCandidates.clear();
			listValuesCandidates.clear();
			listDifferences.clear();
			
			for(int i = 0; i < listCustomersToAssign.size(); i++) 
			{
				idCustomer = listCustomersToAssign.get(i).getIDCustomer();
				listAverages = getListCriteriasByClusters(idCustomer, listClusters, 1);

				posMinValue = listAverages.indexOf(Collections.min(listAverages));
				difference = getDifference(listAverages, posMinValue);
				percent = 0.1 * listAverages.get(posMinValue);

				if(difference >= percent)
				{
					listIDCandidates.add(idCustomer);
					listValuesCandidates.add(listAverages);
					listDifferences.add(difference);				
				}
			}
			
			buclePhaseI:	
			while((!listIDCandidates.isEmpty()))
			{	
				posCustomerRef = listDifferences.indexOf(Collections.max(listDifferences));
				idCustomer = listIDCandidates.get(posCustomerRef);
				requestCustomer = Problem.getProblem().getRequestByIDCustomer(idCustomer);

				posCluster = listValuesCandidates.get(posCustomerRef).indexOf(Collections.min(listValuesCandidates.get(posCustomerRef)));
				requestCluster = listClusters.get(posCluster).getRequestCluster();
				capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(listClusters.get(posCluster).getIDCluster()));

				if(capacityDepot >= (requestCluster + requestCustomer))
				{
					requestCluster += requestCustomer;
					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
					listIDCandidates.clear();
					listDifferences.clear();
					listValuesCandidates.clear();

					if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
	    			{
						if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
							solution.getClusters().add(listClusters.remove(posCluster));
						else
							listClusters.remove(posCluster);
	    			}
					
					continue bucleInit;
				}
				else
				{
					listIDCandidates.remove(posCustomerRef);
					listDifferences.remove(posCustomerRef);
					listValuesCandidates.remove(posCustomerRef);

					continue buclePhaseI;
				}
			}	

			if(!listCustomersToAssign.isEmpty())
			{
				for(int j = 0; j < listCustomersToAssign.size(); j++) 
				{
					idCustomer = listCustomersToAssign.get(j).getIDCustomer();
					listVariances = getListCriteriasByClusters(idCustomer, listClusters, 2);

					posMinValue = listVariances.indexOf(Collections.min(listVariances));
					difference = getDifference(listVariances, posMinValue);
					percent = 0.4 * listVariances.get(posMinValue);

					if(difference >= percent)
					{
						listIDCandidates.add(idCustomer);
						listValuesCandidates.add(listVariances);
						listDifferences.add(difference);
					}
				}

				buclePhaseII:
				while((!listIDCandidates.isEmpty()))
				{
					posCustomerRef = listDifferences.indexOf(Collections.max(listDifferences));
					idCustomer = listIDCandidates.get(posCustomerRef);
					requestCustomer = Problem.getProblem().getRequestByIDCustomer(idCustomer);
	
					posCluster = listValuesCandidates.get(posCustomerRef).indexOf(Collections.min(listValuesCandidates.get(posCustomerRef)));
					requestCluster = listClusters.get(posCluster).getRequestCluster();
					capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(listClusters.get(posCluster).getIDCluster()));

					if(capacityDepot >= (requestCluster + requestCustomer))
					{
						requestCluster += requestCustomer;
						listClusters.get(posCluster).setRequestCluster(requestCluster);
						listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

						listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
						listIDCandidates.clear();
						listDifferences.clear();
						listValuesCandidates.clear();
						
						if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
		    			{
							if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
								solution.getClusters().add(listClusters.remove(posCluster));
							else
								listClusters.remove(posCluster);
		    			}
						
						continue bucleInit;
					}
					else
					{
						listIDCandidates.remove(posCustomerRef);
						listDifferences.remove(posCustomerRef);
						listValuesCandidates.remove(posCustomerRef);

						continue buclePhaseII;
					}
				}	

				ArrayList<Double> listNearestDist = new ArrayList<Double>();
				
				for(int k = 0; k < listCustomersToAssign.size(); k++)
				{
					idCustomer = listCustomersToAssign.get(k).getIDCustomer();
					listAverages = getListCriteriasByClusters(idCustomer, listClusters, 1); //pq 1
					
					listIDCandidates.add(idCustomer);
					listValuesCandidates.add(listAverages);
			
					posCluster = listAverages.indexOf(Collections.min(listAverages));
					ArrayList<Double> listDistCC = getDistancesInCluster(idCustomer, listClusters.get(posCluster));
					listNearestDist.add(listDistCC.get(listDistCC.indexOf(Collections.min(listDistCC))));

				}
				
				buclePhaseIII:
				while(!listIDCandidates.isEmpty())
				{	
					posCustomerRef = listNearestDist.indexOf(Collections.min(listNearestDist));
					idCustomer = listCustomersToAssign.get(posCustomerRef).getIDCustomer();
					requestCustomer = Problem.getProblem().getRequestByIDCustomer(idCustomer);
					
					posCluster = listValuesCandidates.get(posCustomerRef).indexOf(Collections.min(listValuesCandidates.get(posCustomerRef)));
					requestCluster = listClusters.get(posCluster).getRequestCluster();
					capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(listClusters.get(posCluster).getIDCluster()));

					if(capacityDepot >= (requestCluster + requestCustomer))
					{		
						requestCluster += requestCustomer;
						listClusters.get(posCluster).setRequestCluster(requestCluster);
						listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

						listCustomersToAssign.remove(Problem.getProblem().findPosCustomer(listCustomersToAssign, idCustomer));
						listValuesCandidates.clear();
						listIDCandidates.remove(posCustomerRef);
						
						if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
		    			{
							if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
								solution.getClusters().add(listClusters.remove(posCluster));
							else
								listClusters.remove(posCluster);
		    			}
						
						continue bucleInit;
					}
					else
					{
				
						listValuesCandidates.remove(posCustomerRef);
						listNearestDist.remove(posCustomerRef);
						listIDCandidates.remove(posCustomerRef);
						
						if(listIDCandidates.isEmpty())
							for(int j = 0; j < listCustomersToAssign.size(); j++)
								solution.getUnassignedItems().add(listCustomersToAssign.remove(j).getIDCustomer());
						
						continue buclePhaseIII;
					}
				}
			}
		}
	}
	
	@Override
	public Solution finish() {
		if(!listCustomersToAssign.isEmpty())					
			for(int j = 0; j < listCustomersToAssign.size(); j++)	
				solution.getUnassignedItems().add(listCustomersToAssign.get(j).getIDCustomer());
	
		if(!listClusters.isEmpty())
			for(int k = 0; k < listClusters.size(); k++)
				if(!listClusters.get(k).getItemsOfCluster().isEmpty())
					solution.getClusters().add(listClusters.get(k));
		
		return solution;

	}
	
	/*Este método devuelve la diferencia entre los dos clústers más cercanos a un cliente dado*/
	private double getDifference(ArrayList<Double> listValues, int posFirstMin){
		double difference = 0.0;
		
		double secondMin = listValues.get(0);
		int i = 1;
		
		if((posFirstMin == 0) && (listValues.size() > 1))
		{
			secondMin = listValues.get(1);
			i = 2; 
		}
		
		for (; i < listValues.size(); i++)
		{
			if((listValues.get(i) < secondMin) && (i != posFirstMin))
				secondMin = listValues.get(i);			
		}
		
		difference = secondMin - listValues.get(posFirstMin);
		
		return difference;		
	}
	
	/*Este método se encarga de devolver una lista con los valores de uno de los dos criterios de un cliente dado a todos los clústers*/	
	private ArrayList<Double> getListCriteriasByClusters(int idCustomer, ArrayList<Cluster> clusters, int criteria){
		ArrayList<Double> listValues = new ArrayList<Double>();
		
		int posCustomer = Problem.getProblem().getPosElement(idCustomer);
		
		switch(criteria)
		{
			case 1: 
			{
				for(int i = 0; i < clusters.size(); i++)
					listValues.add(getAvgByCluster(posCustomer, clusters.get(i), costMatrix));
				
				break;
			}
			case 2: 
			{
				for(int i = 0; i < clusters.size(); i++)
					listValues.add(getVarByCluster(posCustomer, clusters.get(i), costMatrix));
				
				break;
			}
		}
		
		return listValues;		
	}
	
	/*Este método devuelve la distancia promedio de un cliente a un cluster*/
	private double getAvgByCluster(int posCustomer, Cluster cluster, NumericMatrix costMatrix){
		double distances = 0.0;

		for(int i = 0; i < cluster.getItemsOfCluster().size(); i++)
			distances += costMatrix.getItem(posCustomer, Problem.getProblem().getPosElement(cluster.getItemsOfCluster().get(i)));
		
		distances += costMatrix.getItem(posCustomer, Problem.getProblem().getPosElement(cluster.getIDCluster()));
				
		return distances/(cluster.getItemsOfCluster().size() + 1);
	}
	
	/*Este método calcula la varianza del promedio de distancias de un cliente dado a un cluster*/
	private double getVarByCluster(int posCustomer, Cluster cluster, NumericMatrix costMatrix){
		int posElement = -1;	
		double distance = 0.0;
		double difference = 0.0;
		double avgDist = getAvgByCluster(posCustomer, cluster, costMatrix);
		
		for(int i = 0; i < cluster.getItemsOfCluster().size(); i++)
		{
			posElement = Problem.getProblem().getPosElement(cluster.getItemsOfCluster().get(i));
			distance = costMatrix.getItem(posCustomer, posElement);
			difference += Math.pow(distance - avgDist, 2);
		}

		posElement = Problem.getProblem().getPosElement(cluster.getIDCluster());
		distance = costMatrix.getItem(posCustomer, posElement);
		difference += Math.pow(distance - avgDist, 2);

		return difference/(cluster.getItemsOfCluster().size() + 1);
	}
	
	/*Este metodo devuelve una lista con las distancias de un cliente a cada cliente del cluster que se pasa por parametros*/
	private ArrayList<Double> getDistancesInCluster(int idCustomerRef, Cluster cluster){
		ArrayList<Double> listDistCluster = new ArrayList<Double>();
		
		int posCustomerRef = Problem.getProblem().getPosElement(idCustomerRef);
		
		int posCC = -1;
		
		for(int i = 0; i < cluster.getItemsOfCluster().size(); i++)
		{
			posCC = Problem.getProblem().getPosElement(cluster.getItemsOfCluster().get(i));
			listDistCluster.add(costMatrix.getItem(posCustomerRef, posCC));
		}

		if(listDistCluster.isEmpty())
		{
			posCC = Problem.getProblem().getPosElement(cluster.getIDCluster());
			listDistCluster.add(costMatrix.getItem(posCustomerRef, posCC));
		}

		return listDistCluster;	
	}		
}