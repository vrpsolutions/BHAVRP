from typing import List
from .heuristic import Heuristic
from ...problem.input.customer import Customer

class ByNotUrgency(Heuristic):
    
    def __init__(self):
        super().__init__()
    
    # Método que determina si existen clientes que puedan ser asignados al depósito a partir de su demanda.
    def is_full_depot(self, customers: List[Customer], request_cluster: float, capacity_depot: float) -> bool:
        is_full: bool = True
        current_request: float = capacity_depot - request_cluster
        
        if current_request > 0:
            for customer in customers:
                if customer.get_request_customer() <= current_request:
                    is_full = False
                    break
                
        return is_full