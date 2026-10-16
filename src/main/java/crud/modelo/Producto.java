package crud.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Producto del inventario. El precio no incluye IVA.
 */
public record Producto(int codigo, String nombre, BigDecimal precio, int stock) {

    public Producto {
        nombre = Validar.requerido(nombre, "Ingrese el nombre del producto");
        if (precio == null || precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        precio = precio.setScale(2, RoundingMode.HALF_UP);
    }

    public Producto conCodigo(int nuevoCodigo) {
        return new Producto(nuevoCodigo, nombre, precio, stock);
    }

    public Producto conStock(int nuevoStock) {
        return new Producto(codigo, nombre, precio, nuevoStock);
    }

    @Override
    public String toString() {
        return codigo + " · " + nombre + " · $" + precio;
    }
}
