package dot.dominionofcity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import dot.dominionofcity.toollib.ConnectionHandler;

public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    MainActivity mainActivity;
    AlertDialog.Builder builder;
    ConnectionHandler conHan;
    String username, password;
    BackgroundWorker (Context ctx) {
        context = ctx;
    }
    BackgroundWorker (MainActivity mainActivity, Context context) {
        this.mainActivity = mainActivity;
        this.context = context;
    }

    @Override
    protected String doInBackground(String...params) {
        String type = params[0];
        username = params[1];
        password = params[2];
        String login_url = "http://come2jp.com/dominion/login.php";
        String register_url = "http://come2jp.com/dominion/register.php";
        if(type.equals("login")) {
            try {
                URL url = new URL(login_url);
                conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                return conHan.post(post_data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(type.equals("register")){
            try {
                URL url = new URL(register_url);
                conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                return conHan.post(post_data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        builder= new AlertDialog.Builder(context);
        builder.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        if(result.startsWith("fail")) {
            builder.setMessage(result);
            builder.setNegativeButton("Back",null);
            builder.show();
        }else{
            context.getSharedPreferences("RememberMe", Context.MODE_PRIVATE)
                    .edit()
                    .putString("username", username)
                    .putString("password", password)
                    .apply();
//            builder.setMessage("Success");
//            builder.setPositiveButton("Enter Lobby ", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog,
//                                    int which) {
                    Intent intent = new Intent(context, Lobby.class);
                    context.startActivity(intent);
//                    mainActivity.finish();
//                }
//            });
        }
//        builder.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}