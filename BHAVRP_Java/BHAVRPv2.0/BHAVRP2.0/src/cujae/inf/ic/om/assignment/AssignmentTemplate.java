package cujae.inf.ic.om.assignment;

import cujae.inf.ic.om.problem.output.Solution;

public abstract class AssignmentTemplate implements IAssignment{
	
	public void initialize() {};
	public void assign() {};
	public Solution finish() {
		return null;
	}
}
