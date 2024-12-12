from abc import ABC
from typing import List
from ...assignment import Assignment
from .....problem.input.problem import Problem
from .....problem.input.depot import Depot
from .....problem.output.solution.cluster import Cluster

class Hierarchical(Assignment, ABC):
    
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