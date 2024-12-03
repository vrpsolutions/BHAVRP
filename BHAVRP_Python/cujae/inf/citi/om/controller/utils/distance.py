from scipy.spatial import distance as scipy_distance
from distance_type import DistanceType

class Distance:
    def __init__(self, distance_type: DistanceType):
        self.distance_type = distance_type

    def calculateDistance(self, x1, y1, x2, y2):
        if self.distance_type == DistanceType.EUCLIDEAN:
            return self.euclideanDistance(x1, y1, x2, y2)
        elif self.distance_type == DistanceType.MANHATTAN:
            return self.manhattanDistance(x1, y1, x2, y2)
        elif self.distance_type == DistanceType.REAL:
            return self.realDistance(x1, y1, x2, y2)
        else:
            raise ValueError("Tipo de distancia no soportado")

    def euclideanDistance(self, x1, y1, x2, y2):
        return scipy_distance.euclidean((x1, y1), (x2, y2))

    def manhattanDistance(self, x1, y1, x2, y2):
        return scipy_distance.cityblock((x1, y1), (x2, y2))

    def realDistance(self, x1, y1, x2, y2):
        return self.simulateOSRMCall(x1, y1, x2, y2)

    def simulateOSRMCall(self, x1, y1, x2, y2):
        print(f"Calculando distancia real entre ({x1}, {y1}) y ({x2}, {y2}) usando OSRM...")
        return 10.0

# Función que crea un objeto Distance según el tipo de distancia
def newDistance(distance_type: DistanceType):
    return Distance(distance_type)