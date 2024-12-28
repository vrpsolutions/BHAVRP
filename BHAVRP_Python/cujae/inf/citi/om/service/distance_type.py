from enum import Enum

class DistanceType(Enum):
    EUCLIDEAN = "Euclidean"
    MANHATTAN = "Manhattan"
    CHEBYSHEV = "Chebyshev"
    MIKOWSKI = "Minkowski"
    REAL = "Real"