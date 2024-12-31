package cujae.inf.ic.om.factory.interfaces;

import cujae.inf.ic.om.assignment.Assignment;

/* Interfaz que define c�mo crear un objeto Assignment*/
public interface IFactoryAssignment {
	public Assignment createAssignment(AssignmentType assignmentType);
}
