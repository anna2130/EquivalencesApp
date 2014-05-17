package app;

import java.util.BitSet;
import java.util.Stack;

import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeManipulation.RuleApplicator;
import treeManipulation.RuleEngine;
import treeManipulation.RuleSelector;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.equivalencesapp.R;

public class BeginEquivalenceActivity extends ActionBarActivity {

	Context context;
	RuleEngine re;
    Compiler compiler;
    
    FormationTree topTree;
    FormationTree bottomTree;
	String start;
    String end;
    
    Stack<TextView> topStack;
    Stack<TextView> bottomStack;
    LinearLayout topLinearLayout;
    LinearLayout bottomLinearLayout;
    
    int oldTopStackSize;
    
    TextView rulesList;
    TextureView formationTree;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    // Get the equivalences from the intent
		context = this;
	    Intent intent = getIntent();
	    start = intent.getStringExtra(MainActivity.START_EQUIVALENCE);
	    end = intent.getStringExtra(MainActivity.END_EQUIVALENCE);
	    
	    topStack = new Stack<TextView>();
	    bottomStack = new Stack<TextView>();
	    
	    oldTopStackSize = 0;
	    
	    re = new RuleEngine();
	    compiler = new Compiler();
	    topTree = compiler.compile(start);
	    bottomTree = compiler.compile(end);
	    
	    // Set the user interface layout for this Activity
	    setContentView(R.layout.fragment_begin_equivalence);
	    rulesList = (TextView) findViewById(R.id.rules_list);
	    
		addTextViewToTop(new TextView(context), start);
		addTextViewToBottom(new TextView(context), end);
	}
	
	public void addTextViewToTop(TextView textView, String text) {
		topLinearLayout = (LinearLayout) findViewById(R.id.top_linear_layout);
	    
	    textView.setId(topStack.size());
	    textView.setText(text);
	    textView.setTextSize(20);
	    textView.setClickable(true);
	    textView.setLayoutParams(new LinearLayout.LayoutParams(
	    		LinearLayout.LayoutParams.WRAP_CONTENT,
	    		LinearLayout.LayoutParams.WRAP_CONTENT));
	    
	    textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.equals(topStack.peek())) {
					// Currently apply random rule to current equivalence
					// TODO: Set rules list and choose rule to apply
					Node node = topTree.getRoot();
					BitSet bs = re.getApplicableRules(topTree, node);
					
					String rules = "";
					for (int i = 0; i < re.rulesToString(bs, topTree, node).length; ++i) {
						rules += re.rulesToString(bs, topTree, node)[i] + "\n";
					}
					rulesList.setText(rules);
					
			        // Remove redo equivalences
//					for (int i = topStack.size() - 1; i < oldTopStackSize; ++i) {
//						topLinearLayout.removeViewAt(i);
//						oldTopStackSize--;
//					}
					
					re.applyRandomRule(bs, topTree, (BinaryOperator) node);
					addTextViewToTop(new TextView(context), topTree.toString());
			        
			        if (equivalenceComplete(topTree.toString(), end))
						rulesList.setText("COMPLETE");
			        
//				} else if (view.getTag() == "Undone") {
//					int position = view.getId();
//					for (int i = topStack.size() - 1; i <= position; ++i) {
//						TextView ruleView = (TextView) topLinearLayout.findViewById(i);
//						ruleView.setTextColor(Color.argb(255, 0, 0, 0));
//						ruleView.setTag("");
////						topLinearLayout.removeViewAt(i);
////						topStack.pop();
//					}
				} else {
//					// Undo to position in list clicked
//					// TODO: Add redo functionality
			        int position = view.getId();
					for (int i = topStack.size() - 1; i > position; --i) {
						TextView ruleView = (TextView) topLinearLayout.findViewById(i);
						ruleView.setTextColor(Color.argb(100, 0, 0, 0));
						ruleView.setTag("Undone");
						topLinearLayout.removeViewAt(i);
						topStack.pop();
					}
					
					topTree = compiler.compile(topStack.peek().getText().toString());
				}
			}
		});
	    
	    topLinearLayout.addView(textView);
	    topStack.push(textView);
	    oldTopStackSize++;
	}
	
	public void addTextViewToBottom(TextView textView, String text) {
	    bottomLinearLayout = (LinearLayout) findViewById(R.id.bottom_linear_layout);

	    textView.setId(bottomStack.size());
	    textView.setText(text);
	    textView.setTextSize(20);
	    textView.setClickable(true);
	    textView.setLayoutParams(new LinearLayout.LayoutParams(
	    		LinearLayout.LayoutParams.WRAP_CONTENT,
	    		LinearLayout.LayoutParams.WRAP_CONTENT));
	    
	    textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.equals(bottomStack.peek())) {
					// Currently apply random rule to current equivalence
					// TODO: Set rules list and choose rule to apply
					Node node = bottomTree.getRoot();
					BitSet bs = re.getApplicableRules(bottomTree, node);
					re.applyRandomRule(bs, bottomTree, (BinaryOperator) node);
					addTextViewToBottom(new TextView(context), bottomTree.toString());
			        
			        if (equivalenceComplete(bottomTree.toString(), start))
						rulesList.setText("COMPLETE");
			        
				} else {
					// Undo to position in list clicked
					// TODO: Add redo functionality
					int position = bottomStack.size() - view.getId() - 1;
					for (int i = 0; i < position; ++i) {
						bottomLinearLayout.removeViewAt(0);
						bottomStack.pop();
					}
					
					bottomTree = compiler.compile(bottomStack.peek().getText().toString());
				}
			}
		});
	    
	    bottomLinearLayout.addView(textView, 0);
	    bottomStack.push(textView);
	}
	
	public boolean equivalenceComplete(String top, String bottom) {
		return top.equals(bottom);
	}
	
