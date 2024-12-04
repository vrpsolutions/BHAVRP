from typing import List

class Cluster:
    
    """
    Constructor para la clase Cluster.
    :param id_cluster: Identificador del cluster.
    :param request_cluster: Valor de la solicitud del cluster.
    :param items_of_cluster: Lista de elementos que pertenecen al cluster.
    """
    def __init__(
        self, 
        id_cluster: int = -1, 
        request_cluster: float = 0.0, 
        items_of_cluster: List[int] = None
    ):
        self._id_cluster = id_cluster
        self._request_cluster = request_cluster
        self._items_of_cluster = items_of_cluster if items_of_cluster is not None else []
        
    def get_id_cluster(self) -> int:
        return self.id_cluster
    
    def set_id_cluster(self, value: int):
        self.id_cluster = value
        
    def get_request_cluster(self) -> float:
        return self.request_cluster
    
    def set_request_cluster(self, value: float):
        self.request_cluster = value
        
    def get_items_of_cluster(self) -> List[int]:
        return self.items_of_cluster

    def set_items_of_cluster(self, value: List[int]):
        self.items_of_cluster = value
        
    # Limpia el cluster reiniciando la solicitud y vaciando la lista de elementos.       
    def clean_cluster(self):
        self.request_cluster = 0.0
        self.items_of_cluster.clear()