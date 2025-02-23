import random
import numpy as np
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class RandomSequentialCyclic(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        self.list_depots: List[Depot]
        self.list_id_depots: List[int]
        
    def to_clustering(self) -> Solution:
        self.initialize()
        self.assign()
        return self.finish()  

    def initialize(self):
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.list_depots = list(Problem.get_problem().get_depots())
        self.list_id_depots = list(Problem.get_problem().get_list_id_depots())
        
    def assign(self):    
        cost_matrix = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        is_full: bool = False
        
        while any(self.list_customers_to_assign) and self.list_clusters:
            pos_rdm_depot = random.randint(0, len(self.list_depots) - 1)
            current_depot = self.list_depots[pos_rdm_depot]
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(current_depot)
            current_capacity_depot = capacity_depot

            pos_cluster = pos_rdm_depot

            if pos_cluster != -1:
                cluster = self.list_clusters[pos_cluster]

                while not is_full and any(self.list_customers_to_assign):
                    request_cluster = cluster.get_request_cluster()
                    
                    cost_row = cost_matrix[((cluster.get_id_cluster() - len(self.list_customers_to_assign)) - 1), :] 
                    col = np.argmin(cost_row)
                    row = ((cluster.get_id_cluster() - len(self.list_customers_to_assign)) - 1)
                    
                    selected_customer = self.list_customers_to_assign[col]
                    id_customer = selected_customer.get_id_customer()
                    request_customer = selected_customer.get_request_customer()
                    
                    if current_capacity_depot >= request_customer:
                        current_capacity_depot -= request_customer
                        cluster.set_request_cluster(request_cluster + request_customer)
                        cluster.get_items_of_cluster().append(id_customer)
                        
                        self.list_customers_to_assign[col] = None
                        cost_matrix[:, col] = float('inf')
                        
                    else:
                        cost_matrix[row, col] = float('inf')
                        
                        if self.is_full_depot(self.list_customers_to_assign, request_cluster, capacity_depot):
                            if cluster.get_items_of_cluster():
                                self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                            else:
                                self.list_clusters.pop(pos_cluster)
                                
                            is_full = True
                            self.list_depots.pop(0)

                is_full = False

    def finish(self) -> Solution:
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