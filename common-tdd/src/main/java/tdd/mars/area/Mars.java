package tdd.mars.area;

import lombok.Data;

@Data
public class Mars {


    private  int x;

    private int y;

    public Mars(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
