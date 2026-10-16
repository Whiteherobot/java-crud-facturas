package crud.vista;

import crud.controlador.ClienteControl;
import crud.modelo.Cliente;

import javax.swing.JTextField;
import java.util.List;

class PanelClientes extends PanelCrud<Cliente> {

    final JTextField nombres = new JTextField(14);
    final JTextField apellidos = new JTextField(14);
    final JTextField telefono = new JTextField(10);
    final JTextField correo = new JTextField(14);

    private final ClienteControl control;

    PanelClientes(ClienteControl control, Mensajes mensajes) {
        super("Cliente", new ModeloTabla<Cliente>()
                .numero("Código", Cliente::codigo)
                .columna("Nombres", Cliente::nombres)
                .columna("Apellidos", Cliente::apellidos)
                .columna("Teléfono", Cliente::telefono)
                .columna("Correo", Cliente::correo), mensajes);
        this.control = control;
        agregarCampo("Nombres *", nombres);
        agregarCampo("Teléfono", telefono);
        agregarCampo("Apellidos *", apellidos);
        agregarCampo("Correo", correo);
        refrescar();
        limpiar();
    }

    @Override
    protected List<Cliente> listar() {
        return control.listar();
    }

    @Override
    protected int codigo(Cliente c) {
        return c.codigo();
    }

    @Override
    protected void mostrarEnFormulario(Cliente c) {
        nombres.setText(c.nombres());
        apellidos.setText(c.apellidos());
        telefono.setText(c.telefono());
        correo.setText(c.correo());
    }

    @Override
    protected void vaciarFormulario() {
        for (JTextField campo : new JTextField[] {nombres, apellidos, telefono, correo}) {
            campo.setText("");
        }
    }

    @Override
    protected Cliente guardar(int codigo) {
        return control.guardar(codigo, nombres.getText(), apellidos.getText(), telefono.getText(), correo.getText());
    }

    @Override
    protected void eliminar(int codigo) {
        control.eliminar(codigo);
    }

    @Override
    protected String descripcion(Cliente c) {
        return "el cliente " + c.codigo() + " (" + c.nombreCompleto() + ")";
    }
}
