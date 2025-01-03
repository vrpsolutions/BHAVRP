from enum import Enum

# Enumerado que indica los tipos de métodos de asignación.
class AssignmentType(Enum):
    BestCyclicAssignment = "cujae.inf.citi.om.assignment.others.basedcyclic.best_cyclic_assignment"
    
    BestNearest = "cujae.inf.citi.om.assignment.others.distance.best_nearest"
    
    Clara = "cujae.inf.citi.om.assignment.clustering.partitional.clara"
    
    CoefficientPropagation = "cujae.inf.citi.om.assignment.classical.cluster.coefficient_propagation"
    
    CyclicAssignment = "cujae.inf.citi.om.assignment.classical.cyclic.cyclic_assignment"
    
    FarthestFirst = "cujae.inf.citi.om.assignment.clustering.partitional.farthest_first"
    
    Kmeans = "cujae.inf.citi.om.assignment.clustering.partitional.k_means"
    
    NearestByCustomer = "cujae.inf.citi.om.assignment.others.distance.nearest_by_customer"
    
    NearestByDepot = "cujae.inf.citi.om.assignment.others.distance.nearest_by_depot"
    
    PAM = "cujae.inf.citi.om.assignment.clustering.partitional.pam"
    
    Parallel = "cujae.inf.citi.om.assignment.classical.urgency.parallel"
    
    RandomByElement = "cujae.inf.citi.om.assignment.others.distance.random_by_element"
    
    RandomSequentialCyclic = "cujae.inf.citi.om.assignment.others.basedcyclic.random_sequential_cyclic"
    
    SequentialCyclic = "cujae.inf.citi.om.assignment.others.basedcyclic.sequential_cyclic"
    
    Simplified = "cujae.inf.citi.om.assignment.classical.urgency.simplified"
    
    Sweep = "cujae.inf.citi.om.assignment.classical.urgency.sweep"
    
    ThreeCriteriaClustering = "cujae.inf.citi.om.assignment.classical.cluster.three_criteria_clustering"
    
    UPGMC = "cujae.inf.citi.om.assignment.clustering.hierarchical.upgmc"
    
    def __str__(self):
        return self.value