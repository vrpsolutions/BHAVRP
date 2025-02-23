import numpy as np
from typing import List
from .by_urgency import ByUrgency
from .i_urgency_with_mu import IUrgencyWithMU
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class Sweep(ByUrgency, IUrgencyWithMU):
    
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
        urgency_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        closest_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        
        self.list_depots_ordered = self.get_depots_ordered(self.list_customers_to_assign, self.list_id_depots, closest_matrix)
        self.mu_id_depot: int = self.find_cluster_with_mu(self.list_clusters)
        self.list_urgencies = self.get_list_urgencies(self.list_customers_to_assign, self.list_depots_ordered, urgency_matrix, self.mu_id_depot)

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
                    self.list_urgencies[pos_customer] = -1.0
                    self.list_depots_ordered[pos_customer] = None

                    # Recalcular la urgencia si es necesario
                    if id_closest_depot == self.mu_id_depot:
                        self.mu_id_depot = self.find_cluster_with_mu(self.list_clusters)

                        if id_closest_depot != self.mu_id_depot:
                            recalculate_urgency_matrix = self.initialize_cost_matrix(Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), self.distance_type)
                            list_urgencies = self.get_list_urgencies(self.list_customers_to_assign, self.list_depots_ordered, recalculate_urgency_matrix, self.mu_id_depot)
                else:
                    if capacity_depot > request_cluster:
                        self.list_depots_ordered[pos_customer].pop(0)
                        
                        if not self.list_depots_ordered[pos_customer]:
                            self.solution.get_unassigned_items().append(id_customer)
                            
                            self.list_customers_to_assign[pos_customer] = None
                            self.list_urgencies[pos_customer] = -1.0
                            self.list_depots_ordered[pos_customer] = None
                        else:
                            list_urgencies[pos_customer] = self.get_urgency(id_customer, self.list_depots_ordered[pos_customer], urgency_matrix, self.mu_id_depot)
                    else:
                        pos_depot = self.list_id_depots.index(id_closest_depot)

                        for i, depots_ordered in enumerate(self.list_depots_ordered):
                            if id_closest_depot in depots_ordered:
                                depots_ordered.remove(id_closest_depot)
                                if not depots_ordered:
                                    self.solution.get_unassigned_items().append(self.list_customers_to_assign[i].get_id_customer())
                                    
                                    self.list_customers_to_assign[i] = None
                                    self.list_urgencies[i] = -1.0
                                    self.list_depots_ordered[i] = None
                                else:
                                    list_urgencies[i] = self.get_urgency(self.list_customers_to_assign[i].get_id_customer(), depots_ordered, urgency_matrix, self.mu_id_depot)

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
    
    # Retorna el identificador del depósito cuyo cluster es el de mayor demanada insatisfecha.
    def find_cluster_with_mu(self, clusters: List[Cluster]) -> int:
        id_depot = clusters[0].get_id_cluster()
        mu_request = Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().get_depot_by_id_depot(id_depot)) - clusters[0].get_request_cluster()

        for cluster in clusters[1:]:
            cu_request = Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().get_depot_by_id_depot(cluster.get_id_cluster())) - cluster.get_request_cluster()

            if cu_request > mu_request:
                mu_request = cu_request
                id_depot = cluster.get_id_cluster()
        return id_depot
    
    # Método que retorna un listado con las urgencias de los clientes del listado entrado por parámetro.
    def get_list_urgencies(self, list_customers_to_assign: List[Customer], list_id_depots: List[List[int]], urgency_matrix: np.ndarray, mu_id_depot: int) -> List[float]:
        urgencies: List[float] = []

        if len(list_id_depots) > 1:
            for i, customer in enumerate(list_customers_to_assign):
                if customer is not None:
                    urgencies.append(self.get_urgency(customer.get_id_customer(), list_id_depots[i], urgency_matrix, mu_id_depot))
        else:
            for i, customer in enumerate(list_customers_to_assign):
                if customer is not None:
                    urgencies.append(self.get_urgency(customer.get_id_customer(), list_id_depots[0], urgency_matrix, mu_id_depot))
        return urgencies
    
    # Implementacion del método encargado de obtener la urgencia.
    def get_urgency(self, id_customer: int, list_id_depots: List[int], urgency_matrix: np.ndarray, mu_id_depot: int) -> float:
        urgency: float = 0.0
        closest_dist: float = 0.0
        mu_dist: float = 0.0
        
        total_customers = Problem.get_problem().get_total_customers()
        pos_matrix_customer = Problem.get_problem().get_pos_element(id_customer)
        pos_depot_matrix_closest = Problem.get_problem().get_pos_element(list_id_depots[0])
        
        closest_dist = urgency_matrix[(pos_depot_matrix_closest - total_customers), pos_matrix_customer]

        pos_mu_depot_matrix = Problem.get_problem().get_pos_element(mu_id_depot)
        
        mu_dist = urgency_matrix[(pos_mu_depot_matrix - total_customers), pos_matrix_customer]

        if mu_dist == float('inf'):
            urgency = closest_dist
        else:
            urgency = self.calculate_urgency(closest_dist, mu_dist)

        return urgency