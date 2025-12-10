package Modelo;

import ConnectionDB.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

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
    
    // 3. LISTAR (Recuperar id_categoria)
    public List<Producto> listarProductosConCategoria() {
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
                prod.setId_categoria(rs.getInt("id_categoria")); // Recuperamos el ID
                lista.add(prod);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.toString());
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
    
    // 1. REGISTRAR (CREAR)
    public boolean registrar(Producto pro) {
        // Agregamos id_categoria al insert
        String sql = "INSERT INTO productos (nombre, precio, stock, id_categoria) VALUES (?, ?, ?, ?)";
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getNombre());
            ps.setDouble(2, pro.getPrecio());
            ps.setInt(3, pro.getStock());
            ps.setInt(4, pro.getId_categoria());
            ps.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar: " + e.toString());
            return false;
        }
    }

    //MODIFICAR
    public boolean modificar(Producto pro) {
        String sql = "UPDATE productos SET nombre=?, precio=?, stock=?, id_categoria=? WHERE id_producto=?";
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getNombre());
            ps.setDouble(2, pro.getPrecio());
            ps.setInt(3, pro.getStock());
            ps.setInt(4, pro.getId_categoria()); // Actualizamos categoría
            ps.setInt(5, pro.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar: " + e.toString());
            return false;
        }
    }

    // 3. ELIMINAR
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id_producto=?";
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.toString());
            return false;
        }
    }
    
}