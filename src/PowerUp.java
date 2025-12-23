import java.awt.*;

public class PowerUp extends GameObject {

    public PowerUp(int x, int y) {
        super(x, y, 20, 20, Color.GREEN); // Quà hình vuông nhỏ màu xanh
    }

    @Override
    public void update() {
        y += 3; // Rơi chậm hơn kẻ địch một chút
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);

        // Vẽ chữ "P" (Power) bên trong
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("P", x + 5, y + 15);

        // Vẽ viền trắng cho dễ nhìn
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }
}