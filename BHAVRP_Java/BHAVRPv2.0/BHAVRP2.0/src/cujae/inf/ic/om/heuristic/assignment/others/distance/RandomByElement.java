package cujae.inf.ic.om.heuristic.assignment.others.distance;

import java.util.ArrayList;
import java.util.Random;

import cujae.inf.ic.om.heuristic.assignment.classical.ByNotUrgency;
import cujae.inf.ic.om.problem.input.Customer;
import cujae.inf.ic.om.problem.input.Problem;

import cujae.inf.ic.om.problem.output.solution.Cluster;
import cujae.inf.ic.om.problem.output.solution.Solution;

/*Clase que modela como asignar los clientes a los depósitos de forma aleatoria la selección de ambos*/
public class RandomByElement extends ByNotUrgency {

	private Random random = new Random();
	private Solution solution = new Solution();
	
	private ArrayList<Cluster> listClusters;
	private ArrayList<Integer> listIDDepots;
	private ArrayList<Customer> listCustomersToAssign;

	public RandomByElement() {
		super();
	}

	@Override
	public Solution toClustering() {	
		initialize();
		assign();
		return finish();
	}

	@Override
	public void initialize() {
		listClusters = initializeClusters();
		listIDDepots = Problem.getProblem().getListIDDepots();
		listCustomersToAssign = Problem.getProblem().getCustomers();
	}
	
	@Override
	public void assign() {
		while((!listCustomersToAssign.isEmpty()) && (!listClusters.isEmpty()))
		{		
			int posRDMDepot = random.nextInt(listIDDepots.size());
			int idDepot = listIDDepots.get(posRDMDepot);
			double capacityDepot = Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepotByIDDepot(idDepot));

			int posRDMCustomer = random.nextInt(listCustomersToAssign.size());
			int idCustomer = listCustomersToAssign.get(posRDMCustomer).getIDCustomer();
			double requestCustomer = listCustomersToAssign.get(posRDMCustomer).getRequestCustomer();

			int posCluster = findCluster(idDepot, listClusters);

			if(posCluster != -1)
			{
				double requestCluster = listClusters.get(posCluster).getRequestCluster();

				if(capacityDepot >= (requestCluster + requestCustomer)) 
				{
					requestCluster += requestCustomer;

					listClusters.get(posCluster).setRequestCluster(requestCluster);
					listClusters.get(posCluster).getItemsOfCluster().add(idCustomer);

					listCustomersToAssign.remove(posRDMCustomer);
				}

				if(isFullDepot(listCustomersToAssign, requestCluster, capacityDepot))
				{
					listIDDepots.remove(posRDMDepot);

					if(!(listClusters.get(posCluster).getItemsOfCluster().isEmpty()))
						solution.getClusters().add(listClusters.remove(posCluster));
					else
						listClusters.remove(posCluster);
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
				if(!(listClusters.get(k).getItemsOfCluster().isEmpty()))
					solution.getClusters().add(listClusters.get(k));

		return solution;
	}
}