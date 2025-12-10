/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author Natalia
 */
import ConnectionDB.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class InsumoDAO {

    // Conexión (Ajusta esto a como conectas tu DB normalmente)
    Connection con;
    ConnectionDB acceso = ConnectionDB.getInstance();
    PreparedStatement ps;
    ResultSet rs;

    // 1. REGISTRAR
    public boolean registrar(Insumo in) {
        String sql = "INSERT INTO insumos (codigo, nombre, cantidad) VALUES (?, ?, ?)";
        try {
            con = acceso.getConnection(); // O tu método para obtener conexión
            ps = con.prepareStatement(sql);
            ps.setString(1, in.getCodigo());
            ps.setString(2, in.getNombre());
            ps.setInt(3, in.getCantidad());
            ps.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar: " + e.toString());
            return false;
        }
    }

    // 2. LISTAR
    public List<Insumo> listarInsumos() {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM insumos";
        try {
            con = acceso.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Insumo in = new Insumo();
                in.setId(rs.getInt("id"));
                in.setCodigo(rs.getString("codigo"));
                in.setNombre(rs.getString("nombre"));
                in.setCantidad(rs.getInt("cantidad"));
                lista.add(in);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return lista;
    }

}
