import numpy as np
from typing import List
from ..by_not_urgency import ByNotUrgency
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class CoefficientPropagation(ByNotUrgency):
    degradation_coefficient = 0.5
    
    def __init__(self):
        super().__init__()
    
    def to_clustering(self):
        if not (0 <= self.degradation_coefficient <= 1):
            self.degradation_coefficient = 0.5
        
        solution = Solution()
        
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_coefficients: List[float] = self.initialize_coefficients()
        scaled_matrix: np.ndarray = self.initialize_scaled_matrix()
        
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers())
        
        total_items = len(list_customers_to_assign)
        
        while(
            list_customers_to_assign
            and list_clusters
            and not np.all(np.isinf(scaled_matrix))
        ):
            row, col = np.unravel_index(np.argmin(scaled_matrix), scaled_matrix.shape)
            
            id_customer = Problem.get_problem().get_list_id_customers()[col]
            request_customer = Problem.get_problem().get_request_by_id_customer(id_customer)
            
            if row >= total_items:
                id_depot = Problem.get_problem().get_list_id_depots()[row - total_items]
                pos_cluster = self.find_cluster(id_depot, list_clusters)
                pos_depot = row
            else:
                pos_cluster = self.get_pos_cluster(row, list_clusters)
                id_depot = list_clusters[pos_cluster].get_id_cluster()
                pos_depot = Problem.get_problem().get_pos_element(id_depot)
            
            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                    Problem.get_problem().get_depot_by_id_depot(id_depot)
                )
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)
                    
                    self.calculate_attraction_coefficient(col, row, list_coefficients)
                    scaled_matrix[row, col] = np.inf
                    
                    list_customers_to_assign.remove(Problem.get_problem().find_pos_customer(list_customers_to_assign, id_customer))
                    self.update_scaled_matrix(list_customers_to_assign, col, scaled_matrix, list_coefficients)
                    
                    if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                        scaled_matrix[pos_depot, :] = np.inf
                        for customer_id in list_clusters[pos_cluster].get_items_of_cluster():
                            pos_element_1 = Problem.get_problem().get_pos_element(customer_id)
                            scaled_matrix[pos_element_1, :] = np.inf
                            
                        if list_clusters[pos_cluster].get_items_of_cluster():
                            solution.get_clusters().append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(pos_cluster)
                    else:
                        if np.all(np.isinf(scaled_matrix[row, col])):
                            solution.get_total_unassigned_items().append(id_customer)
                            list_customers_to_assign.remove(Problem.get_problem().find_pos_customer(
                                list_customers_to_assign, id_customer)
                            )
                        scaled_matrix[row, col] = np.inf
                
            # Asignar los elementos no asignados a los clientes
            if list_customers_to_assign:
                solution.get_unassigned_items().extend([customer.get_id_customer() for customer in list_customers_to_assign])
            
            # Agregar clusters restantes
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)

        return solution
    
    def calculate_attraction_coefficient(
        self, 
        pos_customer: int,
        pos_element: int,
        coefficients: List[float]
    ):
        current_att_coeff = 1.0
        new_att_coeff = 1.0
        
        if pos_element < len(coefficients):
            current_att_coeff = coefficients[pos_element]
            new_att_coeff = min(1, (current_att_coeff +(current_att_coeff * self.degradation_coefficient)))
        
        coefficients[pos_customer] = new_att_coeff
    
    def initialize_coefficients(self) -> List[float]:
        coefficients = [0.0] * Problem.get_problem().get_total_customers()
        return coefficients

    def fill_list_scaled_distances(self) -> List[List[float]]:
        scaled_distances = []
        
        cost_matrix = np.array(Problem.get_problem().get_cost_matrix())
        list_id_customers = Problem.get_problem().get_list_id_customers()
        list_id_depots = Problem.get_problem().get_list_id_depots()
        
        total_items = len(list_id_customers)
        total_clusters = len(list_id_depots)
        
        for i in range(total_items):
            list_distances = [np.inf] * (total_items + total_clusters)
            scaled_distances.append(list_distances)
        
        for i in range(total_clusters):
            pos_depot_matrix = Problem.get_problem().get_pos_element(list_id_depots[i])
            list_id_depots = []
            for j in range(total_items):
                pos_customer_matrix = Problem.get_problem().get_pos_element(list_id_customers[j])
                list_distances.append(cost_matrix[pos_customer_matrix, pos_depot_matrix])
            list_distances.extend([np.inf] * total_clusters)
            scaled_distances.append(list_distances)
        
        return scaled_distances

    def initialize_scaled_matrix(self, scaled_distances: List[List[float]]) -> np.ndarray:
        scaled_matrix = np.array(scaled_distances)
        return scaled_matrix
    
    def update_scaled_matrix(
        self, 
        customers_to_assign: List[Customer], 
        pos_customer: int,
        scaled_matrix: np.ndarray,
        coefficients: List[float]
    ):
        cost_matrix = np.ndarray(Problem.get_problem().get_cost_matrix())
        
        for customer in customers_to_assign:
            pos_new_customer = Problem.get_problem().get_pos_element(customer.get_id_customer())
            distance = cost_matrix[pos_new_customer, pos_customer]
            scaled_distance = distance * coefficients[pos_customer]
            
            scaled_matrix[pos_customer, pos_new_customer] = scaled_distance