import random
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class RandomByElement(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        self.list_id_depots: List[int]
        
    def to_clustering(self) -> Solution:
        self.initialize()
        self.assign()
        return self.finish()
        
    def initialize(self):
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.list_id_depots = list(Problem.get_problem().get_list_id_depots())
        
    def assign(self):
        while self.list_customers_to_assign and self.list_clusters:
            pos_rdm_depot = random.randint(0, len(self.list_id_depots) - 1)
            id_depot = self.list_id_depots[pos_rdm_depot]
            depot = Problem.get_problem().get_depot_by_id_depot(id_depot)
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(depot.get_id_depot())
            
            pos_rdm_customer = random.randint(0, len(self.list_customers_to_assign) - 1)
            customer = self.list_customers_to_assign[pos_rdm_customer]
            id_customer = customer.get_id_customer()
            request_customer = customer.get_request_customer()
            
            pos_cluster = self.find_cluster(id_depot, self.list_clusters)
            
            if pos_cluster != -1:
                cluster = self.list_clusters[pos_cluster]
                request_cluster = cluster.get_request_cluster()
                
                if capacity_depot >= (request_cluster + request_customer):
                    capacity_depot -= request_customer
                    cluster.set_request_cluster(request_cluster + request_customer)
                    cluster.get_items_of_cluster().append(id_customer)
                    
                    self.list_customers_to_assign.remove(customer)
                
                if self.is_full_depot(self.list_customers_to_assign, cluster.get_request_cluster(), capacity_depot):
                    self.list_id_depots.pop(pos_rdm_depot)
                    if cluster.get_items_of_cluster():
                        self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                    else:
                        self.list_clusters.pop(pos_cluster)

    def finish(self) -> Solution:
        if self.list_customers_to_assign:
            for customer in self.list_customers_to_assign:
                self.solution.get_unassigned_items().append(customer.get_id_customer())
                    
        if self.list_clusters:
            for cluster in self.list_clusters:
                if cluster.get_items_of_cluster():
                    self.solution.get_clusters().append(cluster)    
        
        return self.solution