package app;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.Variable;
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
	private boolean firstOrder;

	private FormationTree topTree;
	private FormationTree bottomTree;
	private String start;
	private String end;

	private View topRedoLine;
	private View bottomRedoLine;
	private Node selected;
	private FormationTree selectedTree;

	private Stack<FormationTree> topTreeStack;
	private Stack<FormationTree> bottomTreeStack;
	
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

	private static int min_user_input_required;
	private static int first_order_rules;
	private static int fo_min_user_input_required;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_begin_equivalence);

		// Get the equivalences from the intent
		context = this;
		Intent intent = getIntent();
		start = intent.getStringExtra(EnterEquivalencesActivity.START_EQUIVALENCE);
		end = intent.getStringExtra(EnterEquivalencesActivity.END_EQUIVALENCE);
		firstOrder = intent.getBooleanExtra(MainActivity.FIRSTORDER, false);

		topStack = new Stack<TextView>();
		bottomStack = new Stack<TextView>();
		topRedoStack = new Stack<TextView>();
		bottomRedoStack = new Stack<TextView>();

		topTreeStack = new Stack<FormationTree>();
		bottomTreeStack = new Stack<FormationTree>();

		re = new RuleEngine(firstOrder);
		compiler = new Compiler(firstOrder);
		topTree = compiler.compile(start);
		bottomTree = compiler.compile(end);

		min_user_input_required = re.getMinUserInputRequired();
		first_order_rules = re.getMinFirstOrderRules();
		fo_min_user_input_required = re.getMinFOUserInputRequired();

		topFormationTree = (DrawView) findViewById(R.id.top_formation_tree);
		topFormationTree.setFirstOrder(firstOrder);
		topFormationTree.setTree(topTree);
		bottomFormationTree = (DrawView) findViewById(R.id.bottom_formation_tree);
		bottomFormationTree.setFirstOrder(firstOrder);
		bottomFormationTree.setTree(bottomTree);

		topLinearLayout = (LinearLayout) findViewById(R.id.top_linear_layout);
		bottomLinearLayout = (LinearLayout) findViewById(R.id.bottom_linear_layout);
		topRedoLinearLayout = (LinearLayout) findViewById(R.id.top_redo_linear_layout);
		bottomRedoLinearLayout = (LinearLayout) findViewById(R.id.bottom_redo_linear_layout);

		topRedoLine = findViewById(R.id.top_redo_line);
		bottomRedoLine = findViewById(R.id.bottom_redo_line);
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
		System.out.println("Undo");
		System.out.println("Text: " + undone.getText());
		int id = stack.size();
		undone.setId(id);
		line.setTag(id);
		undone.setTextColor(Color.argb(100, 0, 0, 0));

		if (stack == topRedoStack)
			topRedoLine.setVisibility(View.VISIBLE);
		else
			bottomRedoLine.setVisibility(View.VISIBLE);
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

	public void setUpTree(FormationTree tree, Stack<FormationTree> treeStack, DrawView formationTree) {
//		tree = treeStack.peek();
		formationTree.setTree(tree);
	}
	
	public void addTextViewToTop(TextView textView, String text) {
		System.out.println("Initial: " + topTreeStack);
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

						// Redo
						undone.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								int undoPos = v.getId();

								for (int j = topRedoStack.size() - 1; j >= undoPos; --j) {
									TextView textView = (TextView) topRedoLinearLayout.findViewById(j);
									View line = topRedoLinearLayout.findViewWithTag(j);
									redo(textView, topRedoLinearLayout, topRedoStack, line);
									addTextViewToTop(new TextView(context), textView.getText().toString());
									topTree = topTreeStack.peek();
									setUpTree(topTree, topTreeStack, topFormationTree);

									if (j == 0)
										topRedoLine.setVisibility(View.INVISIBLE);
								}
							}
						});
						topLinearLayout.removeView(undone);
						topLinearLayout.removeView(line);
						topRedoLinearLayout.addView(undone, 0);
						topRedoLinearLayout.addView(line, 0);
						topStack.pop();
						topTreeStack.pop();
						topRedoStack.push(undone);
						topTree = topTreeStack.peek();
					}
					setUpTree(topTree, topTreeStack, topFormationTree);
				}
			}
		});
		topLinearLayout.addView(textView);
		addLine(topLinearLayout, false, id);
		topStack.push(textView);
		System.out.println("Before: " + topTreeStack);
		
		FormationTree tree = compiler.compile(text);
		topTreeStack.push(tree);
		System.out.println("After: " + topTreeStack);
		System.out.println("Added tree " + tree);
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
									setUpTree(bottomTree, bottomTreeStack, bottomFormationTree);

									if (j == 0)
										bottomRedoLine.setVisibility(View.INVISIBLE);
								}
							}
						});
						bottomLinearLayout.removeView(undone);
						bottomLinearLayout.removeView(line);
						bottomRedoLinearLayout.addView(undone);
						bottomRedoLinearLayout.addView(line);
						bottomStack.pop();
						System.out.println("Pop: " + bottomTreeStack.peek());
						bottomTreeStack.pop();
						bottomRedoStack.push(undone);
						System.out.println("Next: " + bottomTreeStack.peek());
						bottomTree = bottomTreeStack.peek();
					}
					setUpTree(bottomTree, bottomTreeStack, bottomFormationTree);
				}
			}
		});
		bottomLinearLayout.addView(textView, 0);
		addLine(bottomLinearLayout, true, bottomStack.size());
		bottomStack.push(textView);
		
		FormationTree tree = compiler.compile(text);
		bottomTreeStack.push(tree);
	}

	public boolean equivalenceComplete(String top, String bottom) {
		return top.equals(bottom);
	}

	public void setRules(SparseArray<String> rules, Node selected, FormationTree selectedTree) {
		this.selected = selected;
		this.selectedTree = selectedTree;

		PopupMenu rulesList;

		if (isTopTree(selectedTree)) {
			rulesList = topRulesList;
		} else {
			rulesList = bottomRulesList;
		}

		rulesList.getMenu().clear();

		int key;
		for (int i = 0; i < rules.size(); ++i) {
			key = rules.keyAt(i);

			// User input not required
			if (key < min_user_input_required || (key >= first_order_rules && key < fo_min_user_input_required)) {
				rulesList.getMenu().add(Menu.NONE, key, Menu.NONE, rules.get(key));
			} else {
				SubMenu sub = rulesList.getMenu().addSubMenu(Menu.NONE, key, Menu.NONE, rules.get(key));

				if (key >= fo_min_user_input_required && key < 106) {
					SortedSet<String> vars = topTree.getVariables();
					vars.addAll(bottomTree.getVariables());
					Variable[] variables = Variable.values();
					for (Variable v : variables) {
						String s = v.getValue();
						
						// Adding quantifier rules
						if (key == 104 || key == 105) {
							if (s != "┬" && s != "⊥" && !selected.hasFree(s)) {
								Node parent = selected.getParent();
								while (!parent.isBound(s)) {
									if (parent.isRoot()) {
										sub.add(Menu.NONE, key, Menu.NONE, rules.get(key) + " using " + s);
										break;
									} else {
										parent = parent.getParent();
									}
								}
								
							}
						// Replacement rules
						} else if (s != "┬" && s != "⊥" && !(selected.hasFree(s) || selected.isBound(s)))
							sub.add(Menu.NONE, key, Menu.NONE, rules.get(key) + " using " + s);
					}
				} else if (firstOrder) {
					SortedSet<String> vars = topTree.getAtoms();
					vars.addAll(bottomTree.getAtoms());
					for (String v : vars)
						sub.add(Menu.NONE, key, Menu.NONE, rules.get(key) + " using " + v);
				} else {
					SortedSet<String> vars = topTree.getVariables();
					vars.addAll(bottomTree.getVariables());
					for (String v : vars)
						sub.add(Menu.NONE, key, Menu.NONE, rules.get(key) + " using " + v);
				}
			}
		}
		rulesList.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();

		FormationTree newTree = selectedTree.clone();

		if (isTopTree(selectedTree)) {
			topTreeStack.pop();
			topTreeStack.push(newTree);
		} else {
			bottomTreeStack.pop();
			bottomTreeStack.push(newTree);
		}
		
		// User input not required
		if (id < min_user_input_required || (id >= first_order_rules && id < fo_min_user_input_required) || id > 105) {
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
		topRedoStack.clear();
		bottomRedoStack.clear();
		topRedoLine.setVisibility(View.INVISIBLE);
		bottomRedoLine.setVisibility(View.INVISIBLE);
		return true;
	}

	public void itemClicked() {
		if (isTopTree(selectedTree)) {
			System.out.println("Item clicked: " + topTreeStack);
			addTextViewToTop(new TextView(context), selectedTree.toString());
		} else {
			addTextViewToBottom(new TextView(context), selectedTree.toString());
		}

		// check for completion
		System.out.println("Checking for completion... " + topTree.toString() + " " + bottomTree.toString());
		if (equivalenceComplete(topTree.toString(), bottomTree.toString())) {
			CharSequence text = "Equivalence complete!";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

		// check for cycles
		Stack<TextView> selectedStack = (isTopTree(selectedTree)) ? topStack : bottomStack;

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

	public void moveTopLeft(View view) {
		topFormationTree.addToXOffset(-50);
	}
	
	public void moveTopRight(View view) {
		topFormationTree.addToXOffset(50);
	}

	public void moveBottomLeft(View view) {
		bottomFormationTree.addToXOffset(-50);
	}
	
	public void moveBottomRight(View view) {
		bottomFormationTree.addToXOffset(50);
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
	
	private boolean isTopTree(FormationTree tree) {
		return tree == topTree;
	}
}
