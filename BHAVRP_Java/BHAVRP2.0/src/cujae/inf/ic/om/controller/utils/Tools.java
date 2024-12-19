package cujae.inf.ic.om.controller.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import cujae.inf.ic.om.problem.input.*;

import cujae.inf.ic.om.matrix.NumericMatrix;

public abstract class Tools {
	
	public static void randomOrdenate(){
		Random random = new Random();
		int posRandom = -1;
		ArrayList<Depot> orderedDepots = new ArrayList<Depot>();
		ArrayList<Depot> copyDepots = new ArrayList<Depot>(Problem.getProblem().getDepots());
		
		while(!Problem.getProblem().getDepots().isEmpty())
		{
			posRandom = random.nextInt(Problem.getProblem().getDepots().size());
			orderedDepots.add(Problem.getProblem().getDepots().remove(posRandom));
		}
		
		for(int i = 0; i < orderedDepots.size(); i++)
			Problem.getProblem().getDepots().add(orderedDepots.get(i));
		
		NumericMatrix copyCostMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		int currentDepots = 0;
		int totalDepots = Problem.getProblem().getTotalDepots();
		int totalCustomers = Problem.getProblem().getTotalCustomers();
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteCol(totalCustomers);			
			currentDepots++;
		}
			
		int posDepot;
		
		for(int j = 0; j < copyDepots.size(); j++)
		{
			posDepot = Problem.getProblem().findPosDepot(copyDepots, Problem.getProblem().getDepots().get(j).getIDDepot());
			copyCostMatrix.addCol((totalCustomers + j), Problem.getProblem().getCostMatrix().getCol(totalCustomers + posDepot));
		}	
		
