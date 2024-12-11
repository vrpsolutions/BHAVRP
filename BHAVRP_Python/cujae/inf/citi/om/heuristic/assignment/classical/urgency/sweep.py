import numpy as np
from typing import List
from by_urgency import ByUrgency
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

class Sweep(ByUrgency):
    
    def __init__(self):
        super().__init__()
        
    def to_clustering(self) -> Solution:
        solution = Solution()
        
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers().copy())
        list_id_depots: List[int] = list(Problem.get_problem().get_list_id_depots())
        
        urgency_matrix: np.ndarray = np.array(Problem.get_problem().get_cost_matrix())
        closest_matrix: np.ndarray = np.array(Problem.get_problem().get_cost_matrix())
        
        list_depots_ordered: List[List[int]] = self.get_depots_ordered(list_customers_to_assign, list_id_depots, closest_matrix)
        
        mu_id_depot: int = self.find_cluster_with_mu(list_clusters)
        list_urgencies: List[float] = self.get_list_urgencies(list_customers_to_assign, list_depots_ordered, urgency_matrix, mu_id_depot)

        while list_customers_to_assign and list_clusters:
            # Encontrar el cliente con mayor urgencia
            pos_customer = np.argmax(list_urgencies)
            id_customer = list_customers_to_assign[pos_customer].get_id_customer()
            request_customer = list_customers_to_assign[pos_customer].get_request_customer()

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

                    # Recalcular la urgencia si es necesario
                    if id_closest_depot == mu_id_depot:
                        mu_id_depot = self.find_cluster_with_mu(list_clusters)

                        if id_closest_depot != mu_id_depot:
                            urgency_matrix = np.array(Problem.get_problem().get_cost_matrix())
                            list_urgencies = self.get_list_urgencies(
                                list_customers_to_assign, list_depots_ordered, urgency_matrix, mu_id_depot
                            )
                else:
                    if capacity_depot > request_cluster:
                        list_depots_ordered[pos_customer].pop(0)

                        if not list_depots_ordered[pos_customer]:
                            solution.get_unassigned_items().append(id_customer)

                            list_customers_to_assign.pop(pos_customer)
                            list_urgencies.pop(pos_customer)
                            list_depots_ordered.pop(pos_customer)
                        else:
                            list_urgencies[pos_customer] = self.get_urgency(id_customer, list_depots_ordered[pos_customer], urgency_matrix, mu_id_depot)
                    else:
                        pos_depot = list_id_depots.index(id_closest_depot)

                        # Recorre los depósitos ordenados y gestiona la asignación
                        for i, depots_ordered in enumerate(list_depots_ordered):
                            if id_closest_depot in depots_ordered:
                                depots_ordered.remove(id_closest_depot)

                                if not depots_ordered:
                                    solution.get_unassigned_items().append(list_customers_to_assign[i].get_id_customer())

                                    list_customers_to_assign.pop(i)
                                    list_urgencies.pop(i)
                                    list_depots_ordered.pop(i)
                                else:
                                    list_urgencies[i] = self.get_urgency(list_customers_to_assign[i].get_id_customer(), depots_ordered, urgency_matrix, mu_id_depot)

                        list_id_depots.pop(pos_depot)

                        if cluster.get_items_of_cluster():
                            solution.get_clusters().append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(pos_cluster)

        # Asignación final de clientes no asignados
        if list_customers_to_assign:
            for customer in list_customers_to_assign:
                solution.get_unassigned_items().append(customer.get_id_customer())

        # Añadir los clusters restantes
        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)

        return solution
    
    # Retorna el identificador del depósito cuyo cluster es el de mayor demanada insatisfecha.
    def find_cluster_with_mu(self, clusters: List[Cluster]) -> int:
        id_depot = clusters[0].get_id_cluster()
        mu_request = Problem.get_problem().get_total_capacity_by_depot(
            Problem.get_problem().get_depot_by_id_depot(id_depot)
        ) - clusters[0].get_request_cluster()

        for cluster in clusters[1:]:
            cu_request = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(cluster.get_id_cluster())
            ) - cluster.get_request_cluster()

            if cu_request > mu_request:
                mu_request = cu_request
                id_depot = cluster.get_id_cluster()
        return id_depot
    
    # Implementacion del método encargado de obtener la urgencia.
    def get_urgency(
        self, 
        id_customer: int, 
        list_id_depots: List[int], 
        urgency_matrix: np.ndarray, 
        mu_id_depot: int
    ) -> float:
        pos_customer_matrix = Problem.get_problem().get_pos_element(id_customer)
        pos_depot_matrix_closest = Problem.get_problem().get_pos_element(list_id_depots[0])
        closest_dist = urgency_matrix[pos_depot_matrix_closest, pos_customer_matrix]

        pos_mu_depot_matrix = Problem.get_problem().get_pos_element(mu_id_depot)
        mu_dist = urgency_matrix[pos_mu_depot_matrix, pos_customer_matrix]

        if mu_dist == np.inf:
            return closest_dist
        else:
            return self.calculate_urgency(closest_dist, mu_dist)