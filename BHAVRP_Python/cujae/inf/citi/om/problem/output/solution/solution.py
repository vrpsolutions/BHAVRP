from typing import List
from cluster import Cluster

class Solution:

    """
    Constructor para la clase Solution.
    :param clusters: Lista de objetos Cluster.
    :param unassigned_items: Lista de índices o identificadores de elementos no asignados.
    """    
    def __init__(
        self, 
        clusters: List[Cluster] = None,
        unassigned_items: List[int] = None
    ):
        self.clusters = clusters if clusters is not None else []
        self.unassigned_items = unassigned_items if unassigned_items is not None else []
        
    def get_clusters(self) -> List[Cluster]:
        return self._clusters
    
    def set_clusters(self, value: List[Cluster]):
        self._clusters = value
        
    def get_unassigned_items(self) -> List[int]:
        return self._unassigned_items

    def set_unassigned_items(self, value: List[int]):
        self._unassigned_items = value
        
    # Verifica si existen elementos no asignados.
    def exist_unassigned_items(self) -> bool:
        return not self._unassigned_items
    
    # Devuelve la cantidad total de elementos no asignados.
    def get_total_unassigned_items(self) -> int:
        return len(self._unassigned_items)

    # Calcula el número total de elementos en todos los clusters.
    def elements_clustering(self) -> int:
        total_elements = sum(len(cluster.items_of_cluster) for cluster in self._clusters)
        return total_elements