class Location:
    
    """
    Constructor para la clase Location.
    :param axis_x: Coordenada en el eje X.
    :param axis_y: Coordenada en el eje Y.
    """
    def __init__(self, axis_x: float = 0.0, axis_y: float = 0.0):
        self.axis_x = axis_x
        self.axis_y = axis_y
    
    def get_axis_x(self) -> float:
        return self.axis_x
    
    def set_axis_x(self, value: float):
        self.axis_x = value

    def get_axis_y(self) -> float:
        return self.axis_y

    def set_axis_y(self, value: float):
        self.axis_y = value