package in.springpebbles.newspapertripura;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by sourav9674 on 7/29/2017.
 */

public class JavaScriptInterface{
    private Activity activity;

    public JavaScriptInterface(Activity activiy) {
        this.activity = activiy;
    }


    @JavascriptInterface
    public void advertiserPage(String url){
        Log.d("mylog","the url is"+url);
        Intent intent = new Intent(activity,plainWebview.class);
        intent.putExtra("url",url);
        activity.startActivityForResult(intent,0);
    }

}
