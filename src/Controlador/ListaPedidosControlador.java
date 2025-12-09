package Controlador;

import Modelo.Pedido;
import Modelo.PedidoDAO;
import Vistas.ListaPedidosVista;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;               // Para manejar fechas
import java.time.format.DateTimeFormatter;// Para los formatos
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ListaPedidosControlador implements ActionListener {

    private ListaPedidosVista vista;
    private PedidoDAO pedDao;
    private DefaultTableModel modelo;

    public ListaPedidosControlador(ListaPedidosVista vista) {
        this.vista = vista;
        this.pedDao = new PedidoDAO();
        this.modelo = new DefaultTableModel();

        // Configurar Columnas
        String[] titulos = {"ID Pedido", "Fecha", "Total", "Estado"};
        modelo.setColumnIdentifiers(titulos);
        vista.tablaListaPedidos.setModel(modelo);

        // Listeners
        this.vista.btnBuscar.addActionListener(this);
        this.vista.btnImprimir.addActionListener(this); // Asumo que el botón se llama btnImprimir según tu imagen
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // --- FILTRAR POR FECHA ---
        if (e.getSource() == vista.btnBuscar) {
            
            String fechaIngresada = vista.txtFecha.getText().trim(); // Ej: "5/12/24"

            if (!fechaIngresada.isEmpty()) {
                try {
                    // Definimos cómo viene la fecha del usuario (d/MM/yy)
                    DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("d/MM/yy");
                    
                    // Convertimos el String a un objeto Fecha real
                    // Esto validará si la fecha existe (ej. error si ponen 30/02/24)
                    LocalDate fechaObjeto = LocalDate.parse(fechaIngresada, formatoEntrada);
                    
                    // Definimos cómo la necesita la Base de Datos (yyyy-MM-dd)
                    DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String fechaParaSQL = fechaObjeto.format(formatoSalida); // "2024-12-05"

                    // Ahora sí llamamos al DAO con la fecha correcta
                    limpiarTabla();
                    List<Pedido> lista = pedDao.listarPedidosPorFecha(fechaParaSQL);

                    if (lista.isEmpty()) {
                         JOptionPane.showMessageDialog(null, "No se encontraron pedidos en esa fecha.");
                    }

                    for (Pedido p : lista) {
                        Object[] fila = {p.getId(), p.getFecha(), p.getTotal(), p.getEstado()};
                        modelo.addRow(fila);
                    }

                } catch (DateTimeParseException ex) {
                    // Esto ocurre si el usuario escribe "hola" o "50/50/24"
                    JOptionPane.showMessageDialog(null, 
                        "Formato de fecha inválido.\nUse el formato día/mes/año (ej: 05/12/24).");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor ingrese una fecha para buscar.");
            }
        }

        // --- EXPORTAR A EXCEL/CSV (Botón Imprimir/Exportar) ---
        if (e.getSource() == vista.btnImprimir) {
            exportarExcel(vista.tablaListaPedidos);
        }
    }

    private void limpiarTabla() {
        modelo.setRowCount(0);
    }

    private void exportarExcel(JTable tabla) {
        try {
            JFileChooser seleccionarArchivo = new JFileChooser();
            seleccionarArchivo.setDialogTitle("Guardar Reporte");

            int usuarioSeleccion = seleccionarArchivo.showSaveDialog(null);

            if (usuarioSeleccion == JFileChooser.APPROVE_OPTION) {
                File archivo = seleccionarArchivo.getSelectedFile();

                // Forzamos la extensión .csv si el usuario no la puso
                if (!archivo.getName().endsWith(".csv")) {
                    archivo = new File(archivo.getAbsolutePath() + ".csv");
                }

                BufferedWriter bfw = new BufferedWriter(new FileWriter(archivo));

                // Escribir Títulos
                for (int i = 0; i < tabla.getColumnCount(); i++) {
                    bfw.write(tabla.getColumnName(i));
                    if (i < tabla.getColumnCount() - 1) {
                        bfw.write(",");
                    }
                }
                bfw.newLine();

                // Escribir Datos
                for (int i = 0; i < tabla.getRowCount(); i++) {
                    for (int j = 0; j < tabla.getColumnCount(); j++) {
                        Object dato = tabla.getValueAt(i, j);
                        if (dato != null) {
                            bfw.write(dato.toString());
                        }
                        if (j < tabla.getColumnCount() - 1) {
                            bfw.write(",");
                        }
                    }
                    bfw.newLine();
                }

                bfw.close();
                JOptionPane.showMessageDialog(null, "¡Reporte guardado exitosamente!");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al exportar: " + e.getMessage());
        }
    }
}
