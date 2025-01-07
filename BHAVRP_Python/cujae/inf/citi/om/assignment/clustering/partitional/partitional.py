import random
import numpy as np
from typing import List
from ..seed_type import SeedType
from ..clustering import Clustering
from ....factory.interfaces.assignment_type import AssignmentType
from ....service.distance_type import DistanceType
from ....problem.input.problem import Problem
from ....problem.input.problem import Customer
from ....problem.input.problem import Depot
from ....problem.input.location import Location
from ....problem.solution.cluster import Cluster

class Partitional(Clustering):
    
    def __init__(self):
        super().__init__()
        self.seed_type = SeedType.NEAREST_DEPOT
        self.count_max_iterations = 100   # Configurable
        self.current_iteration = 0
    
    # Método que genera elementos iniciales (centroides o medoides) para los clústeres.
    def generate_elements(self) -> List[int]:
        id_elements: List[int] = []
        total_depots: int = Problem.get_problem().get_total_depots()
        total_customers = Problem.get_problem().get_total_customers()
        counter = total_depots

        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")

        if self.seed_type == SeedType.FARTHEST_DEPOT:
            depot = Depot()
            depot.set_id_depot(-1)
            depot.set_location_depot(self.calculate_mean_coordinate())
            list_depot = [depot]
            
            cost_matrix: np.ndarray = self.initialize_cost_matrix(
                Problem.get_problem().get_customers(), list_depot, self.distance_type)
            
            while counter > 0:
                max_value_index = np.argmax(cost_matrix)
                row = max_value_index // cost_matrix.shape[1]
                col = max_value_index % cost_matrix.shape[1]
                
                print(f"FILA SELECCIONADA: {row}")
                print(f"COLUMNA SELECCIONADA: {col}")
                print(f"VALOR SELECCIONADO: {cost_matrix[row, col]}")
                
                id_element = Problem.get_problem().get_customers()[col].get_id_customer()
                id_elements.append(id_element)
                
                print(f"ELEMENTO: {id_element}")
                print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")
                    
                cost_matrix[row, col] = float('-inf')
                counter -= 1   
        
        elif self.seed_type == SeedType.NEAREST_DEPOT:

            cost_matrix: np.ndarray = Problem.get_problem().get_cost_matrix()
            if cost_matrix is None or not isinstance(cost_matrix, (np.ndarray, list)) or len(cost_matrix) == 0:
                cost_matrix = self.initialize_cost_matrix(
                    Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), self.distance_type
                )
            
            while counter > 0:
                min_value_index = np.argmin(cost_matrix)
                row = min_value_index // cost_matrix.shape[1]
                col = min_value_index % cost_matrix.shape[1]
            
                print(f"FILA SELECCIONADA: {row}")
                print(f"COLUMNA SELECCIONADA: {col}")
                print(f"VALOR SELECCIONADO: {cost_matrix[row, col]}")
                
                id_element = Problem.get_problem().get_customers()[col].get_id_customer()
                id_elements.append(id_element)
                
                print(f"ELEMENTO: {id_element}")
                print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")
                
                cost_matrix[row, col] = float('-inf')
                counter -= 1
        
        elif self.seed_type == SeedType.RANDOM_DEPOT:
            random.seed()
            
            while counter > 0:
                id_element = random.randint(0, total_customers - 1)
                id_elements.append(Problem.get_problem().get_customers()[id_element].get_id_customer())
                
                print(f"ELEMENTO: {id_element}")
                print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")
                
                counter -= 1
            
        print(f"--------------------------------------------------")
        print(f"CENTROIDES/MEDOIDES INICIALES")
        print(f"{id_elements}")
        print(f"--------------------------------------------------")
            
        return id_elements 
    
    # Método que ordena los elementos por proximidad a los depósitos según el tipo de distancia, 
    # utilizando una matriz de costos para determinar el orden.
    def sorted_elements(self, id_elements: List[int], distance_type: DistanceType) -> List[int]:
        total_depots = Problem.get_problem().get_total_depots()
        j = 0
        
        sorted_elements: List[int] = [-1] * len(id_elements)
        customers: List[Customer] = []
        
        for element_id in id_elements:
            customers.append(Problem.get_problem().get_customer_by_id_customer(element_id))

        cost_matrix: np.ndarray = self.initialize_cost_matrix(
            customers, Problem.get_problem().get_depots(), distance_type)
                
        while j < len(id_elements):
            min_index = np.argmin(cost_matrix)
            row = min_index // cost_matrix.shape[1]
            col = min_index % cost_matrix.shape[1]
            
            print(f"ROW SELECCIONADA: {row}")
            print(f"COL SELECCIONADA: {col}")
            print(f"VALOR SELECCIONADO: {cost_matrix[row, col]}")

            cost_matrix[0:len(id_elements), col] = float('inf')
            cost_matrix[row, 0:len(id_elements) + total_depots - 1] = float('inf')

            sorted_elements[col - len(id_elements)] = id_elements[row]

            print(f"LISTADO DE ELEMENTOS SELECCIONADOS ORDENADOS ACTUALIZADA: {sorted_elements}")
            j += 1
            
        print(f"LISTADO DE ELEMENTOS SELECCIONADOS ORDENADOS: {sorted_elements}")

        return sorted_elements
    
    # Método que calcula y retorna la coordenada promedio de todas las ubicaciones de los clientes.
    def calculate_mean_coordinate(self) -> Location:
        axis_x: float = 0.0
        axis_y: float = 0.0

        list_coordinates_customers: List[Location] = list(Problem.get_problem().get_list_coordinates_customers())

        for customer in list_coordinates_customers:
            axis_x += customer.get_axis_x()
            axis_y += customer.get_axis_y()

        axis_x /= len(list_coordinates_customers)
        axis_y /= len(list_coordinates_customers)

        mean_location = Location(axis_x, axis_y)

        return mean_location
    
    # Método de asignación de clientes a clusters según los depósitos, basado en la matriz de costos, 
    # la demanda de los clientes y la capacidad de los depósitos.
    def step_assignment(self, list_clusters: List[Cluster], list_centroids: List[Depot]) -> List[Cluster]:
        total_customers = len(self.list_customers_to_assign)
        
        print("--------------------------------------------------------------------")
        print(f"PROCESO DE ASIGNACIÓN")
        
        while self.list_customers_to_assign:
            cost_matrix: np.ndarray = self.initialize_cost_matrix(
                self.list_customers_to_assign, 
                list_centroids,
                self.distance_type
            )
            
            min_index = np.argmin(cost_matrix)
            row_best = min_index // cost_matrix.shape[1]
            col_best = min_index % cost_matrix.shape[1]
            
            selected_customer = self.list_customers_to_assign[col_best]
            id_customer = selected_customer.get_id_customer()
            request_customer = selected_customer.get_request_customer()
            
            print("-----------------------------------------------------------")
            print(f"ID CLIENTE SELECCIONADO: {id_customer}")
            print(f"POSICIÓN DEL CLIENTE SELECCIONADO: {col_best}")
            print(f"DEMANDA DEL CLIENTE SELECCIONADO: {request_customer}")
            
            selected_depot: Depot = Problem.get_problem().get_depots()[row_best]
            id_depot = selected_depot.get_id_depot()
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(id_depot)
            
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
                else:
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
         
    # Método que crea una lista de centroides a partir de los identificadores de elementos, 
    # asignando a cada uno su ubicación correspondiente según la posición de los clientes.
    def create_centroids(self) -> List[Depot]:
        centroids: List[Depot] = []

        for i in range(len(self.list_id_elements)):
            centroid = Depot()
            customer = Problem.get_problem().get_customer_by_id_customer(self.list_id_elements[i])

            if customer is not None:  # Verificar si el cliente existe
                centroid.set_id_depot(self.list_id_elements[i])

                location = Location()
                location.set_axis_x(customer.get_location_customer().get_axis_x())
                location.set_axis_y(customer.get_location_customer().get_axis_y())
                centroid.set_location_depot(location)

                centroids.append(centroid)
            else:
                print(f"Cliente con ID {self.list_id_elements[i]} no encontrado.")

        return centroids
        
    # Método para actualizar la lista de clientes por asignar, eliminando aquellos cuyos IDs 
    # coincidan con los elementos especificados en una lista de IDs.
    def update_customer_to_assign(self):
        
        for id_element in self.list_id_elements:
            found: bool = False
            i: int = 0
            while not found and i < len(self.list_customers_to_assign):
                if self.list_customers_to_assign[i].get_id_customer() == id_element:
                    found = True
                    del self.list_customers_to_assign[i]
                else:
                    i += 1
                    
        print("CLIENTES A ASIGNAR")
        for customer in self.list_customers_to_assign:
            print(f"--------------------------------------------------")
            print(f"ID CLIENTE: {customer.get_id_customer()}")
            print(f"X: {customer.get_location_customer().get_axis_x()}")
            print(f"Y: {customer.get_location_customer().get_axis_y()}")
            print(f"DEMANDA: {customer.get_request_customer()}")
            
    # Método que limpia los clusters eliminando sus elementos y muestra información detallada 
    # de cada cluster.
    def clean_clusters(self, clusters: List[Cluster]):
        for cluster in clusters:
            cluster.clean_cluster()
        
        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for i, cluster in enumerate(clusters):
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")
        