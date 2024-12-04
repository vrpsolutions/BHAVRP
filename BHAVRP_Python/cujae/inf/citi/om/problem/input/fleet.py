class Fleet:
    
    """
    Constructor para la clase Fleet.
    :param count_vehicles: Cantidad de vehículos en la flota.
    :param capacity_vehicle: Capacidad de cada vehículo en la flota.
    """
    def __init__(self, count_vehicles: int = 0, capacity_vehicle: float = 0.0):
        self.count_vehicles = count_vehicles
        self.capacity_vehicle = capacity_vehicle
    
    def get_count_vehicles(self) -> int:
        return self.count_vehicles

    def set_count_vehicles(self, value: int):
        self.count_vehicles = value

    def get_capacity_vehicle(self) -> float:
        return self.capacity_vehicle

    def set_capacity_vehicle(self, value: float):
        self.capacity_vehicle = value