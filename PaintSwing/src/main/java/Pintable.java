import java.awt.*;
import java.util.ArrayList;

public interface Pintable {
    //Colour
    public Color getCor();

    public void setCor(Color cor);

    public void setCor(int r, int g, int b);

    //Width
    public int getWidth();

    public void setWidth(int grosor);

    //Figure
    public Shape getShape();

    public String getTipoPintable();

    //Points
    public void addPunto(int x, int y);

    public void addPunto(Point p);

    public ArrayList<Point> getPuntos();

    public int getIdPintable();

    public void setIdPintable(int id);
}
