import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaintController {
    private Debuxo modelo;
    private final DebuxoDAO debuxoDAO;
    private final Lienzo vista;

    public PaintController(Debuxo modelo) {
        this.modelo = modelo;
        ConnectionManager conManager = ConnectionManager.getConnectionManager();
        debuxoDAO = new DebuxoDAO(conManager.getConnection());
        vista = new Lienzo(this);
    }

    public void setNome(String nome) {
        if (modelo != null) {
            modelo.setNome(nome);
        }
    }

    public String getNome() {
        if (modelo != null) {
            return modelo.getNome();
        }
        return "";
    }

    public void saveDebuxoToFile(File f) {
        if (modelo != null && debuxoDAO != null) {
            debuxoDAO.saveDebuxoToFile(modelo, f);
        }
    }

    public void saveDebuxoTODB() {
        if (modelo != null && debuxoDAO != null) {
            debuxoDAO.save(modelo, Optional.empty());
        }
    }

    public void loadDebuxoFromFile(File f) {
        if (debuxoDAO != null) {
            modelo = debuxoDAO.loadDebuxoFromFile(f);
        }
    }

    public void loadDebuxoFromDB(int idDebuxo) {
        if (debuxoDAO != null) {
            modelo = debuxoDAO.get(idDebuxo).orElseGet(() -> new Debuxo());
        }
    }

    public void deleteDebuxosFromDB() {
        if (debuxoDAO != null) {
            debuxoDAO.deleteAll();
        }
    }

    public void deleteDebuxosFromDB(int idDebuxo) {
        if (debuxoDAO != null) {
            debuxoDAO.deleteById(idDebuxo);
        }
    }

    public List<String> getNomeDebuxos() {
        List<String> nomes = new ArrayList<>();
        if (debuxoDAO != null) {
            List<Debuxo> debuxos = debuxoDAO.getAll(Optional.empty());
            for (Debuxo debuxo : debuxos) {
                nomes.add(debuxo.getNome());
            }
        }
        return nomes;
    }

    public List<Integer> getIdDebuxos() {
        List<Integer> ids = new ArrayList<>();
        if (debuxoDAO != null) {
            List<Debuxo> debuxos = debuxoDAO.getAll(Optional.empty());
            for (Debuxo debuxo : debuxos) {
                ids.add(debuxo.getIdDebuxo());
            }
        }
        return ids;
    }

    public Lienzo getLienzo() {
        return vista;
    }

    public void clear() {
        if (modelo != null) {
            modelo.clear();
        }
    }

    public void addShape(String tipo, ArrayList<Point> puntos, Color cor, int grosor) {
        modelo.addPintable(tipo, puntos, cor, grosor);
    }

    public ArrayList<Shape> getShapes() {
        return modelo.getShapes();
    }

    public ArrayList<Integer> getWidths() {
        return modelo.getWidths();
    }

    public ArrayList<Color> getColors() {
        return modelo.getColors();
    }


}
