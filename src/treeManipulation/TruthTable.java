package treeManipulation;

import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;

public class TruthTable {

	Compiler compiler;
	
	public TruthTable(Compiler compiler) {
		this.compiler = compiler;
	}
	
	public boolean testEquivalence(FormationTree t1, FormationTree t2) {
		ArrayList<Integer> list1 = getTruthValues(t1);
		ArrayList<Integer> list2 = getTruthValues(t2);
		
		if (list1.size() == list2.size())
			return list1.equals(list2);
		else {
			if (list1.size() > list2.size()) {
				list2.addAll(list2);
			} else {
				list1.addAll(list1);
			}
			return list1.equals(list2);
		}
	}
	
	public ArrayList<Integer> getTruthValues(FormationTree	tree) {
		ArrayList<Integer> equivTruthVals = new ArrayList<Integer>();
		
		SortedSet<String> variables = tree.getVariables();
		
		int numVariables = variables.size();
		int rows = (int) Math.pow(2,numVariables);
    	ArrayList<int[]> table = createTruthTable(numVariables);
		
		for (int i = 0; i < rows; ++i) {
			HashMap<String, Integer> values = new HashMap<String, Integer>();
			Iterator<String> it = variables.iterator();
			
			for (int j = 0; j < numVariables; ++j) {
				if (it.hasNext())
					values.put(it.next(), table.get(i)[j]);
			}
			
			Integer truthValue;
			if (tree.getTruthValue(values))
				truthValue = 1;
			else
				truthValue = 0;
			
			equivTruthVals.add(truthValue);
		}
		
		return equivTruthVals;
	}
	
	private ArrayList<int[]> createTruthTable(int n) {
        int rows = (int) Math.pow(2,n);
    	ArrayList<int[]> table = new ArrayList<int[]>();

        for (int i = 0; i < rows; ++i) {
        	int[] row = new int[n];
            for (int j = n-1; j >= 0; --j) {
            	row[j] = (i/(int) Math.pow(2, j)) % 2;
            }
            table.add(row);
        }
        return table;
    }
	
	private String truthTableToString(ArrayList<int[]> table) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < table.size(); ++i) {
            for (int j = table.get(0).length - 1; j >= 0; --j) {
            	sb.append(table.get(i)[j] + " ");
            }
            sb.append("\n");
        }
		
		return sb.toString();
	}
}
