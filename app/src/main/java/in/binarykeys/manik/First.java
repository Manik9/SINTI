package in.binarykeys.manik;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;



public class First extends AppCompatActivity {
    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner=(Spinner) findViewById(R.id.spinner);

    }

    public void go(View v){
        Intent i=new Intent(this,MainActivity.class);
        i.putExtra("selected",spinner.getSelectedItem().toString());
        startActivity(i);
    }
}
