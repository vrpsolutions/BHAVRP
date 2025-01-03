from .i_assignment import IAssignment
from ..problem.solution.solution import Solution

class AssignmentTemplate(IAssignment):
    
    def __init__(self):
        super().__init__()
    
    def initialize(self):
        pass
    
    def assign(self):
        pass
    
    def finish(self) -> Solution:
        return None