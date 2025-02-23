import numpy as np
from typing import List
from abc import ABC, abstractmethod
from ....problem.input.customer import Customer

class IUrgencyWithMU(ABC):
    
    @abstractmethod
    def get_list_urgencies(
        self,
        list_customers: List[Customer],
        list_id_depots: List[List[int]],
        urgency_matrix: np.ndarray,
        mu_id_depot: int
    ) -> List[float]:
        pass
    
    @abstractmethod
    def get_urgency(
        self,
        id_customer: int,
        list_id_depots: List[int],
        urgency_matrix: np.ndarray,
        mu_id_depot: int
    ) -> float:
        pass