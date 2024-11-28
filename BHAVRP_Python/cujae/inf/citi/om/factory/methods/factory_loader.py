import importlib

class FactoryLoader:

    @staticmethod
    def get_instance(class_name: str):
        try:
            module_name, class_name = class_name.rsplit('.', 1)
            module = importlib.import_module(module_name)
            class_ = getattr(module, class_name)
            
            instance = class_()
            return instance
        
        except ModuleNotFoundError as e:
            print(f"Module {module_name} not found.")
            print(e)
            
        except AttributeError as e:
            print(f"Class {class_name} not found in module {module_name}.")
            print(e)
            
        except Exception as e:
            print(f"An error occurred: {e}.")
        
        return None