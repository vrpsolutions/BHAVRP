import numpy as np
from typing import List
from by_centroids import ByCentroids
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class KMEANS(ByCentroids):
    
    def __init__(self):
        super().__init__()
        
    def get_current_iteration(self) -> int:
        return self.current_iteration
    
    def to_clustering(self) -> Solution:
        solution = Solution()
        
        # Generar elementos iniciales con el tipo de semilla y distancia.
        list_id_elements: List[int] = self.generate_elements(self.seed_type, self.distance_type)
        
        # Inicializar clústeres.
        list_clusters: List[Cluster] = self.initialize_clusters(list_id_elements)
        
        change: bool = False
        first: bool = True
        
        list_customers_to_assign: List[Customer] = []
        list_centroids: List[Depot] = []
        
        while True:
            # Obtener lista de clientes a asignar.
            list_customers_to_assign = list(Problem.get_problem().get_customers())
            
            if first:
                self.update_customer_to_assign(list_customers_to_assign, list_id_elements)
                list_centroids = self.create_centroids(list_id_elements)
                first = False
            else:
                self.clean_clusters(list_clusters)
            
            # Crear y llenar la matriz de costos.
            cost_matrix: np.ndarray = None
            try:
                cost_matrix = np.array(Problem.get_problem().fill_cost_matrix(
                    list_customers_to_assign, list_centroids, self.distance_type)
                )
            except (AttributeError, TypeError, ValueError) as e:
                print(f"Error al llenar la matriz de costos: {e}")
                
            # Asignar clientes a clústeres.
            self.step_assignment(list_clusters, list_customers_to_assign, cost_matrix)
            
            # Verificar si los centroides han cambiado.
            change = self.verify_centroids(list_clusters, list_centroids, self.distance_type)
            
            self.current_iteration += 1
            print(f"ITERACIÓN ACTUAL: {self.current_iteration}")
            
            if not change or self.current_iteration >= self.count_max_iterations:
                break
            
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