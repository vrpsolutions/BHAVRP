from typing import List
from .by_centroids import ByCentroids
from ..seed_type import SeedType
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class KMEANS(ByCentroids):
    
    def __init__(self):
        super().__init__()
        self.seed_type = SeedType.NEAREST_DEPOT
        self.list_id_elements: List[int]
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        self.list_centroids: List[Depot]
        
    def get_current_iteration(self) -> int:
        return self.current_iteration
    
    def to_clustering(self) -> Solution:
        self.initialize()
        self.assign()
        return self.finish()

    def initialize(self):
        self.list_id_elements = self.generate_elements()
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = []
        self.list_centroids = []
        self.current_iteration = 0
        
    def assign(self):    
        change: bool = True
        first: bool = True
        
        while change and self.current_iteration < self.count_max_iterations:
            if first:
                self.list_customers_to_assign = list(Problem.get_problem().get_customers())
                self.update_customer_to_assign()
                self.list_centroids = self.create_centroids()
                first = False
            else:
                self.clean_clusters(self.list_clusters)
                self.list_customers_to_assign = list(Problem.get_problem().get_customers())

            self.step_assignment(self.list_clusters, self.list_centroids)
            change = self.verify_centroids()
            
            self.current_iteration += 1
            print(f"ITERACIÓN ACTUAL: {self.current_iteration}")
            
    def finish(self) -> Solution:
        solution = Solution()
        
        if self.list_customers_to_assign:
            for customer in self.list_customers_to_assign:
                if customer is not None:
                    solution.get_unassigned_items().append(customer.get_id_customer())

        if self.list_clusters:
            for cluster in self.list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)
                    
        OSRMService.clear_distance_cache()
        
        return solution