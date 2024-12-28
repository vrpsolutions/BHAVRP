from enum import Enum

# Enumerado que indica los tipos de métodos de asignación.
class AssignmentType(Enum):
    BestCyclicAssignment = "cujae.inf.ic.om.heuristic.assignment.others.basedcyclic.BestCyclicAssignment"
    
    BestNearest = "cujae.inf.ic.om.heuristic.assignment.others.distance.BestNearest"
    
    Clara = "cujae.inf.ic.om.heuristic.assignment.clustering.partitional.Clara"
    
    CoefficientPropagation = "cujae.inf.ic.om.heuristic.assignment.classical.cluster.CoefficientPropagation"
    
    CyclicAssignment = "cujae.inf.ic.om.heuristic.assignment.classical.cyclic.CyclicAssignment"
    
    FarthestFirst = "cujae.inf.ic.om.heuristic.assignment.clustering.partitional.Farthest_First"
    
    Kmeans = "cujae.inf.ic.om.heuristic.assignment.clustering.partitional.Kmeans"
    
    NearestByCustomer = "cujae.inf.ic.om.heuristic.assignment.others.distance.NearestByCustomer"
    
    NearestByDepot = "cujae.inf.ic.om.heuristic.assignment.others.distance.NearestByDepot"
    
    PAM = "cujae.inf.ic.om.heuristic.assignment.clustering.partitional.PAM"
    
    Parallel = "cujae.inf.ic.om.heuristic.assignment.classical.urgency.Parallel"
    
    RandomByElement = "cujae.inf.ic.om.heuristic.assignment.others.distance.RandomByElement"
    
    RandomSequentialCyclic = "cujae.inf.ic.om.heuristic.assignment.others.basedcyclic.RandomSequentialCyclic"
    
    SequentialCyclic = "cujae.inf.ic.om.heuristic.assignment.others.basedcyclic.SequentialCyclic"
    
    Simplified = "cujae.inf.ic.om.heuristic.assignment.classical.urgency.Simplified"
    
    Sweep = "cujae.inf.ic.om.heuristic.assignment.classical.urgency.Sweep"
    
    ThreeCriteriaClustering = "cujae.inf.ic.om.heuristic.assignment.classical.cluster.ThreeCriteriaClustering"
    
    UPGMC = "cujae.inf.ic.om.heuristic.assignment.clustering.hierarchical.UPGMC"
    
    def __str__(self):
        return self.value