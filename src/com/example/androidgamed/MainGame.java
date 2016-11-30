package com.example.androidgamed;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainGame extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        
        Button btnStart = (Button)findViewById(R.id.btnSt);
        btnStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				setContentView(R.layout.main);
				
			}
		});
    }

}
