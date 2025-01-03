import random
import numpy as np
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

"""
Clase que modela como asignar clientes a los depósitos dn forma secuencial por depósitos 
escogiendo el depósito al azar.
"""
class RandomSequentialCyclic(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
    
    def to_clustering(self) -> Solution:
        solution = Solution()
        
        # Inicialización de las listas.
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers().copy())
        list_id_depots: List[int] = list(Problem.get_problem().get_list_id_depots())
        
        # Obtener la matriz de costos.
        cost_matrix = np.array(Problem.get_problem().get_cost_matrix())
        
        # Inicializar variables auxiliares.
        is_full = False
        count_try = 0

        pos_element_matrix = -1
        id_customer = -1
        request_customer = 0.0

        random_gen = random.Random()
        pos_rdm_depot = -1
        capacity_depot = 0.0

        pos_cluster = -1
        request_cluster = 0.0
        
        while list_customers_to_assign and list_clusters:
            # Escoger un depósito aleatorio.
            pos_rdm_depot = random_gen.randint(0, len(list_id_depots) - 1)
            pos_element_matrix = Problem.get_problem().get_pos_element(list_id_depots[pos_rdm_depot])
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(list_id_depots[pos_rdm_depot])
            )
            pos_cluster = pos_rdm_depot

            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()

                while not is_full:
                    # Encontrar el cliente con el menor costo.
                    rc_best_element = (pos_element_matrix, cost_matrix[pos_element_matrix, :].argmin())

                    # Obtener la información del cliente.
                    id_customer = Problem.get_problem().get_customers()[rc_best_element[1]].get_id_customer()
                    request_customer = Problem.get_problem().get_customers()[rc_best_element[1]].get_request_customer()

                    # Verificar si cabe el cliente en el depósito.
                    if capacity_depot >= (request_cluster + request_customer):
                        request_cluster += request_customer

                        list_clusters[pos_cluster].set_request_cluster(request_cluster)
                        list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)

                        # Eliminar cliente de la lista.
                        list_customers_to_assign.remove(Problem.get_problem().find_pos_customer(list_customers_to_assign, id_customer))

                        # Actualizar la matriz de costos
                        cost_matrix[rc_best_element[0], :] = np.inf
                        cost_matrix[:, rc_best_element[1]] = np.inf

                        pos_element_matrix = Problem.get_problem().get_pos_element(
                            list_clusters[pos_cluster].get_items_of_cluster()[-1]
                        )

                        # Verificar si el depósito está lleno.
                        if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                            if list_clusters[pos_cluster].get_items_of_cluster():
                                solution.get_clusters().append(list_clusters.pop(pos_cluster))
                            else:
                                list_clusters.pop(pos_cluster)

                            list_id_depots.pop(pos_cluster)
                            is_full = True
                    else:
                        cost_matrix[rc_best_element[0], rc_best_element[1]] = np.inf
                        count_try += 1

                        if count_try >= len(list_customers_to_assign):
                            if list_clusters[pos_cluster].get_items_of_cluster():
                                solution.get_clusters().append(list_clusters.pop(pos_cluster))

                            list_id_depots.pop(pos_cluster)
                            is_full = True

                # Reiniciar variables.
                is_full = False
                count_try = 0

        # Asignar clientes no asignados
        if list_customers_to_assign:
            for customer in list_customers_to_assign:
                solution.get_unassigned_items().append(customer.get_id_customer())

        # Asignar clusters restantes
        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)
        
        return solution