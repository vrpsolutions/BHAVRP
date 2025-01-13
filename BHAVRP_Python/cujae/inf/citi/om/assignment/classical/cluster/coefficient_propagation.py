import numpy as np
from typing import List
from ..by_not_urgency import ByNotUrgency
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class CoefficientPropagation(ByNotUrgency):
    degradation_coefficient = 0.5
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        self.list_coefficients: List[float]
        self.list_scaled_distances: List[List[float]]
        self.scaled_matrix: np.ndarray

    def to_clustering(self):
        if not (0 <= self.degradation_coefficient <= 1):
            self.degradation_coefficient = 0.5
        
        self.initialize()
        self.assign()
        return self.finish()  
    
    def initialize(self):
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.list_coefficients = self.initialize_coefficients()
        self.list_scaled_distances = self.fill_list_scaled_distances()
        self.scaled_matrix = self.initialize_scaled_matrix()

    def assign(self):
        total_items = len(self.list_customers_to_assign)
        cost_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        
        while(any(self.list_customers_to_assign) and self.list_clusters):
            min_value = np.min(cost_matrix) 
            row, col = np.where(cost_matrix == min_value)
            row, col = row[0], col[0]
            
            id_customer = Problem.get_problem().get_list_id_customers()[col]
            request_customer = Problem.get_problem().get_request_by_id_customer(id_customer)
            
            if (row + total_items) >= total_items:
                id_depot = Problem.get_problem().get_list_id_depots()[row]
                pos_cluster = self.find_cluster(id_depot, self.list_clusters)
                pos_depot = row
            else:   
                pos_cluster = self.get_pos_cluster(row, self.list_clusters)
                id_depot = self.list_clusters[pos_cluster].get_id_cluster()
                pos_depot = Problem.get_problem().get_pos_element(id_depot)
            
            if pos_cluster != -1:
                cluster = self.list_clusters[pos_cluster]
                request_cluster = cluster.get_request_cluster()
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().get_depot_by_id_depot(id_depot))
                
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    capacity_depot -= request_cluster
                    
                    cluster.set_request_cluster(request_cluster)
                    cluster.get_items_of_cluster().append(id_customer)
                    
                    self.calculate_attraction_coefficient(col, row, self.list_coefficients)
                    #self.scaled_matrix[row, col] = float('inf')
                    
                    self.list_customers_to_assign[col] = None
                    self.update_scaled_matrix(self.list_clusters, col, self.scaled_matrix, self.list_coefficients)
                    
                    if self.is_full_depot(self.list_customers_to_assign, request_cluster, capacity_depot):
                        self.scaled_matrix[pos_depot, :] = float('inf')
                        cost_matrix[pos_depot, :] = float('inf')
                        
                        for customer_id in self.list_clusters[pos_cluster].get_items_of_cluster():
                            pos_element_1 = Problem.get_problem().get_pos_element(customer_id)
                            self.scaled_matrix[:, pos_element_1] = float('inf')
                            cost_matrix[:, pos_element_1] = float('inf')
                            
                        if self.list_clusters[pos_cluster].get_items_of_cluster():
                            self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                        else:
                            self.list_clusters.pop(pos_cluster)
                    else:
                        if np.all(np.isinf(self.scaled_matrix[row, col])):
                            self.solution.get_unassigned_items().append(id_customer)
                            filtered_customers = [customer for customer in self.list_customers_to_assign if customer is not None]
                            self.list_customers_to_assign[Problem.get_problem().find_pos_customer(filtered_customers, id_customer)] = None
                        
                        self.scaled_matrix[:, col] = float('inf')
                        cost_matrix[:, col] = float('inf')
                        
    def finish(self):
        if self.list_customers_to_assign:
            for customer in self.list_customers_to_assign:
                if customer is not None:
                    self.solution.get_unassigned_items().append(customer.get_id_customer())

        if self.list_clusters:
            for cluster in self.list_clusters:
                if cluster.get_items_of_cluster():
                    self.solution.get_clusters().append(cluster)
        
        OSRMService.clear_distance_cache()
        
        return self.solution
    
    def calculate_attraction_coefficient(self, pos_customer: int, pos_element: int, coefficients: List[float]):
        current_att_coeff = 1.0
        new_att_coeff = 1.0
        
        if (pos_element + len(coefficients)) < len(coefficients):
            current_att_coeff = coefficients[pos_element]
            new_att_coeff = min(1, (current_att_coeff +(current_att_coeff * self.degradation_coefficient)))
        else:
            new_att_coeff = 1.0
        
        coefficients[pos_customer] = new_att_coeff
    
    def initialize_coefficients(self) -> List[float]:
        coefficients = [0.0] * Problem.get_problem().get_total_customers()
        return coefficients

    def fill_list_scaled_distances(self) -> List[List[float]]:
        scaled_distances = []
        cost_matrix: np.ndarray = self.initialize_cost_matrix(Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), self.distance_type)
        
        list_id_customers = Problem.get_problem().get_list_id_customers()
        list_id_depots = Problem.get_problem().get_list_id_depots()
        
        id_to_index = {id_depot: idx for idx, id_depot in enumerate(list_id_depots)}
        indexed_depots = [id_to_index[id_depot] for id_depot in list_id_depots]
        
        total_items = len(list_id_customers)
        total_clusters = len(list_id_depots)
        
        for i in range(total_clusters):
            list_distances = [float('inf')] * (total_items)
            scaled_distances.append(list_distances)
        
        for i in range(total_clusters):
            pos_depot_matrix = indexed_depots[i]
                
            for j in range(total_items):
                pos_customer_matrix = Problem.get_problem().get_pos_element(list_id_customers[j])
                list_distances[j] = cost_matrix[pos_depot_matrix, pos_customer_matrix]
                
            #list_distances.extend([float('inf')] * total_clusters)
            scaled_distances[i] = list_distances
        
        return scaled_distances

    def initialize_scaled_matrix(self) -> np.ndarray:
        scaled_matrix: np.ndarray = np.full((4, 50), float('inf'))
        
        for i in range(len(self.list_scaled_distances)):
            for j in range(len(self.list_scaled_distances[i])):
                scaled_matrix[i, j] = self.list_scaled_distances[i][j]
        
        return scaled_matrix
    
    def update_scaled_matrix(self, clusters: List[Cluster], pos_customer: int, scaled_matrix: np.ndarray, coefficients: List[float]):
        cost_matrix: np.ndarray = self.initialize_cost_matrix(Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), self.distance_type)
        
        for cluster in clusters:
            pos_cluster = (Problem.get_problem().get_pos_element(cluster.get_id_cluster()) - len(self.list_customers_to_assign))
            distance = cost_matrix[pos_cluster, pos_customer]
            scaled_distance = distance * coefficients[pos_customer]
            
            scaled_matrix[pos_cluster, pos_customer] = scaled_distance

    def get_pos_cluster(self, pos_customer: int, clusters: List[Cluster]):
        id_customer = Problem.get_problem().get_list_id_customers()[pos_customer]
        i = 0
        found: bool = False
        
        while i < len(clusters):
            j = 0
            if clusters[i].get_items_of_cluster():
                while j < len(clusters[i].get_items_of_cluster()) and not found:
                    if clusters[i].get_items_of_cluster()[j] == id_customer:
                        pos_cluster = i
                        found = True
                    else:
                        j += 1
            i += 1
        return pos_cluster