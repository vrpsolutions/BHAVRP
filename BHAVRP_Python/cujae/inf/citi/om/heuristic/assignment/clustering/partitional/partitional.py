import random
import numpy as np
from abc import ABC
from typing import List
from ..seed_type import SeedType
from ..sampling_type import SamplingType
from ...assignment import Assignment
from .....controller.utils.distance_type import DistanceType
from .....problem.input.problem import Problem
from .....problem.input.problem import Customer
from .....problem.input.problem import Depot
from .....problem.input.location import Location
from .....problem.output.solution.cluster import Cluster

class Partitional(Assignment, ABC):
    
    def __init__(self):
        super().__init__()
        
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
        
    # Método que actualiza los clusters asignándoles nuevos elementos y ajustando su demanda 
    # en base a los IDs proporcionados.
    def update_clusters(self, clusters: List[Cluster], id_elements: List[int]):
        for i in range(len(clusters)):
            clusters[i].get_items_of_cluster().append(id_elements[i])
            clusters[i].set_request_cluster(Problem.get_problem().get_request_by_id_customer(id_elements[i]))
            
        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for cluster in clusters:
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")
    
    # Método que genera elementos iniciales (centroides o medoides) para los clústeres.
    def generate_elements(self, seed_type: SeedType, distance_type: DistanceType) -> List[int]:
        id_elements: List[int] = []
        
        total_customers: int = Problem.get_problem().get_total_customers()
        total_depots: int = Problem.get_problem().get_total_depots()
        counter: int = total_depots
        
        cost_matrix: np.ndarray = np.array(Problem.get_problem().get_cost_matrix())

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
    
    # Método que genera elementos iniciales (centroides o medoides) para los clústeres.
    def generate_elements(self, customers: List[Customer], distance_type: DistanceType) -> List[int]:
        id_elements: List[int] = [-1] * Problem.get_problem().get_total_depots()
        
        total_customers: int = len(customers)
        total_depots: int = Problem.get_problem().get_total_depots()
        counter = total_depots
        
        cost_matrix: np.ndarray = np.array(Problem.get_problem().get_cost_matrix())
        id_element = -1
        
        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")
        
        while counter > 0:
            row, col = np.unravel_index(
                np.argmin(cost_matrix[total_customers:, :]), cost_matrix[total_customers:, :].shape
            )
            
            print(f"FILA SELECCIONADA: {row}")
            print(f"COLUMNA SELECCIONADA: {col}")
            print(f"VALOR SELECCIONADO: {cost_matrix[row, col]}")
            
            id_element = customers[col].get_id_customer()
            id_elements[row - total_customers] = id_element
        
            print(f"ELEMENTO: {id_element}")
            print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")
            
            cost_matrix[total_customers:, col] = np.inf
            cost_matrix[row, :] = np.inf
            counter -= 1
        
        print("--------------------------------------------------")
        print("CENTROIDES/MEDOIDES INICIALES")
        print(id_elements)
        print("--------------------------------------------------")
        
        return id_elements
    
    # Método que genera elementos iniciales (centroides o medoides) para los clústeres.
    def generate_elements(self, distance_type: DistanceType) -> List[int]:
        id_elements: List[int] = []
        
        total_customers: int = Problem.get_problem().get_total_customers()
        total_depots: int = Problem.get_problem().get_total_depots()
        counter = total_depots
        
        cost_matrix: np.ndarray = None
        id_element = -1
        
        depot = Depot()
        depot.set_id_depot(-1)
        depot.set_location_depot(self.calculate_mean_coordinate())
        
        list_depot = [depot]
        
        if distance_type.value in [1, 2, 3, 4]:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix(
                    Problem.get_problem().get_customers(), list_depot, distance_type
                )
            except (AttributeError, TypeError, ValueError) as e:
                print(f"Error filling cost matrix: {e}")
        elif distance_type.ordinal() == 5:
            pass
        
        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")
        
        while counter > 0:
            row, col = np.unravel_index(
                np.argmax(cost_matrix[:total_customers, :total_customers]), cost_matrix[:total_customers, :total_customers].shape
            )
            
            print(f"FILA SELECCIONADA: {row}")
            print(f"COLUMNA SELECCIONADA: {col}")
            print(f"VALOR SELECCIONADO: {cost_matrix[row, col]}")
            
            id_element = Problem.get_problem().get_customers()[col].get_id_customer()
            id_elements.append(id_element)
            
            print(f"ELEMENTO: {id_element}")
            print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")
            
            cost_matrix[row, col] = float('-inf')
            counter -= 1       
            
        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}") 
        
        return self.sorted_elements(id_elements, distance_type)
    
    # Método que genera elementos iniciales (centroides o medoides) para los clústeres.
    def generate_elements_xxx(self, distance_type: DistanceType) -> List[int]:
        id_elements: List[int] = []
        
        total_customers: int = Problem.get_problem().get_total_customers()
        total_depots: int = Problem.get_problem().get_total_depots()
        counter = total_depots
        
        cost_matrix: np.ndarray = None
        
        if distance_type.value in [1, 2, 3, 4]:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix_xxx(
                    Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), distance_type
                )
            except (AttributeError, TypeError, ValueError) as e:
                print(f"Error filling cost matrix: {e}")
        elif distance_type.value == 5:
            pass
        
        pos_element = random.randint(0, total_customers - 1)
        id_elements.append(Problem.get_problem().get_customers()[pos_element].get_id_customer())

        print(f"POS_ELEMENT: {pos_element}")
        print(f"UPDATED LIST OF ELEMENTS: {id_elements}")
        
        while counter > 1:
            row, col = cost_matrix.index_bigger_value(
                pos_element, 0, pos_element, total_customers - 1
            )

            print(f"SELECTED ROW: {row}")
            print(f"SELECTED COL: {col}")
            print(f"SELECTED VALUE: {cost_matrix.get_item(row, col)}")

            id_elements.append(Problem.get_problem().get_customers()[col].get_id_customer())
            pos_element = col

            print(f"POS_ELEMENT: {pos_element}")
            print(f"UPDATED LIST OF ELEMENTS: {id_elements}")

            cost_matrix[0:total_customers, pos_element] = float('-inf')
            counter -= 1

        print(f"FINAL LIST OF SELECTED ELEMENTS: {id_elements}")
        
        return self.sorted_elements(id_elements, distance_type)
    
    # Método que genera elementos iniciales (centroides o medoides) para los clústeres.
    def generate_elements_xx(self, distance_type: DistanceType) -> List[int]:
        list_centroids: List[int] = []
        list_customers: List[Customer] = list(Problem.get_problem().get_customers())
        
        total_customers = len(list_customers)
        counter = Problem.get_problem().get_total_depots()
        id_element = -1
        
        pos_centroid = random.randint(0, total_customers - 1)
        id_element = list_customers[pos_centroid].get_id_customer()
        list_centroids.append(id_element)
        list_customers.pop(pos_centroid)
        counter -= 1
        
        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {list_centroids}")
        
        cost_matrix: np.ndarray = None
        
        if distance_type.value in [1, 2, 3, 4]:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix_xxx(
                    Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), distance_type
                )
            except (ValueError, AttributeError, TypeError) as e:
                print(f"Error filling cost matrix: {e}")
        elif distance_type == 5:
            cost_matrix = np.array(Problem.get_problem().get_cost_matrix())
            
        pos_centroid_row, pos_centroid_col = np.unravel_index(
            np.argmax(cost_matrix[pos_centroid, :total_customers]), cost_matrix.shape
        )
        id_element = Problem.get_problem().get_customers()[pos_centroid_col].get_id_customer()
        list_centroids.append(id_element)
        list_customers.remove(
            Problem.get_problem().get_pos_element(id_element, list_customers)
        )
        counter -= 1
        
        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {list_centroids}")
        
        while counter > 0:
            max_distances = []
            for customer in list_customers:
                pos_customer = Problem.get_problem().get_pos_element(customer.get_id_customer())
                max_distance = max(
                    cost_matrix[Problem.get_problem().get_pos_element(centroid), pos_customer]
                    for centroid in list_centroids
                )
                max_distances.append(max_distance)

            min_distance = min(max_distances)
            pos_centroid = max_distances.index(min_distance)
            id_element = list_customers[pos_centroid].get_id_customer()
            list_centroids.append(id_element)
            list_customers.pop(pos_centroid)
            counter -= 1

            print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {list_centroids}")

        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {list_centroids}")
        
        return self.sorted_elements(list_centroids, distance_type)
    
    # Método que verifica y actualiza los centroides de los clústeres.
    def verify_centroids(
        self, 
        clusters: List[Cluster], 
        centroids: List[Depot], 
        distance_type: DistanceType
    ) -> bool:
        change: bool = False
        dummy_depot: Location = None
        
        print(f"change: {change}")
        
        for i, cluster in enumerate(clusters):
            if cluster.get_items_of_cluster():  # Si el clúster no está vacío
                dummy_depot = self.recalculate_centroid(cluster)
            else:
                dummy_depot = centroids[i].get_location_depot()
            
            print("------------------------------------------------------------------")
            print(f"DUMMY_DEPOT {i} X: {dummy_depot.get_axis_x()}")
            print(f"DUMMY_DEPOT {i} Y: {dummy_depot.get_axis_y()}")

            print(f"CENTROIDE {i} X: {centroids[i].get_location_depot().get_axis_x()}")
            print(f"CENTROIDE {i} Y: {centroids[i].get_location_depot().get_axis_y()}")
            
            if (centroids[i].get_location_depot().get_axis_x() != dummy_depot.get_axis_x() or centroids[i].get_location_depot().get_axis_y() != dummy_depot.get_axis_y()):
                change = True
                centroids[i].set_id_depot(-1)
                
                location = Location()
                location.set_axis_x(dummy_depot.get_axis_x())
                location.set_axis_y(dummy_depot.get_axis_y())
                centroids[i].set_location_depot(location)

                print(f"change: {change}")
                print(f"CENTROIDE {i} X: {centroids[i].get_location_depot().get_axis_x()}")
                print(f"CENTROIDE {i} Y: {centroids[i].get_location_depot().get_axis_y()}")
            else:
                print(f"CENTROIDE {i} X: {centroids[i].get_location_depot().get_axis_x()}")
                print(f"CENTROIDE {i} Y: {centroids[i].get_location_depot().get_axis_y()}")

        if change:
            self.update_centroids(clusters, centroids, distance_type)
            
        print(f"CAMBIO LOS CENTROIDES: {change}")
        
        return change
    
    # Método que actualiza los centroides de los clústeres.
    def update_centroids(
        self, 
        clusters: List[Cluster], 
        centroids: List[Depot], 
        distance_type: DistanceType
    ):
        cost_matrix: np.ndarray = None
        
        try:
            cost_matrix = Problem.get_problem().calculate_cost_matrix(
                centroids, Problem.get_problem().get_depots(), distance_type
            )
        except (ValueError, AttributeError, TypeError) as e:
            print("Error calculating cost matrix:", e)
        
        temp_centroids = centroids.copy()
        total_centroids = len(centroids)
        pos_centroid = -1
        pos_depot = -1
        
        print("-------------------------------------")
        for i, centroid in enumerate(centroids):
            print(f"CENTROIDE ID: {centroid.get_id_depot()}")
            print(f"CENTROIDE X: {centroid.get_location_depot().get_axis_x()}")
            print(f"CENTROIDE Y: {centroid.get_location_depot().get_axis_y()}")

        for i in range(cost_matrix.shape[0]):
            for j in range(cost_matrix.shape[1]):
                print(f"Row: {i} Col: {j} VALUE: {cost_matrix[i, j]}")
            print("---------------------------------------------")
            
        while not np.all(cost_matrix[:total_centroids, :total_centroids] == float('inf')):
            row, col = np.unravel_index(
                np.argmin(cost_matrix[:total_centroids, :total_centroids]), 
                cost_matrix[:total_centroids, :total_centroids].shape
            )

            print(f"BestAllRow: {row}")
            print(f"BestAllCol: {col}")
            print(f"COSTO: {cost_matrix[row, col]}")

            pos_centroid = row
            pos_depot = col

            print(f"POSICIÓN DEL CENTROIDE: {pos_centroid}")
            print(f"POSICIÓN DEL DEPÓSITO: {pos_depot}")

            if pos_centroid != pos_depot:
                depot = Depot()

                depot.set_id_depot(temp_centroids[pos_centroid].get_id_depot())
                print(f"ID CENTROIDE: {temp_centroids[pos_centroid].get_id_depot()}")

                axis_x = temp_centroids[pos_centroid].get_location_depot().get_axis_x()
                axis_y = temp_centroids[pos_centroid].get_location_depot().get_axis_y()

                location = Location()
                location.set_axis_x(axis_x)
                location.set_axis_y(axis_y)
                depot.set_location_depot(location)

                fleet = temp_centroids[pos_centroid].get_fleet_depot()
                depot.set_fleet_depot(fleet.copy())

                centroids[pos_depot] = depot

            cost_matrix[:, pos_depot] = float('inf')
            cost_matrix[pos_centroid, :] = float('inf')

            for i in range(cost_matrix.shape[0]):
                for j in range(cost_matrix.shape[1]):
                    print(f"Row: {i} Col: {j} VALUE: {cost_matrix[i, j]}")
                print("---------------------------------------------")
                
    # Método que calcula el costo total de los clústeres.
    def calculate_cost(self, clusters: List[Cluster], cost_matrix: np.ndarray) -> float:
        cost: float = 0.0
        
        for cluster in clusters:
            list_id_customers = list(cluster.get_items_of_cluster())
            pos_depot = Problem.get_problem().get_pos_element(cluster.get_id_cluster())
            
            print(f"CLIENTES: {list_id_customers}")
            print(f"POSICIÓN DEPÓSITO: {pos_depot}")
            
            for customer_id in list_id_customers:
                pos_customer = Problem.get_problem().get_pos_element(customer_id)
                cost += cost_matrix[pos_depot, pos_customer]

                print(f"ID CLIENTE: {customer_id}")
                print(f"POSICIÓN CLIENTE: {pos_customer}")
                print(f"COSTO: {cost_matrix[pos_depot, pos_customer]}")
                print(f"COSTO ACUMULADO: {cost}")
                
        return cost
    
    # Método que calcula el costo total de los clústeres considerando los depósitos como medoides.
    def calculate_cost(self, clusters: List[Cluster], cost_matrix: np.ndarray, medoids: List[Depot]) -> float:
        cost: float = 0.0
        
        print("-------------------------------------------------------------------------------")
        print("CÁLCULO DEL MEJOR COSTO")
        
        for i, cluster in enumerate(clusters):
            pos_depot = Problem.get_problem().get_pos_element(medoids[i].get_id_depot())
            list_id_customers = list(cluster.get_items_of_cluster())
            
            print("-------------------------------------------------------------------------------")
            print(f"ID MEDOIDE: {medoids[i].get_id_depot()}")
            print(f"POSICIÓN DEL MEDOIDE: {pos_depot}")
            print(f"CLIENTES ASIGNADOS AL MEDOIDE: {list_id_customers}")
            print("-------------------------------------------------------------------------------")

            for customer_id in list_id_customers:
                pos_customer = Problem.get_problem().get_pos_element(customer_id)

                if pos_depot != pos_customer:
                    cost += cost_matrix[pos_depot, pos_customer]
                
                print(f"ID CLIENTE: {customer_id}")
                print(f"POSICIÓN DEL CLIENTE: {pos_customer}")
                print(f"COSTO: {cost_matrix[pos_depot, pos_customer] if pos_depot != pos_customer else 0.0}")
                print("-------------------------------------------------------------------------------")
                print(f"COSTO ACUMULADO: {cost}")
                print("-------------------------------------------------------------------------------")
        
        print(f"MEJOR COSTO TOTAL: {cost}")
        print("-------------------------------------------------------------------------------")
        
        return cost
    
    # Método que calcula el costo total de los clústeres considerando los depósitos como medoides.
    def calculate_cost(
        self, 
        clusters: List[Cluster], 
        cost_matrix: np.ndarray, 
        medoids: List[Depot], 
        list_partition: List[Customer]
    ) -> float:
        cost = 0.0

        print("-------------------------------------------------------------------------------")
        print("CÁLCULO DEL MEJOR COSTO")
        
        for i, cluster in enumerate(clusters):
            pos_depot = Problem.get_problem().get_pos_element(medoids[i].get_id_depot(), list_partition)
            list_id_customers = cluster.get_items_of_cluster()

            print("-------------------------------------------------------------------------------")
            print(f"ID MEDOIDE: {medoids[i].get_id_depot()}")
            print(f"POSICIÓN DEL MEDOIDE: {pos_depot}")
            print(f"CLIENTES ASIGNADOS AL MEDOIDE: {list_id_customers}")
            print("-------------------------------------------------------------------------------")

            for customer_id in list_id_customers:
                pos_customer = Problem.get_problem().get_pos_element(customer_id, list_partition)

                if pos_depot != pos_customer:
                    cost += cost_matrix[pos_depot, pos_customer]
                
                print(f"ID CLIENTE: {customer_id}")
                print(f"POSICIÓN DEL CLIENTE: {pos_customer}")
                print(f"COSTO: {cost_matrix[pos_depot, pos_customer] if pos_depot != pos_customer else 0.0}")
                print("-------------------------------------------------------------------------------")
                print(f"COSTO ACUMULADO: {cost}")
                print("-------------------------------------------------------------------------------")
        
        print(f"MEJOR COSTO TOTAL: {cost}")
        print("-------------------------------------------------------------------------------")
        
        return cost

    # Método que replica los depósitos actuales para crear una nueva lista de depósitos.
    def replicate_depots(self, depots: List[Depot]) -> List[Depot]:
        new_depots: List[Depot] = []
        
        print("--------------------------------------------------")
        print("MEDOIDES/CENTROIDES ACTUALES")

        for i, depot in enumerate(depots):
            new_depot = Depot()

            new_depot.set_id_depot(depot.get_id_depot())

            axis_x = depot.get_location_depot().get_axis_x()
            axis_y = depot.get_location_depot().get_axis_y()

            location = Location()
            location.set_axis_x(axis_x)
            location.set_axis_y(axis_y)
            new_depot.set_location_depot(location)

            fleet = depot.get_fleet_depot()
            new_depot.set_fleet_depot(fleet)

            new_depots.append(new_depot)

            print("--------------------------------------------------")
            print(f"ID MEDOIDE/CENTROIDE: {new_depot.get_id_depot()}")
            print(f"X: {new_depot.get_location_depot().get_axis_x()}")
            print(f"Y: {new_depot.get_location_depot().get_axis_y()}")
            print(f"CAPACIDAD DE VEHÍCULO: {new_depot.get_fleet_depot()[0].get_capacity_vehicle()}")
            print(f"CANTIDAD DE VEHÍCULOS: {new_depot.get_fleet_depot()[0].get_count_vehicles()}")

        return new_depots

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

    # Método que verifica si ha habido cambios en las ubicaciones de los medoides 
    # comparando sus coordenadas actuales con las anteriores.
    def verify_medoids(self, old_medoids: List[Depot], current_medoids: List[Depot]) -> bool:
        change: bool = False
        i = 0
        
        while not change and i < len(current_medoids):
            if (old_medoids[i].get_location_depot().get_axis_x() != current_medoids[i].get_location_depot().get_axis_x()) or \
            (old_medoids[i].get_location_depot().get_axis_y() != current_medoids[i].get_location_depot().get_axis_y()):
                change = True
            else:
                i += 1

        print(f"change: {change}")

        return change

    # Método que realiza la búsqueda de mejores medoides en cada clúster evaluando diferentes candidatos.
    def step_search_medoids(
        self, 
        clusters: List[Cluster], 
        medoids: List[Depot], 
        cost_matrix: np.ndarray, 
        best_cost: float
    ):
        current_cost: float = 0.0
        
        old_medoids: List[Depot] = self.replicate_depots(medoids)
        
        print("--------------------------------------------------------------------")
        print(f"PROCESO DE BÚSQUEDA")
        
        for i in range(len(clusters)):
            best_loc_medoid = Location(medoids[i].get_location_depot().get_axis_x(),
                                    medoids[i].get_location_depot().get_axis_y())
            
            print("--------------------------------------------------")
            print(f"MEJOR MEDOIDE ID: {medoids[i].get_id_depot()}")
            print(f"MEJOR MEDOIDE LOCATION X: {best_loc_medoid.get_axis_x()}")
            print(f"MEJOR MEDOIDE LOCATION Y: {best_loc_medoid.get_axis_y()}")
            print("--------------------------------------------------")
            
            for j in range(1, len(clusters[i].get_items_of_cluster())):
                new_id_medoid = clusters[i].get_items_of_cluster()[j]
                new_medoid = Customer()
                
                customer = Problem.get_problem().get_customer_by_id_customer(new_id_medoid)
                new_medoid.set_id_customer(customer.get_id_customer())
                new_medoid.set_request_customer(customer.get_request_customer())
                
                location = Location()
                location.set_axis_x(customer.get_location_customer().get_axis_x())
                location.set_axis_y(customer.get_location_customer().get_axis_y())
                new_medoid.set_location_customer(location)
                
                medoids[i].set_id_depot(new_id_medoid)
                medoids[i].set_location_depot(new_medoid.get_location_customer())
                
                print(f"ID DEL NUEVO MEDOIDE: {new_id_medoid}")
                print(f"X DEL NUEVO MEDOIDE: {new_medoid.get_location_customer().get_axis_x()}")
                print(f"Y DEL NUEVO MEDOIDE: {new_medoid.get_location_customer().get_axis_y()}")
                print("--------------------------------------------------")
                print("LISTA DE MEDOIDES")
                print(f"ID: {medoids[i].get_id_depot()}")
                print(f"X: {medoids[i].get_location_depot().get_axis_x()}")
                print(f"Y: {medoids[i].get_location_depot().get_axis_y()}")
                
                print("LISTA DE ANTERIORES MEDOIDES")
                print(f"ID: {old_medoids[i].get_id_depot()}")
                print(f"X: {old_medoids[i].get_location_depot().get_axis_x()}")
                print(f"Y: {old_medoids[i].get_location_depot().get_axis_y()}")
                print("--------------------------------------------------")
                
                current_cost = self.calculate_cost(clusters, cost_matrix, medoids)
                
                print("---------------------------------------------")
                print(f"ACTUAL COSTO TOTAL: {current_cost}")
                print("---------------------------------------------")
                
                if current_cost < best_cost:
                    best_cost = current_cost
                    best_loc_medoid = medoids[i].get_location_depot()
                    
                    print(f"NUEVO MEJOR COSTO TOTAL: {best_cost}")
                    print(f"NUEVO MEDOIDE ID: {medoids[i].get_id_depot()}")
                    print(f"NUEVO MEDOIDE LOCATION X: {best_loc_medoid.get_axis_x()}")
                    print(f"NUEVO MEDOIDE LOCATION Y: {best_loc_medoid.get_axis_y()}")
                    print("---------------------------------------------")
                    
                    old_medoids[i].set_id_depot(medoids[i].get_id_depot())
                    old_medoids[i].get_location_depot().set_axis_x(medoids[i].get_location_depot().get_axis_x())
                    old_medoids[i].get_location_depot().set_axis_y(medoids[i].get_location_depot().get_axis_y())
                else:
                    medoids[i].set_id_depot(old_medoids[i].get_id_depot())
                    medoids[i].get_location_depot().set_axis_x(old_medoids[i].get_location_depot().get_axis_x())
                    medoids[i].get_location_depot().set_axis_y(old_medoids[i].get_location_depot().get_axis_y())
                
                print(f"ID MEDOIDE: {medoids[i].get_id_depot()}")
                print(f"LISTA DE MEDOIDES X: {medoids[i].get_location_depot().get_axis_x()}")
                print(f"LISTA DE MEDOIDES Y: {medoids[i].get_location_depot().get_axis_y()}")
                print("---------------------------------------------")
            
            medoids[i].set_location_depot(best_loc_medoid)
        
    # Método que realiza la búsqueda de mejores medoides en cada clúster evaluando diferentes candidatos.    
    def step_search_medoids(
        self, 
        clusters: List[Cluster], 
        medoids: List[Depot], 
        cost_matrix: np.ndarray, 
        best_cost: float, 
        list_partition: List[Customer]
    ):
        current_cost: float = 0.0
        
        old_medoids: List[Depot] = self.replicate_depots(medoids)
        
        print("--------------------------------------------------------------------")
        print(f"PROCESO DE BÚSQUEDA")
        
        for i in range(len(clusters)):
            best_loc_medoid = Location(medoids[i].location_depot.get_axis_x(), medoids[i].location_depot.get_axis_y())

            print("--------------------------------------------------")
            print(f"MEJOR MEDOIDE ID: {medoids[i].id_depot}")
            print(f"MEJOR MEDOIDE LOCATION X: {best_loc_medoid.get_axis_x()}")
            print(f"MEJOR MEDOIDE LOCATION Y: {best_loc_medoid.get_axis_y()}")
            print("--------------------------------------------------")
            
            for j in range(1, len(clusters[i].items_of_cluster)):
                new_id_medoid = clusters[i].items_of_cluster[j]
                new_medoid = Customer()
                
                new_medoid.set_id_customer(Problem.get_problem().get_customer_by_id_customer(new_id_medoid).get_id_customer())
                new_medoid.set_request_customer(Problem.get_problem().get_customer_by_id_customer(new_id_medoid).get_request_customer())
                
                location = Location()
                location.set_axis_x(Problem.get_problem().get_customer_by_id_customer(new_id_medoid).get_location_customer().get_axis_x())
                location.set_axis_y(Problem.get_problem().get_customer_by_id_customer(new_id_medoid).get_location_customer().get_axis_y())
                new_medoid.set_location_customer(location)
                
                medoids[i].set_id_depot(new_id_medoid)
                medoids[i].set_location_depot(new_medoid.get_location_customer())
                
                print(f"ID DEL NUEVO MEDOIDE: {new_id_medoid}")
                print(f"X DEL NUEVO MEDOIDE: {new_medoid.get_location_customer().get_axis_x()}")
                print(f"Y DEL NUEVO MEDOIDE: {new_medoid.get_location_customer().get_axis_y()}")
                
                print("--------------------------------------------------")
                print("LISTA DE MEDOIDES")
                print(f"ID: {medoids[i].id_depot}")
                print(f"X: {medoids[i].location_depot.x}")
                print(f"Y: {medoids[i].location_depot.y}")                    

                print("LISTA DE ANTERIORES MEDOIDES")
                print(f"ID: {old_medoids[i].get_id_depot()}")
                print(f"X: {old_medoids[i].get_location_depot().get_axis_x()}")
                print(f"Y: {old_medoids[i].get_location_depot().get_axis_y()}")
                print("--------------------------------------------------")
                
                current_cost = self.calculate_cost(clusters, cost_matrix, medoids, list_partition)

                print("---------------------------------------------")
                print(f"ACTUAL COSTO TOTAL: {current_cost}")
                print("---------------------------------------------")
                
                if current_cost < best_cost:
                    best_cost = current_cost
                    best_loc_medoid = medoids[i].get_location_depot()
                    
                    print(f"NUEVO MEJOR COSTO TOTAL: {best_cost}")
                    print(f"NUEVO MEDOIDE ID: {medoids[i].get_id_depot()}")
                    print(f"NUEVO MEDOIDE LOCATION X: {best_loc_medoid.get_axis_x()}")
                    print(f"NUEVO MEDOIDE LOCATION Y: {best_loc_medoid.get_axis_y()}")
                    print("---------------------------------------------")
                    
                    old_medoids[i].set_id_depot(medoids[i].get_id_depot())
                    old_medoids[i].get_location_depot().set_axis_x(medoids[i].get_location_depot().get_axis_x())
                    old_medoids[i].get_location_depot().set_axis_y(medoids[i].get_location_depot().get_axis_y())
                    
                    medoids[i].set_id_depot(old_medoids[i].get_id_depot())
                    medoids[i].get_location_depot().set_axis_x(old_medoids[i].get_location_depot().get_axis_x())
                    medoids[i].get_location_depot().set_axis_y(old_medoids[i].get_location_depot().get_axis_y())
                
                print(f"ID MEDOIDE: {medoids[i].get_id_depot()}")
                print(f"LISTA DE MEDOIDES X: {medoids[i].get_location_depot().get_axis_x()}")
                print(f"LISTA DE MEDOIDES Y: {medoids[i].get_location_depot().get_axis_y()}")
                print("---------------------------------------------")
            
            medoids[i].set_location_depot(best_loc_medoid)
            
    # Método que obtiene y retorna una lista de los identificadores de los medoides actuales.
    def get_id_medoids(self, medoids: List[Depot]) -> List[int]:
        id_medoids: List[int] = []
        
        for medoid in medoids:
            id_medoids.append(medoid.get_id_depot())
        
        print("--------------------------------------------------")
        print("ID MEDOIDES ACTUALES")
        print("--------------------------------------------------")
        print(id_medoids)
        
        return id_medoids
    
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

    # Método que ordena los elementos por proximidad a los depósitos según el tipo de distancia, 
    # utilizando una matriz de costos para determinar el orden.
    def sorted_elements(self, id_elements: List[int], distance_type: DistanceType) -> List[int]:
        total_depots = Problem.get_problem().get_total_depots()
        j = 0
        
        sorted_elements: List[int] = [-1] * len(id_elements)
        customers: List[Customer] = []
        
        for element_id in id_elements:
            customers.append(Problem.get_problem().get_customer_by_id_customer(element_id))

        cost_matrix: np.ndarray = None
        
        if distance_type.value in [1, 2, 3, 4]:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix(
                    customers, Problem.get_problem().get_depots(), distance_type
                )
            except (ValueError, AttributeError, TypeError) as e:
                print(f"Error filling cost matrix: {e}")
        elif distance_type == 5:
            pass
        
        while j < len(id_elements):
            row, col = np.unravel_index(
                np.argmin(
                    cost_matrix[0:len(id_elements), len(id_elements):len(id_elements) + total_depots]
                    ), cost_matrix[0:len(id_elements), len(id_elements):len(id_elements) + total_depots].shape
            )
            
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
    
    # Método que genera particiones de clientes con tamaños especificados, 
    # utilizando un muestreo aleatorio o secuencial según el tipo de muestreo indicado.
    def generate_partitions(self, sampsize: int, sampling_type: SamplingType) -> List[List[Customer]]:
        partitions: List[List[Customer]] = []
        partition: List[Customer] = []
        
        total_customers = len(Problem.get_problem().get_customers())
        total_partitions = total_customers // sampsize
        
        if total_customers % sampsize != 0:
            total_partitions += 1
            
        print(f"TOTAL DE PARTICIONES: {total_partitions}")
        print("---------------------------------------------------------------")
        
        if sampling_type == 0:
            j = 0
            customers = list(Problem.get_problem().get_customers())
            
            for i in range(total_partitions):
                while j < (sampsize * (i + 1)) and j < total_customers:
                    pos_element = random.randint(0, len(customers) - 1)
                    partition.append(customers.pop(pos_element))
                    j += 1
                
                print(f"PARTICIÓN {i + 1}: ")
                print(f"TOTAL DE ELEMENTOS DE LA PARTICIÓN {i + 1}: {len(partition)}")
                for customer in partition:
                    print(f"ELEMENTOS DE LA PARTICIÓN {i + 1}: {customer.get_id_customer()}")
                print("---------------------------------------------------------------")
                
                partitions.append(partition)
                partition = []
        
        elif sampling_type == 2:
            j = 0
            
            for i in range(total_partitions):
                while j < (sampsize * (i + 1)) and j < total_customers:
                    partition.append(Problem.get_problem().get_customers()[j])
                    j += 1
                
                print(f"PARTICIÓN {i + 1}: ")
                print(f"TOTAL DE ELEMENTOS DE LA PARTICIÓN {i + 1}: {len(partition)}")
                for customer in partition:
                    print(f"ELEMENTOS DE LA PARTICIÓN {i + 1}: {customer.get_id_customer()}")
                print("---------------------------------------------------------------")
                
                partitions.append(partition)
                partition = []
        
        return partitions
    
    # Método que genera elementos iniciales (centroides o medoides) para los clústeres.
    def generate_elements(self, p_customers: List[Customer], sampsize: int, distance_type: DistanceType) -> List[int]:
        id_elements: List[int] = []
        
        total_customers = len(Problem.get_problem().get_customers())
        total_depots = len(Problem.get_problem().get_depots())
        counter = total_depots
        
        cost_matrix: np.ndarray = None
        
        if distance_type.value in [1, 2, 3, 4]:
            pass
        elif distance_type.value == 5:
            cost_matrix = np.array(Problem.get_problem().get_cost_matrix())
            
        id_elements = [-1] * counter
        
        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")
        
        while counter > 0:
            row, col = np.unravel_index(
                np.argmin(cost_matrix[total_customers:, total_customers:]), cost_matrix[total_customers:, total_customers:].shape
            )

            print(f"ROW SELECCIONADA: {row}")
            print(f"COL SELECCIONADA: {col}")
            print(f"VALOR SELECCIONADO: {cost_matrix[row, col]}")

            id_element = Problem.get_problem().get_customers()[col].get_id_customer()
            id_elements[row - total_customers] = id_element

            print(f"ELEMENTO: {id_element}")
            print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")

            cost_matrix[total_customers:, col] = np.inf
            cost_matrix[row, :] = np.inf

            counter -= 1

        print("--------------------------------------------------")
        print("CENTROIDES/MEDOIDES INICIALES")
        print(id_elements)
        print("--------------------------------------------------")

        return id_elements
    
    # Método que calcula el coeficiente de disimilitud promedio entre los elementos de los clústeres, 
    # utilizando una matriz de disimilitud basada en el tipo de distancia especificado.
    def calculate_dissimilarity(self, distance_type: DistanceType, clusters: List[Cluster]) -> float:
        current_dissimilarity: float = 0.0
        dissimilarity_matrix: np.ndarray = None
        
        if distance_type.value in [1, 2, 3, 4]:
            try:
                dissimilarity_matrix = Problem.get_problem().fill_cost_matrix(
                    Problem.get_problem().get_customers(),
                    Problem.get_problem().get_depots(),
                    distance_type
                )
            except Exception as e:
                print(f"Error al llenar la matriz de disimilitud: {e}")

        elif distance_type.value == 5:
            pass
        
        total_clusters = len(clusters)
        current_dissimilarity_sum = 0.0
        
        for cluster in clusters:
            total_items = len(cluster.get_items_of_cluster())
            
            for j in range(total_items):
                pos_first_item = Problem.get_problem().get_pos_element(cluster.get_items_of_cluster()[j])
                
                for k in range(j + 1, total_items):
                    pos_second_item = Problem.get_problem().get_pos_element(cluster.get_items_of_cluster()[k])
                    
                    current_dissimilarity_sum += dissimilarity_matrix[pos_first_item, pos_second_item]
                    
        current_dissimilarity = current_dissimilarity_sum / total_clusters
        
        print(f"COEFICIENTE DE DISIMILITUD ACTUAL: {current_dissimilarity}")
        print("-------------------------------------------------------------------------------")

        return current_dissimilarity