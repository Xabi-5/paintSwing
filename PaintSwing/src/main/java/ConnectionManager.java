import java.sql.*;

public class ConnectionManager {
    //Constantes
    public static final String JDBC_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:./src/main/resources/debuxos";

    //Non van a ser empregados, non hai usuarios
    public static final String DB_USER = "";
    public static final String DB_PASS = "";

    private static ConnectionManager conexionManager;

    //Atributos
    private final Connection conexion;

    //Constructor
    private ConnectionManager(Connection conexion) {
        this.conexion = conexion;
    }

    //Metodos
    public Connection getConnection() {
        return conexion;
    }

    public boolean isClosed() {
        try {
            return conexion.isClosed();
        } catch (SQLException ex) {
            System.out.println("Erro de SQL" + ex.getMessage());
        }
        return true;
    }

    public static final ConnectionManager getConnectionManager() {
        try {
            if (conexionManager == null || conexionManager.isClosed()) {
                /*synchronized (ConnectionManager.class) {
                    if (conexionManager.isClosed() || conexionManager == null) {*/
                        conexionManager = new ConnectionManager(
                                DriverManager.getConnection(DB_URL));
                    }
               // }
           // }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar" + ex.getMessage());
        }
        return conexionManager;
    }


}
