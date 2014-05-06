package app;

import java.util.ArrayList;
import java.util.BitSet;

import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeManipulation.RuleApplicator;
import treeManipulation.RuleSelector;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.equivalencesapp.R;

public class BeginEquivalenceActivity extends ActionBarActivity {

	Context context;
	RuleSelector rs;
	RuleApplicator ra;
    Compiler compiler;
    FormationTree tree;
	
	String start;
    String end;
	
    ListView topListView;
    ListView bottomListView;
    ArrayList<String> forward;
    ArrayList<String> backward;
    ArrayAdapter<String> topAdapter;
    ArrayAdapter<String> bottomAdapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    // Get the equivalences from the intent
		context = this;
	    Intent intent = getIntent();
	    start = intent.getStringExtra(MainActivity.START_EQUIVALENCE);
	    end = intent.getStringExtra(MainActivity.END_EQUIVALENCE);
	    
	    rs = new RuleSelector();
	    ra = new RuleApplicator();
	    compiler = new Compiler();
	    tree = compiler.compile(start);
	    
	    // Set the user interface layout for this Activity
	    setContentView(R.layout.fragment_begin_equivalence);
	    
	    topListView = (ListView) findViewById(R.id.start_equivalence);
	    forward = new ArrayList<String>();
	    forward.add(start);
	    topAdapter = new ArrayAdapter<String>(context, 
	    		android.R.layout.simple_list_item_1, forward);
	    topListView.setAdapter(topAdapter);
	    topListView.setOnItemClickListener(new OnItemClickListener() {
	    	
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				int topChild = topListView.getChildCount();
				if (position == topChild - 1) {
					// Currently apply random rule to current equivalence
					// TODO: Set rules list and choose rule to apply
					Node node = tree.getRoot();
					BitSet bs = rs.getApplicableRules(tree, node);
					ra.applyRandomRule(bs, tree, (BinaryOperator) node);
					forward.add(tree.toString());
			        topAdapter.notifyDataSetChanged();
			        
			        if (equivalenceComplete(tree.toString(), end))
			        	Log.d("DEBUG", "Complete");
			        
				} else {
					// Undo to position in list clicked
					// TODO: Add redo functionality
					for (int i = forward.size() - 1; i > position; --i) {
						forward.remove(i);
					}
					
					tree = compiler.compile(forward.get(position));
			        topAdapter.notifyDataSetChanged();
				}
			}
		});

	    TextView bottomTextView = (TextView) findViewById(R.id.end_equivalence);
	    bottomTextView.setTextSize(40);
	    bottomTextView.setText(end);
	    bottomTextView.setMovementMethod(LinkMovementMethod.getInstance());
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
	
	public void setRulesList(final FormationTree tree, int key, int depth) {
		final Node node = tree.findNode(key, depth);
		final BitSet bs = rs.getApplicableRules(tree, node);
		String[] rules = rs.rulesToString(bs, tree, node);
    	final ListView listview = (ListView) findViewById(R.id.rules_list);
    	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
			android.R.layout.simple_list_item_1, android.R.id.text1, rules);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("DEBUG", ""+bs.toString()+" "+position);
				
				ra.applyRuleFromBitSet(bs, position, tree, (BinaryOperator) node);
				
				TextView topTextView = (TextView) findViewById(R.id.start_equivalence);
//			    topTextView.setText(setClickableOperators(tree, tree.toString()));
				topTextView.setText(tree.toString());
			    
			    String[] rules = new String[] {};
			    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_1, android.R.id.text1, rules);
				listview.setAdapter(adapter);
			}
		});
	}

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
