package dot.dominionofcity;

import java.util.List;

/**
 * Created by dixon on 20/1/2017.
 */

public class Game {
    private int id;
    private Team[] teams;
    private Generator[][] generators;

    public Game(int id, Team[] teams, Generator[][] generators) {
        this.id = id;
        this.teams = teams;
        this.generators = generators;
    }

    public int getId() {
        return id;
    }

    public Team[] getTeams() {
        return teams;
    }

    public void setTeams(Team[] teams) {
        this.teams = teams;
    }

    public Generator[][] getGenerators() {
        return generators;
    }

    public void setGenerators(Generator[][] generators) {
        this.generators = generators;
    }

    // TODO: 22/1/2017 get game information update from server 
}
