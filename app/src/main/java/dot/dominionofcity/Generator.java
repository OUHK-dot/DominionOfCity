package dot.dominionofcity;

import java.io.IOException;

/**
 * Created by dixon on 20/1/2017.
 */

/*
Generators represent the real locations to be occupied by players in the game.
Security increases each time the generator is occupied. (start from 1)
*/
public class Generator {
    private int id;
    private double latitude;
    private double longitude;
    private Team occupant;
    private int security;

    public Generator(int id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.occupant = null;
        security = 1;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double[] getCoordinate() {
        return new double[]{latitude, longitude};
    }

    public Team getOccupant() {
        return occupant;
    }

    public void setOccupant(Team occupant) {
        this.occupant = occupant;
    }

    public void occupied(Team occupant) {
        HttpPostTask postTask = new HttpPostTask("");
        String queryString = String.format("generator=%d&team=%d", id, occupant.getId());
        postTask.execute(queryString);
        setOccupant(occupant);
        security++;
    }
}
