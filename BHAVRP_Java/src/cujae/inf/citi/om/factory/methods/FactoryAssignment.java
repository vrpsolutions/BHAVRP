package cujae.inf.citi.om.factory.methods;

//import java.lang.reflect.InvocationTargetException;

import cujae.inf.citi.om.factory.interfaces.AssignmentType;
import cujae.inf.citi.om.factory.interfaces.IFactoryAssignment;
import cujae.inf.citi.om.heuristic.assignment.Assignment;

/* Clase que implementa el Patrón Factory para la carga dinámica de un determinado método de asignación*/

public class FactoryAssignment implements IFactoryAssignment {

	@Override
	public Assignment createAssignment(AssignmentType assignmentType) {
		Assignment assignment = null;
		
		try {
			assignment = (Assignment) Assignment.class.getClassLoader().loadClass(assignmentType.toString()).newInstance();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return assignment;
	}
}
