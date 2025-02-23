import numpy as np
from typing import List
from .customer import Customer
from .depot import Depot
from .fleet import Fleet
from .location import Location
from ...service.distance import Distance
from ...service.osrm_service import OSRMService
from ...service.distance_type import DistanceType

class Problem:
        
    _problem = None   # Atributo para la instancia Singleton.
    
    def __init__(self):
        self.customers: List[Customer] = []
        self.depots: List[Depot] = []
        self.cost_matrix: np.ndarray = np.array([])
        
    def __init__(self):
        self.customers = []
        self.depots = [] 
    
    def __init__(self):
        self.customers = []
        self.depots = []
        self.cost_matrix = [] 
    
    # Método Singleton para obtener la única instancia de la clase Problem.    
    @classmethod
    def get_problem(cls):
        if cls._problem is None:
            cls._problem = Problem()
        return cls._problem
    
    def get_customers(self) -> List[Customer]:
        return self.customers

    def set_customers(self, customers: List[Customer]):
        self.customers = customers

    def get_depots(self) -> List[Depot]:
        return self.depots

    def set_depots(self, depots: List[Depot]):
        self.depots = depots
        
    def get_cost_matrix(self) -> np.ndarray:
        return self.cost_matrix

    def set_cost_matrix(self, cost_matrix: np.ndarray):
        self.cost_matrix = cost_matrix

    # Método para obtener el total de los clientes.
    def get_total_customers(self) -> int:
        return len(self.customers)

    # Método para obtener el total de los depósitos.
    def get_total_depots(self) -> int:
        return len(self.depots)
    
    # Método para obtener la lista de IDs de clientes.
    def get_list_id_customers(self) -> List[int]:
        list_id_customers = [customer.get_id_customer() for customer in self.customers]
        return list_id_customers
    
    # Método para obtener la lista de coordenadas de los clientes.
    def get_list_coordinates_customers(self) -> List[Location]:
        list_coordinates_customers = [customer.get_location_customer() for customer in self.customers]
        return list_coordinates_customers

    # Método para obtener la demanda total.
    def get_total_request(self) -> float:
        total_request = sum(customer.get_request_customer() for customer in self.customers)
        return total_request

    # Método para obtener un cliente dado su ID.
    def get_customer_by_id_customer(self, id_customer: int) -> Customer:
        for customer in self.customers:
            if customer.get_id_customer() == id_customer:
                return customer
        return None  # Retorna None si no encuentra el cliente

    # Método para obtener las coordenadas de un cliente dado su ID.
    def get_location_by_id_customer(self, id_customer: int) -> Location:
        for customer in self.customers:
            if customer.get_id_customer() == id_customer:
                return customer.get_location_customer()
        return None  # Retorna None si no encuentra las coordenadas
    
    # Método encargado de buscar un depósito dado su identificador.
    def get_depot_by_id_depot(self, id_depot: int) -> Depot:
        for depot in self.depots:
            if depot.get_id_depot() == id_depot:
                return depot
        return None  # Retorna None si no encuentra el depósito
    
    # Método encargado de buscar las coordenadas de un depósito dado su identificador.
    def get_location_by_id_depot(self, id_depot: int) -> Location:
        for depot in self.depots:
            if depot.get_id_depot() == id_depot:
                return depot.get_location_depot()
        return None  # Retorna None si no encuentra las coordenadas
    
    # Método encargado de devolver la posición que ocupa un depósito en la lista de depósitos pasada por parámetro.
    def find_pos_element(self, list_id: List[int], id_element: int) -> int:
        try:
            return list_id.index(id_element)
        except ValueError:
            return -1  # Retorna -1 si no encuentra el elemento

    # Método encargado de devolver la posición que ocupa un cliente en la lista pasada por parámetro.
    def find_pos_customer(self, customers: List[Customer], id_customer: int) -> int:
        for i, customer in enumerate(customers):
            if customer is not None:
                if customer.get_id_customer() == id_customer:
                    return i
        return -1  # Retorna -1 si no encuentra el cliente

    # Método encargado de devolver la posición que ocupa un depósito en la lista pasada por parámetro.
    def find_pos_depot(self, depots: List[Depot], id_element: int) -> int:
        for i, depot in enumerate(depots):
            if depot.get_id_depot() == id_element:
                return i
        return -1  # Retorna -1 si no encuentra el depósito
    
    # Método encargado de devolver la demanda de un cliente dado su identificador.
    def get_request_by_id_customer(self, id_customer: int) -> float:
        for customer in self.customers:
            if customer.get_id_customer() == id_customer:
                return customer.get_request_customer()
        return 0.0  # Retorna 0.0 si no encuentra la demanda del cliente
    
    # Método encargado de devolver la posición del elemento en la matriz de costo.
    def get_pos_element(self, id_element: int) -> int:
        pos_element = -1
        total_depots = len(self.depots)
        counter = 0
        found = True
        
        while counter < total_depots and found:
            if self.depots[counter].get_id_depot() == id_element:
                pos_element = counter + len(self.customers)
                found = False
            else:
                counter += 1
        if found:
            counter = 0
            while counter < len(self.customers) and found:
                if self.customers[counter].get_id_customer() == id_element:
                    pos_element = counter
                    found = False
                else:
                    counter += 1
        return pos_element
    
    # Método encargado de devolver la posición del elemento en la matriz de costo (usando una lista personalizada de clientes).
    def get_pos_element_in_list(self, id_element: int, list_customers: List[Customer]) -> int:
        pos_element = -1
        total_customers = len(list_customers)
        total_depots = len(self.depots)
        counter = 0
        found = True
        
        while counter < total_depots and found:
            if self.depots[counter].get_id_depot() == id_element:
                pos_element = counter
                found = False
            else:
                counter += 1
        if found:
            counter = 0
            while counter < total_customers and found:
                if list_customers[counter].get_id_customer() == id_element:
                    pos_element = counter
                    found = False
                else:
                    counter += 1
        return pos_element
    
    # Método encargado de devolver la capacidad total de los depósitos.
    def get_total_capacity(self) -> float:
        total_capacity = 0.0
        for depot in self.depots:
            total_capacity += self.get_total_capacity_by_depot(depot)
        return total_capacity

    # Método encargado de devolver la capacidad total de un depósito dado el depósito.
    def get_total_capacity_by_depot(self, depot: Depot) -> float:
        total_capacity = 0.0
        
        for fleet in depot.get_fleet_depot():
            capacity_vehicle = fleet.get_capacity_vehicle()
            count_vehicles = fleet.get_count_vehicles()
            total_capacity += capacity_vehicle * count_vehicles
        
        return total_capacity
    
    # Método encargado de devolver la capacidad total de un depósito dado su identificador.
    def get_total_capacity_by_id_depot(self, id_depot: int) -> float:
        total_capacity = 0.0
        # Encontrar el depósito por ID
        depot = next((d for d in self.depots if d.get_id_depot() == id_depot), None)
        
        if depot:
            # Calcular la capacidad total de la flota del depósito
            for fleet in depot.get_fleet_depot():
                capacity_vehicle = fleet.get_capacity_vehicle()
                count_vehicles = fleet.get_count_vehicles()
                total_capacity += capacity_vehicle * count_vehicles
        return total_capacity
    
    # Método encargado de obtener el id del depósito con mayor capacidad.
    def get_depot_with_mu(self) -> int:
        max_capacity_depot = self.get_total_capacity_by_depot(self.depots[0].get_id_depot())
        id_depot_mu = self.depots[0].get_id_depot()
        
        for depot in self.depots[1:]:
            current_capacity_depot = self.get_total_capacity_by_depot(depot.get_id_depot())
            if max_capacity_depot < current_capacity_depot:
                max_capacity_depot = current_capacity_depot
                id_depot_mu = depot.get_id_depot()
        return id_depot_mu
    
    # Método encargado de obtener el id del depósito con mayor capacidad de la lista.
    def get_depot_with_mu(self, depots: List[Depot]) -> int:
        max_capacity_depot = self.get_total_capacity_by_depot(depots[0].get_id_depot())
        id_depot_mu = depots[0].get_id_depot()
        
        for depot in depots[1:]:
            current_capacity_depot = self.get_total_capacity_by_depot(depot.get_id_depot())
            if max_capacity_depot < current_capacity_depot:
                max_capacity_depot = current_capacity_depot
                id_depot_mu = depot.get_id_depot()
        return id_depot_mu
    
    # Método encargado de obtener la capacidad del depósito con mayor capacidad de la lista.
    def get_capacity_depot_with_mu(self, depots: List[Depot]) -> float:
        max_capacity_depot = self.get_total_capacity_by_depot(depots[0].get_id_depot())
        
        for depot in depots[1:]:
            current_capacity_depot = self.get_total_capacity_by_depot(depot.get_id_depot())
            if max_capacity_depot < current_capacity_depot:
                max_capacity_depot = current_capacity_depot
        
        return max_capacity_depot
    
    # Método encargado de obtener la lista de los id de los clientes y los depósitos.
    def get_list_id_elements(self) -> List[int]:
        list_id_elements = [customer.get_id_customer() for customer in self.customers]
        list_id_elements.extend(depot.get_id_depot() for depot in self.depots)
        return list_id_elements

    # Método encargado de obtener la lista de los id de los depósitos.
    def get_list_id_depots(self) -> List[int]:
        list_id_depots = [depot.get_id_depot() for depot in self.depots]
        return list_id_depots
    
    # Método encargado de obtener los identificadores de los elementos en la lista pasada por parámetros.
    def get_list_id(self, customers: List[Customer]) -> List[int]:
        list_id = [customer.get_id_customer() for customer in customers]
        return list_id

    # Método encargado de cargar los datos de los clientes con coordenadas.
    def load_customer(self, id_customers: List[int], request_customers: List[float], axis_x_customers: List[float], axis_y_customers: List[float]):
        for i in range(len(id_customers)):
            customer = Customer()
            customer.set_id_customer(id_customers[i])
            customer.set_request_customer(request_customers[i])
            
            location = Location(round(axis_x_customers[i], 6), round(axis_y_customers[i], 6))
            customer.set_location_customer(location)
            
            self.customers.append(customer)
            
    # Método encargado de cargar los datos de los depósitos (con coordenadas) y las flotas.
    def load_depot(self, id_depots: List[int], axis_x_depots: List[float], axis_y_depots: List[float], count_vehicles: List[List[int]], capacity_vehicles: List[List[float]]):
        depots = []
        total_depots = len(id_depots)

        for i in range(total_depots):
            depot = Depot()
            depot.set_id_depot(id_depots[i])

            location = Location(round(axis_x_depots[i], 6), round(axis_y_depots[i], 6))
            depot.set_location_depot(location)

            fleets = []
            total_fleets = len(count_vehicles[i])

            for j in range(total_fleets):
                fleet = Fleet()
                fleet.set_count_vehicles(count_vehicles[i][j])
                fleet.set_capacity_vehicle(capacity_vehicles[i][j])

                fleets.append(fleet)

            depot.set_fleet_depot(fleets)
            depots.append(depot)
        
        self.depots = depots
             
    # Método encargado de llenar la matriz de costo usando la distancia deseada.
    def fill_cost_matrix(self, customers: List[Customer], depots: List[Depot], distance_type: DistanceType) -> np.ndarray:
        total_customers = len(customers)
        total_depots = len(depots)

        # Crear una matriz de costos de tamaño totalCustomers + totalDepots
        cost_matrix = np.zeros((total_depots, total_customers))

        for depot_index, depot in enumerate(depots):
            axis_x_depot = depot.get_location_depot().get_axis_x()
            axis_y_depot = depot.get_location_depot().get_axis_y()

            for customer_index, customer in enumerate(customers):
                axis_x_customer = customer.get_location_customer().get_axis_x()
                axis_y_customer = customer.get_location_customer().get_axis_y()
                
                cost = Distance.calculate_distance(axis_x_depot, axis_y_depot, axis_x_customer, axis_y_customer, distance_type)
                cost_matrix[depot_index, customer_index] = cost

        return cost_matrix
    
    # Método para llenar la matriz de costos usando distancias reales entre clientes y depósitos.
    def fill_cost_matrix_real(self, customers: List[Customer], depots: List[Depot]) -> np.ndarray:
        total_customers = len(customers)
        total_depots = len(depots)
        cost_matrix = np.zeros((total_depots, total_customers))
        
        for depot_index, depot in enumerate(depots):
            axis_x_depot = depot.get_location_depot().get_axis_x()
            axis_y_depot = depot.get_location_depot().get_axis_y()

            for customer_index, customer in enumerate(customers):
                axis_x_customer = customer.get_location_customer().get_axis_x()
                axis_y_customer = customer.get_location_customer().get_axis_y()

                cost = None
                while cost is None:
                    try:
                        cost = OSRMService.calculate_distance(axis_x_depot, axis_y_depot, axis_x_customer, axis_y_customer)
                    except Exception as e:
                        print(f"Error al calcular la distancia entre el depósito {depot_index} y el cliente {customer_index}: {e}")
                        # Opcional: aquí podrías poner un `time.sleep()` si quieres hacer una espera entre intentos
                        continue  # Reintenta el cálculo

                cost_matrix[depot_index, customer_index] = cost
        
        return cost_matrix
    
    def fill_cost_matrix_test(self, customers: List[Customer], depots: List[Depot], distance_type: DistanceType):
        total_customers = len(customers)
        total_depots = len(depots)
        last_point_one = 0

        # Crear una matriz de costos de tamaño 12x12 (10 clientes + 2 depósitos)
        cost_matrix = np.zeros((total_customers + total_depots, total_customers + total_depots))

        # Inicializar las ubicaciones y calcular las distancias
        for i in range(total_customers + total_depots):
            if i < total_customers:
                axis_x_ini = customers[i].get_location_customer().get_axis_x()
                axis_y_ini = customers[i].get_location_customer().get_axis_y()
            else:
                axis_x_ini = depots[last_point_one].get_location_depot().get_axis_x()
                axis_y_ini = depots[last_point_one].get_location_depot().get_axis_y()
                last_point_one += 1

            last_point_two = 0

            for j in range(total_customers + total_depots):
                if j < total_customers:
                    axis_x_end = customers[j].get_location_customer().get_axis_x()
                    axis_y_end = customers[j].get_location_customer().get_axis_y()
                else:
                    axis_x_end = depots[last_point_two].get_location_depot().get_axis_x()
                    axis_y_end = depots[last_point_two].get_location_depot().get_axis_y()
                    last_point_two += 1

                if i == j:
                    cost_matrix[i, j] = float('inf')  # Distancia infinita en la diagonal
                else:
                    cost = Distance.calculate_distance(axis_x_ini, axis_y_ini, axis_x_end, axis_y_end, distance_type)
                    cost_matrix[i, j] = cost
                    cost_matrix[j, i] = cost  # Matriz simétrica

        return cost_matrix
    
    def fill_cost_matrix_test_real(self, customers: List[Customer], depots: List[Depot]):
        total_customers = len(customers)
        total_depots = len(depots)
        last_point_one = 0

        # Crear una matriz de costos de tamaño 12x12 (10 clientes + 2 depósitos)
        cost_matrix = np.zeros((total_customers + total_depots, total_customers + total_depots))

        # Inicializar las ubicaciones y calcular las distancias
        for i in range(total_customers + total_depots):
            if i < total_customers:
                axis_x_ini = customers[i].get_location_customer().get_axis_x()
                axis_y_ini = customers[i].get_location_customer().get_axis_y()
            else:
                axis_x_ini = depots[last_point_one].get_location_depot().get_axis_x()
                axis_y_ini = depots[last_point_one].get_location_depot().get_axis_y()
                last_point_one += 1

            last_point_two = 0

            for j in range(total_customers + total_depots):
                if j < total_customers:
                    axis_x_end = customers[j].get_location_customer().get_axis_x()
                    axis_y_end = customers[j].get_location_customer().get_axis_y()
                else:
                    axis_x_end = depots[last_point_two].get_location_depot().get_axis_x()
                    axis_y_end = depots[last_point_two].get_location_depot().get_axis_y()
                    last_point_two += 1

                if i == j:
                    cost_matrix[i, j] = float('inf')  # Distancia infinita en la diagonal
                else:
                    cost = OSRMService.calculate_distance(axis_x_ini, axis_y_ini, axis_x_end, axis_y_end)
                    cost_matrix[i, j] = cost
                    cost_matrix[j, i] = cost  # Matriz simétrica

        return cost_matrix

    # Método para calcular la matriz de costos entre centroides y depósitos.
    def calculate_cost_matrix(self, centroids: List[Depot], depots: List[Depot], distance_type: DistanceType) -> np.ndarray:
        total_depots = len(depots)
        total_centroids = len(centroids)
        
        # Crear una matriz de costos de tamaño totalCustomers + totalDepots
        cost_matrix = np.zeros((total_depots, total_centroids))

        for centroid_index, centroid in enumerate(centroids):
            axis_x_centroid = centroid.get_location_depot().get_axis_x()
            axis_y_centroid = centroid.get_location_depot().get_axis_y()

            for depot_index, depot in enumerate(depots):
                axis_x_depot = depot.get_location_depot().get_axis_x()
                axis_y_depot = depot.get_location_depot().get_axis_y()

                cost = Distance.calculate_distance(
                    axis_x_centroid, 
                    axis_y_centroid, 
                    axis_x_depot, 
                    axis_y_depot, 
                    distance_type
                )
                cost_matrix[depot_index, centroid_index] = cost
                
        return cost_matrix
    
    # Calcula una matriz de costos basada en distancias reales entre centroids y depots usando datos reales.
    def calculate_cost_matrix_real(self, centroids: List[Depot], depots: List[Depot]) -> np.ndarray:
        total_depots = len(depots)
        cost_matrix = np.zeros((total_depots, total_depots))
        
        print("----------------------------------------------------")
        for i in range(total_depots):
            axis_x_point_one = centroids[i].get_location_depot().axis_x
            axis_y_point_one = centroids[i].get_location_depot().axis_y
        
            print(f"CENTROIDE {i} X: {axis_x_point_one}")
            print(f"CENTROIDE {i} Y: {axis_y_point_one}")
            print("----------------------------------------------------")
            
            for j in range(total_depots):
                axis_x_point_two = depots[j].get_location_depot().axis_x
                axis_y_point_two = depots[j].get_location_depot().axis_y

                print(f"DEPOSITO {j} X: {axis_x_point_two}")
                print(f"DEPOSITO {j} Y: {axis_y_point_two}")
                
                try:
                    cost = OSRMService.calculate_distance(
                        axis_x_point_one, axis_y_point_one, 
                        axis_x_point_two, axis_y_point_two
                    )
                except Exception as e:
                    print(f"Error calculando la distancia: {e}")
                    cost = float('inf')
                
                print(f"COSTO: {cost}")
                cost_matrix[i, j] = cost
            print("----------------------------------------------------")
            
        return cost_matrix
    
    # Método para limpiar la información del problema.
    def clean_info_problem(self):
        self.customers.clear()
        self.depots.clear()
        self.cost_matrix = np.empty((0, 0))
        self.problem = None