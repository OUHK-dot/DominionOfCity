package dot.dominionofcity.model;

/**
 * Created by user on 30/3/2017.
 */

public class GenModel {
    private String GenName;
    private double lat;
    private double lng;

    public String getGenName() {return GenName;}
    public void setGenName(String GenName) { this.GenName = GenName;}

    public double getLat() {return lat;}
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
}
