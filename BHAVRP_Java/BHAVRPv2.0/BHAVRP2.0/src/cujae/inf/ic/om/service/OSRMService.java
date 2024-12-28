package cujae.inf.ic.om.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OSRMService {

	private static final CloseableHttpClient httpClient = HttpClients.createDefault();
	//private static final String OSRM_URL = "https://router.project-osrm.org/";
	private static final String OSRM_Local_URL = "http://localhost:5000/route/v1/driving/";
	private static final Map<String, Double> distanceCache = new HashMap<>();

	public OSRMService() {
		super();
	}

	/**
	 * Método para obtener la distancia entre dos puntos utilizando OSRM API
	 *
	 * @param axisXIni Coordenada X del punto inicial
	 * @param axisYIni Coordenada Y del punto inicial
	 * @param axisXEnd Coordenada X del punto final
	 * @param axisYEnd Coordenada Y del punto final
	 * @return Distancia en metros
	 * @throws IOException En caso de error en la comunicación con OSRM
	 */
	public static double calculateDistance(double axisXIni, double axisYIni, double axisXEnd, double axisYEnd) throws DistanceCalculationException, InterruptedException {
		// Generar clave única para la caché
        String key = axisXIni + "," + axisYIni + "->" + axisXEnd + "," + axisYEnd;

        // Verificar si la distancia ya está en la caché
        if (distanceCache.containsKey(key)) {
            //System.out.println("Cache hit for: " + key);
            return distanceCache.get(key);
        } else {
            //System.out.println("Cache miss for: " + key);
        }
		
		String url = OSRM_Local_URL + axisYIni + "," + axisXIni + ";" + axisYEnd + "," + axisXEnd + "?overview=false&alternatives=false";
		//String url = OSRM_URL + axisYIni + "," + axisXIni + ";" + axisYEnd + "," + axisXEnd + "?overview=false&alternatives=false";

		// Validar la url generada
		//System.out.println("Request URL: " + url);

		HttpGet request = new HttpGet(url);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != 200) {
				throw new IOException("OSRM API returned status code: " + statusCode + " for URL: " + url);
			}

			String responseBody = EntityUtils.toString(response.getEntity());
			double distance = parseDistanceFromResponse(responseBody);

			// Pausa para evitar rate limiting
			//Thread.sleep(1); //Farthest-First demora 0.2 mins en ejecutarse correctamente 1 vez
			//Thread.sleep(2); //Farthest-First demora 0.3 mins en ejecutarse correctamente 1 vez
			//Thread.sleep(5); //Farthest-First demora 0.6 mins en ejecutarse correctamente 1 vez
			//Thread.sleep(10) //Farthest-First demora 1.3 mins en ejecutarse correctamente 1 vez
			//Thread.sleep(100); //Farthest-First demora 9.3 mins en ejecutarse correctamente 1 vez
			//Thread.sleep(1000);
			//System.out.println("DISTANCE BETWEEN " + axisYIni + ";" + axisXIni + " and " + axisYEnd + ";" + axisXEnd + ": " + distance + " meters");
			//System.out.println("DISTANCE BETWEEN " + axisYIni + ";" + axisXIni + " and " + axisYEnd + ";" + axisXEnd + ": " + distance + " km");

			// Guardar en caché la distancia calculada
            distanceCache.put(key, distance);
			
			return distance;
		} catch (IOException e) {
	        throw new DistanceCalculationException("I/O error during OSRM API call for URL: " + url, e);
		} catch (Exception e) {
	        throw new DistanceCalculationException("Unexpected error occurred during OSRM API call.", e);
	    }
	}

	/**
	 * Método auxiliar para parsear la distancia desde la respuesta de OSRM
	 *
	 * @param responseBody Respuesta JSON de OSRM
	 * @return Distancia en metros
	 */
	private static double parseDistanceFromResponse(String responseBody) {
		// Parsear la respuesta JSON
		JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

		// Validar la estructura del JSON
		if (!jsonResponse.has("routes") || jsonResponse.getAsJsonArray("routes").size() == 0) {
			throw new IllegalStateException("No routes found in OSRM API response: " + responseBody);
		}

		// Extraer la distancia
		JsonArray routes = jsonResponse.getAsJsonArray("routes");
		JsonObject firstRoute = routes.get(0).getAsJsonObject();

		// Verificar si la distancia está presente
		if (!firstRoute.has("distance")) {
			throw new IllegalStateException("Distance not found in the first route of OSRM API response: " + responseBody);
		}

		return Math.floor((firstRoute.get("distance").getAsDouble() * 0.001) * 100) / 100.0;
	}

	/**
	 * Método para cerrar el cliente HTTP al finalizar la aplicación
	 */
	public static void closeHttpClient() {
		try {
			httpClient.close();
		} catch (IOException e) {
			System.err.println("Error closing HTTP client.");
			e.printStackTrace();
		}
	}
	
	/**
     * Método para limpiar la caché de distancias.
     */
    public static void clearDistanceCache() {
    	System.out.println("---------------------------------------------");
    	System.out.println("CLEARING DISTANCE CACHE WITH: " + distanceCache.size() + " ENTRIES");
        distanceCache.clear();
    }
}

/*
	public static void main(String[] args) throws IOException, InterruptedException {
		// Ejemplo de uso
		double startY = -82.4070739;
		double startX = 23.13220582;
		double endY = -82.3611794;
		double endX = 23.03944083;

		double distance = 0.0;
		try {
			distance = calculateDistance(startX, startY, endX, endY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("La distancia es: " + distance + " metros");
	}
}
*/