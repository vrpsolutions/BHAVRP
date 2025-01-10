import numpy as np
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class BestNearest(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        self.list_id_depots: List[int]
        
    def to_clustering(self):
        self.initialize()
        self.assign()
        return self.finish()
    
    def initialize(self):
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.list_id_depots = list(Problem.get_problem().get_list_id_depots())
        
    def assign(self):
        cost_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        
        while any(self.list_customers_to_assign) and self.list_clusters:
            min_value = np.min(cost_matrix) 
            row_best, col_best = np.where(cost_matrix == min_value)
            row_best, col_best = row_best[0], col_best[0]

            selected_customer = Problem.get_problem().get_customers()[col_best]
            request_customer = selected_customer.get_request_customer()
              
            id_depot = Problem.get_problem().get_list_id_depots()[row_best]    
            selected_depot: Depot = Problem.get_problem().get_depot_by_id_depot(id_depot)
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(selected_depot)
                
            pos_cluster = self.find_cluster(id_depot, self.list_clusters)

            if pos_cluster != -1:
                cluster: Cluster = self.list_clusters[pos_cluster]
                request_cluster = cluster.get_request_cluster()
        
                if capacity_depot >= (request_cluster + request_customer):
                    cluster.set_request_cluster(request_cluster + request_customer)
                    cluster.get_items_of_cluster().append(selected_customer.get_id_customer())

                    capacity_depot -= request_customer
                    self.list_customers_to_assign[col_best] = None
                    
                    if self.is_full_depot(self.list_customers_to_assign, cluster.get_request_cluster(), capacity_depot):
                        if cluster.get_items_of_cluster():
                            self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                    
                    cost_matrix[:, col_best] = float('inf')    
            
        if np.all(cost_matrix == float("inf")):
            for customer in self.list_customers_to_assign:
                if customer is not None:
                    self.solution.get_total_unassigned_items().append(selected_customer.get_id_customer())

              
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