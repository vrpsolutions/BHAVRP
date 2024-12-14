"""
Interfaz que define como crear un objeto Assignment
"""

from abc import ABC, abstractmethod
from assignment_type import AssignmentType
from ...heuristic.assignment.assignment import Assignment

class IFactoryAssignment(ABC):
    
    @abstractmethod
    def create_assignment(self, assignment_type: AssignmentType) -> Assignment:
        pass