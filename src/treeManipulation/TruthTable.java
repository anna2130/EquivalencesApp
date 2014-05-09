package treeManipulation;

import java.util.ArrayList;
import treeBuilder.FormationTree;
import treeBuilder.Compiler;

public class TruthTable {

	Compiler compiler;
	
	public TruthTable(Compiler compiler) {
		this.compiler = compiler;
	}
	
	public ArrayList<Boolean> getTruthValues(String equiv) {
		ArrayList<Boolean> truthValues = new ArrayList<Boolean>();
		
		FormationTree tree = compiler.compile(equiv);
		int numVariables = 0;
		
		// 2 ^ numVariables is no. of permutations to truth values possible
		for (int i = 0; i < (2 ^ numVariables); ++i) {
			
		}
		
		return truthValues;
	}
	
}
