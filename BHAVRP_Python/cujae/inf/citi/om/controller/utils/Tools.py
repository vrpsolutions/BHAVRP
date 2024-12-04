from typing import List
from ...problem.input.problem import Problem
from ...problem.input.customer import Customer
import random
import numpy

class Tools:
    
    @staticmethod
    def random_ordenate():
        random.shuffle(Problem.get_problem().depots)
        
        copy_cost_matrix = Problem.get_problem().cost_matrix.copy()
        total_depots = Problem.get_problem().get_total_depots()
        total_customers = Problem.get_problem().get_total_customers()
        
        for _ in range(total_depots):
            copy_cost_matrix = numpy.delete(copy_cost_matrix, total_customers, axis=1)
        
        copy_depots = Problem.get_problem().depots.copy()
        
        for j in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().depots[j].id_depot)
            new_col = Problem.get_problem().cost_matrix[:, total_customers + pos_depot].reshape(-1, 1)
            copy_cost_matrix = numpy.hstack((copy_cost_matrix, new_col))
        
        for _ in range(total_depots):
            copy_cost_matrix = numpy.delete(copy_cost_matrix, total_customers, axis=0)
        
        for k in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().depots[k].id_depot)
            new_row = Problem.get_problem().cost_matrix[total_customers + pos_depot, :].reshape(1, -1)
            copy_cost_matrix = numpy.vstack((copy_cost_matrix, new_row))
        
        Problem.get_problem().set_cost_matrix(copy_cost_matrix)
        
    @staticmethod
    def descendent_ordenate():
        copy_depots = Problem.get_problem().depots.copy()
        total_depots = Problem.get_problem().get_total_depots()
            
        for i in range(total_depots - 1):
            for j in range(total_depots - i - 1):
                if Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().depots[j + 1]) > Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().depots[j]):
                    Problem.get_problem().depots[j], Problem.get_problem().depots[j + 1] = Problem.get_problem().depots[j + 1], Problem.get_problem().depots[j]
            
        copy_cost_matrix = Problem.get_problem().cost_matrix.copy()
        total_customers = Problem.get_problem().get_total_customers()
            
        for _ in range(total_depots):
            copy_cost_matrix = numpy.delete(copy_cost_matrix, total_customers, axis=1)
            
        for j in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().depots[j].id_depot)
            new_col = Problem.get_problem().cost_matrix[:, total_customers + pos_depot].reshape(-1, 1)
            copy_cost_matrix = numpy.hstack((copy_cost_matrix, new_col))
                
        for _ in range(total_depots):
            copy_cost_matrix = numpy.delete(copy_cost_matrix, total_customers, axis=0)
            
        for k in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().depots[k].id_depot)
            new_row = Problem.get_problem().cost_matrix[total_customers + pos_depot, :].reshape(1, -1)
            copy_cost_matrix = numpy.vstack((copy_cost_matrix, new_row))
            
        Problem.get_problem().set_cost_matrix(copy_cost_matrix)

    @staticmethod
    def ascendent_ordenate():
        copy_depots = Problem.get_problem().depots.copy()
        total_depots = Problem.get_problem().get_total_depots()
            
        for i in range(total_depots - 1):
            for j in range(total_depots - i - 1):
                if Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().depots[j + 1]) < Problem.get_problem().get_total_capacity_by_depot(Problem.get_problem().depots[j]):
                    Problem.get_problem().depots[j], Problem.get_problem().depots[j + 1] = Problem.get_problem().depots[j + 1], Problem.get_problem().depots[j]
            
        copy_cost_matrix = Problem.get_problem().cost_matrix.copy()
        total_customers = Problem.get_problem().get_total_customers()
            
        for _ in range(total_depots):
            copy_cost_matrix = numpy.delete(copy_cost_matrix, total_customers, axis=1)
            
        for j in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().depots[j].id_depot)
            new_col = Problem.get_problem().cost_matrix[:, total_customers + pos_depot].reshape(-1, 1)
            copy_cost_matrix = numpy.hstack((copy_cost_matrix, new_col))
                
        for _ in range(total_depots):
            copy_cost_matrix = numpy.delete(copy_cost_matrix, total_customers, axis=0)
            
        for k in range(len(copy_depots)):
            pos_depot = Problem.get_problem().find_pos_depot(copy_depots, Problem.get_problem().depots[k].id_depot)
            new_row = Problem.get_problem().cost_matrix[total_customers + pos_depot, :].reshape(1, -1)
            copy_cost_matrix = numpy.vstack((copy_cost_matrix, new_row))
            
        Problem.get_problem().set_cost_matrix(copy_cost_matrix)

    @staticmethod
    def random_ordenate_customers(customers: List['Customer']):
        random.shuffle(customers)

    @staticmethod
    def descendent_ordenate_customers(customers: List['Customer']):
        customers.sort(key=lambda c: Problem.get_problem().get_request_by_id_customer(c.id_customer), reverse=True)

    @staticmethod
    def ascendent_ordenate_customers(customers: List['Customer']):
        customers.sort(key=lambda c: Problem.get_problem().get_request_by_id_customer(c.id_customer))

    @staticmethod
    def truncate_double(number: float, decimal_place: int) -> float:
        return round(number, decimal_place)