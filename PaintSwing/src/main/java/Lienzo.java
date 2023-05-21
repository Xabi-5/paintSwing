import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lienzo extends JPanel implements MouseMotionListener, MouseListener {
    //Constantes
    public static Cursor CURSOR_CRUZ = new Cursor(Cursor.CROSSHAIR_CURSOR);
    public static Cursor CURSOR_TEXTO = new Cursor(Cursor.TEXT_CURSOR);
    public static final Color DEFAULT_COR = new Color(0, 153, 255);
    public static final int DEFAULT_GROSOR = 2;
    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;
    public static final Stroke PINCEL_PUNTEADO = new BasicStroke(DEFAULT_GROSOR, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 2f, new float[]{30, 10, 10, 10}, 3f);

    //Propiedades
    public static String fileExtension = "png";
    private Color cor = DEFAULT_COR;
    private int grosor = DEFAULT_GROSOR;
    private String tipoFigura = "Ruta";

    private final PaintController control;
    private final ArrayList<Point> puntos;

    private JPopupMenu popupNome;
    private JTextField tfNome;

    //Constructores
    public Lienzo(PaintController control) {
        this.control = control;
        puntos = new ArrayList<>();
        crearGUI();
    }

    public Lienzo() {
        this(null);
    }

    private void crearEditPopup() {
        tfNome = new JTextField();
        popupNome = new JPopupMenu();
        tfNome.addActionListener((ActionEvent e) -> {
            control.setNome(tfNome.getText());
            ((TitledBorder) getBorder()).setTitle(tfNome.getText());
            popupNome.setVisible(false);
            popupNome.getInvoker().revalidate();
            popupNome.getInvoker().repaint();
        });
        popupNome.setBorder(new EmptyBorder(0, 0, 0, 0));
        popupNome.add(tfNome);
    }

    private void crearGUI() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(control.getNome()));
        setCursor(CURSOR_CRUZ);
        crearEditPopup();
        addMouseMotionListener(this);
        addMouseListener(this);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public Color getCor() {
        return cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }

    public int getGrosor() {
        return grosor;
    }

    public void setGrosor(Integer grosor) {
        this.grosor = grosor;
    }

    public String getNome() {
        return control.getNome();
    }

    public void setNome(String nome) {
        control.setNome(nome);
    }

    public String getTipoFigura() {
        return tipoFigura;
    }

    public void setTipoFigura(String tipoFigura) {
        this.tipoFigura = tipoFigura;
    }

    public void saveImage(File f) {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));) {
            BufferedImage imaxe = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) imaxe.getGraphics();
            paintComponent(g);
            ImageIO.write(imaxe, fileExtension, out);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        control.clear();
        puntos.clear();
        repaint();
    }

    public boolean isDentroTitulo(Point p) {
        TitledBorder tituloBorder = (TitledBorder) getBorder();
        FontMetrics fm = this.getFontMetrics(tituloBorder.getTitleFont());
        int anchoTitulo = fm.stringWidth(tituloBorder.getTitle()) + 20;
        Rectangle contorno = new Rectangle(0, 0, anchoTitulo, fm.getHeight());
        return contorno.contains(p);
    }

    //Operacións con ficheiros
    public void loadDebuxoFromFile(File f) {
        control.loadDebuxoFromFile(f);
    }

    public void saveDebuxoToFile(File f) {
        control.saveDebuxoToFile(f);
    }

    public void loadDebuxoFromDB(int idDebuxo) {
        control.loadDebuxoFromDB(idDebuxo);
    }

    public void saveDebuxoTODB() {
        control.saveDebuxoTODB();
    }

    public List<String> getNomeDebuxos() {
        return control.getNomeDebuxos();
    }

    public List<Integer> getIdDebuxos() {
        return control.getIdDebuxos();
    }

    //Xestión de eventos

    @Override
    public void mousePressed(MouseEvent e) {
        puntos.add(e.getPoint());
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        puntos.add(e.getPoint());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        puntos.add(e.getPoint());
        puntos.add(null); //Esta linea engadina de probas
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 1){
            Border borde = this.getBorder();
            if(borde instanceof TitledBorder){
                TitledBorder tituloBorder = (TitledBorder) getBorder();
                FontMetrics fm = this.getFontMetrics(tituloBorder.getTitleFont());
                int anchoTitulo = fm.stringWidth(tituloBorder.getTitle()) +20;
                Rectangle contorno = new Rectangle(0, 0, anchoTitulo, fm.getHeight());
                if(contorno.contains(e.getPoint())){
                    tfNome.setText(tituloBorder.getTitle());
                    Dimension d = tfNome.getPreferredSize();
                    d.width = anchoTitulo;
                    popupNome.setPreferredSize(d);
                    popupNome.show(this, 0, 0);
                    tfNome.selectAll();
                    tfNome.requestFocusInWindow();
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(isDentroTitulo(e.getPoint())){
            setCursor(CURSOR_TEXTO);
        }else {
            setCursor(CURSOR_CRUZ);
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ArrayList<Shape> figuras = control.getShapes();
        ArrayList<Color> cores = control.getColors();
        ArrayList<Integer> grosores = control.getWidths();

        for (int i = 0; i < figuras.size(); i++) {
            g2.setColor(cores.get(i));
            g2.setStroke(new BasicStroke(grosores.get(i), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(figuras.get(i));
        }
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2f,
                new float[]{30, 10, 10, 10}, 3f));

        for (int i = 0; i < puntos.size() - 1; i++) {
            Point p1 = puntos.get(i);
            Point p2 = puntos.get(i+1);
            /*Este if tiven que engadilo eu. Cando o rato é soltado, engadirase un punto null ao Arraylist de puntos
            * Se ese valor null é detectado, non se debuxara unha linea recta entre os dous puntos.
            * Sen esto, a aplicacion fai unha liña recta entre o ultimo punto soltado e o primeiro premido*/
            if(p1 != null && p2 != null){
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }
}
