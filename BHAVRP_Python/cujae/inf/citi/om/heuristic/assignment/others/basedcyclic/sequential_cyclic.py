import numpy as np
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

"""
Clase que modela como asignar elementos en forma secuencial por depósitos.
"""
class SequentialCyclic(ByNotUrgency):
    
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
        
        total_items = len(list_customers_to_assign)
        
        # Inicializar variables auxiliares.
        pos_element_matrix = -1
        is_full = False
        i = 0
        count_try = 0
        
        capacity_depot = 0.0
        request_customer = 0.0
        id_customer = -1
        pos_cluster = -1
        request_cluster = 0.0
        
        while list_customers_to_assign and list_clusters:
            # Escoger un depósito.
            pos_element_matrix = total_items + i
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(list_id_depots[0])
            )
            pos_cluster = 0
            
            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                
                while not is_full:
                    # Encontrar el cliente con el menor costo.
                    col_index = np.argmin(cost_matrix[pos_element_matrix, :total_items])
                    
                    # Obtener la información del cliente.
                    id_customer = Problem.get_problem().get_customers()[col_index].get_id_customer()
                    request_customer = Problem.get_problem().get_customers()[col_index].get_request_customer()
                    
                    # Verificar si cabe el cliente en el depósito.
                    if capacity_depot >= (request_cluster + request_customer):
                        request_cluster += request_customer
                        
                        list_clusters[pos_cluster].set_request_cluster(request_cluster)
                        list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)
                        
                        # Eliminar cliente de la lista.
                        list_customers_to_assign = [
                            customer for customer in list_customers_to_assign
                            if customer.get_id_customer() != id_customer
                        ]
                        
                        # Actualizar la matriz de costos.
                        cost_matrix[pos_element_matrix, col_index] = np.inf
                        cost_matrix[:, col_index] = np.inf
                        
                        # Verificar si el depósito está lleno.
                        if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                            if list_clusters[pos_cluster].get_items_of_cluster():
                                solution.clusters.append(list_clusters.pop(pos_cluster))
                            else:
                                list_clusters.pop(pos_cluster)
                            
                            is_full = True
                            list_id_depots.pop(0)
                    else:
                        cost_matrix[pos_element_matrix, col_index] = np.inf
                        count_try += 1
                        
                        if count_try >= len(list_customers_to_assign):
                            if list_clusters[pos_cluster].get_items_of_cluster():
                                solution.clusters.append(list_clusters[pos_cluster])
                            list_clusters.pop(pos_cluster)
                            list_id_depots.pop(0)
                            is_full = True

                # Reiniciar variables.
                is_full = False
                count_try = 0
                i += 1
                
        # Asignar clientes no asignados.
        if list_customers_to_assign:
            for customer in list_customers_to_assign:
                solution.unassigned_items.append(customer.get_id_customer())

        # Asignar clusters no vacíos.
        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.clusters.append(cluster)
        
        return solution