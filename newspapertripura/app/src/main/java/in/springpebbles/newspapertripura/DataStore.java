package in.springpebbles.newspapertripura;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sourav on 07/07/2017.
 */

public class DataStore {

    public int newspaperID;
    Context context;


    int currentVersionCode = 11;

    int firstPage=0;
    int lastPage=0;
    boolean shouldFetchUrl=false;

    final String[] mobileArray = {"Dainik Sambad","Daily Desher Katha","Kajcareer Tripura","Kajcareer Westbengal","Tripura Times(PDF)","Syandan Patrika(PDF)","More Papers Will Be Added Soon"};

    String[] controlVariablesId = {"is_first_value_set","ad_interstitial_1","ad_native_newspaperlist",
            "ad_native_newslist","custom_ad_1","custom_ad_2",
            "match_scorecard","match_url_override","match_url"};


    SharedPreferences sharedPreferences;

    public DataStore(int newspaperID,Context context){
        this.newspaperID = newspaperID;
        this.context = context;


        int i=0;

        if(mobileArray[newspaperID].equals(mobileArray[i++])){
            shouldFetchUrl=false;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            shouldFetchUrl=false;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            shouldFetchUrl=true;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            shouldFetchUrl=true;
        }else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            shouldFetchUrl=false;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            shouldFetchUrl=false;
        }







