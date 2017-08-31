package in.springpebbles.newspapertripura;

/**
 * Created by Sourav on 24/05/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.RangeValueIterator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class newspaperlist extends AppCompatActivity {
    // Array of strings...

    // Step 1, Add the newspaper name here...

    //String[] mobileArray = {"Dainik Sambad","Tripura Times","Syandan Patrika","More Papers Will Be Added Soon"};
    public boolean dummy=false;
    public String fileType = null;

    SharedPreferences dataSharedPreferences;
    SharedPreferences.Editor dataEditor;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    final Handler handler = new Handler();

    FrameLayout frameLayout;

    DataStore dataStore = new DataStore(0,this);

    boolean thoughtTvAlreadyExecuted = false;
    boolean matchUpdateRunnableRun = false;

    String adInterstitial1,adNative1,adNative2,customAd1,customAd2;

    String[] controlVariablesId = {"is_first_value_set","ad_interstitial_1","ad_native_newspaperlist","ad_native_newslist","custom_ad_1","custom_ad_2"};
    int[] newspaperList   = {R.id.npcv1};

    TextView updateIntent;

    WebView customAdview1,customAdview2;
    WebView getCustomAdview2;

    JavaScriptInterface jsInterface;

    TextView visitSite;

    TextView matchUpdateRecordTV;

    FetchMatchData fetchMatchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newspaperlist);

        customAdview1 = (WebView)findViewById(R.id.custom_adview_1);
        customAdview2 = (WebView)findViewById(R.id.custom_adview_2);


        jsInterface = new JavaScriptInterface(this);

        dataSharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        dataEditor = dataSharedPreferences.edit();
        thought = dataSharedPreferences.getString("thought","");
        TextView thoughtcv = (TextView) findViewById(R.id.thoughttv);
        thoughtcv.setText(thought);

        sharedPreferences = getSharedPreferences("control_variables",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(!sharedPreferences.getBoolean("is_first_value_set",false)){

            editor.putString(dataStore.controlVariablesId[1],"no");
            editor.putString(dataStore.controlVariablesId[2],"no");
            editor.putString(dataStore.controlVariablesId[3],"no");
            editor.putString(dataStore.controlVariablesId[4],"no");
            editor.putString(dataStore.controlVariablesId[5],"no");
            editor.putString(dataStore.controlVariablesId[6],"yes");
            editor.putString(dataStore.controlVariablesId[7],"no");
            editor.putString(dataStore.controlVariablesId[8],"no");
            editor.putBoolean(dataStore.controlVariablesId[0],true);
            editor.commit();
        }


        if(sharedPreferences.getString(dataStore.controlVariablesId[2],"not found").equals("yes")){
            findViewById(R.id.adView).setVisibility(View.VISIBLE);
        }

        if(sharedPreferences.getString(dataStore.controlVariablesId[4],"not found").equals("yes")){
            findViewById(R.id.custom_ad_cv1).setVisibility(View.VISIBLE);
        }

        if(sharedPreferences.getString(dataStore.controlVariablesId[5],"not found").equals("yes")){
            findViewById(R.id.custom_ad_cv2).setVisibility(View.VISIBLE);
        }








        new Load().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        new FetchMatchData().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        //loadAdBanner("http://www.springpebbles.in/apis/newspapers_of_tripura_api/ad_banner/banner.html");
        loadCustomAdBanner(R.id.custom_adview_1,"http://www.springpebbles.in/apis/newspapers_of_tripura_api/ad_banner/ad_banner_1_newspaperlist.html");
        loadCustomAdBanner(R.id.custom_adview_2,"http://www.springpebbles.in/apis/newspapers_of_tripura_api/ad_banner/ad_banner_2_newspaperlist.html");

        Log.d("mylog","mylog working");

        MobileAds.initialize(this, "ca-app-pub-6249242764892548~2358586918");

        NativeExpressAdView adView = (NativeExpressAdView)findViewById(R.id.adView);

        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);


        adView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                    Log.d("mylog", "add loaded");

            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });



        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.listview, dataStore.mobileArray);


        updateIntent = (TextView)findViewById(R.id.updatetv);

        updateIntent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    updateIntent.setBackgroundColor(Color.parseColor("#b6f442"));
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){

                    //showToast("inside action up");

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=in.springpebbles.newspapertripura"));
                    startActivity(i);


                    updateIntent.setBackgroundColor(Color.parseColor("#3836c9"));
                }

                return false;
            }
        });



        //ListView listView = (ListView) findViewById(R.id.mobile_list);
        //listView.setAdapter(adapter);

        //frameLayout = (FrameLayout)findViewById(R.id.fb_page_promo);


        // Step 2, Add file type here...\.
/*
        listView.setOnItemClickListener(new OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


               fileType = dataStore.getFileType(position);

                if(position!=dataStore.mobileArray.length-1) {
                Intent i = new Intent(getApplicationContext(), pagelist.class);
                i.putExtra("select", position);
                i.putExtra("fileType",fileType);
                startActivity(i);
            }

            }
        });

        */




