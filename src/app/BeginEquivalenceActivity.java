package app;

import java.util.Iterator;
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

public class BeginEquivalenceActivity extends Activity implements android.widget.PopupMenu.OnMenuItemClickListener, android.widget.PopupMenu.OnDismissListener {

	private Context context;
	private RuleEngine re;
	private Compiler compiler;

	private FormationTree topTree;
	private FormationTree bottomTree;
	private String start;
	private String end;

	private View centerLine;
	private Node selected;
	private FormationTree selectedTree;

	private Stack<TextView> topStack;
	private Stack<TextView> bottomStack;
	private Stack<TextView> topRedoStack;
	private Stack<TextView> bottomRedoStack;

	private LinearLayout topLinearLayout;
	private LinearLayout bottomLinearLayout;
	private LinearLayout topRedoLinearLayout;
	private LinearLayout bottomRedoLinearLayout;

	private PopupMenu topRulesList;
	private PopupMenu bottomRulesList;
	private DrawView topFormationTree;
	private DrawView bottomFormationTree;
	
	public static int min_user_input_required;

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
		
		min_user_input_required = re.getMinUserInputRequired();

		topFormationTree = (DrawView) findViewById(R.id.top_formation_tree);
		topFormationTree.setTree(topTree);
		bottomFormationTree = (DrawView) findViewById(R.id.bottom_formation_tree);
		bottomFormationTree.setTree(bottomTree);

		topLinearLayout = (LinearLayout) findViewById(R.id.top_linear_layout);
		bottomLinearLayout = (LinearLayout) findViewById(R.id.bottom_linear_layout);
		topRedoLinearLayout = (LinearLayout) findViewById(R.id.top_redo_linear_layout);
		bottomRedoLinearLayout = (LinearLayout) findViewById(R.id.bottom_redo_linear_layout);

		centerLine = findViewById(R.id.redo_line);
		addTextViewToTop(new TextView(context), start);
		addTextViewToBottom(new TextView(context), end);
		addLine(bottomLinearLayout, false, 0);

