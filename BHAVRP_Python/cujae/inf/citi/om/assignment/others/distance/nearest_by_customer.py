import numpy as np
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class NearestByCustomer(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
    
    def to_clustering(self):
        solution = Solution()
        
        # Inicialización de listas.
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers())
        cost_matrix = np.array(Problem.get_problem().get_cost_matrix())
        total_customers = len(list_customers_to_assign)
        
        # Variables auxiliares.
        count_try = 0

        id_depot = -1
        pos_depot = -1
        capacity_depot = 0.0

        pos_customer = 0
        request_customer = 0.0

        pos_cluster = -1
        request_cluster = 0.0
        
        while (
            list_customers_to_assign
            and list_clusters
            and not np.all(cost_matrix[:total_customers, :] == float("inf"))
        ):
            # Buscar el depósito con el menor costo.
            min_cost_row, min_cost_col = np.unravel_index(
                np.argmin(cost_matrix[:total_customers, pos_customer:pos_customer + 1]),
                cost_matrix[:total_customers, pos_customer:pos_customer + 1].shape
            )
            min_cost_row += pos_customer
            
            pos_depot = min_cost_row - total_customers
            id_depot = Problem.get_problem().get_list_id_depots()[pos_depot]
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(id_depot)
            )
            
            request_customer = list_customers_to_assign[0].get_request_customer()
            cost_matrix[min_cost_row, min_cost_col] = float("inf")

            pos_cluster = self.find_cluster(id_depot, list_clusters)

            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()

                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    
                    id_customer = list_customers_to_assign[0].get_id_customer()
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)

                    list_customers_to_assign.pop(0)
                    pos_customer += 1
                    count_try = 0
                else:
                    count_try += 1
                    if count_try >= len(list_clusters):
                        count_try = 0
                        solution.get_unassigned_items().append(
                            list_customers_to_assign[0].get_id_customer()
                        )
                        list_customers_to_assign.pop(0)
                        pos_customer += 1

                if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                    if list_clusters[pos_cluster].get_items_of_cluster():
                        solution.get_clusters().append(list_clusters.pop(pos_cluster))
                    else:
                        list_clusters.pop(pos_cluster)

                    cost_matrix[min_cost_row, :] = float("inf")

        # Manejar clientes no asignados restantes.
        if list_customers_to_assign:
            for customer in list_customers_to_assign:
                solution.get_unassigned_items().append(customer.get_id_customer())

        # Manejar clusters restantes.
        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)
        
        return solution