package crud.controlador;

import crud.modelo.Cliente;
import crud.servicio.Tienda;

import java.util.List;

public class ClienteControl {

    private final Tienda tienda;

    public ClienteControl(Tienda tienda) {
        this.tienda = tienda;
    }

    /** Registra un cliente nuevo si el código es 0; si no, modifica el existente. */
    public Cliente guardar(int codigo, String nombres, String apellidos, String telefono, String correo) {
        Cliente datos = new Cliente(codigo, nombres, apellidos, telefono, correo);
        return codigo == 0 ? tienda.registrarCliente(datos) : tienda.modificarCliente(datos);
    }

    public Cliente eliminar(int codigo) {
        return tienda.eliminarCliente(codigo);
    }

    public List<Cliente> listar() {
        return tienda.clientes();
    }
}
