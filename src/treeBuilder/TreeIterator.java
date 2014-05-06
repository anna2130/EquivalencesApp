package treeBuilder;

public class TreeIterator {
	private Node next;
	
	public TreeIterator(Node root){
		next = root;
	     
	    if(next == null)
	    	return;
	     
	    while (next.hasChildren())
	        next = next.getChildren()[0];
	}
	
	public boolean hasNext() {
		return next != null;
	}
	
	public Node next() {
		if (!hasNext())
			return null;
		
		Node current = next;
		
		// if you can walk right, walk right, then fully left.
	    // otherwise, walk up until you come from left.
		if (next.hasChildren() && next.getChildren().length == 2) {
			next = next.getChildren()[1];
			while (next.hasChildren())
				next = next.getChildren()[0];
			return current;
		} else while (true) {
			if (!next.hasParent()) {
				next = null;
				return current;
			}
			if (next.getParent().getChildren()[0] == next) {
				next = next.getParent();
				return current;
			}
			next = next.getParent();
		}
	}
}
