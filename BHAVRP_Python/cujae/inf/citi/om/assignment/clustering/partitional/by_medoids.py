import random
import numpy as np
from typing import List
from partitional import Partitional
from ..sampling_type import SamplingType
from ....service.distance_type import DistanceType
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.input.location import Location
from ....problem.solution.cluster import Cluster

class ByMedoids(Partitional):
    
    def __init__(self):
        super().__init__()
        
    # Método que genera particiones de clientes con tamaños especificados, 
    # utilizando un muestreo aleatorio o secuencial según el tipo de muestreo indicado.
    def generate_partitions(
        self, 
        sampsize: int, 
        sampling_type: SamplingType
    ) -> List[List[Customer]]:
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
    def generate_elements(self, customers: List[Customer], distance_type: DistanceType) -> List[int]:
        id_elements: List[int] = [-1] * Problem.get_problem().get_total_depots()
        
        total_customers: int = len(customers)
        total_depots: int = Problem.get_problem().get_total_depots()
        counter = total_depots
        
        cost_matrix: np.ndarray = self.initialize_cost_matrix(
            customers,
            Problem.get_problem().get_depots(),
            distance_type
        )
        
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