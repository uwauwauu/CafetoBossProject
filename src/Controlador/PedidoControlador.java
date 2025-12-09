package Controlador;

import Modelo.*;
import Vistas.PedidoVista;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PedidoControlador implements ActionListener {

    private PedidoVista vista;
    private ProductoDAO prodDao;
    private PedidoDAO pedDao;
    private CategoriaDAO catDao;
    private DefaultTableModel modeloTabla;

    public PedidoControlador(PedidoVista vista) {
        this.vista = vista;
        this.prodDao = new ProductoDAO();
        this.pedDao = new PedidoDAO();
        this.catDao = new CategoriaDAO(); //
        this.modeloTabla = new DefaultTableModel();

        // Configuración de la tabla
        String[] titulos = {"ID", "Producto", "Cantidad", "Precio U.", "Subtotal"};
        modeloTabla.setColumnIdentifiers(titulos);
        this.vista.tablaPedidos.setModel(modeloTabla);

        // --- LISTENERS ---
        this.vista.btnAgregar.addActionListener(this);
        this.vista.btnGuardar.addActionListener(this);

        // Listener especial para cuando cambias la Categoría
        this.vista.cbxCategoria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarProductosPorCategoria();
            }
        });

        // Listener especial para mostrar el precio cuando eliges un producto
        this.vista.cbxProductos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarPrecioProducto();
            }
        });

        cargarCategorias(); // Cargar categorías al abrir la ventana
    }

    // Llenar el combo de Categorías
    private void cargarCategorias() {
        vista.cbxCategoria.removeAllItems();
        List<Categoria> lista = catDao.listarCategorias();
        for (Categoria cat : lista) {
            vista.cbxCategoria.addItem(cat);
        }
    }

    // Filtrar productos (Se llama al cambiar categoría)
    private void cargarProductosPorCategoria() {
        Categoria catSeleccionada = (Categoria) vista.cbxCategoria.getSelectedItem();
        vista.cbxProductos.removeAllItems();

        if (catSeleccionada != null) {
            // Usamos el método con filtro que creamos en ProductoDAO
            List<Producto> lista = prodDao.listarProductosPorCategoria(catSeleccionada.getId());
            for (Producto prod : lista) {
                vista.cbxProductos.addItem(prod);
            }
        }
    }

    // Mostrar precio unitario en el label/textfield
    private void mostrarPrecioProducto() {
        Producto prod = (Producto) vista.cbxProductos.getSelectedItem();
        if (prod != null) {
            // Asumiendo que tu campo de precio unitario se llama txtPrecio o lblPrecio
            vista.txtPrecioUnitario.setText(String.valueOf(prod.getPrecio()));
        }
    }

    // Calcular el total sumando la columna de la tabla
    private void calcularTotalGeneral() {
        double total = 0.00;
        int columnaSubtotal = 4; // La columna "Subtotal" es la 4 (0,1,2,3,4)

        for (int i = 0; i < vista.tablaPedidos.getRowCount(); i++) {
            total += Double.parseDouble(modeloTabla.getValueAt(i, columnaSubtotal).toString());
        }

        vista.txtTotal.setText(String.valueOf(total));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // --- AGREGAR A LA TABLA ---
        if (e.getSource() == vista.btnAgregar) {
            Producto prod = (Producto) vista.cbxProductos.getSelectedItem();

            // Validar cantidad
            int cantidad = Integer.parseInt(vista.spinnerCantidad.getValue().toString());

            if (prod != null && cantidad > 0) {
                double subtotal = prod.getPrecio() * cantidad;

                Object[] fila = new Object[5];
                fila[0] = prod.getId();
                fila[1] = prod.getNombre();
                fila[2] = cantidad;
                fila[3] = prod.getPrecio();
                fila[4] = subtotal;

                modeloTabla.addRow(fila);

                calcularTotalGeneral(); // <--- ¡AQUÍ LLAMAMOS AL CÁLCULO!
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione producto y cantidad mayor a 0");
            }
        }

        // --- BOTÓN GUARDAR (A la Base de Datos) ---
        if (e.getSource() == vista.btnGuardar) {
            Pedido pedido = new Pedido();
            pedido.setTotal(Double.parseDouble(vista.txtTotal.getText()));

            List<DetallePedido> detalles = new ArrayList<>();

            // Recorrer la tabla visual para llenar la lista
            for (int i = 0; i < vista.tablaPedidos.getRowCount(); i++) {
                DetallePedido det = new DetallePedido();
                det.setId_producto((int) vista.tablaPedidos.getValueAt(i, 0));
                det.setCantidad((int) vista.tablaPedidos.getValueAt(i, 2));
                det.setPrecio((double) vista.tablaPedidos.getValueAt(i, 3));
                det.setSubtotal((double) vista.tablaPedidos.getValueAt(i, 4));
                detalles.add(det);
            }

            if (pedDao.registrarPedido(pedido, detalles)) {
                JOptionPane.showMessageDialog(null, "¡Pedido registrado!");
                modeloTabla.setRowCount(0); // Limpiar tabla
            }
        }
    }

    // ... Métodos auxiliares para calcularTotalGeneral y cargarProductos ...
}
