import numpy as np
from typing import List
from .partitional import Partitional
from ....service.distance_type import DistanceType
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.input.fleet import Fleet
from ....problem.input.location import Location

class ByCentroids(Partitional):
    
    def __init__(self):
        super().__init__()
    
    # Método que verifica y actualiza los centroides de los clústeres.
    def verify_centroids(self) -> bool:
        change: bool = False
        dummy_depot: Location = None
        
        print(f"change: {change}")
        
        for i, cluster in enumerate(self.list_clusters):
            if cluster.get_items_of_cluster():  # Si el clúster no está vacío
                dummy_depot = self.recalculate_centroid(cluster)
            else:
                dummy_depot = self.list_centroids[i].get_location_depot()
            
            print("------------------------------------------------------------------")
            print(f"DUMMY_DEPOT {i} X: {dummy_depot.get_axis_x()}")
            print(f"DUMMY_DEPOT {i} Y: {dummy_depot.get_axis_y()}")

            print(f"CENTROIDE {i} X: {self.list_centroids[i].get_location_depot().get_axis_x()}")
            print(f"CENTROIDE {i} Y: {self.list_centroids[i].get_location_depot().get_axis_y()}")
            
            if (self.list_centroids[i].get_location_depot().get_axis_x() != dummy_depot.get_axis_x() or self.list_centroids[i].get_location_depot().get_axis_y() != dummy_depot.get_axis_y()):
                change = True
                self.list_centroids[i].set_id_depot(-1)
                
                location = Location()
                location.set_axis_x(dummy_depot.get_axis_x())
                location.set_axis_y(dummy_depot.get_axis_y())
                self.list_centroids[i].set_location_depot(location)

                print(f"change: {change}")
                print(f"CENTROIDE {i} X: {self.list_centroids[i].get_location_depot().get_axis_x()}")
                print(f"CENTROIDE {i} Y: {self.list_centroids[i].get_location_depot().get_axis_y()}")
            else:
                print(f"CENTROIDE {i} X: {self.list_centroids[i].get_location_depot().get_axis_x()}")
                print(f"CENTROIDE {i} Y: {self.list_centroids[i].get_location_depot().get_axis_y()}")

        if change:
            self.update_centroids(self.list_centroids, self.distance_type)
            
        print(f"CAMBIO LOS CENTROIDES: {change}")
        
        return change
    
    # Método que actualiza los centroides de los clústeres.
    def update_centroids(
        self, 
        centroids: List[Depot], 
        distance_type: DistanceType
    ):
        cost_matrix: np.ndarray = None
        
        if distance_type == DistanceType.REAL:
            try:
                cost_matrix = Problem.get_problem().calculate_cost_matrix_real(
                    centroids, Problem.get_problem().get_depots()
                )
            except (ValueError, AttributeError, TypeError) as e:
                print("Error calculating cost matrix real:", e)
        else:
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
            min_index = np.argmin(cost_matrix[:total_centroids, :total_centroids])
            row = min_index // cost_matrix.shape[1]
            col = min_index % cost_matrix.shape[1]

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

                fleet: List[Fleet] = temp_centroids[pos_centroid].get_fleet_depot()
                depot.set_fleet_depot(fleet)

                centroids[pos_depot] = depot

            cost_matrix[:, pos_depot] = float('inf')
            cost_matrix[pos_centroid, :] = float('inf')

            for i in range(cost_matrix.shape[0]):
                for j in range(cost_matrix.shape[1]):
                    print(f"Row: {i} Col: {j} VALUE: {cost_matrix[i, j]}")
                print("---------------------------------------------")