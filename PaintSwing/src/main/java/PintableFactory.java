import java.awt.*;
import java.util.ArrayList;

public class PintableFactory {
    public static enum TipoPintable {
        RUTA("Ruta"), LINEA("Line"), RECTANGULO("Rectangle"), RECTANGULO_REDONDEADO("RoundRectangle"),
        ELIPSE("Ellipse"), ARCO("Arc"), CURVA_CUADRATICA("QuadCurve"), CURVA_CUBICA("CubiCurve"), CIRCULO("Circle");
        private final String tipo;

        private TipoPintable(String tipo) {
            this.tipo = tipo;
        }

        public String getTipo() {
            return tipo;
        }
    }

    public static Pintable getPintable(String tipoFigura, ArrayList<Point> puntos, Color cor, int anchura) {
        if (tipoFigura == null) {
            return null;
        }
        if (tipoFigura.equalsIgnoreCase(TipoPintable.RUTA.tipo)) {
            return new Ruta(puntos, cor, anchura);
        } else if (tipoFigura.equalsIgnoreCase(TipoPintable.CIRCULO.tipo)) {
            //TODO, WIP
        } else if (tipoFigura.equalsIgnoreCase(TipoPintable.RECTANGULO.tipo)) {
            //TODO, WIP
        } else {

        }
        return null;
    }

    public static Pintable getPintable(String tipoFigura) {
        if (tipoFigura == null) {
            return null;
        }
        if (tipoFigura.equalsIgnoreCase(TipoPintable.RUTA.tipo)) {
            return new Ruta();
        } else if (tipoFigura.equalsIgnoreCase(TipoPintable.CIRCULO.tipo)) {
            //TODO, WIP
        } else if (tipoFigura.equalsIgnoreCase(TipoPintable.RECTANGULO.tipo)) {
            //TODO, WIP
        } else {

        }
        return null;
    }
}
