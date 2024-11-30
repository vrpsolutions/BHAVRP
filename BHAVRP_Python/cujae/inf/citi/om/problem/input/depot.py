from location import Location

class Depot:
    
    """
    Constructor para la clase Depot.
    :param id_depot: ID del depósito.
    :param location_depot: Ubicación del depósito (por defecto es None, se inicializa más tarde).
    :param fleet_depot: Flota del depósito (por defecto es una lista vacía).
    """
    def __init__(self, id_depot, location_depot=None, fleet_depot=None):
        if location_depot is None:
            location_depot = Location(0.0, 0.0)
        if fleet_depot is None:
            fleet_depot = []
        
        self.id_depot = id_depot
        self.location_depot = location_depot
        self.fleet_depot = fleet_depot
    
    @property
    def id_depot(self):
        return self._id_depot

    @id_depot.setter
    def id_depot(self, value):
        self._id_depot = value

    @property
    def location_depot(self):
        return self._location_depot

    @location_depot.setter
    def location_depot(self, value):
        self._location_depot = value

    @property
    def fleet_depot(self):
        return self._fleet_depot

    @fleet_depot.setter
    def fleet_depot(self, value):
        self._fleet_depot = value