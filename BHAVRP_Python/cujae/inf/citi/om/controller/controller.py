from typing import List
from ..problem.input.problem import Problem
from .utils.tools import Tools
from .utils.order_type import OrderType
from ..factory.methods.factory_assignment import FactoryAssignment

class Controller:
    _instance = None   # Atributo para la instancia Singleton
    
    def __init__(self):
        if Controller._instance is None:
            raise Exception
        self.solution = None
        
    """Método Singleton para obtener la única instancia de la clase controladora."""    
    @staticmethod
    def get_instance():
        if Controller._instance is None:
            Controller._instance = Controller()
        return Controller._instance

    def get_solution(self):
        return self.solution
    
    def set_solution(self, solution):
        self.solution = solution
        
    # Método para cargar datos de problemas utilizando listas de distancias.
    def load_problem(
        self,
        id_customers: List[int],
        request_customers: List[float],
        id_depots: List[int],
        count_vehicles: List[List[int]],
        capacity_vehicles: List[List[float]],
        list_distances: List[List[float]]
    ) -> bool:
        
        loaded = False
        
        print("ENTRADA A LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")
        print(f"CANTIDAD DE CLIENTES: {len(id_customers)}")
        print("-------------------------------------------------------------------------------")
        
        for i in range(len(id_customers)):
            print(f"ID CLIENTE: {id_customers[i]}")
            print(f"DEMANDA : {request_customers[i]}")
        
        print(f"CANTIDAD DE DEPÓSITOS: {len(id_depots)}")
        print("-------------------------------------------------------------------------------")
        
        for i in range(len(id_depots)):
            print(f"ID DEPÓSITO: {id_depots[i]}")
            print(f"CANTIDAD DE FLOTAS DEL DEPÓSITO: {len(count_vehicles[i])}")
            for j in range(len(count_vehicles[i])):
                print(f"CANTIDAD DE VEHÍCULOS: {count_vehicles[i][j]}")
                print(f"CAPACIDAD DE LOS VEHÍCULOS: {capacity_vehicles[i][j]}")
            print("-------------------------------------------------------------------------------")

        # Verifica que los datos de entrada sean válidos
        if (id_customers and request_customers and len(id_customers) == len(request_customers) and
            id_depots and count_vehicles and capacity_vehicles and
            len(id_depots) == len(count_vehicles) == len(capacity_vehicles) and
            list_distances and len(list_distances) == (len(id_customers) + len(id_depots))):
            
            # Simular la carga del problema 
            print("Simulating Problem Loading...")
            total_capacity = sum(sum(fleet) for fleet in count_vehicles)
            total_request = sum(request_customers)
            
            if total_capacity >= total_request:
                loaded = True
                # Simular el llenado de la matriz de costos
                print("Filling Cost Matrix...")
        
        print("-------------------------------------------------------------------------------")
        print(f"DEMANDA TOTAL DE LOS CLIENTES: {sum(request_customers)}")
        print(f"CAPACIDAD TOTAL DE LOS DEPÓSITOS: {total_capacity}")
        print("-------------------------------------------------------------------------------")
        print(f"CARGA EXITOSA: {loaded}")
        print("FIN DE LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")
        
        return loaded
    
    # Método encargado de cargar los datos del problema usando matriz de costo
    def load_problem(
        self, 
        id_customers: List[int], 
        request_customers: List[float], 
        id_depots: List[int], 
        count_vehicles: List[List[int]], 
        capacity_vehicles: List[List[float]], 
        cost_matrix
    ) -> bool:
        
        loaded = False
        
        print("ENTRADA A LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")
        print(f"CANTIDAD DE CLIENTES: {len(id_customers)}")
        print("-------------------------------------------------------------------------------")
        
        for i in range(len(id_customers)):
            print(f"ID CLIENTE: {id_customers[i]}")
            print(f"DEMANDA : {request_customers[i]}")
        
        print(f"CANTIDAD DE DEPÓSITOS: {len(id_depots)}")
        print("-------------------------------------------------------------------------------")
        
        for i in range(len(id_depots)):
            print(f"ID DEPÓSITO: {id_depots[i]}")
            print(f"CANTIDAD DE FLOTAS DEL DEPÓSITO: {len(count_vehicles[i])}")
            print("-------------------------------------------------------------------------------")
            
            for j in range(len(count_vehicles[i])):
                print(f"CANTIDAD DE VEHÍCULOS: {count_vehicles[i][j]}")
                print(f"CAPACIDAD DE LOS VEHÍCULOS: {capacity_vehicles[i][j]}")
            
            print("-------------------------------------------------------------------------------")

        # Validar las condiciones
        if (id_customers and request_customers and len(id_customers) == len(request_customers) and
            id_depots and count_vehicles and capacity_vehicles and 
            len(id_depots) == len(count_vehicles) == len(capacity_vehicles) and
            cost_matrix.shape[0] == len(id_customers) + len(id_depots) and cost_matrix.shape[1] == len(id_customers) + len(id_depots)):

            # Aquí debes cargar los clientes y depósitos en el problema
            # (Esto dependería de tu implementación del objeto 'Problem', ya que se utiliza como un singleton)
            problem = Problem.get_problem()
            problem.load_customer(id_customers, request_customers)
            problem.load_depot(id_depots, count_vehicles, capacity_vehicles)

            # Verificar la capacidad total contra la demanda total
            if problem.get_total_capacity() >= problem.get_total_request():
                loaded = True
                problem.set_cost_matrix(cost_matrix)

        print("-------------------------------------------------------------------------------")
        print(f"DEMANDA TOTAL DE LOS CLIENTES: {Problem.get_problem().get_total_request()}")
        print(f"CAPACIDAD TOTAL DE LOS DEPÓSITOS: {Problem.get_problem().get_total_capacity()}")
        print("-------------------------------------------------------------------------------")
        print(f"CARGA EXITOSA: {loaded}")
        print("FIN DE LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")

        return loaded
    
    # Método encargado de cargar los datos del problema (incluido las coordenadas) usando listas de distancias
    def load_problem(
        self, 
        id_customers, 
        request_customers, 
        axis_x_customers, 
        axis_y_customers, 
        id_depots, 
        axis_x_depots, 
        axis_y_depots, 
        count_vehicles, 
        capacity_vehicles, 
        list_distances
    ) -> bool:
        
        loaded = False
        
        print("ENTRADA A LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")
        print(f"CANTIDAD DE CLIENTES: {len(id_customers)}")
        print("-------------------------------------------------------------------------------")
        for i in range(len(id_customers)):
            print(f"ID CLIENTE: {id_customers[i]}")
            print(f"DEMANDA : {request_customers[i]}")
            print(f"X : {axis_x_customers[i]}")
            print(f"Y : {axis_y_customers[i]}")

        print(f"CANTIDAD DE DEPÓSITOS: {len(id_depots)}")
        print("-------------------------------------------------------------------------------")

        total_vehicles = 0
        capacity_vehicle = 0.0

        for i in range(len(id_depots)):
            print(f"ID DEPÓSITO: {id_depots[i]}")
            print(f"X : {axis_x_depots[i]}")
            print(f"Y : {axis_y_depots[i]}")

            print(f"CANTIDAD DE FLOTAS DEL DEPÓSITO: {len(count_vehicles[i])}")
            for j in range(len(count_vehicles[i])):
                total_vehicles = count_vehicles[i][j]
                capacity_vehicle = capacity_vehicles[i][j]

                print(f"CANTIDAD DE VEHÍCULOS: {count_vehicles[i][j]}")
                print(f"CAPACIDAD DE LOS VEHÍCULOS: {capacity_vehicles[i][j]}")

            print(f"CAPACIDAD TOTAL DEL DEPÓSITO: {total_vehicles * capacity_vehicle}")
            print("-------------------------------------------------------------------------------")

        # Validar las condiciones necesarias para proceder con la carga
        if (id_customers and request_customers and axis_x_customers and axis_y_customers and
            id_depots and axis_x_depots and axis_y_depots and count_vehicles and
            capacity_vehicles and list_distances):
            
            problem = Problem.get_problem()
            problem.load_customer(id_customers, request_customers, axis_x_customers, axis_y_customers)
            problem.load_depot(id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles)

            # Verificar que la capacidad total es suficiente para la demanda total
            if problem.get_total_capacity() >= problem.get_total_request():
                loaded = True
                problem.fill_cost_matrix(list_distances)

        print("-------------------------------------------------------------------------------")
        print(f"DEMANDA TOTAL DE LOS CLIENTES: {Problem.get_problem().get_total_request()}")
        print(f"CAPACIDAD TOTAL DE LOS DEPÓSITOS: {Problem.get_problem().get_total_capacity()}")
        print("-------------------------------------------------------------------------------")
        print(f"CARGA EXITOSA: {loaded}")
        print("FIN DE LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")

        return loaded
    
    # Método encargado de cargar los datos del problema (incluido las coordenadas) usando listas de distancias
    def load_problem(
        id_customers, 
        request_customers, 
        axis_x_customers, 
        axis_y_customers, 
        id_depots, 
        axis_x_depots, 
        axis_y_depots, 
        count_vehicles, 
        capacity_vehicles, 
        cost_matrix
    ) -> bool:
            
        loaded = False

        print("ENTRADA A LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")
        print(f"CANTIDAD DE CLIENTES: {len(id_customers)}")
        print("-------------------------------------------------------------------------------")
        for i in range(len(id_customers)):
            print(f"ID CLIENTE: {id_customers[i]}")
            print(f"DEMANDA : {request_customers[i]}")
            print(f"X : {axis_x_customers[i]}")
            print(f"Y : {axis_y_customers[i]}")

        print(f"CANTIDAD DE DEPÓSITOS: {len(id_depots)}")
        print("-------------------------------------------------------------------------------")
        for i in range(len(id_depots)):
            print(f"ID DEPÓSITO: {id_depots[i]}")
            print(f"X : {axis_x_depots[i]}")
            print(f"Y : {axis_y_depots[i]}")
            print(f"CANTIDAD DE FLOTAS DEL DEPÓSITO: {len(count_vehicles[i])}")
            for j in range(len(count_vehicles[i])):
                print(f"CANTIDAD DE VEHÍCULOS: {count_vehicles[i][j]}")
                print(f"CAPACIDAD DE LOS VEHÍCULOS: {capacity_vehicles[i][j]}")
            print("-------------------------------------------------------------------------------")

        if (
            id_customers and request_customers and axis_x_customers and axis_y_customers and
            id_depots and axis_x_depots and axis_y_depots and count_vehicles and capacity_vehicles and
            len(cost_matrix) == (len(id_customers) + len(id_depots)) and 
            all(len(row) == (len(id_customers) + len(id_depots)) for row in cost_matrix)
        ):
            problem = Problem.get_problem()
            problem.load_customer(id_customers, request_customers, axis_x_customers, axis_y_customers)
            problem.load_depot(id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles)

            if problem.get_total_capacity() >= problem.get_total_request():
                loaded = True
                problem.set_cost_matrix(cost_matrix)

        print("-------------------------------------------------------------------------------")
        print(f"DEMANDA TOTAL DE LOS CLIENTES: {problem.get_total_request()}")
        print(f"CAPACIDAD TOTAL DE LOS DEPÓSITOS: {problem.get_total_capacity()}")
        print("-------------------------------------------------------------------------------")
        print(f"CARGA EXITOSA: {loaded}")
        print("FIN DE LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")
        
        return loaded

    # Método encargado de cargar los datos del problema (incluido las coordenadas) usando el tipo de distancias
    def load_problem(
        id_customers, 
        request_customers, 
        axis_x_customers, 
        axis_y_customers, 
        id_depots, 
        axis_x_depots, 
        axis_y_depots, 
        count_vehicles, 
        capacity_vehicles, 
        distance_type
    ) -> bool:
        
        loaded = False
        
        print("ENTRADA A LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")
        print(f"CANTIDAD DE CLIENTES: {len(id_customers)}")
        print("-------------------------------------------------------------------------------")
        for i in range(len(id_customers)):
            print(f"ID CLIENTE: {id_customers[i]}")
            print(f"DEMANDA : {request_customers[i]}")
            print(f"X : {axis_x_customers[i]}")
            print(f"Y : {axis_y_customers[i]}")

        print(f"CANTIDAD DE DEPÓSITOS: {len(id_depots)}")
        print("-------------------------------------------------------------------------------")

        for i in range(len(id_depots)):
            print(f"ID DEPÓSITO: {id_depots[i]}")
            print(f"X : {axis_x_depots[i]}")
            print(f"Y : {axis_y_depots[i]}")
            print(f"CANTIDAD DE FLOTAS DEL DEPÓSITO: {len(count_vehicles[i])}")

            total_vehicles = 0
            capacity_vehicle = 0.0

            for j in range(len(count_vehicles[i])):
                total_vehicles = count_vehicles[i][j]
                capacity_vehicle = capacity_vehicles[i][j]
                print(f"CANTIDAD DE VEHÍCULOS: {total_vehicles}")
                print(f"CAPACIDAD DE LOS VEHÍCULOS: {capacity_vehicle}")

            print(f"CAPACIDAD TOTAL DEL DEPÓSITO: {total_vehicles * capacity_vehicle}")
            print("-------------------------------------------------------------------------------")

        if (
            id_customers and request_customers and axis_x_customers and axis_y_customers and
            id_depots and axis_x_depots and axis_y_depots and count_vehicles and capacity_vehicles
        ):
            problem = Problem.get_problem()
            problem.load_customer(id_customers, request_customers, axis_x_customers, axis_y_customers)
            problem.load_depot(id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles)

            if problem.get_total_capacity() >= problem.get_total_request():
                loaded = True
                problem.fill_cost_matrix(distance_type)

        print("-------------------------------------------------------------------------------")
        print(f"DEMANDA TOTAL DE LOS CLIENTES: {Problem.get_problem().get_total_request()}")
        print(f"CAPACIDAD TOTAL DE LOS DEPÓSITOS: {Problem.get_problem().get_total_capacity()}")
        print("-------------------------------------------------------------------------------")
        print(f"CARGA EXITOSA: {loaded}")
        print("FIN DE LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")

        return loaded
    
    # Método encargado de ejecutar la heurística de asignación
    def execute_assignment(self, assignment_type):
        
        assignment = self.new_assignment(assignment_type)
        
        print("EJECUCIÓN DE LA HEURÍSTICA")
        print("-------------------------------------------------------------------------------")
        print(f"HEURÍSTICA: {assignment_type.name}")
        print("-------------------------------------------------------------------------------")
        
        if assignment_type in [
            AssignmentType.NearestByCustomer,
            AssignmentType.SequentialCyclic,
            AssignmentType.CyclicAssignment,
            AssignmentType.KMEANS,
            AssignmentType.CoefficientPropagation,
            AssignmentType.NearestByDepot,
            AssignmentType.RandomSequentialNearestByDepot,
        ]:
            if self.order_type == OrderType.ASCENDENT:
                Tools.ascendent_ordenate()
            elif self.order_type == OrderType.DESCENDENT:
                Tools.descendent_ordenate()
            elif self.order_type == OrderType.RANDOM:
                Tools.random_ordenate()
        
        self.solution = assignment.to_clustering()
        
        print("-------------------------------------------------------------------------------")
        print("SOLUTION:")
        print(f"CANTIDAD DE CLUSTERS: {len(self.solution.get_clusters())}")
        print("-------------------------------------------------------------------------------")
        
        for cluster in self.solution.get_clusters():
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"TOTAL DE ELEMENTOS DEL CLUSTER: {len(cluster.get_items_of_cluster())}")
            print("-------------------------------------------------------------------------------")
            for item in cluster.get_items_of_cluster():
                print(f"ID DEL ELEMENTO: {int(item)}")
            print("-------------------------------------------------------------------------------")
        
        print(f"TOTAL DE CLIENTES NO ASIGNADOS: {self.solution.get_total_unassigned_items()}")
        if self.solution.get_total_unassigned_items() > 0:
            print("CLIENTES NO ASIGNADOS:")
            print("-------------------------------------------------------------------------------")
            for item in self.solution.get_unassigned_items():
                print(f"ID DEL ELEMENTO NO ASIGNADO: {int(item)}")
            print("-------------------------------------------------------------------------------")
        
        id_dep_without_cust = self.get_depots_without_customers()
        print(f"TOTAL DE DEPÓSITOS SIN ASIGNACIÓN DE CLIENTES: {len(id_dep_without_cust)}")
        if id_dep_without_cust:
            print("DEPÓSITOS SIN ASIGNACIÓN DE CLIENTES:")
            print("-------------------------------------------------------------------------------")
            for depot_id in id_dep_without_cust:
                print(f"ID DEL DEPÓSITO: {int(depot_id)}")
            print("-------------------------------------------------------------------------------")
        
        # self.clean_controller()
        
    # Método encargado de crear una método de asignación
    def new_assignment(self, type_assignment):
        i_factory_assignment = FactoryAssignment()
        assignment = i_factory_assignment.create_assignment(type_assignment)
        return assignment

    # Método encargado de devolver la demanda cubierta para un depósito dado en la solución
    def request_for_depot(self, id_depot):
        request_depot = 0.0

        i = 0
        found = False

        while i < len(self.solution.get_clusters()) and not found:
            if self.solution.get_clusters()[i].get_id_cluster() == id_depot:
                request_depot = self.solution.get_clusters()[i].get_request_cluster()
                found = True
            else:
                i += 1

        return request_depot
    
    # Método encargado de devolver los depósitos a los que no se les asigno ningún cliente en la solución
    def get_depots_without_customers(self):
        id_depots = []

        total_depots = len(Problem.get_problem().get_depots())
        total_clusters = len(self.solution.get_clusters())

        if total_clusters < total_depots:
            for i in range(total_depots):
                j = 0
                found = False

                id_depot = Problem.get_problem().get_depots()[i].get_id_depot()

                while j < total_clusters and not found:
                    if self.solution.get_clusters()[j].get_id_cluster() == id_depot:
                        found = True
                    else:
                        j += 1

                if not found:
                    id_depots.append(id_depot)

        return id_depots

    # Método encargado de restaurar los parámetros globales de la clase Controller
    def clean_controller(self):
        self.solution.get_clusters().clear()
        self.solution.get_unassigned_items().clear()
        self.solution = None
    
    # Método encargado de destruir la instancia de la controladora
    @staticmethod
    def destroy_controller():
        global controller
        controller = None