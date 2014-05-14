package treeManipulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;

import treeBuilder.FormationTree;

public class TruthTable {
	
	private SortedSet<String> variables;
	private HashSet<ArrayList<Integer>> table;
	
	public TruthTable(FormationTree tree) {
		variables = tree.getVariables();
		table = getTruthValues(tree);
	}

	// Case 1: Both equivalences have the same number of same variables
	// Case 2: Both equivalences have the same number but different variables
	// Case 3: One equivalence has a subset of the others variables
	// Case 4: One equivalence is not a subset of the other
	public boolean testEquivalence(TruthTable tt2) {
		SortedSet<String> v2 = tt2.getVariables();
		HashSet<ArrayList<Integer>> t2 = tt2.getTable();

		if (variables.equals(v2) && table.equals(t2))
			return true;
		else {
			if (variables.size() > v2.size())
				return testSubsetEquivalence(t2, v2);
			else
				return tt2.testSubsetEquivalence(table, variables);
		}
	}
	
	private boolean testSubsetEquivalence(HashSet<ArrayList<Integer>> t2, SortedSet<String> v2) {
		HashSet<ArrayList<Integer>> reducedTable = new HashSet<ArrayList<Integer>>();
		
		Iterator<String> it1 = variables.iterator();
		Iterator<String> it2 = v2.iterator();
		int i = 0;
		HashSet<Integer> keptValues = new HashSet<Integer>();
		
		while (it1.hasNext() && it2.hasNext()) {
			String s1 = it1.next();
			String s2 = it2.next();
			
			while (!s1.equals(s2) && it1.hasNext()) {
				s1 = it1.next();
				++i;
			}
			
			keptValues.add(i);
			++i;
		}
		
		keptValues.add(variables.size());
		Iterator<ArrayList<Integer>> tabIt = table.iterator();
		
		while (tabIt.hasNext()) {
			ArrayList<Integer> arr = new ArrayList<Integer>();
			Iterator<Integer> it = keptValues.iterator();
			ArrayList<Integer> next = tabIt.next();
			
			for (int k = 0; k <= variables.size() && it.hasNext(); ++k)
				arr.add(next.get(it.next()));
			reducedTable.add(arr);
		}
		
		return reducedTable.equals(t2);
	}
	
	private HashSet<ArrayList<Integer>> getTruthValues(FormationTree tree) {
		int numVars = variables.size();
		HashSet<ArrayList<Integer>> newTable = createTruthTable(numVars);
		Iterator<ArrayList<Integer>> tabIt = newTable.iterator();
		
		while (tabIt.hasNext()) {
			HashMap<String, Integer> values = new HashMap<String, Integer>();
			Iterator<String> varIt = variables.iterator();
			ArrayList<Integer> row = tabIt.next();
			
			for (int j =  0; j < numVars && varIt.hasNext(); ++j) {
				String var = varIt.next();
				values.put(var, row.get(j));
			}
			
			if (tree.getTruthValue(values))
				row.add(1);
			else
				row.add(0);
		}
		return newTable;
	}
	
	private HashSet<ArrayList<Integer>> createTruthTable(int numVars) {
        int rows = (int) Math.pow(2, numVars);
        HashSet<ArrayList<Integer>> table = new HashSet<ArrayList<Integer>>();

        for (int i = 0; i < rows; ++i) {
        	ArrayList<Integer> row = new ArrayList<Integer>();
            for (int j = numVars - 1; j >= 0; --j)
            	row.add((i / (int) Math.pow(2, j)) % 2);
            table.add(row);
        }

        return table;
    }
	
	public SortedSet<String> getVariables() {
		return variables;
	}
	
	public HashSet<ArrayList<Integer>> getTable() {
		return table;
	}
}
