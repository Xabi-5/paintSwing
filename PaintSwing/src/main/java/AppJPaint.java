import javax.swing.*;

public class AppJPaint {
    public static void main(String[] args) {

        PaintController control = new PaintController(new Debuxo("Sen nome"));
        new VentaPaint("MeuPaint", control.getLienzo());
    }
}
