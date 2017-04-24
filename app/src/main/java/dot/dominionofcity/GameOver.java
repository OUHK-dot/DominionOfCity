package dot.dominionofcity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GameOver extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog.Builder builder;
    GameOver (Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String gameover_url = params[0];
        try {
            URL url = new URL(gameover_url);
            ConnectionHandler conHan = new ConnectionHandler(url);
            conHan.useSession(context);
            return conHan.get();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }return null;
    }

    @Override
    protected void onPreExecute() {
        builder= new AlertDialog.Builder(context);
        builder.setTitle("Game Finish");
    }

    @Override
    protected void onPostExecute(String result) {
        builder.setMessage(result);
        if(result.startsWith("Game Finished")) {
            builder.setPositiveButton("Back to Lobby", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int which) {
                    ((Game) context).finish();
                }
            });
        }else{
            builder.setNegativeButton("Back",null);
        }
        builder.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
