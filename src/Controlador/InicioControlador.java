/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.UsuarioDAO;
import Vistas.MenuVista; // <--- NUEVO IMPORT
import Vistas.InicioAdminVista; // <--- NUEVO IMPORT
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
import javax.swing.JLabel;

/**
 *
 * @author Natalia
 */
public class InicioControlador implements ActionListener {
    
    // Declaramos ambas vistas para saber cuál estamos usando
    private InicioVista vistaEmpleado;
    private InicioAdminVista vistaAdmin;

    // --- CONSTRUCTOR 1: PARA EMPLEADOS (El que ya tenías) ---
    public InicioControlador(InicioVista vista) {
        this.vistaEmpleado = vista;
        this.vistaAdmin = null; // No hay admin aquí

        // Listeners
        this.vistaEmpleado.BRegPedido.addActionListener(this);
        this.vistaEmpleado.BListPedidos.addActionListener(this);
        this.vistaEmpleado.BInventario.addActionListener(this);

        // Configurar el botón de Salir (lblSalir)
        configurarBotonSalir(this.vistaEmpleado.lblSalir);
    }

    // --- CONSTRUCTOR 2: PARA ADMINISTRADORES (Nuevo) ---
    public InicioControlador(InicioAdminVista vista) {
        this.vistaAdmin = vista;
        this.vistaEmpleado = null; // No hay empleado aquí

        // Listeners Comunes (Asumiendo que usaste los mismos nombres de variables)
        this.vistaAdmin.BRegPedido.addActionListener(this);
        this.vistaAdmin.BListPedidos.addActionListener(this);
        this.vistaAdmin.BInventario.addActionListener(this);
        
        // Listener EXCLUSIVO de Admin (Editar Menú)
        // NOTA: Asegúrate que tu botón en la vista Admin se llame 'BEditarMenu'
        this.vistaAdmin.BEditarMenu.addActionListener(this); 

        // Configurar el botón de Salir (lblSalir)
        configurarBotonSalir(this.vistaAdmin.lblSalir);
    }
    
    // Método auxiliar para no repetir código del MouseListener
    private void configurarBotonSalir(JLabel boton) {
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cerrarSesion();
            }
        });
    }
    
    private void cerrarSesion() {
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(
                null,
                "¿Seguro que deseas cerrar sesión?",
                "Cerrar Sesión",
                javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            // Cerramos la ventana que esté abierta (sea admin o empleado)
            if (vistaEmpleado != null) {
                vistaEmpleado.dispose();
            } else if (vistaAdmin != null) {
                vistaAdmin.dispose();
            }

            // Abrir la ventana de Login
            LoginVista login = new LoginVista();
            login.setVisible(true);
            UsuarioDAO modelo = new UsuarioDAO();
            LoginControlador loginCtrl = new LoginControlador(modelo, login);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // --- REGISTRAR PEDIDO (Funciona para ambos) ---
        if ((vistaEmpleado != null && e.getSource() == vistaEmpleado.BRegPedido) || 
            (vistaAdmin != null && e.getSource() == vistaAdmin.BRegPedido)) {
            
            PedidoVista vistaPedidos = new PedidoVista();
            PedidoControlador ctrlPedidos = new PedidoControlador(vistaPedidos);
            vistaPedidos.setVisible(true);
        }
        
        // --- LISTA DE PEDIDOS (Funciona para ambos) ---
        if ((vistaEmpleado != null && e.getSource() == vistaEmpleado.BListPedidos) ||
            (vistaAdmin != null && e.getSource() == vistaAdmin.BListPedidos)) {
            
            ListaPedidosVista listavistaPedidos = new ListaPedidosVista();
            ListaPedidosControlador ctrlListaPedidos = new ListaPedidosControlador(listavistaPedidos);
            listavistaPedidos.setVisible(true);
        }
        
        // --- INVENTARIO (Funciona para ambos) ---
        if ((vistaEmpleado != null && e.getSource() == vistaEmpleado.BInventario) ||
            (vistaAdmin != null && e.getSource() == vistaAdmin.BInventario)) {
            
            InventarioVista invista = new InventarioVista();
            InventarioControlador ctrlInv = new InventarioControlador(invista);
            invista.setVisible(true);
        }

        // --- EDITAR MENU (SOLO ADMIN) ---
        if (vistaAdmin != null && e.getSource() == vistaAdmin.BEditarMenu) {
            MenuVista vMenu = new MenuVista();
            MenuControlador ctrlMenu = new MenuControlador(vMenu);
            vMenu.setVisible(true);
        }
    }
    
}