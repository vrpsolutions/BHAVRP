from .location import Location

class Customer:
    # Constructor para la clase Customer.
    def __init__(
        self, 
        id_customer: int, 
        request_customer: float, 
        location_customer: Location
    ):
        self.id_customer = id_customer
        self.request_customer = request_customer
        self.location_customer = location_customer
    
    def __init__(self):
        self.id_customer = None
        self.request_customer = 0.0
        self.location_customer = None
    
    def get_id_customer(self) -> int:
        return self.id_customer

    def set_id_customer(self, value: int):
        self.id_customer = value

    def get_request_customer(self) -> float:
        return self.request_customer

    def set_request_customer(self, value: float):
        self.request_customer = value

    def get_location_customer(self) -> Location:
        return self.location_customer

    def set_location_customer(self, value: Location):
        self.location_customer = value