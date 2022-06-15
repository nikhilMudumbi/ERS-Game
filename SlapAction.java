import java.time.LocalTime;

public class SlapAction implements Comparable<SlapAction> {
    public long time;
    public int player;

    public SlapAction(long time, int player) {
        this.time = time;
        this.player = player;
    }

    @Override
    public int compareTo(SlapAction o) {
        if (this.time != o.time) {
            if (this.time > o.time) return 1;
            else return -1;
        }
        if (Math.random() <= 0.5) {
            return 1;
        }
        return -1;
    }

    public long getTime() {
        return time;
    }

    public int getPlayer() {
        return player;
    }
    
}