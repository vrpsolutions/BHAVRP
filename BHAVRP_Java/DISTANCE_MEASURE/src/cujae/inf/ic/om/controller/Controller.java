package cujae.inf.ic.om.controller;

import cujae.inf.ic.om.distance.IDistance;
import cujae.inf.ic.om.factory.DistanceType;
import cujae.inf.ic.om.factory.interfaces.IFactoryDistance;
import cujae.inf.ic.om.factory.methods.FactoryDistance;

public class Controller {
	
	private IFactoryDistance factoryDistance;
	
	public Controller() {
		this.factoryDistance = new FactoryDistance();
	}

	public Double calculateDistance(DistanceType distanceType, double axisXPointOne, double axisYPointOne, double axisXPointTwo, double axisYPointTwo) {
		IDistance distanceStrategy = factoryDistance.createDistance(distanceType);
        
        if (distanceStrategy == null) {
        	throw new IllegalArgumentException("Tipo de distancia no soportado: " + distanceType);
        }
        return distanceStrategy.calculateDistance(axisXPointOne, axisYPointOne, axisXPointTwo, axisYPointTwo);
    }
	
    public String getUsedDistanceType(DistanceType distanceType) {
        IDistance distanceStrategy = factoryDistance.createDistance(distanceType);
        return distanceStrategy != null ? distanceStrategy.getClass().getSimpleName() : "Tipo de distancia no soportado";
    }
}
