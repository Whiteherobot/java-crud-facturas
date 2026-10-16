package crud.controlador;

import crud.modelo.Producto;
import crud.servicio.Tienda;

import java.math.BigDecimal;
import java.util.List;

public class ProductoControl {

    private final Tienda tienda;

    public ProductoControl(Tienda tienda) {
        this.tienda = tienda;
    }

    /** Registra un producto nuevo si el código es 0; si no, modifica el existente. */
    public Producto guardar(int codigo, String nombre, String precio, int stock) {
        Producto datos = new Producto(codigo, nombre, leerPrecio(precio), stock);
        return codigo == 0 ? tienda.registrarProducto(datos) : tienda.modificarProducto(datos);
    }

    public Producto eliminar(int codigo) {
        return tienda.eliminarProducto(codigo);
    }

    public List<Producto> listar() {
        return tienda.productos();
    }

    private static BigDecimal leerPrecio(String texto) {
        try {
            return new BigDecimal(texto.strip().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio no es un número válido");
        }
    }
}
