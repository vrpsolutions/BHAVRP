import numpy as np
from typing import List
from partitional import Partitional
from ..seed_type import SeedType
from ..sampling_type import SamplingType
from .....controller.utils.distance_type import DistanceType
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.input.depot import Depot
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

class CLARA(Partitional):
    
    def __init__(self):
        self.distance_type = DistanceType.Euclidean
        self.seed_type = SeedType.NEAREST_DEPOT
        self.sampling_type = SamplingType.RANDOM_SAMPLING
        self.count_max_iterations = 2                      # Configurable
        self.sampsize = 10
        self.current_iteration = 0
        
    def get_current_iteration(self) -> int:
        return self.current_iteration
        
    def to_clustering(self) -> Solution:
        solution = Solution()
        
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
                solution.get_unassigned_items().add(customer_id)

        # Si se encontró el mejor clúster, agregarlo a la solución.
        if best_cluster:
            for cluster in best_cluster:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().add(cluster)
        
        return solution