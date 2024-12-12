import numpy as np
from typing import List
from hierarchical import Hierarchical
from .....controller.utils.distance_type import DistanceType
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.input.depot import Depot
from .....problem.input.location import Location
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

class UPGMC(Hierarchical):
    
    def __init__(self):
        super().__init__()
        
    distance_type = DistanceType.Euclidean;
    
    def to_clustering(self) -> Solution:
        solution = Solution()
        
        # Inicializar listas.
        list_id_elements: List[int] = list(Problem.get_problem().get_list_id_elements())
        list_clusters: List[Cluster] = self.initialize_clusters(list_id_elements)
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers())
        list_depots: List[Depot] = list(Problem.get_problem().get_depots())
        
        total_depots = len(list_depots)
        change = True
        
        # Inicializar matriz de costos.
        cost_matrix: np.ndarray = None
        
        while list_customers_to_assign and total_depots > 0:
            current_depots = len(list_depots)
            current_customers = len(list_customers_to_assign)
            
            if change:
                cost_matrix = Problem.get_problem().fill_cost_matrix(
                    list_customers_to_assign, list_depots, UPGMC.distance_type
                )
                
            # Encontrar la mejor fila y columna.
            row_col_best_all = np.argmin(
                cost_matrix[0:current_customers + current_depots, 0:current_customers]
            )
            pos_row, pos_col = np.unravel_index(row_col_best_all, cost_matrix.shape)
            
            if pos_col < current_customers and pos_row < current_customers:
                id_customer_one = list_customers_to_assign[pos_col].get_id_customer()
                id_customer_two = list_customers_to_assign[pos_row].get_id_customer()

                print(f"ID Customer One: {id_customer_one}")
                print(f"ID Customer Two: {id_customer_two}")
                
                pos_cluster_one = self.find_cluster(id_customer_one, list_clusters)
                pos_cluster_two = self.find_cluster(id_customer_two, list_clusters)
                
                print(f"Position Cluster One: {pos_cluster_one}")
                print(f"Position Cluster Two: {pos_cluster_two}")
                
                if pos_cluster_one != -1 and pos_cluster_two != -1:
                    request_cluster_one = list_clusters[pos_cluster_one].get_request_cluster()
                    request_cluster_two = list_clusters[pos_cluster_two].get_request_cluster()
                    total_request = request_cluster_one + request_cluster_two
                    
                    print(f"Request Cluster One: {request_cluster_one}")
                    print(f"Request Cluster Two: {request_cluster_two}")
                    print(f"Total Request: {total_request}")

                    id_depot_with_mu = self.get_id_cluster_with_mu(list_depots, list_clusters)
                    pos_depot = Problem.get_problem().find_pos_depot(list_depots, id_depot_with_mu)
                    capacity_depot_with_mu = Problem.get_problem().get_total_capacity_by_depot(
                        list_depots[pos_depot]
                    )
                    pos_cluster = self.find_cluster(id_depot_with_mu, list_clusters)
                    request_cluster = list_clusters[pos_cluster].get_request_cluster()
                    
                    print(f"ID Depot with MU: {id_depot_with_mu}")
                    print(f"Position Depot: {pos_depot}")
                    print(f"Capacity Depot with MU: {capacity_depot_with_mu}")
                    print(f"Position Cluster: {pos_cluster}")
                    print(f"Request Cluster: {request_cluster}")

                    if capacity_depot_with_mu >= (request_cluster + total_request):

                        list_clusters[pos_cluster_one].set_request_cluster(total_request)
                        list_clusters[pos_cluster_one].get_items_of_cluster().extend(
                            list_clusters[pos_cluster_two].get_items_of_cluster()
                        )
                        
                        new_location: Location = self.recalculate_centroid(list_clusters[pos_cluster_one])
                        
                        print(f"New Location X: {new_location.get_axis_x()}")
                        print(f"New Location Y: {new_location.get_axis_y()}")

                        pos_customer_one = Problem.get_problem().find_pos_customer(
                            list_customers_to_assign, id_customer_one
                        )
                        list_customers_to_assign[pos_customer_one].set_location_customer(new_location)
                        
                        print(f"New Location X: {new_location.get_axis_x()}")
                        print(f"New Location Y: {new_location.get_axis_y()}")
                        print(f"Position Customer One: {pos_customer_one}")                        

                        list_clusters.pop(pos_cluster_two)
                        pos_customer_two = Problem.get_problem().find_pos_customer(
                                list_customers_to_assign, id_customer_two
                        )
                        list_customers_to_assign.pop(pos_customer_two)
                        
                        print(f"List Clusters: {len(list_clusters)}")
                        print(f"Position Customer Two: {pos_customer_two}")
                        print(f"List Customers to Assign: {len(list_customers_to_assign)}")
                        
                        change = True
                    else:
                        cost_matrix[pos_row, pos_col] = float("inf")
                        change = False

            elif (
                (pos_col < current_customers <= pos_row)
                or (pos_row < current_customers <= pos_col)
            ):
                if pos_col < current_customers:
                    pos_customer_one, pos_depot_matrix = pos_col, pos_row
                else:
                    pos_customer_one, pos_depot_matrix = pos_row, pos_col
                    
                print(f"Position Customer One: {pos_customer_one}")
                print(f"Position Depot Matrix: {pos_depot_matrix}")

                id_customer_one = list_customers_to_assign[pos_customer_one].get_id_customer()
                pos_cluster_one = self.find_cluster(id_customer_one, list_clusters)

                pos_depot = pos_depot_matrix - current_customers
                id_depot = list_depots[pos_depot].get_id_depot()
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                    list_depots[pos_depot]
                )
                pos_cluster = self.find_cluster(id_depot, list_clusters)
                
                print(f"Position Depot: {pos_depot}")
                print(f"ID Depot: {id_depot}")
                print(f"Capacity Depot: {capacity_depot}")
                print(f"Position Cluster: {pos_cluster}")

                if pos_cluster_one != -1 and pos_cluster != -1:
                    request_cluster_one = list_clusters[pos_cluster_one].get_request_cluster()
                    request_cluster = list_clusters[pos_cluster].get_request_cluster()
                    
                    print(f"Request Cluster One: {request_cluster_one}")
                    print(f"Request Cluster: {request_cluster}")

                    if capacity_depot >= request_cluster + request_cluster_one:
                        list_clusters[pos_cluster].set_request_cluster(
                            request_cluster + request_cluster_one
                        )
                        list_clusters[pos_cluster].get_items_of_cluster().extend(
                            list_clusters[pos_cluster_one].get_items_of_cluster()
                        )
                        list_customers_to_assign.pop(
                            Problem.get_problem().find_pos_customer(
                                list_customers_to_assign, id_customer_one
                            )
                        )
                        list_clusters.pop(pos_cluster_one)
                        change = True
                    else:
                        cost_matrix[pos_row, pos_col] = float("inf")
                        change = False

                # Verificar si el depósito está lleno
                if self.is_full_depot(
                    list_clusters, request_cluster, capacity_depot, len(list_customers_to_assign)
                ):
                    pos_cluster = self.find_cluster(id_depot, list_clusters)
                    print(f"Position Cluster: {pos_cluster}")
                    
                    if list_clusters[pos_cluster].get_items_of_cluster():
                        solution.get_clusters().append(list_clusters.pop(pos_cluster))
                    else:
                        list_clusters.pop(pos_cluster)

                    list_depots.pop(pos_depot)
                    total_depots -= 1
                    change = True
                    
        self.finish(list_clusters, solution)
        
        # Verificamos si list_clusters no está vacía.
        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.clusters.append(cluster)
        
        return solution
    
    # Método encargado de obtener el id del depósito con mayor capacidad de la lista.
    def get_id_cluster_with_mu(self, depots: List[Depot], clusters: List[Cluster]) -> int:
        id_depot_mu = depots[0].get_id_depot()
        pos_cluster = self.find_cluster(id_depot_mu, clusters)
        request_cluster = clusters[pos_cluster].get_request_cluster()
        max_capacity_depot = Problem.get_problem().get_total_capacity_by_depot(depots[0])
        max_capacity_depot -= request_cluster
        total_depots = len(depots)

        current_capacity_depot = 0

        for i in range(1, total_depots):
            pos_cluster = self.find_cluster(depots[i].get_id_depot(), clusters)
            request_cluster = clusters[pos_cluster].get_request_cluster()
            current_capacity_depot = Problem.get_problem().get_total_capacity_by_depot(depots[i])
            current_capacity_depot -= request_cluster

            if max_capacity_depot < current_capacity_depot:
                max_capacity_depot = current_capacity_depot
                id_depot_mu = depots[i].get_id_depot()

        return id_depot_mu
    
    # Verifica si el depósito está lleno.
    def is_full_depot(
        self, 
        clusters: List[Cluster], 
        request_cluster: float, 
        capacity_depot: float, 
        current_customer: int
    ) -> bool:
        is_full = True

        current_request = capacity_depot - request_cluster

        if current_request > 0:
            i = 0

            while i < len(clusters) and i < current_customer and is_full:
                if clusters[i].get_request_cluster() <= current_request:
                    is_full = False
                else:
                    i += 1

        return is_full
    
    # Finaliza el proceso y agrega elementos no asignados.
    def finish(self, clusters: List[Cluster], solution: Solution):
        pos_element = -1

        i = 0
        while i < len(clusters):
            
            print("\n")
            print(f"ID Cluster: {clusters[i].get_id_cluster()}")
            print(f"Position Cluster: {clusters[i].get_items_of_cluster()}")
            print(f"Request Cluster: {clusters[i].get_request_cluster()}")

            pos_element = Problem.get_problem().find_pos_customer(Problem.get_problem().get_customers(), clusters[i].get_id_cluster())

            if pos_element != -1:
                for item in clusters[i].get_items_of_cluster():
                    solution.get_unassigned_items().append(int(item))

                del clusters[i]  # Remover el cluster

            else:
                i += 1