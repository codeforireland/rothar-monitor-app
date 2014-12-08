package eu.appbucket.rothar.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import eu.appbucket.rothar.R;

public class RegistrationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	public void registerTagByCode(View view) {
		String tagCode = ((EditText) findViewById(R.id.tag_code)).getText().toString();
		Toast.makeText(this, "Registering tag code: " + tagCode, Toast.LENGTH_SHORT).show();
		new RegisterTask(this).execute(tagCode);
	}
}
