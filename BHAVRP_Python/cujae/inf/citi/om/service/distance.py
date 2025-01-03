from .distance_type import DistanceType
from scipy.spatial import distance as scipy_distance
import osrm

class Distance:

    @staticmethod
    def calculate_distance(x1: float, y1: float, x2: float, y2: float, distance_type: DistanceType) -> float:
        if distance_type == DistanceType.EUCLIDEAN:
            return Distance.euclidean_distance(x1, y1, x2, y2)
        elif distance_type == DistanceType.MANHATTAN:
            return Distance.manhattan_distance(x1, y1, x2, y2)
        elif distance_type == DistanceType.CHEBYSHEV:
            return Distance.chebyshev_distance(x1, y1, x2, y2)
        elif distance_type == DistanceType.MIKOWSKI:
            return Distance.minkowski_distance(x1, y1, x2, y2)
        elif distance_type == DistanceType.REAL:
            return Distance.real_distance(x1, y1, x2, y2)
        else:
            raise ValueError("Tipo de distancia no soportado")

    @staticmethod
    def euclidean_distance(x1: float, y1: float, x2: float, y2: float) -> float:
        return scipy_distance.euclidean((x1, y1), (x2, y2))

    @staticmethod
    def manhattan_distance(x1: float, y1: float, x2: float, y2: float) -> float:
        return scipy_distance.cityblock((x1, y1), (x2, y2))
    
    @staticmethod
    def chebyshev_distance(x1: float, y1: float, x2: float, y2: float) -> float:
        return scipy_distance.chebyshev((x1, y1), (x2, y2))

    @staticmethod
    def minkowski_distance(x1: float, y1: float, x2: float, y2: float, p: int = 3) -> float:
        return scipy_distance.minkowski((x1, y1), (x2, y2), p)

    @staticmethod
    def real_distance(x1: float, y1: float, x2: float, y2: float) -> float:
        return Distance.simulateOSRMCall(x1, y1, x2, y2)
 
    # Calcula la distancia real entre dos puntos utilizando OSRM.
    @staticmethod
    def simulate_OSRM_call(x1: float, y1: float, x2: float, y2: float) -> float:
        
        print(f"Calculando distancia real entre ({x1}, {y1}) y ({x2}, {y2}) usando OSRM...")
        try:
            # Configuración para realizar la consulta
            # El host cambia en dependencia de si se usa local o remotamente
            client = osrm.Client(host="http://router.project-osrm.org")
            #client = osrm.Client(host = "http://127.0.0.1:5000") 
            
            # Coordenadas de entrada
            coords = [(x1, y1), (x2, y2)]
            
            response = client.route(
                coordinates = coords,
                overview = osrm.overview.false # Respuesta optimizada sin detalles de ruta
            )
            
            # Extraer la distancia del resultado
            distance_meters = response['routes'][0]['distance'] # Distancia en metros
            distance = distance_meters / 1000.0 # Convertir a kilómetros
            print(f"Distancia calculada: {distance:.2f} km")
            
            return distance
        except Exception as e:
            print(f"Error al calcular la distancia real: {e}")
            return float('inf') # Retornar infinito en caso de error