package cujae.inf.ic.om.factory.methods;

import cujae.inf.ic.om.assignment.Assignment;
import cujae.inf.ic.om.factory.interfaces.AssignmentType;
import cujae.inf.ic.om.factory.interfaces.IFactoryAssignment;


/* Clase que implementa el Patrón Factory para la carga dinámica de un determinado método de asignación*/
public class FactoryAssignment implements IFactoryAssignment {

	@Override
	public Assignment createAssignment(AssignmentType assignmentType) {
		Assignment assignment = null;
		
		try {
			assignment = (Assignment) Assignment.class.getClassLoader().loadClass(assignmentType.toString()).newInstance();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		
		return assignment;
	}
}