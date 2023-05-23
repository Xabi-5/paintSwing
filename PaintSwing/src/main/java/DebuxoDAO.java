import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DebuxoDAO implements DAO<Debuxo, Integer> {
    //Atributos
    private final Connection conexion;

    //Constructores
    public DebuxoDAO(Connection con) {
        this.conexion = con;
    }

    //Métodos
    @Override
    public List<Debuxo> getAll(Optional<Integer> idDebux) {
        List<Debuxo> debuxos = new ArrayList<>();
        try (Statement stDebuxo = conexion.createStatement();) {
            ResultSet rsDebuxos = stDebuxo.executeQuery("SELECT idDebuxo, nome FROM Debuxo" +
                    idDebux.map(id -> " WHERE idDebuxo=" + id).orElse(""));
            while (rsDebuxos.next()) {
                int idDebuxo = rsDebuxos.getInt(1);
                Debuxo debuxo = new Debuxo(rsDebuxos.getString("nome"));
                PintableDAO pintableDAO = new PintableDAO(conexion);
                debuxo.setFiguras(pintableDAO.getAll(Optional.of(idDebuxo)));

                debuxos.add(debuxo);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao ler os debuxos" + e.getMessage());
        }
        return debuxos;
    }

    @Override
    public int save(Debuxo debuxo, Optional<Integer> id) {
        int idDebuxo = 0;
        if (debuxo == null) {
            return 0;
        }
        try (PreparedStatement stDebuxo = conexion.prepareStatement("INSERT INTO Debuxo (nome) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS);) {
            stDebuxo.setString(1, debuxo.getNome());
            stDebuxo.executeUpdate();

            ResultSet ids = stDebuxo.getGeneratedKeys();
            if (ids.next()) {
                idDebuxo = ids.getInt(1);
                System.out.println("idDebuxo: " + idDebuxo);
                debuxo.setIdDebuxo(idDebuxo);
                PintableDAO pintableDAO = new PintableDAO(conexion);
                List<Pintable> pintables = debuxo.getFiguras();
                for (Pintable pintable : pintables) {
                    pintableDAO.save(pintable, Optional.of(idDebuxo));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gardar o debuxo" + debuxo + "\n" + e.getMessage());
        }
        return idDebuxo;
    }

    @Override
    public int delete(Debuxo debuxo) {
        if (debuxo == null) {
            return 0;
        }
        return deleteById((int) debuxo.getIdDebuxo());
    }

    @Override
    public int deleteById(Integer idDebuxo) {
        int borradas = 0;
        try (PreparedStatement stDebuxo = conexion.prepareStatement("DELETE FROM Debuxo WHERE idDebuxo =?");) {
            PintableDAO pintableDAO = new PintableDAO(conexion);
            pintableDAO.deleteById(idDebuxo);
            stDebuxo.setLong(1, idDebuxo);
            borradas = stDebuxo.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao borrar o debuxo por id: " + idDebuxo + "\n" + e.getMessage());
        }
        return borradas;
    }

    @Override
    public int deleteAll() {
        int r = 0;
        try (Statement st = conexion.createStatement()) {
            st.executeUpdate("DELETE FROM Point");
            st.executeUpdate("DELETE FROM Pintable");
            r = st.executeUpdate("DELETE FROM Debuxo");
        } catch (SQLException e) {
            System.out.println("Erro ao borrar os debuxos" + e.getMessage());
        }
        return r;

    }

    @Override
    public boolean update(Debuxo debuxo) {
        int actualizada = 0;
        if (debuxo == null) {
            return false;
        }
        try (PreparedStatement stDebuxo = conexion.prepareStatement("UPDATE Debuxo SET nome= ?" +
                " WHERE idDebuxo = ?");) {
            conexion.setAutoCommit(false);
            int idDebuxo = debuxo.getIdDebuxo();
            stDebuxo.setString(1, debuxo.getNome());
            stDebuxo.setInt(2, debuxo.getIdDebuxo());
            actualizada = stDebuxo.executeUpdate();
            if (actualizada != 0) {
                PintableDAO pintableDAO = new PintableDAO(conexion);
                pintableDAO.deleteById(debuxo.getIdDebuxo());

                ArrayList<Pintable> figuras = debuxo.getFiguras();
                for (Pintable figura : figuras) {
                    pintableDAO.save(figura, Optional.of(idDebuxo));
                }
            }
            conexion.commit();
        } catch (SQLException e) {
            System.out.println("Erro ao gardar o debuxo" + debuxo + "\n" + e.getMessage());
            try {
                conexion.rollback();
            } catch (SQLException ex) {
            }
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
            }
        }
        return actualizada != 0;
    }

    @Override
    public Optional<Debuxo> get(Integer idDebuxo) {
        Debuxo debuxo = null;
        try (Statement stDebuxos = conexion.createStatement();) {
            ResultSet rsDebuxos = stDebuxos.executeQuery("SELECT nome FROM Debuxo WHERE idDebuxo= " + idDebuxo);
            if (rsDebuxos.next()) {
                debuxo = new Debuxo(idDebuxo, rsDebuxos.getString(1));
                PintableDAO pintableDAO = new PintableDAO(conexion);
                debuxo.setFiguras(pintableDAO.getAll(Optional.of(idDebuxo)));
            }
        } catch (SQLException e) {
            System.out.println("Erro de SQL " + e.getMessage());
        }
        return Optional.ofNullable(debuxo);
    }

    public void saveDebuxoToFile(Debuxo d, File f){
        if(d==null){
            return;
        }
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
            bw.write(Debuxo.LINE_COMMENT + " Nome debuxo:" + System.lineSeparator());
            bw.write(d.getNome() + System.lineSeparator());
            int i = 0;
            List<Pintable> figuras = d.getFiguras();
            for(var figura : figuras) {
                bw.write(Debuxo.LINE_COMMENT + " Tipo de pintable " + (++i) + ":" + System.lineSeparator());
                bw.write(figura.getTipoPintable() + System.lineSeparator());
                bw.write(Debuxo.LINE_COMMENT + " Anchura " + (++i) + ":" + System.lineSeparator());
                bw.write(figura.getWidth() + System.lineSeparator());
                bw.write(Debuxo.LINE_COMMENT + " Cor " + (i) + ":" + System.lineSeparator());
                Color cor = figura.getCor();
                bw.write(cor.getRed() + "," + cor.getGreen() + "," + cor.getBlue() + System.lineSeparator());
                bw.write(Debuxo.LINE_COMMENT + " Puntos " + (i) + ":" + System.lineSeparator());
                ArrayList<Point> puntos = figura.getPuntos();
                for (Point punto : puntos) {
                    bw.write(punto.x + "," + punto.y + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Produciuse un erro de E/S");
        }
    }

    public Debuxo loadDebuxoFromFile(File f){
        Debuxo d = new Debuxo();
        try (BufferedReader br = new BufferedReader(new FileReader(f))){
            ArrayList<Pintable> pintables = new ArrayList<>();
            Pintable pintable = null;
            boolean nameRead = false;
            String linha;
            while ((linha = br.readLine()) != null){
                linha = linha.trim();
                if ((linha.length() > 0) && (linha.charAt(0) != Debuxo.LINE_COMMENT)){
                    if(!nameRead){
                        d.setNome(linha);
                        nameRead = true;
                    }else {
                        String[] campos = linha.split(",");
                        switch (campos.length){
                            case 1 -> {
                                try{
                                    int anchura = Integer.parseInt(linha);
                                    if (pintable != null){
                                        pintable.setWidth(anchura);
                                        System.out.println("anchura: " + Integer.valueOf(linha));
                                    }
                                }catch (NumberFormatException e){
                                    pintable = PintableFactory.getPintable(linha);
                                    pintables.add(pintable);
                                    System.out.println("Tipo: "+ linha);
                                }
                            }
                            case 2 -> {
                                if (pintable != null){
                                    pintable.addPunto(Integer.parseInt(campos[0].trim()),
                                            Integer.parseInt(campos[1].trim()));
                                }
                            }
                            case 3 -> {
                                if(pintable != null){
                                    pintable.setCor(Integer.parseInt(campos[0].trim()),
                                            Integer.parseInt(campos[1].trim()),
                                            Integer.parseInt(campos[2].trim()));
                                }
                            }
                        }
                    }
                }
            }
            d.setFiguras(pintables);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return d;
    }














}
