import os
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

import time
from typing import List
from tester.load_file import LoadFile
from cujae.inf.citi.om.controller.controller import Controller
from cujae.inf.citi.om.factory.interfaces.assignment_type import AssignmentType
from cujae.inf.citi.om.problem.input.problem import Problem

def main():
    
    total_instances = 1      # p21 para reales, p500 instancia grande
    load = LoadFile()
    
    base_path = r"C:\Users\eriza\Downloads\PPII\Implementacion\BHAVRP"
    instance_path = os.path.join(base_path, "BHAVRP_Java", "BHAVRPv2.0", "BHAVRP2.0", "C-mdvrp", f"p{total_instances}")

    load.load_file(instance_path)

    print("-------------------------------------------------------------------------------")
    print(f"INSTANCIA: P{total_instances}")
    print("-------------------------------------------------------------------------------")

    id_customers: List[int] = []
    axis_x_customers: List[float] = []
    axis_y_customers: List[float] = []
    request_customers: List[float] = []

    id_depots: List[int] = []
    axis_x_depots: List[float] = []
    axis_y_depots: List[float] = []
    count_vehicles: List[List[int]] = []
    capacity_vehicles: List[List[float]] = []
        
    load.load_count_vehicles_for_depot(count_vehicles)
    load.load_capacity_vehicles(capacity_vehicles)
    load.load_customers(id_customers, axis_x_customers, axis_y_customers, request_customers)
    load.load_depots(id_depots, axis_x_depots, axis_y_depots)

    # Cargar el problema en el controlador
    if Controller.get_instance().load_problem(id_customers, request_customers, axis_x_customers, axis_y_customers, id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles):
        avg_time = 0.0
        run_time = 0.0
        
        run = 1
            
        for k in range(run):
            start = time.time() * 1000  # Convertir a milisegundos
                
            j = 22
                        
            if j == 0:
                Controller.get_instance().execute_assignment(AssignmentType.BestCyclicAssignment)
            elif j == 1:
                Controller.get_instance().execute_assignment(AssignmentType.BestNearest)
            elif j == 2:
                Controller.get_instance().execute_assignment(AssignmentType.Clara)
            elif j == 3:
                Controller.get_instance().execute_assignment(AssignmentType.CoefficientPropagation)
            elif j == 4:
                Controller.get_instance().execute_assignment(AssignmentType.CyclicAssignment)
            elif j == 5:
                Controller.get_instance().execute_assignment(AssignmentType.FarthestFirst)
            elif j == 6:
                Controller.get_instance().execute_assignment(AssignmentType.Kmeans)
            elif j == 7:
                #Controller.get_instance().execute_assignment(AssignmentType.Modified_KMEANS)
                pass
            elif j == 8:
                #Controller.get_instance().execute_assignment(AssignmentType.Modified_PAM)
                pass
            elif j == 9:
                Controller.get_instance().execute_assignment(AssignmentType.NearestByCustomer)
            elif j == 10:
                Controller.get_instance().execute_assignment(AssignmentType.NearestByDepot)
            elif j == 11:
                Controller.get_instance().execute_assignment(AssignmentType.PAM)
            elif j == 12:
                Controller.get_instance().execute_assignment(AssignmentType.Parallel)
                # Controller.get_instance().execute_assignment(AssignmentType.ParallelPlus)
            elif j == 13:
                Controller.get_instance().execute_assignment(AssignmentType.RandomByElement)
            elif j == 14:
                # Controller.get_instance().execute_assignment(AssignmentType.RandomNearestByCustomer)
                pass
            elif j == 15:
                # Controller.get_instance().execute_assignment(AssignmentType.RandomNearestByDepot)
                pass
            elif j == 16:
                Controller.get_instance().execute_assignment(AssignmentType.RandomSequentialCyclic)
            elif j == 17:
                # Controller.get_instance().execute_assignment(AssignmentType.RandomSequentialNearestByDepot)
                pass
            elif j == 18:
                # Controller.get_instance().execute_assignment(AssignmentType.ROCK)
                pass
            elif j == 19:
                Controller.get_instance().execute_assignment(AssignmentType.SequentialCyclic)
            elif j == 20:
                # Controller.get_instance().execute_assignment(AssignmentType.SequentialNearestByDepot)
                pass
            elif j == 21:
                Controller.get_instance().execute_assignment(AssignmentType.Simplified)
            elif j == 22:
                Controller.get_instance().execute_assignment(AssignmentType.Sweep)
            elif j == 23:
                Controller.get_instance().execute_assignment(AssignmentType.ThreeCriteriaClustering)
            elif j == 24:
                Controller.get_instance().execute_assignment(AssignmentType.UPGMC)
                
        # end = time.time()
        end = time.time() * 1000  # Convertir a milisegundos
        run_time = end - start
        avg_time += run_time
                
        #total_clusters = len(Controller.get_instance().get_solution().get_clusters())
           
        print("-------------------------------------------------------------------------------")
        print(f"Instancia ejecutada: p{total_instances}")
        print(f"Tiempo de Ejecución Total: {avg_time:.2f} ms")
        print(f"Tiempo de Ejecución Promedio: {avg_time / run:.2f} ms")
        print("-------------------------------------------------------------------------------")
        
    Problem.get_problem().clean_info_problem()
        
if __name__ == "__main__":
    main()