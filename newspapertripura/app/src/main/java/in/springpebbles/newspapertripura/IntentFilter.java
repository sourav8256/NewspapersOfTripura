package in.springpebbles.newspapertripura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Switch;

/**
 * Created by sourav9674 on 8/1/2017.
 */

public class IntentFilter extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataStore dataStore = new DataStore(1,this);

        Intent intent = getIntent();
        Uri urlWithParameters = intent.getData();
        String type = urlWithParameters.getQueryParameter("type");
        Log.d("mylog","type of intent "+type);

        Intent intentToPass = new Intent(getApplicationContext(),NewsView.class);

        switch (type){
            case "online_news":
                intentToPass = new Intent(IntentFilter.this,NewsView.class);
                intentToPass.putExtra("url","http://www.tripurainfoway.com/"+urlWithParameters.getQueryParameter("id"));
                break;
            case "newspaper":
                intentToPass = new Intent(IntentFilter.this,webview.class);
                //dataStore.showToast(Uri.decode(dataStore.showUrl(urlWithParameters.getQueryParameter("url"),3)));
                intentToPass.putExtra("urlFromShareIntent", Uri.decode(dataStore.showUrl(urlWithParameters.getQueryParameter("url"),3)));
                intentToPass.putExtra("newspaperID", intent.getExtras().getInt("newspaperID"));
                intentToPass.putExtra("pageNumber", intent.getExtras().getInt("pageNumber"));
                break;
        }
        intentToPass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentToPass);
        finish();

    }
}
