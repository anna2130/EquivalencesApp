package treeManipulation;

import java.util.ArrayList;
import java.util.Collections;
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
		table = getTruthValues(tree);
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

		SortedSet<String> testV1 = replace(variables);
		SortedSet<String> testV2 = replace(v2);
		
		if (testV1.equals(testV2) && equalsSet(t2))
			return true;
		else {
			if (variables.size() > v2.size()) 
				return testSubsetEquivalence(table, variables, t2, v2);
			else if (variables.size() < v2.size()) 
				return tt2.testSubsetEquivalence(t2, v2, table, variables);
		}
		return false;
	}

//	public boolean testRuleEquivalence(TruthTable tt2) {
//		SortedSet<String> v2 = tt2.getVariables();
//		HashSet<ArrayList<Integer>> t2 = tt2.getTable();
//		
//		if (equalsSet(t2))
//			return true;
//		else {
//			variables = replace(variables);
//			v2 = replace(v2);
//			if (variables.size() > v2.size()) {
//				return testSubsetEquivalence(table, variables, t2, v2);
//			} else if (variables.size() < v2.size()) {
//				return tt2.testSubsetEquivalence(t2, v2, table, variables);
//			}
//		}
//		return false;
//	}
	
	private SortedSet<String> replace(SortedSet<String> v1) {
		SortedSet<String> result = new java.util.TreeSet<String>();
		char c = 'a';
		for (int i = 0; i < v1.size(); ++i) {
			result.add("" + c);
			c++;
		}
		return result;
	}
	
	private boolean testSubsetEquivalence(HashSet<ArrayList<Integer>> t1, SortedSet<String> v1, 
			HashSet<ArrayList<Integer>> t2, SortedSet<String> v2) {
		HashSet<ArrayList<Integer>> reducedTable = new HashSet<ArrayList<Integer>>();

		Iterator<String> it1 = v1.iterator();
		Iterator<String> it2 = v2.iterator();
		int i = 0;
		SortedSet<Integer> keptValues = new TreeSet<Integer>();
		
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
		keptValues.add(v1.size());
		
		for (ArrayList<Integer> next : t1) {
			ArrayList<Integer> arr = new ArrayList<Integer>();
			
			for (Integer val : keptValues)
				arr.add(next.get(val));
			reducedTable.add(arr);
		}
		return reducedTable.equals(t2);
	}
	
	public LinkedHashSet<ArrayList<Integer>> getTruthValues(FormationTree tree) {
		int numVars = variables.size();
		LinkedHashSet<ArrayList<Integer>> newTable = createTruthTable(numVars);
		
		for (ArrayList<Integer> row : newTable) {
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
		return newTable;
	}
	
	private LinkedHashSet<ArrayList<Integer>> createTruthTable(int numVars) {
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
	
	private boolean equalsSet(HashSet<ArrayList<Integer>> t2) {
		boolean result = true;
		
		Iterator<ArrayList<Integer>> it1 = table.iterator();
		Iterator<ArrayList<Integer>> it2 = t2.iterator();
		
		if (table.size() != t2.size())
			return false;
		
		while (it1.hasNext() && it2.hasNext()) {
			result &= equalLists(it1.next(), it2.next());
		}
		
		return result;
	}
	
	private  boolean equalLists(ArrayList<Integer> l1, ArrayList<Integer> l2){
	    if (l1 == null && l2 == null){
	        return true;
	    }

	    if((l1 == null && l2 != null) 
	      || l1 != null && l2 == null
	      || l1.size() != l2.size()){
	        return false;
	    }

	    // to avoid messing the order of the lists we will use a copy
	    l1 = new ArrayList<Integer>(l1); 
	    l2 = new ArrayList<Integer>(l2);   

	    Collections.sort(l1);
	    Collections.sort(l2);      
	    return l1.equals(l2);
	}
	
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
