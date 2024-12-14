from abc import ABC, abstractmethod
from interfaces.assignment_type import AssignmentType
from ...heuristic.assignment import Assignment

class FactoryAssignment:
    
    def create_assignment(self, assignment_type: AssignmentType) -> Assignment:
        assignment = None
        
        try:
            class_name = assignment_type.name
            assignment_class = globals().get(class_name)
            
            if assignment_class:
                assignment = assignment_class()
            else:
                raise ValueError(f"Class for {class_name} not found.")
            
        except Exception as e:
            print(f"Error ocurred: {e}")
            
        return assignment