/*
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //frameLayout.setBackgroundColor(0xffffff);


            }
        });

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    //frameLayout.setBackgroundColor(0xb6f442);
                    frameLayout.setBackgroundColor(Color.parseColor("#b6f442"));
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    //frameLayout.setBackgroundColor(0x3836c9);


                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.facebook.com/springpebbles.in/"));
                    startActivity(i);

                    frameLayout.setBackgroundColor(Color.parseColor("#3836c9"));
                }
                return false;
            }
        });

  */


        visitSite = (TextView)findViewById(R.id.visitSite);
        visitSite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    visitSite.setBackgroundColor(Color.parseColor("#b6f442"));
                    Handler handler = new Handler();
                    Runnable resetColor = new Runnable() {
                        @Override
                        public void run() {
                            visitSite.setBackgroundColor(Color.parseColor("#3836c9"));
                        }
                    };
                    handler.postDelayed(resetColor,1000);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){

                    //showToast("inside action up");

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://www.cricbuzz.com/"));
                    startActivity(i);


                    visitSite.setBackgroundColor(Color.parseColor("#3836c9"));
                } else if(event.getAction() == MotionEvent.ACTION_SCROLL){
                    visitSite.setBackgroundColor(Color.parseColor("#3836c9"));
                    return true;
                }

                return false;
            }
        });


        Switch autoupdateSwitch = (Switch)findViewById(R.id.autoupdateS);

        autoupdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    dataStore.showToast("Autoupdate Of Score Started");
                    matchUpdate.run();
                } else {
                    dataStore.showToast("Autoupdate Of Score Stopped");
                    handler.removeCallbacks(matchUpdate);
                }

            }
        });






    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        dataStore.showToast("Autoupdate Of Score Stopped");
        handler.removeCallbacks(matchUpdate);

    }

    Runnable matchUpdate = new Runnable() {
        @Override
        public void run() {
            new FetchMatchData().execute();
            handler.postDelayed(matchUpdate,10000);
        }
    };




    @Override
    protected void onResume() {
        super.onResume();

        //new Load().execute();

    }

    public void matchDetailsUpdate(View v){
        new FetchMatchData().execute();
    }

    public void customAdClickHandler(String url){
        Intent intent = new Intent(newspaperlist.this,plainWebview.class);
        intent.putExtra("url",url);
        startActivityForResult(intent,0);
    }

    public void cardclick(View v){
        showToast("card 1");
    }


    public void showToast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public void showToastf(View v){
        Toast.makeText(getApplicationContext(),"toast",Toast.LENGTH_LONG).show();
    }



    public void showMatchList(View v){
//        dataStore.showLog("checking elements "+matchNamesForAlert.get(0));
        CharSequence[] items = new String[matchNamesForAlert.size()];
        matchNamesForAlert.toArray(items);
        AlertDialog.Builder builder = new AlertDialog.Builder(newspaperlist.this);
        builder.setTitle("Please Select Match");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                editor.putString(dataStore.controlVariablesId[7],"yes");
                editor.putString(dataStore.controlVariablesId[8],matchUrlsForAlert.get(which).toString());
                editor.commit();

                new FetchMatchData().execute();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showPageList(int newspaperID){

        fileType = dataStore.getFileType(newspaperID-1);


            Intent i = new Intent(getApplicationContext(), pagelist.class);
            i.putExtra("select", newspaperID-1);
            i.putExtra("fileType",fileType);
            startActivity(i);
    }

    public void npcv1(View v){
        showPageList(1);
    }

    public void npcv2(View v){
        showPageList(2);
    }

    public void npcv3(View v){
        showPageList(3);
    }

    public void npcv4(View v){
        showPageList(4);
    }

    public void npcv5(View v){
        showPageList(5);
    }

    public void npcv6(View v){
        showPageList(6);
    }




    public void showOnlineNewsList(int newsSourceID){
        Intent i = new Intent(getApplicationContext(),OnlineNewsList.class);
        startActivityForResult(i,0);
    }


    public void oncv1(View v){showOnlineNewsList(1);}


    public void expandDisclaimer(View v){
        TextView textView = (TextView)findViewById(R.id.disclaimertv);
        if(textView.getText().equals("Disclaimer (click to expand)")) {
            textView.setText("The news content of this app is solely for the easy access of the users and the developer does not claim any right on it but is owned by the respective newspaper/online sources as mentioned, in case of any complaint or any other issue they may contact the developer at the developer's email(also available at playstore) souravmandalm@gmail.com and I assure you the issue will be solved as soon as possible, ThankYou! \n (Dear reader you may click again to collapse this)");
        } else {
            textView.setText("Disclaimer (click to expand)");
        }
    }



    Document doc,doc2;
    String thought;
    boolean isDataFetched = false;

    private class Load extends AsyncTask<Void,Void,Void>{


        String controlVariables;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }

        @Override
        protected Void doInBackground(Void... params) {


            try {

                //doc2 = Jsoup.connect("http://www.springpebbles.in/apis/newspapers_of_tripura_api/thought_of_the_day/data.txt").get();
            } catch (Exception e){

            }


            try {
                doc = Jsoup.connect("http://www.springpebbles.in/apis/newspapers_of_tripura_api/fetch_data.html").get();
            } catch (Exception e){
                return null;
            }



            if(doc == null){
                return null;
            } else {
                isDataFetched = true;
            }





            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(isDataFetched) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);


                Elements dataElements = doc.select("data");
                Element dataElement;

                if(dataElements.size()>0) {
                    thought = dataElements.get(0).attr("thought");

                    dataEditor.putString("thought", thought);
                    dataEditor.apply();

                    TextView thoughtcv = (TextView) findViewById(R.id.thoughttv);

                    thoughtcv.setText(thought);
                }

                Elements controlElements;
                Element controlElement;

                controlElements = doc.select("controlVariables");
                if(controlElements.size()>0) {
                    controlElement = controlElements.get(0);
                    SharedPreferences sharedPreferences = getSharedPreferences("control_variables", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(dataStore.controlVariablesId[1], controlElement.attr("ad_interstitial_1"));
                    editor.putString(dataStore.controlVariablesId[2], controlElement.attr("ad_native_newspaperlist"));
                    editor.putString(dataStore.controlVariablesId[3], controlElement.attr("ad_native_newslist"));
                    editor.putString(dataStore.controlVariablesId[4], controlElement.attr("custom_ad_1"));
                    editor.putString(dataStore.controlVariablesId[5], controlElement.attr("custom_ad_2"));
                    editor.putString(dataStore.controlVariablesId[6], controlElement.attr("match_scorecard"));
                    //editor.putString(dataStore.controlVariablesId[7], controlElement.attr("match_url_override"));
                    //editor.putString(dataStore.controlVariablesId[8], controlElement.attr("match_url"));
                    editor.putBoolean(dataStore.controlVariablesId[0], true);
                    editor.apply();


                    Log.d("mylog", String.valueOf(Integer.parseInt(controlElement.attr("latest_version_code"))));

                    if (Integer.parseInt(controlElement.attr("latest_version_code")) > dataStore.currentVersionCode) {
                        findViewById(R.id.updatetv).setVisibility(View.VISIBLE);
                    }



                }

            } else {
                adbanner.loadData("So Sorry! There Has Been An ERROR, Probably With The Internet Connection","Text/Html","UTF-18");
            }
        }
    }

    String matchUrl="http://m.cricbuzz.com/";


    ArrayList matchNamesForAlert = new ArrayList();
    ArrayList matchUrlsForAlert = new ArrayList();

    private class FetchMatchData extends AsyncTask<Void,Void,Void>{

        Document matchDataDoc=null,matchUrlDoc;
        String matchTitle;
        String matchDetails;

        String errorMessage="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            findViewById(R.id.match_list).setVisibility(View.GONE);

            matchUpdateRecordTV = (TextView)findViewById(R.id.match_update_record_tv);
            matchUpdateRecordTV.setText("updating...");
        }

        @Override
        protected Void doInBackground(Void... params) {
try {
    Log.d("mylog", "the shared pref value" + sharedPreferences.getString(dataStore.controlVariablesId[8], "n3o"));

    try {
        if ((sharedPreferences.getString(dataStore.controlVariablesId[7], "no").equals("yes"))) {
            matchUrl = sharedPreferences.getString(dataStore.controlVariablesId[8], "not found");
            Log.d("mylog", "inside shared pref");

            if(matchUrlsForAlert.size()<=0) {
                matchUrlDoc = Jsoup.connect("http://m.cricbuzz.com/").get();
                Log.d("mylog", "match url is " + matchUrl);
            }
        } else {
            matchUrlDoc = Jsoup.connect("http://m.cricbuzz.com/").get();
            matchUrl = "http://m.cricbuzz.com" + matchUrlDoc.getElementsByClass("cb-list-item ").get(0).attr("href");
            Log.d("mylog", "match url is " + matchUrl);
        }
        matchDataDoc = Jsoup.connect(matchUrl).get();
    } catch (Exception e) {
        return null;
        //Log.d("mylog",e.getMessage());
    }

    matchTitle = matchDataDoc.getElementsByClass("cb-list-item ui-header ui-branding-header").text();
    matchDetails =
            matchDataDoc.getElementsByClass("cbz-ui-status").text()
                    + "\n\n" +
                    matchDataDoc.getElementsByClass("teamscores ui-bowl-team-scores").text()
                    + "\n" +
                    matchDataDoc.getElementsByClass("miniscore-teams ui-bat-team-scores").text()
                    + "\n\nBATTING\n";


                    if(matchDataDoc.getElementsByClass("bat-bowl-miniscore").size()>0 && matchDataDoc.getElementsByClass("cbz-grid-table-fix ").size()>6) {
                        matchDetails += matchDataDoc.getElementsByClass("bat-bowl-miniscore").get(0).text() + "   " + matchDataDoc.getElementsByClass("cbz-grid-table-fix ").get(6).text()+ "\n";
                    }

                    if(matchDataDoc.getElementsByClass("bat-bowl-miniscore").size()<=2){
                        matchDetails += "\nBOWLING\n";
                    }

                    if(matchDataDoc.getElementsByClass("bat-bowl-miniscore").size()>1 && matchDataDoc.getElementsByClass("cbz-grid-table-fix ").size()>11) {
                        matchDetails += matchDataDoc.getElementsByClass("bat-bowl-miniscore").get(1).text() + "   " + matchDataDoc.getElementsByClass("cbz-grid-table-fix ").get(11).text();
                    }

                    if(matchDataDoc.getElementsByClass("bat-bowl-miniscore").size()>2 && matchDataDoc.getElementsByClass("cbz-grid-table-fix ").size()>21) {
                        matchDetails +=  "\n\nBOWLING\n"+matchDataDoc.getElementsByClass("bat-bowl-miniscore").get(2).text() + "   " + matchDataDoc.getElementsByClass("cbz-grid-table-fix ").get(21).text()+"Overs";
                    }

    ;
} catch (Exception e){
    errorMessage = "Unexpected network error: "+e.getMessage();
    return null;
}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!errorMessage.equals("")) {
                showToast(errorMessage);
            }

            if(matchUrlDoc!=null) {
                Elements matchUrlsToSave = matchUrlDoc.select("a.cb-list-item ");
                for (Element element : matchUrlsToSave) {
                    if (element.attr("href").startsWith("/cricket-commentary/") && !matchUrlsForAlert.contains("http://m.cricbuzz.com" + element.attr("href"))) {
                        dataStore.showLog("checking elements urls " + element.attr("href"));
                        matchUrlsForAlert.add("http://m.cricbuzz.com" + element.attr("href"));
                    }
                }

                matchUrlsToSave = matchUrlDoc.getElementsByClass("matchheader");
                for (Element element : matchUrlsToSave) {

                    if (!matchNamesForAlert.contains(element.text())) {
                        dataStore.showLog("checking elements names " + element.text());
                        matchNamesForAlert.add(element.text());
                    }
                }
            }
            findViewById(R.id.match_list).setVisibility(View.VISIBLE);

            TextView matchTitleTV = (TextView)findViewById(R.id.match_title);
            matchTitleTV.setText(matchTitle);

            TextView matchDetailsTV = (TextView)findViewById(R.id.match_details);
            if(matchDetails!=null) {
                matchDetailsTV.setText(matchDetails);
            } else {
                matchDetailsTV.setText("Error: Connection failed...");
            }
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            int seconds = calendar.get(Calendar.SECOND);

            matchUpdateRecordTV = (TextView)findViewById(R.id.match_update_record_tv);
            matchUpdateRecordTV.setText("Updated: "+hour+":"+minutes+":"+seconds);

        }
    }

    WebView adbanner,customAdbanner;
    
    
  /*
    public void loadAdBanner(String url){

        Log.d("msgsourav","url for adbanner "+url);

        //String data = "<img style=\"height: 100%; width: 100%\" src=\""+url+"\" />";

        adbanner = (WebView) findViewById(R.id.ad_view);
        //adbanner.getSettings().setJavaScriptEnabled(true);
        //adbanner.getSettings().setBuiltInZoomControls(true);
        adbanner.getSettings().setDisplayZoomControls(false);
        //adbanner.loadDataWithBaseURL("file:///android_res/", dataStore.generatePage(url), "text/html", "utf-8", null);
        adbanner.loadUrl(url);
        //adbanner.loadData(data, "text/html", "UTF-8");

    }

    */


    public void loadCustomAdBanner(int viewId,String url){

        Log.d("msgsourav","url for adbanner "+url);

        //String data = "<img style=\"height: 100%; width: 100%\" src=\""+url+"\" />";

        adbanner = (WebView) findViewById(viewId);
        //adbanner.getSettings().setJavaScriptEnabled(true);
        //adbanner.getSettings().setBuiltInZoomControls(true);
        adbanner.getSettings().setDisplayZoomControls(false);
        //adbanner.loadDataWithBaseURL("file:///android_res/", dataStore.generatePage(url), "text/html", "utf-8", null);
        adbanner.loadUrl(url);
        //adbanner.loadData(data, "text/html", "UTF-8");

    }




}