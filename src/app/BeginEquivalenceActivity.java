package app;

import java.util.BitSet;
import java.util.Stack;

import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeManipulation.RuleEngine;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.equivalencesapp.R;

public class BeginEquivalenceActivity extends Activity implements android.widget.PopupMenu.OnMenuItemClickListener {

	Context context;
	RuleEngine re;
	Compiler compiler;

	FormationTree topTree;
	FormationTree bottomTree;
	String start;
	String end;
	
	Node selected;
	FormationTree selectedTree;

	Stack<TextView> topStack;
	Stack<TextView> bottomStack;
	LinearLayout topLinearLayout;
	LinearLayout bottomLinearLayout;

	int oldTopStackSize;

	PopupMenu topRulesList;
	PopupMenu bottomRulesList;
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

		topLinearLayout = (LinearLayout) findViewById(R.id.top_linear_layout);
		bottomLinearLayout = (LinearLayout) findViewById(R.id.bottom_linear_layout);
		
		addTextViewToTop(new TextView(context), start);
		addTextViewToBottom(new TextView(context), end);

		topRulesList = new PopupMenu(this, topFormationTree);
		topRulesList.setOnMenuItemClickListener(this);
		
		bottomRulesList = new PopupMenu(this, bottomFormationTree);
		bottomRulesList.setOnMenuItemClickListener(this);
	}

	public void addTextViewToTop(TextView textView, String text) {
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
					
					// Remove redo equivalences
					//					for (int i = topStack.size() - 1; i < oldTopStackSize; ++i) {
					//						topLinearLayout.removeViewAt(i);
					//						oldTopStackSize--;
					//					}

//					if (equivalenceComplete(topTree.toString(), end))
//						rulesList.setText("COMPLETE");

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
					topFormationTree.setTree(topTree);
				}
			}
		});

		topLinearLayout.addView(textView);
		topStack.push(textView);
		oldTopStackSize++;
	}

	public void addTextViewToBottom(TextView textView, String text) {

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

//					if (equivalenceComplete(bottomTree.toString(), start))
//						rulesList.setText("COMPLETE");

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

	public void setRules(SparseArray<String> rules, Node selected, FormationTree selectedTree) {
		this.selected = selected;
		this.selectedTree = selectedTree;
		
		int key;
		for (int i = 0; i < rules.size(); ++i) {
			key = rules.keyAt(i);
			if (selectedTree == topTree)
				topRulesList.getMenu().add(Menu.NONE, key, Menu.NONE, rules.get(key));
			else
				bottomRulesList.getMenu().add(Menu.NONE, key, Menu.NONE, rules.get(key));
		}
		if (selectedTree == topTree)
			topRulesList.show();
		else
			bottomRulesList.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();
		re.applyRuleFromBitSet(id, selectedTree, selected, null);

		if (selectedTree == topTree) {
			addTextViewToTop(new TextView(context), selectedTree.toString());
			topRulesList.dismiss();
		} else {
			addTextViewToBottom(new TextView(context), selectedTree.toString());
			bottomRulesList.dismiss();
		}
		
		return true;
	}
}
