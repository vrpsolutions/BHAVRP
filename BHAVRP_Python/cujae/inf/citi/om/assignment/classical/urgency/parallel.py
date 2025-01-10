import numpy as np
from typing import List
from .by_urgency import ByUrgency
from .i_urgency import IUrgency
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster
class Parallel(ByUrgency, IUrgency):
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        self.list_id_depots: List[int]
        self.list_depots_ordered: List[List[int]]
        self.list_urgencies: List[float]
        
    def to_clustering(self):
        self.initialize()
        self.assign()
        return self.finish()
        
    def initialize(self):
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.list_id_depots = list(Problem.get_problem().get_list_id_depots())
        
    def assign(self):
        urgency_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        closest_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        
        self.list_depots_ordered = self.get_depots_ordered(self.list_customers_to_assign, self.list_id_depots, closest_matrix)
        self.list_urgencies = self.get_list_urgencies(self.list_customers_to_assign, self.list_depots_ordered, urgency_matrix)

        while any(self.list_customers_to_assign) and self.list_clusters:
            pos_customer = self.list_urgencies.index(max(self.list_urgencies))
            customer = Problem.get_problem().get_customers()[pos_customer]
            id_customer = customer.get_id_customer()
            request_customer = customer.get_request_customer()
            
            id_closest_depot = self.list_depots_ordered[pos_customer][0]
            selected_depot: Depot = Problem.get_problem().get_depot_by_id_depot(id_closest_depot)
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(selected_depot)
            
            pos_cluster = self.find_cluster(id_closest_depot, self.list_clusters)
            
            if pos_cluster != -1:
                cluster: Cluster = self.list_clusters[pos_cluster]
                request_cluster = cluster.get_request_cluster()
                
                if capacity_depot >= (request_cluster + request_customer):
                    cluster.set_request_cluster(request_cluster + request_customer)
                    cluster.get_items_of_cluster().append(id_customer)

                    capacity_depot -= request_customer
                    
                    self.list_customers_to_assign[pos_customer] = None
                    self.list_urgencies[pos_customer] = 0.0
                    self.list_depots_ordered[pos_customer] = None
                else:
                    if capacity_depot > request_cluster:
                        self.list_depots_ordered[pos_customer].pop(0)
                        
                        if not self.list_depots_ordered[pos_customer]:
                            self.solution.get_unassigned_items().append(id_customer)
                            
                            self.list_customers_to_assign[pos_customer] = None
                            self.list_urgencies[pos_customer] = 0.0
                            self.list_depots_ordered[pos_customer] = None
                        else:
                            self.list_urgencies[pos_customer] = self.get_urgency(id_customer, self.list_depots_ordered[pos_customer], urgency_matrix)
                    else:
                        pos_depot = Problem.get_problem().find_pos_element(self.list_id_depots, id_closest_depot)
                        
                        for i in range(len(self.list_depots_ordered)):
                            current_pos_depot = Problem.get_problem().find_pos_element(self.list_depots_ordered[i], id_closest_depot)
                            if current_pos_depot != -1:
                                self.list_depots_ordered[i].pop(current_pos_depot)
                                
                                if not self.list_depots_ordered[i]:
                                    self.solution.get_unassigned_items().append(self.list_customers_to_assign[i].get_id_customer())
                                    
                                    self.list_customers_to_assign[i] = None
                                    self.list_urgencies[i] = 0.0
                                    self.list_depots_ordered[i] = None
                                else:
                                    self.list_urgencies[i] = self.get_urgency(self.list_customers_to_assign[i].get_id_customer(), self.list_depots_ordered[i], urgency_matrix)
                        self.list_id_depots.pop(pos_depot)
                        
                        if self.list_clusters[pos_cluster].get_items_of_cluster():
                            self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                        else:
                            self.list_clusters.pop(pos_cluster)
        
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
    
    # Método que retorna un listado con las urgencias de los clientes del listado entrado por parámetro.
    def get_list_urgencies(self, list_customers_to_assign: List[Customer], list_id_depots: List[List[int]], urgency_matrix: np.ndarray) -> List[float]:
        urgencies: List[float] = []

        if len(list_id_depots) > 1:
            for i, customer in enumerate(list_customers_to_assign):
                urgencies.append(self.get_urgency(customer.get_id_customer(), list_id_depots[i], urgency_matrix))
        else:
            for i, customer in enumerate(list_customers_to_assign):
                urgencies.append(self.get_urgency(customer.get_id_customer(), list_id_depots[0], urgency_matrix))
        return urgencies
    
    # Implementacion del método encargado de obtener la urgencia.
    def get_urgency(self, id_customer: int, list_id_depots: List[int], urgency_matrix: np.ndarray) -> float:
        urgency: float = 0.0
        closest_dist: float = 0.0
        other_dist: float = 0.0
        
        pos_matrix_customer: int = Problem.get_problem().get_pos_element(id_customer)
        pos_matrix_depot: int = Problem.get_problem().get_pos_element(list_id_depots[0])
        
        closest_dist = urgency_matrix[(len(self.list_customers_to_assign) - pos_matrix_depot), pos_matrix_customer]
        
        if len(list_id_depots) == 1:
            urgency = closest_dist
        else: 
            if len(list_id_depots) > 1:
                for i in range(1, len(list_id_depots)):
                    pos_matrix_depot = Problem.get_problem().get_pos_element(list_id_depots[i])
                    other_dist += urgency_matrix[(len(self.list_customers_to_assign) - pos_matrix_depot), pos_matrix_customer]
                    
                urgency = self.calculate_urgency(closest_dist, other_dist)
        return urgency