package dot.dominionofcity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import dot.dominionofcity.satellitehack.SatelliteHackActivity;
import dot.dominionofcity.toollib.ConnectionHandler;

public class Game extends AppCompatActivity {
    private TextView ScoreTextView;
    private TextView PointTextView;
    private TextView RoomTextView;
    EditText GeneratorIDEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button btn_dom = (Button)findViewById(R.id.btn_dom);
        Button btn_score = (Button)findViewById(R.id.btn_score);
        Button btn_PointStatus = (Button)findViewById(R.id.btn_PointStatus);
        ScoreTextView = (TextView)findViewById(R.id.ScoreTextView);
        PointTextView = (TextView)findViewById(R.id.PointTextView);
        RoomTextView = (TextView)findViewById(R.id.tvRoom);
        GeneratorIDEt = (EditText)findViewById(R.id.etGeneratorID);
        new SetRoomInfoTask(this).execute();
    }

    public void OnDominate(View view){
        String GeneratorID = GeneratorIDEt.getText().toString();
        new DominateTask(this).execute(GeneratorID);
        Intent satelliteHack = new Intent(Game.this,
                SatelliteHackActivity.class)
                .putExtra(SatelliteHackActivity.LEVEL, 2);
        startActivity(satelliteHack);
    }
    public void OnShowScore(View view) throws IOException {
        String score_url = "http://come2jp.com/dominion/showScore.php";
        new GetScoreTask(this).execute(score_url);
    }

    public void OnShowPointStatus(View view) throws IOException {
        String point_url = "http://come2jp.com/dominion/showPointStatus.php";
        new GetPointTask(this).execute(point_url);
    }

    public void OnGameOver(View view) throws IOException {
        String gameover_url = "http://come2jp.com/dominion/GameJudgement.php";
        new GameOver(this).execute(gameover_url);
    }

    public class GetScoreTask extends AsyncTask<String,String,String>{
        Context context;
        GetScoreTask (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String result = conHan.get();
                JSONObject parentObject = new JSONObject(result);
                String A_Score = parentObject.getString("A_score");
                String B_Score = parentObject.getString("B_score");
                return "A score: " + A_Score + " B score: " + B_Score;
            }catch (Exception e){
                e.printStackTrace();
            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ScoreTextView.setText(result);
        }
    }
    public class GetPointTask extends AsyncTask<String,String,String>{
        Context context;
        GetPointTask (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String result = conHan.get();
                JSONObject parentObject = new JSONObject(result);
                JSONArray parentArray = parentObject.getJSONArray("generators");
                StringBuffer finalBufferedData = new StringBuffer();
                for (int i=0;i<parentArray.length();i++){
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String uname = finalObject.getString("uname");
                    String GeneratorName = finalObject.getString("GeneratorName");
                    finalBufferedData.append(" Generator Name:  " + GeneratorName + " User Name: " + uname + "\n");
                }
                return finalBufferedData.toString();
            }catch (Exception e){
                e.printStackTrace();
            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            PointTextView.setText(result);
        }
    }
    public class SetRoomInfoTask extends AsyncTask<String,String,String>{
        Context context;
        SetRoomInfoTask (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String get_url = "http://come2jp.com/dominion/getSessionData.php";
                URL url = new URL(get_url);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String post_data = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode("rid", "UTF-8");
                String rid = conHan.post(post_data);
                return "Room Number: "+rid;
            }catch (Exception e){
                e.printStackTrace();
            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            RoomTextView.setText(result);
        }
    }
    public class DominateTask extends AsyncTask<String,String,String>{
        Context context;
        DominateTask (Context ctx) {
            context = ctx;
        }
        AlertDialog.Builder builder;
        String dominate_url = "http://come2jp.com/dominion/dominate.php";
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
                } catch (MalformedURLException e) {
                    e.printStackTrace();
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
}
