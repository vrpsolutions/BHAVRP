import numpy as np
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class BestNearest(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
        
    def to_clustering(self):
        solution = Solution()
        
        # Inicialización de listas.
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers())
        list_id_depots: List[int] = list(Problem.get_problem().get_list_id_depots())
        cost_matrix = np.array(Problem.get_problem().get_cost_matrix())
        total_customers = len(list_customers_to_assign)

        # Variables auxiliares.
        id_customer = -1
        pos_customer = -1
        request_customer = 0.0
        
        id_depot = -1
        pos_depot = -1
        capacity_depot = 0.0
        
        pos_cluster = -1
        request_cluster = 0.0
        
        while(
            list_customers_to_assign
            and list_clusters
            and not np.all(cost_matrix[:total_customers, :] == float("inf"))
        ):
            # Buscar el menor costo en la matriz.
            row, col = np.unravel_index(
                np.argmin(cost_matrix[:total_customers, :], axis=None),
                cost_matrix[:total_customers, :].shape
            )
            
            # Obtener información del depósito.
            pos_depot = row - total_customers
            id_depot = list_id_depots[pos_depot]
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(id_depot)
            )
            
            # Obtener información del cliente.
            pos_customer = col
            customer = Problem.get_problem().get_customers()[pos_customer]
            id_customer = customer.get_id_customer()
            request_customer = customer.get_request_customer()
            
            # Buscar el clúster correspondiente.
            pos_cluster = self.find_cluster(id_depot, list_clusters)
            
            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                
                # Verificar si se puede asignar el cliente al depósito.
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)
                    
                    cost_matrix[:, pos_customer] = float("inf")
                    list_customers_to_assign.remove(customer)
                else:
                    cost_matrix.setItem[row,pos_customer] = float("inf")
                
                # Verificar si el depósito está lleno.
                if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                    if list_clusters[pos_cluster].get_items_of_cluster():
                        solution.get_clusters().append(list_clusters.pop(pos_cluster))
                    else:
                        list_clusters.pop(pos_cluster)
                
                cost_matrix[row, :] = float("inf")
            
            # Manejar clientes no asignados.
            if list_customers_to_assign:
                for customer in list_customers_to_assign:
                    solution.get_unassigned_items().append(customer.get_id_customer())
            
            # Manejar clusters restantes.
            if list_clusters:
                for cluster in list_clusters:
                    if cluster.get_items_of_cluster():
                        solution.get_clusters().append(cluster)
                        
        return solution