"""
Enumerado que indica los tipos de m�todos de asignaci�n
"""

from enum import Enum

class AssignmentType(Enum):
    BestCyclicAssignment = 1
    BestNearest = 2
    CLARA = 3
    CoefficientPropagation = 4
    CyclicAssignment = 5
    FarthestFirst = 6
    KMEANS = 7
    ModifiedKMEANS = 8
    ModifiedPAM = 9
    NearestByCustomer = 10
    NearestByDepot = 11
    PAM = 12
    Parallel = 13
    ParallelPlus = 14
    RandomByElement = 15
    RandomNearestByCustomer = 16
    RandomNearestByDepot = 17
    RandomSequentialCyclic = 18
    RandomSequentialNearestByDepot = 19
    SequentialCyclic = 20
    SequentialNearestByDepot = 21
    Simplified = 22
    Sweep = 23
    ThreeCriteriaClustering = 24
    UPGMC = 25
    
    def __str__(self) -> str:
        if self == AssignmentType.BestCyclicAssignment:
            return "cujae.inf.citi.om.heuristic.assignment.classical.cyclic.BestCyclicAssignment"
        elif self == AssignmentType.BestNearest:
            return "cujae.inf.citi.om.heuristic.assignment.classical.other.distance.BestNearest"
        elif self == AssignmentType.CLARA:
            return "cujae.inf.citi.om.heuristic.assignment.clustering.partitional.CLARA"
        elif self == AssignmentType.CoefficientPropagation:
            return "cujae.inf.citi.om.heuristic.assignment.classical.cluster.CoefficientPropagation"
        elif self == AssignmentType.CyclicAssignment:
            return "cujae.inf.citi.om.heuristic.assignment.classical.cyclic.CyclicAssignment"
        elif self == AssignmentType.FarthestFirst:
            return "cujae.inf.citi.om.heuristic.assignment.clustering.partitional.FarthestFirst"
        elif self == AssignmentType.KMEANS:
            return "cujae.inf.citi.om.heuristic.assignment.clustering.partitional.KMEANS"
        elif self == AssignmentType.ModifiedKMEANS:
            return "cujae.inf.citi.om.heuristic.assignment.others.ModifiedKMEANS"
        elif self == AssignmentType.ModifiedPAM:
            return "cujae.inf.citi.om.heuristic.assignment.others.ModifiedPAM"
        elif self == AssignmentType.NearestByCustomer:
            return "cujae.inf.citi.om.heuristic.assignment.classical.other.distance.NearestByCustomer"
        elif self == AssignmentType.NearestByDepot:
            return "cujae.inf.citi.om.heuristic.assignment.classical.other.distance.NearestByDepot"
        elif self == AssignmentType.PAM:
            return "cujae.inf.citi.om.heuristic.assignment.clustering.partitional.PAM"
        elif self == AssignmentType.Parallel:
            return "cujae.inf.citi.om.heuristic.assignment.classical.urgency.Parallel"
        elif self == AssignmentType.ParallelPlus:
            return "cujae.inf.citi.om.heuristic.assignment.classical.urgency.ParallelPlus"
        elif self == AssignmentType.RandomByElement:
            return "cujae.inf.citi.om.heuristic.assignment.classical.other.RandomByElement"
        elif self == AssignmentType.RandomNearestByCustomer:
            return "cujae.inf.citi.om.heuristic.assignment.others.RandomNearestByCustomer"
        elif self == AssignmentType.RandomNearestByDepot:
            return "cujae.inf.citi.om.heuristic.assignment.others.RandomNearestByDepot"
        elif self == AssignmentType.RandomSequentialCyclic:
            return "cujae.inf.citi.om.heuristic.assignment.classical.cyclic.RandomSequentialCyclic"
        elif self == AssignmentType.RandomSequentialNearestByDepot:
            return "cujae.inf.citi.om.heuristic.assignment.others.RandomSequentialNearestByDepot"
        elif self == AssignmentType.SequentialCyclic:
            return "cujae.inf.citi.om.heuristic.assignment.classical.cyclic.SequentialCyclic"
        elif self == AssignmentType.SequentialNearestByDepot:
            return "cujae.inf.citi.om.heuristic.assignment.others.SequentialNearestByDepot"
        elif self == AssignmentType.Simplified:
            return "cujae.inf.citi.om.heuristic.assignment.classical.urgency.Simplified"
        elif self == AssignmentType.Sweep:
            return "cujae.inf.citi.om.heuristic.assignment.classical.urgency.Sweep"
        elif self == AssignmentType.ThreeCriteriaClustering:
            return "cujae.inf.citi.om.heuristic.assignment.classical.cluster.ThreeCriteriaClustering"
        elif self == AssignmentType.UPGMC:
            return "cujae.inf.citi.om.heuristic.assignment.clustering.hierarchical.UPGMC"
        
        
        
        
        
        