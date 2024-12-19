package cujae.inf.ic.om.problem.input;

public class Customer {
	private int idCustomer;
	private double requestCustomer;
	private Location locationCustomer;
	
	public Customer() {
		super();
	}
	
	public Customer(int idCustomer, double requestCustomer,
			Location locationCustomer) {
		super();
		this.idCustomer = idCustomer;
		this.requestCustomer = requestCustomer;
		this.locationCustomer = locationCustomer;
	}

	public int getIDCustomer() {
		return idCustomer;
	}

	public void setIDCustomer(int idCustomer) {
		this.idCustomer = idCustomer;
	}

	public double getRequestCustomer() {
		return requestCustomer;
	}

	public void setRequestCustomer(double requestCustomer) {
		this.requestCustomer = requestCustomer;
	}

	public Location getLocationCustomer() {
		return locationCustomer;
	}

	public void setLocationCustomer(Location locationCustomer) {
		this.locationCustomer = locationCustomer;
	}
}