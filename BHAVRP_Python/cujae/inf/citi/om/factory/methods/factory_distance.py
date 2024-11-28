import importlib

class FactoryDistance:
    
    def create_distance(self, distance_type):
        
        class_name = f"cujae.inf.citi.om.heuristic.distance.{distance_type}"
        
        distance = None
        try: 
            module_name, class_name = class_name.rsplit('.', 1)
            module = importlib.import_module(module_name)
            class_ = getattr(module, class_name)
            
            distance = class_()
            
        except ModuleNotFoundError as e:
            print(f"Module {module_name} not found.")
            print(e)
            
        except AttributeError as e:
            print(f"Class {class_name} not found in module {module_name}.")
            print(e)
            
        except Exception as e:
            print(f"An error occurred: {e}.")
        
        return distance
            