import java.awt.*;

public class Enemy extends GameObject {
    private int speed = 5;

    public Enemy(int x, int y) {
        super(x, y, 40, 40, Color.RED);
    }

    @Override
    public void update() {
        y += speed; // Kẻ địch chỉ rơi xuống
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g); // Vẽ hình vuông đỏ từ lớp cha

        // Vẽ thêm mắt cho "ngầu"
        g.setColor(Color.BLACK);
        g.fillOval(x + 10, y + 10, 8, 8);
        g.fillOval(x + 22, y + 10, 8, 8);
    }
}