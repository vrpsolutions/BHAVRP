from .distance_type import DistanceType
from scipy.spatial import distance as scipy_distance
import osrm

class Distance:
    def __init__(self, distance_type: DistanceType):
        self.distance_type = distance_type

    def calculate_distance(self, x1, y1, x2, y2):
        if self.distance_type == DistanceType.Euclidean:
            return self.euclidean_distance(x1, y1, x2, y2)
        elif self.distance_type == DistanceType.Manhattan:
            return self.manhattan_distance(x1, y1, x2, y2)
        elif self.distance_type == DistanceType.Chebyshev:
            return self.chebyshev_distance(x1, y1, x2, y2)
        elif self.distance_type == DistanceType.Minkowski:
            return self.minkowski_distance(x1, y1, x2, y2)
        elif self.distance_type == DistanceType.Real:
            return self.real_distance(x1, y1, x2, y2)
        else:
            raise ValueError("Tipo de distancia no soportado")

    def euclidean_distance(self, x1, y1, x2, y2):
        return scipy_distance.euclidean((x1, y1), (x2, y2))

    def manhattan_distance(self, x1, y1, x2, y2):
        return scipy_distance.cityblock((x1, y1), (x2, y2))
    
    def chebyshev_distance(self, x1, y1, x2, y2):
        return scipy_distance.chebyshev((x1, y1), (x2, y2))

    def minkowski_distance(self, x1, y1, x2, y2, p=3):
        # Calcula la distancia de Minkowski con un valor predeterminado de p=3.
        return scipy_distance.minkowski((x1, y1), (x2, y2), p)

    def real_distance(self, x1, y1, x2, y2):
        return self.simulateOSRMCall(x1, y1, x2, y2)
 
    # Calcula la distancia real entre dos puntos utilizando OSRM.
    def simulate_OSRM_call(self, x1, y1, x2, y2):
        
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

# Función que crea un objeto Distance según el tipo de distancia
def new_distance(distance_type: DistanceType):
    return Distance(distance_type)