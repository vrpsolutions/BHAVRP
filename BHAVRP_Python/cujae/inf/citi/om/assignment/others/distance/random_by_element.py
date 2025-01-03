import random
from typing import List
from ...classical.by_not_urgency import ByNotUrgency
from ....problem.input.problem import Problem
from ....problem.solution.solution import Solution

class RandomByElement(ByNotUrgency):
    
    def __init__(self):
        super().__init__()
        
    def to_clustering(self) -> Solution:
        
        solution = Solution()
        list_clusters = self.initialize_cluster()
        
        list_id_depots = List(Problem.get_problem().get_list_id_depots())
        list_customers_to_assign = List(Problem.get_problem().get_customers())
         
        while list_customers_to_assign and list_clusters:
            
            # Selección aleatoria de un depósito y un cliente
            pos_rdm_depot = random.randint(0, len(list_id_depots) - 1)
            id_depot = list_id_depots[pos_rdm_depot]
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(
                Problem.get_problem().get_depot_by_id_depot(id_depot)
            )
            
            pos_rdm_customer = random.randint(0, len(list_customers_to_assign) - 1)
            customer = list_customers_to_assign[pos_rdm_customer]
            id_customer = customer.get_id_customer()
            request_customer = customer.get_request_customer()
            
            pos_cluster = self.find_cluster(id_depot, list_clusters)
            
            if pos_cluster != -1:
                request_cluster = list_clusters[pos_cluster].get_request_cluster()
                
                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer
                    list_clusters[pos_cluster].set_request_cluster(request_cluster)
                    list_clusters[pos_cluster].get_items_of_cluster().append(id_customer)
                    
                    list_customers_to_assign.pop(pos_rdm_customer)
                
                if self.is_full_depot(list_customers_to_assign, request_cluster, capacity_depot):
                    list_id_depots.pop(pos_rdm_customer)
                    
                    if list_clusters[pos_cluster].get_items_of_cluster():
                        solution.get_clusters().append(list_clusters.pop(pos_cluster))
                    else:
                        list_clusters.pop(pos_cluster)

        # Manejar clientes no asignados
        if list_customers_to_assign:
            for customer in list_customers_to_assign:
                solution.get_unassigned_items().append(customer.get_id_customer())
                    
        # Manejar clusters restantes
        if list_clusters:
            for cluster in list_clusters:
                if cluster.get_items_of_cluster():
                    solution.get_clusters().append(cluster)    
        
        return solution