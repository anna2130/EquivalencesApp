package app;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeManipulation.RuleEngine;
import treeManipulation.TruthTable;
import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.equivalencesapp.R;

public class MainActivity extends ActionBarActivity {

	public final static String START_EQUIVALENCE 
		= "app.START_EQUIVALENCE";
	public final static String END_EQUIVALENCE 
		= "app.END_EQUIVALENCE";

    static CustomKeyboard mCustomKeyboard;
    
	static Activity activity;
    static KeyboardView mKeyboardView;
	
    private Compiler c;
    private RuleEngine re;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setContentView(R.layout.fragment_main);
		
	    mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.layout.logickbd );
	    mCustomKeyboard.registerEditText(R.id.start_equivalence);
	    mCustomKeyboard.registerEditText(R.id.end_equivalence);
	    
	    c = new Compiler();
	    re = new RuleEngine();
	}

	@Override public void onBackPressed() {
	    if( mCustomKeyboard.isCustomKeyboardVisible() ) mCustomKeyboard.hideCustomKeyboard(); else this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			System.out.println("Enter two equivalences");
			setErrorMessage("Please enter two equivalent formulae");
		} else {

		// TODO: Check equivalences are syntactically correct
		FormationTree t1 = c.compile(startEquivalence);
		FormationTree t2 = c.compile(endEquivalence);
		
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
	
	public void randomise(EditText startText, EditText endText) {
		Compiler c = new Compiler();
		String start = startText.getText().toString();
		String end;
		
		FormationTree tree;
		
		if (start.equals("")) {
			end = c.generateRandomEquivalence(4, 1);
			tree = c.compile(end);
		} else {
			tree = c.compile(start);
		}
		
		re.applyRandomRules(tree, 3);
		end = tree.toString();
		endText.setText(end);
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
