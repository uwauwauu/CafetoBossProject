package Controlador;

import Modelo.Categoria;
import Modelo.CategoriaDAO;
import Modelo.Producto;
import Modelo.ProductoDAO;
import Vistas.MenuVista;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class MenuControlador implements ActionListener {

    private Producto pro = new Producto();
    private ProductoDAO proDao = new ProductoDAO();
    private CategoriaDAO catDao = new CategoriaDAO(); // Necesitamos el DAO de categorías
    private MenuVista vista;
    private DefaultTableModel modelo = new DefaultTableModel();

    public MenuControlador(MenuVista vista) {
        this.vista = vista;

        // 1. Configurar Tabla (Agregamos columna Categoria ID oculta visualmente si quieres, o visible)
        String[] titulos = {"ID", "Nombre", "Precio", "Stock", "ID Categoria"};
        modelo.setColumnIdentifiers(titulos);
        this.vista.tablaMenu.setModel(modelo);

        // 2. LLENAR EL COMBOBOX DE CATEGORIAS
        llenarCategorias();

        // 3. Listeners
        this.vista.btnCrear.addActionListener(this);
        this.vista.btnActualizar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        this.vista.btnBuscar.addActionListener(this);

        // 4. Evento Click en Tabla
        this.vista.tablaMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = vista.tablaMenu.rowAtPoint(e.getPoint());

                vista.txtCodigo.setText(vista.tablaMenu.getValueAt(fila, 0).toString());
                vista.txtNombre.setText(vista.tablaMenu.getValueAt(fila, 1).toString());
                vista.txtPrecio.setText(vista.tablaMenu.getValueAt(fila, 2).toString());
                vista.spnStock.setValue(Integer.valueOf(vista.tablaMenu.getValueAt(fila, 3).toString()));

                // SELECCIONAR LA CATEGORÍA CORRECTA EN EL COMBOBOX
                int idCat = Integer.parseInt(vista.tablaMenu.getValueAt(fila, 4).toString());
                seleccionarCategoriaPorId(idCat);

                vista.txtCodigo.setEditable(false);
            }
        });

        listarTabla();
    }

    // --- MÉTODO PARA LLENAR EL JCOMBOBOX ---
    private void llenarCategorias() {
        List<Categoria> lista = catDao.listarCategorias();
        DefaultComboBoxModel modelCombo = new DefaultComboBoxModel(lista.toArray());
        vista.cbxCategoria.setModel(modelCombo);
    }

    // --- MÉTODO AUXILIAR PARA SELECCIONAR EN EL COMBO ---
    private void seleccionarCategoriaPorId(int id) {
        for (int i = 0; i < vista.cbxCategoria.getItemCount(); i++) {
            Categoria cat = (Categoria) vista.cbxCategoria.getItemAt(i);
            if (cat.getId() == id) {
                vista.cbxCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // --- CREAR ---
        if (e.getSource() == vista.btnCrear) {
            if (vista.txtNombre.getText().isEmpty() || vista.txtPrecio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Complete los campos");
            } else {
                try {
                    pro.setNombre(vista.txtNombre.getText());
                    pro.setPrecio(Double.parseDouble(vista.txtPrecio.getText()));
                    pro.setStock((int) vista.spnStock.getValue());

                    // OBTENER CATEGORÍA SELECCIONADA
                    Categoria catSeleccionada = (Categoria) vista.cbxCategoria.getSelectedItem();
                    pro.setId_categoria(catSeleccionada.getId()); // Guardamos el ID

                    if (proDao.registrar(pro)) {
                        JOptionPane.showMessageDialog(null, "Producto Registrado");
                        limpiarCampos();
                        listarTabla();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error en los datos: " + ex.getMessage());
                }
            }
        }

        // --- ACTUALIZAR ---
        if (e.getSource() == vista.btnActualizar) {
            if (vista.txtCodigo.getText().isEmpty()) {
                return;
            }
            try {
                pro.setId(Integer.parseInt(vista.txtCodigo.getText()));
                pro.setNombre(vista.txtNombre.getText());
                pro.setPrecio(Double.parseDouble(vista.txtPrecio.getText()));
                pro.setStock((int) vista.spnStock.getValue());

                // OBTENER CATEGORÍA
                Categoria catSeleccionada = (Categoria) vista.cbxCategoria.getSelectedItem();
                pro.setId_categoria(catSeleccionada.getId());

                if (proDao.modificar(pro)) {
                    JOptionPane.showMessageDialog(null, "Producto Actualizado");
                    limpiarCampos();
                    listarTabla();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }

        // --- ELIMINAR (Igual que antes) ---
        if (e.getSource() == vista.btnEliminar) {
            if (!vista.txtCodigo.getText().isEmpty()) {
                int id = Integer.parseInt(vista.txtCodigo.getText());
                if (proDao.eliminar(id)) {
                    limpiarCampos();
                    listarTabla();
                }
            }
        }

        if (e.getSource() == vista.btnLimpiar) {
            limpiarCampos();
        }
        if (e.getSource() == vista.btnBuscar) {
            listarTabla();
        }
    }

    public void listarTabla() {
        modelo.setRowCount(0);
        List<Producto> lista = proDao.listarProductosConCategoria();
        Object[] ob = new Object[5];
        for (int i = 0; i < lista.size(); i++) {
            ob[0] = lista.get(i).getId();
            ob[1] = lista.get(i).getNombre();
            ob[2] = lista.get(i).getPrecio();
            ob[3] = lista.get(i).getStock();
            ob[4] = lista.get(i).getId_categoria(); // Mostramos ID Categoría en tabla
            modelo.addRow(ob);
        }
        vista.tablaMenu.setModel(modelo);
    }

    public void limpiarCampos() {
        vista.txtCodigo.setText("");
        vista.txtCodigo.setEditable(true);
        vista.txtNombre.setText("");
        vista.txtPrecio.setText("");
        vista.spnStock.setValue(0);
        vista.cbxCategoria.setSelectedIndex(0); // Reiniciar combo
    }
}
