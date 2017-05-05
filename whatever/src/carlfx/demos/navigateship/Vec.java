package carlfx.demos.navigateship;

public class Vec {

    public double mx;
    public double my;

    public double x;
    public double y;

    public Vec(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec(double mx, double my, double centerX, double centerY) {
        this.x = convertX(mx, centerX);
        this.y = convertY(my, centerY);
        this.mx = mx;
        this.my = my;
    }

    public int quadrant() {
        int q = 0;
        if (x > 0 && y > 0) {
            q = 1;
        } else if (x < 0 && y > 0) {
            q = 2;
        } else if (x < 0 && y < 0) {
            q = 3;
        } else if (x > 0 && y < 0) {
            q = 4;
        }
        return q;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ") quadrant=" + quadrant();
    }

    static double convertX(double mouseX, double originX) {
        return mouseX - originX;
    }

    static double convertY(double mouseY, double originY) {
        return originY - mouseY;
    }

}
