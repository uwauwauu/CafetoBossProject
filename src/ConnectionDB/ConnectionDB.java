package ConnectionDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;  

public class ConnectionDB {
   
    private static ConnectionDB instance = null;
    private static Connection con;
    
    // Ya no son final strings fijos, se cargarán del archivo
    
    private ConnectionDB(){
        try {
            // Cargar el archivo de propiedades
            Properties props = new Properties();
            // Entramos al paquete properties
            InputStream input = getClass().getResourceAsStream("/properties/db.properties");
            
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo db.properties");
            }
            
            props.load(input);

            //Obtener los valores del archivo
            String driver = props.getProperty("db.driver");
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Carga la clase Driver
            Class.forName(driver);
            
            // Establecer la conexión con las variables cargadas
            con = DriverManager.getConnection(url, user, password);
            
            System.out.println("Conexión establecida exitosamente");

        } catch (Exception ex) {
            System.out.println("Error al establecer la conexión: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public synchronized static ConnectionDB getInstance(){
        if(instance == null){
            instance = new ConnectionDB();
        }
        return instance;  
    }
    
    public Connection getConnection() {
        return con;
    }

    public void close() {
        instance = null;
    }
}