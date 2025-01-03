from typing import List
from .fleet import Fleet
from .location import Location

class Depot:
    # Constructor para la clase Depot.
    def __init__(
        self, 
        id_depot: int, 
        location_depot: Location, 
        fleet_depot: List[Fleet]
    ):
        # Si no se pasa una ubicación, se inicializa con coordenadas por defecto
        if location_depot is None:
            location_depot = Location(0.0, 0.0)
        if fleet_depot is None:
            fleet_depot = []
        
        self.id_depot = id_depot
        self.location_depot = location_depot
        self.fleet_depot = fleet_depot
    
    def __init__(self):
        self.id_depot = None
        self.location_depot = None
        self.fleet_depot = None
    
    def get_id_depot(self) -> int:
        return self.id_depot

    def set_id_depot(self, value: int):
        self.id_depot = value

    def get_location_depot(self) -> Location:
        return self.location_depot

    def set_location_depot(self, value: Location):
        self.location_depot = value

    def get_fleet_depot(self) -> List[Fleet]:
        return self.fleet_depot

    def set_fleet_depot(self, value: List[Fleet]):
        self.fleet_depot = value