import java.awt.*;

public class Bullet extends GameObject {
    public Bullet(int x, int y) {
        super(x, y, 6, 15, Color.YELLOW);
    }

    @Override
    public void update() {
        y -= 12; // Đạn bay lên trên
    }
}