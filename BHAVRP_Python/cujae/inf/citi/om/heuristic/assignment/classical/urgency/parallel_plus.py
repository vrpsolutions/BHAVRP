import numpy as np
from typing import List
from parallel import Parallel
from .....problem.input.problem import Problem
from .....problem.input.customer import Customer
from .....problem.output.solution.solution import Solution
from .....problem.output.solution.cluster import Cluster

class ParallelPlus(Parallel):
    
    def __init__(self):
        super().__init__()
        
    def to_clustering(self) -> Solution:
        solution = Solution()
        
        list_clusters: List[Cluster] = self.initialize_clusters()
        list_customers_to_assign: List[Customer] = list(Problem.get_problem().get_customers().copy())
        list_id_depots: List[int] = list(Problem.get_problem().get_list_id_depots())
        
        urgency_matrix: np.ndarray = np.array(Problem.get_problem().get_cost_matrix())
        closest_matrix: np.ndarray = np.array(Problem.get_problem().get_cost_matrix())
        
        list_depots_ordered: List[List[int]] = self.get_depots_ordered(
            list_customers_to_assign, list_id_depots, closest_matrix
        )
        list_urgencies: List[float] = self.get_list_urgencies(
            list_customers_to_assign, list_depots_ordered, urgency_matrix, -1
        )
        
        while list_customers_to_assign and list_clusters:
            pos_customer = self.get_pos_max_value(list_urgencies)
            customer = list_customers_to_assign[pos_customer]
            request_customer = customer.get_request_customer()

            id_closest_depot = list_depots_ordered[pos_customer][0]
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(id_closest_depot)
            )

            pos_cluster = self.find_cluster(id_closest_depot, list_clusters)

            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()

                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer

                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(customer.get_id_customer())

                    list_customers_to_assign.pop(pos_customer)
                    list_urgencies.pop(pos_customer)
                    list_depots_ordered.pop(pos_customer)

                customers_out_depot = self.get_customers_out_depot(
                    list_customers_to_assign, request_cluster, capacity_depot
                )

                if customers_out_depot:
                    if len(customers_out_depot) == len(list_customers_to_assign):
                        list_id_depots.remove(
                            Problem.get_problem().find_pos_element(list_id_depots, id_closest_depot))

                        if list_clusters[pos_cluster].get_items_of_cluster():
                            solution.get_clusters().append(list_clusters.pop(pos_cluster))
                        else:
                            list_clusters.pop(pos_cluster)
                            
                    for i in customers_out_depot:
                        current_pos_depot = Problem.get_problem().find_pos_element(
                            list_depots_ordered[i], id_closest_depot)

                        if current_pos_depot != -1:
                            list_depots_ordered[i].pop(current_pos_depot)

                            if not list_depots_ordered[i]:
                                solution.get_unassigned_items().append(list_customers_to_assign[i].get_id_customer())

                                list_customers_to_assign.pop(i)
                                list_urgencies.pop(i)
                                list_depots_ordered.pop(i)
                            else:
                                list_urgencies[i] = self.get_surgency(
                                    list_customers_to_assign[i].get_id_customer(),
                                    list_depots_ordered[i],
                                    urgency_matrix, -1)

        if list_customers_to_assign:
            for customer in list_customers_to_assign:
                solution.get_unassigned_items().append(customer.get_id_customer())

        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)

        return solution