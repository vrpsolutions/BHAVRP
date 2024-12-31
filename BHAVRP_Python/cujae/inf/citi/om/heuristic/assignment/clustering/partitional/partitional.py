import random
import numpy as np
from typing import List
from ..seed_type import SeedType
from ..sampling_type import SamplingType
from ..clustering import Clustering
from .....service.distance_type import DistanceType
from .....problem.input.problem import Problem
from .....problem.input.problem import Customer
from .....problem.input.problem import Depot
from .....problem.input.location import Location
from .....problem.output.solution.cluster import Cluster

class Partitional(Clustering):
    
    def __init__(self):
        self.seed_type = SeedType.NEAREST_DEPOT
        self.count_max_iterations = 100   # Configurable
        self.current_iteration = 0
        
    # Método que genera elementos iniciales (centroides o medoides) para los clústeres.
    def generate_elements(self, seed_type: SeedType, distance_type: DistanceType) -> List[int]:
        id_elements: List[int] = []
        
        total_customers: int = Problem.get_problem().get_total_customers()
        total_depots: int = Problem.get_problem().get_total_depots()
        counter: int = total_depots
        
        cost_matrix: np.ndarray = self.initialize_cost_matrix(
            Problem.get_problem().get_customers(),
            Problem.get_problem().get_depots(),
            distance_type
        )

        id_element = -1
        
        if seed_type == SeedType.FARTHEST_DEPOT:
            id_elements = [-1] * counter
            
            print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")
            
            while counter > 0:
                row, col = np.unravel_index(
                    np.argmax(cost_matrix[total_customers:, :]), cost_matrix[total_customers:, :].shape
                )
                row += total_customers
                
                print(f"FILA SELECCIONADA: {row}")
                print(f"COLUMNA SELECCIONADA: {col}")
                print(f"VALOR SELECCIONADO: {cost_matrix[row, col]}")
                
                id_element = Problem.get_problem().get_customers()[col].get_id_customer()
                id_elements[row - total_customers] = id_element
                
                print(f"ELEMENTO: {id_element}")
                print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")
                
                cost_matrix[total_customers:, col] = -np.inf
                cost_matrix[row, :] = -np.inf
                counter -= 1
        
        elif seed_type == SeedType.NEAREST_DEPOT:
            id_elements = [-1] * counter
            
            print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")
            
            while counter > 0:
                row, col = np.unravel_index(
                    np.argmin(cost_matrix[total_customers:, :]), cost_matrix[total_customers:, :].shape
                )
                row += total_customers
                
                print(f"FILA SELECCIONADA: {row}")
                print(f"COLUMNA SELECCIONADA: {col}")
                print(f"VALOR SELECCIONADO: {cost_matrix[row, col]}")
                
                id_element = Problem.get_problem().get_customers()[col].get_id_customer()
                id_elements[row - total_customers] = id_element
                
                print(f"ELEMENTO: {id_element}")
                print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")
                
                cost_matrix[total_customers:, col] = np.inf
                cost_matrix[row, :] = np.inf
                counter -= 1
        
        elif seed_type == SeedType.RANDOM_DEPOT:
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
        
    # Método que crea una lista de centroides a partir de los identificadores de elementos, 
    # asignando a cada uno su ubicación correspondiente según la posición de los clientes.
    def create_centroids(self, id_elements: List[int]) -> List[Depot]:
        centroids: List[Depot] = []

        for i in range(len(id_elements)):
            centroid = Depot()

            centroid.set_id_depot(id_elements[i])

            location = Location()
            customer = Problem.get_problem().get_customer_by_id_customer(id_elements[i])
            location.set_axis_x(customer.get_location_customer().get_axis_x())
            location.set_axis_y(customer.get_location_customer().get_axis_y())
            centroid.set_location_depot(location)

            centroids.append(centroid)

        return centroids
    
    # Método de asignación de clientes a clusters según los depósitos, basado en la matriz de costos, 
    # la demanda de los clientes y la capacidad de los depósitos.
    def step_assignment(
        self, 
        clusters: List[Cluster], 
        customer_to_assign: List[Customer], 
        cost_matrix: np.ndarray
    ) -> List[Cluster]:
        id_depot = -1
        pos_depot = -1
        capacity_depot = 0.0

        id_customer = -1
        pos_customer = -1
        request_customer = 0.0

        pos_cluster = -1
        request_cluster = 0.0

        list_customers = list(customer_to_assign)
        total_customers = len(customer_to_assign)
        total_depots = len(clusters)
        
        print("--------------------------------------------------------------------")
        print(f"PROCESO DE ASIGNACIÓN")
        
        while customer_to_assign and not np.all(
            cost_matrix[0:total_customers, 0:total_customers + total_depots - 1] == float('inf')
        ):
            
            row_best, col_best = np.unravel_index(
                np.argmin(
                    cost_matrix[0:total_customers, 0:total_customers + total_depots - 1]), 
                    (total_customers, total_customers + total_depots - 1
                )
            )

            pos_customer = col_best
            id_customer = list_customers[pos_customer].get_id_customer()
            request_customer = list_customers[pos_customer].get_request_customer()

            print("-----------------------------------------------------------")
            print(f"BestAllCol: {col_best}")
            print(f"BestAllRow: {row_best}")
            print(f"ID CLIENTE SELECCIONADO: {id_customer}")
            print(f"POSICIÓN DEL CLIENTE SELECCIONADO: {pos_customer}")
            print(f"DEMANDA DEL CLIENTE SELECCIONADO: {request_customer}")

            pos_depot = row_best - total_customers
            id_depot = Problem.get_problem().get_depots()[pos_depot].get_id_depot()
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(id_depot)

            print(f"ID DEPOSITO SELECCIONADO: {id_depot}")
            print(f"POSICIÓN DEL DEPOSITO SELECCIONADO: {pos_depot}")
            print(f"CAPACIDAD TOTAL DEL DEPOSITO SELECCIONADO: {capacity_depot}")

            pos_cluster = self.find_cluster(id_depot, clusters)

            print(f"POSICION DEL CLUSTER: {pos_cluster}")

            if pos_cluster != -1:
                request_cluster = clusters[pos_cluster].get_request_cluster()

                print(f"DEMANDA DEL CLUSTER: {request_cluster}")

                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer

                    clusters[pos_cluster].set_request_cluster(request_cluster)
                    clusters[pos_cluster].get_items_of_cluster().append(id_customer)

                    print(f"DEMANDA DEL CLUSTER ACTUALIZADA: {request_cluster}")
                    print(f"ELEMENTOS DEL CLUSTER: {clusters[pos_cluster].get_items_of_cluster()}")

                    cost_matrix[row_best, pos_customer] = float('inf')
                    customer_to_assign.remove(
                        Problem.get_problem().find_pos_customer(customer_to_assign, id_customer)
                    )

                    print(f"CANTIDAD DE CLIENTES SIN ASIGNAR: {len(customer_to_assign)}")
                else:
                    cost_matrix[row_best, pos_customer] = float('inf')

                if self.is_full_depot(customer_to_assign, request_cluster, capacity_depot):
                    print("DEPOSITO LLENO")

                    cost_matrix[row_best, 0:total_customers + total_depots - 1] = float('inf')

        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for cluster in clusters:
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"CANTIDAD DE ELEMENTOS EN EL CLUSTER: {len(cluster.get_items_of_cluster())}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")

        return clusters
    
    # Método para actualizar la lista de clientes por asignar, eliminando aquellos cuyos IDs 
    # coincidan con los elementos especificados en una lista de IDs.
    def update_customer_to_assign(self, customer_to_assign: List[Customer], id_elements: List[int]):
        for id_element in id_elements:
            customer_to_assign = [customer for customer in customer_to_assign if customer.get_id_customer() != id_element]

        print("CLIENTES A ASIGNAR")
        for customer in customer_to_assign:
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
        