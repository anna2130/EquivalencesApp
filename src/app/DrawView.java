package app;

import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.UnaryOperator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
	Paint paint;
	Context context;
	
	FormationTree tree;
	int offset;
	int nodeHeight;

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
		context = c;
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
		Node root = tree.getRoot();
		drawTree(canvas, root, getWidth() / 2, 0);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
	    return super.onTouchEvent(event);
	}
	
	public void drawTree(Canvas canvas, Node node, int width, int widthOffset) {
		int height = (node.getDepth() * nodeHeight) + offset;
		String val = node.getValue();
		
		canvas.drawText(val, width, height, paint);
		
		if (node.hasChildren()) {
			if (node instanceof BinaryOperator) {
				Node leftChild = ((BinaryOperator) node).getLeftChild();
				Node rightChild = ((BinaryOperator) node).getRightChild();

				drawTree(canvas, leftChild, width / 2, 0);
				drawTree(canvas, rightChild, width + width / 2, width);
			} else if (node instanceof UnaryOperator) {
				Node child = ((UnaryOperator) node).getChild();
				drawTree(canvas, child, width, 0);
			}
		}

//		this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height + offset));
	}

	public void setTree(FormationTree tree) {
		this.tree = tree;
	}
	
}


