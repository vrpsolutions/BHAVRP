from abc import ABC, abstractmethod
from ..problem.solution.solution import Solution

class IAssignment(ABC):
    
    @abstractmethod
    def to_clustering(self) -> Solution:
        pass