import numpy as np
from typing import List
from ..by_not_urgency import ByNotUrgency
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class CyclicAssignment(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        
    def to_clustering(self) -> Solution:
        self.initialize()
        self.assign()
        return self.finish()     
        
    def initialize(self):
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.total_clusters = len(Problem.get_problem().get_depots())
        self.total_items = len(self.list_customers_to_assign)       

    def assign(self):
        cost_matrix = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        items_selected = [(self.total_items + i) for i in range(self.total_clusters)]
        
        j = 0
        is_next: bool = True
        
        while any(self.list_customers_to_assign) and self.list_clusters:
            if is_next:
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().get_depot_by_id_depot(self.list_clusters[j].get_id_cluster()))
                pos_element_matrix = items_selected[j]
                pos_cluster = j

            min_value = np.min(cost_matrix) 
            row_best, col_best = np.where(cost_matrix == min_value)
            row_best, col_best = row_best[0], col_best[0]

            id_customer = Problem.get_problem().get_customers()[col_best].get_id_customer()
            request_customer = Problem.get_problem().get_customers()[col_best].get_request_customer()
            
            if pos_cluster != -1:
                request_cluster = self.list_clusters[pos_cluster].get_request_cluster()

                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    self.list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    self.list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)
                    
                    self.list_customers_to_assign[col_best] = None
                    
                    cost_matrix[:, col_best] = float('inf')
                    
                    if self.is_full_depot(self.list_customers_to_assign, self.list_clusters[pos_cluster].get_request_cluster(), capacity_depot):
                        if self.list_clusters[pos_cluster].get_items_of_cluster():
                            self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                        else:
                            self.list_clusters.pop(pos_cluster)
                        
                        items_selected.pop(j)
                    else:
                        j += 1
                    is_next = True
                else:
                    is_next = False
                    cost_matrix[row_best, col_best] = float('inf')
                    
                    if self.is_full_depot(self.list_customers_to_assign, self.list_clusters[pos_cluster].get_request_cluster(), capacity_depot):
                        if self.list_clusters[pos_cluster].get_items_of_cluster():
                            self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                        else:
                            self.list_clusters.pop(j)
                        
                        items_selected.pop(j)
                        is_next = True
                    else:
                        if np.all(cost_matrix[pos_element_matrix, :] == float('inf')):
                            if self.list_clusters[pos_cluster].get_items_of_cluster():
                                self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                            else:
                                self.list_clusters.pop(pos_cluster)
                            
                            items_selected.pop(j)
                            is_next = True
                
                if j == len(self.list_clusters):
                    if any(self.list_customers_to_assign) and self.list_clusters:
                        j = 0
                        items_selected.clear()
                        
                        for cluster in self.list_clusters:
                            if cluster.get_items_of_cluster():
                                pos_element_matrix = Problem.get_problem().get_pos_element(cluster.get_items_of_cluster()[-1])
                            items_selected.append(pos_element_matrix)
                        is_next = True
        
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