package in.springpebbles.newspapertripura;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by sourav9674 on 7/29/2017.
 */

public class plainWebview extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.webview_plain);

        Intent intent = getIntent();
        String url = intent.getExtras().getString("url","not found");

        WebView webView = (WebView)findViewById(R.id.webview);
        webView.loadUrl(url);

    }
}
