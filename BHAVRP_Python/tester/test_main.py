import os
import time
from load_file import LoadFile
from ..cujae.inf.citi.om.controller.controller import Controller
from ..cujae.inf.citi.om.factory.interfaces.assignment_type import AssignmentType
from ..cujae.inf.citi.om.problem.input.problem import Problem

def main():
    path_files = "C-mdvrp//p"
    total_instances = 500  # instancia 6, 10, 15 y 19
    load = LoadFile()
    
    for i in range(total_instances):
        load.load_file(path_files + str(i + 1))

        print("-------------------------------------------------------------------------------")
        print(f"INSTANCIA: P{i + 1}")
        print("-------------------------------------------------------------------------------")

        id_customers = []
        axis_x_customers = []
        axis_y_customers = []
        request_customers = []

        id_depots = []
        axis_x_depots = []
        axis_y_depots = []
        count_vehicles = []
        capacity_vehicles = []

        list_distances = []
        
        load.load_count_vehicles_for_depot(count_vehicles)
        load.load_capacity_vehicles(capacity_vehicles)
        load.load_customers(id_customers, axis_x_customers, axis_y_customers, request_customers)
        load.load_depots(id_depots, axis_x_depots, axis_y_depots)
        
        load.fill_list_distances(id_customers, axis_x_customers, axis_y_customers, id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles, list_distances)
        
        # Cargar el problema en el controlador
        if Controller.get_instance().load_problem(id_customers, request_customers, axis_x_customers, axis_y_customers, id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles, list_distances):
            avg_time = 0.0
            run_time = 0.0
            
            run = 1
            
            for k in range(run):
                start = time.time() * 1000  # Convertir a milisegundos
                
                j = 5  # 0, 7 y 23
                
                # for j in range(1, len(AssignmentType)):  # En caso de usar todos los tipos de asignación
                if j == 1:
                    Controller.get_instance().execute_assignment(AssignmentType.BestCyclicAssignment)
                elif j == 2:
                    Controller.get_instance().execute_assignment(AssignmentType.BestNearest)
                elif j == 3:
                    Controller.get_instance().execute_assignment(AssignmentType.CLARA)
                elif j == 4:
                    Controller.get_instance().execute_assignment(AssignmentType.CoefficientPropagation)
                elif j == 5:
                    Controller.get_instance().execute_assignment(AssignmentType.CyclicAssignment)
                elif j == 6:
                    Controller.get_instance().execute_assignment(AssignmentType.Farthest_First)
                elif j == 7:
                    Controller.get_instance().execute_assignment(AssignmentType.KMEANS)
                elif j == 8:
                    Controller.get_instance().execute_assignment(AssignmentType.Modified_KMEANS)
                elif j == 9:
                    Controller.get_instance().execute_assignment(AssignmentType.Modified_PAM)
                elif j == 10:
                    Controller.get_instance().execute_assignment(AssignmentType.NearestByCustomer)
                elif j == 11:
                    # Controller.get_instance().execute_assignment(AssignmentType.NearestByDepot)
                    pass
                elif j == 12:
                    Controller.get_instance().execute_assignment(AssignmentType.PAM)
                elif j == 13:
                    Controller.get_instance().execute_assignment(AssignmentType.Parallel)
                    # Controller.get_instance().execute_assignment(AssignmentType.ParallelPlus)
                elif j == 14:
                    Controller.get_instance().execute_assignment(AssignmentType.RandomByElement)
                elif j == 15:
                    # Controller.get_instance().execute_assignment(AssignmentType.RandomNearestByCustomer)
                    pass
                elif j == 16:
                    # Controller.get_instance().execute_assignment(AssignmentType.RandomNearestByDepot)
                    pass
                elif j == 17:
                    # Controller.get_instance().execute_assignment(AssignmentType.RandomSequentialCyclic)
                    pass
                elif j == 18:
                    # Controller.get_instance().execute_assignment(AssignmentType.RandomSequentialNearestByDepot)
                    pass
                elif j == 19:
                    # Controller.get_instance().execute_assignment(AssignmentType.ROCK)
                    pass
                elif j == 20:
                    Controller.get_instance().execute_assignment(AssignmentType.SequentialCyclic)
                elif j == 21:
                    # Controller.get_instance().execute_assignment(AssignmentType.SequentialNearestByDepot)
                    pass
                elif j == 22:
                    Controller.get_instance().execute_assignment(AssignmentType.Simplified)
                elif j == 23:
                    Controller.get_instance().execute_assignment(AssignmentType.Sweep)
                elif j == 24:
                    Controller.get_instance().execute_assignment(AssignmentType.ThreeCriteriaClustering)
                elif j == 25:
                    Controller.get_instance().execute_assignment(AssignmentType.UPGMC)
                
                # end = time.time()
                end = time.time() * 1000  # Convertir a milisegundos
                run_time = end - start
                avg_time += run_time
                
                total_clusters = len(Controller.get_instance().get_solution().get_clusters())
                
            print(f"Tiempo de Ejecución Promedio: {avg_time / run} ms")
        
        Problem.get_problem().clean_info_problem()
        
if __name__ == "__main__":
    main()