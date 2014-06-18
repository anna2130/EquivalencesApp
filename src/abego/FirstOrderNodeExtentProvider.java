package abego;

import treeBuilder.Node;

public class FirstOrderNodeExtentProvider implements NodeExtentProvider<Node> {
	
	@Override
	public double getWidth(Node treeNode) {
		return treeNode.getWidth();
	}
	@Override
	public double getHeight(Node treeNode) {
		return treeNode.getHeight();
	}
}
