package app;

import java.util.Map;

import treeBuilder.FormationTree;
import treeBuilder.Node;
import abego.DefaultConfiguration;
import abego.FixedNodeExtentProvider;
import abego.TreeLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
	private Paint linePaint;
	private Paint fontPaint;
	private Paint backgroundPaint;

	private FormationTree tree;
	private int offset;
	private int shift;

	private static double gapBetweenLevels = 60;
	private static double gapBetweenNodes = 35;
	private static float fontSize = 30;

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

		linePaint = new Paint();
		linePaint.setColor(Color.BLACK);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(2);
		
		fontPaint = new Paint();
		fontPaint.setColor(Color.BLACK);
		fontPaint.setTextSize(fontSize);
		fontPaint.setTextAlign(Align.CENTER);
		
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.WHITE);
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
		float rootX = getBoundsOfNode(tree.getRoot()).centerX();
		float canvasCenterX = canvas.getWidth() / 2;
		shift = (int) (canvasCenterX - rootX);
		
		paintEdges(canvas, tree.getRoot());
		
		for (Node node : treeLayout.getNodeBounds().keySet()) {
			paintNode(canvas, node);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Map<Node, RectF> map = treeLayout.getNodeBounds();
		System.out.println("Touched at: " + event.getX() + " " + event.getY());
		return super.onTouchEvent(event);
	}

	public void paintEdges(Canvas canvas, Node root) {
		if (!root.isAtom()) {
			RectF b1 = getBoundsOfNode(root);
			int x1 = (int) b1.centerX();
			int y1 = (int) b1.centerY();
			for (Node child : root.getChildren()) {
				RectF b2 = getBoundsOfNode(child);
				canvas.drawLine(x1 + shift, y1 + offset, b2.centerX() + shift, b2.centerY() + offset, linePaint);

				paintEdges(canvas, child);
			}
		}
	}

	public void paintNode(Canvas canvas, Node node) {
		RectF bounds = getBoundsOfNode(node);
		System.out.println("Node: " + node + " has bounds " + bounds);
		//		canvas.drawRect((float) bounds.getX(),(float) (bounds.getY() + bounds.getHeight()),
		//				(float) (bounds.getX() + bounds.getWidth()),(float) bounds.getY(), paint);

		float xpos = bounds.centerX();
		float ypos = ((bounds.bottom + bounds.top) / 2 - ((fontPaint.descent() + fontPaint.ascent()) / 2));

		canvas.drawCircle(bounds.centerX() + shift, bounds.centerY() + offset, 25, backgroundPaint);
		canvas.drawCircle(bounds.centerX() + shift, bounds.centerY() + offset, 25, linePaint);
		canvas.drawText(node.getValue(), xpos + shift, ypos + offset, fontPaint);
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


