package dot.dominionofcity;

import java.util.ArrayList;

/**
 * Created by dixon on 20/1/2017.
 */

/*
Team is inherited from ArrayList to store users as members.
*/
public class Team extends ArrayList{
    private int id;
    private int score;

    public Team(int id) {
        super();
        this.id = id;
        score = 0;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
