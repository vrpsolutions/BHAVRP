import numpy as np
from typing import List
from ..heuristic import Heuristic
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer

"""
Clase abstracta que define los métodos necesarios para calcular urgencias
y ordenar depósitos por cercanía para cada cliente.
"""
class ByUrgency(Heuristic):
    
    # Método para calcular la urgencia.
    def calculate_urgency(self, first_distance: float, other_distance: float) -> float:
        return other_distance - first_distance
    
    # Método que para cada cliente crea un listado con los identificadores de los depósitos 
    # ordenados por cercanía.
    def get_depots_ordered(
        self,
        list_customers_to_assign: List[Customer],
        list_id_depots: List[int],
        cost_matrix: np.ndarray
    ) -> List[List[int]]:
        list_nearest_depots_by_customer: List[List[int]] = []
        
        for customer in list_customers_to_assign:
            list_nearest_depots_by_customer.append(
                self.get_depots_ordered_by_customer(
                    customer.get_id_customer(),
                    list_id_depots,
                    cost_matrix,
                    len(list_id_depots)
                )
            )
        return list_nearest_depots_by_customer
    
    # Método que para un cliente dado, crea un listado con los identificadores de los depósitos
    # ordenados por cercanía.
    def get_depots_ordered_by_customer(
        self,
        id_customer: int,
        list_id_depots: List[int],
        cost_matrix: np.ndarray,
        current_depots: int
    ) -> List[int]:
        list_closest_depots_by_customer: List[int] = []
        
        pos_customer = Problem.get_problem().get_pos_element(id_customer)
        total_customers = len(Problem.get_problem().get_customers())
        total_depots = len(list_id_depots)
        
        counter = 0
        
        while counter < current_depots:
            row_col_current_best_depot = cost_matrix[total_customers:, pos_customer].argmin()
            
            pos_closest_depot = row_col_current_best_depot
            id_closest_depot = Problem.get_problem().get_list_id_depots(pos_closest_depot)
            list_closest_depots_by_customer.append(id_closest_depot)
            
            cost_matrix[total_customers + pos_closest_depot, pos_customer] = float("inf")
            
            counter += 1
        return list_closest_depots_by_customer