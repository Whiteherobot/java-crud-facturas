package crud.vista;

import crud.controlador.FacturaControl;
import crud.modelo.Cliente;
import crud.modelo.Factura;
import crud.modelo.Producto;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

class PanelFacturas extends PanelCrud<Factura> {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    final JComboBox<Cliente> cliente = new JComboBox<>();
    final JComboBox<Producto> producto = new JComboBox<>();
    final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 10_000, 1));

    private final FacturaControl control;

    PanelFacturas(FacturaControl control, Mensajes mensajes) {
        super("Factura", new ModeloTabla<Factura>()
                .numero("Número", Factura::numero)
                .columna("Fecha", f -> f.fecha().format(FECHA))
                .columna("Cliente", f -> control.cliente(f.codigoCliente()).nombreCompleto())
                .columna("Producto", f -> control.producto(f.codigoProducto()).nombre())
                .numero("Cantidad", Factura::cantidad)
                .numero("P. unitario", f -> "$" + f.precioUnitario())
                .numero("Total con IVA", f -> "$" + f.total()), mensajes);
        this.control = control;
        agregarCampo("Cliente", cliente);
        agregarCampo("Cantidad", cantidad);
        agregarCampo("Producto", producto);
        refrescar();
        limpiar();
    }

    /** Además de la tabla, recarga los clientes y productos que pudieron cambiar en otras pestañas. */
    @Override
    void refrescar() {
        cliente.setModel(new DefaultComboBoxModel<>(control.clientes().toArray(Cliente[]::new)));
        producto.setModel(new DefaultComboBoxModel<>(control.productos().toArray(Producto[]::new)));
        super.refrescar();
    }

    @Override
    protected List<Factura> listar() {
        return control.listar();
    }

    @Override
    protected int codigo(Factura f) {
        return f.numero();
    }

    @Override
    protected void mostrarEnFormulario(Factura f) {
        cliente.setSelectedItem(control.cliente(f.codigoCliente()));
        producto.setSelectedItem(control.producto(f.codigoProducto()));
        cantidad.setValue(f.cantidad());
    }

    @Override
    protected void vaciarFormulario() {
        cantidad.setValue(1);
    }

    @Override
    protected Factura guardar(int numero) {
        return control.guardar(numero, (Cliente) cliente.getSelectedItem(), (Producto) producto.getSelectedItem(),
                (Integer) cantidad.getValue());
    }

    @Override
    protected void eliminar(int numero) {
        control.eliminar(numero);
    }

    @Override
    protected String descripcion(Factura f) {
        return "la factura " + f.numero();
    }
}
