import numpy as np
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class NearestByDepot(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
    
    def to_clustering(self):
        solution = Solution()
        
        # Inicialización de listas.
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers())
        list_id_depots: List[int] = list(Problem.get_problem().get_list_id_depots())
        cost_matrix = np.array(Problem.get_problem().get_cost_matrix())
        
        # Variables auxiliares.
        rc_best_customer = None
        is_next = True

        id_depot = -1
        pos_depot = 0
        capacity_depot = 0.0
        pos_depot_matrix = -1

        id_customer = -1
        pos_customer = -1
        request_customer = 0.0

        pos_cluster = -1
        request_cluster = 0.0
        
        while(
            list_customers_to_assign
            and list_clusters
            and not np.all(cost_matrix == float("inf"))
        ):
            if is_next:
                id_depot = list_id_depots[pos_depot]
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                    Problem.get_problem().get_depot_by_id_depot(id_depot)
                )
                pos_depot_matrix = Problem.get_problem().get_pos_element(id_depot)
                
                # Buscar el cliente más cercano.
                min_cost_row, min_cost_col = np.unravel_index(np.argmin(cost_matrix[pos_depot_matrix, :]), cost_matrix.shape)
                pos_customer = min_cost_col
                id_customer = list_customers_to_assign[pos_customer].get_id_customer()
                request_customer = list_customers_to_assign[pos_customer].get_request_customer()

                # Marcar la posición como procesada.
                cost_matrix[pos_depot_matrix, pos_customer] = float("inf")

                # Buscar el cluster correspondiente.
                pos_cluster = self.find_cluster(id_depot, list_clusters)
                
                if pos_cluster != -1:
                    request_cluster = list_clusters[pos_cluster].get_request_cluster()

                    # Verificar si el depósito tiene suficiente capacidad.
                    if capacity_depot >= (request_cluster + request_customer):
                        request_cluster += request_customer
                        list_clusters[pos_cluster].set_request_cluster(request_cluster)
                        list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)

                        # Marcar las posiciones correspondientes como infinito positivo.
                        cost_matrix[pos_depot_matrix, pos_customer:] = float("inf")
                        list_customers_to_assign.pop(pos_customer)

                        # Si el depósito está lleno, mover al siguiente.
                        if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                            if list_clusters[pos_cluster].get_items_of_cluster():
                                solution.get_clusters().append(list_clusters.pop(pos_cluster))
                            else:
                                list_clusters.pop(pos_cluster)

                            list_id_depots.pop(pos_depot)
                            pos_depot -= 1

                        pos_depot += 1
                        if pos_depot >= len(list_id_depots):
                            pos_depot = 0

                        is_next = True
                    else:
                        is_next = False

                        # Si el depósito está lleno, avanzar al siguiente.
                        if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                            if list_clusters[pos_cluster].get_items_of_cluster():
                                solution.get_clusters().append(list_clusters.pop(pos_cluster))
                            else:
                                list_clusters.pop(pos_cluster)

                            list_id_depots.pop(pos_depot)
                            pos_depot -= 1

                            pos_depot += 1
                            if pos_depot >= len(list_id_depots):
                                pos_depot = 0

                            is_next = True

            # Agregar los clientes no asignados.
            if list_customers_to_assign:
                for customer in list_customers_to_assign:
                    solution.get_unassigned_items().append(customer.get_id_customer())

            # Agregar los clusters restantes.
            if list_clusters:
                for cluster in list_clusters:
                    if cluster.get_items_of_cluster():
                        solution.get_clusters().append(cluster)
        
        return solution