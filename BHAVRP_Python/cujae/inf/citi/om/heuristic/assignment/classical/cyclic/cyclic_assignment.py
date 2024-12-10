import numpy as np
from typing import List
from cyclic import Cyclic
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

"""
Clase que modela como asignar el mejor cliente al último cliente - depósito 
asignado en forma paralela por depósitos.
"""
class CyclicAssignment(Cyclic):
    
    def __init__(self):
        super().__init__()
        
    def to_clustering(self) -> Solution:
        solution = Solution()
        
        # Inicialización de las listas.
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers().copy())
        
        # Obtener la matriz de costos.
        cost_matrix = np.array(Problem.get_problem().get_cost_matrix())
        
        # Inicializar variables auxiliares.
        total_clusters = len(Problem.get_problem().get_depots())
        total_items = len(list_customers_to_assign)
        
        items_selected = [(total_items + i) for i in range(total_clusters)]
        
        j = 0
        is_next: bool = True
        
        while list_customers_to_assign and list_clusters:
            # Determinar el depósito correspondiente al cliente seleccionado.
            if is_next:
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                    Problem.get_problem().get_depot_by_id_depot(list_clusters[j].get_id_cluster())
                )
                pos_element_matrix = items_selected[j]
                pos_cluster = j

            row_col_best_element = np.argmin(cost_matrix[pos_element_matrix, :total_items])
            
            id_customer = Problem.get_problem().get_customers()[row_col_best_element].get_id_customer()
            request_customer = Problem.get_problem().get_customers()[row_col_best_element].get_request_customer()
            
            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                
                # Verificar si el cliente puede ser asignado al depósito.
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)
                    list_customers_to_assign.remove(Problem.get_problem().find_pos_customer(list_customers_to_assign, id_customer))
                    
                    # Actualizar la matriz de costos para reflejar la asignación.
                    cost_matrix[:, row_col_best_element] = np.inf
                    cost_matrix[pos_element_matrix, :] = np.inf
                    
                    # Verificar si el depósito está lleno.
                    if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                        if list_clusters[pos_cluster].get_items_of_cluster():
                            solution.get_clusters().append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(pos_cluster)
                        
                        items_selected.pop(j)
                    else:
                        j += 1
                    is_next = True
                else:
                    # No es posible asignar el cliente al depósito actual.
                    is_next = False
                    cost_matrix[pos_element_matrix, row_col_best_element] = np.inf
                    
                    # Verificar si el depósito está lleno después de intentar la asignación.
                    if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                        if list_clusters[pos_cluster].get_items_of_cluster():
                            solution.get_clusters().append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(j)
                        
                        items_selected.pop(j)
                        is_next = True
                    else:
                        # Verificar si no quedan clientes asignables.
                        if np.all(cost_matrix[pos_element_matrix, :] == np.inf):
                            if list_clusters[pos_cluster].get_items_of_cluster():
                                solution.get_clusters().append(list_clusters.pop(pos_cluster))
                            else:
                                list_clusters.pop(pos_cluster)
                            
                            items_selected.pop(j)
                            is_next = True
                
                # Reiniciar el ciclo si se ha terminado la lista de clusters.
                if j == len(list_clusters):
                    if list_customers_to_assign and list_clusters:
                        j = 0
                        items_selected.clear()
                        
                        # Actualizar los elementos seleccionados para cada depósito.
                        for cluster in list_clusters:
                            if cluster.get_items_of_cluster():
                                pos_element_matrix = Problem.get_problem().get_pos_element(
                                    cluster.get_items_of_cluster()[-1]
                                )
                            items_selected.append(pos_element_matrix)
                        is_next = True
        
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