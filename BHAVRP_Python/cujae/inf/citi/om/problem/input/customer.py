class Customer:
    
    """
    Constructor para la clase Customer.
    :param id_customer: ID del cliente.
    :param request_customer: Solicitud del cliente (carga o demanda).
    :param location_customer: Ubicación del cliente. 
    """
    def __init__(self, id_customer, request_customer, location_customer):
        self.id_customer = id_customer
        self.request_customer = request_customer
        self.location_customer = location_customer
    
    @property
    def id_customer(self):
        return self._id_customer

    @id_customer.setter
    def id_customer(self, value):
        self._id_customer = value

    @property
    def request_customer(self):
        return self._request_customer

    @request_customer.setter
    def request_customer(self, value):
        self._request_customer = value

    @property
    def location_customer(self):
        return self._location_customer

    @location_customer.setter
    def location_customer(self, value):
        self._location_customer = value