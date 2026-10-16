package crud.controlador;

import crud.modelo.Cliente;
import crud.modelo.Factura;
import crud.modelo.Producto;
import crud.servicio.Tienda;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class FacturaControl {

    private final Tienda tienda;
    private final Clock reloj;

    public FacturaControl(Tienda tienda, Clock reloj) {
        this.tienda = tienda;
        this.reloj = reloj;
    }

    /** Crea la factura si el número es 0; si no, modifica la existente. */
    public Factura guardar(int numero, Cliente cliente, Producto producto, int cantidad) {
        if (cliente == null || producto == null) {
            throw new IllegalArgumentException("Seleccione un cliente y un producto");
        }
        return numero == 0
                ? tienda.crearFactura(cliente.codigo(), producto.codigo(), cantidad, LocalDate.now(reloj))
                : tienda.modificarFactura(numero, cliente.codigo(), producto.codigo(), cantidad);
    }

    public Factura eliminar(int numero) {
        return tienda.eliminarFactura(numero);
    }

    public List<Factura> listar() {
        return tienda.facturas();
    }

    public List<Cliente> clientes() {
        return tienda.clientes();
    }

    public List<Producto> productos() {
        return tienda.productos();
    }

    public Cliente cliente(int codigo) {
        return tienda.cliente(codigo);
    }

    public Producto producto(int codigo) {
        return tienda.producto(codigo);
    }
}
