package treeManipulation;

import java.util.ArrayList;

import treeBuilder.FormationTree;
import treeBuilder.Compiler;

public class TruthTable {

	Compiler compiler;
	int[][] table;
	
	public TruthTable(Compiler compiler) {
		this.compiler = compiler;
	}
	
	public ArrayList<Boolean> getTruthValues(String equiv) {
		ArrayList<Boolean> truthValues = new ArrayList<Boolean>();
		
		FormationTree tree = compiler.compile(equiv);
		boolean variables[] = new boolean[] {true, true};
		int numVariables = variables.length;
		
		// 2 ^ numVariables is no. of permutations to truth values possible
		for (int i = 0; i < (2 ^ numVariables); ++i) {
			
			
			truthValues.add(false);
		}
		
		return truthValues;
	}
	
	public int[][] createTruthTable(int n) {
        int rows = (int) Math.pow(2,n);
		table = new int[n][rows];

        for (int i = 0; i < rows; ++i) {
            for (int j = n-1; j >= 0; --j) {
            	table[j][i] = (i/(int) Math.pow(2, j)) % 2;
            }
        }
        return table;
    }
	
	public String truthTableToString(int[][] table) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < table[0].length; ++i) {
            for (int j = table.length - 1; j >= 0; --j) {
            	sb.append(table[j][i] + " ");
            }
            sb.append("\n");
        }
		
		return sb.toString();
	}
}
