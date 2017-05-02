package dot.dominionofcity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import dot.dominionofcity.game.Crystalization;
import dot.dominionofcity.model.RoomModel;
import dot.dominionofcity.toollib.ConnectionHandler;

public class Room extends AppCompatActivity {
    private TextView tvA1,tvA2,tvA3,tvB1,tvB2,tvB3;
    private TextView RoomTextView;
    private int mInterval = 3000; // 3 seconds by default, can be changed later
    private Handler mHandler;
    private Button btn_start;

    private RoomModel roomModel = new RoomModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        btn_start = (Button)findViewById(R.id.btnStartGame);
        Button btn_change = (Button)findViewById(R.id.btnChangeTeam);

        tvA1 = (TextView)findViewById(R.id.tvA1);
        tvA2 = (TextView)findViewById(R.id.tvA2);
        tvA3 = (TextView)findViewById(R.id.tvA3);
        tvB1 = (TextView)findViewById(R.id.tvB1);
        tvB2 = (TextView)findViewById(R.id.tvB2);
        tvB3 = (TextView)findViewById(R.id.tvB3);
        RoomTextView = (TextView)findViewById(R.id.tvRoom);
        new SetRoomPlayerTask(this).execute();
        new CheckAdminTask(this).execute();
        mHandler = new Handler();
        startRepeatingTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                new SetRoomPlayerTask(getApplicationContext()).execute(); //this function can change value of mInterval.
                new CheckAdminTask(getApplicationContext()).execute();
                new StartGameTask(getApplicationContext()).execute();
            } finally {
                // 100% guarantee that this always happens, even if,   your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public void OnStartGame(View view) {
        if(roomModel.getPlayerA().size() == roomModel.getPlayerB().size()){
            new ChangeStatus(this).execute();
        }
        else{
            AlertDialog.Builder builder;
            builder= new AlertDialog.Builder(Room.this);
            builder.setTitle("Start Game Fail");
            builder.setMessage("Players number not equal!");
            builder.setNegativeButton("Back",null);
            builder.show();
        }

    }
    public class ChangeStatus extends AsyncTask<String,Void,String> {
        Context context;
        ChangeStatus(Context ctx) {
            context = ctx;
        }
        String lobbyid = "1";
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://come2jp.com/dominion/changeStatus.php");
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String post_data = URLEncoder.encode("LobbyID", "UTF-8") + "=" + URLEncoder.encode(lobbyid, "UTF-8");
                return conHan.post(post_data);
            } catch (IOException e) {
                e.printStackTrace();
            }return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.startsWith("success")){
                startGame();
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }
    public void OnChangeTeam(View view){
        try {
            URL url = new URL("http://come2jp.com/dominion/changeTeam.php");
            ConnectionHandler conHan = new ConnectionHandler(url);
            conHan.useSession(this);
            conHan.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new SetRoomPlayerTask(this).execute();
    }

    public class SetRoomPlayerTask extends AsyncTask<String,String,RoomModel> {
        Context context;
        SetRoomPlayerTask(Context ctx) {
            context = ctx;
        }
        @Override
        protected RoomModel doInBackground(String... params) {
            String showPlayer_url = "http://come2jp.com/dominion/showRoomPlayer.php";
            try {
                URL url = new URL(showPlayer_url);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                RoomModel roomModel2 = new RoomModel();
                List<String> playerListA = new ArrayList<String>();
                List<String> playerListB = new ArrayList<String>();
                String result = conHan.get();
                JSONArray parentArray = new JSONArray(result);
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject playerObject = parentArray.getJSONObject(i);
                    if (playerObject.getString("team").equals("A")) {
                        playerListA.add(playerObject.getString("uname"));
                    } else if (playerObject.getString("team").equals("B")) {
                        playerListB.add(playerObject.getString("uname"));
                    }
                }
                roomModel2.setPlayerA(playerListA);
                roomModel2.setPlayerB(playerListB);
                return roomModel2;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
            @Override
            protected void onPostExecute(RoomModel result) {
                super.onPostExecute(result);
                tvA1.setText("");
                tvA2.setText("");
                tvA3.setText("");
                tvB1.setText("");
                tvB2.setText("");
                tvB3.setText("");
                List<String> playerA = result.getPlayerA();
                List<String> playerB = result.getPlayerB();
                for(int i=0;i<playerA.size();i++){
                    if(i==0){
                        tvA1.setText(playerA.get(i));
                    }else if(i==1){
                        tvA2.setText(playerA.get(i));
                    }else{
                        tvA3.setText(playerA.get(i));
                    }
                }
                for(int i=0;i<playerB.size();i++){
                    if(i==0){
                        tvB1.setText(playerB.get(i));
                    }else if(i==1){
                        tvB2.setText(playerB.get(i));
                    }else{
                        tvB3.setText(playerB.get(i));
                    }
                }
                roomModel = result;
            }
    }
    public class CheckAdminTask extends AsyncTask<String,Void,String> {
        Context context;
        CheckAdminTask (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            String checkadmin_url = "http://come2jp.com/dominion/checkRoomAdmin.php";
            try {
                URL url = new URL(checkadmin_url);
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
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.startsWith("Y")){
                btn_start.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }
    public void onQuitRoom(View view) {
        new QuitRoomTask(this).execute();
    }
    public class QuitRoomTask extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog.Builder builder;
        QuitRoomTask (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            String quitroom_url = "http://come2jp.com/dominion/quitGameRoom.php";
            try {
                URL url = new URL(quitroom_url);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                return conHan.get();
            } catch (IOException e) {
                e.printStackTrace();
            }return null;
        }

        @Override
        protected void onPreExecute() {
            builder= new AlertDialog.Builder(context);
            builder.setTitle("Quit Room");
        }

        @Override
        protected void onPostExecute(String result) {
            builder.setMessage(result);
            if(result.startsWith("fail")) {
                builder.setNegativeButton("Back",null);
            }else{
                builder.setPositiveButton("Back to Lobby", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        ((Room) context).finish();
                    }
                });
            }
            builder.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }
    public class StartGameTask extends AsyncTask<String,Void,String>
    {
        Context context;
        StartGameTask (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            String checkroom_url = "http://come2jp.com/dominion/checkRoom.php";
            try {
                URL url = new URL(checkroom_url);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String result = conHan.get();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }return null;
        }

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String result) {
            if(result.startsWith("start")) {
                startGame();
            }
    }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void startGame() {
        Intent intent = new Intent(Room.this, Crystalization.class);
        startActivity(intent);
        (Room.this).finish();
    }

}
