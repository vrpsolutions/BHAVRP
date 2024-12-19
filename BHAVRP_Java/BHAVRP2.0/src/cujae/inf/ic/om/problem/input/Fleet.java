package cujae.inf.ic.om.problem.input;

public class Fleet {
	private int countVehicles;
	private double capacityVehicle;

	public Fleet() {
		super();
	}

	public Fleet(int countVehicles, double capacityVehicle) {
		super();
		this.countVehicles = countVehicles;
		this.capacityVehicle = capacityVehicle;
	}

	public int getCountVehicles() {
		return countVehicles;
	}

	public void setCountVehicles(int countVehicles) {
		this.countVehicles = countVehicles;
	}

	public double getCapacityVehicle() {
		return capacityVehicle;
	}

	public void setCapacityVehicle(double capacityVehicle) {
		this.capacityVehicle = capacityVehicle;
	}
}