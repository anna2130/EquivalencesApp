package app;

import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeLayout.Bounds;
import abego.DefaultConfiguration;
import abego.FixedNodeExtentProvider;
import abego.TreeLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
	private Paint paint;

	private FormationTree tree;
	private int offset;
	private int nodeHeight;
	
	private static double gapBetweenLevels = 50;
	private static double gapBetweenNodes = 10;
 
	private TreeLayout<Node> treeLayout;
	
	public DrawView(Context context) {
		super(context);
		init(context);
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context c){
		tree = null;
		offset = 20;
		nodeHeight = 40;

		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(25);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(w, h);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		Node root = tree.getRoot();
//		drawTree(canvas, getWidth() / 2, 0);
		setUpTreeLayout();
		paintEdges(canvas);
		System.out.println("Successfully set up layout");

		// paint the boxes
		for (Node node : treeLayout.getNodeBounds().keySet()) {
//			System.out.println("Node: " + node);
			paintNode(canvas, node);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

//	public void drawTree(Canvas canvas, int width, int widthOffset) {
//		int height = (node.getDepth() * nodeHeight) + offset;
//		String val = node.getValue();
//
//		canvas.drawText(val, width, height, paint);
//
//		
//		if (node.hasChildren()) {
//			if (node instanceof BinaryOperator) {
//				Node leftChild = ((BinaryOperator) node).getLeftChild();
//				Node rightChild = ((BinaryOperator) node).getRightChild();
//
//				drawTree(canvas, leftChild, width / 2, 0);
//				drawTree(canvas, rightChild, width + width / 2, width);
//			} else if (node instanceof UnaryOperator) {
//				Node child = ((UnaryOperator) node).getChild();
//				drawTree(canvas, child, width, 0);
//			}
//		}
//
//				this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height + offset));
//	}
	
	public void paintEdges(Canvas canvas) {
		
	}
	
	public void paintNode(Canvas canvas, Node node) {
		Bounds bounds = getBoundsOfNode(node);
		System.out.println("Node: " + node + " has bounds " + bounds);
		canvas.drawRect((float) bounds.getX(),(float) (bounds.getY() + bounds.getHeight()),
				(float) (bounds.getX() + bounds.getWidth()),(float) bounds.getY(), paint);
	}

	public void setUpTreeLayout() {
		// setup the tree layout configuration
		DefaultConfiguration<Node> configuration = new DefaultConfiguration<Node>(
				gapBetweenLevels, gapBetweenNodes);

		// create the NodeExtentProvider for TextInBox nodes
		FixedNodeExtentProvider<Node> nodeExtentProvider = new FixedNodeExtentProvider<>(20, 20);

		// create the layout
		treeLayout = new TreeLayout<Node>(tree,
				nodeExtentProvider, configuration);
	}

	public Bounds getBoundsOfNode(Node node) {
		return treeLayout.getNodeBounds().get(node);
	}
	
	public void setTree(FormationTree tree) {
		this.tree = tree;
	}

}


