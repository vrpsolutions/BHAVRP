package cujae.inf.ic.om.distance;

/*Clase que modela como calcular la distancia mediante la fórmula Euclidiana*/
public class Euclidean implements IDistance{

	public Euclidean() {
		super();
	}

	@Override
	public Double calculateDistance(double axisXPointOne, double axisYPointOne, double axisXPointTwo, double axisYPointTwo){
		double distance = 0.0;	
		distance = Math.sqrt((Math.pow((axisXPointOne - axisXPointTwo), 2)) + Math.pow((axisYPointOne - axisYPointTwo), 2));	
		
		return distance;
	}
}
