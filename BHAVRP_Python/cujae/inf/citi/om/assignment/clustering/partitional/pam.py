import numpy as np
from typing import List
from .by_medoids import ByMedoids
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.input.location import Location
from ....problem.solution.solution import Solution
from ....problem.solution.cluster import Cluster

class PAM(ByMedoids):
    
    def __init__(self):
        super().__init__()
        self.list_id_elements: List[int]
        self.list_medoids: List[Depot]
        self.list_customers_to_assign: List[Customer]
        self.list_clusters: List[Cluster]
        
    def get_current_iteration(self) -> int:
        return self.current_iteration
    
    def to_clustering(self) -> Solution:
        self.initialize()
        self.assign()
        return self.finish()
        
    def initialize(self):
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.list_id_elements = self.generate_elements(self.list_customers_to_assign)
        self.list_clusters = self.initialize_clusters()
        self.list_medoids = []
        self.current_iteration = 0

    def assign(self):
        change: bool = True
        first: bool = True

        while change and self.current_iteration < self.count_max_iterations:
            if first:
                self.list_customers_to_assign = list(Problem.get_problem().get_customers())
                self.update_customer_to_assign()
                self.list_medoids = self.create_centroids()
                first = False
            else:
                self.update_clusters(self.list_clusters, self.list_id_elements)
                self.list_customers_to_assign = list(Problem.get_problem().get_customers())
                
            self.step_assignment(self.list_clusters, self.list_medoids)
            self.old_medoids = self.replicate_depots(self.list_medoids)
            self.best_cost = self.calculate_cost(self.list_medoids)
            self.step_search_medoids(self.list_clusters, self.list_medoids)

            change = self.verify_medoids(self.old_medoids, self.list_medoids)
            if change and (self.current_iteration + 1) != self.count_max_iterations:
                self.list_id_elements = self.get_id_medoids(self.list_medoids)
                self.clean_clusters(self.list_clusters)

            self.current_iteration += 1
            print(f"ITERACIÓN: {self.current_iteration}")
            
    def finish(self) -> Solution:
        solution = Solution()    
        
        if self.list_customers_to_assign:
            for customer in self.list_customers_to_assign:
                solution.get_total_unassigned_items().append(customer.get_id_customer())

        if self.list_clusters:
            for cluster in self.list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)
                    
        OSRMService.clear_distance_cache()
        
        return solution
    
    # Método que realiza la búsqueda de mejores medoides en cada clúster evaluando diferentes candidatos.
    def step_search_medoids(
        self, 
        clusters: List[Cluster], 
        medoids: List[Depot]
    ):
        current_cost = 0.0
        
        old_medoids: List[Depot] = self.replicate_depots(medoids)
        
        print("--------------------------------------------------------------------")
        print(f"PROCESO DE BÚSQUEDA")
        
        for i in range(len(clusters)):
            best_loc_medoid = Location(medoids[i].get_location_depot().get_axis_x(), medoids[i].get_location_depot().get_axis_y())
            
            print("--------------------------------------------------")
            print(f"MEJOR MEDOIDE ID: {medoids[i].get_id_depot()}")
            print(f"MEJOR MEDOIDE LOCATION X: {best_loc_medoid.get_axis_x()}")
            print(f"MEJOR MEDOIDE LOCATION Y: {best_loc_medoid.get_axis_y()}")
            print("--------------------------------------------------")
            
            for j in range(1, len(clusters[i].get_items_of_cluster())):
                new_id_medoid = clusters[i].get_items_of_cluster()[j]
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
                print(f"ID: {medoids[i].get_id_depot()}")
                print(f"X: {medoids[i].get_location_depot().get_axis_x()}")
                print(f"Y: {medoids[i].get_location_depot().get_axis_y()}")
                
                print("LISTA DE ANTERIORES MEDOIDES")
                print(f"ID: {old_medoids[i].get_id_depot()}")
                print(f"X: {old_medoids[i].get_location_depot().get_axis_x()}")
                print(f"Y: {old_medoids[i].get_location_depot().get_axis_y()}")
                print("--------------------------------------------------")
                
                current_cost = self.calculate_cost(medoids)
                
                print("---------------------------------------------")
                print(f"ACTUAL COSTO TOTAL: {current_cost}")
                print("---------------------------------------------")
                
                if current_cost < self.best_cost:
                    self.best_cost = current_cost
                    best_loc_medoid = medoids[i].get_location_depot()
                    
                    print(f"NUEVO MEJOR COSTO TOTAL: {self.best_cost}")
                    print(f"NUEVO MEDOIDE ID: {medoids[i].get_id_depot()}")
                    print(f"NUEVO MEDOIDE LOCATION X: {best_loc_medoid.get_axis_x()}")
                    print(f"NUEVO MEDOIDE LOCATION Y: {best_loc_medoid.get_axis_y()}")
                    print("---------------------------------------------")
                    
                    old_medoids[i].set_id_depot(medoids[i].get_id_depot())
                    old_medoids[i].get_location_depot().set_axis_x(medoids[i].get_location_depot().get_axis_x())
                    old_medoids[i].get_location_depot().set_axis_y(medoids[i].get_location_depot().get_axis_y())
                else:
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
        medoids: List[Depot]
    ) -> float:
        cost = 0.0
        
        print("-------------------------------------------------------------------------------")
        print("CÁLCULO DEL MEJOR COSTO")
        
        cost_matrix: np.ndarray = self.initialize_cost_matrix(Problem.get_problem().get_customers(), medoids, self.distance_type)

        for i, cluster in enumerate(self.list_clusters):
            pos_depot = Problem.get_problem().get_pos_element(medoids[i].get_id_depot())
            if pos_depot >= len(medoids):
                pos_depot = pos_depot % len(medoids)
            list_id_customers = list(cluster.get_items_of_cluster())
            
            print("-------------------------------------------------------------------------------")
            print(f"ID MEDOIDE: {medoids[i].get_id_depot()}")
            print(f"POSICIÓN DEL MEDOIDE: {pos_depot}")
            print(f"CLIENTES ASIGNADOS AL MEDOIDE: {list_id_customers}")
            print("-------------------------------------------------------------------------------")

            for customer_id in list_id_customers:
                pos_customer = Problem.get_problem().get_pos_element(customer_id)

                if pos_depot == pos_customer:
                    cost += 0.0
                    actual_cost = 0.0
                else:
                    actual_cost = cost_matrix[pos_depot, pos_customer]
                    cost += actual_cost
                
                print(f"ID CLIENTE: {customer_id}")
                print(f"POSICIÓN DEL CLIENTE: {pos_customer}")
                print(f"COSTO: {actual_cost}")
                print("-------------------------------------------------------------------------------")
                print(f"COSTO ACUMULADO: {cost}")
                print("-------------------------------------------------------------------------------")
        
        print(f"MEJOR COSTO TOTAL: {cost}")
        print("-------------------------------------------------------------------------------")
        
        return cost