        try {
            sharedPreferences = context.getSharedPreferences("urlCasts", 0);
            Log.d("msgsourav","inside try");
        } catch (Exception e){

            Log.d("msgsourav","error message "+e.getMessage());

        }
    }



    public String getFileType(int newspaperID){
        String fileType="";

        int i=0;

        if(mobileArray[newspaperID].equals(mobileArray[i++])){
            fileType="img";
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            fileType="img";
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            fileType="img";
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            fileType="img";
        }else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            fileType="pdf";
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            fileType="pdf";
        }


        return fileType;
    };



    private String dainiksambadURLCast(int pageno){

        DateFormat formatter= new SimpleDateFormat("ddMMyyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
        String days=(formatter.format(new Date()));

        String finalURL = "http://www.dainiksambad.net/epaperimages//"+days+"//"+"page-"+pageno+".jpg";

        return finalURL;
    }




    private String tripuraTimesURLCast(int pageno){

        DateFormat formatter= new SimpleDateFormat("ddMMyyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
        String days=(formatter.format(new Date()));

        //String finalURL = "http://docs.google.com/gview?url=http://tripuratimes.com/admin/pdfs/"+days+pageno+".pdf&amp;embedded=true";

        String finalURL = "http://tripuratimes.com/admin/pdfs/"+days+pageno+".pdf";


        //http://docs.google.com/gview?url=http://tripuratimes.com/admin/pdfs/250520171.pdf&amp;embedded=true

        return finalURL;
    }



    private String syandanPatrikaURLCast(int pageno){

        DateFormat formatter= new SimpleDateFormat("ddMMyyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
        String days=(formatter.format(new Date()));

        String finalURL = "http://syandanpatrika.com/sp/admin/pdfs/"+days+pageno+".pdf";

        return finalURL;
    }



    private String dailydesherkathaURLCast(int pageno){

        DateFormat formatter= new SimpleDateFormat("ddMMyyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
        String days=(formatter.format(new Date()));

        // an example url of dailydesherkatha page 8 :: http://www.dailydesherkatha.net/epaperimages//13072017//13072017-md-hr-8.jpg

        //String finalURL = "http://syandanpatrika.com/sp/admin/pdfs/"+days+pageno+".pdf";
        String finalURL = "http://www.dailydesherkatha.net/epaperimages//"+days+"//"+days+"-md-hr-"+pageno+".jpg";

        return finalURL;
    }




    public String kajcareerTripuraURLParser(int pageno,Document doc){

        DateFormat formatter= new SimpleDateFormat("ddMMyyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
        String days=(formatter.format(new Date()));


        String finalURL="";

        if(doc != null) {

            //String finalURL = "http://kaajcareer.in/tripura/epaper/101212/page"+pageno+".jpg";

            if (sharedPreferences.getString("kajcareerTripuraURLCast", "not found").equals("not found") || sharedPreferences.getString("kajcareerTripuraURLCast", "not found").equals("not found")) {
                String fetchedData = doc.select("span#ex3>img").attr("src");

                String[] explodedData = fetchedData.split("/");


                String implodedData = "";
                int i;
                for (i = 0; i < (explodedData.length - 1); i++) {
                    implodedData += explodedData[i]+"/";
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("kajcareerTripuraURLCast", implodedData);
                editor.apply();

                finalURL = "http://kaajcareer.in/tripura/"+implodedData + "/page" + pageno + ".jpg";

            } else {
                finalURL = "http://kaajcareer.in/tripura/"+sharedPreferences.getString("kajcareerTripuraURLCast", "http://kaajcareer.in/tripura/notfound?") + "page" + pageno + ".jpg";
                Log.d("msgsourav","final url is"+finalURL);
            }
        } else {
            finalURL = "http://kaajcareer.in/tripura/"+sharedPreferences.getString("kajcareerTripuraURLCast", "http://kaajcareer.in/tripura/notfound?") + "page" + pageno + ".jpg";
            Log.d("msgsourav","final url is"+finalURL);
        }

        return finalURL;

    }


    public String kajcareerWestbengalURLParser(int pageno,Document doc){

        DateFormat formatter= new SimpleDateFormat("ddMMyyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
        String days=(formatter.format(new Date()));


        String finalURL="";

        if(doc != null) {

            //String finalURL = "http://kaajcareer.in/tripura/epaper/101212/page"+pageno+".jpg";

            if (sharedPreferences.getString("kajcareerTripuraURLCast", "not found").equals("not found") || sharedPreferences.getString("kajcareerTripuraURLCast", "not found").equals("not found")) {
                String fetchedData = doc.select("span#ex3>img").attr("src");

                String[] explodedData = fetchedData.split("/");


                String implodedData = "";
                int i;
                for (i = 0; i < (explodedData.length - 1); i++) {
                    implodedData += explodedData[i]+"/";
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("kajcareerTripuraURLCast", implodedData);
                editor.apply();

                finalURL = "http://kaajcareer.in/"+implodedData + "/page" + pageno + ".jpg";

            } else {
                finalURL = "http://kaajcareer.in/"+sharedPreferences.getString("kajcareerTripuraURLCast", "http://kaajcareer.in/notfound?") + "page" + pageno + ".jpg";
                Log.d("msgsourav","final url is"+finalURL);
            }
        } else {
            finalURL = "http://kaajcareer.in/"+sharedPreferences.getString("kajcareerTripuraURLCast", "http://kaajcareer.in/notfound?") + "page" + pageno + ".jpg";
            Log.d("msgsourav","final url is"+finalURL);
        }

        return finalURL;

    }




    public String kajcareerWestbengalURLCast(int pageno,Document doc){

        DateFormat formatter= new SimpleDateFormat("ddMMyyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
        String days=(formatter.format(new Date()));


        String finalURL = "http://kaajcareer.in/epaper/101212/page"+pageno+".jpg";

        return finalURL;

    }


    public String kajcareerTripuraURLCast(int pageno,Document doc){

        DateFormat formatter= new SimpleDateFormat("ddMMyyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
        String days=(formatter.format(new Date()));


        String finalURL = "http://kaajcareer.in/tripura/epaper/101212/page"+pageno+".jpg";

        return finalURL;

    }



    // Step 2: Add your newspaper to the switch case.....




    public String returnFinalURL(int pageno,Document doc){

        String finalURL;
        finalURL = dainiksambadURLCast(pageno);
        firstPage=1;
        lastPage=12;

        int i=0;

        if(mobileArray[newspaperID].equals(mobileArray[i++])){
            finalURL = dainiksambadURLCast(pageno);
            firstPage=1;
            lastPage=12;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            finalURL = dailydesherkathaURLCast(pageno);
            firstPage=1;
            lastPage=8;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            finalURL = kajcareerTripuraURLParser(pageno,doc);
            firstPage=1;
            lastPage=16;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            finalURL = kajcareerWestbengalURLParser(pageno,doc);
            firstPage=1;
            lastPage=16;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            finalURL = tripuraTimesURLCast(pageno);
            firstPage=1;
            lastPage=8;
        } else if(mobileArray[newspaperID].equals(mobileArray[i++])){
            finalURL = syandanPatrikaURLCast(pageno);
            firstPage=1;
            lastPage=10;
        }

        return finalURL;
    }





    // Step 1: Add firstPage and lastPage.....
/*
    public void setPageLimits(int newspaperID){

        String finalURL;

        switch (newspaperID){
            case 0:
                firstPage=1;
                lastPage=12;
                break;
            case 1:
                firstPage=1;
                lastPage=8;
                break;
            case 2:
                firstPage=1;
                lastPage=12;
            default:
                firstPage=1;
                lastPage=12;

        }

    }


*/


    public String generatePage(String url) {
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int displayWidth = display.getWidth();

        String result =
                "<html>"
                        + "<meta name=\"viewport\" content=\"target-densitydpi=device-dpi,width=device-width\">"
                        + "<head>"
                        + " <title>Title</title>"
                        + "</head>"
                        + "<body>"
                        + "<image id=\"myImage\" src=\""+url+"\" width=\"" + displayWidth + "px\" />"
                        + "</body>"
                        + "</html>";
        return result;
    }



    // extremely low security use only for hiding

    public String hideUrl(String url, int key){

        String finalString="";
        char[] chars = new char[url.length()];
        chars = url.toCharArray();
        for(char c : chars){

            c+=key;
            finalString+=c;

        }


        return finalString;

    }


    // extremely low security use only for hiding

    public String showUrl(String url, int key){

        String finalString="";
        char[] chars = new char[url.length()];
        chars = url.toCharArray();
        for(char c : chars){

            c-=key;
            finalString+=c;

        }


        return finalString;

    }



    public void showToast(String s){
        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
    }

    public void showLog(String s){
        Log.d("mylog",s);
    }




}