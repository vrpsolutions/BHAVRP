class Solution:

    """
    Constructor para la clase Solution.
    :param clusters: Lista de objetos Cluster.
    """    
    def __init__(self, clusters = None):
        self._clusters = clusters if clusters is not None else []
        self._unassignend_items = []
        
    @property
    def clusters(self):
        return self._clusters
    
    @clusters.setter
    def clusters(self, value):
        self._clusters = value
        
    @property
    def unassigned_items(self):
        return self._unassigned_items

    @unassigned_items.setter
    def unassigned_items(self, value):
        self._unassigned_items = value
        
    """
    Verifica si existen elementos no asignados.
    :return: True si no hay elementos no asignados, de lo contrario False.
    """
    def exist_unassigned_items(self):
        return not self._unassigned_items
    
    """
    Devuelve la cantidad total de elementos no asignados.
    :return: Número de elementos no asignados.
    """
    def get_total_unassigned_items(self):
        return len(self._unassigned_items)

    """
    Calcula el número total de elementos en todos los clusters.
    :return: Número total de elementos en los clusters.
    """
    def elements_clustering(self):
        total_elements = sum(len(cluster.items_of_cluster) for cluster in self._clusters)
        return total_elements
    
    