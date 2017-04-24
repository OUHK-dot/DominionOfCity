package dot.dominionofcity.model;

import java.util.List;

/**
 * Created by Tim on 4/2/2017.
 */

public class RoomModel {
    private String rid;
    private List<String> playerA;
    private List<String> playerB;

    public String getRid() {
        return rid;
    }
    public void setRid(String rid) {
        this.rid = rid;
    }

    public List<String> getPlayerB() {
        return playerB;
    }

    public void setPlayerB(List<String> playerB) {
        this.playerB = playerB;
    }

    public List<String> getPlayerA() {
        return playerA;
    }

    public void setPlayerA(List<String> playerA) {
        this.playerA = playerA;
    }
}
