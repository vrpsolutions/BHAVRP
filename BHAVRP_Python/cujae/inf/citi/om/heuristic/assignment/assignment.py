from abc import ABC, abstractmethod
from typing import List
from ...controller.utils.tools import Tools
from ...problem.input.problem import Problem
from ...problem.input.customer import Customer
from ...problem.input.location import Location
from ...problem.output.solution.solution import Solution
from ...problem.output.solution.cluster import Cluster

"""
Clase que modela un algoritmo de asignación para problemas con múltiples depósitos
"""
class Assignment(ABC):
    
    @abstractmethod
    def to_clustering(self) -> Solution:
        pass
    
    # Método para inicializar una lista de clusters.
    def initialize_clusters(self) -> List[Cluster]:
        list_clusters: List[Cluster] = []
        
        total_clusters = len(Problem.get_problem().get_depots())
        
        for i in range(total_clusters):
            list_id_items: List[int] = []
            cluster = Cluster(Problem.get_problem().get_list_id_depots()[i], 0.0, list_id_items)
            list_clusters.append(cluster)
        
        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        
        for cluster in list_clusters:
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")

        return list_clusters
    
    # Método para inicializar una lista de clusters con una lista de elementos.
    def initialize_clusters(self, list_id_elements: List[int]) -> List[Cluster]:
        clusters: List[Cluster] = []
        
        total_elements = len(list_id_elements)

        if total_elements == len(Problem.get_problem().get_depots()):
            for i in range(total_elements):
                list_id_customers: List[int] = [list_id_elements[i]]
                cluster = Cluster(
                    Problem.get_problem().get_depots()[i].get_id_depot(),
                    Problem.get_problem().get_request_by_id_customer(list_id_elements[i]),
                    list_id_customers
                )
                clusters.append(cluster)
        else:
            for i in range(total_elements):
                list_id_customers: List[int] = []
                if Problem.get_problem().find_pos_depot(Problem.get_problem().get_depots(), list_id_elements[i]) == -1:
                    list_id_customers.append(list_id_elements[i])
                    cluster = Cluster(
                        list_id_elements[i], 
                        Problem.get_problem().get_request_by_id_customer(total_elements[i]), 
                        list_id_customers
                    )
                else:
                    cluster = Cluster(list_id_elements[i], 0.0, list_id_customers)
                clusters.append(cluster)

        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for cluster in clusters:
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")

        return clusters
    
    # Método que busca la posición de un cluster en el listado de clusters.
    def find_cluster(self, id_cluster: int, clusters: List[Cluster]) -> int:
        pos_cluster = -1
        for i, cluster in enumerate(clusters):
            if cluster.get_id_cluster() == id_cluster:
                pos_cluster = i
                break
        return pos_cluster
    
    # Método que obtiene la posición del valor máximo en una lista de números flotantes.
    def get_pos_max_value(self, list: List[float]) -> int:
        if not list:
            return -1

        max_value = list[0]
        pos_max_value = 0

        for i in range(1, len(list)):
            if list[i] > max_value:
                max_value = list[i]
                pos_max_value = i
        return pos_max_value

    # Método que obtiene la posición del valor mínimo en una lista de números flotantes.
    def get_pos_min_value(self, list: List[float]) -> int:
        if not list:
            return -1

        min_value = list[0]
        pos_min_value = 0

        for i in range(1, len(list)):
            if list[i] < min_value:
                min_value = list[i]
                pos_min_value = i
        return pos_min_value
    
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

    # Método que determina si existen clientes que puedan ser asignado al depósito.
    def get_customers_out_depot(
        self, 
        customers: List[Customer], 
        request_cluster: float, 
        capacity_depot: float
    ) -> List[int]:
        customers_out_depot: List[int] = []
        current_request: float = capacity_depot - request_cluster
        
        if current_request != 0:
            for i, customer in enumerate(customers):
                if customer.get_request_customer() > current_request:
                    customers_out_depot.append(i)
        return customers_out_depot

    # Método que devuelve la posición del cluster (en la lista de clusters) al que debe asignarse 
    # el cliente que se está analizando, dado el identificador del elemento por el que se va a asignar dicho cliente.
    def get_pos_cluster(self, pos_customer: int, clusters: List[Cluster]) -> int:
        id_customer = Problem.get_problem().get_list_id_customers()[pos_customer]
        for i, cluster in enumerate(clusters):
            if id_customer in cluster.get_items_of_cluster():
                return i
        return -1
    
    # Método que recalcula la ubicación media entre dos clusters en función de 
    # las ubicaciones de los clientes asignados a ellos.
    def recalculate_test(
        self, 
        cluster_one: Cluster, 
        cluster_two: Cluster, 
        customers_to_assign: List[Customer]
    ) -> Location:
        location_one = customers_to_assign[
            Problem.get_problem().find_pos_customer(
                customers_to_assign, 
                cluster_one.get_id_cluster()
            )].get_location_customer()
        location_two = customers_to_assign[
            Problem.get_problem().find_pos_customer(
                customers_to_assign, 
                cluster_two.get_id_cluster()
            )].get_location_customer()

        ave_axis_x = (location_one.get_axis_x() + location_two.get_axis_y()) / 2
        ave_axis_y = (location_two.get_axis_x() + location_two.get_axis_y()) / 2

        location = Location()
        location.set_axis_x(ave_axis_x)
        location.set_axis_y(ave_axis_y)

        return location
    
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