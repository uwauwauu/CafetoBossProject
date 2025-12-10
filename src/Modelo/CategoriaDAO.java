package Modelo;

import ConnectionDB.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    ConnectionDB acceso = ConnectionDB.getInstance();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setId(rs.getInt("id_categoria"));
                cat.setNombre(rs.getString("nombre"));
                lista.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("Error listar categorias: " + e.toString());
        }
        return lista;
    }
    
}
