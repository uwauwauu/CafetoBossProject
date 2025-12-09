package Modelo;

import ConnectionDB.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    ConnectionDB acceso = ConnectionDB.getInstance();

    // Método para llenar el ComboBox
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Producto prod = new Producto();
                prod.setId(rs.getInt("id_producto"));
                prod.setNombre(rs.getString("nombre"));
                prod.setPrecio(rs.getDouble("precio"));
                prod.setStock(rs.getInt("stock")); 
                
                lista.add(prod);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.toString());
        }
        return lista;
    }
    
    // Método EXTRA: Por si quieres usar el filtro de "Categoría" que tienes en tu diseño
    public List<Producto> listarProductosPorCategoria(int idCategoria) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE id_categoria = ?";
        
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCategoria); // Pasamos el ID de la categoría seleccionada
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Producto prod = new Producto();
                prod.setId(rs.getInt("id_producto"));
                prod.setNombre(rs.getString("nombre"));
                prod.setPrecio(rs.getDouble("precio"));
                prod.setStock(rs.getInt("stock"));
                lista.add(prod);
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar productos: " + e.toString());
        }
        return lista;
    }
    
    // Método para obtener un producto específico (útil si necesitas refrescar el stock)
    public Producto buscarProducto(int id) {
        Producto prod = new Producto();
        String sql = "SELECT * FROM productos WHERE id_producto = ?";
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                prod.setId(rs.getInt("id_producto"));
                prod.setNombre(rs.getString("nombre"));
                prod.setPrecio(rs.getDouble("precio"));
                prod.setStock(rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.toString());
        }
        return prod;
    }
}