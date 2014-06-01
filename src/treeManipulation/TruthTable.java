package treeManipulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import treeBuilder.FormationTree;

public class TruthTable {
	
	private SortedSet<String> variables;
	private LinkedHashSet<ArrayList<Integer>> table;
	private String result;
	
	public TruthTable(FormationTree tree) {
		variables = tree.getVariables();
		table = createTruthTable(tree);
		result = tree.toString();
	}
	
	public TruthTable(SortedSet<String> variables, LinkedHashSet<ArrayList<Integer>> table, String result) {
		this.variables = variables;
		this.table = table;
		this.result = result;
	}
	
	// Case 1: Both equivalences have the same number of same variables
	// Case 2: Both equivalences have the same number but different variables
	// Case 3: One equivalence has a subset of the others variables
	// Case 4: One equivalence is not a subset of the other
	public boolean testEquivalence(TruthTable tt2) {
		SortedSet<String> v2 = tt2.getVariables();
		HashSet<ArrayList<Integer>> t2 = tt2.getTable();

		SortedSet<String> testV1 = removeTruthValues(variables);
		SortedSet<String> testV2 = removeTruthValues(v2);

		if ((testV1.containsAll(testV2) || testV2.containsAll(testV1)) 
				&& tablesEqual(t2))
			return true;
		else {
			if (variables.size() > v2.size()) 
				return testSubsetEquivalence(table, variables, t2, v2);
			else if (variables.size() < v2.size()) 
				return tt2.testSubsetEquivalence(t2, v2, table, variables);
		}
		return false;
	}

	private SortedSet<String> removeTruthValues(SortedSet<String> v1) {
		SortedSet<String> result = new java.util.TreeSet<String>();

		result.addAll(v1);
		result.remove("┬");
		result.remove("⊥");
		
		return result;
	}
	
	private HashSet<ArrayList<Integer>> createReducedTable(HashSet<ArrayList<Integer>> table1, SortedSet<String> v1, 
			HashSet<ArrayList<Integer>> table2, SortedSet<String> v2) {
		HashSet<ArrayList<Integer>> reducedTable = new HashSet<ArrayList<Integer>>();

		int i = 0;
		SortedSet<Integer> keptValues = new TreeSet<Integer>();

		for (String var : v1) {
			if (v2.contains(var))
				keptValues.add(i);
			++i;
		}
		keptValues.add(v1.size());
		
		for (ArrayList<Integer> oldRow : table1) {
			ArrayList<Integer> newRow = new ArrayList<Integer>();
			
			for (Integer index : keptValues)
				newRow.add(oldRow.get(index));
			reducedTable.add(newRow);
		}
		
		return reducedTable;
	}
	
	private boolean testSubsetEquivalence(HashSet<ArrayList<Integer>> t1, SortedSet<String> v1, 
			HashSet<ArrayList<Integer>> t2, SortedSet<String> v2) {

		HashSet<ArrayList<Integer>> reducedTable1 = createReducedTable(t1, v1, t2, v2);
		HashSet<ArrayList<Integer>> reducedTable2 = createReducedTable(t2, v2, t1, v1);
		
		return reducedTable1.equals(reducedTable2);
	}
	
	public LinkedHashSet<ArrayList<Integer>> createTruthTable(FormationTree tree) {
		int numVars = variables.size();
		LinkedHashSet<ArrayList<Integer>> table = createAtomicTruthValues(numVars);
		
		for (ArrayList<Integer> row : table) {
			HashMap<String, Integer> values = new HashMap<String, Integer>();
			Iterator<String> varIt = variables.iterator();
			
			for (int j =  0; j < numVars && varIt.hasNext(); ++j) {
				String var = varIt.next();
				values.put(var, row.get(j));
			}
			
			if (tree.getTruthValue(values))
				row.add(1);
			else
				row.add(0);
		}
		return table;
	}
	
	private LinkedHashSet<ArrayList<Integer>> createAtomicTruthValues(int numVars) {
        int rows = (int) Math.pow(2, numVars);
        LinkedHashSet<ArrayList<Integer>> table = new LinkedHashSet<ArrayList<Integer>>();

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
	
	private boolean tablesEqual(HashSet<ArrayList<Integer>> t2) {
		boolean result = true;
		
		Iterator<ArrayList<Integer>> it1 = table.iterator();
		Iterator<ArrayList<Integer>> it2 = t2.iterator();
		
		if (table.size() != t2.size())
			return false;
		
		while (it1.hasNext() && it2.hasNext())
			result &= it1.next().equals(it2.next());
		
		return result;
	}
	
//	private  boolean listsEqual(ArrayList<Integer> l1, ArrayList<Integer> l2){
//	    if (l1 == null && l2 == null){
//	        return true;
//	    }
//
//	    if((l1 == null && l2 != null) 
//	      || l1 != null && l2 == null
//	      || l1.size() != l2.size()){
//	        return false;
//	    }
//	    // to avoid messing the order of the lists we will use a copy
//	    l1 = new ArrayList<Integer>(l1); 
//	    l2 = new ArrayList<Integer>(l2);   
//
//	    Collections.sort(l1);
//	    Collections.sort(l2);      
//	    return l1.equals(l2);
//	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (String s : variables) {
			sb.append(s);
			sb.append(" ");
		}
		sb.append(result);
		sb.append("\n");
		
		for (ArrayList<Integer> row : table) {
			for (Integer i : row) {
				sb.append(i);
				sb.append(" ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
