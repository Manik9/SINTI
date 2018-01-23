package in.binarykeys.manik;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, FetchDataCallbackInterface {
    private ZXingScannerView mScannerView;
    static String data;
    String num;
    String day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        Intent i=getIntent();
        Bundle b=i.getExtras();
        day= b != null ? b.getString("selected") : null;


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        String data=rawResult.getText();
        Log.v("formatter",rawResult.getText());
        String number=data.substring(data.indexOf(':')+1,data.lastIndexOf(':'));
        num=number;
        String hash=data.substring(data.lastIndexOf(':')+1,data.length());
        Log.v("hash",hash);
        Log.v("number",number);

        String url = "https://mittaltech.000webhostapp.com/sinti/validatedata.php?mobile="+number+"&hash="+hash+"&daynum="+day.toLowerCase();
        Log.v("url",url);
        FetchData f = new FetchData(url, this, MainActivity.this);
        f.execute();
    }

    @Override
    public void fetchDataCallback(String result) {
        data = result;
        renderData();
    }

    public void renderData() {
        // do something with your data here
    }

    public class FetchData extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection;
        String url;
        ProgressDialog p;
        Context context;
        FetchDataCallbackInterface callbackInterface;

        public FetchData(String url, FetchDataCallbackInterface callbackInterface, Context context) {
            this.url = url;
            this.context = context;
            this.callbackInterface = callbackInterface;
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(this.url);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             p=new ProgressDialog(context);
             p.setMessage("Wait...");
             p.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.hide();
            // pass the result to the callback function
            this.callbackInterface.fetchDataCallback(result);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Scan Result");
            Log.v("websitedata",result);
            if(result.indexOf('<')!=-1){
                int p=Integer.parseInt(result.substring(0,result.indexOf('<')));
                    if(p>0)
                        builder.setMessage("Not Allowed :"+num);
                    else
                        builder.setMessage("Allowed");
                }else
                    builder.setMessage("No Records Found for "+num);

            builder.setCancelable(false);
            builder.setPositiveButton("Contine", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mScannerView.resumeCameraPreview(MainActivity.this);
                }
            });
            AlertDialog alert1 = builder.create();
            alert1.show();
        }

    }

}
