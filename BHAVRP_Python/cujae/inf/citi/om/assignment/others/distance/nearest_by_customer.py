import numpy as np
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class NearestByCustomer(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        
    def to_clustering(self):
        self.initialize()
        self.assign()
        return self.finish()
    
    def initialize(self):
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.total_customers = len(self.list_customers_to_assign)
        
    def assign(self):
        cost_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        current_customer_index = -1
        
        while self.list_customers_to_assign and self.list_clusters:
            current_customer_index += 1
            current_customer = self.list_customers_to_assign[0]
            request_customer = current_customer.get_request_customer()
            
            cost_col = cost_matrix[:, current_customer_index] 
            row = np.argmin(cost_col)
            col = current_customer_index
            
            id_depot = Problem.get_problem().get_list_id_depots()[row]
            depot = Problem.get_problem().get_depot_by_id_depot(id_depot)
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(depot)

            pos_cluster = self.find_cluster(id_depot, self.list_clusters)

            if pos_cluster != -1:
                cluster = self.list_clusters[pos_cluster]
                request_cluster = cluster.get_request_cluster()

                if capacity_depot >= (request_cluster + request_customer):
                    cluster.set_request_cluster(request_cluster + request_customer)
                    cluster.get_items_of_cluster().append(current_customer.get_id_customer())

                    capacity_depot -= request_customer
                    self.list_customers_to_assign.pop(0)
                    
                    if self.is_full_depot(self.list_customers_to_assign, cluster.get_request_cluster(), capacity_depot):
                        if cluster.get_items_of_cluster():
                            self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                    
                    cost_matrix[row, col] = float("inf")    
            
            if np.all(cost_matrix == float("inf")):
                self.solution.get_total_unassigned_items().append(current_customer.get_id_customer())
                self.list_customers_to_assign.pop(0)

    def finish(self):
        if self.list_customers_to_assign:
            for customer in self.list_customers_to_assign:
                self.solution.get_unassigned_items().append(customer.get_id_customer())

        if self.list_clusters:
            for cluster in self.list_clusters:
                if cluster.get_items_of_cluster():
                    self.solution.get_clusters().append(cluster)
        
        OSRMService.clear_distance_cache()
        
        return self.solution