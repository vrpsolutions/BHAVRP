import numpy as np
from typing import List
from by_urgency import ByUrgency
from i_urgency import IUrgency
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class Parallel(ByUrgency, IUrgency):
    
    def __init__(self):
        super().__init__()
        
    def to_clustering(self) -> Solution:
        solution = Solution()
        
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers().copy())
        list_id_depots: List[int] = list(Problem.get_problem().get_list_id_depots())
        
        urgency_matrix: np.ndarray = Problem.get_problem().get_cost_matrix()
        closest_matrix: np.ndarray = Problem.get_problem().get_cost_matrix()
        
        list_depots_ordered: List[List[int]] = self.get_depots_ordered(
            list_customers_to_assign, list_id_depots, closest_matrix
        )
        list_urgencies: List[float] = self.get_list_urgencies(
            list_customers_to_assign, list_depots_ordered, urgency_matrix, -1
        )

        while list_customers_to_assign and list_clusters:
            pos_customer = self.get_pos_max_value(list_urgencies)
            customer = list_customers_to_assign[pos_customer]
            id_customer = customer.get_id_customer()
            request_customer = customer.get_request_customer()
            
            id_closest_depot = list_depots_ordered[pos_customer][0]
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(id_closest_depot)
            )
            pos_cluster = self.find_cluster(id_closest_depot, list_clusters)
            
            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)
                    
                    list_customers_to_assign.pop(pos_customer)
                    list_urgencies.pop(pos_customer)
                    list_depots_ordered.pop(pos_customer)
                else:
                    if capacity_depot > request_cluster:
                        list_depots_ordered[pos_customer].pop(0)
                        
                        if not list_depots_ordered[pos_customer]:
                            solution.get_unassigned_items().append(id_customer)
                            
                            list_customers_to_assign.pop(pos_customer)
                            list_urgencies.pop(pos_customer)
                            list_depots_ordered.pop(pos_customer)
                        else:
                            list_urgencies[pos_customer] = self.get_urgency(
                                id_customer, list_depots_ordered[pos_customer], urgency_matrix, -1
                            )
                    else:
                        pos_depot = Problem.get_problem().find_pos_element(
                            list_id_depots, id_closest_depot
                        )
                        
                        for i in range(len(list_depots_ordered)):
                            current_pos_depot = Problem.get_problem().find_pos_element(
                                list_depots_ordered[i], id_closest_depot
                            )
                            if current_pos_depot != -1:
                                list_depots_ordered[i].pop(current_pos_depot)
                                
                                if not list_depots_ordered[i]:
                                    solution.get_unassigned_items().append(list_customers_to_assign[i].get_id_customer())
                                    
                                    list_customers_to_assign.pop(i)
                                    list_urgencies.pop(i)
                                    list_depots_ordered.pop(i)
                                else:
                                    list_urgencies[i] = self.get_urgency(
                                        list_customers_to_assign[i].get_id_customer(), list_depots_ordered[i], urgency_matrix, -1
                                    )
                        list_id_depots.pop(pos_depot)
                        
                        if list_clusters[pos_cluster].get_items_of_cluster():
                            solution.get_clusters().append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(pos_cluster)
        
        if list_customers_to_assign:
            for customer in list_customers_to_assign:
                solution.get_unassigned_items().append(customer.get_id_customer())

        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)
                
        return solution
    
    # Método que retorna un listado con las urgencias de los clientes del listado entrado por parámetro.
    def get_list_urgencies(
        self, 
        list_customers_to_assign: List[Customer], 
        list_id_depots: List[List[int]], 
        urgency_matrix: np.ndarray
    ) -> List[float]:
        urgencies: List[float] = []

        if len(list_id_depots) > 1:
            for i, customer in enumerate(list_customers_to_assign):
                urgencies.append(
                    self.get_urgency(
                        customer.get_id_customer(),
                        list_id_depots[i],
                        urgency_matrix
                    )
                )
        else:
            for i, customer in enumerate(list_customers_to_assign):
                urgencies.append(
                    self.get_urgency(
                        customer.get_id_customer(),
                        list_id_depots[0],
                        urgency_matrix
                    )
                )
        return urgencies
    
    # Implementacion del método encargado de obtener la urgencia.
    def get_urgency(
        self, 
        id_customer: int, 
        list_id_depots: List[int], 
        urgency_matrix: np.ndarray
    ) -> float:
        urgency: float = 0.0
        closest_dist: float = 0.0
        other_dist: float = 0.0
        
        pos_matrix_customer: int = Problem.get_problem().get_pos_element(id_customer)
        pos_matrix_depot: int = Problem.get_problem().get_pos_element(list_id_depots[0])
        
        closest_dist = urgency_matrix[pos_matrix_depot, pos_matrix_customer]
        
        if len(list_id_depots) == 1:
            urgency = closest_dist
        else: 
            if len(list_id_depots) > 1:
                for i in range(1, len(list_id_depots)):
                    pos_matrix_depot = Problem.get_problem().get_pos_element(list_id_depots[i])
                    other_dist += urgency_matrix[pos_matrix_depot, pos_matrix_customer]
                    
                urgency = self.calculate_urgency(closest_dist, other_dist)
        return urgency