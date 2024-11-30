class Location:
    
    """
    Constructor para la clase Location.
    :param axis_x: Coordenada en el eje X.
    :param axis_y: Coordenada en el eje Y.
    """
    def __init__(self, axis_x=0.0, axis_y=0.0):
        self.axis_x = axis_x
        self.axis_y = axis_y
    
    @property
    def axis_x(self):
        return self._axis_x

    @axis_x.setter
    def axis_x(self, value):
        self._axis_x = value

    @property
    def axis_y(self):
        return self._axis_y

    @axis_y.setter
    def axis_y(self, value):
        self._axis_y = value