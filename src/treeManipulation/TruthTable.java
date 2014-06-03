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

	// Case 1: Both formulae have the same variables and are equivalent
	// Case 2: Both formulae have different variables and are equivalent
	// Case 3: The formulae are not equal - test fails
	public boolean testEquivalence(TruthTable tt2) {
		SortedSet<String> v2 = tt2.getVariables();
		HashSet<ArrayList<Integer>> t2 = tt2.getTable();

		// Case 1:
		if (variables.equals(v2) && tablesEqual(t2))
			return true;
		// Case 2:
		else {
			SortedSet<String> all = new TreeSet<String>();
			all.addAll(variables);
			all.addAll(v2);

			TruthTable extendedTable1 = this;
			TruthTable extendedTable2 = tt2;

			for (String var : all) {
				if (!variables.contains(var))
					extendedTable1 = extendedTable1.addVariable(var);
				if (!v2.contains(var))
					extendedTable2 = extendedTable2.addVariable(var);
			}

			return extendedTable1.tablesEqual(extendedTable2.getTable());
		}
	}

	public TruthTable addVariable(String var) {
		LinkedHashSet<ArrayList<Integer>> newTable = new LinkedHashSet<ArrayList<Integer>>();

		SortedSet<String> newVariables = new TreeSet<String>();
		newVariables.addAll(variables);
		newVariables.add(var);

		Iterator<String> varIt = newVariables.iterator();
		int column = -1;

		for (int i = 0; varIt.hasNext(); ++i) {
			if (varIt.next().equals(var))
				column = i;
		}

		Iterator<ArrayList<Integer>> rowIt = table.iterator();
		ArrayList<Integer> row = null;
		int newVal = 0;

		for (int i = 0; i < table.size() * 2; ++i) {
			if (newVal == 0)
				row = rowIt.next();

			Iterator<Integer> valIt = row.iterator();
			ArrayList<Integer> newRow = new ArrayList<Integer>();

			int val = -1;

			for (int j = 0; j < row.size(); ++j) {
				val = valIt.next();

				if (j == column)
					newRow.add(newVal);

				newRow.add(val);
			}
			// XOR with 1 to alternate between 0 and 1
			newVal = newVal ^ 1;

			newTable.add(newRow);
		}

		TruthTable newTt = new TruthTable(newVariables, newTable, result);

		return newTt;
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

	private boolean tablesEqual(HashSet<ArrayList<Integer>> t2) {
		if (!tablesSameSize(t2))
			return false;

		boolean tableMatch = true;
		for (ArrayList<Integer> row : table) {
			boolean rowMatch = false;
			for (ArrayList<Integer> row2 : t2) {
				rowMatch |= row.equals(row2);
			}
			tableMatch &= rowMatch;
		}
		return tableMatch;
	}

	private boolean tablesSameSize(HashSet<ArrayList<Integer>> t2) {
		return table.size() == t2.size();
	}

	public SortedSet<String> getVariables() {
		return variables;
	}

	public LinkedHashSet<ArrayList<Integer>> getTable() {
		return table;
	}

	public String getResult() {
		return result;
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
