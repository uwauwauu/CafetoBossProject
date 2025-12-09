/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import ConnectionDB.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Statement;
/**
 *
 * @author Natalia
 */
public class PedidoDAO {
    Connection con;
    ConnectionDB acceso = ConnectionDB.getInstance();

    public boolean registrarPedido(Pedido pedido, List<DetallePedido> listaDetalles) {
        String sqlPedido = "INSERT INTO pedidos (total, fecha) VALUES (?, NOW())";
        String sqlDetalle = "INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps;
        ResultSet rs;

        try {
            con = acceso.getConnection();
            con.setAutoCommit(false); // ¡IMPORTANTE! Iniciamos transacción manual

            // Insertar la cabecera (El Pedido)
            ps = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, pedido.getTotal());
            ps.executeUpdate();

            // Obtener el ID generado (ej: Pedido #50)
            rs = ps.getGeneratedKeys();
            int idPedido = 0;
            if (rs.next()) {
                idPedido = rs.getInt(1);
            }

            // Insertar los productos de la lista
            ps = con.prepareStatement(sqlDetalle);
            for (DetallePedido det : listaDetalles) {
                ps.setInt(1, idPedido);
                ps.setInt(2, det.getId_producto());
                ps.setInt(3, det.getCantidad());
                ps.setDouble(4, det.getPrecio());
                ps.setDouble(5, det.getSubtotal());
                ps.addBatch(); // Agregamos al lote
            }
            ps.executeBatch(); // Ejecutamos todas las inserciones juntas

            con.commit(); // Guardamos cambios
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
            } // Si falla, deshacer todo
            System.out.println("Error: " + e.toString());
            return false;
        }
    }
}
