from typing import List
from ..clustering import Clustering
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.output.cluster import Cluster

class Hierarchical(Clustering):
    
    def __init__(self):
        super().__init__()
        
    def find_depot_of_cluster(self, clusters: List[Cluster]) -> bool:
        found: bool = False
        i = 0
        
        while i < len(clusters) and not found:
            j = 0
            depots: List[Depot] = Problem.get_problem().get_depots()
            
            while j < len(depots) and not found:
                if clusters[i].get_id_cluster() == depots[j].get_id_depot():
                    found = True
                else: 
                    j += 1
            i += 1
        return found
    
    # Método que determina si existen clientes que puedan ser asignados al depósito a partir de su demanda.
    def is_full_depot(self, clusters: List[Cluster], customers: int, request_cluster: float, capacity_depot: float) -> bool:
        is_full: bool = True
        current_request: float = capacity_depot - request_cluster
        if current_request > 0:
            i = 0;
            while i < len(clusters) and i < customers and is_full:
                if clusters[i].get_request_cluster() <= current_request:
                    is_full = False
                else:
                    i += 1
        return is_full