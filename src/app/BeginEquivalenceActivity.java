package app;

import java.util.BitSet;
import java.util.Stack;

import treeBuilder.Atom;
import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.TreeIterator;
import treeManipulation.RuleApplicator;
import treeManipulation.RuleSelector;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.equivalencesapp.R;

public class BeginEquivalenceActivity extends ActionBarActivity {

	final Context context = this;
	Stack<FormationTree> forward;
	Stack<FormationTree> backward;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    // Get the message from the intent
	    Intent intent = getIntent();
	    String start = intent.getStringExtra(MainActivity.START_EQUIVALENCE);
	    String end = intent.getStringExtra(MainActivity.END_EQUIVALENCE);
	    
	    // Set up stack for previous equivalences
	    forward = new Stack<FormationTree>();
	    backward = new Stack<FormationTree>();
	    
	    Compiler compiler = new Compiler();
	    FormationTree tree = compiler.compile(start);
	    forward.push(tree);
	    
	    // Set the user interface layout for this Activity
	    setContentView(R.layout.fragment_begin_equivalence);
//    	setRulesList(forward.peek(), 0, 0);
	    
	    SpannableString ss = setClickableOperators(tree, start);
	    
	    // Create the text views
	    TextView topTextView = (TextView) findViewById(R.id.start_equivalence);
	    topTextView.setTextSize(40);
	    topTextView.setText(ss);
	    topTextView.setMovementMethod(LinkMovementMethod.getInstance());

	    TextView bottomTextView = (TextView) findViewById(R.id.end_equivalence);
	    bottomTextView.setTextSize(40);
	    bottomTextView.setText(end);
	    bottomTextView.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	public SpannableString setClickableOperators(FormationTree tree, String start) {
		SpannableString ss = new SpannableString(forward.peek().toString());
	    // Make each operator node clickable
	    TreeIterator iterator = new TreeIterator(tree.getRoot());
	    String s = start;
	    int i = 0;
	    
	    while (iterator.hasNext()) {
	    	final Node next = iterator.next();
	    	
	    	while (s.charAt(i) == '(' || s.charAt(i) == ')')
	    		++i;
	    	
	    	if (!(next instanceof Atom)) {
		    	ClickableSpan clickableSpan = new ClickableSpan() {
			        @Override
			        public void onClick(View textView) {
			        	setRulesList(forward.peek(), next.getKey(), next.getDepth());
			        }
			    };
			    ss.setSpan(clickableSpan, i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    	}
	    	
		    ++i;
	    }
	    return ss;
	}
	
	public void setRulesList(final FormationTree tree, int key, int depth) {
		final Node node = tree.findNode(key, depth);
		
		RuleSelector rs = new RuleSelector();
		final BitSet bs = rs.getApplicableRules(tree, node);
		String[] rules = rs.rulesToString(bs, tree, node);
		
    	ListView listview = (ListView) findViewById(R.id.rules_list);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
			android.R.layout.simple_list_item_1, android.R.id.text1, rules);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("DEBUG", "Clicked");
				
				RuleApplicator ra = new RuleApplicator();
				ra.applyRuleFromBitSet(bs, position, tree, (BinaryOperator) node);
				
				TextView topTextView = (TextView) findViewById(R.id.start_equivalence);
			    topTextView.setText(setClickableOperators(tree, tree.toString()));
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_begin_equivalence, container, false);
			return rootView;
		}
	}

}
