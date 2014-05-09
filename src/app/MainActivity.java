package app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.equivalencesapp.R;

public class MainActivity extends ActionBarActivity {

	public final static String START_EQUIVALENCE 
		= "app.START_EQUIVALENCE";
	public final static String END_EQUIVALENCE 
		= "app.END_EQUIVALENCE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
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
		
		// TODO: Check equivalences are valid and equivalent
		
		intent.putExtra(START_EQUIVALENCE, startEquivalence);
		intent.putExtra(END_EQUIVALENCE, endEquivalence);
	    startActivity(intent);
	}
}
