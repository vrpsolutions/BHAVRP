package cujae.inf.citi.om.heuristic.distance;

/*Clase que modela como calcular la distancia mediante la fórmula de Manhattan*/
public class Manhattan implements Distance {

	public Manhattan() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Double calculateDistance(double axisXPointOne, double axisYPointOne, double axisXPointTwo, double axisYPointTwo){
		double distance = 0.0;	
		distance = Math.abs((axisXPointOne - axisXPointTwo)) + Math.abs((axisYPointOne - axisYPointTwo));
		
		return distance;
	}
}
