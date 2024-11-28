"""
Enumerado que indica los tipos de distancia
"""

from enum import Enum

class DistanceType(Enum):
    Chebyshev = 1
    Euclidean = 2
    Haversine = 3
    Manhattan = 4
    Real = 5
