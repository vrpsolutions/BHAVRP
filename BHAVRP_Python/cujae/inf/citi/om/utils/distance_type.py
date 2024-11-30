from enum import Enum

class DistanceType(Enum):
    Euclidean = "euclidean"
    Manhattan = "manhattan"
    Chebyshev = "chebyshev"
    Minkowski = "minkowski"
    Real = "real"
