from enum import Enum

class DistanceType(Enum):
    EUCLIDEAN = "Euclidean"
    MANHATTAN = "Manhattan"
    CHEBYSHEV = "Chebyshev"
    HAVERSINE = "Haversine"
    MIKOWSKI = "Minkowski"
    REAL = "Real"