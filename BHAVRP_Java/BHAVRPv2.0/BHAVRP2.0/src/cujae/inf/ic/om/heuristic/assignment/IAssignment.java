package cujae.inf.ic.om.heuristic.assignment;

import cujae.inf.ic.om.problem.output.solution.Solution;

public interface IAssignment {
	
	Solution toClustering();
	void initialize();
	void assign();
	Solution finish();

}
