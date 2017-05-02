package dot.dominionofcity.game;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dot.dominionofcity.R;
import dot.dominionofcity.chatroom.Chatroom;
import dot.dominionofcity.chatroom.ChatroomView;
import dot.dominionofcity.model.GenModel;
import dot.dominionofcity.satellitehack.SatelliteHackActivity;
import dot.dominionofcity.toollib.ConnectionHandler;

public class Crystalization extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Chatroom chatroom;

    static TextView scoreTextView1;
    static TextView scoreTextView2;
    private TextView VS;
    private String LED="fonts/LED.ttf";
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    static Double myLatitude;
    static Double myLongitude;
    static Location lastLocation;
    static double EARTH_RADIUS = 6378137;
    private Button crystal[][] = new Button[4][4];
    private Button Mapbtn;
    private String team = "N";
    private int[][] bridge = new int[16][2];
    private boolean bridgeReady = false;
    private boolean genInfoReady = false;
    private int[] level = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private String[] crystalTeam = {"N","N","N","N","N","N","N","N","N","N","N","N","N","N","N","N"};
    static List<GenModel> genInfo = new ArrayList<GenModel>();
    private int mInterval = 3000;
    private Handler mHandler;
    private Handler mHandler1;
    private Boolean[] btnEnable= {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lbs);
        OnGetBridge();
        OnGetGenInfo();
        new getUserTeam(this).execute();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        scoreTextView1 = (TextView) findViewById(R.id.Score1);
        scoreTextView1.setTypeface(Typeface.createFromAsset(getAssets(),LED));
        scoreTextView2 = (TextView) findViewById(R.id.Score2);
        scoreTextView2.setTypeface(Typeface.createFromAsset(getAssets(),LED));
        VS = (TextView) findViewById(R.id.VS);
        VS.setTypeface(Typeface.createFromAsset(getAssets(),LED));
        try {
            chatroom = new Chatroom(Crystalization.this, "http://come2jp.com/dominion",
                    (ChatroomView) findViewById(R.id.chatroom));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        crystal[0][0] = (Button) findViewById(R.id.crystal00);
        crystal[0][1] = (Button) findViewById(R.id.crystal01);
        crystal[0][2] = (Button) findViewById(R.id.crystal02);
        crystal[0][3] = (Button) findViewById(R.id.crystal03);

        crystal[1][0] = (Button) findViewById(R.id.crystal10);
        crystal[1][1] = (Button) findViewById(R.id.crystal11);
        crystal[1][2] = (Button) findViewById(R.id.crystal12);
        crystal[1][3] = (Button) findViewById(R.id.crystal13);

        crystal[2][0] = (Button) findViewById(R.id.crystal20);
        crystal[2][1] = (Button) findViewById(R.id.crystal21);
        crystal[2][2] = (Button) findViewById(R.id.crystal22);
        crystal[2][3] = (Button) findViewById(R.id.crystal23);

        crystal[3][0] = (Button) findViewById(R.id.crystal30);
        crystal[3][1] = (Button) findViewById(R.id.crystal31);
        crystal[3][2] = (Button) findViewById(R.id.crystal32);
        crystal[3][3] = (Button) findViewById(R.id.crystal33);
        Mapbtn = (Button) findViewById(R.id.mapbtn);
        int screen_W = metrics.widthPixels;
        int crystalWH = (int) (screen_W * 0.85 / 4);
        int chatRoomW = (int) (screen_W * 0.85);
        int btnWH = (int) (screen_W * 0.15);
        for (int i = 0; i < crystal.length; i++) {
            for (int j = 0; j < crystal[i].length; j++) {
                GridLayout.LayoutParams params = (GridLayout.LayoutParams) crystal[i][j].getLayoutParams();
                params.height = crystalWH;
                params.width = crystalWH;
                crystal[i][j].setBackgroundResource(R.drawable.g1);
                crystal[i][j].setLayoutParams(params);
                crystal[i][j].setEnabled(false);
            }
        }

        GridLayout.LayoutParams param2 = (GridLayout.LayoutParams) Mapbtn.getLayoutParams();
        param2.height = btnWH;
        param2.width = btnWH;
        Mapbtn.setLayoutParams(param2);
        mHandler = new Handler();
        startRepeatingTask();
        mHandler1 = new Handler();
        startSettingName();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();




        locationRequest = new LocationRequest();
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(7 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
    void startRepeatingTask() {
        mStatusChecker.run();
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                OnShowScore(); //this function can change value of mInterval.
                OnStartMonDist();
            } finally {
                // 100% guarantee that this always happens, even if,   your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    void startSettingName() {
        settingName.run();
    }

    Runnable settingName = new Runnable() {
        @Override
        public void run() {
            try {
                new setName().execute(); //this function can change value of mInterval.

            } finally {
                mHandler1.postDelayed(settingName, mInterval);
            }
        }
    };

    void stopSettingName() {
        mHandler1.removeCallbacks(settingName);
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        myLatitude = lastLocation.getLatitude();
        myLongitude = lastLocation.getLongitude();

        //latitudeText.setText("Latitude : " + String.valueOf(location.getLatitude()));
        //longitudeText.setText("Longitude : " + String.valueOf(location.getLongitude()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
        startRepeatingTask();
        chatroom.setBeeperOn(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        stopRepeatingTask();
        chatroom.setBeeperOn(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
        stopRepeatingTask();
    }

    public void Play(View v){
        for (int i = 0; i < this.level.length; i++) {
            if (v.equals(crystal[i/crystal.length][i%crystal[0].length])) {
                Intent Dixon = new Intent(this, SatelliteHackActivity.class);
                Dixon.putExtra("id", i);
                Dixon.putExtra("level", level[i]);
                startActivityForResult(Dixon, 0);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            if (data.getBooleanExtra(SatelliteHackActivity.RESULT, false)) {
                int id = data.getIntExtra("id", -1);
                if (id > -1) {
                    new DominateTask(Crystalization.this)
                            .execute(String.valueOf(id));
                }
            }
        }
    }

    public void onMap(View v) {


        Intent dialogMap = new Intent(getApplicationContext(), DialogMap.class);
        dialogMap.putExtra("Lat", myLatitude.toString());
        dialogMap.putExtra("Lng", myLongitude.toString());
        startActivity(dialogMap);
    }

    public class setName extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void...params) {
            if(!genInfoReady || !bridgeReady){return false;}
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                for (int i = 0; i < bridge.length; i++) {
                    int row = bridge[i][0] - 1;
                    int col = bridge[i][1] - 1;
                    crystal[row][col].setText(genInfo.get(i).getGenName());
                }
                stopSettingName();
            }
        }
    }
    public void OnSend(View view) {
        for (int i = 0; i < bridge.length; i++) {
            int row = bridge[i][0] - 1;
            int col = bridge[i][1] - 1;
            crystal[row][col].setText(genInfo.get(i).getGenName());
        }
    }
    public void OnShowScore() {
        String score_url = "http://come2jp.com/dominion/showScore.php";
        new GetScoreTask(this).execute(score_url);
    }
    public void OnGetGenInfo() {
        String getGenInfo_url = "http://come2jp.com/dominion/getGenInfo.php";
        new GetGenInfo(this,this).execute(getGenInfo_url);
    }
    public void OnGetBridge() {
        String getBridge_url = "http://come2jp.com/dominion/getBridge.php";
        new GetBridge(this,this).execute(getBridge_url);
    }
    public void OnStartMonDist(){
        String getDist_url = "http://come2jp.com/dominion/showPointStatus.php";
        new OnMonDistance(this).execute(getDist_url);
    }

    public class GetBridge extends AsyncTask<String,Void,int[][]> {
        Context context;
        Activity act;
        Exception ee;
        int[][] bridgeModel = new int[16][3];

        GetBridge(Context ctx, Activity act) {
            context = ctx;
            this.act = act;
        }

        @Override
        protected int[][] doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String result = conHan.get();
                JSONArray oriArray = new JSONArray(result);

                for (int i = 0; i < oriArray.length(); i++) {
                    JSONObject finalObject = oriArray.getJSONObject(i);
                    bridgeModel[i][0] = finalObject.getInt("row");
                    bridgeModel[i][1] = finalObject.getInt("col");
                }
                return bridgeModel;
            } catch (Exception e) {
                ee = e;
                act.runOnUiThread( new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder3 = new AlertDialog.Builder((context));

                        builder3.setTitle("e")
                                .setMessage(ee.toString());
                        builder3.show();
                    }
                });

            }
            return null;
        }

        protected void onPostExecute(int[][] result) {
            super.onPostExecute(result);
            bridge = result;
            bridgeReady = true;
        }
    }

    public class GetGenInfo extends AsyncTask<String,Void,List<GenModel>> {
        Context ctx;
        Activity act;
        Exception ee;
        List<GenModel> GenModelList = new ArrayList<GenModel>();


        GetGenInfo(Context ctx, Activity act) {
            this.ctx = ctx;
            this.act = act;
        }

        @Override
        protected List<GenModel> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(this.ctx);
                String result = conHan.get();
                JSONArray oriArray = new JSONArray(result);
                for (int i = 0; i < oriArray.length(); i++) {
                    JSONObject finalObject = oriArray.getJSONObject(i);
                    GenModel genModel = new GenModel();
                    genModel.setGenName(finalObject.getString("GeneratorName"));
                    genModel.setLat(finalObject.getDouble("latitude"));
                    genModel.setLng(finalObject.getDouble("longitude"));
                    GenModelList.add(genModel);
                }

                return GenModelList;

            } catch (Exception e) {
                        ee = e;
                        act.runOnUiThread( new Runnable() {
                            public void run() {
                                AlertDialog.Builder builder3 = new AlertDialog.Builder((ctx));

                                builder3.setTitle("e")
                                        .setMessage(ee.toString());
                                builder3.show();
                            }
                        });
            }
            return null;
        }

        protected void onPostExecute(List<GenModel> result) {
            super.onPostExecute(result);
            genInfo = result;
            genInfoReady = true;
        }
    }
    private double rad(double d) {return d * Math.PI / 180.0;}
    public class OnMonDistance extends AsyncTask<String,Void,Boolean[]> {
        Context context;
        OnMonDistance(Context ctx) {
            context = ctx;
        }
        int[] aLevel = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        String[] aTeam = {"N","N","N","N","N","N","N","N","N","N","N","N","N","N","N","N"};
        Boolean[] pass= {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
        double a,b,s,radLat2;
        @Override
        protected Boolean[] doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String result = conHan.get();
                JSONArray oriArray = new JSONArray(result);
                for (int i = 0; i < oriArray.length(); i++) {
                    JSONObject finalObject = oriArray.getJSONObject(i);

                    aLevel[i] = finalObject.getInt("no_Of_Dominion");
                    aTeam[i] = finalObject.getString("team");
                }

                if(lastLocation != null) {
                    double radLat1 = rad(myLatitude);
                    double radLng1 = rad(myLongitude);
                    for(int i = 0; i< genInfo.size(); i++) {
                        radLat2 = rad(genInfo.get(i).getLat());
                        a = radLat1 - radLat2;
                        b = radLng1 - rad(genInfo.get(i).getLng());
                        s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
                        s = Math.round(s * EARTH_RADIUS * 10000) / 10000;
                        if (s <= 17 && (aTeam[i] != team)) {
                            pass[i] = true;
                        }
                    }
                    return pass;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //return pass;
            return pass;
        }

        protected void onPostExecute(Boolean[] result) {
            super.onPostExecute(result);
            if(result != btnEnable) {
                btnEnable = result;
                for(int i = 0; i< crystal.length; i++) {
                    for (int j = 0; j < crystal[i].length; j++) {
                        crystal[i][j].setEnabled(result[i * 4 + j]);
                    }
                }
            }
            if(!Arrays.equals(level,aLevel) || !Arrays.equals(level,aLevel)){
                level = aLevel;
                crystalTeam = aTeam;
                UpdateCrystal();
                //setCrystal();
            }
            crystal[0][0].setEnabled(true);
        }
    }

    public class getUserTeam extends AsyncTask<Void,Void,String> {
        Context ctx;
        getUserTeam(Context ctx) {
            this.ctx = ctx;
        }
        @Override
        protected String doInBackground(Void... aVoid){
            try {
                URL url = new URL("http://come2jp.com/dominion/getUserTeam.php");
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(this.ctx);
                String result = conHan.get();
                JSONObject jsonObject = new JSONObject(result);
                String aTeam = jsonObject.getString("team");
                return aTeam;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("N")){
                new getUserTeam(ctx).execute();
            } else {
                team = result;
            }
        }
    }

    public void UpdateCrystal(){
                for(int i = 0; i < crystalTeam.length; i++) {
                    if(!crystalTeam[i].equals("N")){
                        if(crystalTeam[i].equals("A")){
                            if(level[i] == 1) {
                                crystal[bridge[i][0]-1][bridge[i][1]-1].setBackgroundResource(R.drawable.m1);
                            } else if (level[i] == 2) {
                                crystal[bridge[i][0]-1][bridge[i][1]-1].setBackgroundResource(R.drawable.m2);
                            } else if (level[i] >= 3) {
                                crystal[bridge[i][0]-1][bridge[i][1]-1].setBackgroundResource(R.drawable.m3);
                            }
                        } else {
                            if (level[i] == 1) {
                                crystal[bridge[i][0]-1][bridge[i][1]-1].setBackgroundResource(R.drawable.c1);
                            } else if (level[i] == 2) {
                                crystal[bridge[i][0]-1][bridge[i][1]-1].setBackgroundResource(R.drawable.c2);
                            } else if (level[i] >= 3) {
                                crystal[bridge[i][0]-1][bridge[i][1]-1].setBackgroundResource(R.drawable.c3);
                            }
                        }
                    }
                }
    }
    public class GetScoreTask extends AsyncTask<String,Void,String[]>{
        Context context;
        GetScoreTask (Context ctx) {
            context = ctx;
        }
        @Override
        protected String[] doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                ConnectionHandler conHan = new ConnectionHandler(url);
                conHan.useSession(context);
                String result = conHan.get();
                JSONObject parentObject = new JSONObject(result);
                String[] Score = new String[2];
                Score[0] = parentObject.getString("A_score");
                Score[1] = parentObject.getString("B_score");
                return Score;
            }catch (Exception e){
                e.printStackTrace();
            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            scoreTextView1.setText(result[0]);
            scoreTextView2.setText(result[1]);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
