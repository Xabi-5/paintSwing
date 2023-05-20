import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

public class Ruta extends Path2D.Float implements Pintable {

    //Constantes
    private static final Color DEFAULT_COLOR = new Color(0, 153, 255);
    private static final int DEFAULT_WIDTH = 2;

    //Atributos
    private Color color;
    private int width;
    private int idRuta;

    //Constructores
    public Ruta(Point puntoInicial, Color color, int width) {
        this.color = color;
        this.width = width;
        moveTo(puntoInicial.x, puntoInicial.y);
    }

    public Ruta(ArrayList<Point> puntos, Color cor, int width) {
        this.color = color;
        this.width = width;
        if (puntos != null && !puntos.isEmpty()) {
            moveTo(puntos.get(0).x, puntos.get(0).y);
            for (int i = 1; i < puntos.size(); i++) {
                lineTo(puntos.get(i).x, puntos.get(i).y);
            }
        }
    }

    public Ruta(Color color, int width) {
        this.color = color;
        this.width = width;
    }

    public Ruta() {
        this.color = DEFAULT_COLOR;
        this.width = DEFAULT_WIDTH;
    }


    @Override
    public Color getCor() {
        return color;
    }

    @Override
    public void setCor(Color cor) {
        this.color = cor;
    }

    @Override
    public void setCor(int r, int g, int b) {
        color = new Color(r, g, b);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int grosor) {
        width = grosor;
    }

    public long getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    @Override
    public Shape getShape() {
        return this;
    }

    //TODO
    @Override
    public String getTipoPintable() {
        return PintableFactory.TipoPintable.RUTA.getTipo();
    }

    @Override
    public void addPunto(int x, int y) {
        Point2D actual = getCurrentPoint();
        if (actual == null) {
            moveTo(x, y);
        } else {
            lineTo(x, y);
        }
    }

    @Override
    public void addPunto(Point p) {
        if (p != null) {
            addPunto(p.x, p.y);
        }
    }

    @Override
    public ArrayList<Point> getPuntos() {
        ArrayList<Point> puntos = new ArrayList<>();
        float[] coords = new float[6];
        PathIterator pathIterator = getPathIterator(new AffineTransform());
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO -> {
                    puntos.add(new Point((int) coords[0], (int) coords[1]));
                }
                case PathIterator.SEG_LINETO -> {
                    puntos.add(new Point((int) coords[0], (int) coords[1]));
                }
                case PathIterator.SEG_QUADTO -> {
                    puntos.add(new Point((int) coords[0], (int) coords[1]));
                }
                case PathIterator.SEG_CUBICTO -> {
                    puntos.add(new Point((int) coords[0], (int) coords[1]));
                }
                case PathIterator.SEG_CLOSE -> {
                    System.out.println("cerrar\n");
                }
            }
            pathIterator.next();
        }
        return puntos;
    }

    @Override
    public int getIdPintable() {
        return idRuta;
    }

    @Override
    public void setIdPintable(int idRuta) {
        this.idRuta = idRuta;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Ruta de cor RGB");
        sb.append(color.getRed()).append(", ").append(color.getGreen()).append(", ")
                .append(color.getBlue()).append(", ").append(" e anchura")
                .append(width).append(" pixeles. Puntos: ");
        ArrayList<Point> puntos = getPuntos();
        puntos.forEach(punto -> {
            sb.append('(').append(punto.x).append(", ")
                    .append(punto.y).append(')').append(" ");
        });
        return sb.toString();
    }


}
