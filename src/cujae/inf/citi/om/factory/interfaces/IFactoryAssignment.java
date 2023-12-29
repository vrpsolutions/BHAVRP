package cujae.inf.citi.om.factory.interfaces;

import cujae.inf.citi.om.heuristic.assignment.Assignment;

/* Interfaz que define c�mo crear un objeto Assignment*/

public interface IFactoryAssignment {
	
	public Assignment createAssignment(AssignmentType assignmentType);
}
