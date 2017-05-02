package dot.dominionofcity.game;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import dot.dominionofcity.toollib.ConnectionHandler;

public class DominateTask extends AsyncTask<String,String,String> {
    private Context context;
    DominateTask (Context ctx) {
        context = ctx;
    }
    private AlertDialog.Builder builder;
    private String dominate_url = "http://come2jp.com/dominion/dominate.php";
    @Override
    protected String doInBackground(String... params) {
        try {
            String GeneratorID = params[0];
            builder.setTitle("Dominate Status");
            URL url = new URL(dominate_url);
            ConnectionHandler conHan = new ConnectionHandler(url);
            conHan.useSession(context);
            String post_data = URLEncoder.encode("GeneratorID","UTF-8")+"="+URLEncoder.encode(GeneratorID,"UTF-8");
            return conHan.post(post_data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected void onPreExecute() {
        builder= new AlertDialog.Builder(context);
        builder.setTitle("Dominate Status");
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        builder.setMessage(result);
        builder.setNegativeButton("Back",null);
        builder.show();
    }
}