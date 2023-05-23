import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PintableDAO implements DAO<Pintable, Integer> {

    //Atributos
    private final Connection conexion;

    //Constructores
    public PintableDAO(Connection conexion) {
        this.conexion = conexion;
    }

    //Metodos

    @Override
    public List<Pintable> getAll(Optional<Integer> idDebuxo) {
        List<Pintable> pintables = new ArrayList<>();
        try (Statement stPintable = conexion.createStatement(); Statement stPoint = conexion.createStatement()) {
            ResultSet rsPintable = stPintable.executeQuery("SELECT * FROM Pintable " +
                    idDebuxo.map(id -> "WHERE idDebuxo = " + id).orElse(""));
            while (rsPintable.next()) {
                ArrayList<Point> puntos = new ArrayList<>();
                int idPintable = rsPintable.getInt("idPintable");
                ResultSet rsPoint = stPoint.executeQuery("SELECT x, y FROM Point WHERE idPintable=" + idPintable);
                while (rsPoint.next()) {
                    puntos.add(new Point(rsPoint.getInt(1), rsPoint.getInt(2)));
                }
                Pintable pintable = PintableFactory.getPintable(rsPintable.getString("tipo"), puntos,
                        new Color(rsPintable.getInt("red"),
                                rsPintable.getInt("green"),
                                rsPintable.getInt("blue")),
                        rsPintable.getInt("width"));
                pintables.add(pintable);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cargar os pintables " + e.getMessage());
        }
        return pintables;
    }

    @Override
    public int save(Pintable pintable, Optional<Integer> idDebuxoFK) {
        int idPintable = 0;
        if (pintable == null || idDebuxoFK.isEmpty()) {
            System.out.println("Nulo ou baleiro");
            return 0;
        }
        try (PreparedStatement psPintable = conexion.prepareStatement("INSERT INTO Pintable" +
                " (idDebuxo, tipo, width, red, green, blue) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psPuntos = conexion.prepareStatement("INSERT INTO Point (idPintable, x, y)" +
                     " VALUES (?,?,?)");) {
            psPintable.setInt(1, idDebuxoFK.get());
            psPintable.setString(2, pintable.getTipoPintable());
            psPintable.setInt(3, pintable.getWidth());
            psPintable.setInt(4, pintable.getCor().getRed());
            psPintable.setInt(5, pintable.getCor().getGreen());
            psPintable.setInt(6, pintable.getCor().getBlue());
            psPintable.executeUpdate();

            ResultSet ids = psPintable.getGeneratedKeys();

            if (ids.next()) {
                idPintable = ids.getInt(1);
                ArrayList<Point> puntos = pintable.getPuntos();
                for (Point punto : puntos) {
                    psPuntos.setInt(1, idPintable);
                    psPuntos.setInt(2, punto.x);
                    psPuntos.setInt(3, punto.y);
                    psPuntos.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao gardar o pintable " + ex.getMessage());
        }
        return idPintable;
    }

    @Override
    public int delete(Pintable p) {
        int r = 0;
        try (Statement st = conexion.createStatement()) {
            r = st.executeUpdate("DELETE FROM Point WHERE idPintable=" + p.getIdPintable());
            r = st.executeUpdate("DELETE FROM Pintable WHERE idPintable=" + p.getIdPintable());
        } catch (SQLException e) {
            System.out.println("Erro ao borrar pintables con ID" + e.getMessage());
        }
        return r;
    }

    @Override
    public int deleteById(Integer idDebuxo) {
        int r = 0;
        try (Statement st = conexion.createStatement()) {
            r = st.executeUpdate("DELETE FROM Point WHERE idPintable=" + idDebuxo);
        } catch (SQLException e) {
            System.out.println("Erro ao borrar os pintables do debuxo con id:" + e.getMessage());
        }
        return r;
    }

    @Override
    public int deleteAll() {
        int r = 0;
        try (Statement st = conexion.createStatement()) {
            st.executeUpdate("DELETE FROM Point");
            r = st.executeUpdate("DELETE FROM Pintable");
        } catch (SQLException e) {
            System.out.println("Erro ao borrar todos os pintables" + e.getMessage());
        }
        return r;
    }

    @Override
    public boolean update(Pintable p) {
        int actualizada = 0;
        if (p == null) {
            return false;
        }
        try (PreparedStatement psPintable = conexion.prepareStatement("UPDATE Pintable SET tipo=?, width=?," +
                " red = ?, green = ?, blue= ? WHERE idPintable=?"); Statement st = conexion.createStatement()) {
            int idPintable = p.getIdPintable();
            psPintable.setString(1, p.getTipoPintable());
            psPintable.setInt(2, p.getWidth());
            psPintable.setInt(3, p.getCor().getRed());
            psPintable.setInt(4, p.getCor().getGreen());
            psPintable.setInt(5, p.getCor().getBlue());
            psPintable.setInt(6, idPintable);
            actualizada = psPintable.executeUpdate();
            if (actualizada != 0) {
                st.executeUpdate("DELLETE FROM Point");
                ArrayList<Point> puntos = p.getPuntos();
                for (Point punto : puntos) {
                    st.executeUpdate("INSERT INTO Point (idPintable, x, y) VALUES (" + idPintable + ", "
                            + punto.x + ", " + punto.y + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao actualizar o pintable" + e.getMessage());
        }
        return actualizada != 0;
    }

    @Override
    public Optional<Pintable> get(Integer idPintable) {
        Pintable pintable = null;
        try (Statement stPintable = conexion.createStatement(); Statement stPoint = conexion.createStatement()) {
            ResultSet rsPintable = stPintable.executeQuery("SELECT * FROM Pintable  WHERE idPintable="
                    + idPintable);
            if (rsPintable.next()) {
                ArrayList<Point> puntos = new ArrayList<>();
                ResultSet rsPoint = stPoint.executeQuery("SELECT x, y FROM Point WHERE idPintable=" +
                        idPintable);
                while (rsPoint.next()) {
                    puntos.add(new Point(rsPoint.getInt(1), rsPoint.getInt(2)));
                }
                pintable = PintableFactory.getPintable(rsPintable.getString("Tipo"),
                        puntos, new Color(rsPintable.getInt("red"), rsPintable.getInt("green"),
                                rsPintable.getInt("blue")), rsPintable.getInt("width"));
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL en Optional" + e.getMessage());
        }
        return Optional.ofNullable(pintable);
    }
}
