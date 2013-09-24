package rogo.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RogoMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rogo_activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rogo_main, menu);
        return true;
    }
    
}
