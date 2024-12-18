package cujae.inf.ic.om.distance;

public class Haversine implements IDistance{

	public static final double EARTH_RADIUS_KM = 6371;
	
	public Haversine() {
		super();
	}

	@Override
	public Double calculateDistance(double axisXPointOne, double axisYPointOne, double axisXPointTwo, double axisYPointTwo){
		double distance = 0.0;
		
		double longitudeStart = axisXPointOne * (Math.PI/180);
		double latitudeStart = axisYPointOne * (Math.PI/180);
		
		double longitudeEnd = axisXPointTwo * (Math.PI/180);
		double latitudeEnd = axisYPointTwo * (Math.PI/180);
		
		double difLatitude = latitudeEnd - latitudeStart;
		double difLongtitude = longitudeEnd - longitudeStart;
		
		distance = Math.pow(Math.sin((difLatitude/2)), 2) + Math.cos(latitudeStart) * Math.cos(latitudeEnd) * Math.pow(Math.sin((difLongtitude/2)), 2);		
		distance = 2 * EARTH_RADIUS_KM * Math.atan2(Math.sqrt(distance), Math.sqrt((1 - distance)));
		
		return distance;
	}
}
