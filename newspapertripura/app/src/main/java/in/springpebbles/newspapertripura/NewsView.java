package in.springpebbles.newspapertripura;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Sourav on 16/07/2017.
 */

public class NewsView extends AppCompatActivity {


    WebView webView;
    String url;
    String urlOfImage;

    Boolean isDataFetched;


    //space below is important and given for a reason
    String newsContent="                                                                                                                                                                                   ";

    Document doc;


    TextView textView;
    TextView visitSite;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_viewer);


        isDataFetched = false;

        Intent i = getIntent();
        Uri urlWithParameters = i.getData();
        if(urlWithParameters != null) {
            String id = urlWithParameters.getQueryParameter("id");
            url = "http://www.tripurainfoway.com/"+id;
        } else {
            url = i.getStringExtra("url");
        }

        new Load().execute();

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
                    i.setData(Uri.parse(url));
                    startActivity(i);


                    visitSite.setBackgroundColor(Color.parseColor("#3836c9"));
                }

                return false;
            }
        });


    }


    public void shareButton(View v){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
        intent.putExtra(Intent.EXTRA_TEXT,"Read the full story and more on our app http://www.springpebbles.in/redir.php?rc=1&type=online_news&id="+url.replace("http://www.tripurainfoway.com/",""));
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("image/*");
        startActivity(intent);
    }

    public void showToast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }



    private class Load extends AsyncTask<Void,Void,Void>{



        String news1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            TextView textView = (TextView)findViewById(R.id.nvtv);
            textView.setText("Loading...");

        }


        @Override
        protected Void doInBackground(Void... params) {


/*

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            String html = "";
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null)
            {
                str.append(line);
            }
            in.close();
            html = str.toString();

*/

        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e){

        }

        if(doc == null) {
            return null;
        } else{
            isDataFetched = true;
        }

            Elements imageUrl = doc.select("div.nwsdetl_right>img[src]");
            urlOfImage = imageUrl.attr("src");


            Elements newsTitle = doc.select("div.nwsdetailhd");
            news1 = Jsoup.parse(newsTitle.html()).text();


            //newsContent+= "***"+news1+"**"+"\n\n\n\n";

            Elements news2 = doc.select("div.nwscontnt>p");

            for(Element e : news2) {
                newsContent += Jsoup.parse(e.html()).text()+"\n\n";
            }

            newsContent = newsContent.replace("&nbsp;","");

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(isDataFetched) {

                String data = "<img style=\"width: 100%\" src=\""+urlOfImage+"\" />";

                webView = (WebView) findViewById(R.id.webView);
                webView.loadData(data,"text/html","UTF-8");
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);


                TextView nvtitleview = (TextView)findViewById(R.id.nvtitleview);
                nvtitleview.setText(news1);
                nvtitleview.setBackgroundColor(Color.parseColor("#ff266275"));

                textView = (TextView) findViewById(R.id.nvtv);
                textView.setText(newsContent);
                textView.setBackgroundColor(Color.parseColor("#c1ffe1"));
            } else {

                textView = (TextView) findViewById(R.id.nvtv);
                textView.setText("Connection Failed...");
                //new Load().execute();
            }

        }
    }






}
