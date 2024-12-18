package cujae.inf.ic.om.distance;

/*Clase que modela como calcular la distancia mediante la fórmula de Manhattan*/
public class Manhattan implements IDistance {

	public Manhattan() {
		super();
	}

	@Override
	public Double calculateDistance(double axisXPointOne, double axisYPointOne, double axisXPointTwo, double axisYPointTwo){
		double distance = 0.0;	
		distance = Math.abs((axisXPointOne - axisXPointTwo)) + Math.abs((axisYPointOne - axisYPointTwo));
		
		return distance;
	}
}
