package app;

import java.util.SortedSet;
import java.util.Stack;

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
import android.view.SubMenu;
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
	Stack<TextView> redoStack;
	LinearLayout topLinearLayout;
	LinearLayout bottomLinearLayout;
	LinearLayout redoLinearLayout;

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
		redoStack = new Stack<TextView>();

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
		redoLinearLayout = (LinearLayout) findViewById(R.id.redo_linear_layout);

		addTextViewToTop(new TextView(context), start);
		addTextViewToBottom(new TextView(context), end);

		topRulesList = new PopupMenu(this, topFormationTree);
		topRulesList.setOnMenuItemClickListener(this);

		bottomRulesList = new PopupMenu(this, bottomFormationTree);
		bottomRulesList.setOnMenuItemClickListener(this);
	}

	public void setUpTextView(TextView textView, int id, String text) {
		textView.setId(id);
		textView.setText(text);
		textView.setTextSize(20);
		textView.setClickable(true);
		textView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
	}
	
	public void setUpUndoneTextView(TextView undone) {
		undone.setId(redoStack.size());
		undone.setTextColor(Color.argb(100, 0, 0, 0));
	}
	
	public void toggleVisibility(DrawView tree) {
		if (tree.getVisibility() == View.GONE)
			tree.setVisibility(View.VISIBLE);
		else
			tree.setVisibility(View.GONE);
	}
	
	public void redo(TextView textView) {
		textView.setTextColor(Color.argb(255, 0, 0, 0));
		redoLinearLayout.removeView(textView);
		redoStack.pop();
	}
	
	public void setUpTree(FormationTree tree, Stack<TextView> stack, DrawView formationTree) {
		tree = compiler.compile(stack.peek().getText().toString());
		formationTree.setTree(tree);
	}
	
	public void addTextViewToTop(TextView textView, String text) {
		setUpTextView(textView, topStack.size(), text);
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.equals(topStack.peek())) {
					toggleVisibility(topFormationTree);
				} else {
					// Undo to position in list clicked
					// TODO: Add redo functionality
					int position = view.getId();
					for (int i = topStack.size() - 1; i > position; --i) {
						TextView undone = (TextView) topLinearLayout.findViewById(i);
						setUpUndoneTextView(undone);
						undone.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								int undoPos = v.getId();
								
								for (int j = redoStack.size() - 1; j >= undoPos; --j) {
									TextView textView = (TextView) redoLinearLayout.findViewById(j);
									redo(textView);
									addTextViewToTop(new TextView(context), textView.getText().toString());
									setUpTree(topTree, topStack, topFormationTree);
								}
							}
						});
						topLinearLayout.removeViewAt(i);
						redoLinearLayout.addView(undone, 0);
						topStack.pop();
						redoStack.push(undone);
					}
					setUpTree(topTree, topStack, topFormationTree);
				}
			}
		});
		topLinearLayout.addView(textView);
		topStack.push(textView);
		oldTopStackSize++;
	}
	
	public void addTextViewToBottom(TextView textView, String text) {
		setUpTextView(textView, bottomStack.size(), text);

		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.equals(bottomStack.peek())) {
					// Currently apply random rule to current equivalence
					// TODO: Set rules list and choose rule to apply
					toggleVisibility(topFormationTree);

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

		PopupMenu rulesList;

		if (selectedTree == topTree)
			rulesList = topRulesList;
		else
			rulesList = bottomRulesList;
		
		rulesList.getMenu().clear();

		int key;
		for (int i = 0; i < rules.size(); ++i) {
			key = rules.keyAt(i);

			// is user input required for new atom
			if (key < 69) {
				rulesList.getMenu().add(Menu.NONE, key, Menu.NONE, rules.get(key));
			} else {
				SubMenu sub = rulesList.getMenu().addSubMenu(Menu.NONE, key, Menu.NONE, rules.get(key));
				SortedSet<String> vars = topTree.getVariables();
				vars.addAll(bottomTree.getVariables());

				for (String v : vars) {
					sub.add(Menu.NONE, key, Menu.NONE, rules.get(key) + " using " + v);
				}
			}
		}
		rulesList.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();

		if (id < 69) {
			re.applyRuleFromBitSet(id, selectedTree, selected, null);
			itemClicked();
		} else {
			String title = item.getTitle().toString();
			if (title.contains(" using ")) {
				String[] splits = title.split(" using ");
				String variable = splits[1];
				re.applyRuleFromBitSet(id, selectedTree, selected, variable);
				itemClicked();
			} else
				return false;
		}
		redoLinearLayout.removeAllViews();
		return true;
	}
	
	public void itemClicked() {
		if (selectedTree == topTree) {
			addTextViewToTop(new TextView(context), selectedTree.toString());
			topRulesList.dismiss();
		} else {
			addTextViewToBottom(new TextView(context), selectedTree.toString());
			bottomRulesList.dismiss();
		}
	}
	
}
