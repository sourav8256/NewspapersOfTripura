package in.springpebbles.newspapertripura;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Sourav on 16/07/2017.
 */

public class OnlineNewsList extends AppCompatActivity{


    TextView textView;
    Document doc;
    String parsedHtml = "default";
    String parsedHref = "default";


    Boolean isDataFetched = false;

    String[] titleContainer = new  String[10];
    String[] linkContainer = new String[10];

    String sharedPreferenceskey = "onlineNewsList";

    SharedPreferences sharedPreferences;

    //ArrayList<String> titleContainer;
    //ArrayList<String> linkContainer;

    TextView[] ontv = new TextView[10];
    int[] ontvelements = {R.id.ontv1,R.id.ontv2,R.id.ontv3,R.id.ontv4,R.id.ontv5,R.id.ontv6,R.id.ontv7,R.id.ontv8,R.id.ontv9,R.id.ontv10};


    int[] oncvelements = {R.id.oncv1,R.id.oncv2,R.id.oncv3,R.id.oncv4,R.id.oncv5,R.id.oncv6,R.id.oncv7,R.id.oncv8,R.id.oncv9,R.id.oncv10};


    ProgressDialog mProgressDialog;

    TextView visitSite;
    DataStore dataStore;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_news_list);
        dataStore = new DataStore(1,this);


        SharedPreferences controlSharedPreferences = getSharedPreferences("control_variables",MODE_PRIVATE);

        if(controlSharedPreferences.getString(dataStore.controlVariablesId[3],"not found").equals("yes")){
            findViewById(R.id.adView).setVisibility(View.VISIBLE);
        }


        NativeExpressAdView adView = (NativeExpressAdView)findViewById(R.id.adView);

        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);

        sharedPreferences = getSharedPreferences(sharedPreferenceskey, MODE_PRIVATE);

        sharedPreferences.getString("key","Not Found");

        int i;
        for(i=0;i<10;i++){
            ontv[i] = (TextView)findViewById(ontvelements[i]);
        }


        visitSite = (TextView)findViewById(R.id.visitSite);

        visitSite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    visitSite.setBackgroundColor(Color.parseColor("#b6f442"));
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){

                    //showToast("inside action up");

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://www.tripurainfo.com/"));
                    startActivity(i);


                    visitSite.setBackgroundColor(Color.parseColor("#3836c9"));
                }

                return false;
            }
        });


        if(!sharedPreferences.getString("title9","Not Found").equals("Not Found")){
            for(i=0;i<10;i++){
                String title = sharedPreferences.getString("title"+i,"Not Found");
                titleContainer[i] = title;
                ontv[i].setText(title);

                String link = sharedPreferences.getString("link"+i,"Not Found");
                linkContainer[i] = link;
            }
        }


        new Load().execute();

    }




    private class Load extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //mProgressDialog = new ProgressDialog(OnlineNewsList.this);
            //mProgressDialog.show();

            TextView textView = (TextView)findViewById(R.id.action_title);
            textView.setText("Updating...");

        }

        @Override
        protected Void doInBackground(Void... params) {




            try {
                doc = Jsoup.connect("http://www.tripurainfoway.com/").get();
            } catch (Exception e) {
                //e.printStackTrace();
                Log.d("myerror",e.getMessage());
            }

            if(doc == null){
                return null;
            } else {
                isDataFetched = true;
            }

            Elements classes = doc.select("div.nwssection_right>ul>ul>li>a");
            //Elements classes = doc.getElementsByClass("nwssection_right");


            int i=0;

            for(Element classesBreakDown : classes){

                //parsedHtml+= classesBreakDown.select("ul>ul>li>a").attr("href");
                parsedHtml = classesBreakDown.html();
                parsedHref = classesBreakDown.attr("href");


                titleContainer[i] = parsedHtml;
                linkContainer[i] = parsedHref;

                //titleContainer.add(classesBreakDown.html());
                //linkContainer.add(classesBreakDown.attr("href"));

                i++;
            }



            //for(Element classesBreakDown : classes){

                //titleContainer.add(classesBreakDown.html());
                //linkContainer.add(classesBreakDown.attr("href"));

            //\}



            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if(isDataFetched) {


                int i;
                for (i = 0; i < 10; i++) {
                    ontv[i].setText(titleContainer[i]);
                    //ontv[i].setText("test");
                }


                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (i = 0; i < 10; i++) {
                    editor.putString("title" + i, titleContainer[i]);
                    editor.putString("link" + i, linkContainer[i]);
                }
                editor.apply();


                TextView textView = (TextView) findViewById(R.id.action_title);
                textView.setText("News List");


                //mProgressDialog.dismiss();
                //textView.setText(parsedHtml);

            } else {
                //new Load().execute();

                TextView textView = (TextView) findViewById(R.id.action_title);
                textView.setText("Connection failed...");

                //Toast.makeText(getApplicationContext(),"Connection failed...",Toast.LENGTH_SHORT).show();

            }

        }
    }




    public void openNewsView(View v){

        String url="default";

            int i=0;
            for(int id : oncvelements) {
                if (v.getId() == id) {
                    //url="inside check";
                    url = linkContainer[i];
                }
                
                i++;
            }


        Intent openingIntent = new Intent(getApplicationContext(),NewsView.class);
        openingIntent.putExtra("url",url);
        startActivityForResult(openingIntent,007);



    }




}
