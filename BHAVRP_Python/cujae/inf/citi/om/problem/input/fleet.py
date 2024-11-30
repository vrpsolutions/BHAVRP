class Fleet:
    
    """
    Constructor para la clase Fleet.
    :param count_vehicles: Cantidad de vehículos en la flota.
    :param capacity_vehicle: Capacidad de cada vehículo en la flota.
    """
    def __init__(self, count_vehicles=0, capacity_vehicle=0.0):
        self.count_vehicles = count_vehicles
        self.capacity_vehicle = capacity_vehicle
    
    @property
    def count_vehicles(self):
        return self._count_vehicles

    @count_vehicles.setter
    def count_vehicles(self, value):
        self._count_vehicles = value

    @property
    def capacity_vehicle(self):
        return self._capacity_vehicle

    @capacity_vehicle.setter
    def capacity_vehicle(self, value):
        self._capacity_vehicle = value