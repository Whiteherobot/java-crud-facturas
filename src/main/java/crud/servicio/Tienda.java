package crud.servicio;

import crud.modelo.Cliente;
import crud.modelo.Factura;
import crud.modelo.Producto;

import java.time.LocalDate;
import java.util.List;

/**
 * Reglas que involucran a más de una entidad: el stock de los productos al
 * crear, modificar o eliminar facturas, y la integridad entre registros (no se
 * elimina un cliente o un producto que aparece en una factura).
 */
public class Tienda {

    private final Repositorio<Cliente> clientes = new Repositorio<>("un cliente", Cliente::codigo);
    private final Repositorio<Producto> productos = new Repositorio<>("un producto", Producto::codigo);
    private final Repositorio<Factura> facturas = new Repositorio<>("una factura", Factura::numero);

    // Clientes

    public Cliente registrarCliente(Cliente datos) {
        return clientes.crear(datos.conCodigo(clientes.siguienteCodigo()));
    }

    public Cliente modificarCliente(Cliente cliente) {
        return clientes.actualizar(cliente);
    }

    public Cliente eliminarCliente(int codigo) {
        long enUso = facturas.listar().stream().filter(f -> f.codigoCliente() == codigo).count();
        if (enUso > 0) {
            throw new IllegalStateException("No se puede eliminar: el cliente tiene " + enUso + " factura(s).");
        }
        return clientes.eliminar(codigo);
    }

    public List<Cliente> clientes() {
        return clientes.listar();
    }

    // Productos

    public Producto registrarProducto(Producto datos) {
        return productos.crear(datos.conCodigo(productos.siguienteCodigo()));
    }

    public Producto modificarProducto(Producto producto) {
        return productos.actualizar(producto);
    }

    public Producto eliminarProducto(int codigo) {
        long enUso = facturas.listar().stream().filter(f -> f.codigoProducto() == codigo).count();
        if (enUso > 0) {
            throw new IllegalStateException("No se puede eliminar: el producto aparece en " + enUso + " factura(s).");
        }
        return productos.eliminar(codigo);
    }

    public List<Producto> productos() {
        return productos.listar();
    }

    // Facturas

    public Factura crearFactura(int codigoCliente, int codigoProducto, int cantidad, LocalDate fecha) {
        clientes.obtener(codigoCliente);
        Producto producto = productos.obtener(codigoProducto);
        exigirStock(producto, cantidad);
        Factura factura = facturas.crear(new Factura(facturas.siguienteCodigo(), codigoCliente, codigoProducto,
                cantidad, producto.precio(), fecha));
        productos.actualizar(producto.conStock(producto.stock() - cantidad));
        return factura;
    }

    /**
     * Cambia el cliente, el producto o la cantidad. El stock se ajusta por la
     * diferencia: primero se devuelve lo de la factura anterior y luego se toma
     * lo de la nueva. Si no alcanza, no se modifica nada.
     */
    public Factura modificarFactura(int numero, int codigoCliente, int codigoProducto, int cantidad) {
        Factura anterior = facturas.obtener(numero);
        clientes.obtener(codigoCliente);
        Producto nuevo = productos.obtener(codigoProducto);

        boolean mismoProducto = codigoProducto == anterior.codigoProducto();
        int disponible = nuevo.stock() + (mismoProducto ? anterior.cantidad() : 0);
        if (cantidad > disponible) {
            throw new IllegalStateException("Stock insuficiente de " + nuevo.nombre() + ": hay " + disponible + ".");
        }
        // Un producto distinto se factura al precio actual; el mismo conserva el precio original.
        Factura modificada = new Factura(numero, codigoCliente, codigoProducto, cantidad,
                mismoProducto ? anterior.precioUnitario() : nuevo.precio(), anterior.fecha());

        devolverStock(anterior);
        Producto actualizado = productos.obtener(codigoProducto);
        productos.actualizar(actualizado.conStock(actualizado.stock() - cantidad));
        return facturas.actualizar(modificada);
    }

    /** Elimina la factura y devuelve las unidades al inventario. */
    public Factura eliminarFactura(int numero) {
        Factura factura = facturas.eliminar(numero);
        devolverStock(factura);
        return factura;
    }

    public List<Factura> facturas() {
        return facturas.listar();
    }

    public Cliente cliente(int codigo) {
        return clientes.obtener(codigo);
    }

    public Producto producto(int codigo) {
        return productos.obtener(codigo);
    }

    private void devolverStock(Factura factura) {
        productos.buscar(factura.codigoProducto())
                .ifPresent(p -> productos.actualizar(p.conStock(p.stock() + factura.cantidad())));
    }

    private static void exigirStock(Producto producto, int cantidad) {
        if (cantidad > producto.stock()) {
            throw new IllegalStateException("Stock insuficiente de " + producto.nombre() + ": hay " + producto.stock() + ".");
        }
    }
}
