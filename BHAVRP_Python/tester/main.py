import os
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

import time
from typing import List
from tester.load_file import LoadFile
from cujae.inf.citi.om.controller.controller import Controller
from cujae.inf.citi.om.factory.interfaces.assignment_type import AssignmentType
from cujae.inf.citi.om.service.distance_type import DistanceType
from cujae.inf.citi.om.problem.input.problem import Problem

def main():
    
    total_instances = 21      # p21 para reales
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

    if Controller.get_instance().load_problem(id_customers, request_customers, axis_x_customers, axis_y_customers, id_depots, axis_x_depots, axis_y_depots, count_vehicles, capacity_vehicles):
        avg_time = 0.0
        run_time = 0.0
        
        run = 1
            
        for k in range(run):
            start = time.time() * 1000
            
            j = 1
            
            if j == 0:
                Controller.get_instance().execute_assignment(AssignmentType.BestNearest)
            elif j == 1:
                Controller.get_instance().execute_assignment(AssignmentType.FarthestFirst)
            elif j == 2:
                Controller.get_instance().execute_assignment(AssignmentType.RandomByElement)
            elif j == 3:
                Controller.get_instance().execute_assignment(AssignmentType.Sweep)
            
        end = time.time() * 1000
        run_time = end - start
        avg_time += run_time
        
        print("-------------------------------------------------------------------------------")
        print("RESUMEN DE LA CARGA DE DATOS:")
        print(f"CLIENTES: {Problem.get_problem().get_total_customers()}")
        print(f"DEMANDA TOTAL DE LOS CLIENTES: {Problem.get_problem().get_total_request()}")
        print(f"DEPÓSITOS: {Problem.get_problem().get_total_depots()}")
        print(f"CAPACIDAD TOTAL DE LOS DEPÓSITOS: {Problem.get_problem().get_total_capacity()}")
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
                
        selected_customers = Problem.get_problem().get_customers()[:10]
        selected_depots = Problem.get_problem().get_depots()[:2]

        euclidean_matrix = Problem.get_problem().fill_cost_matrix_test(selected_customers, selected_depots, DistanceType.EUCLIDEAN)
        haversine_matrix = Problem.get_problem().fill_cost_matrix_test(selected_customers, selected_depots, DistanceType.HAVERSINE)
        manhattan_matrix = Problem.get_problem().fill_cost_matrix_test(selected_customers, selected_depots, DistanceType.MANHATTAN)
        chebyshev_matrix = Problem.get_problem().fill_cost_matrix_test(selected_customers, selected_depots, DistanceType.CHEBYSHEV)
        if total_instances == 21:
            real_matrix = Problem.get_problem().fill_cost_matrix_test_real(selected_customers, selected_depots)

        if total_instances == 1:
            print("-------------------------------------------------------------------------------")
            print("MATRIZ DE COSTOS - EUCLIDEAN:")
            print_matrix(euclidean_matrix)
            print("-------------------------------------------------------------------------------")
            print("MATRIZ DE COSTOS - HAVERSINE:")
            print_matrix(haversine_matrix)
            print("-------------------------------------------------------------------------------")
            print("MATRIZ DE COSTOS - MANHATTAN:")
            print_matrix(manhattan_matrix)
            print("-------------------------------------------------------------------------------")
            print("MATRIZ DE COSTOS - CHEBYSHEV:")
            print_matrix(chebyshev_matrix)
        else:
            print("-------------------------------------------------------------------------------")
            print("MATRIZ DE COSTOS - REAL:")
            print_matrix(real_matrix)
        
        print("-------------------------------------------------------------------------------")
        print(f"NÚMERO DE EJECUCIONES: {run}")
        print()
        print(f"TIEMPO DE EJECUCIÓN TOTAL: {avg_time} ms")
        print(f"TIEMPO DE EJECUCIÓN TOTAL: {avg_time / 1000:.2f} s")
        print()
        print(f"TIEMPO DE EJECUCIÓN PROMEDIO: {avg_time / run:.2f} ms")
        print(f"TIEMPO DE EJECUCIÓN PROMEDIO: {avg_time / run / 1000:.2f} s")
        print("-------------------------------------------------------------------------------")

    Problem.get_problem().clean_info_problem()
        
def print_matrix(matrix):
    for i, row in enumerate(matrix):
        if i < 10:
            print(f"C{i+1}", end=" ")
        else:
            print(f"D{i-9}", end=" ")
        print(" ".join(f"{val:.2f}" for val in row))
    
if __name__ == "__main__":
    main()                