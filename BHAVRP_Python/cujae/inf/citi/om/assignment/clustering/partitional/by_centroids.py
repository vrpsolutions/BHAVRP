import numpy as np
from typing import List
from .partitional import Partitional
from ....service.distance_type import DistanceType
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.input.location import Location
from ....problem.solution.cluster import Cluster

class ByCentroids(Partitional):
    
    def __init__(self):
        super().__init__()
    
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
        
        if distance_type == DistanceType.REAL:
            pass
        else:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix(
                    Problem.get_problem().get_customers(), list_depot, distance_type
                )
            except (AttributeError, TypeError, ValueError) as e:
                print(f"Error filling cost matrix: {e}")
        
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
    
    # Método que verifica y actualiza los centroides de los clústeres.
    def verify_centroids(
        self, 
        clusters: List[Cluster], 
        centroids: List[Depot]
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
            self.update_centroids(clusters, centroids)
            
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
                
    # Método que actualiza los centroides de los clústeres.
    def update_centroids(
        self, 
        clusters: List[Cluster], 
        centroids: List[Depot]
    ):
        cost_matrix: np.ndarray = None
        
        try:
            cost_matrix = Problem.get_problem().calculate_cost_matrix_real(
                centroids, Problem.get_problem().get_depots()
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