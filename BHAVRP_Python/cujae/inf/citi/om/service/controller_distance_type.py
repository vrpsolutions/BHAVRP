from scipy.spatial import distance
from distance_type import DistanceType
import osrm
import logging

class ControllerDistanceType:
    
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.logger.setLevel(logging.INFO)
        self.handler = logging.StreamHandler()
        self.handler.setFormatter(logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s'))
        self.logger.addHandler(self.handler)

    def test_distances(self):

        self.logger.info("Probando cálculos de distancias con scipy...")
        
        points = ([1, 2, 3], [4, 5, 6])
        
        try:
            valid_distances = {
                DistanceType.Euclidean: distance.euclidean,
                DistanceType.Manhattan: distance.cityblock,
                DistanceType.Chebyshev: distance.chebyshev,
                DistanceType.Minkowski: lambda p1, p2: distance.minkowski(p1, p2, 3),
            }

            for distance_type, dist_function in valid_distances.items():
                result = dist_function(*points)
                self.logger.info(f"Distancia {distance_type.name}: {result}")
            
        except Exception as e:
            self.logger.error(f"Error en el cálculo de distancias: {e}")
            
    def test_osrm(self):
        self.logger.info("Probando conexión con OSRM...")
        
        client = osrm.Client(host='http://router.project-osrm.org')
        
        coords = [
            (-82.422441, 23.089065),
            (-82.422442, 23.076812 )
        ]
        
        try:
            route = client.route(
                coordinates = coords, 
                overview = osrm.overview.full
            )
            
            distance_km = route['routes'][0]['distance']/1000
            duration_min = route['routes'][0]['duration']/60
            self.logger.info(f"Distancia calculada por OSRM: {distance_km:.2f} km")
            self.logger.info(f"Duración estimada: {duration_min:.2f} minutos")
            
        except Exception as e:
            self.logger.error(f"Error al conectar con OSRM: {e}")


if __name__ == "__main__":
    controller = ControllerDistanceType()
    #controller.test_distances()
    controller.test_osrm()