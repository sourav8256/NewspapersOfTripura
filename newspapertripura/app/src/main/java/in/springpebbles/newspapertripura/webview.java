package in.springpebbles.newspapertripura;

import android.*;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sourav on 24/05/2017.
 */

public class webview extends Activity {

    public Button button1;
    public Button button2;
    public TextView textView;

    String url;
    String url2;
    int pageNumber=0;
    int newspaperID=0;

    private long DownloadId;
    DownloadManager downloadManager;

    WebView webView;


    private InterstitialAd mInterstitialAd;
    private int serviceCounter=0;


    public int firstPage,lastPage;



    DataStore dataStore;

    Document doc;


    TextView visitSite;

    SharedPreferences controlSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        controlSharedPreferences = getSharedPreferences("control_variables",MODE_PRIVATE);

        mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test
        mInterstitialAd.setAdUnitId("ca-app-pub-6249242764892548/5312053313"); //real
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        Intent i = getIntent();
        Uri urlWithParameters = i.getData();
        url = i.getStringExtra("url");

        String urlFromShareIntent="default";
        pageNumber = i.getIntExtra("pageNumber",1);
        newspaperID = i.getIntExtra("newspaperID",0);

/*
        if(urlWithParameters != null) {
            pageNumber = Integer.parseInt(urlWithParameters.getQueryParameter("pgno"));
            newspaperID = Integer.parseInt(urlWithParameters.getQueryParameter("npaperid"));
        } else {
            pageNumber = i.getIntExtra("pageNumber",1);
            newspaperID = i.getIntExtra("newspaperID",0);
        }

*/

        dataStore = new DataStore(newspaperID,this);

        //dataStore.setPageLimits(newspaperID);
        dataStore.returnFinalURL(1,doc);
        firstPage = dataStore.firstPage;
        lastPage = dataStore.lastPage;




        if(i.hasExtra("urlFromShareIntent")){
            urlFromShareIntent = i.getStringExtra("urlFromShareIntent");
            //showToast(urlFromShareIntent);
            url2 = urlFromShareIntent;
        } else {
            url2 = dataStore.returnFinalURL(pageNumber,doc);
        }



        loadURL(url2);


        //button1 = (Button)findViewById(R.id.button3);
        //button2 = (Button)findViewById(R.id.button4);
        textView = (TextView)findViewById(R.id.textView);

        textView.setText(url2);



        visitSite = (TextView)findViewById(R.id.visitSite);
        visitSite.setText("Content Source "+getDomainName(url2)+"\nClick To Visit Site");
        visitSite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    visitSite.setBackgroundColor(Color.parseColor("#b6f442"));
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){

                    //showToast("inside action up");

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getDomainName(url2)));
                    startActivity(i);


                    visitSite.setBackgroundColor(Color.parseColor("#3836c9"));
                }

                return false;
            }
        });




    }



    private GestureDetector gs = null;

    private View.OnTouchListener onTouch = new View.OnTouchListener() {

        float zoomLevel = 1;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (gs == null) {
                gs = new GestureDetector(
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onDoubleTapEvent(MotionEvent e) {

                                dataStore.showToast("doubletap");
                                zoomLevel+=400;
                                //Double Tap
                                if(Build.VERSION.SDK_INT>=17) {
                                    webView.setInitialScale(10);//Zoom in
                                }
                                return false;
                            }

                            @Override
                            public boolean onSingleTapConfirmed(MotionEvent e) {

                                dataStore.showToast("singletap");
                                zoomLevel=100;
                                //Single Tab
                                if(Build.VERSION.SDK_INT>=17) {
                                    webView.setInitialScale(100);//Zoom in
                                }// Zoom out
                                return false;
                            };
                        });
            }

            gs.onTouchEvent(event);

            return false;
        }
    };


    public void loadURL(String url){

        Log.d("msgsourav","url for webview "+url);

        String data = "<img style=\"width: 100%\" src=\""+url+"\" />";

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        //webView.setOnTouchListener(onTouch);
        //webView.loadDataWithBaseURL("file:///android_res/", dataStore.generatePage(url), "text/html", "utf-8", null);
        //webView.loadUrl(url);
        webView.loadData(data, "text/html", "UTF-8");

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


    public void nextButton(View view){
        if(pageNumber<lastPage){pageNumber++;}
        url2 = dataStore.returnFinalURL(pageNumber,doc);


        loadURL(url2);

        textView.setText(url2);

        if(serviceCounter>=7) {
           showInterstitialAd();
        } else {
            serviceCounter++;
        }

    }

    public void previousButton(View view) {
        if (pageNumber > firstPage) {pageNumber--;}
        url2 = dataStore.returnFinalURL(pageNumber,doc);

        loadURL(url2);

        textView.setText(url2);



        if(serviceCounter>=7) {
            showInterstitialAd();
        } else {
            serviceCounter++;
        }

        }


    public void shareButton(View v){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        11243);
            }
        } else {
            takeScreenshot();
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 11243: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    takeScreenshot();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void takeScreenshot() {
        //Date now = new Date();
        //android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "screenshot_to_share_7867656" + ".jpg";
            //String mPath = getFilesDir().toString() + "/" + "screenshot_to_share_7867656" + ".jpg";
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }


    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(imageFile);
        intent.putExtra(Intent.EXTRA_TEXT,"Read the full story and more on our app http://www.springpebbles.in/redir.php?rc=1&type=newspaper&pgno="+pageNumber+"&npaperid="+newspaperID+"&url="+Uri.encode(dataStore.hideUrl(url2,3)));
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("image/*");
        startActivity(intent);
    }

    public void showToast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    public void showInterstitialAd() {

        if (controlSharedPreferences.getString(dataStore.controlVariablesId[1], "not found").equals("yes")) {


            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                serviceCounter = 0;
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }

        }

    }
}
