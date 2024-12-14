from typing import List

class LoadFile:
    
    def __init__(self):
        self.instance_file = []
        
    def get_instance_file(self):
        return self.instance_file

    def set_instance_file(self, instance_file):
        self.instance_file = instance_file

    def find_end_element(self, lines):
        return "EOF" in lines
    
    def load_file(self, path_file) -> bool:
        load = False
        self.instance_file = []
        
        with open(path_file, 'r') as file:
            cad = ''
            while not self.find_end_element(cad):
                cad = file.readline()
                if cad:
                    self.instance_file.append(cad.strip())
                    load = True
                else:
                    load = False
                    break

        return load
    
    """
    def load_file(self, path_file) -> bool:
    load = False
    self.instance_file = []
    
    try:
        with open(path_file, 'r') as file:
            cad = ''
            while cad.strip() != "EOF":
                cad = file.readline()
                if cad:
                    self.instance_file.append(cad.strip())
                    load = True
                else:
                    load = False
                    break
    except Exception as e:
        print(f"Error loading file: {e}")
        load = False

    return load
    """
    
    def load_count_vehicles_for_depot(self, count_vehicles: List[List[int]]):
        first_line_tokens = self.instance_file[0].split()
        total_vehicles = int(first_line_tokens[0])
        total_depots = self.load_total_depots()

        count_fleet = [total_vehicles]
        
        for _ in range(total_depots):
            count_vehicles.append(count_fleet)
            
    def load_count_vehicles_for_depot_x(self, count_vehicles: List[List[int]]):
        total_depots = self.load_total_depots()
        starting_index = len(self.instance_file) - total_depots
        
        if starting_index < 0:
            print("Error: Starting index for depots is out of bounds")
            return

        for i in range(starting_index, len(self.instance_file)):
            line_tokens = self.instance_file[i].split()
            total_vehicles = int(line_tokens[0])

            count_fleet = [total_vehicles]
            count_vehicles.append(count_fleet)
            
    def load_total_customers(self):
        first_line_tokens = self.instance_file[0].split()
        return int(first_line_tokens[1])

    def load_total_depots(self):
        first_line_tokens = self.instance_file[0].split()
        return int(first_line_tokens[2])

    def load_capacity_vehicles(self, capacity_vehicles: List[List[float]]):
        total_depots = self.load_total_depots()

        for i in range(1, total_depots + 1):
            try:
                line_tokens = self.instance_file[i].split()
                capacity_fleet = [float(line_tokens[0])]
                capacity_vehicles.append(capacity_fleet)
            except ValueError:
                print(f"Error: Invalid data at line {i}")
            
    def load_customers(self, id_customers: List[int], axis_x_customers: List[float], axis_y_customers: List[float], request_customers: List[float]):
        total_customers = self.load_total_customers()
        total_depots = self.load_total_depots()

        for i in range(total_depots + 1, total_customers + total_depots + 1):
            line_tokens = self.instance_file[i].split()
            id_customers.append(int(line_tokens[0]))
            axis_x_customers.append(float(line_tokens[1]))
            axis_y_customers.append(float(line_tokens[2]))
            request_customers.append(1.0)

    def load_depots(self, id_depots: List[int], axis_x_depots: List[float], axis_y_depots: List[float]):
        total_customers = self.load_total_customers()
        total_depots = self.load_total_depots()

        for i in range(total_depots + total_customers + 1, len(self.instance_file)):
            line_tokens = self.instance_file[i].split()
            id_depots.append(int(line_tokens[0]))
            axis_x_depots.append(float(line_tokens[1]))
            axis_y_depots.append(float(line_tokens[2]))
            
    def calculate_distance(self, axis_x_start: float, axis_y_start: float, axis_x_end: float, axis_y_end: float) -> float:
        axis_x = (axis_x_start - axis_x_end) ** 2
        axis_y = (axis_y_start - axis_y_end) ** 2
        return (axis_x + axis_y) ** 0.5

    def fill_list_distances(self, id_customers: List[int], axis_x_customers: List[float], axis_y_customers: List[float], id_depots: List[int], axis_x_depots: List[float], axis_y_depots: List[float], list_distances: List[List[float]]):
        total_customers = len(id_customers)
        total_depots = len(id_depots)

        for i in range(total_customers):
            distances_from_customers = []

            for j in range(total_customers):
                distances_from_customers.append(self.calculate_distance(axis_x_customers[j], axis_y_customers[j], axis_x_customers[i], axis_y_customers[i]))

            for k in range(total_depots):
                distances_from_customers.append(self.calculate_distance(axis_x_depots[k], axis_y_depots[k], axis_x_customers[i], axis_y_customers[i]))

            list_distances.append(distances_from_customers)

        for i in range(total_depots):
            distances_from_depots = []

            for j in range(total_customers):
                distances_from_depots.append(self.calculate_distance(axis_x_customers[j], axis_y_customers[j], axis_x_depots[i], axis_y_depots[i]))

            for k in range(total_depots):
                distances_from_depots.append(self.calculate_distance(axis_x_depots[k], axis_y_depots[k], axis_x_depots[i], axis_y_depots[i]))

            list_distances.append(distances_from_depots)