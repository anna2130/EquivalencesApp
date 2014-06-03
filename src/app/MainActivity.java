package app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import com.example.equivalencesapp.R;

public class MainActivity extends Activity {

	public final static String DIFFICULTY 
	= "app.DIFFICULTY";
	public final static String PERCENT 
	= "app.PERCENT";

	private static SeekBar seekbar;
	private int percent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_equivalences);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
			View rootView = inflater.inflate(
					R.layout.fragment_main, container, false);

			seekbar = (SeekBar) rootView.findViewById(R.id.seek_bar);
			seekbar.setProgress(50);
			
			return rootView;
		}
	}

	/** Called when the user clicks the Start button */
	public void startPropositional(View view) {		
		// Use intents to carry extra information to the activity
		// such as the equivalences used
		Intent intent = new Intent(this, EnterEquivalencesActivity.class);

		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		int difficulty = spinner.getSelectedItemPosition();

		SeekBar seekbar = (SeekBar) findViewById(R.id.seek_bar);
		percent = seekbar.getProgress();
		
		seekbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setPercent(progress);
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		intent.putExtra(DIFFICULTY, difficulty);
		intent.putExtra(PERCENT, percent);
		startActivity(intent);
	}

	private void setPercent(int p) {
		percent = p;
	}
	
	
}