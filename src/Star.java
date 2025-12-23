import java.awt.*;
import java.util.Random;

public class Star {
    private int x, y, speed;
    private Random random;

    public Star(int x, int y) {
        this.x = x;
        this.y = y;
        this.random = new Random();
        this.speed = random.nextInt(3) + 1; // Tốc độ ngẫu nhiên
    }

    public void update() {
        y += speed;
        // Nếu sao rơi quá màn hình thì reset lên đầu ngẫu nhiên
        if (y > SpaceShooterGame.HEIGHT) {
            y = 0;
            x = random.nextInt(SpaceShooterGame.WIDTH);
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, 2, 2);
    }
}