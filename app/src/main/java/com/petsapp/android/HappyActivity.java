package com.petsapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class HappyActivity extends AppCompatActivity {
    TextView cont;
    String petn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        TextView text = (TextView) findViewById(R.id.dogname);
        Bundle extras = getIntent().getExtras();
        petn = extras.getString("petname");
        text.setText(petn);

        cont = (TextView) findViewById(R.id.cont);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
                finish();

            }
        });
    }

}
