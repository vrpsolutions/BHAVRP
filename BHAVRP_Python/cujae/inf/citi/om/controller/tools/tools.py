import random
import numpy as np
from typing import List
from ...assignment.assignment import Assignment
from ...service.distance_type import DistanceType
from ...problem.input.problem import Problem
from ...problem.input.customer import Customer

class Tools:
        
    @staticmethod
    def random_ordenate():
        random.shuffle(Problem.get_problem().get_depots())
        distance_type: DistanceType = Assignment.distance_type 
        
        copy_cost_matrix = Assignment.initialize_cost_matrix(Problem.get_problem().get_customers(), copy_depots, distance_type)
        total_depots = Problem.get_problem().get_total_depots()
        total_customers = Problem.get_problem().get_total_customers()
        
        for _ in range(total_depots):
            copy_cost_matrix = np.delete(copy_cost_matrix, total_customers, axis=1)
        
        copy_depots = list(Problem.get_problem().get_depots())
        
        for j in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().depots[j].id_depot)
            col_index = total_customers + pos_depot 
            while col_index >= copy_cost_matrix.shape[1]:
                new_col = np.zeros((copy_cost_matrix.shape[0], 1))
                copy_cost_matrix = np.hstack((copy_cost_matrix, new_col))
            new_col = Problem.get_problem().cost_matrix[:, col_index].reshape(-1, 1)
            copy_cost_matrix = np.hstack((copy_cost_matrix, new_col))

        for _ in range(total_depots):
            copy_cost_matrix = np.delete(copy_cost_matrix, total_customers, axis=0)
        
        for k in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().depots[k].id_depot)
            row_index = total_customers + pos_depot 
            if row_index < copy_cost_matrix.shape[0]:
                new_row = copy_cost_matrix[row_index, :].reshape(1, -1) 
                copy_cost_matrix = np.vstack((copy_cost_matrix, new_row))
            else:
                new_row = copy_cost_matrix[-1, :].reshape(1, -1)
                copy_cost_matrix = np.vstack((copy_cost_matrix, new_row)) 

        Problem.get_problem().set_cost_matrix(copy_cost_matrix)
        
    @staticmethod
    def descendent_ordenate():
        copy_depots = list(Problem.get_problem().get_depots())
        total_depots = len(copy_depots)
        distance_type: DistanceType = Assignment.distance_type    
            
        for i in range(total_depots - 1):
            for j in range(total_depots - i - 1):
                if Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().depots[j + 1]) > Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().depots[j]):
                    Problem.get_problem().depots[j], Problem.get_problem().depots[j + 1] = Problem.get_problem().depots[j + 1], Problem.get_problem().depots[j]
            
        copy_cost_matrix = Assignment.initialize_cost_matrix(Problem.get_problem().get_customers(), copy_depots, distance_type)
        total_customers = Problem.get_problem().get_total_customers()
            
        for _ in range(total_depots):
            if total_customers < copy_cost_matrix.shape[1]:
                copy_cost_matrix = np.delete(copy_cost_matrix, total_customers - 1, axis=1)
            
        for j in range(total_depots):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().get_depots()[j].get_id_depot())
            col_index = total_customers + pos_depot - 1
            while col_index >= copy_cost_matrix.shape[1]:
                new_col = np.zeros((copy_cost_matrix.shape[0], 1))  # Nueva columna con ceros
                copy_cost_matrix = np.hstack((copy_cost_matrix, new_col))
            new_col = copy_cost_matrix[:, col_index].reshape(-1, 1)
            copy_cost_matrix = np.hstack((copy_cost_matrix, new_col))

        for _ in range(total_depots):
            if total_customers < copy_cost_matrix.shape[0]:
                copy_cost_matrix = np.delete(copy_cost_matrix, total_customers - 1, axis=0)
            
        for k in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().get_depots()[k].get_id_depot())
            row_index = total_customers + pos_depot - 1
            if row_index < copy_cost_matrix.shape[0]:
                new_row = copy_cost_matrix[row_index, :].reshape(1, -1)
                copy_cost_matrix = np.vstack((copy_cost_matrix, new_row))
            else:
                new_row = copy_cost_matrix[-1, :].reshape(1, -1)
                copy_cost_matrix = np.vstack((copy_cost_matrix, new_row))
                
        Problem.get_problem().set_cost_matrix(copy_cost_matrix)

    @staticmethod
    def ascendent_ordenate():
        copy_depots = list(Problem.get_problem().get_depots())
        total_depots = len(copy_depots)
        distance_type: DistanceType = Assignment.distance_type
            
        for i in range(total_depots - 1):
            for j in range(total_depots - i - 1):
                if Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().depots[j + 1]) < Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().depots[j]):
                    Problem.get_problem().depots[j], Problem.get_problem().depots[j + 1] = Problem.get_problem().depots[j + 1], Problem.get_problem().depots[j]
            
        copy_cost_matrix = Assignment.initialize_cost_matrix(Problem.get_problem().get_customers(), copy_depots, distance_type)
        total_customers = Problem.get_problem().get_total_customers()
            
        for _ in range(total_depots):
            copy_cost_matrix = np.delete(copy_cost_matrix, total_customers, axis=1)
            
        for j in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().get_depots()[j].get_id_depot())
            col_index = total_customers + pos_depot 
            while col_index >= copy_cost_matrix.shape[1]:
                new_col = np.zeros((copy_cost_matrix.shape[0], 1))
                copy_cost_matrix = np.hstack((copy_cost_matrix, new_col))
            new_col = Problem.get_problem().cost_matrix[:, col_index].reshape(-1, 1)
            copy_cost_matrix = np.hstack((copy_cost_matrix, new_col))
            
        for _ in range(total_depots):
            copy_cost_matrix = np.delete(copy_cost_matrix, total_customers, axis=0)
            
        for k in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().get_depots()[k].get_id_depot())
            row_index = total_customers + pos_depot
            if row_index < copy_cost_matrix.shape[0]:
                new_row = copy_cost_matrix[row_index, :].reshape(1, -1)
                copy_cost_matrix = np.vstack((copy_cost_matrix, new_row))
            else:
                new_row = copy_cost_matrix[-1, :].reshape(1, -1)
                copy_cost_matrix = np.vstack((copy_cost_matrix, new_row))
                
        Problem.get_problem().set_cost_matrix(copy_cost_matrix)

    @staticmethod
    def random_ordenate_customers(customers: List[Customer]):
        random.shuffle(customers)

    @staticmethod
    def descendent_ordenate_customers(customers: List[Customer]):
        customers.sort(key=lambda c: Problem.get_problem().get_request_by_id_customer(c.id_customer), reverse=True)

    @staticmethod
    def ascendent_ordenate_customers(customers: List[Customer]):
        customers.sort(key=lambda c: Problem.get_problem().get_request_by_id_customer(c.id_customer))

    @staticmethod
    def truncate_double(number: float, decimal_place: int) -> float:
        return round(number, decimal_place)