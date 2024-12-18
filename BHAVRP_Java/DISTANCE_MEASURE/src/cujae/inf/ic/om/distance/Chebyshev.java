package cujae.inf.ic.om.distance;

/*Clase que modela como calcular la distancia mediante la fórmula de Chebyshev*/
public class Chebyshev implements IDistance {

	public Chebyshev() {
		super();
	}

	@Override
	public Double calculateDistance(double axisXPointOne, double axisYPointOne, double axisXPointTwo, double axisYPointTwo){
		double distance = 0.0;	
		distance = Math.max(Math.abs(axisXPointOne - axisXPointTwo), Math.abs(axisYPointOne - axisYPointTwo));
		
		return distance;
	}
}
