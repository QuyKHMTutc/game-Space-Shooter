import java.awt.*;

public class Bullet extends GameObject {
    private double dx; // Vận tốc theo phương ngang
    private double dy; // Vận tốc theo phương dọc

    // Constructor cho đạn bay thẳng (Mặc định)
    public Bullet(int x, int y) {
        super(x, y, 6, 15, Color.YELLOW);
        this.dx = 0;
        this.dy = -12; // Bay thẳng lên
    }

    // Constructor cho đạn bay theo góc (Dùng cho Level 4, 5)
    // angle: Góc lệch so với phương thẳng đứng (độ)
    public Bullet(int x, int y, double angle) {
        super(x, y, 6, 15, Color.YELLOW);
        double speed = 12.0;
        // Tính toán vector vận tốc dựa trên góc
        // Math.toRadians nhận vào độ, sin/cos tính toán hướng
        this.dx = Math.sin(Math.toRadians(angle)) * speed;
        this.dy = -Math.cos(Math.toRadians(angle)) * speed;
    }

    @Override
    public void update() {
        x += dx;
        y += dy;
    }
}