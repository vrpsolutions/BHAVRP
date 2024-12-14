import numpy as np
from typing import List
from partitional import Partitional
from ..seed_type import SeedType
from .....controller.utils.distance_type import DistanceType
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.input.depot import Depot
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

class PAM(Partitional):
    
    def __init__(self):
        self.distance_type = DistanceType.Euclidean
        self.seed_type = SeedType.NEAREST_DEPOT
        self.count_max_iterations = 100                 # Configurable
        self.current_iteration = 0
        
    def get_current_iteration(self) -> int:
        return self.current_iteration
    
    def to_clustering(self) -> Solution:
        solution = Solution()
        
        # Generar elementos iniciales con el tipo de semilla y distancia.
        list_id_elements: List[int] = self.generate_elements(self.seed_type, self.distance_type)
        list_clusters: List[Cluster] = self.initialize_clusters(list_id_elements)

        change: bool = True
        first: bool = True
        
        list_customers_to_assign: List[Customer] = []
        list_medoids: List[Depot] = []

        while change and self.current_iteration < self.count_max_iterations:
            # Obtener lista de clientes a asignar.
            list_customers_to_assign = list(Problem.get_problem().get_customers())
            self.update_customer_to_assign(list_customers_to_assign, list_id_elements)

            if first:
                list_medoids = self.create_centroids(list_id_elements)
                first = False
            else:
                self.update_clusters(list_clusters, list_id_elements)
                
            # Crear y llenar la matriz de costos.
            cost_matrix: np.ndarray = None
            cost_matrix_copy: np.ndarray = None
            try:
                cost_matrix = np.array(Problem.get_problem().fill_cost_matrix(
                    list_customers_to_assign, list_medoids, self.distance_type
                ))
                cost_matrix_copy = cost_matrix.copy()
            except (AttributeError, TypeError, ValueError) as e:
                print(f"Error al llenar la matriz de costos: {e}")
                
            # Asignar clientes a clústeres.
            self.step_assignment(list_clusters, list_customers_to_assign, cost_matrix)
            old_medoids: List[Depot] = self.replicate_depots(list_medoids)

            # Calcular el costo actual.
            best_cost: float = self.calculate_cost(list_clusters, cost_matrix_copy, list_medoids)

            # Realizar búsqueda para mejorar los medoids.
            self.step_search_medoids(list_clusters, list_medoids, cost_matrix_copy, best_cost)

            # Verificar si los medoids han cambiado.
            change = self.verify_medoids(old_medoids, list_medoids)
            
            if change and self.current_iteration + 1 != self.count_max_iterations:
                list_id_elements = self.get_id_medoids(list_medoids)
                self.clean_clusters(list_clusters)

            self.current_iteration += 1
            print(f"ITERACIÓN: {self.current_iteration}")
            
        # Procesar clientes no asignados.
        if list_customers_to_assign:
            for customer in list_customers_to_assign:
                solution.get_total_unassigned_items().append(customer.get_id_customer())

        # Agregar clústeres con elementos al resultado.
        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)
                    
        return solution