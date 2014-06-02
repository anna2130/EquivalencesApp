package app;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeManipulation.RuleEngine;
import treeManipulation.TruthTable;
import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.equivalencesapp.R;

public class EnterEquivalencesActivity extends Activity {

	public final static String START_EQUIVALENCE 
	= "app.START_EQUIVALENCE";
	public final static String END_EQUIVALENCE 
	= "app.END_EQUIVALENCE";
	
	private int numVars;
	private int depth;
	private int numRules;
	
	private String initialFormula;

	private static CustomKeyboard mCustomKeyboard;

	static Activity activity;
	static KeyboardView mKeyboardView;

	private Compiler c;
	private RuleEngine re;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_enter_equivalences);

		Intent intent = getIntent();
		int difficulty = intent.getIntExtra(MainActivity.DIFFICULTY, 0);
		
		switch (difficulty) {
		case 0: numVars = 3;
				depth = 0;
				numRules = 3;
				System.out.println("Beginner");
				break;
		case 1: numVars = 5;
				depth = 1;
				numRules = 4;
				break;
		case 2: numVars = 10;
				depth = 2;
				numRules = 5;
				break;
		}
		
		initialFormula = "";
		
		mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.layout.logickbd );
		mCustomKeyboard.registerEditText(R.id.start_equivalence);
		mCustomKeyboard.registerEditText(R.id.end_equivalence);

		c = new Compiler();
		re = new RuleEngine();
	}

	@Override 
	public void onBackPressed() {
		if(mCustomKeyboard.isCustomKeyboardVisible())
			mCustomKeyboard.hideCustomKeyboard(); 
		else 
			this.finish();
	}

	/** Called when the user clicks the Start button */
	public void startEquivalence(View view) {		
		// Use intents to carry extra information to the activity
		// such as the equivalences used
		Intent intent = new Intent(this, BeginEquivalenceActivity.class);

		EditText startText = (EditText) findViewById(R.id.start_equivalence);
		EditText endText = (EditText) findViewById(R.id.end_equivalence);
		String startEquivalence = startText.getText().toString();
		String endEquivalence = endText.getText().toString();

		if (startEquivalence.equals("") || endEquivalence.equals("")) {
			setErrorMessage("Please enter two equivalent formulae");
		} else {
			FormationTree t1 = c.compile(startEquivalence);
			FormationTree t2 = c.compile(endEquivalence);

			startEquivalence = t1.toString();
			endEquivalence = t2.toString();

			if (t1.hasError() || t2.hasError()) {
				setErrorMessage("Incorrect syntax");
			} else {
				if (t1 != null && t2 != null) {
					TruthTable tt1 = new TruthTable(t1);
					TruthTable tt2 = new TruthTable(t2);

					if (tt1.testEquivalence(tt2)) {
						intent.putExtra(START_EQUIVALENCE, startEquivalence);
						intent.putExtra(END_EQUIVALENCE, endEquivalence);
						startActivity(intent);
					} else {
						setErrorMessage("Not equivalent");
					}
				}
			}
		}
	}

	public void randomiseStart(View view) {
		EditText startText = (EditText) findViewById(R.id.start_equivalence);
		EditText endText = (EditText) findViewById(R.id.end_equivalence);
		randomise(startText, endText);
	}

	public void randomiseEnd(View view) {
		EditText startText = (EditText) findViewById(R.id.start_equivalence);
		EditText endText = (EditText) findViewById(R.id.end_equivalence);
		randomise(endText, startText);
	}

	public void randomise(EditText otherText, EditText formulaText) {
		Compiler c = new Compiler();
		String other = otherText.getText().toString();
		String formula;

		FormationTree tree;
		// If other equivalence is empty, generate new equivalence from scratch
		if (other.equals("")) {
			initialFormula = c.generateRandomEquivalence(numVars, depth);
			tree = c.compile(initialFormula);
			
			// limit length of geneated equivalence
			while (tree.toString().length() > 25) {
				initialFormula = c.generateRandomEquivalence(numVars, depth);
				tree = c.compile(initialFormula);
			}
		} else {
			// else compile other equivalence
			tree = c.compile(initialFormula);
//			re.applyRandomRules(tree, numRules);
		}
		
		System.out.println("Initial Formula: " + initialFormula);

		// TODO: Doesn't pick up on incorrect syntax beginning with atom?
		if (tree.hasError()) {
			setErrorMessage("Incorrect syntax");
		} else {
			// apply random rules to other equivalence
			re.applyRandomRules(tree, numRules);
			
			formula = tree.toString();
			if (formula.equals(other)) {
				randomise(otherText, formulaText);
			} else {
				formulaText.setText(formula);
			}
		}
	}

	public void resetInitialFormula() {
		initialFormula = "";
	}
	
	public void setErrorMessage(String err) {
		TextView error = (TextView) findViewById(R.id.error_message);
		error.setText(err);
		error.setVisibility(View.VISIBLE);
	}

	public void hideErrorMessage() {
		TextView error = (TextView) findViewById(R.id.error_message);
		error.setVisibility(View.INVISIBLE);
	}
}
