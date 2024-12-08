import numpy as np
from typing import List
from by_cluster import ByCluster
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

class ThreeCriteriaClustering(ByCluster):
    
    def __init__(self):
        super().__init__()
        
    def to_clustering(self):
        solution = Solution()
        
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = Problem.get_problem().get_total_customers().copy()
        
        id_customer: int = -1
        request_customer: float = 0.0
        
        capacity_depot: float = 0.0
        
        pos_cluster: int = -1
        request_cluster: float = 0.0
        
        difference: float = -1.0
        percent: float = -1.0
        pos_min_value: int = -1
        pos_customer_ref: int = -1
        
        list_id_candidates: List[int] = []
        list_values_candidates: List[List[float]] = []
        list_differences: List[float] = []
        
        while list_customers_to_assign and list_clusters:
            
            # Determinar candidatos.
            for customer in list_customers_to_assign:
                id_customer = customer.get_id_customer()
                list_averages = self.get_list_criterias_by_clusters(id_customer, list_clusters, 1)
                
                pos_min_value = self.get_pos_min_value(list_averages)
                difference = self.get_difference(list_averages, pos_min_value)
                percent = 0.1 * list_averages[pos_min_value]
                
                if difference >= percent:
                    list_id_candidates.append(id_customer)
                    list_values_candidates.append(list_averages)
                    list_differences.append(difference)
                
            # Asignar candidatos - Primera fase.
            while list_id_candidates:
                pos_customer_ref = self.get_pos_max_value(list_differences)
                id_customer = list_id_candidates[pos_customer_ref]
                request_customer = Problem.get_problem().get_request_by_id_customer(id_customer)
                
                pos_cluster = self.get_pos_min_value(list_values_candidates[pos_customer_ref])
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                    Problem.get_problem().get_depot_by_id_depot(list_clusters[pos_cluster].get_id_cluster())
                )
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)
                    
                    list_customers_to_assign.remove(customer)
                    list_id_candidates.clear()
                    list_differences.clear()
                    list_values_candidates.clear()
                    
                    if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                        if list_clusters[pos_cluster].get_items_of_cluster():
                            solution.clusters.append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(pos_cluster)
                    break
                else:
                    del list_id_candidates[pos_customer_ref]
                    del list_differences[pos_customer_ref]
                    del list_values_candidates[pos_customer_ref]
                
            # Asignar candidatos - Segunda fase.
            for customers in list_customers_to_assign:
                id_customer = customers.get_id_customer()
                list_variances = self.get_list_criterias_by_clusters(id_customer, list_clusters, 2)
                
                pos_min_value = self.get_pos_min_value(list_variances)
                difference = self.get_difference(list_variances, pos_min_value)
                percent = 0.4 * list_variances[pos_min_value]
                
                if difference >= percent:
                    list_id_candidates.append(id_customer)
                    list_values_candidates.append(list_variances)
                    list_differences.append(difference)
            
            while list_id_candidates:
                pos_customer_ref = self.get_pos_max_value(list_differences)
                id_customer = list_id_candidates[pos_customer_ref]
                request_customer = Problem.get_problem().get_request_by_id_customer(id_customer)
                
                pos_cluster = self.get_pos_min_value(list_values_candidates[pos_customer_ref])
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                    Problem.get_problem().get_depot_by_id_depot(list_clusters[pos_cluster].get_id_cluster())
                )
                
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)

                    list_customers_to_assign.remove(customer)
                    list_id_candidates.clear()
                    list_differences.clear()
                    list_values_candidates.clear()
                    
                    if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                        if list_clusters[pos_cluster].get_items_of_cluster():
                            solution.clusters.append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(pos_cluster)
                    break
                else:
                    del list_id_candidates[pos_customer_ref]
                    del list_differences[pos_customer_ref]
                    del list_values_candidates[pos_customer_ref]
        
            # Asignar candidatos - Tercera fase.
            list_nearest_dist = []
            for customer in list_customers_to_assign:
                id_customer = customer.get_id_customer()
                list_averages = self.get_list_criterias_by_clusters(id_customer, list_clusters, 1)
                
                list_id_candidates.append(id_customer)
                list_values_candidates.append(list_averages)
                
                pos_cluster = self.get_pos_min_value(list_averages)
                list_dist_cc = self.get_distances_in_cluster(id_customer, list_clusters[pos_cluster])
                list_nearest_dist.append(list_dist_cc[self.get_pos_min_value(list_dist_cc)])
                
            while list_id_candidates:
                pos_customer_ref = self.get_pos_min_value(list_nearest_dist)
                id_customer = list_customers_to_assign[pos_customer_ref].get_id_customer()
                request_customer = Problem.get_problem().get_request_by_id_customer(id_customer)

                pos_cluster = self.get_pos_min_value(list_values_candidates[pos_customer_ref])
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                    Problem.get_problem().get_depot_by_id_depot(list_clusters[pos_cluster].get_id_cluster())
                )
                
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)

                    list_customers_to_assign.remove(customer)
                    list_id_candidates.pop(pos_customer_ref)
                    list_values_candidates.pop(pos_customer_ref)
                    list_nearest_dist.pop(pos_customer_ref)

                    if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                        if list_clusters[pos_cluster].get_items_of_cluster():
                            solution.clusters.append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(pos_cluster)
                    break
                else:
                    del list_id_candidates[pos_customer_ref]
                    del list_nearest_dist[pos_customer_ref]
                    del list_values_candidates[pos_customer_ref]
                    
        # Agregar elementos restantes a solución.
        for customer in list_customers_to_assign:
            solution.unassigned_items.append(customer.get_id_customer())

        for cluster in list_clusters:
            if cluster.get_items_of_cluster():
                solution.clusters.append(cluster)
        
        return solution
    
    # Este método devuelve la diferencia entre los dos clústers más cercanos a un cliente dado.
    def get_difference(self, list_values, pos_first_min):
        second_min = float("inf")
        for i, value in enumerate(list_values):
            if i != pos_first_min and value < second_min:
                second_min = value
        return second_min - list_values[pos_first_min]

    # Este método se encarga de devolver una lista con los valores de uno de los dos criterios 
    # de un cliente dado a todos los clústers.
    def get_list_criterias_by_clusters(self, id_customer, clusters, criteria, problem):
        list_values = []
        cost_matrix = problem.get_cost_matrix()
        pos_customer = problem.get_pos_element(id_customer)

        if criteria == 1:
            for cluster in clusters:
                list_values.append(
                    self.get_avg_by_cluster(pos_customer, cluster, cost_matrix, problem)
                )
        elif criteria == 2:
            for cluster in clusters:
                list_values.append(
                    self.get_var_by_cluster(pos_customer, cluster, cost_matrix, problem)
                )

        return list_values
    
    # Este método devuelve la distancia promedio de un cliente a un cluster.
    def get_avg_by_cluster(self, pos_customer, cluster, cost_matrix, problem):
        distances = 0.0
        for item in cluster.get_items_of_cluster():
            distances += cost_matrix[pos_customer, problem.get_pos_element(item)]
        distances += cost_matrix[pos_customer, problem.get_pos_element(cluster.get_id_cluster())]
        return distances / (len(cluster.get_items_of_cluster()) + 1)
    
    # Este método calcula la varianza del promedio de distancias de un cliente dado a un cluster.
    def get_var_by_cluster(self, pos_customer, cluster, cost_matrix, problem):
        avg_dist = self.get_avg_by_cluster(pos_customer, cluster, cost_matrix, problem)
        variance = 0.0
        for item in cluster.get_items_of_cluster():
            diff = cost_matrix[pos_customer, problem.get_pos_element(item)] - avg_dist
            variance += diff ** 2
        return variance / len(cluster.get_items_of_cluster())
    
    # Este metodo devuelve una lista con las distancias de un cliente a cada cliente del cluster que se pasa por parametros.
    def get_distances_in_cluster(self):
        pass