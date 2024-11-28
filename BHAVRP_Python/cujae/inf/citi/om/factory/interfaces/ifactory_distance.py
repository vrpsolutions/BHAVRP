"""
Interfaz que define como crear un objeto Distance
"""

from abc import ABC, abstractmethod
from distance_type import DistanceType

class IFactoryDistance(ABC):
    
    @abstractmethod
    def calculate_distance(self, distance_type: DistanceType) -> DistanceType:
        pass
    