from typing import List
from .cluster import Cluster

class Solution:
    

    # Constructor para la clase Solution.
    def __init__(
        self, 
        clusters: List[Cluster] = None,
        unassigned_items: List[int] = None
    ):
        self.clusters = clusters if clusters is not None else []
        self.unassigned_items = unassigned_items if unassigned_items is not None else []
        
    def get_clusters(self) -> List[Cluster]:
        return self.clusters
    
    def set_clusters(self, value: List[Cluster]):
        self.clusters = value
        
    def get_unassigned_items(self) -> List[int]:
        return self.unassigned_items

    def set_unassigned_items(self, value: List[int]):
        self.unassigned_items = value
        
    # Verifica si existen elementos no asignados.
    def exist_unassigned_items(self) -> bool:
        return bool(self.unassigned_items)
    
    # Devuelve la cantidad total de elementos no asignados.
    def get_total_unassigned_items(self) -> int:
        return len(self.unassigned_items)

    # Calcula el número total de elementos en todos los clusters.
    def elements_clustering(self) -> int:
        total_elements = sum(len(cluster.items_of_cluster) for cluster in self.clusters)
        return total_elements