import numpy as np
from typing import List
from .hierarchical import Hierarchical
from ....service.osrm_service import OSRMService
from ....problem.input.problem import Problem
from ....problem.input.customer import Customer
from ....problem.input.depot import Depot
from ....problem.input.location import Location
from ....problem.output.solution import Solution
from ....problem.output.cluster import Cluster

class UPGMC(Hierarchical):
    
    def __init__(self):
        super().__init__()
        self.solution = Solution()
        self.list_id_elements: List[int]
        self.list_clusters: List[Cluster]
        self.list_customers_to_assign: List[Customer]
        self.list_depots: List[Depot]
            
    def to_clustering(self) -> Solution:
        self.initialize()
        self.assign()
        return self.finish()
        
    def initialize(self):
        self.list_id_elements = list(Problem.get_problem().get_list_id_elements())
        self.list_clusters = self.initialize_clusters()
        self.list_customers_to_assign = list(Problem.get_problem().get_customers())
        self.list_depots = list(Problem.get_problem().get_depots())

    def assign(self):
        change: bool = True
        total_depots = len(self.list_depots)

        while any(self.list_customers_to_assign) and len(self.list_depots) > 0:
            current_customers = len(self.list_customers_to_assign)
            current_depots = len(self.list_depots)
            
            print(f"Current Customers: {current_customers}")
            print(f"Current Depots: {current_depots}")
            
            if change:
                cost_matrix: np.ndarray = self.initialize_cost_matrix(self.list_customers_to_assign, self.list_depots, self.distance_type)
        
            min_value = np.min(cost_matrix) 
            row_best, col_best = np.where(cost_matrix == min_value)
            row_best, col_best = row_best[0], col_best[0]
            
            print(f"Minimun Value: {min_value}")
            print(f"Row Best: {row_best}")
            print(f"Col Best: {col_best}")
            
            if col_best < current_customers and (row_best + current_customers) < current_customers:
                id_customer_one = self.list_customers_to_assign[col_best].get_id_customer()
                id_customer_two = self.list_customers_to_assign[row_best].get_id_customer()

                print(f"ID Customer One: {id_customer_one}")
                print(f"ID Customer Two: {id_customer_two}")
                
                pos_cluster_one = self.find_cluster(id_customer_one, self.list_clusters)
                pos_cluster_two = self.find_cluster(id_customer_two, self.list_clusters)
                
                print(f"Position Cluster One: {pos_cluster_one}")
                print(f"Position Cluster Two: {pos_cluster_two}")
                
                if pos_cluster_one != -1 and pos_cluster_two != -1:
                    request_cluster_one = self.list_clusters[pos_cluster_one].get_request_cluster()
                    request_cluster_two = self.list_clusters[pos_cluster_two].get_request_cluster()
                    total_request = request_cluster_one + request_cluster_two
                    
                    print(f"Request Cluster One: {request_cluster_one}")
                    print(f"Request Cluster Two: {request_cluster_two}")
                    print(f"Total Request: {total_request}")

                    id_depot_with_mu = self.get_id_cluster_with_mu(self.list_depots, self.list_clusters)
                    pos_depot = Problem.get_problem().find_pos_depot(self.list_depots, id_depot_with_mu)
                    capacity_depot_with_mu = Problem.get_problem().get_total_capacity_by_depot(self.list_depots[pos_depot])
                    
                    pos_cluster = self.find_cluster(id_depot_with_mu, self.list_clusters)
                    request_cluster = self.list_clusters[pos_cluster].get_request_cluster()
                    
                    print(f"ID Depot with MU: {id_depot_with_mu}")
                    print(f"Position Depot: {pos_depot}")
                    print(f"Capacity Depot with MU: {capacity_depot_with_mu}")
                    print(f"Position Cluster: {pos_cluster}")
                    print(f"Request Cluster: {request_cluster}")

                    if capacity_depot_with_mu >= (request_cluster + total_request):

                        self.list_clusters[pos_cluster_one].set_request_cluster(total_request)
                        self.list_clusters[pos_cluster_one].get_items_of_cluster().extend(self.list_clusters[pos_cluster_two].get_items_of_cluster())
                        
                        new_location: Location = self.recalculate_centroid(self.list_clusters[pos_cluster_one])
                        
                        print(f"New Location X: {new_location.get_axis_x()}")
                        print(f"New Location Y: {new_location.get_axis_y()}")

                        pos_customer_one = Problem.get_problem().find_pos_customer(self.list_customers_to_assign, id_customer_one)
                        self.list_customers_to_assign[pos_customer_one].set_location_customer(new_location)
                        
                        print(f"New Location X: {new_location.get_axis_x()}")
                        print(f"New Location Y: {new_location.get_axis_y()}")
                        print(f"Position Customer One: {pos_customer_one}")                        

                        self.list_clusters.pop(pos_cluster_two)
                        pos_customer_two = Problem.get_problem().find_pos_customer(self.list_customers_to_assign, id_customer_two)
                        self.list_customers_to_assign.pop(pos_customer_two)
                        
                        print(f"List Clusters: {len(self.list_clusters)}")
                        print(f"Position Customer Two: {pos_customer_two}")
                        print(f"List Customers to Assign: {len(self.list_customers_to_assign)}")
                        
                        change = True
                    else:
                        cost_matrix[row_best, col_best] = float("inf")
                        change = False

            elif ((col_best < current_customers <= (row_best + current_customers)) or ((row_best + current_customers) < current_customers <= col_best)):
                if col_best < current_customers:
                    pos_customer_one, pos_depot_matrix = col_best, row_best
                else:
                    pos_customer_one, pos_depot_matrix = row_best, col_best
                    
                print("--------------------------------------")
                print(f"Position Customer One: {pos_customer_one}")
                print(f"Position Depot Matrix: {pos_depot_matrix}")

                id_customer_one = self.list_customers_to_assign[pos_customer_one].get_id_customer()
                pos_cluster_one = self.find_cluster(id_customer_one, self.list_clusters)
                
                print("--------------------------------------")
                print(f"ID Customer One: {id_customer_one}")
                print(f"Position Cluster One: {pos_cluster_one}")

                id_depot = self.list_depots[pos_depot_matrix].get_id_depot()
                capacity_depot = Problem.get_problem().get_total_capacity_by_depot(self.list_depots[pos_depot_matrix])
                pos_cluster = self.find_cluster(id_depot, self.list_clusters)
                
                print("--------------------------------------")
                print(f"Position Depot: {pos_depot_matrix}")
                print(f"ID Depot: {id_depot}")
                print(f"Capacity Depot: {capacity_depot}")
                print(f"Position Cluster: {pos_cluster}")

                if pos_cluster_one != -1 and pos_cluster != -1:
                    request_cluster_one = self.list_clusters[pos_cluster_one].get_request_cluster()
                    request_cluster = self.list_clusters[pos_cluster].get_request_cluster()
                    
                    print("--------------------------------------")
                    print(f"Request Cluster One: {request_cluster_one}")
                    print(f"Request Cluster: {request_cluster}")
                    print("--------------------------------------")

                    if capacity_depot >= request_cluster + request_cluster_one:
                        capacity_depot -= request_cluster + request_cluster_one
                        self.list_clusters[pos_cluster].set_request_cluster(request_cluster + request_cluster_one)
                        self.list_clusters[pos_cluster].get_items_of_cluster().extend(self.list_clusters[pos_cluster_one].get_items_of_cluster())
                        self.list_customers_to_assign.pop(Problem.get_problem().find_pos_customer(self.list_customers_to_assign, id_customer_one))
                        self.list_clusters.pop(pos_cluster_one)
                        change = True
                    else:
                        cost_matrix[row_best, col_best] = float("inf")
                        change = False

                # Verificar si el depósito está lleno
                if self.is_full_depot(self.list_clusters, len(self.list_customers_to_assign), request_cluster, capacity_depot):
                    pos_cluster = self.find_cluster(id_depot, self.list_clusters)
                    print(f"Position Cluster: {pos_cluster}")
                    
                    if self.list_clusters[pos_cluster].get_items_of_cluster():
                        self.solution.get_clusters().append(self.list_clusters.pop(pos_cluster))
                    else:
                        self.list_clusters.pop(pos_cluster)

                    self.list_depots.pop(pos_depot_matrix)
                    total_depots -= 1
                    change = True
        
    def finish(self) -> Solution:       
        pos_element = -1
        i = 0
        while i < len(self.list_clusters):
            clusters = self.list_clusters
            cluster = clusters[i]
            
            print("\n")
            print(f"ID Cluster: {cluster.get_id_cluster()}")
            print(f"Position Cluster: {cluster.get_items_of_cluster()}")
            print(f"Request Cluster: {cluster.get_request_cluster()}")

            pos_element = Problem.get_problem().find_pos_customer(Problem.get_problem().get_customers(), cluster.get_id_cluster())

            if pos_element != -1:
                for item in cluster.get_items_of_cluster():
                    self.solution.get_unassigned_items().append(int(item))
                clusters.remove(cluster)
            else:
                i += 1
         
        if self.list_clusters:
            for cluster in self.list_clusters:
                if cluster.get_items_of_cluster():
                    self.solution.get_clusters().append(cluster) 

        OSRMService.clear_distance_cache()
        
        return self.solution
    
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