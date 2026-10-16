package crud.vista;

import crud.controlador.ClienteControl;
import crud.controlador.FacturaControl;
import crud.controlador.ProductoControl;
import crud.servicio.Tienda;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.time.Clock;

/**
 * Ventana principal con una pestaña por entidad.
 */
public class VentanaCrud extends JFrame implements Mensajes {

    public VentanaCrud() {
        super("Clientes, productos y facturas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Tienda tienda = new Tienda();
        ClienteControl clientes = new ClienteControl(tienda);
        ProductoControl productos = new ProductoControl(tienda);
        FacturaControl facturas = new FacturaControl(tienda, Clock.systemDefaultZone());
        DatosEjemplo.cargar(clientes, productos, facturas);

        PanelClientes panelClientes = new PanelClientes(clientes, this);
        PanelProductos panelProductos = new PanelProductos(productos, this);
        PanelFacturas panelFacturas = new PanelFacturas(facturas, this);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Clientes", panelClientes);
        pestanias.addTab("Productos", panelProductos);
        pestanias.addTab("Facturas", panelFacturas);
        // Una factura cambia el stock y usa clientes y productos: al cambiar de pestaña se recarga todo.
        pestanias.addChangeListener(e -> {
            panelClientes.refrescar();
            panelProductos.refrescar();
            panelFacturas.refrescar();
        });
        setContentPane(pestanias);
        setSize(860, 560);
        setLocationRelativeTo(null);
    }

    @Override
    public void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignorada) {
                // Se usa el look and feel de Java.
            }
            new VentanaCrud().setVisible(true);
        });
    }
}
