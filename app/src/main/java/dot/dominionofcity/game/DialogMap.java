package dot.dominionofcity.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dot.dominionofcity.R;
import dot.dominionofcity.model.GenModel;

/**
 * Created by user on 15/3/2017.
 */

public class DialogMap extends AppCompatActivity implements OnMapReadyCallback {
    private double Lat,Lng;
    private LatLng MyPo;
    private LatLng[] genCoor = new LatLng[16];
    private Marker[] genMarker = new Marker[16];
    private Polygon mMutablePolygon;
    private Circle[] mCircles = new Circle[16];
    private GoogleMap mMap;
    private List<GenModel> GenModelList = new ArrayList<GenModel>();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);
            SharedPreferences pref = this.getSharedPreferences(
                    "genInfo", MODE_PRIVATE);
            String genInfoStr = pref.getString("genInfoStr", "");
            try {
                JSONArray oriArray = new JSONArray(genInfoStr);
                for (int i = 0; i < oriArray.length(); i++) {
                    JSONObject finalObject = oriArray.getJSONObject(i);
                    GenModel genModel = new GenModel();
                    genModel.setGenName(finalObject.getString("GeneratorName"));
                    genModel.setLat(finalObject.getDouble("latitude"));
                    genModel.setLng(finalObject.getDouble("longitude"));
                    GenModelList.add(genModel);
                }
            }catch (JSONException e){}

            for (int i = 0; i < GenModelList.size(); i++){
                genCoor[i] = new LatLng(GenModelList.get(i).getLat(), GenModelList.get(i).getLng());
            }

            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            Intent intent =getIntent();
            Lat = Double.parseDouble(intent.getStringExtra("Lat"));
            Lng = Double.parseDouble(intent.getStringExtra("Lng"));
            MyPo = new LatLng(Lat, Lng);


        }

        @Override
        public void onMapReady(GoogleMap map) {
            mMap = map;
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle_night));
            addMarkersToMap();
            addCirlesToMap();
            mMap.addMarker(new MarkerOptions()
                    .position(MyPo).title("YOU")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pegman)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyPo, 18));

            mMutablePolygon = map.addPolygon(new PolygonOptions()
                    .addAll(createRectangle(new LatLng(22.3179705, 114.170713), 0.0025, 0.0031))
                    .strokeColor(Color.HSVToColor(200, new float[]{72, 1, 1}))
                    .strokeWidth(20));


        }
        private void addCirlesToMap() {
            /*List<DraggableCircle> mCircles = new ArrayList<>();
            Circle mCircle;
            for (int i = 0; i < genMarker.length; i++) {
                DraggableCircle circle = new DraggableCircle(genCoor[i], 15);
                mCircles.add(circle);*/

            for (int i = 0; i < genMarker.length; i++) {
                mCircles[i] = mMap.addCircle(new CircleOptions()
                        .center(genCoor[i])
                        .radius(17)
                        .strokeWidth(10)
                        .strokeColor(Color.HSVToColor(150, new float[]{144, 1, 1}))
                        .fillColor(Color.HSVToColor(30, new float[]{144, 1, 1})));
            }

        }
        private void addMarkersToMap() {

            for (int i=0; i < genMarker.length; i++){
                genMarker[i] = mMap.addMarker(new MarkerOptions()
                        .position(genCoor[i])
                        .icon(BitmapDescriptorFactory.defaultMarker(72))
                        .title(GenModelList.get(i).getGenName()));

            }
        }

    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
    }

}