import os
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

import time
from typing import List
from tester.load_file import LoadFile
from tester.save_file import SaveFile
from cujae.inf.citi.om.controller.controller import Controller
from cujae.inf.citi.om.factory.interfaces.assignment_type import AssignmentType
from cujae.inf.citi.om.problem.input.problem import Problem
from cujae.inf.citi.om.problem.output.solution import Solution

def main():
    solution: Solution = None
    
    base_path = r"C:\Users\eriza\Downloads\Tesis\Implementacion\BHAVRP"
    
    path_file_end = os.path.join(base_path, "BHAVRP_Python", "result", "INSTANCIA_P05.xlsx")
    SaveFile.get_save_file().create_result_file(path_file_end)
    
    total_instances = 5
    load = LoadFile()
    
    instance_path = os.path.join(base_path, "BHAVRP_Java", "BHAVRPv2.0", "BHAVRP2.0", "C-mdvrp", f"p{total_instances}")
    load.load_file(instance_path)
    
    heuristic: AssignmentType = None
    
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

    if Controller.get_instance().load_problem(id_customers, request_customers, axis_x_customers, axis_y_customers, id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles):
        avg_time = 0.0
        run_time = 0.0

        run = 20
        heuristics = list(AssignmentType)
        execution_times = []
        unassigned_customers = []
        depots_without_cust = []
        
        for heuristic in heuristics:
            heuristic_total_time = 0.0
            
            for j in range(run):
                start = time.perf_counter()
                    
                solution = Controller.get_instance().execute_assignment(heuristic)
                    
                end = time.perf_counter()
                run_time = end - start
                heuristic_total_time += run_time  
                
                execution_times.append(run_time)
                unassigned_customers.append(solution.get_total_unassigned_items())
                depots_without_cust = Controller.get_instance().get_depots_without_customers(solution)

                print(f"\nTiempo de Ejecución {j + 1}: {run_time:.2f} ms\n")
            
            avg_time += heuristic_total_time

            SaveFile.save_results_to_excel(run, path_file_end, heuristic, execution_times, unassigned_customers, depots_without_cust)

            execution_times.clear()
            unassigned_customers.clear()
            depots_without_cust.clear()
        
        print("-------------------------------------------------------------------------------")
        print(f"Instancia ejecutada: p{total_instances}")
        print(f"Heurística: {heuristic.name}")
        print("Resultados:")
        print(f"\nNúmero de Ejecuciones: {run}\n")
        print(f"Tiempo de Ejecución Total: {avg_time:.2f} ms")
        print(f"Tiempo de Ejecución Total: {avg_time / 1000:.2f} s\n")
        print(f"Tiempo de Ejecución Promedio: {avg_time / run:.2f} ms")
        print(f"Tiempo de Ejecución Promedio: {avg_time / (run * 1000):.2f} s")
        print("-------------------------------------------------------------------------------")

    Problem.get_problem().clean_info_problem()

if __name__ == "__main__":
    main()