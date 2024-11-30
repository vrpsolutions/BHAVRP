class Cluster:
    
    """
    Constructor para la clase Cluster.
    :param id_cluster: Identificador del cluster.
    :param request_cluster: Valor de la solicitud del cluster.
    :param items_of_cluster: Lista de elementos que pertenecen al cluster.
    """
    def __init__(self, id_cluster =- 1, request_cluster = 0.0, items_of_cluster = None):
        self._id_cluster = id_cluster
        self._request_cluster = request_cluster
        self._items_of_cluster = items_of_cluster if items_of_cluster is not None else []
        
    @property
    def id_cluster(self):
        return self._id_cluster
    
    @id_cluster.setter
    def id_cluster(self, value):
        self._id_cluster = value
        
    @property
    def request_cluster(self):
        return self._request_cluster
    
    @request_cluster.setter
    def request_cluster(self, value):
        self._request_cluster = value
        
    @property
    def items_of_cluster(self):
        return self._items_of_cluster

    @items_of_cluster.setter
    def items_of_cluster(self, value):
        self._items_of_cluster = value
        
    """
    Limpia el cluster reiniciando la solicitud y vaciando la lista de elementos.
    """        
    def clean_cluster(self):
        self._request_cluster = 0.0
        self._items_of_cluster.clear()