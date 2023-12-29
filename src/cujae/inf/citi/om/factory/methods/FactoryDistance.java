package cujae.inf.citi.om.factory.methods;

import java.lang.reflect.InvocationTargetException;

import cujae.inf.citi.om.factory.interfaces.DistanceType;
import cujae.inf.citi.om.factory.interfaces.IFactoryDistance;
import cujae.inf.citi.om.heuristic.distance.Distance;

/* Clase que implementa el Patrón Factory para la carga dinámica de una determinada distancia*/

public class FactoryDistance implements IFactoryDistance {
	
	public Distance createDistance(DistanceType distanceType) {
		
		String className = "cujae.inf.citi.om.heuristic.distance." + distanceType;
		Distance distance = null;
		
		try {
			distance = (Distance) FactoryLoader.getInstance(className);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return distance;
	}
	
	/*@Override
	public Distance createDistance(DistanceType distanceType) {
		Distance distance = null;
		
		try {
			distance = (Distance) Distance.class.getClassLoader().loadClass(distanceType.toString()).newInstance();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return distance;
	}*/
}
