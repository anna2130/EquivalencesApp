package app;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeManipulation.RuleEngine;
import treeManipulation.TruthTable;
import android.app.Activity;
import android.content.Intent;
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
	private int percent;
	
	private String initialFormula;
	private boolean firstOrder;

	private static CustomKeyboard mCustomKeyboard;

	private Compiler c;
	private RuleEngine re;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_enter_equivalences);

		Intent intent = getIntent();
		int difficulty = intent.getIntExtra(MainActivity.DIFFICULTY, 0);
		percent = intent.getIntExtra(MainActivity.PERCENT, 50);
		firstOrder = intent.getBooleanExtra(MainActivity.FIRSTORDER, false);
		
		switch (difficulty) {
		case 0: numVars = 3;
				depth = 0;
				numRules = 3;
				break;
		case 1: numVars = 5;
				depth = 1;
				numRules = 4;
				break;
		case 2: numVars = 6;
				depth = 2;
				numRules = 5;
				break;
		}
		
		initialFormula = "";

		TextView exampleRule1 = (TextView) findViewById(R.id.formula1);
		TextView exampleRule2 = (TextView) findViewById(R.id.formula2);
		TextView exampleRule3 = (TextView) findViewById(R.id.formula3);
		
		// ∀ | ∃ | ∧ | ∨ | ┬ | ⊥ | ¬ | → | ↔
		
		if (firstOrder) {
			mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.layout.firstorderkbd );
			exampleRule1.setText("∀x∃y[Px→Qxy]");
			exampleRule2.setText("∀x∀y[¬(Px∨Ryz)→Qy]");
			exampleRule3.setText("∃y∀x[Px↔Qyz]");
		} else {		
			mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.layout.logickbd );
			exampleRule1.setText("z→(y∧x)");
			exampleRule2.setText("(x∨┬)→┬");
			exampleRule3.setText("x∧¬(y∨z)");
		}
		
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

			if (t1.hasError() || t2.hasError()) {
				setErrorMessage("Incorrect syntax");
			} else {
				if (t1 != null && t2 != null) {
					if (firstOrder) {
						intent.putExtra(START_EQUIVALENCE, startEquivalence);
						intent.putExtra(END_EQUIVALENCE, endEquivalence);
						startActivity(intent);
					} else {
						TruthTable tt1 = new TruthTable(t1);
						TruthTable tt2 = new TruthTable(t2);
	
						if (tt1.testEquivalence(tt2)) {
							startEquivalence = t1.toString();
							endEquivalence = t2.toString();
							
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

	public void randomise(EditText formulaText, EditText otherText) {
		String other = otherText.getText().toString();
		String formula;

		// Case 1: Neither text box contain formulae and one needs to be generated from scratch.
		// Case 2: The other text box contains a valid formula and an equivalent one needs to be generated.
		// Case 3: The other text box contains an invalid formula and an error will be thrown.
		
		FormationTree tree;
		// Case 1: If other equivalence is empty, generate new equivalence from scratch
		if (other.equals("")) {
			initialFormula = c.generateRandomEquivalence(numVars, depth, percent);
			tree = c.compile(initialFormula);
			
			// limit length of geneated equivalence
			while (tree.toString().length() > 25) {
				initialFormula = c.generateRandomEquivalence(numVars, depth, percent);
				tree = c.compile(initialFormula);
			}
		} else {
			// Case 2: else compile other equivalence
			if (initialFormula.equals(""))
				initialFormula = other;
			tree = c.compile(initialFormula);
		}
		
		System.out.println("Initial Formula: " + initialFormula);

		// TODO: Doesn't pick up on incorrect syntax beginning with atom?
		// Case 3: Error
		if (tree.hasError()) {
			setErrorMessage("Incorrect syntax");
		} else {
			// apply random rules to other equivalence
			re.setTruthValueProbability(percent);
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
