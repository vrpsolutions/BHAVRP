package cujae.inf.ic.om.factory.methods;

import java.lang.reflect.InvocationTargetException;

import cujae.inf.ic.om.distance.IDistance;
import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.factory.interfaces.IFactoryDistance;

/* Clase que implementa el Patrón Factory para la carga dinámica de una determinada distancia*/
public class FactoryDistance implements IFactoryDistance {
	
	@Override
	public IDistance createDistance(DistanceType distanceType) {
		String className = "cujae.inf.ic.om.distance." + distanceType;
		IDistance distance = null;
		
		try {
			distance = (IDistance) FactoryLoader.getInstance(className);
			
		} catch (ClassNotFoundException e) {
			System.err.println("El tipo de distancia no existe: " + distanceType);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		return distance;
	}
}
