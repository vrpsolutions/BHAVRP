import numpy as np
from typing import List
from partitional import Partitional
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.input.depot import Depot
from .....problem.input.location import Location
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

class PAM(Partitional):
    
    def __init__(self):
        super().__init__()
        
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
    
    # Método que realiza la búsqueda de mejores medoides en cada clúster evaluando diferentes candidatos.
    def step_search_medoids(
        self, 
        clusters: List[Cluster], 
        medoids: List[Depot], 
        cost_matrix: np.ndarray, 
        best_cost: float
    ):
        current_cost: float = 0.0
        
        old_medoids: List[Depot] = self.replicate_depots(medoids)
        
        print("--------------------------------------------------------------------")
        print(f"PROCESO DE BÚSQUEDA")
        
        for i in range(len(clusters)):
            best_loc_medoid = Location(medoids[i].get_location_depot().get_axis_x(),
                                    medoids[i].get_location_depot().get_axis_y())
            
            print("--------------------------------------------------")
            print(f"MEJOR MEDOIDE ID: {medoids[i].get_id_depot()}")
            print(f"MEJOR MEDOIDE LOCATION X: {best_loc_medoid.get_axis_x()}")
            print(f"MEJOR MEDOIDE LOCATION Y: {best_loc_medoid.get_axis_y()}")
            print("--------------------------------------------------")
            
            for j in range(1, len(clusters[i].get_items_of_cluster())):
                new_id_medoid = clusters[i].get_items_of_cluster()[j]
                new_medoid = Customer()
                
                customer = Problem.get_problem().get_customer_by_id_customer(new_id_medoid)
                new_medoid.set_id_customer(customer.get_id_customer())
                new_medoid.set_request_customer(customer.get_request_customer())
                
                location = Location()
                location.set_axis_x(customer.get_location_customer().get_axis_x())
                location.set_axis_y(customer.get_location_customer().get_axis_y())
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
                
                current_cost = self.calculate_cost(clusters, cost_matrix, medoids)
                
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
    def calculate_cost(self, clusters: List[Cluster], cost_matrix: np.ndarray, medoids: List[Depot]) -> float:
        cost: float = 0.0
        
        print("-------------------------------------------------------------------------------")
        print("CÁLCULO DEL MEJOR COSTO")
        
        for i, cluster in enumerate(clusters):
            pos_depot = Problem.get_problem().get_pos_element(medoids[i].get_id_depot())
            list_id_customers = list(cluster.get_items_of_cluster())
            
            print("-------------------------------------------------------------------------------")
            print(f"ID MEDOIDE: {medoids[i].get_id_depot()}")
            print(f"POSICIÓN DEL MEDOIDE: {pos_depot}")
            print(f"CLIENTES ASIGNADOS AL MEDOIDE: {list_id_customers}")
            print("-------------------------------------------------------------------------------")

            for customer_id in list_id_customers:
                pos_customer = Problem.get_problem().get_pos_element(customer_id)

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