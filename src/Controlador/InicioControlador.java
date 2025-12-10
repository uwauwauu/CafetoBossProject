/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.UsuarioDAO;
import Vistas.InicioVista;
import Vistas.InventarioVista;
import Vistas.ListaPedidosVista;
import Vistas.LoginVista;
import Vistas.PedidoVista;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Natalia
 */
public class InicioControlador implements ActionListener {
    private InicioVista vista;

    public InicioControlador(InicioVista vista) {
        this.vista = vista;
        this.vista.BRegPedido.addActionListener(this);
        this.vista.BListPedidos.addActionListener(this);
        this.vista.BInventario.addActionListener(this);

        // Cambiar el cursor a "Mano" para que parezca botón al pasar el mouse
        this.vista.lblSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Agregar el evento de Clic
        this.vista.lblSalir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cerrarSesion();
            }
        });
    }
    
    private void cerrarSesion() {
        // Preguntar antes de salir (Recomendado)
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(
                null,
                "¿Seguro que deseas cerrar sesión?",
                "Cerrar Sesión",
                javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            // Cerrar la ventana actual (Menú)
            vista.dispose();

            // Abrir la ventana de Login
            LoginVista login = new LoginVista();
            login.setVisible(true);
            UsuarioDAO modelo = new UsuarioDAO();
            LoginControlador loginCtrl = new LoginControlador(modelo, login);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.BRegPedido) {
            PedidoVista vistaPedidos = new PedidoVista();
            // Conectar el controlador
            PedidoControlador ctrlPedidos = new PedidoControlador(vistaPedidos);
            // Mostrar la ventana
            vistaPedidos.setVisible(true);
        }
        if (e.getSource() == vista.BListPedidos) {
            ListaPedidosVista listavistaPedidos = new ListaPedidosVista();
            // Conectar el controlador
            ListaPedidosControlador ctrlListaPedidos = new ListaPedidosControlador(listavistaPedidos);
            // Mostrar la ventana
            listavistaPedidos.setVisible(true);
        }
        if (e.getSource() == vista.BInventario) {
            InventarioVista invista = new InventarioVista();
            // Conectar el controlador
            InventarioControlador ctrlInv = new InventarioControlador(invista);
            // Mostrar la ventana
            invista.setVisible(true);
        }
    }
    
}
