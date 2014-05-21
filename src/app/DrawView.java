package app;

import treeBuilder.FormationTree;
import treeBuilder.Node;
import abego.DefaultConfiguration;
import abego.FixedNodeExtentProvider;
import abego.TreeLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
	private Paint paint;
	private Paint backgroundPaint;

	private FormationTree tree;
//	private int offset;
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
//		offset = 20;
		nodeHeight = 40;

		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(30);
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.RED);
		backgroundPaint.setStrokeWidth(10);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(w, h);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		setUpTreeLayout();
		paintEdges(canvas, tree.getRoot());
		System.out.println("Successfully set up layout");

		// paint the boxes
		for (Node node : treeLayout.getNodeBounds().keySet()) {
			//			drawTree(canvas, node, getWidth() / 2, 0);
			//			System.out.println("Node: " + node);
			paintNode(canvas, node);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	//	public void drawTree(Canvas canvas, Node node, int width, int widthOffset) {
	//		int height = (node.getDepth() * nodeHeight) + offset;
	//		String val = node.getValue();
	//
	//		canvas.drawText(val, width, height, paint);
	//	}

	public void paintEdges(Canvas canvas, Node root) {
		if (!root.isAtom()) {
			RectF b1 = getBoundsOfNode(root);
			double x1 = b1.centerX();
			double y1 = b1.centerY();
			for (Node child : root.getChildren()) {
				RectF b2 = getBoundsOfNode(child);
				canvas.drawLine((int) x1, (int) y1, (int) b2.centerX(),
						(int) b2.centerY(), paint);

				paintEdges(canvas, child);
			}
		}
	}

	public void paintNode(Canvas canvas, Node node) {
		RectF bounds = getBoundsOfNode(node);
		System.out.println("Node: " + node + " has bounds " + bounds);
		//		canvas.drawRect((float) bounds.getX(),(float) (bounds.getY() + bounds.getHeight()),
		//				(float) (bounds.getX() + bounds.getWidth()),(float) bounds.getY(), paint);
		
		canvas.drawRect(bounds, backgroundPaint);
		canvas.drawRect(0, 0, 100, 100, backgroundPaint);
		canvas.drawText(node.getValue(), (float) bounds.centerX(), 
				(float) bounds.centerY(), paint);
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

	public RectF getBoundsOfNode(Node node) {
		return treeLayout.getNodeBounds().get(node);
	}

	public void setTree(FormationTree tree) {
		this.tree = tree;
	}

}


