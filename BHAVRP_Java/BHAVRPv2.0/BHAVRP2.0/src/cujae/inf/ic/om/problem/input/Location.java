package cujae.inf.ic.om.problem.input;

public class Location {
	private double axisX;
	private double axisY;
	
	public Location() {
		super();
	}

	public Location(double axisX, double axisY) {
		super();
		this.axisX = axisX;
		this.axisY = axisY;
	}

	public double getAxisX() {
		return axisX;
	}

	public void setAxisX(double axisX) {
		this.axisX = axisX;
	}

	public double getAxisY() {
		return axisY;
	}

	public void setAxisY(double axisY) {
		this.axisY = axisY;
	}
}