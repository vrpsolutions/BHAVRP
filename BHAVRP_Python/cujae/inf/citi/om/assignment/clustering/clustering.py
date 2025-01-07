from typing import List
from ..assignment import Assignment
from ...controller.tools.tools import Tools
from ...problem.input.problem import Problem
from ...problem.input.customer import Customer
from ...problem.input.location import Location
from ...problem.solution.cluster import Cluster

class Clustering(Assignment):
    
    def __init__(self):
        super().__init__()
    
    # Método para inicializar una lista de clusters con una lista de elementos.
    def initialize_clusters(self) -> List[Cluster]:
        if not self.list_id_elements:
            raise ValueError("La lista de elementos no puede estar vacía.")
        
        clusters: List[Cluster] = []
        total_elements = len(self.list_id_elements)

        if total_elements == len(Problem.get_problem().get_depots()):
            for i in range(total_elements):
                list_id_customers: List[int] = [self.list_id_elements[i]]
                cluster = Cluster(
                    Problem.get_problem().get_depots()[i].get_id_depot(),
                    Problem.get_problem().get_request_by_id_customer(self.list_id_elements[i]),
                    list_id_customers
                )
                clusters.append(cluster)
        else:
            for i in range(total_elements):
                list_id_customers: List[int] = []
                pos_depot = Problem.get_problem().find_pos_depot(Problem.get_problem().get_depots(), self.list_id_elements[i])
            
                if pos_depot == -1:  # No es un depot
                    list_id_customers.append(self.list_id_elements[i])
                    cluster = Cluster(
                        self.list_id_elements[i], 
                        Problem.get_problem().get_request_by_id_customer(self.list_id_elements[i]), 
                        list_id_customers
                    )
                else:
                    cluster = Cluster(self.list_id_elements[i], 0.0, list_id_customers)
                clusters.append(cluster)

        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for cluster in clusters:
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")

        return clusters
    
    # Método que determina si existen clientes que puedan ser asignados al depósito a partir de su demanda.
    def is_full_depot(self, customers: List[Customer], request_cluster: float, capacity_depot: float) -> bool:
        is_full: bool = True
        current_request: float = capacity_depot - request_cluster
        if current_request > 0:
            for customer in customers:
                if customer.get_request_customer() <= current_request:
                    is_full = False
                    break
        return is_full
    
    # Método que recalcula el centroide de un cluster basado en las ubicaciones de sus clientes asignados.
    def recalculate_centroid(self, cluster: Cluster) -> Location:
        ave_axis_x = 0.0
        ave_axis_y = 0.0
        count_customers = len(cluster.get_items_of_cluster())

        for customer_id in cluster.get_items_of_cluster():
            location = Problem.get_problem().get_location_by_id_customer(customer_id)
            ave_axis_x += location.get_axis_x()
            ave_axis_y += location.get_axis_y()

        ave_axis_x /= count_customers
        ave_axis_y /= count_customers

        location_centroid = Location()
        location_centroid.set_axis_x(Tools.truncate_double(ave_axis_x, 6))
        location_centroid.set_axis_y(Tools.truncate_double(ave_axis_y, 6))

        return location_centroid