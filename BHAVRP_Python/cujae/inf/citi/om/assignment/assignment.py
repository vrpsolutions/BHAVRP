import traceback
import numpy as np
from typing import List
from .assignment_template import AssignmentTemplate
from ..service.distance_type import DistanceType
from ..problem.input.problem import Problem
from ..problem.input.customer import Customer
from ..problem.input.depot import Depot
from ..problem.solution.solution import Solution
from ..problem.solution.cluster import Cluster

class Assignment(AssignmentTemplate):
    distance_type = DistanceType.EUCLIDEAN
    
    def __init__(self):
        super().__init__()
    
    # Método que busca la posición de un cluster en el listado de clusters.
    def find_cluster(self, id_cluster: int, clusters: List[Cluster]) -> int:
        pos_cluster: int = -1
        counter: int = 0
        found: bool = True
        
        while counter < len(clusters) and found:
            if clusters[counter].get_id_cluster() == id_cluster:
                found = False
                pos_cluster = counter
            else:
                counter += 1
        return pos_cluster
    
    # Método que crea la matriz de costos a partir del tipo de distancia.
    @staticmethod
    def initialize_cost_matrix(
        list_customers: List[Customer], 
        list_depots: List[Depot], 
        distance_type: DistanceType
    ) -> np.ndarray:
        cost_matrix: np.ndarray = None
        
        try:
            if distance_type == DistanceType.REAL:
                cost_matrix = Problem.get_problem().fill_cost_matrix_real(list_customers, list_depots)
            else:
                cost_matrix = Problem.get_problem().fill_cost_matrix(list_customers, list_depots, distance_type)
        except Exception:
            traceback.print_exc()
        
        return cost_matrix
    
    # Método de asignación de clientes a clusters según los depósitos, basado en la matriz de costos, 
    # la demanda de los clientes y la capacidad de los depósitos.
    def step_assignment(self, list_clusters: List[Cluster], list_centroids: List[Depot]) -> List[Cluster]:
        total_customers = len(self.list_customers_to_assign)
        
        print("--------------------------------------------------------------------")
        print(f"PROCESO DE ASIGNACIÓN")
        
        cost_matrix: np.ndarray = self.initialize_cost_matrix(
            self.list_customers_to_assign, 
            list_centroids,
            self.distance_type
        )

        while self.list_customers_to_assign:
            
            
            min_value = np.min(cost_matrix) 
            print(min_value)
            row_best, col_best = np.where(cost_matrix == min_value)
            row_best, col_best = row_best[0], col_best[0]
            
            selected_customer = self.list_customers_to_assign[col_best]
            id_customer = selected_customer.get_id_customer()
            request_customer = selected_customer.get_request_customer()
            
            print("-----------------------------------------------------------")
            print(f"ID CLIENTE SELECCIONADO: {id_customer}")
            print(f"POSICIÓN DEL CLIENTE SELECCIONADO: {col_best}")
            print(f"DEMANDA DEL CLIENTE SELECCIONADO: {request_customer}")
            
            selected_depot: Depot = Problem.get_problem().get_depots()[row_best]
            id_depot = selected_depot.get_id_depot()
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(selected_depot)
            
            print(f"ID DEPOSITO SELECCIONADO: {id_depot}")
            print(f"POSICIÓN DEL DEPOSITO SELECCIONADO: {row_best}")
            print(f"CAPACIDAD TOTAL DEL DEPOSITO SELECCIONADO: {capacity_depot}")

            pos_cluster = self.find_cluster(id_depot, list_clusters)
            
            print(f"POSICION DEL CLUSTER: {pos_cluster}")

            if pos_cluster != -1:
                cluster: Cluster = self.list_clusters[pos_cluster]
                
                print(f"DEMANDA DEL CLIENTE: {request_customer}")
                print(f"CAPACIDAD DEL DEPÓSITO: {capacity_depot}")

                new_demand = cluster.get_request_cluster() + request_customer
    
                print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")

                if capacity_depot >= new_demand:
                    cluster.set_request_cluster(new_demand)
                    items_of_cluster: List[int] = cluster.get_items_of_cluster()
                    items_of_cluster.append(id_customer)
                    cluster.set_items_of_cluster(items_of_cluster)

                    print(f"DEMANDA DEL CLUSTER ACTUALIZADA: {new_demand}")
                    print(f"ELEMENTOS DEL CLUSTER: {items_of_cluster}")

                    self.list_customers_to_assign.remove(selected_customer)

                    print(f"CANTIDAD DE CLIENTES SIN ASIGNAR: {len(self.list_customers_to_assign)}")
                    cost_matrix[row_best, col_best] = float('inf')

                if self.is_full_depot(self.list_customers_to_assign, cluster.get_request_cluster(), capacity_depot):
                    print("DEPOSITO LLENO")

                    cost_matrix[row_best, 0:total_customers] = float('inf')

        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")

        for cluster in list_clusters:
            print(f"ID CLUSTER: {cluster.id_cluster}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"CANTIDAD DE ELEMENTOS EN EL CLUSTER: {len(cluster.get_items_of_cluster())}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")

        return list_clusters