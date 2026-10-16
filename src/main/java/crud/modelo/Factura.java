package crud.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Factura de un producto para un cliente. Guarda el precio del momento de la
 * venta, así no cambia si después se modifica el precio del producto.
 */
public record Factura(int numero, int codigoCliente, int codigoProducto, int cantidad,
                      BigDecimal precioUnitario, LocalDate fecha) {

    /** Tarifa general de IVA en Ecuador desde abril de 2024. */
    public static final BigDecimal IVA = new BigDecimal("0.15");

    public Factura {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }

    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal iva() {
        return subtotal().multiply(IVA).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal total() {
        return subtotal().add(iva());
    }
}
