package cujae.inf.citi.om.heuristic.distance;

/*Clase que modela como calcular la distancia mediante la fórmula Euclidiana*/

public class Euclidean implements Distance{

	public Euclidean() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Double calculateDistance(double axisXPointOne, double axisYPointOne, double axisXPointTwo, double axisYPointTwo){
		double distance = 0.0;	
		distance = Math.sqrt((Math.pow((axisXPointOne - axisXPointTwo), 2)) + Math.pow((axisYPointOne - axisYPointTwo), 2));	
		
		return distance;
	}
}
