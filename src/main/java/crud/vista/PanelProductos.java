package crud.vista;

import crud.controlador.ProductoControl;
import crud.modelo.Producto;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.util.List;

class PanelProductos extends PanelCrud<Producto> {

    final JTextField nombre = new JTextField(16);
    final JTextField precio = new JTextField(8);
    final JSpinner stock = new JSpinner(new SpinnerNumberModel(0, 0, 100_000, 1));

    private final ProductoControl control;

    PanelProductos(ProductoControl control, Mensajes mensajes) {
        super("Producto", new ModeloTabla<Producto>()
                .numero("Código", Producto::codigo)
                .columna("Nombre", Producto::nombre)
                .numero("Precio (sin IVA)", p -> "$" + p.precio())
                .numero("Stock", Producto::stock), mensajes);
        this.control = control;
        agregarCampo("Nombre *", nombre);
        agregarCampo("Precio *", precio);
        agregarCampo("Stock", stock);
        refrescar();
        limpiar();
    }

    @Override
    protected List<Producto> listar() {
        return control.listar();
    }

    @Override
    protected int codigo(Producto p) {
        return p.codigo();
    }

    @Override
    protected void mostrarEnFormulario(Producto p) {
        nombre.setText(p.nombre());
        precio.setText(p.precio().toPlainString());
        stock.setValue(p.stock());
    }

    @Override
    protected void vaciarFormulario() {
        nombre.setText("");
        precio.setText("");
        stock.setValue(0);
    }

    @Override
    protected Producto guardar(int codigo) {
        return control.guardar(codigo, nombre.getText(), precio.getText(), (Integer) stock.getValue());
    }

    @Override
    protected void eliminar(int codigo) {
        control.eliminar(codigo);
    }

    @Override
    protected String descripcion(Producto p) {
        return "el producto " + p.codigo() + " (" + p.nombre() + ")";
    }
}
