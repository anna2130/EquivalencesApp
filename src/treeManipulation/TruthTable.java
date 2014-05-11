package treeManipulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;

import treeBuilder.FormationTree;

public class TruthTable {
	
	public boolean testEquivalence(FormationTree t1, FormationTree t2) {
		SortedSet<String> v1 = t1.getVariables();
		SortedSet<String> v2 = t2.getVariables();
		
		ArrayList<Integer> list1 = getTruthValues(t1);
		ArrayList<Integer> list2 = getTruthValues(t2);
		
		// Case 1: Both equivalences have the same number of same variables
		// Case 2: Both equivalences have the same number but different variables
		if (v1.size() == v2.size())
			return v1.equals(v2) && list1.equals(list2);
		else {
		// Case 3: One equivalence has a subset of the others variables
			if (v1.size() > v2.size()) {
				if (v1.containsAll(v2))
					list2.addAll(list2);
				// Case 4: One equivalence is not a subset of the other
				else
					return false;
			} else {
				if (v2.containsAll(v1))
					list1.addAll(list1);
				else
					return false;
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
		
		for (int i = rows - 1; i >= 0; --i) {
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

		for (int i = table.size() - 1; i >=0 ; --i) {
            for (int j = table.get(0).length - 1; j >= 0; --j) {
            	sb.append(table.get(i)[j] + " ");
            }
            sb.append("\n");
        }
		
		return sb.toString();
	}
}
