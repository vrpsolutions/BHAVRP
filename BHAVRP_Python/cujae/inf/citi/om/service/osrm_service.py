import requests
import json
from math import floor

class OSRMService:
    OSRM_URL = "https://router.project-osrm.org/"
    OSRM_LOCAL_URL = "http://localhost:5000/route/v1/driving/"
    distance_cache = {}
    remote_error_count = 0
    MAX_REMOTE_ERRORS = 3
    
    # Método para obtener la distancia entre dos puntos utilizando OSRM API.
    @staticmethod
    def calculate_distance(
        axis_x_ini: float, 
        axis_y_ini: float, 
        axis_x_end: float, 
        axis_y_end: float
    ) -> float:
        
        key = f"{axis_x_ini},{axis_y_ini}->{axis_x_end},{axis_y_end}"
         
        if key in OSRMService.distance_cache:
            return OSRMService.distance_cache[key]
         
        remote_url = f"{OSRMService.OSRM_URL}{axis_y_ini},{axis_x_ini};{axis_y_end},{axis_x_end}?overview=false&alternatives=false"
        local_url = f"{OSRMService.OSRM_LOCAL_URL}{axis_y_ini},{axis_x_ini};{axis_y_end},{axis_x_end}?overview=false&alternatives=false"
        
        if OSRMService.remote_error_count < OSRMService.MAX_REMOTE_ERRORS:
            try:
                return OSRMService.fetch_distance_from_server(remote_url, key)
            except Exception as e:
                print(f"Remote server failed. Switching to local server. Reason: {e}")
                
                OSRMService.remote_error_count += 1
                
                if OSRMService.remote_error_count >= OSRMService.MAX_REMOTE_ERRORS:
                    print("Max error attempts reached. Switching to local server only.")
        else:
            try:
                return OSRMService.fetch_distance_from_server(local_url, key)
            except Exception as e:
                raise Exception("Local servers failed to calculate distance.") from e
    
    # Método auxiliar para ejecutar la solicitud al servidor y obtener la distancia.
    @staticmethod
    def fetch_distance_from_server(url: str, key: str) -> float:
        response = requests.get(url)
        
        if response.status_code != 200:
            raise Exception(f"OSRM API returned status code: {response.status_code} for URL: {url}")

        distance = OSRMService.parse_distance_from_response(response.text)
        OSRMService.distance_cache[key] = distance
        
        return distance
    
    # Método auxiliar para parsear la distancia desde la respuesta de OSRM.
    @staticmethod
    def parse_distance_from_response(response_body):
        json_response = json.loads(response_body)
        
        if "routes" not in json_response or len(json_response["routes"]) == 0:
            raise Exception(f"No routes found in OSRM API response: {response_body}")

        first_route = json_response["routes"][0]
        if "distance" not in first_route:
            raise Exception(f"Distance not found in the first route of OSRM API response: {response_body}")

        return floor((first_route["distance"] * 0.001) * 100) / 100.0  # Convertir a km, redondear a 2 decimales

    # Método para limpiar la caché de distancias.
    @staticmethod
    def clear_distance_cache():
        print("---------------------------------------------")
        print(f"CLEARING DISTANCE CACHE WITH: {len(OSRMService.distance_cache)} ENTRIES")
        
        OSRMService.distance_cache.clear()