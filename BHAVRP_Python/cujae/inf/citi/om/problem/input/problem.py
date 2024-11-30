from typing import List
from customer import Customer
from depot import Depot
from fleet import Fleet
from location import Location
from ...utils.distance import Distance, DistanceType
from ...controller.utils import Tools
import numpy as np

class Problem:
    _problem = None   # Atributo para la instancia Singleton
    
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
    
    """Método Singleton para obtener la única instancia de la clase Problem."""    
    @classmethod
    def get_problem(cls):
        if cls._problem is None:
            cls._problem = Problem()
        return cls._problem
    
    @staticmethod
    def newDistance(distance_type: DistanceType):
        return Distance(distance_type)
    
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

    # Método para obtener el total de los clientes
    def get_total_customers(self) -> int:
        return len(self.customers)

    # Método para obtener el total de los depósitos
    def get_total_depots(self) -> int:
        return len(self.depots)
    
    # Método para obtener la lista de IDs de clientes
    def get_list_id_customers(self) -> List[int]:
        list_id_customers = [customer.get_IDCustomer() for customer in self.customers]
        return list_id_customers
    
    # Método para obtener la lista de coordenadas de los clientes
    def get_list_coordinates_customers(self) -> List[Location]:
        list_coordinates_customers = [customer.get_locationCustomer() for customer in self.customers]
        return list_coordinates_customers

    # Método para obtener la demanda total
    def get_total_request(self) -> float:
        total_request = sum(customer.get_requestCustomer() for customer in self.customers)
        return total_request

    # Método para obtener un cliente dado su ID
    def get_customer_by_id_customer(self, id_customer: int) -> Customer:
        for customer in self.customers:
            if customer.get_IDCustomer() == id_customer:
                return customer
        return None  # Retorna None si no encuentra el cliente

    # Método para obtener las coordenadas de un cliente dado su ID
    def get_location_by_id_customer(self, id_customer: int) -> Location:
        for customer in self.customers:
            if customer.get_IDCustomer() == id_customer:
                return customer.get_locationCustomer()
        return None  # Retorna None si no encuentra las coordenadas
    
    # Método encargado de buscar un depósito dado su identificador
    def get_depot_by_id_depot(self, id_depot: int) -> Depot:
        for depot in self.depots:
            if depot.get_IDDepot() == id_depot:
                return depot
        return None  # Retorna None si no encuentra el depósito
    
    # Método encargado de buscar las coordenadas de un depósito dado su identificador
    def get_location_by_id_depot(self, id_depot: int) -> Location:
        for depot in self.depots:
            if depot.get_IDDepot() == id_depot:
                return depot.get_locationDepot()
        return None  # Retorna None si no encuentra las coordenadas
    
    # Método encargado de devolver la posición que ocupa un depósito en la lista de depósitos pasada por parámetro
    def find_pos_element(self, list_id: List[int], id_element: int) -> int:
        try:
            return list_id.index(id_element)
        except ValueError:
            return -1  # Retorna -1 si no encuentra el elemento

    # Método encargado de devolver la posición que ocupa un cliente en la lista pasada por parámetro
    def find_pos_customer(self, customers: List[Customer], id_customer: int) -> int:
        for i, customer in enumerate(customers):
            if customer.get_IDCustomer() == id_customer:
                return i
        return -1  # Retorna -1 si no encuentra el cliente

    # Método encargado de devolver la posición que ocupa un depósito en la lista pasada por parámetro
    def find_pos_depot(self, depots: List[Depot], id_element: int) -> int:
        for i, depot in enumerate(depots):
            if depot.get_IDDepot() == id_element:
                return i
        return -1  # Retorna -1 si no encuentra el depósito
    
    # Método encargado de devolver la demanda de un cliente dado su identificador
    def get_request_by_id_customer(self, id_customer: int) -> float:
        for customer in self.customers:
            if customer.get_IDCustomer() == id_customer:
                return customer.get_requestCustomer()
        return 0.0  # Retorna 0.0 si no encuentra la demanda del cliente
    
    # Método encargado de devolver la posición del elemento en la matriz de costo
    def get_pos_element(self, id_element: int) -> int:
        # Buscar en los depósitos
        for i, depot in enumerate(self.depots):
            if depot.get_IDDepot() == id_element:
                return i + len(self.customers)
        
        # Buscar en los clientes
        for i, customer in enumerate(self.customers):
            if customer.get_IDCustomer() == id_element:
                return i
        
        return -1  # Retorna -1 si no encuentra el elemento
    
    # Método encargado de devolver la posición del elemento en la matriz de costo (usando una lista personalizada de clientes)
    def get_pos_element_by_customer_list(self, id_element: int, list_customers: List[Customer]) -> int:
        # Buscar en los depósitos
        for i, depot in enumerate(self.depots):
            if depot.get_IDDepot() == id_element:
                return i + len(list_customers)
        
        # Buscar en la lista de clientes
        for i, customer in enumerate(list_customers):
            if customer.get_IDCustomer() == id_element:
                return i
        
        return -1  # Retorna -1 si no encuentra el elemento
    
    # Método encargado de devolver la capacidad total de los depósitos
    def get_total_capacity(self) -> float:
        total_capacity = 0.0
        for depot in self.depots:
            total_capacity += self.get_total_capacity_by_depot(depot)
        return total_capacity

    # Método encargado de devolver la capacidad total de un depósito dado el depósito
    def get_total_capacity_by_depot(self, depot: Depot) -> float:
        total_capacity = 0.0
        for fleet in depot.get_fleetDepot():
            capacity_vehicle = fleet.get_capacityVehicle()
            count_vehicles = fleet.get_countVehicles()
            total_capacity += capacity_vehicle * count_vehicles
        return total_capacity
    
    # Método encargado de devolver la capacidad total de un depósito dado su identificador
    def get_total_capacity_by_depot(self, id_depot: int) -> float:
        total_capacity = 0.0
        # Encontrar el depósito por ID
        depot = next((d for d in self.depots if d.get_IDDepot() == id_depot), None)
        
        if depot:
            # Calcular la capacidad total de la flota del depósito
            for fleet in depot.get_fleetDepot():
                capacity_vehicle = fleet.get_capacityVehicle()
                count_vehicles = fleet.get_countVehicles()
                total_capacity += capacity_vehicle * count_vehicles
        return total_capacity
    
    # Método encargado de obtener la lista de las capacidades de los depósitos
    def get_capacities_depot(self) -> List[float]:
        capacities = []
        for depot in self.depots:
            capacity_depot = self.get_total_capacity_by_depot(depot.get_IDDepot())
            capacities.append(capacity_depot)
        return capacities
    
    # Método encargado de obtener el id del depósito con mayor capacidad
    def get_depot_with_mu(self) -> int:
        max_capacity_depot = self.get_total_capacity_by_depot(self.depots[0].get_IDDepot())
        id_depot_mu = self.depots[0].get_IDDepot()
        
        for depot in self.depots[1:]:
            current_capacity_depot = self.get_total_capacity_by_depot(depot.get_IDDepot())
            if max_capacity_depot < current_capacity_depot:
                max_capacity_depot = current_capacity_depot
                id_depot_mu = depot.get_IDDepot()
        
        return id_depot_mu
    
    # Método encargado de obtener el id del depósito con mayor capacidad de la lista
    def get_depot_with_mu_from_list(self, depots: List[Depot]) -> int:
        max_capacity_depot = self.get_total_capacity_by_depot(depots[0].get_IDDepot())
        id_depot_mu = depots[0].get_IDDepot()
        
        for depot in depots[1:]:
            current_capacity_depot = self.get_total_capacity_by_depot(depot.get_IDDepot())
            if max_capacity_depot < current_capacity_depot:
                max_capacity_depot = current_capacity_depot
                id_depot_mu = depot.get_IDDepot()
        
        return id_depot_mu
    
    # Método encargado de obtener la capacidad del depósito con mayor capacidad de la lista
    def get_capacity_depot_with_mu_from_list(self, depots: List[Depot]) -> float:
        max_capacity_depot = self.get_total_capacity_by_depot(depots[0].get_IDDepot())
        
        for depot in depots[1:]:
            current_capacity_depot = self.get_total_capacity_by_depot(depot.get_IDDepot())
            if max_capacity_depot < current_capacity_depot:
                max_capacity_depot = current_capacity_depot
        
        return max_capacity_depot
    
    # Método encargado de obtener la lista de los id de los clientes y los depósitos
    def get_list_id_elements(self) -> List[int]:
        list_id_elements = [customer.get_IDCustomer() for customer in self.customers]
        list_id_elements.extend(depot.get_IDDepot() for depot in self.depots)
        return list_id_elements

    # Método encargado de obtener la lista de los id de los depósitos
    def get_list_id_depots(self) -> List[int]:
        list_id_depots = [depot.get_IDDepot() for depot in self.depots]
        return list_id_depots
    
    # Método encargado de obtener los identificadores de los elementos en la lista pasada por parámetros
    def get_list_id(self, customers: List[Customer]) -> List[int]:
        list_id = [customer.get_IDCustomer() for customer in customers]
        return list_id

    # Método encargado de cargar los datos de los clientes sin coordenadas
    def load_customer(self, id_customers: List[int], request_customers: List[float]) -> None:
        for i in range(len(id_customers)):
            customer = Customer()
            customer.set_IDCustomer(id_customers[i])
            customer.set_RequestCustomer(request_customers[i])
            self.customers.append(customer)

    # Método encargado de cargar los datos de los clientes con coordenadas
    def load_customer_with_coordinates(self, id_customers: List[int], request_customers: List[float], axis_x_customers: List[float], axis_y_customers: List[float]) -> None:
        for i in range(len(id_customers)):
            customer = Customer()
            customer.set_IDCustomer(id_customers[i])
            customer.set_RequestCustomer(request_customers[i])
            
            location = Location(Tools.truncate_double(axis_x_customers[i], 6), Tools.truncate_double(axis_y_customers[i], 6))
            customer.set_LocationCustomer(location)
            
            self.customers.append(customer)
            
    # Método encargado de cargar los datos de los depósitos (con coordenadas) y las flotas
    def loadDepot(idDepots, axisXDepots, axisYDepots, countVehicles, capacityVehicles):
        depots = []
        totalDepots = len(idDepots)

        for i in range(totalDepots):
            depot = Depot()
            depot.setIDDepot(idDepots[i])

            location = Location(round(axisXDepots[i], 6), round(axisYDepots[i], 6))
            depot.setLocationDepot(location)

            fleets = []
            totalFleets = len(countVehicles[i])

            for j in range(totalFleets):
                fleet = Fleet()
                fleet.setCountVehicles(countVehicles[i][j])
                fleet.setCapacityVehicle(capacityVehicles[i][j])

                fleets.append(fleet)

            depot.setFleetDepot(fleets)
            depots.append(depot)
            
    # Método encargado de cargar los datos de los depósitos (sin coordenadas) y las flotas
    def loadDepotWithoutCoordinates(idDepots, countVehicles, capacityVehicles):
        depots = []
        totalDepots = len(idDepots)

        for i in range(totalDepots):
            depot = Depot()
            depot.setIDDepot(idDepots[i])

            fleets = []
            totalFleets = len(countVehicles[i])

            for j in range(totalFleets):
                fleet = Fleet()
                fleet.setCountVehicles(countVehicles[i][j])
                fleet.setCapacityVehicle(capacityVehicles[i][j])

                fleets.append(fleet)

            depot.setFleetDepot(fleets)
            depots.append(depot)
            
    # Método encargado de llenar la matriz de costo usando listas de distancias
    def fillCostMatrixWithDistances(distances):
        totalDistances = len(distances)
        costMatrix = np.zeros((totalDistances, totalDistances))

        for i in range(totalDistances):
            for j in range(len(distances[i])):
                costInDistance = distances[i][j]
                costMatrix.setItem(i, j, costInDistance)
            
    # Método encargado de llenar la matriz de costo usando la distancia deseada
    def fillCostMatrixWithDistanceType(self, customers, depots, distanceType):
        totalCustomers = len(customers)
        totalDepots = len(depots)

        # Crear una matriz de costos de tamaño totalCustomers + totalDepots
        costMatrix = np.full((totalCustomers + totalDepots, totalCustomers + totalDepots), np.inf)

        # Crear el objeto de distancia según el tipo
        distance = self.newDistance(distanceType)

        lastPointOne = 0
        lastPointTwo = 0

        for i in range(totalCustomers + totalDepots):
            if i < totalCustomers:
                axisXIni = customers[i].getLocationCustomer().getAxisX()
                axisYIni = customers[i].getLocationCustomer().getAxisY()
            else:
                axisXIni = depots[lastPointOne].getLocationDepot().getAxisX()
                axisYIni = depots[lastPointOne].getLocationDepot().getAxisY()
                lastPointOne += 1

            for j in range(totalCustomers + totalDepots):
                if j < totalCustomers:
                    axisXEnd = customers[j].getLocationCustomer().getAxisX()
                    axisYEnd = customers[j].getLocationCustomer().getAxisY()
                else:
                    axisXEnd = depots[lastPointTwo].getLocationDepot().getAxisX()
                    axisYEnd = depots[lastPointTwo].getLocationDepot().getAxisY()
                    lastPointTwo += 1

                if i == j:
                    costMatrix.setItem(i, j, float('inf'))
                else:
                    cost = distance.calculateDistance(axisXIni, axisYIni, axisXEnd, axisYEnd)
                    costMatrix.setItem(i, j, cost)
                    costMatrix.setItem(j, i, cost)

        return costMatrix
    
    # Método encargado de llenar la matriz de costo usando el tipo de distancia deseada
    def fillCostMatrixWithType(self, distanceType):
        totalCustomers = len(self.customers)
        totalDepots = len(self.depots)

        costMatrix = np.full((totalCustomers + totalDepots, totalCustomers + totalDepots))

        if distanceType == "Real":
            # Si la distancia es real, se debe definir el comportamiento específico aquí
            pass
        else:
            distance = self.newDistance(distanceType)

            lastPointOne = 0
            lastPointTwo = 0

            for i in range(totalCustomers + totalDepots):
                if i < totalCustomers:
                    axisXIni = self.customers[i].getLocationCustomer().getAxisX()
                    axisYIni = self.customers[i].getLocationCustomer().getAxisY()
                else:
                    axisXIni = self.depots[lastPointOne].getLocationDepot().getAxisX()
                    axisYIni = self.depots[lastPointOne].getLocationDepot().getAxisY()
                    lastPointOne += 1

                for j in range(totalCustomers + totalDepots):
                    if j < totalCustomers:
                        axisXEnd = self.customers[j].getLocationCustomer().getAxisX()
                        axisYEnd = self.customers[j].getLocationCustomer().getAxisY()
                    else:
                        axisXEnd = self.depots[lastPointTwo].getLocationDepot().getAxisX()
                        axisYEnd = self.depots[lastPointTwo].getLocationDepot().getAxisY()
                        lastPointTwo += 1

                    if i == j:
                        costMatrix.setItem(i, j, float('inf'))
                    else:
                        cost = distance.calculateDistance(axisXIni, axisYIni, axisXEnd, axisYEnd)
                        costMatrix.setItem(i, j, cost)
                        costMatrix.setItem(j, i, cost)

        return costMatrix
    
    # Método para llenar la matriz de costos usando el tipo de distancia proporcionado
    def fill_cost_matrix_xxx(self, customers, depots, distance_type):
        total_customers = len(customers)
        total_depots = len(depots)

        # Crear una matriz de costos (matriz cuadrada)
        cost_matrix = np.full((total_customers + total_depots, total_customers + total_depots))
        distance = self.newDistance(distance_type)

        axis_x_ini = 0.0
        axis_y_ini = 0.0
        axis_x_end = 0.0
        axis_y_end = 0.0
        last_point_one = 0
        last_point_two = 0
        cost = 0.0

        for i in range(total_customers + total_depots):
            if i <= total_customers - 1:
                axis_x_ini = customers[i].get_location_customer().get_axis_x()
                axis_y_ini = customers[i].get_location_customer().get_axis_y()
            else:
                axis_x_ini = depots[last_point_one].get_location_depot().get_axis_x()
                axis_y_ini = depots[last_point_one].get_location_depot().get_axis_y()
                last_point_one += 1

            last_point_two = 0

            for j in range(total_customers + total_depots):
                if j <= total_customers - 1:
                    axis_x_end = customers[j].get_location_customer().get_axis_x()
                    axis_y_end = customers[j].get_location_customer().get_axis_y()
                else:
                    axis_x_end = depots[last_point_two].get_location_depot().get_axis_x()
                    axis_y_end = depots[last_point_two].get_location_depot().get_axis_y()
                    last_point_two += 1

                if i == j:
                    cost_matrix.set_item(i, j, float('-inf'))
                else:
                    cost = distance.calculate_distance(axis_x_ini, axis_y_ini, axis_x_end, axis_y_end)
                    cost_matrix.set_item(i, j, cost)
                    cost_matrix.set_item(j, i, cost)

        return cost_matrix
    
    # Método encargado de llenar la matriz de costo usando la distancia deseada
    def create_cost_matrix(self, customers, depots, distance_type):
        total_customers = len(customers)
        total_depots = len(depots)

        cost_matrix = np.full((total_customers + total_depots, total_customers + total_depots))
        distance = self.newDistance(distance_type)

        axis_x_ini = 0.0
        axis_y_ini = 0.0
        axis_x_end = 0.0
        axis_y_end = 0.0
        last_point_one = 0
        last_point_two = 0
        cost = 0.0

        for i in range(total_customers + total_depots):
            if i <= total_customers - 1:
                axis_x_ini = customers[i].get_location_customer().get_axis_x()
                axis_y_ini = customers[i].get_location_customer().get_axis_y()
            else:
                axis_x_ini = depots[last_point_one].get_location_customer().get_axis_x()
                axis_y_ini = depots[last_point_one].get_location_customer().get_axis_y()
                last_point_one += 1

            last_point_two = 0

            for j in range(total_customers + total_depots):
                if j <= total_customers - 1:
                    axis_x_end = customers[j].get_location_customer().get_axis_x()
                    axis_y_end = customers[j].get_location_customer().get_axis_y()
                else:
                    axis_x_end = depots[last_point_two].get_location_customer().get_axis_x()
                    axis_y_end = depots[last_point_two].get_location_customer().get_axis_y()
                    last_point_two += 1

                if i == j:
                    cost_matrix.set_item(i, j, float('inf'))
                else:
                    cost = distance.calculate_distance(axis_x_ini, axis_y_ini, axis_x_end, axis_y_end)
                    cost_matrix.set_item(i, j, cost)
                    cost_matrix.set_item(j, i, cost)

        return cost_matrix
    
    # Método para calcular la matriz de costos entre centroides y depósitos
    def calculate_cost_matrix(self, centroids, depots, type_distance):
        total_depots = len(depots)
        cost_matrix = np.full((total_depots, total_depots))
        distance = self.newDistance(type_distance)

        axis_x_point_one = 0.0
        axis_y_point_one = 0.0
        axis_x_point_two = 0.0
        axis_y_point_two = 0.0
        cost = 0.0

        print("----------------------------------------------------")

        for i in range(total_depots):
            axis_x_point_one = centroids[i].get_location_depot().get_axis_x()
            axis_y_point_one = centroids[i].get_location_depot().get_axis_y()

            print(f"CENTROIDE {i} X: {axis_x_point_one}")
            print(f"CENTROIDE {i} Y: {axis_y_point_one}")
            print("----------------------------------------------------")

            for j in range(total_depots):
                axis_x_point_two = depots[j].get_location_depot().get_axis_x()
                axis_y_point_two = depots[j].get_location_depot().get_axis_y()

                print(f"DEPOSITO {j} X: {axis_x_point_two}")
                print(f"DEPOSITO {j} Y: {axis_y_point_two}")

                cost = distance.calculate_distance(axis_x_point_one, axis_y_point_one, axis_x_point_two, axis_y_point_two)

                print(f"COSTO: {cost}")

                cost_matrix.set_item(i, j, cost)

            print("----------------------------------------------------")

        return cost_matrix
    
    # Método para calcular la matriz de costos entre medoids y clientes
    def calculate_cost_matrix_v2(self, type_distance, medoids, customers):
        total_depots = len(medoids)
        total_customers = len(customers)

        cost_matrix = np.full((total_customers + total_depots, total_customers + total_depots))
        distance = self.newDistance(type_distance)

        axis_x_ini = 0.0
        axis_y_ini = 0.0
        axis_x_end = 0.0
        axis_y_end = 0.0
        cost = 0.0

        # Se calculan las distancias solo de los depósitos o medoids a los clientes
        for i in range(total_depots):
            axis_x_ini = medoids[i].get_location_depot().get_axis_x()
            axis_y_ini = medoids[i].get_location_depot().get_axis_y()

            for j in range(total_customers):
                axis_x_end = customers[j].get_location_customer().get_axis_x()
                axis_y_end = customers[j].get_location_customer().get_axis_y()

                cost = distance.calculate_distance(axis_x_ini, axis_y_ini, axis_x_end, axis_y_end)

                cost_matrix.set_item(i, j, cost)

        return cost_matrix
    
    # Método para limpiar la información del problema
    def clean_info_problem(self):
        self.customers.clear()
        self.depots.clear()
        self.cost_matrix.clear()
        self.problem = None