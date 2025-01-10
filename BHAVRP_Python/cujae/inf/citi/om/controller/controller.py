from typing import List
from .tools.tools import Tools
from .tools.order_type import OrderType
from ..assignment.assignment import Assignment;
from ..factory.methods.factory_assignment import FactoryAssignment
from ..factory.interfaces.assignment_type import AssignmentType
from ..factory.interfaces.ifactory_assignment import IFactoryAssignment
from ..problem.input.problem import Problem
from ..problem.output.solution import Solution

class Controller:
    _instance = None   # Atributo para la instancia Singleton
    order_type = OrderType.INPUT  # Instancia de OrderType
    distance_type = Assignment.distance_type
    
    def __init__(self):
        if Controller._instance is not None:
            raise Exception("This class is a Singleton! Use get_instance() instead.")
        self.solution = None
        
    # Método Singleton para obtener la única instancia de la clase controladora.  
    @staticmethod
    def get_instance():
        if Controller._instance is None:
            Controller._instance = Controller()
        return Controller._instance

    def get_solution(self) -> Solution:
        return self.solution
    
    def set_solution(self, solution: Solution):
        self.solution = solution
         
    # Método encargado de cargar los datos del problema (incluido las coordenadas) usando listas de distancias
    def load_problem(
        self, 
        id_customers: List[int], 
        request_customers: List[float], 
        axis_x_customers: List[float], 
        axis_y_customers: List[float], 
        id_depots: List[int], 
        axis_x_depots: List[float], 
        axis_y_depots: List[float], 
        count_vehicles: List[List[int]], 
        capacity_vehicles: List[List[float]]
    ) -> bool:
        loaded: bool = False
        
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

        total_capacity = 0.0

        for i in range(len(id_depots)):
            print(f"ID DEPÓSITO: {id_depots[i]}")
            print(f"X : {axis_x_depots[i]}")
            print(f"Y : {axis_y_depots[i]}")

            print(f"CANTIDAD DE FLOTAS DEL DEPÓSITO: {len(count_vehicles[i])}")
            
            total_depot_capacity = 0.0
            
            for j in range(len(count_vehicles[i])):
                vehicles = count_vehicles[i][j]
                capacity = capacity_vehicles[i][j]
                total_depot_capacity += vehicles * capacity
                
                print(f"CANTIDAD DE VEHÍCULOS: {vehicles}")
                print(f"CAPACIDAD DE LOS VEHÍCULOS: {capacity}")

            print(f"CAPACIDAD TOTAL DEL DEPÓSITO: {total_depot_capacity}")
            total_capacity += total_depot_capacity
            print("-------------------------------------------------------------------------------")

        # Validar las condiciones necesarias para proceder con la carga
        if (id_customers and request_customers and axis_x_customers and axis_y_customers and
            id_depots and axis_x_depots and axis_y_depots and count_vehicles and
            capacity_vehicles):
            
            Problem.get_problem().load_customer(id_customers, request_customers, axis_x_customers, axis_y_customers)
            Problem.get_problem().load_depot(id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles)         
            loaded = True
            
        print(f"DEMANDA TOTAL DE LOS CLIENTES: {Problem.get_problem().get_total_request()}")
        print(f"CAPACIDAD TOTAL DE LOS DEPÓSITOS: {total_capacity}")
        print("-------------------------------------------------------------------------------")
        print(f"CARGA EXITOSA: {loaded}")
        print("FIN DE LA CARGA DE DATOS")
        print("-------------------------------------------------------------------------------")

        return loaded
    
    # Método encargado de ejecutar la heurística de asignación
    def execute_assignment(self, assignment_type: AssignmentType):
                
        assignment: Assignment = self.new_assignment(assignment_type)
        
        print("EJECUCIÓN DE LA HEURÍSTICA")
        print("-------------------------------------------------------------------------------")
        print(f"HEURÍSTICA: {assignment_type.name}")
        print("-------------------------------------------------------------------------------")
        
        if assignment_type in [
            AssignmentType.NearestByCustomer,
            AssignmentType.SequentialCyclic,
            AssignmentType.CyclicAssignment,
            AssignmentType.Kmeans,
            AssignmentType.CoefficientPropagation,
            AssignmentType.NearestByDepot
        ]:
            if self.order_type == OrderType.ASCENDENT:
                Tools.ascendent_ordenate()
            elif self.order_type == OrderType.DESCENDENT or self.order_type == OrderType.INPUT:
                Tools.descendent_ordenate()
            elif self.order_type == OrderType.RANDOM:
                Tools.random_ordenate()
        
        solution: Solution = assignment.to_clustering()
        
        print("-------------------------------------------------------------------------------")
        print("SOLUTION:")
        print(f"CANTIDAD DE CLUSTERS: {len(solution.get_clusters())}")
        print("-------------------------------------------------------------------------------")
        
        for cluster in solution.get_clusters():
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"TOTAL DE ELEMENTOS DEL CLUSTER: {len(cluster.get_items_of_cluster())}")
            print("-------------------------------------------------------------------------------")
            
            for item in cluster.get_items_of_cluster():
                print(f"ID DEL ELEMENTO: {int(item)}")
            print("-------------------------------------------------------------------------------")
        
        print(f"TOTAL DE CLIENTES NO ASIGNADOS: {solution.get_total_unassigned_items()}")
        
        if solution.get_total_unassigned_items() > 0:
            print("CLIENTES NO ASIGNADOS:")
            print("-------------------------------------------------------------------------------")
            
            for item in solution.get_unassigned_items():
                print(f"ID DEL ELEMENTO NO ASIGNADO: {int(item)}")
            print("-------------------------------------------------------------------------------")
        
        id_dep_without_cust = self.get_depots_without_customers(solution)
        
        print(f"TOTAL DE DEPÓSITOS SIN ASIGNACIÓN DE CLIENTES: {len(id_dep_without_cust)}")
        
        if id_dep_without_cust:
            print("DEPÓSITOS SIN ASIGNACIÓN DE CLIENTES:")
            print("-------------------------------------------------------------------------------")
            
            for depot_id in id_dep_without_cust:
                print(f"ID DEL DEPÓSITO: {int(depot_id)}")
            print("-------------------------------------------------------------------------------")
        
        # self.clean_controller()
        
    # Método encargado de crear una método de asignación
    def new_assignment(self, type_assignment: AssignmentType) -> Assignment:
        i_factory_assignment: IFactoryAssignment = FactoryAssignment()
        assignment = i_factory_assignment.create_assignment(type_assignment)
        return assignment

    # Método encargado de devolver la demanda cubierta para un depósito dado en la solución
    def request_for_depot(self, id_depot: int) -> float:
        request_depot: float = 0.0

        i = 0
        found: bool = False

        while i < len(self.solution.get_clusters()) and not found:
            if self.solution.get_clusters()[i].get_id_cluster() == id_depot:
                request_depot = self.solution.get_clusters()[i].get_request_cluster()
                found = True
            else:
                i += 1

        return request_depot
    
    # Método encargado de devolver los depósitos a los que no se les asigno ningún cliente en la solución
    def get_depots_without_customers(self, solution: Solution) -> List[int]:
        id_depots: List[int] = []

        total_depots = len(Problem.get_problem().get_depots())
        total_clusters = len(solution.get_clusters())

        if total_clusters < total_depots:
            for i in range(total_depots):
                j = 0
                found = False

                id_depot = Problem.get_problem().get_depots()[i].get_id_depot()

                while j < total_clusters and not found:
                    if solution.get_clusters()[j].get_id_cluster() == id_depot:
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
        Controller._instance = None