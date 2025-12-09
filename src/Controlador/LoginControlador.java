/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Modelo.Usuario;
import Modelo.UsuarioDAO;
import Vistas.LoginVista;
import Vistas.InicioAdminVista;
import Vistas.InicioVista;
import javax.swing.JOptionPane;
/**
 *
 * @author Natalia
 */
public class LoginControlador implements ActionListener{
    private UsuarioDAO usDao;
    private Usuario us;
    private LoginVista loginVista;
    
    public LoginControlador(UsuarioDAO usDao, LoginVista loginVista) {
        this.usDao = usDao;
        this.loginVista = loginVista;
        this.us = new Usuario();

        // Escuchamos el botón de la vista
        this.loginVista.btnIngresar.addActionListener(this);
    }

    public void iniciar() {
        loginVista.setTitle("Login - CafetoBoss");
        loginVista.setLocationRelativeTo(null);
        loginVista.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginVista.btnIngresar) {
            // Obtenemos datos de la vista
            String user = loginVista.txtUsuario.getText();
            String pass = String.valueOf(loginVista.txtPass.getPassword());

            // Validamos en el modelo
            us = usDao.login(user, pass);

            if (us != null) {
                JOptionPane.showMessageDialog(null, "Bienvenido/a " + us.getNombre());

                // Lógica para abrir la ventana según el rol del usuario
                if (us.getRol().equals("admin")) {
                    InicioAdminVista admin = new InicioAdminVista();
                    
                    admin.setVisible(true);
                } else {
                    InicioVista empleado = new InicioVista();
                    empleado.setVisible(true);
                    // Conectar el controlador
                    InicioControlador ctrlInicio = new InicioControlador(empleado);
                }

                // Cerramos el login
                this.loginVista.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Acceso denegado");
            }
        }
    }
    
}
