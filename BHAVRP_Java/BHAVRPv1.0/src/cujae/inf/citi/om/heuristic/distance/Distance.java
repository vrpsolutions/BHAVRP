package cujae.inf.citi.om.heuristic.distance;

/*Clase que modela como calcular la distancia entre dos puntos*/

public interface Distance {
	
	public abstract Double calculateDistance(double axisXPointOne, double axisYPointOne, double axisXPointTwo, double axisYPointTwo);

}