		topRulesList = new PopupMenu(this, topFormationTree);
		topRulesList.setOnMenuItemClickListener(this);
		topRulesList.setOnDismissListener(this);
		bottomRulesList = new PopupMenu(this, bottomFormationTree);
		bottomRulesList.setOnMenuItemClickListener(this);
		bottomRulesList.setOnDismissListener(this);
	}

	public void setUpTextView(TextView textView, int id, String text) {
		textView.setId(id);
		textView.setText(text);
		textView.setTextSize(20);
		textView.setGravity(Gravity.CENTER);
		textView.setClickable(true);

		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = 8;
		llp.setMargins(margin, margin, margin, margin);
		textView.setLayoutParams(llp);
	}

	public void addLine(LinearLayout ll, boolean above, int tag) {
		View line = new View(context);

		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		line.setLayoutParams(llp);
		line.setBackgroundColor(Color.LTGRAY);
		line.setTag(tag);

		if (above)
			ll.addView(line, 0);
		else
			ll.addView(line);
	}

	public void setUpUndoneTextView(TextView undone, View line, Stack<TextView> stack) {
		int id = stack.size();
		undone.setId(id);
		line.setTag(id);
		undone.setTextColor(Color.argb(100, 0, 0, 0));
		centerLine.setVisibility(View.VISIBLE);
	}

	public void toggleVisibility(DrawView tree) {
		if (tree.getVisibility() == View.GONE)
			tree.setVisibility(View.VISIBLE);
		else
			tree.setVisibility(View.GONE);
	}

	public void redo(TextView textView, LinearLayout ll, Stack<TextView> stack, View line) {
		textView.setTextColor(Color.argb(255, 0, 0, 0));
		ll.removeView(textView);
		ll.removeView(line);
		stack.pop();
	}

	public void setUpTree(FormationTree tree, Stack<TextView> stack, DrawView formationTree) {
		tree = compiler.compile(stack.peek().getText().toString());
		formationTree.setTree(tree);
	}

	public void addTextViewToTop(TextView textView, String text) {
		int id = topStack.size();
		setUpTextView(textView, id, text);

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
						View line = topLinearLayout.findViewWithTag(i);
						setUpUndoneTextView(undone, line, topRedoStack);

						undone.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								int undoPos = v.getId();

								for (int j = topRedoStack.size() - 1; j >= undoPos; --j) {
									TextView textView = (TextView) topRedoLinearLayout.findViewById(j);
									View line = topRedoLinearLayout.findViewWithTag(j);
									redo(textView, topRedoLinearLayout, topRedoStack, line);
									addTextViewToTop(new TextView(context), textView.getText().toString());
									setUpTree(topTree, topStack, topFormationTree);

									if (j == 0)
										centerLine.setVisibility(View.INVISIBLE);
								}
							}
						});
						topLinearLayout.removeView(undone);
						topLinearLayout.removeView(line);
						topRedoLinearLayout.addView(undone, 0);
						topRedoLinearLayout.addView(line, 0);
						topStack.pop();
						topRedoStack.push(undone);
					}
					setUpTree(topTree, topStack, topFormationTree);
				}
			}
		});
		topLinearLayout.addView(textView);
		addLine(topLinearLayout, false, id);
		topStack.push(textView);
	}

	public void addTextViewToBottom(TextView textView, String text) {
		int id = bottomStack.size();
		setUpTextView(textView, id, text);

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
						View line = bottomLinearLayout.findViewWithTag(i);
						setUpUndoneTextView(undone, line, bottomRedoStack);

						undone.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								int undoPos = v.getId();

								for (int j = bottomRedoStack.size() - 1; j >= undoPos; --j) {
									TextView textView = (TextView) bottomRedoLinearLayout.findViewById(j);
									View line = bottomRedoLinearLayout.findViewWithTag(j);
									redo(textView, bottomRedoLinearLayout, bottomRedoStack, line);
									addTextViewToBottom(new TextView(context), textView.getText().toString());
									setUpTree(bottomTree, bottomStack, bottomFormationTree);

									if (j == 0)
										centerLine.setVisibility(View.INVISIBLE);
								}
							}
						});
						bottomLinearLayout.removeView(undone);
						bottomLinearLayout.removeView(line);
						bottomRedoLinearLayout.addView(undone);
						bottomRedoLinearLayout.addView(line);
						bottomStack.pop();
						bottomRedoStack.push(undone);
					}
					setUpTree(bottomTree, bottomStack, bottomFormationTree);
				}
			}
		});
		bottomLinearLayout.addView(textView, 0);
		addLine(bottomLinearLayout, true, bottomStack.size());
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
			if (key < min_user_input_required) {
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

		if (id < min_user_input_required) {
			re.applyRuleFromBitSet(id, selectedTree, selected, null);
			itemClicked();
		} else {
			String title = item.getTitle().toString();
			if (title.contains(" using ")) {
				String[] splits = title.split(" using ");
				String variable = splits[1];
				re.applyRuleFromBitSet(id, selectedTree, selected, variable);
				itemClicked();
			} else {
				return false;
			}
		}
		topRedoLinearLayout.removeAllViews();
		bottomRedoLinearLayout.removeAllViews();
		centerLine.setVisibility(View.INVISIBLE);
		return true;
	}

	public void itemClicked() {
		if (selectedTree == topTree) {
			addTextViewToTop(new TextView(context), selectedTree.toString());
		} else {
			addTextViewToBottom(new TextView(context), selectedTree.toString());
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
		Stack<TextView> selectedStack = (selectedTree == topTree) ? topStack : bottomStack;

		Iterator<TextView> it = selectedStack.iterator();
		for (int i = 0; i < selectedStack.size() - 1 && it.hasNext(); ++i) {
			TextView textView = it.next();
			if (textView.getText().toString().equals(selectedTree.toString())) {
				generateToast("Cycle found!", Toast.LENGTH_LONG);
			}
		}
	}

	public void generateToast(CharSequence text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public void onDismiss(PopupMenu menu) {
		if (menu == topRulesList) {
			topFormationTree.deselectNode();
			topFormationTree.invalidate();
		} else {
			bottomFormationTree.deselectNode();
			bottomFormationTree.invalidate();
		}
	}
}