		currentDepots = 0;
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteRow(totalCustomers);
			currentDepots++;
		}
		
		for(int k = 0; k < copyDepots.size(); k++)
		{
			posDepot = Problem.getProblem().findPosDepot(copyDepots, Problem.getProblem().getDepots().get(k).getIDDepot());
			copyCostMatrix.addRow((totalCustomers + k), Problem.getProblem().getCostMatrix().getRow(totalCustomers + posDepot));
		}	
		
		Problem.getProblem().setCostMatrix(copyCostMatrix);
	}
	
	public static void descendentOrdenate(){
		Depot depot = new Depot();
		ArrayList<Depot> copyDepots = new ArrayList<Depot>(Problem.getProblem().getDepots());
		int totalDepots = Problem.getProblem().getTotalDepots();
		
		for(int i = 0; i < (totalDepots - 1); i++)
		{
			for(int j = 0; j < (totalDepots - i - 1); j++)
			{
				if(Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepots().get(j + 1)) > Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepots().get(j)))
				{
					depot = Problem.getProblem().getDepots().get(j + 1);
					Problem.getProblem().getDepots().set((j + 1), Problem.getProblem().getDepots().get(j));
					Problem.getProblem().getDepots().set(j, depot);
				}
			}
		}
		
		NumericMatrix copyCostMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		int currentDepots = 0;
		int totalCustomers = Problem.getProblem().getTotalCustomers();
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteCol(totalCustomers);
			currentDepots++;
		}
			
		int posDepot;
		
		for(int j = 0; j < copyDepots.size(); j++)
		{
			posDepot = Problem.getProblem().findPosDepot(copyDepots, Problem.getProblem().getDepots().get(j).getIDDepot());
			copyCostMatrix.addCol((totalCustomers + j), Problem.getProblem().getCostMatrix().getCol(totalCustomers + posDepot));
		}	
		
		currentDepots = 0;
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteRow(totalCustomers);
			currentDepots++;
		}
		
		for(int k = 0; k < copyDepots.size(); k++)
		{
			posDepot = Problem.getProblem().findPosDepot(copyDepots, Problem.getProblem().getDepots().get(k).getIDDepot());
			copyCostMatrix.addRow((totalCustomers + k), Problem.getProblem().getCostMatrix().getRow(totalCustomers + posDepot));
		}	
		
		Problem.getProblem().setCostMatrix(copyCostMatrix);
	}

	public static void ascendentOrdenate(){
		Depot depot = new Depot();
		ArrayList<Depot> copyDepots = new ArrayList<Depot>(Problem.getProblem().getDepots());
		int totalDepots = Problem.getProblem().getTotalDepots();
		
		for(int i = 0; i < (totalDepots - 1); i++)
		{
			for(int j = 0; j < (totalDepots - i - 1); j++)
			{
				if(Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepots().get(j + 1)) < Problem.getProblem().getTotalCapacityByDepot(Problem.getProblem().getDepots().get(j)))
				{
					depot = Problem.getProblem().getDepots().get(j + 1);
					Problem.getProblem().getDepots().set((j + 1), Problem.getProblem().getDepots().get(j));
					Problem.getProblem().getDepots().set(j, depot);
				}
			}
		}
		
		NumericMatrix copyCostMatrix = new NumericMatrix(Problem.getProblem().getCostMatrix());
		int currentDepots = 0;
		int totalCustomers = Problem.getProblem().getTotalCustomers();
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteCol(totalCustomers);
			currentDepots++;
		}
			
		int posDepot;
		
		for(int j = 0; j < copyDepots.size(); j++)
		{
			posDepot = Problem.getProblem().findPosDepot(copyDepots, Problem.getProblem().getDepots().get(j).getIDDepot());
			copyCostMatrix.addCol((totalCustomers + j), Problem.getProblem().getCostMatrix().getCol(totalCustomers + posDepot));
		}	
		
		currentDepots = 0;
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteRow(totalCustomers);
			currentDepots++;
		}
		
		for(int k = 0; k < copyDepots.size(); k++)
		{
			posDepot = Problem.getProblem().findPosDepot(copyDepots, Problem.getProblem().getDepots().get(k).getIDDepot());
			copyCostMatrix.addRow((totalCustomers + k), Problem.getProblem().getCostMatrix().getRow(totalCustomers + posDepot));
		}	
		
		Problem.getProblem().setCostMatrix(copyCostMatrix);
	}

	public static void randomOrdenate(ArrayList<Customer> customers){
		Random random = new Random();
		int posRandom = -1;
		ArrayList<Customer> orderedCustomers = new ArrayList<Customer>();
		
		while(!customers.isEmpty())
		{
			posRandom = random.nextInt(customers.size());
			orderedCustomers.add(customers.remove(posRandom));
		}
		
		for(int i = 0; i < orderedCustomers.size(); i++)
			customers.add(orderedCustomers.get(i));
		
		/* no necesito actualizar la matriz*/
	}

	public static void descendentOrdenate(ArrayList<Customer> customers){
		Customer customer = new Customer();
		int totalCustomers = customers.size();
		
		for(int i = 0; i < (totalCustomers - 1); i++)
		{
			for(int j = 0; j < (totalCustomers - i - 1); j++)
			{
				if(Problem.getProblem().getRequestByIDCustomer(customers.get(j + 1).getIDCustomer()) > Problem.getProblem().getRequestByIDCustomer(customers.get(j).getIDCustomer()))
				{
					customer = customers.get(j + 1);
					customers.set((j + 1), customers.get(j));
					customers.set(j, customer);
					
					/* no actualizo infoproblem*/
				}
			}
		}
	}

	public static void ascendentOrdenate(ArrayList<Customer> customers){
		Customer customer = new Customer();
		int totalCustomers = customers.size();
		
		for(int i = 0; i < (totalCustomers - 1); i++)
		{
			for(int j = 0; j < (totalCustomers - i - 1); j++)
			{
				if(Problem.getProblem().getRequestByIDCustomer(customers.get(j + 1).getIDCustomer()) < Problem.getProblem().getRequestByIDCustomer(customers.get(j).getIDCustomer()))
				{
					customer = customers.get(j + 1);
					customers.set(j+1, customers.get(j));
					customers.set(j, customer);
				}
			}
		}
	}
	
	public static double truncateDouble(double number, int decimalPlace) {
        double numberRound;
        BigDecimal bigDec = new BigDecimal(number);
        bigDec = bigDec.setScale(decimalPlace, BigDecimal.ROUND_DOWN);
        numberRound = bigDec.doubleValue();
        
        return numberRound;
    }
}