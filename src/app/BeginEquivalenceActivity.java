package app;

import java.util.BitSet;
import java.util.Stack;

import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeManipulation.RuleEngine;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
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
	DrawView topFormationTree;
	DrawView bottomFormationTree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_begin_equivalence);

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

		topFormationTree = (DrawView) findViewById(R.id.top_formation_tree);
		topFormationTree.setTree(topTree);

		bottomFormationTree = (DrawView) findViewById(R.id.bottom_formation_tree);
		bottomFormationTree.setTree(bottomTree);
		
//		Spinner dropdown = (Spinner) findViewById(R.id.rule_spinner);
//		String[] items = new String[]{"1", "2", "three"};
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
//		dropdown.setAdapter(adapter);

		addTextViewToTop(new TextView(context), start);
		addTextViewToBottom(new TextView(context), end);

		// Set the user interface layout for this Activity
//		rulesList = (TextView) findViewById(R.id.rules_list);
		rulesList = new TextView(this);
		rulesList.setTextSize(20);
		LinearLayout outerLayout = (LinearLayout) findViewById(R.id.linear_layout);
		outerLayout.addView(rulesList, 2);
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
					if (topFormationTree.getVisibility() == View.GONE)
						topFormationTree.setVisibility(View.VISIBLE);
					else
						topFormationTree.setVisibility(View.GONE);

//					Node node = topTree.getRoot();
//					BitSet bs = re.getApplicableRules(topTree, node);
////
//					String rules = "";
//					for (int i = 0; i < re.rulesToString(bs, topTree, node).length; ++i) {
//						rules += re.rulesToString(bs, topTree, node)[i] + "\n";
//					}
//					rulesList.setText(rules);

					// Remove redo equivalences
					//					for (int i = topStack.size() - 1; i < oldTopStackSize; ++i) {
					//						topLinearLayout.removeViewAt(i);
					//						oldTopStackSize--;
					//					}

//					re.applyRandomRule(bs, topTree, (BinaryOperator) node);
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

//					topTree = compiler.compile(topStack.peek().getText().toString());
//					topFormationTree.setTree(topTree);
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
					bottomFormationTree.setTree(bottomTree);
				}
			}
		});

		bottomLinearLayout.addView(textView, 0);
		bottomStack.push(textView);
	}

	public boolean equivalenceComplete(String top, String bottom) {
		return top.equals(bottom);
	}

	public void setRules(String rules) {
		rulesList.setText(rules);
	}
}
