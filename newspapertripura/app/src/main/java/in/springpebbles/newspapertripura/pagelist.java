package in.springpebbles.newspapertripura;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Sourav on 24/05/2017.
 */

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class pagelist extends AppCompatActivity {
    // Array of strings...


    ArrayList mobileArray = new ArrayList<String>();



    String urlPart1;
    String url;
    int pageno=0;

    private InterstitialAd mInterstitialAd;
    private int serviceCounter=0;


    int newspaperID=0;
    String fileType;



    // Step 1: Add the new cast for new paper.....


    int firstPage,lastPage;

    DataStore dataStore;
    Document doc = null;


    TextView visitSite;

    SharedPreferences controlSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagelist);

        controlSharedPreferences = getSharedPreferences("control_variables",MODE_PRIVATE);

        Intent i = getIntent();
        final int select = i.getIntExtra("select",0);
        newspaperID = select;
        fileType = i.getStringExtra("fileType");

        dataStore = new DataStore(newspaperID,this);


        //Log.d("mylog","\shouldfetchurlvalue"+String.valueOf(dataStore.shouldFetchUrl));
        if(dataStore.shouldFetchUrl) {
            new Load().execute();
        }

        mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
        mInterstitialAd.setAdUnitId("ca-app-pub-6249242764892548/5312053313"); //real
        mInterstitialAd.loadAd(new AdRequest.Builder().build());






        //dataStore.setPageLimits(newspaperID);
        dataStore.returnFinalURL(1,doc);

        firstPage = dataStore.firstPage;
        lastPage = dataStore.lastPage;

        try {

            for (int k = firstPage; k <= lastPage; k++) {

                mobileArray.add("Page " + String.valueOf(k));
            }
        } catch (Exception e){
            Log.d("Error.:",e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }


        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.listview, mobileArray);

        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);




        visitSite = (TextView)findViewById(R.id.visitSite);
        visitSite.setText("Content Source "+getDomainName(dataStore.returnFinalURL(1,doc))+"\nClick To Visit Site");
        visitSite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    visitSite.setBackgroundColor(Color.parseColor("#b6f442"));
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){

                    //showToast("inside action up");

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getDomainName(getDomainName(dataStore.returnFinalURL(1,doc)))));
                    startActivity(i);


                    visitSite.setBackgroundColor(Color.parseColor("#3836c9"));
                }

                return false;
            }
        });





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                        pageno = position+1;

                urlPart1 =dataStore.returnFinalURL(pageno,doc);
                url=urlPart1;

                Log.d("msgsourav","returned url for webview "+url);

                if(fileType.equals("img")) {
                    Intent intent = new Intent(getApplicationContext(), webview.class);
                    intent.putExtra("url", url);
                    intent.putExtra("newspaperID", select);
                    intent.putExtra("pageNumber", pageno);
                    startActivityForResult(intent,0);

                } else if(fileType.equals("pdf")) {

                    //Toast.makeText(getApplicationContext(),"inside pdf",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(
                            Uri.parse(url),
                            "application/pdf");
                    PackageManager pm = getPackageManager();
                    List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
                    if (activities.size() > 0) {
                        try {
                            startActivity(intent);
                        } catch(Exception e) {
                            Toast.makeText(getApplicationContext(),"Sorry There Has Been Some Unknown Error : "+e.getMessage().toString(),Toast.LENGTH_LONG);
                        }
                    } else {

                        Toast.makeText(getApplicationContext(),"This Newspaper Is In Pdf Format You Need To Install Google Pdf To View It!",Toast.LENGTH_LONG);

                    }
                }



                if(serviceCounter>=6) {
                    showInterstitialAd();
                } else {
                    serviceCounter++;
                }



            }






        });


    }

    public void showInterstitialAd(){
        if (controlSharedPreferences.getString(dataStore.controlVariablesId[1], "not found").equals("yes")) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                serviceCounter=0;
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }
    }

    public void showToast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    boolean isDataFetched = false;

    public class Load extends AsyncTask<Void,Void,Void> {

        ProgressDialog progressDialog;
        String passedUrl = "http://kaajcareer.in/tripura/index_epaper.php?pageid=page&date=0";

        public void setPassedUrl(String url){
            passedUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(pagelist.this);
            progressDialog.setMessage("Fetching Url");
            progressDialog.setTitle("Just A Minute");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                doc = Jsoup.connect(passedUrl).get();
            } catch (Exception e){

            }


            if(doc == null){

                //showToast("null found");

                return null;

            } else {
                isDataFetched = true;
            }




            //Log.d("msgsourav",doc.toString());
            //Log.d("msgsourav",implodedData);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            progressDialog.dismiss();



        }
    }

    public String getDomainName(String url){
        String finalUrl="";
        char[] charArray = url.toCharArray();
        int i;

        boolean isDotFound=false;

        for(i=0;i<charArray.length;i++){

            finalUrl +=charArray[i];

            if(charArray[i] == '.'){isDotFound=true;}
            if(isDotFound){
                if(charArray[i]=='/'){
                    break;
                }
            }

        }

        return finalUrl;
    }


}