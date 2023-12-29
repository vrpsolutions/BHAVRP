package cujae.inf.citi.om.problem.input;

import java.util.ArrayList;

public class Depot {
	
	private int idDepot;
	private Location locationDepot;
	private ArrayList<Fleet> fleetDepot;

	public Depot() {
		super();
		fleetDepot = new ArrayList<Fleet>();
	}
	 
	public Depot(int idDepot, Location locationDepot,
			ArrayList<Fleet> fleetDepot) {
		super();
		this.idDepot = idDepot;
		this.locationDepot = locationDepot;
		this.fleetDepot = fleetDepot;
	}

	public int getIDDepot() {
		return idDepot;
	}

	public void setIDDepot(int idDepot) {
		this.idDepot = idDepot;
	}

	public Location getLocationDepot() {
		return locationDepot;
	}

	public void setLocationDepot(Location locationDepot) {
		this.locationDepot = locationDepot;
	}

	public ArrayList<Fleet> getFleetDepot() {
		return fleetDepot;
	}

	public void setFleetDepot(ArrayList<Fleet> fleetDepot) {
		this.fleetDepot = fleetDepot;
	}
}
