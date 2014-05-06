package treeBuilder;

public class Atom extends Node {
	
	public Atom(int key, int depth, String value, int index) {
		super(key, depth, value, index);
	}

	@Override
	public String toString() {
		return super.getValue();
	}
	
	public String toTreeString() {
		return getKey() + "-" + getDepth() + ": " + getValue();
	}

}
