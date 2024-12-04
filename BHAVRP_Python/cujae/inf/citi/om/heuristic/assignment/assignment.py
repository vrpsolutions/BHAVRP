from abc import ABC, abstractmethod
from typing import List
from ...controller.utils.tools import Tools
from ...problem.input import Problem, Customer, Location
from ...problem.output.solution.cluster import Cluster

class Assignment(ABC):
    
    @abstractmethod
    def to_clustering(self):
        pass
    
    def initialize_cluster(self):
        listClusters = []
        
        totalClusters = len(Problem.get_problem().get_depots())
        
        for i in range(totalClusters):
            listIDItems = []
            cluster = Cluster(Problem.get_problem().get_list_id_depots()[i], 0.0, listIDItems)
            listClusters.append(cluster)
        
        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for cluster in listClusters:
            print(f"ID CLUSTER: {cluster.getIDCluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.getRequestCluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.getItemsOfCluster()}")
        print("--------------------------------------------------")

        return listClusters
    
    def initialize_clusters_with_elements(self, listIDElements: List[int]):
        clusters = []
        
        totalElements = len(listIDElements)

        if totalElements == len(Problem.getProblem().getDepots()):
            for i in range(totalElements):
                listIDCustomers = [listIDElements[i]]
                cluster = Cluster(
                    Problem.getProblem().getDepots()[i].getIDDepot(),
                    Problem.getProblem().getRequestByIDCustomer(listIDElements[i]),
                    listIDCustomers
                )
                clusters.append(cluster)
        else:
            for i in range(totalElements):
                listIDCustomers = []
                if Problem.getProblem().findPosDepot(Problem.getProblem().getDepots(), listIDElements[i]) == -1:
                    listIDCustomers.append(listIDElements[i])
                    cluster = Cluster(listIDElements[i], Problem.getProblem().getRequestByIDCustomer(listIDElements[i]), listIDCustomers)
                else:
                    cluster = Cluster(listIDElements[i], 0.0, listIDCustomers)
                clusters.append(cluster)

        print("--------------------------------------------------")
        print("LISTA DE CLUSTERS")
        for cluster in clusters:
            print(f"ID CLUSTER: {cluster.getIDCluster()}")
            print(f"DEMANDA DEL CLUSTER: {cluster.getRequestCluster()}")
            print(f"ELEMENTOS DEL CLUSTER: {cluster.getItemsOfCluster()}")
        print("--------------------------------------------------")

        return clusters
    
    def find_cluster(self, idCluster: int, clusters: List[Cluster]) -> int:
        posCluster = -1
        for i, cluster in enumerate(clusters):
            if cluster.getIDCluster() == idCluster:
                posCluster = i
                break
        return posCluster
    
    def get_pos_max_value(self, list: List[float]) -> int:
        if not list:
            return -1

        maxValue = list[0]
        posMaxValue = 0

        for i in range(1, len(list)):
            if list[i] > maxValue:
                maxValue = list[i]
                posMaxValue = i
        return posMaxValue

    def get_pos_min_value(self, list: List[float]) -> int:
        if not list:
            return -1

        minValue = list[0]
        posMinValue = 0

        for i in range(1, len(list)):
            if list[i] < minValue:
                minValue = list[i]
                posMinValue = i
        return posMinValue
    
    def is_full_depot(self, customers: List[Customer], requestCluster: float, capacityDepot: float) -> bool:
        isFull = True
        currentRequest = capacityDepot - requestCluster
        if currentRequest > 0:
            for customer in customers:
                if customer.getRequestCustomer() <= currentRequest:
                    isFull = False
                    break
        return isFull

    def get_customers_out_depot(self, customers: List[Customer], requestCluster: float, capacityDepot: float) -> List[int]:
        customersOutDepot = []
        currentRequest = capacityDepot - requestCluster
        if currentRequest != 0:
            for i, customer in enumerate(customers):
                if customer.getRequestCustomer() > currentRequest:
                    customersOutDepot.append(i)
        return customersOutDepot

    def get_pos_cluster(self, posCustomer: int, clusters: List[Cluster]) -> int:
        idCustomer = Problem.getProblem().getListIDCustomers()[posCustomer]
        for i, cluster in enumerate(clusters):
            if idCustomer in cluster.getItemsOfCluster():
                return i
        return -1
    
    def recalculate_test(self, clusterOne: Cluster, clusterTwo: Cluster, customersToAssign: List[Customer]) -> Location:
        locationOne = customersToAssign[Problem.getProblem().findPosCustomer(customersToAssign, clusterOne.getIDCluster())].getLocationCustomer()
        locationTwo = customersToAssign[Problem.getProblem().findPosCustomer(customersToAssign, clusterTwo.getIDCluster())].getLocationCustomer()

        aveAxisX = (locationOne.getAxisX() + locationTwo.getAxisX()) / 2
        aveAxisY = (locationOne.getAxisY() + locationTwo.getAxisY()) / 2

        location = Location()
        location.setAxisX(aveAxisX)
        location.setAxisY(aveAxisY)

        return location
    
    def recalculate_centroid(self, cluster: Cluster) -> Location:
        aveAxisX = 0.0
        aveAxisY = 0.0
        countCustomers = len(cluster.getItemsOfCluster())

        for customerID in cluster.getItemsOfCluster():
            location = Problem.getProblem().getLocationByIDCustomer(customerID)
            aveAxisX += location.getAxisX()
            aveAxisY += location.getAxisY()

        aveAxisX /= countCustomers
        aveAxisY /= countCustomers

        locationCentroid = Location()
        locationCentroid.setAxisX(Tools.truncateDouble(aveAxisX, 6))
        locationCentroid.setAxisY(Tools.truncateDouble(aveAxisY, 6))

        return locationCentroid
    
    def update_customer_to_assign(self, customerToAssign: List[Customer], idElements: List[int]):
        for idElement in idElements:
            customerToAssign = [customer for customer in customerToAssign if customer.getIDCustomer() != idElement]

        print("CLIENTES A ASIGNAR")
        for customer in customerToAssign:
            print(f"--------------------------------------------------")
            print(f"ID CLIENTE: {customer.getIDCustomer()}")
            print(f"X: {customer.getLocationCustomer().getAxisX()}")
            print(f"Y: {customer.getLocationCustomer().getAxisY()}")
            print(f"DEMANDA: {customer.getRequestCustomer()}")