package cujae.inf.citi.om.heuristic.controller;
import cujae.inf.citi.om.problem.input.*;

import libmatrix.cujae.inf.citi.om.matrix.NumericMatrix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public abstract class Tools {
	
	public static void randomOrdenate(){
		Random random = new Random();
		int posRandom = -1;
		ArrayList<Depot> orderedDepots = new ArrayList<Depot>();
		ArrayList<Depot> copyDepots = new ArrayList<Depot>(InfoProblem.getProblem().getDepots());
		
		while(!InfoProblem.getProblem().getDepots().isEmpty())
		{
			posRandom = random.nextInt(InfoProblem.getProblem().getDepots().size());
			orderedDepots.add(InfoProblem.getProblem().getDepots().remove(posRandom));
		}
		
		for(int i = 0; i < orderedDepots.size(); i++)
			InfoProblem.getProblem().getDepots().add(orderedDepots.get(i));
		
		NumericMatrix copyCostMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		int currentDepots = 0;
		int totalDepots = InfoProblem.getProblem().getTotalDepots();
		int totalCustomers = InfoProblem.getProblem().getTotalCustomers();
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteCol(totalCustomers);			
			currentDepots++;
		}
			
		int posDepot;
		
		for(int j = 0; j < copyDepots.size(); j++)
		{
			posDepot = InfoProblem.getProblem().findPosDepot(copyDepots, InfoProblem.getProblem().getDepots().get(j).getIDDepot());
			copyCostMatrix.addCol((totalCustomers + j), InfoProblem.getProblem().getCostMatrix().getCol(totalCustomers + posDepot));
		}	
		
		currentDepots = 0;
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteRow(totalCustomers);
			currentDepots++;
		}
		
		for(int k = 0; k < copyDepots.size(); k++)
		{
			posDepot = InfoProblem.getProblem().findPosDepot(copyDepots, InfoProblem.getProblem().getDepots().get(k).getIDDepot());
			copyCostMatrix.addRow((totalCustomers + k), InfoProblem.getProblem().getCostMatrix().getRow(totalCustomers + posDepot));
		}	
		
		InfoProblem.getProblem().setCostMatrix(copyCostMatrix);
	}
	
	public static void descendentOrdenate(){
		Depot depot = new Depot();
		ArrayList<Depot> copyDepots = new ArrayList<Depot>(InfoProblem.getProblem().getDepots());
		int totalDepots = InfoProblem.getProblem().getTotalDepots();
		
		for(int i = 0; i < (totalDepots - 1); i++)
		{
			for(int j = 0; j < (totalDepots - i - 1); j++)
			{
				if(InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepots().get(j + 1)) > InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepots().get(j)))
				{
					depot = InfoProblem.getProblem().getDepots().get(j + 1);
					InfoProblem.getProblem().getDepots().set((j + 1), InfoProblem.getProblem().getDepots().get(j));
					InfoProblem.getProblem().getDepots().set(j, depot);
				}
			}
		}
		
		NumericMatrix copyCostMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		int currentDepots = 0;
		int totalCustomers = InfoProblem.getProblem().getTotalCustomers();
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteCol(totalCustomers);
			currentDepots++;
		}
			
		int posDepot;
		
		for(int j = 0; j < copyDepots.size(); j++)
		{
			posDepot = InfoProblem.getProblem().findPosDepot(copyDepots, InfoProblem.getProblem().getDepots().get(j).getIDDepot());
			copyCostMatrix.addCol((totalCustomers + j), InfoProblem.getProblem().getCostMatrix().getCol(totalCustomers + posDepot));
		}	
		
		currentDepots = 0;
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteRow(totalCustomers);
			currentDepots++;
		}
		
		for(int k = 0; k < copyDepots.size(); k++)
		{
			posDepot = InfoProblem.getProblem().findPosDepot(copyDepots, InfoProblem.getProblem().getDepots().get(k).getIDDepot());
			copyCostMatrix.addRow((totalCustomers + k), InfoProblem.getProblem().getCostMatrix().getRow(totalCustomers + posDepot));
		}	
		
		InfoProblem.getProblem().setCostMatrix(copyCostMatrix);
	}

	public static void ascendentOrdenate(){
		Depot depot = new Depot();
		ArrayList<Depot> copyDepots = new ArrayList<Depot>(InfoProblem.getProblem().getDepots());
		int totalDepots = InfoProblem.getProblem().getTotalDepots();
		
		for(int i = 0; i < (totalDepots - 1); i++)
		{
			for(int j = 0; j < (totalDepots - i - 1); j++)
			{
				if(InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepots().get(j + 1)) < InfoProblem.getProblem().getTotalCapacityByDepot(InfoProblem.getProblem().getDepots().get(j)))
				{
					depot = InfoProblem.getProblem().getDepots().get(j + 1);
					InfoProblem.getProblem().getDepots().set((j + 1), InfoProblem.getProblem().getDepots().get(j));
					InfoProblem.getProblem().getDepots().set(j, depot);
				}
			}
		}
		
		NumericMatrix copyCostMatrix = new NumericMatrix(InfoProblem.getProblem().getCostMatrix());
		int currentDepots = 0;
		int totalCustomers = InfoProblem.getProblem().getTotalCustomers();
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteCol(totalCustomers);
			currentDepots++;
		}
			
		int posDepot;
		
		for(int j = 0; j < copyDepots.size(); j++)
		{
			posDepot = InfoProblem.getProblem().findPosDepot(copyDepots, InfoProblem.getProblem().getDepots().get(j).getIDDepot());
			copyCostMatrix.addCol((totalCustomers + j), InfoProblem.getProblem().getCostMatrix().getCol(totalCustomers + posDepot));
		}	
		
		currentDepots = 0;
		
		while(currentDepots < totalDepots)
		{
			copyCostMatrix.deleteRow(totalCustomers);
			currentDepots++;
		}
		
		for(int k = 0; k < copyDepots.size(); k++)
		{
			posDepot = InfoProblem.getProblem().findPosDepot(copyDepots, InfoProblem.getProblem().getDepots().get(k).getIDDepot());
			copyCostMatrix.addRow((totalCustomers + k), InfoProblem.getProblem().getCostMatrix().getRow(totalCustomers + posDepot));
		}	
		
		InfoProblem.getProblem().setCostMatrix(copyCostMatrix);
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
				if(InfoProblem.getProblem().getRequestByIDCustomer(customers.get(j + 1).getIDCustomer()) > InfoProblem.getProblem().getRequestByIDCustomer(customers.get(j).getIDCustomer()))
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
				if(InfoProblem.getProblem().getRequestByIDCustomer(customers.get(j + 1).getIDCustomer()) < InfoProblem.getProblem().getRequestByIDCustomer(customers.get(j).getIDCustomer()))
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
