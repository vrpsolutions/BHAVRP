package cujae.inf.ic.om.factory.interfaces;

import cujae.inf.ic.om.distance.IDistance;
import cujae.inf.ic.om.factory.DistanceType;

public interface IFactoryDistance {
	
	public IDistance createDistance(DistanceType distanceType);

}
