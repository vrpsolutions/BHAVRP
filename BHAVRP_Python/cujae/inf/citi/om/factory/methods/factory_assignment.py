import sys
import importlib
from ..interfaces.assignment_type import AssignmentType
from ..interfaces.ifactory_assignment import IFactoryAssignment
from ...assignment.assignment import Assignment

# Clase que implementa el Patrón Factory Method para la carga dinámica de un determinado 
# método de asignación.
class FactoryAssignment(IFactoryAssignment):
    
    def create_assignment(self, assignment_type: AssignmentType) -> Assignment:
        assignment: Assignment = None
        
        try:
            class_path = str(assignment_type)
            module_name, class_name = class_path.rsplit(".", 1)
           
            module = importlib.import_module(module_name)

            assignment_class = getattr(module, class_name, None)
            
            if assignment_class:
                assignment = assignment_class()
            else:
                print(f"La clase '{class_name}' no se encuentra en el módulo '{module_name}'")

        except (ModuleNotFoundError, AttributeError) as e:
            print(f"Error: Class '{assignment_type}' not found: {e}")
        except Exception as e:
            print(f"Unexpected error: {e}")
            
        return assignment