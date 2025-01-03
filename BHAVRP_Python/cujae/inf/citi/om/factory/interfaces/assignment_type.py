from enum import Enum

# Enumerado que indica los tipos de métodos de asignación.
class AssignmentType(Enum):
    BestCyclicAssignment = "cujae.inf.citi.om.assignment.others.basedcyclic.best_cyclic_assignment.BestCyclicAssignment"
    
    BestNearest = "cujae.inf.citi.om.assignment.others.distance.best_nearest.BestNearest"
    
    Clara = "cujae.inf.citi.om.assignment.clustering.partitional.clara.CLARA"
    
    CoefficientPropagation = "cujae.inf.citi.om.assignment.classical.cluster.coefficient_propagation.CoefficientPropagation"
    
    CyclicAssignment = "cujae.inf.citi.om.assignment.classical.cyclic.cyclic_assignment.CyclicAssignment"
    
    FarthestFirst = "cujae.inf.citi.om.assignment.clustering.partitional.farthest_first.FarthestFirst"
    
    Kmeans = "cujae.inf.citi.om.assignment.clustering.partitional.k_means.KMEANS"
    
    NearestByCustomer = "cujae.inf.citi.om.assignment.others.distance.nearest_by_customer.NearestByCustomer"
    
    NearestByDepot = "cujae.inf.citi.om.assignment.others.distance.nearest_by_depot.NearestByDepot"
    
    PAM = "cujae.inf.citi.om.assignment.clustering.partitional.pam.PAM"
    
    Parallel = "cujae.inf.citi.om.assignment.classical.urgency.parallel.Parallel"
    
    RandomByElement = "cujae.inf.citi.om.assignment.others.distance.random_by_element.RandomByElement"
    
    RandomSequentialCyclic = "cujae.inf.citi.om.assignment.others.basedcyclic.random_sequential_cyclic.RandomSequentialCyclic"
    
    SequentialCyclic = "cujae.inf.citi.om.assignment.others.basedcyclic.sequential_cyclic.SequentialCyclic"
    
    Simplified = "cujae.inf.citi.om.assignment.classical.urgency.simplified.Simplified"
    
    Sweep = "cujae.inf.citi.om.assignment.classical.urgency.sweep.Sweep"
    
    ThreeCriteriaClustering = "cujae.inf.citi.om.assignment.classical.cluster.three_criteria_clustering.ThreeCriteriaClustering"
    
    UPGMC = "cujae.inf.citi.om.assignment.clustering.hierarchical.upgmc.UPGMC"
    
    def __str__(self):
        return self.value