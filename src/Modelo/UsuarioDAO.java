package Modelo;

import ConnectionDB.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UsuarioDAO {
    
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    ConnectionDB acceso = ConnectionDB.getInstance(); // Usamos tu Singleton

    public Usuario login(String user, String pass) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass); // NOTA: En producción, aquí deberías comparar hashes, no texto plano.
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre_completo"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("password"));
                usuario.setRol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.err.println("Error en el login: " + e.toString());
        }
        // Retorna el objeto Usuario lleno si lo encontró, o null si no.
        return usuario;
    }
}