//	public SpannableString setClickableOperators(FormationTree tree, String start) {
//		SpannableString ss = new SpannableString(tree.toString());
//	    // Make each operator node clickable
//	    TreeIterator iterator = new TreeIterator(tree.getRoot());
//	    String s = start;
//	    int i = 0;
//	    
//	    while (iterator.hasNext()) {
//	    	final Node next = iterator.next();
//	    	
//	    	while (s.charAt(i) == '(' || s.charAt(i) == ')')
//	    		++i;
//	    	
//	    	if (!(next instanceof Atom)) {
//		    	ClickableSpan clickableSpan = new ClickableSpan() {
//			        @Override
//			        public void onClick(View textView) {
//			        	setRulesList(tree.toString(), next.getKey(), next.getDepth());
//			        }
//			    };
//			    ss.setSpan(clickableSpan, i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//	    	}
//	    	
//		    ++i;
//	    }
//	    return ss;
//	}
	
//	public void setRulesList(final FormationTree tree, int key, int depth) {
//		final Node node = tree.findNode(key, depth);
//		final BitSet bs = rs.getApplicableRules(tree, node);
//		String[] rules = rs.rulesToString(bs, tree, node);
//    	final ListView listview = (ListView) findViewById(R.id.rules_list);
//    	
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
//			android.R.layout.simple_list_item_1, android.R.id.text1, rules);
//		listview.setAdapter(adapter);
//		listview.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Log.d("DEBUG", ""+bs.toString()+" "+position);
//				
//				ra.applyRuleFromBitSet(bs, position, tree, (BinaryOperator) node);
//				
//				TextView topTextView = (TextView) findViewById(R.id.start_equivalence);
////			    topTextView.setText(setClickableOperators(tree, tree.toString()));
//				topTextView.setText(tree.toString());
//			    
//			    String[] rules = new String[] {};
//			    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
//					android.R.layout.simple_list_item_1, android.R.id.text1, rules);
//				listview.setAdapter(adapter);
//			}
//		});
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.begin_equivalence, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
