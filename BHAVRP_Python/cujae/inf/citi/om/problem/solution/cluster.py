from typing import List
from dataclasses import dataclass, field

@dataclass
class Cluster:
    id_cluster: int = -1
    request_cluster: float = 0.0
    items_of_cluster: List[int] = field(default_factory=list)
    
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