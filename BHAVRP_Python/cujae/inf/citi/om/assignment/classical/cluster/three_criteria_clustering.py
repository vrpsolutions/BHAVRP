import numpy as np
from typing import List
from ..by_not_urgency import ByNotUrgency
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class ThreeCriteriaClustering(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        
    def to_clustering(self):
        self.initialize()
        self.assign()
        return self.finish()
    
    def initialize(self):    
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.cost_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, Problem.get_problem().get_depots(), self.distance_type)
        self.total_customers = len(self.list_customers_to_assign)

    def assign(self):
        while any(self.list_customers_to_assign) and self.list_clusters:
            self.phase_one()
            self.phase_two()
            self.phase_three()

    def phase_one(self):
        list_id_candidates, list_values_candidates, list_differences = [], [], []
        
        # Preparación de candidatos
        for customer in self.list_customers_to_assign:
            if customer is not None:
                id_customer = customer.get_id_customer()
                list_averages = self.get_list_criterias_by_clusters(id_customer, self.list_clusters, 1)
                pos_min_value = list_averages.index(min(list_averages))
                difference = self.get_difference(list_averages, pos_min_value)
                percent = 0.1 * list_averages[pos_min_value]

                if difference >= percent:
                    list_id_candidates.append(id_customer)
                    list_values_candidates.append(list_averages)
                    list_differences.append(difference)

        # Asignación en la fase I
        while list_id_candidates:
            if not self.handle_candidate_phase_one(
                list_id_candidates, list_values_candidates, list_differences
            ):
                break

    def handle_candidate_phase_one(self, list_id_candidates, list_values_candidates, list_differences):
        pos_customer_ref = list_differences.index(max(list_differences))
        id_customer = list_id_candidates[pos_customer_ref]
        request_customer = Problem.get_problem().get_request_by_id_customer(id_customer)

        pos_cluster = list_values_candidates[pos_customer_ref].index(min(list_values_candidates[pos_customer_ref]))
        
        request_cluster = self.list_clusters[pos_cluster].get_request_cluster()
        capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
            Problem.get_problem().get_depot_by_id_depot(self.list_clusters[pos_cluster].get_id_cluster())
        )

        if capacity_depot >= (request_cluster + request_customer):
            self.update_assignment(pos_cluster, id_customer, request_customer)
            list_id_candidates.clear()
            list_differences.clear()
            list_values_candidates.clear()
            return True
        else:
            list_id_candidates.pop(pos_customer_ref)
            list_differences.pop(pos_customer_ref)
            list_values_candidates.pop(pos_customer_ref)
            return False

    def phase_two(self):
        list_id_candidates, list_values_candidates, list_differences = [], [], []
        
        # Preparación de candidatos
        for customer in self.list_customers_to_assign:
            if customer is not None:
                id_customer = customer.get_id_customer()
                list_variances = self.get_list_criterias_by_clusters(id_customer, self.list_clusters, 2)
                pos_min_value = list_variances.index(min(list_variances))
                difference = self.get_difference(list_variances, pos_min_value)
                percent = 0.4 * list_variances[pos_min_value]

                if difference >= percent:
                    list_id_candidates.append(id_customer)
                    list_values_candidates.append(list_variances)
                    list_differences.append(difference)

        # Asignación en la fase II
        while list_id_candidates:
            if not self.handle_candidate_phase_two(
                list_id_candidates, list_values_candidates, list_differences
            ):
                break

    def handle_candidate_phase_two(self, list_id_candidates, list_values_candidates, list_differences):
        pos_customer_ref = list_differences.index(max(list_differences))
        id_customer = list_id_candidates[pos_customer_ref]
        request_customer = Problem.get_problem().get_request_by_id_customer(id_customer)

        pos_cluster = list_values_candidates[pos_customer_ref].index(min(list_values_candidates[pos_customer_ref]))
        request_cluster = self.list_clusters[pos_cluster].get_request_cluster()
        capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
            Problem.get_problem().get_depot_by_id_depot(self.list_clusters[pos_cluster].get_id_cluster())
        )

        if capacity_depot >= (request_cluster + request_customer):
            self.update_assignment(pos_cluster, id_customer, request_customer)
            list_id_candidates.clear()
            list_differences.clear()
            list_values_candidates.clear()
            return True
        else:
            list_id_candidates.pop(pos_customer_ref)
            list_differences.pop(pos_customer_ref)
            list_values_candidates.pop(pos_customer_ref)
            return False

    def phase_three(self):
        list_id_candidates, list_values_candidates, list_nearest_dist = [], [], []
        
        # Preparación de candidatos
        for customer in self.list_customers_to_assign:
            if customer is not None:
                id_customer = customer.get_id_customer()
                list_averages = self.get_list_criterias_by_clusters(id_customer, self.list_clusters, 1)
                list_id_candidates.append(id_customer)
                list_values_candidates.append(list_averages)

                pos_cluster = list_averages.index(min(list_averages))
                list_dist_cc = self.get_distances_in_cluster(id_customer, self.list_clusters[pos_cluster])
                list_nearest_dist.append(list_dist_cc[list_dist_cc.index(min(list_dist_cc))])

        # Asignación en la fase III
        while list_id_candidates:
            if not self.handle_candidate_phase_three(
                list_id_candidates, list_values_candidates, list_nearest_dist
            ):
                break

    def handle_candidate_phase_three(self, list_id_candidates, list_values_candidates, list_nearest_dist):
        pos_customer_ref = list_nearest_dist.index(min(list_nearest_dist))
        
        if self.list_customers_to_assign[pos_customer_ref] is None:
            list_id_candidates.pop(pos_customer_ref)
            list_values_candidates.pop(pos_customer_ref)
            list_nearest_dist.pop(pos_customer_ref)
            return False
        
        id_customer = self.list_customers_to_assign[pos_customer_ref].get_id_customer()
        request_customer = Problem.get_problem().get_request_by_id_customer(id_customer)

        pos_cluster = list_values_candidates[pos_customer_ref].index(min(list_values_candidates[pos_customer_ref]))
        request_cluster = self.list_clusters[pos_cluster].get_request_cluster()
        capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
            Problem.get_problem().get_depot_by_id_depot(self.list_clusters[pos_cluster].get_id_cluster())
        )

        if capacity_depot >= (request_cluster + request_customer):
            self.update_assignment(pos_cluster, id_customer, request_customer)
            list_id_candidates.pop(pos_customer_ref)
            list_values_candidates.pop(pos_customer_ref)
            list_nearest_dist.pop(pos_customer_ref)
            return True
        else:
            list_id_candidates.pop(pos_customer_ref)
            list_values_candidates.pop(pos_customer_ref)
            list_nearest_dist.pop(pos_customer_ref)
            return False

    def update_assignment(self, pos_cluster, id_customer, request_customer):
        request_cluster = self.list_clusters[pos_cluster].get_request_cluster()
        capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
            Problem.get_problem().get_depot_by_id_depot(self.list_clusters[pos_cluster].get_id_cluster())
        )
        capacity_depot -= request_customer
        self.list_clusters[pos_cluster].set_request_cluster(request_cluster + request_customer)
        self.list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)

        self.list_customers_to_assign.pop(Problem.get_problem().find_pos_customer(self.list_customers_to_assign, id_customer))

        if self.is_full_depot(self.list_customers_to_assign, request_cluster, capacity_depot):
            if self.list_clusters[pos_cluster].get_items_of_cluster():
                self.solution.clusters.append(self.list_clusters.pop(pos_cluster))
            else:
                self.list_clusters.pop(pos_cluster)
                    
    def finish(self) -> Solution:
        if self.list_customers_to_assign:
            for customer in self.list_customers_to_assign:
                if customer is not None:
                    self.solution.get_unassigned_items().append(customer.get_id_customer())

        for cluster in self.list_clusters:
            if cluster.get_items_of_cluster():
                self.solution.clusters.append(cluster)
                
        OSRMService.clear_distance_cache()
        
        return self.solution
    
    # Este método devuelve la diferencia entre los dos clústers más cercanos a un cliente dado.
    def get_difference(self, list_values: List[float], pos_first_min: int):
        second_min = float('inf')
        for i, value in enumerate(list_values):
            if i != pos_first_min and value < second_min:
                second_min = value
        return second_min - list_values[pos_first_min]

    # Este método se encarga de devolver una lista con los valores de uno de los dos criterios 
    # de un cliente dado a todos los clústers.
    def get_list_criterias_by_clusters(self, id_customer: int, clusters: List[Cluster], criteria: int) -> float:
        list_values: List[float] = []
        cost_matrix = self.initialize_cost_matrix(Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), self.distance_type)
        pos_customer = Problem.get_problem().get_pos_element(id_customer)
        if criteria == 1:
            for cluster in clusters:
                list_values.append(self.get_avg_by_cluster(pos_customer, cluster, cost_matrix))
        elif criteria == 2:
            for cluster in clusters:
                list_values.append(self.get_var_by_cluster(pos_customer, cluster, cost_matrix))
        return list_values
    
    # Este método devuelve la distancia promedio de un cliente a un cluster.
    def get_avg_by_cluster(self, pos_customer: int, cluster: Cluster, cost_matrix: np.ndarray) -> float:
        distances = 0.0
        for item in cluster.get_items_of_cluster():
            distances += cost_matrix[(Problem.get_problem().get_pos_element(cluster.get_id_cluster()) - self.total_customers), pos_customer]
        distances += cost_matrix[(Problem.get_problem().get_pos_element(cluster.get_id_cluster()) - self.total_customers), pos_customer]
        return distances / (len(cluster.get_items_of_cluster()) + 1)
    
    # Este método calcula la varianza del promedio de distancias de un cliente dado a un cluster.
    def get_var_by_cluster(self, pos_customer: int, cluster: Cluster, cost_matrix: np.ndarray) -> float:
        avg_dist = self.get_avg_by_cluster(pos_customer, cluster, cost_matrix)
        variance = 0.0
        for item in cluster.get_items_of_cluster():
            diff = cost_matrix[(Problem.get_problem().get_pos_element(cluster.get_id_cluster()) - self.total_customers), pos_customer] - avg_dist
            variance += diff ** 2
        cluster_id_pos = Problem.get_problem().get_pos_element(cluster.get_id_cluster()) - self.total_customers
        diff = cost_matrix[cluster_id_pos, pos_customer] - avg_dist
        variance += diff ** 2
        return variance / (len(cluster.get_items_of_cluster()) + 1)
    
    # Este metodo devuelve una lista con las distancias de un cliente a cada cliente del 
    # cluster que se pasa por parametros.
    def get_distances_in_cluster(self, id_customer_ref: int, cluster: Cluster) -> List[float]:
        list_dist_cluster: List[float] = []
        pos_customer_ref = Problem.get_problem().get_pos_element(id_customer_ref)
        cost_matrix = self.initialize_cost_matrix(Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), self.distance_type)
        for item in cluster.get_items_of_cluster():
            pos_cc = (Problem.get_problem().get_pos_element(cluster.get_id_cluster()) - self.total_customers)
            list_dist_cluster.append(cost_matrix[pos_cc, pos_customer_ref])
        if not list_dist_cluster:
            pos_cc = (Problem.get_problem().get_pos_element(cluster.get_id_cluster()) - self.total_customers)
            list_dist_cluster.append(cost_matrix[pos_cc, pos_customer_ref])
        return list_dist_cluster
        