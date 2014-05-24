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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
	Stack<TextView> topRedoStack;
	Stack<TextView> bottomRedoStack;
	
	LinearLayout topLinearLayout;
	LinearLayout bottomLinearLayout;
	LinearLayout topRedoLinearLayout;
	LinearLayout bottomRedoLinearLayout;

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
		topRedoStack = new Stack<TextView>();
		bottomRedoStack = new Stack<TextView>();

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
		topRedoLinearLayout = (LinearLayout) findViewById(R.id.top_redo_linear_layout);
		bottomRedoLinearLayout = (LinearLayout) findViewById(R.id.bottom_redo_linear_layout);

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
	
	public void setUpUndoneTextView(TextView undone, Stack<TextView> stack) {
		undone.setId(stack.size());
		undone.setTextColor(Color.argb(100, 0, 0, 0));
	}
	
	public void toggleVisibility(DrawView tree) {
		if (tree.getVisibility() == View.GONE)
			tree.setVisibility(View.VISIBLE);
		else
			tree.setVisibility(View.GONE);
	}
	
	public void redo(TextView textView, LinearLayout ll, Stack<TextView> stack) {
		textView.setTextColor(Color.argb(255, 0, 0, 0));
		ll.removeView(textView);
		stack.pop();
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
					// Undo/redo
					int position = view.getId();
					
					for (int i = topStack.size() - 1; i > position; --i) {
						TextView undone = (TextView) topLinearLayout.findViewById(i);
						setUpUndoneTextView(undone, topRedoStack);
						
						undone.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								int undoPos = v.getId();
								
								for (int j = topRedoStack.size() - 1; j >= undoPos; --j) {
									TextView textView = (TextView) topRedoLinearLayout.findViewById(j);
									redo(textView, topRedoLinearLayout, topRedoStack);
									addTextViewToTop(new TextView(context), textView.getText().toString());
									setUpTree(topTree, topStack, topFormationTree);
								}
							}
						});
						topLinearLayout.removeViewAt(i);
						topRedoLinearLayout.addView(undone, 0);
						topStack.pop();
						topRedoStack.push(undone);
					}
					setUpTree(topTree, topStack, topFormationTree);
				}
			}
		});
		topLinearLayout.addView(textView);
		topStack.push(textView);
	}
	
	public void addTextViewToBottom(TextView textView, String text) {
		setUpTextView(textView, bottomStack.size(), text);

		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.equals(bottomStack.peek())) {
					toggleVisibility(bottomFormationTree);
				} else {
					// Undo/redo
					int position = view.getId();
					
					for (int i = bottomStack.size() - 1; i > position; --i) {
						TextView undone = (TextView) bottomLinearLayout.findViewById(i);
						setUpUndoneTextView(undone, bottomRedoStack);
						
						undone.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								int undoPos = v.getId();
								
								for (int j = bottomRedoStack.size() - 1; j >= undoPos; --j) {
									TextView textView = (TextView) bottomRedoLinearLayout.findViewById(j);
									redo(textView, bottomRedoLinearLayout, bottomRedoStack);
									addTextViewToBottom(new TextView(context), textView.getText().toString());
									setUpTree(bottomTree, bottomStack, bottomFormationTree);
								}
							}
						});
						bottomLinearLayout.removeView(undone);
						bottomRedoLinearLayout.addView(undone);
						bottomStack.pop();
						bottomRedoStack.push(undone);
					}
					setUpTree(bottomTree, bottomStack, bottomFormationTree);
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
		topRedoLinearLayout.removeAllViews();
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
		
		// check for completion
		if (equivalenceComplete(topTree.toString(), bottomTree.toString())) {
			CharSequence text = "Equivalence complete!";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		
		// check for cycles
		if (selectedTree == topTree) {
			for (TextView textView : topStack) {
				if (textView.getText().toString().equals(topTree)) {
					CharSequence text = "Cycle found!";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		} else {
		
		}
	}
	
}
