import random
from typing import List
from assignment import Assignment
from ...problem.input import Problem, Customer, Depot, Location
from ...problem.output.solution.cluster import Cluster

class Partitional(Assignment):
    
    def clean_clusters(self, clusters: List[Cluster]) -> None:
        for cluster in clusters:
            cluster.clean_cluster()
            
        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for i, cluster in enumerate(clusters):
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")
        
    def update_clusters(self, clusters: List[Cluster], id_elements: List[int]) -> None:
        for i in range(len(clusters)):
            clusters[i].get_items_of_cluster().append(id_elements[i])
            clusters[i].set_request_cluster(Problem.get_problem().get_request_by_id_customer(id_elements[i]))

        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for i, cluster in enumerate(clusters):
            print(f"ID CLUSTER: {cluster.get_id_cluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.get_request_cluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.get_items_of_cluster()}")
        print("--------------------------------------------------")
        
    def generate_elements(
        self, 
        seed_type: SeedType, 
        distance_type: DistanceType
    ) -> List[int]:
        
        id_elements = []
        total_customers = Problem.get_problem().get_total_customers()
        total_depots = Problem.get_problem().get_total_depots()
        counter = total_depots

        cost_matrix = None
        rc_best_all = RowCol()
        id_element = -1

        if distance_type in [DistanceType.TYPE_0, DistanceType.TYPE_1, DistanceType.TYPE_2, DistanceType.TYPE_3]:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix(Problem.get_problem().get_customers(),
                                                                     Problem.get_problem().get_depots(), distance_type)
            except Exception as e:
                print(e)

        elif distance_type == DistanceType.TYPE_4:
            cost_matrix = NumericMatrix(Problem.get_problem().get_cost_matrix())

        if distance_type == DistanceType.TYPE_0:
            id_elements = [-1] * counter
            print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")

            while counter > 0:
                rc_best_all = cost_matrix.index_bigger_value(total_customers, 0, total_customers + total_depots - 1,
                                                              total_customers - 1)

                print(f"ROW SELECCIONADA: {rc_best_all.get_row()}")
                print(f"COL SELECCIONADA: {rc_best_all.get_col()}")
                print(f"VALOR SELECCIONADO: {cost_matrix.get_item(rc_best_all.get_row(), rc_best_all.get_col())}")

                id_element = Problem.get_problem().get_customers()[rc_best_all.get_col()].get_id_customer()
                id_elements[rc_best_all.get_row() - total_customers] = id_element

                print(f"ELEMENTO: {id_element}")
                print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")

                cost_matrix.fill_value(total_customers, rc_best_all.get_col(), total_customers + total_depots - 1,
                                       rc_best_all.get_col(), float('-inf'))
                cost_matrix.fill_value(rc_best_all.get_row(), 0, rc_best_all.get_row(), total_customers + total_depots - 1,
                                       float('-inf'))
                counter -= 1

        elif distance_type == DistanceType.TYPE_1:
            id_elements = [-1] * counter
            print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")

            while counter > 0:
                rc_best_all = cost_matrix.index_lower_value(total_customers, 0, total_customers + total_depots - 1,
                                                            total_customers - 1)

                print(f"ROW SELECCIONADA: {rc_best_all.get_row()}")
                print(f"COL SELECCIONADA: {rc_best_all.get_col()}")
                print(f"VALOR SELECCIONADO: {cost_matrix.get_item(rc_best_all.get_row(), rc_best_all.get_col())}")

                id_element = Problem.get_problem().get_customers()[rc_best_all.get_col()].get_id_customer()
                id_elements[rc_best_all.get_row() - total_customers] = id_element

                print(f"ELEMENTO: {id_element}")
                print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")

                cost_matrix.fill_value(total_customers, rc_best_all.get_col(), total_customers + total_depots - 1,
                                       rc_best_all.get_col(), float('inf'))
                cost_matrix.fill_value(rc_best_all.get_row(), 0, rc_best_all.get_row(), total_customers + total_depots - 1,
                                       float('inf'))
                counter -= 1

        elif distance_type == DistanceType.TYPE_2:
            rdm = random.Random()

            while counter > 0:
                id_element = rdm.randint(1, total_customers)
                id_elements.append(id_element)

                print(f"ELEMENTO: {id_element}")
                print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")

                counter -= 1

        print("--------------------------------------------------")
        print("CENTROIDES/MEDOIDES INICIALES")
        print(id_elements)
        print("--------------------------------------------------")

        return id_elements
    
    def generate_elements(self, partition: List['Customer'], distance_type):
        id_elements = []

        total_customers = len(partition)
        total_depots = Problem.get_problem().get_total_depots()
        counter = total_depots

        cost_matrix = None
        rc_best_all = None
        id_element = -1

        # Switch case for distance_type
        if distance_type in [0, 1, 2, 3]:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix(partition, Problem.get_problem().get_depots(), distance_type)
            except Exception as e:
                print(e)

        elif distance_type == 4:
            # cost_matrix = NumericMatrix(InfoProblem.get_problem().get_cost_matrix())
            pass

        # Initialize the id_elements list with -1
        for i in range(counter):
            id_elements.append(-1)

        print("LISTADO DE ELEMENTOS SELECCIONADOS:", id_elements)

        # While loop for selecting elements
        while counter > 0:
            rc_best_all = cost_matrix.index_lower_value(total_customers, 0, total_customers + total_depots - 1, total_customers - 1)

            print("ROW SELECCIONADA:", rc_best_all.row)
            print("COL SELECCIONADA:", rc_best_all.col)
            print("VALOR SELECCIONADO:", cost_matrix.get_item(rc_best_all.row, rc_best_all.col))

            id_element = partition[rc_best_all.col].get_id_customer()
            id_elements[rc_best_all.row - total_customers] = id_element

            print("ELEMENTO:", id_element)
            print("LISTADO DE ELEMENTOS ACTUALIZADOS:", id_elements)

            cost_matrix.fill_value(total_customers, rc_best_all.col, total_customers + total_depots - 1, rc_best_all.col, float('inf'))
            cost_matrix.fill_value(rc_best_all.row, 0, rc_best_all.row, total_customers + total_depots - 1, float('inf'))
            counter -= 1

        print("--------------------------------------------------")
        print("CENTROIDES/MEDOIDES INICIALES")
        print(id_elements)
        print("--------------------------------------------------")

        return id_elements
    
    def generate_elements(self, distance_type):
        id_elements = []

        total_customers = len(Problem.get_problem().get_customers())
        total_depots = Problem.get_problem().get_total_depots()
        counter = total_depots

        cost_matrix = None
        rc_best_all = None
        id_element = -1

        depot_central = Depot()
        depot_central.set_id_depot(-1)
        depot_central.set_location_depot(self.central_coordinate())

        list_depot_central = [depot_central]

        # Generar la matriz de distancias con las coordenadas
        if distance_type in [0, 1, 2, 3]:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix(
                    Problem.get_problem().get_customers(), list_depot_central, distance_type)
            except Exception as e:
                print(e)

        print("LISTADO DE ELEMENTOS SELECCIONADOS:", id_elements)

        # Seleccionar los elementos
        while counter > 0:
            rc_best_all = cost_matrix.index_bigger_value(
                total_customers, 0, total_customers, total_customers - 1)

            print("ROW SELECCIONADA:", rc_best_all.row)
            print("COL SELECCIONADA:", rc_best_all.col)
            print("VALOR SELECCIONADO:", cost_matrix.get_item(rc_best_all.row, rc_best_all.col))

            id_element = Problem.get_problem().get_customers()[rc_best_all.col].get_id_customer()
            id_elements.append(id_element)

            print("ELEMENTO:", id_element)
            print("LISTADO DE ELEMENTOS ACTUALIZADOS:", id_elements)

            cost_matrix.set_item(rc_best_all.row, rc_best_all.col, float('-inf'))
            counter -= 1

        print("LISTADO DE ELEMENTOS SELECCIONADOS:", id_elements)

        return self.sorted_elements(id_elements, distance_type)
    
    def generate_elements(self):
        distance_type = DistanceType.Euclidean  # por defecto se coloca la euclidiana

        list_centroids = []
        list_customers = list(Problem.get_problem().get_customers())

        total_customers = len(list_customers)
        counter = Problem.get_problem().get_total_depots()
        id_element = -1

        # Randomly selecting the first centroid
        pos_centroid = random.randint(0, total_customers - 1)
        id_element = Problem.get_problem().get_customers()[pos_centroid].get_id_customer()
        list_centroids.append(id_element)
        list_customers.pop(pos_centroid)

        counter -= 1

        print("LISTADO DE ELEMENTOS SELECCIONADOS:", list_centroids)

        cost_matrix = None

        if distance_type in [0, 1, 2, 3]:
            try:
                cost_matrix = Problem.get_problem().fill_cost_matrix_XXX(
                    Problem.get_problem().get_customers(), Problem.get_problem().get_depots(), distance_type)
            except Exception as e:
                print(e)
        elif distance_type == 4:
            cost_matrix = NumericMatrix(Problem.get_problem().get_cost_matrix())

        rc_best_all = cost_matrix.index_bigger_value(pos_centroid, 0, pos_centroid, total_customers - 1)

        id_element = Problem.get_problem().get_customers()[rc_best_all.col].get_id_customer()
        list_centroids.append(id_element)
        list_customers.pop(Problem.get_problem().get_pos_element(id_element, list_customers))

        counter -= 1

        print("LISTADO DE ELEMENTOS SELECCIONADOS:", list_centroids)

        max_distances = []
        pos_customer = -1

        while counter > 0:
            max_distance = -1
            current_distance = -1

            for customer in list_customers:
                pos_customer = Problem.get_problem().get_pos_element(customer.get_id_customer())

                for i, centroid_id in enumerate(list_centroids):
                    pos_centroid = Problem.get_problem().get_pos_element(centroid_id)
                    current_distance = cost_matrix.get_item(pos_centroid, pos_customer)

                    if i == 0:
                        max_distance = current_distance
                    else:
                        if max_distance < current_distance:
                            max_distance = current_distance

                max_distances.append(max_distance)

            min_distance = max_distances[0]
            pos_centroid = 0

            for k in range(1, len(max_distances)):
                if max_distances[k] < min_distance:
                    min_distance = max_distances[k]
                    pos_centroid = k

            id_element = list_customers[pos_centroid].get_id_customer()
            list_centroids.append(id_element)

            list_customers.pop(pos_centroid)
            counter -= 1

            print("LISTADO DE ELEMENTOS SELECCIONADOS:", list_centroids)

        print("LISTADO DE ELEMENTOS SELECCIONADOS:", list_centroids)

        return self.sorted_elements(list_centroids, distance_type)
    
    def verify_centroids(self, clusters, centroids, distance_type):
        change = False

        print(f"change: {change}")

        for i, cluster in enumerate(clusters):
            dummy_depot = self.recalculate_centroid(cluster)

            print(f"------------------------------------------------------------------")
            print(f"DUMMY_DEPOT{i} X: {dummy_depot.get_axis_x()}")
            print(f"DUMMY_DEPOT{i} Y: {dummy_depot.get_axis_y()}")

            print(f"CENTROIDE{i} X: {centroids[i].get_location_depot().get_axis_x()}")
            print(f"CENTROIDE{i} Y: {centroids[i].get_location_depot().get_axis_y()}")

            if (centroids[i].get_location_depot().get_axis_x() != dummy_depot.get_axis_x() or
                centroids[i].get_location_depot().get_axis_y() != dummy_depot.get_axis_y()):
                change = True

                centroids[i].set_id_depot(-1)

                location = Location()
                location.set_axis_x(dummy_depot.get_axis_x())
                location.set_axis_y(dummy_depot.get_axis_y())
                centroids[i].set_location_depot(location)

                print(f"change: {change}")
                print(f"CENTROIDE{i} X: {centroids[i].get_location_depot().get_axis_x()}")
                print(f"CENTROIDE{i} Y: {centroids[i].get_location_depot().get_axis_y()}")
            else:
                print(f"CENTROIDE{i} X: {centroids[i].get_location_depot().get_axis_x()}")
                print(f"CENTROIDE{i} Y: {centroids[i].get_location_depot().get_axis_y()}")

        if change:
            self.update_centroids(clusters, centroids, distance_type)

        print(f"CAMBIO LOS CENTROIDES: {change}")

        return change
    
    def update_centroids(self, clusters, centroids, distance_type):
        try:
            # Calcular la matriz de costos
            cost_matrix = Problem.get_problem().calculate_cost_matrix(centroids, Problem.get_problem().get_depots(), distance_type)
        except (ValueError, SecurityError, ClassNotFoundError, InstantiationError, 
                IllegalAccessError, InvocationTargetError, NoSuchMethodError) as e:
            print(f"Error al calcular la matriz de costos: {e}")
            return

        # Crear una copia temporal de los centroides
        temp_centroids = list(centroids)

        total_centroids = len(centroids)
        rc_best_all = RowCol()
        pos_centroid = -1
        pos_depot = -1

        print("-------------------------------------")
        for i, centroid in enumerate(centroids):
            print(f"CENTROIDE ID: {centroid.get_id_depot()}")
            print(f"CENTROIDE X: {centroid.get_location_depot().get_axis_x()}")
            print(f"CENTROIDE Y: {centroid.get_location_depot().get_axis_y()}")

        for i in range(cost_matrix.get_row_count()):
            for j in range(cost_matrix.get_col_count()):
                print(f"Row: {i} Col: {j} VALUE: {cost_matrix.get_item(i, j)}")
            print("---------------------------------------------")

        # Iterar hasta que la matriz de costos esté llena de infinitos
        while not cost_matrix.full_matrix(0, 0, total_centroids - 1, total_centroids - 1, float('inf')):
            rc_best_all = cost_matrix.index_lower_value()

            print(f"BestAllRow: {rc_best_all.get_row()}")
            print(f"BestAllCol: {rc_best_all.get_col()}")
            print(f"COSTO: {cost_matrix.get_item(rc_best_all.get_row(), rc_best_all.get_col())}")

            pos_centroid = rc_best_all.get_row()
            pos_depot = rc_best_all.get_col()

            print(f"POSICIÓN DEL CENTROIDE: {pos_centroid}")
            print(f"POSICIÓN DEL DEPOSITO: {pos_depot}")

            if pos_centroid != pos_depot:
                depot = Depot()

                depot.set_id_depot(temp_centroids[pos_centroid].get_id_depot())
                print(f"ID CENTROIDE: {temp_centroids[pos_centroid].get_id_depot()}")

                axis_x = temp_centroids[pos_centroid].get_location_depot().get_axis_x()
                axis_y = temp_centroids[pos_centroid].get_location_depot().get_axis_y()

                location = Location()
                location.set_axis_x(axis_x)
                location.set_axis_y(axis_y)
                depot.set_location_depot(location)

                fleet = temp_centroids[pos_centroid].get_fleet_depot()
                depot.set_fleet_depot(fleet)

                centroids[pos_depot] = depot

            cost_matrix.fill_value(0, pos_depot, total_centroids - 1, pos_depot, float('inf'))
            cost_matrix.fill_value(pos_centroid, 0, pos_centroid, total_centroids - 1, float('inf'))

            for i in range(cost_matrix.get_row_count()):
                for j in range(cost_matrix.get_col_count()):
                    print(f"Row: {i} Col: {j} VALUE: {cost_matrix.get_item(i, j)}")
                print("---------------------------------------------")
                
    def calculate_cost(self, clusters, cost_matrix):
        cost = 0.0

        for cluster in clusters:
            list_id_customers = cluster.get_items_of_cluster()
            pos_depot = Problem.get_problem().get_pos_element(cluster.get_id_cluster())

            print(f"CLIENTES: {list_id_customers}")
            print(f"POSICIÓN DEPOSITO: {pos_depot}")

            for id_customer in list_id_customers:
                pos_customer = Problem.get_problem().get_pos_element(id_customer)

                cost += cost_matrix.get_item(pos_depot, pos_customer)

                print(f"ID CLIENTE: {id_customer}")
                print(f"POSICIÓN CLIENTE: {pos_customer}")
                print(f"COSTO: {cost_matrix.get_item(pos_depot, pos_customer)}")
                print(f"COSTO ACUMULADO: {cost}")

        return cost
    
    def calculate_cost(self, clusters, cost_matrix, medoids):
        cost = 0.0
        
        for i, cluster in enumerate(clusters):
            pos_depot = Problem.get_problem().get_pos_element(medoids[i].get_id_depot())
            list_id_customers = cluster.get_items_of_cluster()

            print("-------------------------------------------------------------------------------")
            print(f"ID MEDOIDE: {medoids[i].get_id_depot()}")
            print(f"POSICIÓN DEL MEDOIDE: {pos_depot}")
            print(f"CLIENTES ASIGNADOS AL MEDOIDE: {list_id_customers}")
            print("-------------------------------------------------------------------------------")

            for id_customer in list_id_customers:
                pos_customer = Problem.get_problem().get_pos_element(id_customer)

                if pos_depot == pos_customer:
                    cost += 0.0
                else:
                    cost += cost_matrix.get_item(pos_depot, pos_customer)

                print(f"ID CLIENTE: {id_customer}")
                print(f"POSICIÓN DEL CLIENTE: {pos_customer}")
                print(f"COSTO: {cost_matrix.get_item(pos_depot, pos_customer)}")
                print("-------------------------------------------------------------------------------")
                print(f"COSTO ACUMULADO: {cost}")
                print("-------------------------------------------------------------------------------")

        print(f"MEJOR COSTO TOTAL: {cost}")
        print("-------------------------------------------------------------------------------")

        return cost
    
    def calculate_cost(self, clusters, cost_matrix, medoids, list_partition):
        cost = 0.0
        
        for i, cluster in enumerate(clusters):
            pos_depot = Problem.get_problem().get_pos_element(medoids[i].get_id_depot(), list_partition)
            list_id_customers = cluster.get_items_of_cluster()

            print("-------------------------------------------------------------------------------")
            print(f"ID MEDOIDE: {medoids[i].get_id_depot()}")
            print(f"POSICIÓN DEL MEDOIDE: {pos_depot}")
            print(f"CLIENTES ASIGNADOS AL MEDOIDE: {list_id_customers}")
            print("-------------------------------------------------------------------------------")
            
            for id_customer in list_id_customers:
                pos_customer = Problem.get_problem().get_pos_element(id_customer, list_partition)

                if pos_depot == pos_customer:
                    cost += 0.0
                else:
                    cost += cost_matrix.get_item(pos_depot, pos_customer)

                print(f"ID CLIENTE: {id_customer}")
                print(f"POSICIÓN DEL CLIENTE: {pos_customer}")
                print(f"COSTO: {cost_matrix.get_item(pos_depot, pos_customer)}")
                print("-------------------------------------------------------------------------------")
                print(f"COSTO ACUMULADO: {cost}")
                print("-------------------------------------------------------------------------------")

        print(f"MEJOR COSTO TOTAL: {cost}")
        print("-------------------------------------------------------------------------------")

        return cost
    
    def calculate_dissimilarity(self, distance_type, clusters):
        current_dissimilarity = 0.0
        dissimilarity_matrix = None
        
        # Método para calcular el promedio de disimilitud
        if distance_type in [0, 1, 2, 3]:
            try:
                dissimilarity_matrix = Problem.get_problem().fill_cost_matrix(
                    Problem.get_problem().get_customers(),
                    Problem.get_problem().get_depots(),
                    distance_type
                )
            except (IllegalArgumentException, SecurityException, ClassNotFoundException, 
                    InstantiationException, IllegalAccessException, InvocationTargetException, 
                    NoSuchMethodException) as e:
                print(f"Error: {e}")
        
        # El caso 4 se omite según el código Java
        elif distance_type == 4:
            pass
        
        # Calcular disimilitud
        for cluster in clusters:
            for j in range(len(cluster.get_items_of_cluster())):
                temp = Problem.get_problem().get_pos_element(cluster.get_items_of_cluster()[j])
                for k in range(j + 1, len(cluster.get_items_of_cluster())):
                    current_dissimilarity += dissimilarity_matrix.get_item(
                        temp,
                        Problem.get_problem().get_pos_element(cluster.get_items_of_cluster()[k])
                    )

        current_dissimilarity /= len(clusters)

        print(f"COEFICIENTE DE DISIMILITUD ACTUAL: {current_dissimilarity}")
        print("-------------------------------------------------------------------------------")

        return current_dissimilarity
    
    def replicate_depots(depots):
        new_depots = []

        print("--------------------------------------------------")
        print("MEDOIDES/CENTROIDES ACTUALES")

        for depot in depots:
            new_depot = Depot()
            new_depot.set_id_depot(depot.id_depot)

            axis_x = depot.location_depot.axis_x
            axis_y = depot.location_depot.axis_y

            location = Location()
            location.set_axis_x(axis_x)
            location.set_axis_y(axis_y)
            new_depot.set_location_depot(location)

            fleet = depot.fleet_depot[:]
            new_depot.set_fleet_depot(fleet)

            new_depots.append(new_depot)

            print("--------------------------------------------------")
            print(f"ID MEDOIDE/CENTROIDE: {new_depot.id_depot}")
            print(f"X: {new_depot.location_depot.axis_x}")
            print(f"Y: {new_depot.location_depot.axis_y}")
            print(f"CAPACIDAD DE VEHICULO: {new_depot.fleet_depot[0].capacity_vehicle}")
            print(f"CANTIDAD DE VEHICULOS: {new_depot.fleet_depot[0].count_vehicles}")

        return new_depots
    
    def step_assignment(clusters, customer_to_assign, cost_matrix):
        id_depot = -1
        pos_depot = -1
        capacity_depot = 0.0

        id_customer = -1
        pos_customer = -1
        request_customer = 0.0

        pos_cluster = -1
        request_cluster = 0.0

        rc_best_all = RowCol()

        list_customers = customer_to_assign[:]
        total_customers = len(customer_to_assign)
        total_depots = len(clusters)

        print("--------------------------------------------------------------------")
        print("PROCESO DE ASIGNACIÓN")

        while customer_to_assign and not cost_matrix.full_matrix(total_customers, 0, (total_customers + total_depots - 1), (total_customers - 1), float('inf')):
            rc_best_all = cost_matrix.index_lower_value(total_customers, 0, (total_customers + total_depots - 1), (total_customers - 1))

            pos_customer = rc_best_all.col
            id_customer = list_customers[pos_customer].id_customer
            request_customer = list_customers[pos_customer].request_customer

            print("-----------------------------------------------------------")
            print(f"BestAllCol: {rc_best_all.col}")
            print(f"BestAllRow: {rc_best_all.row}")

            print(f"ID CLIENTE SELECCIONADO: {id_customer}")
            print(f"POSICIÓN DEL CLIENTE SELECCIONADO: {pos_customer}")
            print(f"DEMANDA DEL CLIENTE SELECCIONADO: {request_customer}")

            pos_depot = rc_best_all.row - total_customers
            id_depot = Problem.get_problem().depots[pos_depot].id_depot
            capacity_depot = Problem.get_problem().get_total_capacity_by_depot(id_depot)

            print(f"ID DEPOSITO SELECCIONADO: {id_depot}")
            print(f"POSICIÓN DEL DEPOSITO SELECCIONADO: {pos_depot}")
            print(f"CAPACIDAD TOTAL DEL DEPOSITO SELECCIONADO: {capacity_depot}")

            pos_cluster = find_cluster(id_depot, clusters)

            print(f"POSICION DEL CLUSTER: {pos_cluster}")

            if pos_cluster != -1:
                request_cluster = clusters[pos_cluster].request_cluster

                print(f"DEMANDA DEL CLUSTER: {request_cluster}")

                if capacity_depot >= (request_cluster + request_customer):
                    request_cluster += request_customer

                    clusters[pos_cluster].request_cluster = request_cluster
                    clusters[pos_cluster].items_of_cluster.append(id_customer)

                    print(f"DEMANDA DEL CLUSTER ACTUALIZADA: {request_cluster}")
                    print(f"ELEMENTOS DEL CLUSTER: {clusters[pos_cluster].items_of_cluster}")

                    cost_matrix.fill_value(total_customers, pos_customer, (total_customers + total_depots - 1), pos_customer, float('inf'))
                    customer_to_assign.remove(Problem.get_problem().find_pos_customer(customer_to_assign, id_customer))

                    print(f"CANTIDAD DE CLIENTES SIN ASIGNAR: {len(customer_to_assign)}")
                else:
                    cost_matrix.set_item(rc_best_all.row, pos_customer, float('inf'))

                if is_full_depot(customer_to_assign, request_cluster, capacity_depot):
                    print("DEPOSITO LLENO")

                    cost_matrix.fill_value(rc_best_all.row, 0, rc_best_all.row, (total_customers + total_depots - 1), float('inf'))

        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for cluster in clusters:
            print(f"ID CLUSTER: {cluster.id_cluster}")
            print(f"DEMANDA DEL CLUSTER: {cluster.request_cluster}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.items_of_cluster}")
        print("--------------------------------------------------")

        return clusters
    
    def create_centroids(id_elements):
        centroids = []

        for id_element in id_elements:
            centroid = Depot()

            centroid.set_id_depot(id_element)

            location = Location()
            location.set_axis_x(Problem.get_problem().get_customer_by_id_customer(id_element).location_customer.axis_x)
            location.set_axis_y(Problem.get_problem().get_customer_by_id_customer(id_element).location_customer.axis_y)
            centroid.set_location_depot(location)

            centroids.append(centroid)

        return centroids
    
    def verify_medoids(old_medoids, current_medoids):
        change = False
        i = 0

        while not change and i < len(current_medoids):
            if old_medoids[i].location_depot.axis_x != current_medoids[i].location_depot.axis_x or old_medoids[i].location_depot.axis_y != current_medoids[i].location_depot.axis_y:
                change = True
            else:
                i += 1

        print(f"change:  {change}")

        return change
    
    def step_search_medoids(clusters, medoids, cost_matrix, best_cost):
        current_cost = 0.0

        old_medoids = replicate_depots(medoids)

        print("--------------------------------------------------------------------")
        print("PROCESO DE BÚSQUEDA")

        for i in range(len(clusters)):
            best_loc_medoid = Location(medoids[i].location_depot.x, medoids[i].location_depot.y)

            print("--------------------------------------------------")
            print(f"MEJOR MEDOIDE ID: {medoids[i].id_depot}")
            print(f"MEJOR MEDOIDE LOCATION X: {best_loc_medoid.x}")
            print(f"MEJOR MEDOIDE LOCATION Y: {best_loc_medoid.y}")
            print("--------------------------------------------------")

            for j in range(1, len(clusters[i].items_of_cluster)):
                new_id_medoid = clusters[i].items_of_cluster[j]
                new_medoid = Customer()

                customer = Problem.get_problem().get_customer_by_id(new_id_medoid)
                new_medoid.id_customer = customer.id_customer
                new_medoid.request_customer = customer.request_customer

                location = Location()
                location.x = customer.location_customer.x
                location.y = customer.location_customer.y
                new_medoid.location_customer = location

                medoids[i].id_depot = new_id_medoid
                medoids[i].location_depot = new_medoid.location_customer

                print("ID DEL NUEVO MEDOIDE: ", new_id_medoid)
                print(f"X DEL NUEVO MEDOIDE: {new_medoid.location_customer.x}")
                print(f"Y DEL NUEVO MEDOIDE: {new_medoid.location_customer.y}")

                print("--------------------------------------------------")
                print("LISTA DE MEDOIDES")
                print(f"ID: {medoids[i].id_depot}")
                print(f"X: {medoids[i].location_depot.x}")
                print(f"Y: {medoids[i].location_depot.y}")

                print("LISTA DE ANTERIORES MEDOIDES")
                print(f"ID: {old_medoids[i].id_depot}")
                print(f"X: {old_medoids[i].location_depot.x}")
                print(f"Y: {old_medoids[i].location_depot.y}")
                print("--------------------------------------------------")

                current_cost = calculate_cost(clusters, cost_matrix, medoids)

                print("---------------------------------------------")
                print(f"ACTUAL COSTO TOTAL: {current_cost}")
                print("---------------------------------------------")

                if current_cost < best_cost:
                    best_cost = current_cost
                    best_loc_medoid = medoids[i].location_depot

                    print(f"NUEVO MEJOR COSTO TOTAL: {best_cost}")
                    print(f"NUEVO MEDOIDE ID: {medoids[i].id_depot}")
                    print(f"NUEVO MEDOIDE LOCATION X: {best_loc_medoid.x}")
                    print(f"NUEVO MEDOIDE LOCATION Y: {best_loc_medoid.y}")
                    print("---------------------------------------------")

                    old_medoids[i].id_depot = medoids[i].id_depot
                    old_medoids[i].location_depot.x = medoids[i].location_depot.x
                    old_medoids[i].location_depot.y = medoids[i].location_depot.y
                else:
                    medoids[i].id_depot = old_medoids[i].id_depot
                    medoids[i].location_depot.x = old_medoids[i].location_depot.x
                    medoids[i].location_depot.y = old_medoids[i].location_depot.y

                print(f"ID MEDOIDE: {medoids[i].id_depot}")
                print(f"LISTA DE MEDOIDES X: {medoids[i].location_depot.x}")
                print(f"LISTA DE MEDOIDES Y: {medoids[i].location_depot.y}")
                print("---------------------------------------------")

            medoids[i].location_depot = best_loc_medoid

    def step_search_medoids(clusters, medoids, cost_matrix, best_cost, list_partition):
        current_cost = 0.0
        
        # Reemplazo de los medoidess
        old_medoids = replicate_depots(medoids)
        
        print("--------------------------------------------------------------------")
        print("PROCESO DE BÚSQUEDA")
        
        for i in range(len(clusters)):
            best_loc_medoid = Location(medoids[i].location_depot.x, medoids[i].location_depot.y)
            
            print("--------------------------------------------------")
            print(f"MEJOR MEDOIDE ID: {medoids[i].id_depot}")
            print(f"MEJOR MEDOIDE LOCATION X: {best_loc_medoid.x}")
            print(f"MEJOR MEDOIDE LOCATION Y: {best_loc_medoid.y}")
            print("--------------------------------------------------")
            
            for j in range(1, len(clusters[i].items_of_cluster)):
                new_id_medoid = clusters[i].items_of_cluster[j]
                new_medoid = Customer()
                
                new_medoid.id_customer = Problem.get_problem().get_customer_by_id(new_id_medoid).id_customer
                new_medoid.request_customer = Problem.get_problem().get_customer_by_id(new_id_medoid).request_customer
                
                location = Location()
                location.x = Problem.get_problem().get_customer_by_id(new_id_medoid).location_customer.x
                location.y = Problem.get_problem().get_customer_by_id(new_id_medoid).location_customer.y
                new_medoid.location_customer = location
                
                medoids[i].id_depot = new_id_medoid
                medoids[i].location_depot = new_medoid.location_customer
                
                print(f"ID DEL NUEVO MEDOIDE: {new_id_medoid}")
                print(f"X DEL NUEVO MEDOIDE: {new_medoid.location_customer.x}")
                print(f"Y DEL NUEVO MEDOIDE: {new_medoid.location_customer.y}")
                
                print("--------------------------------------------------")
                print("LISTA DE MEDOIDES")
                print(f"ID: {medoids[i].id_depot}")
                print(f"X: {medoids[i].location_depot.x}")
                print(f"Y: {medoids[i].location_depot.y}")
                
                print("LISTA DE ANTERIORES MEDOIDES")
                print(f"ID: {old_medoids[i].id_depot}")
                print(f"X: {old_medoids[i].location_depot.x}")
                print(f"Y: {old_medoids[i].location_depot.y}")
                print("--------------------------------------------------")
                
                current_cost = calculate_cost(clusters, cost_matrix, medoids, list_partition)
                
                print("---------------------------------------------")
                print(f"ACTUAL COSTO TOTAL: {current_cost}")
                print("---------------------------------------------")
                
                if current_cost < best_cost:
                    best_cost = current_cost
                    best_loc_medoid = medoids[i].location_depot
                    
                    print(f"NUEVO MEJOR COSTO TOTAL: {best_cost}")
                    print(f"NUEVO MEDOIDE ID: {medoids[i].id_depot}")
                    print(f"NUEVO MEDOIDE LOCATION X: {best_loc_medoid.x}")
                    print(f"NUEVO MEDOIDE LOCATION Y: {best_loc_medoid.y}")
                    print("---------------------------------------------")
                    
                    old_medoids[i].id_depot = medoids[i].id_depot
                    old_medoids[i].location_depot.x = medoids[i].location_depot.x
                    old_medoids[i].location_depot.y = medoids[i].location_depot.y
                else:
                    medoids[i].id_depot = old_medoids[i].id_depot
                    medoids[i].location_depot.x = old_medoids[i].location_depot.x
                    medoids[i].location_depot.y = old_medoids[i].location_depot.y
                
                print(f"ID MEDOIDE: {medoids[i].id_depot}")
                print(f"LISTA DE MEDOIDES X: {medoids[i].location_depot.x}")
                print(f"LISTA DE MEDOIDES Y: {medoids[i].location_depot.y}")
                print("---------------------------------------------")
            
            medoids[i].location_depot = best_loc_medoid

    def get_id_medoids(self, medoids):
        id_medoids = []

        for medoid in medoids:
            id_medoids.append(medoid.id_depot)

        print("--------------------------------------------------")
        print("ID MEDOIDES ACTUALES")
        print("--------------------------------------------------")
        print(id_medoids)

        return id_medoids
    
    def central_coordinate(self):
        current_axis_x = 0.0
        current_axis_y = 0.0

        list_coordinates_customers = Problem.get_problem().get_list_coordinates_customers()

        for coord in list_coordinates_customers:
            current_axis_x += coord.axis_x
            current_axis_y += coord.axis_y

        current_axis_x /= len(list_coordinates_customers)
        current_axis_y /= len(list_coordinates_customers)

        central_coordinate = Location(current_axis_x, current_axis_y)

        return central_coordinate
    
    def central_coordinate(self):
        current_axis_x = 0.0
        current_axis_y = 0.0

        list_coordinates_customers = Problem.get_problem().get_list_coordinates_customers()

        for coord in list_coordinates_customers:
            current_axis_x += coord.axis_x
            current_axis_y += coord.axis_y

        current_axis_x /= len(list_coordinates_customers)
        current_axis_y /= len(list_coordinates_customers)

        central_coordinate = Location(current_axis_x, current_axis_y)

        return central_coordinate
    
    def central_coordinate(self):
        current_axis_x = 0.0
        current_axis_y = 0.0

        list_coordinates_customers = Problem.get_problem().get_list_coordinates_customers()

        for coord in list_coordinates_customers:
            current_axis_x += coord.axis_x
            current_axis_y += coord.axis_y

        current_axis_x /= len(list_coordinates_customers)
        current_axis_y /= len(list_coordinates_customers)

        central_coordinate = Location(current_axis_x, current_axis_y)

        return central_coordinate
    
    def generate_elements(self, p_customers, sampsize, distance_type):
        id_elements = []

        total_customers = Problem.get_problem().get_total_customers()
        total_depots = Problem.get_problem().get_total_depots()
        counter = total_depots

        cost_matrix = None
        rc_best_all = RowCol()
        id_element = -1

        if distance_type == 4:
            cost_matrix = NumericMatrix(Problem.get_problem().get_cost_matrix())

        for _ in range(counter):
            id_elements.append(-1)

        print(f"LISTADO DE ELEMENTOS SELECCIONADOS: {id_elements}")

        while counter > 0:
            rc_best_all = cost_matrix.index_lower_value(total_customers, 0, total_customers + total_depots - 1, total_customers - 1)

            print(f"ROW SELECCIONADA: {rc_best_all.row}")
            print(f"COL SELECCIONADA: {rc_best_all.col}")
            print(f"VALOR SELECCIONADO: {cost_matrix.get_item(rc_best_all.row, rc_best_all.col)}")

            id_element = Problem.get_problem().get_customers()[rc_best_all.col].id_customer
            id_elements[rc_best_all.row - total_customers] = id_element

            print(f"ELEMENTO: {id_element}")
            print(f"LISTADO DE ELEMENTOS ACTUALIZADOS: {id_elements}")

            cost_matrix.fill_value(total_customers, rc_best_all.col, total_customers + total_depots - 1, rc_best_all.col, float('inf'))
            cost_matrix.fill_value(rc_best_all.row, 0, rc_best_all.row, total_customers + total_depots - 1, float('inf'))
            counter -= 1

        print("--------------------------------------------------")
        print("CENTROIDES/MEDOIDES INICIALES")
        print(id_elements)
        print("--------------------------------------------------")

        return id_elements