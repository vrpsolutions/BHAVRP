import numpy as np
from typing import List
from by_medoids import ByMedoids
from ..sampling_type import SamplingType
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.input.location import Location
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class CLARA(ByMedoids):
    
    def __init__(self):
        self.sampling_type = SamplingType.RANDOM_SAMPLING                   # Configurable
        self.sampsize = 10
        
    def get_current_iteration(self) -> int:
        return self.current_iteration
        
    def to_clustering(self) -> Solution:
        
        
        # Listas para manejar los clientes, clusters y elementos no asignados.
        list_customers_to_assign: List[Customer] = []
        best_cluster: List[Cluster] = []
        list_unassigned_customers: List[int] = []
        unassigned_items_in_partition: List[int] = []
        
        # Generar particiones iniciales según el tamaño de la muestra y el tipo de muestreo.
        list_partitions: List[List[Customer]] = self.generate_partitions(self.sampsize, self.sampling_type)
        
        # Iterar sobre cada partición generada.
        for partition in list_partitions:
            self.current_iteration = 0
            
            # Generar los elementos de identificación y los clusters iniciales.
            list_id_elements: List[int] = self.generate_elements(partition, self.distance_type)
            list_clusters: List[Cluster] = self.initialize_clusters(list_id_elements)
            
            change: bool = True
            first: bool = True
            
            list_medoids: List[Depot] = []
            
            # Bucle para iterar mientras haya cambios y no se alcance el número máximo de iteraciones.
            while change and self.current_iteration < self.count_max_iterations:
                # Asignar los clientes de la partición para esta iteración.
                list_customers_to_assign = list(partition)
                self.update_customer_to_assign(list_customers_to_assign, list_id_elements)
                
                if first:
                    # Crear los centroides (medoids) en la primera iteración.
                    list_medoids = self.create_centroids(list_id_elements)
                    first = False
                else:
                    # Actualizar los clusters en iteraciones posteriores.
                    self.update_clusters(list_clusters, list_id_elements)
                
                # Inicializar las matrices de costos.
                cost_matrix: np.ndarray = None
                cost_matrix_copy: np.ndarray = None
                try:
                    cost_matrix = np.array(Problem.get_problem().fill_cost_matrix(
                        list_customers_to_assign, list_medoids, self.distance_type
                    ))
                    cost_matrix_copy = cost_matrix
                except (AttributeError, TypeError, ValueError) as e:
                    print(f"Error al llenar la matriz de costos: {e}")
                
                # Asignar clientes a clusters según la matriz de costos.
                self.step_assignment(list_clusters, list_customers_to_assign, cost_matrix)
                
                # Replicar los medoids de esta iteración para verificar cambios.
                old_medoids = self.replicate_depots(list_medoids)
                
                # Calcular el costo de la partición actual.
                best_cost = self.calculate_cost(list_clusters, cost_matrix_copy, list_medoids, partition)
                
                # Buscar los nuevos medoids con base en el costo.
                self.step_search_medoids(list_clusters, list_medoids, cost_matrix_copy, best_cost, partition)
                
                # Verificar si los medoids han cambiado y decidir si continuar la iteración.
                change = self.verify_medoids(old_medoids, list_medoids)
                
                if change and (self.current_iteration + 1) != self.count_max_iterations:
                    list_id_elements.clear()
                    list_id_elements = self.get_id_medoids(list_medoids)
                    self.clean_clusters(list_clusters)
                
                self.current_iteration += 1
                print(f"ITERACIÓN: {self.current_iteration}")
            
            # Al finalizar todas las iteraciones para esta partición, procesar los clientes no asignados.
            if list_customers_to_assign:
                for customer in list_customers_to_assign:
                    unassigned_items_in_partition.append(customer.get_id_customer())
            
            # Obtener los elementos de la partición actual y actualizar los clientes a asignar.
            elements_in_partition: List[int] = list(Problem.get_problem().get_list_id(partition))
            list_customers_to_assign = list(Problem.get_problem().get_customers())
            self.update_customer_to_assign(list_customers_to_assign, elements_in_partition)
            
            cost_matrix = None
            
            try:
                cost_matrix = np.array(Problem.get_problem().fill_cost_matrix(
                    list_customers_to_assign, list_medoids, self.distance_type
                    ))
            except (AttributeError, TypeError, ValueError) as e:
                print(f"Error al llenar la matriz de costos: {e}")

            # Asignar nuevamente los clientes a los clusters.
            self.step_assignment(list_clusters, list_customers_to_assign, cost_matrix)
            
            # Calcular la disimilitud para la partición actual.
            best_dissimilarity: float = 0.0
            current_dissimilarity: float = self.calculate_dissimilarity(self.distance_type, list_clusters)
            
            # Verificar si esta partición es la mejor hasta ahora según la disimilitud.
            if len(list_partitions) == 1:
                best_dissimilarity = current_dissimilarity
                best_cluster = list_clusters
                
                if unassigned_items_in_partition:
                    list_unassigned_customers = unassigned_items_in_partition
                
            else:
                if current_dissimilarity < best_dissimilarity:
                    best_dissimilarity = current_dissimilarity
                    best_cluster = list_clusters
                    
                    if unassigned_items_in_partition:
                        list_unassigned_customers = unassigned_items_in_partition
            
            unassigned_items_in_partition.clear()
        
        # Si hay clientes no asignados, agregarlos a la solución.
        if list_unassigned_customers:
            for customer_id in list_unassigned_customers:
                self.solution.get_unassigned_items().add(customer_id)

        # Si se encontró el mejor clúster, agregarlo a la solución.
        if best_cluster:
            for cluster in best_cluster:
                if cluster.get_items_of_cluster():
                    self.solution.get_clusters().add(cluster)
        
        return self.solution
    
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