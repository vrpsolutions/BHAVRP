from typing import List
from ..assignment import Assignment
from ...problem.input.problem import Problem
from ...problem.output.cluster import Cluster

class Heuristic(Assignment):
    
    def __init__(self):
        super().__init__()
    
    # Método para inicializar una lista de clusters.
    def initialize_clusters(self) -> List[Cluster]:
        list_clusters: List[Cluster] = []
        total_clusters = len(Problem.get_problem().get_depots())
        list_id_items: List[int] = []
        
        for i in range(total_clusters):
            cluster = Cluster(id_cluster=Problem.get_problem().get_list_id_depots()[i], request_cluster=0.0, items_of_cluster=[])
            list_clusters.append(cluster)
        
        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        
        for cluster in list_clusters:
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")

        return list_clusters