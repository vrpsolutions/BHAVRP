import traceback
import numpy as np
from typing import List
from .assignment_template import AssignmentTemplate
from ..service.distance_type import DistanceType
from ..problem.input.problem import Problem
from ..problem.input.customer import Customer
from ..problem.input.depot import Depot
from ..problem.solution.solution import Solution
from ..problem.solution.cluster import Cluster

class Assignment(AssignmentTemplate):
    distance_type = DistanceType.EUCLIDEAN
    
    def __init__(self):
        super().__init__()
    
    # Método que busca la posición de un cluster en el listado de clusters.
    def find_cluster(self, id_cluster: int, clusters: List[Cluster]) -> int:
        pos_cluster: int = -1
        counter: int = 0
        found: bool = True
        
        while counter < len(clusters) and found:
            if clusters[counter].get_id_cluster() == id_cluster:
                found = False
                pos_cluster = counter
            else:
                counter += 1
        return pos_cluster
    
    # Método que crea la matriz de costos a partir del tipo de distancia.
    @staticmethod
    def initialize_cost_matrix(
        list_customers: List[Customer], 
        list_depots: List[Depot], 
        distance_type: DistanceType
    ) -> np.ndarray:
        cost_matrix: np.ndarray = None
        
        try:
            if distance_type == DistanceType.REAL:
                cost_matrix = Problem.get_problem().fill_cost_matrix_real(list_customers, list_depots)
            else:
                cost_matrix = Problem.get_problem().fill_cost_matrix(list_customers, list_depots, distance_type)
        except Exception:
            traceback.print_exc()
        
        return cost_matrix