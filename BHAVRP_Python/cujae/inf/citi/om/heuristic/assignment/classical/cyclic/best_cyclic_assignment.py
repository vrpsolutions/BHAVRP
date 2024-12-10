import numpy as np
from typing import List, Optional, Tuple
from cyclic import Cyclic
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

"""
Clase que modela como asignar el mejor cliente al último cliente - depósito 
asignando en forma paralela.
"""
class BestCyclicAssignment(Cyclic):
    
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
        total_items = len(list_customers_to_assign)
        total_clusters = len(Problem.get_problem().get_depots())
        
        items_selected = [(total_items + i) for i in range(total_clusters)]
        
        pos_element_matrix = -1
        capacity_depot = 0.0
        request_customer = 0.0
        request_cluster = 0.0
        pos_cluster = -1
        
        while list_customers_to_assign and list_clusters:
            # Determinar el depósito correspondiente al cliente seleccionado.
            row_col_best_all_selected = self.get_best_value_of_selected(items_selected, cost_matrix, total_items)
            
            if row_col_best_all_selected:
                pos_element_matrix, col_index = row_col_best_all_selected
            else: 
                break
            
            if pos_element_matrix >= total_items:
                pos_cluster = pos_element_matrix - total_items
            else: 
                pos_cluster = self.find_element_in_selected(items_selected, pos_element_matrix)
                
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(list_clusters[pos_cluster].get_id_cluster())
            )
            request_customer = Problem.get_problem().get_customers()[col_index].get_request_customer()
            
            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()

                # Verificar si el cliente puede ser asignado al depósito.
                if capacity_depot >= request_cluster + request_customer:
                    request_cluster += request_customer
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(
                        Problem.get_problem().get_customers()[col_index].get_id_customer()
                    )
                    
                    # Remover el cliente de la lista de clientes pendientes por asignar.
                    customer_to_remove = Problem.get_problem().find_pos_customer(
                        list_customers_to_assign,
                        Problem.get_problem().get_customers()[col_index].get_id_customer(),
                    )
                    list_customers_to_assign.remove(customer_to_remove)

                    # Actualizar la matriz de costos para reflejar la asignación.
                    cost_matrix[:, col_index] = np.inf
                    cost_matrix[pos_element_matrix, :] = np.inf

                    items_selected[pos_cluster] = col_index
                else:
                    cost_matrix[pos_element_matrix, col_index] = np.inf

                # Verificar si el depósito está lleno.
                if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                    if list_clusters[pos_cluster].get_items_of_cluster():
                        solution.clusters.append(list_clusters[pos_cluster])
                    list_clusters[pos_cluster] = None
                    items_selected[pos_cluster] = -1
                    cost_matrix[pos_element_matrix, :] = np.inf

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
    
    # Encuentra la combinación fila-columna con el menor costo en la matriz de costos,
    # basada en los elementos seleccionados.
    def get_best_value_of_selected(
        self, 
        items_selected: List[int], 
        cost_matrix: np.array, 
        total_items: int
    ) -> Optional[Tuple[int, int]]:
        best_row_col = None
        best_value = float('inf')
        
        if items_selected:
            for item in items_selected:
                if item != -1:
                    row = cost_matrix[item, :total_items]
                    col_index = np.argmin(row)
                    current_value = row[col_index]

                    if current_value < best_value:
                        best_row_col = (item, col_index)
                        best_value = current_value
                        
        return best_row_col
    
    # Busca la posición de un elemento específico en la lista de elementos seleccionados.
    def find_element_in_selected(self, items_selected: List[int], id_item: int):
        try:
            return items_selected.index(id_item)
        except ValueError:
            return -1