from abc import ABC, abstractmethod
from ..problem.output.solution import Solution

class IAssignment(ABC):
    
    @abstractmethod
    def to_clustering(self) -> Solution:
        pass