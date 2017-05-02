package dot.dominionofcity.game;

import android.content.Intent;
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

import java.util.Arrays;
import java.util.List;

import dot.dominionofcity.R;

import static dot.dominionofcity.game.Crystalization.genInfo;

/**
 * Created by user on 15/3/2017.
 */

public class DialogMap extends AppCompatActivity implements OnMapReadyCallback {
    private double Lat,Lng;
    private LatLng MyPo;
    private final LatLng[] genCoor = new LatLng[16];
    private Marker[] genMarker = new Marker[16];
    private Polygon mMutablePolygon;
    private Circle[] mCircles = new Circle[16];
    private GoogleMap mMap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);
            for (int i = 0; i < genInfo.size(); i++){
                genCoor[i] = new LatLng(genInfo.get(i).getLat(), genInfo.get(i).getLng());

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
                        .title(genInfo.get(i).getGenName()));

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