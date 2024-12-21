package cujae.inf.ic.om.factory.interfaces;

/* Enumerado que indica los tipos de métodos de asignación*/
public enum AssignmentType {

	BestCyclicAssignment	
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.basedcyclic.BestCyclicAssignment.class.getName(); 
		}
	}, 

	BestNearest
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.distance.BestNearest.class.getName(); 
		}
	}, 

	CLARA
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.clustering.partitional.Clara.class.getName(); 
		}
	}, 
	
	CoefficientPropagation
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.classical.cluster.CoefficientPropagation.class.getName(); 
		}
	}, 
	/*
	CURE
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.clustering.hierarchical.CURE.class.getName(); 
		}
	}, 
	*/
	CyclicAssignment
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.classical.cyclic.CyclicAssignment.class.getName(); 
		}
	}, 
	/*
	DBSCAN	
	{
	   @Override
	   public String toString()
	   {
		return cujae.inf.citi.om.heuristic.assignment.clustering.DBSCAN.class.getName(); 
	   }
    }*/
	
	Farthest_First
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.clustering.partitional.Farthest_First.class.getName(); 
		}
	},
	
	KMEANS
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.clustering.partitional.Kmeans.class.getName(); 
		}
	}, 
	/*
	Modified_KMEANS
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.Modified_KMEANS.class.getName(); 
		}
	}, 
	/*
	Modified_PAM
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.Modified_PAM.class.getName(); 
		}
	}, 
	*/
	NearestByCustomer
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.distance.NearestByCustomer.class.getName(); 
		}
	}, 
	
	NearestByDepot
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.distance.NearestByDepot.class.getName(); 
		}
	}, 
	
	PAM
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.clustering.partitional.PAM.class.getName(); 
		}
	}, 
	
	Parallel
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.classical.urgency.Parallel.class.getName(); 
		}
	}, 
	/*
	ParallelPlus
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.others.ParallelPlus.class.getName(); 
		}
	}, 
	*/
	RandomByElement
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.distance.RandomByElement.class.getName(); 
		}
	}, 
	/*
	RandomNearestByCustomer
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.RandomNearestByCustomer.class.getName(); 
		}
	}, 
	
	RandomNearestByDepot
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.RandomNearestByDepot.class.getName(); 
		}
	}, 
	*/
	RandomSequentialCyclic
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.basedcyclic.RandomSequentialCyclic.class.getName(); 
		}
	}, 
	
	RandomSequentialNearestByDepot
	/*{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.RandomSequentialNearestByDepot.class.getName(); 
		}
	}, 
	*/
	/*
	ROCK
	{
		@Override
		public String toString()
		{
			return cujae.inf.citi.om.heuristic.assignment.clustering.ROCK.class.getName(); 
		}
	}*/, 
	
	SequentialCyclic
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.basedcyclic.SequentialCyclic.class.getName(); 
		}
	}, 
	/*
	SequentialNearestByDepot
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.others.SequentialNearestByDepot.class.getName(); 
		}
	}, 
	*/
	Simplified
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.classical.urgency.Simplified.class.getName(); 
		}
	}, 
	
	Sweep
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.classical.urgency.Sweep.class.getName(); 
		}
	}, 
	
	ThreeCriteriaClustering
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.classical.cluster.ThreeCriteriaClustering.class.getName(); 
		}
	}, 

	UPGMC
	{
		@Override
		public String toString()
		{
			return cujae.inf.ic.om.heuristic.assignment.clustering.hierarchical.UPGMC.class.getName(); 
		}
	};
}