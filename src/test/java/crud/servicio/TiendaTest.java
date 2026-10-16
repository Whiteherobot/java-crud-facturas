package crud.servicio;

import crud.modelo.Cliente;
import crud.modelo.Factura;
import crud.modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TiendaTest {

    private static final LocalDate HOY = LocalDate.of(2022, 6, 22);
    private Tienda tienda;
    private Cliente ana;
    private Producto teclado;
    private Producto mouse;

    @BeforeEach
    void preparar() {
        tienda = new Tienda();
        ana = tienda.registrarCliente(new Cliente(0, "Ana", "Torres", "", ""));
        teclado = tienda.registrarProducto(new Producto(0, "Teclado", new BigDecimal("18.50"), 10));
        mouse = tienda.registrarProducto(new Producto(0, "Mouse", new BigDecimal("12.90"), 5));
    }

    private int stock(Producto p) {
        return tienda.producto(p.codigo()).stock();
    }

    @Test
    void asignaCodigosSinReutilizarlos() {
        assertEquals(1, ana.codigo());
        assertEquals(2, mouse.codigo());
        tienda.eliminarProducto(mouse.codigo());
        assertEquals(3, tienda.registrarProducto(new Producto(0, "Cable", BigDecimal.ONE, 1)).codigo());
    }

    @Test
    void modificarOEliminarUnCodigoInexistenteFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> tienda.modificarCliente(new Cliente(99, "X", "Y", "", "")));
        assertThrows(IllegalArgumentException.class, () -> tienda.eliminarCliente(99));
        assertEquals("Ana", tienda.cliente(1).nombres());
    }

    @Test
    void crearFacturaDescuentaStockYGuardaElPrecio() {
        Factura f = tienda.crearFactura(ana.codigo(), teclado.codigo(), 3, HOY);
        assertEquals(7, stock(teclado));
        tienda.modificarProducto(new Producto(teclado.codigo(), "Teclado", new BigDecimal("25.00"), 7));
        assertEquals(new BigDecimal("18.50"), tienda.facturas().get(0).precioUnitario());
        assertEquals(new BigDecimal("63.83"), f.total());     // 55.50 + 8.325 → 8.33
        assertThrows(IllegalStateException.class, () -> tienda.crearFactura(ana.codigo(), mouse.codigo(), 6, HOY));
    }

    @Test
    void modificarFacturaAjustaElStockPorLaDiferencia() {
        Factura f = tienda.crearFactura(ana.codigo(), teclado.codigo(), 3, HOY);
        tienda.modificarFactura(f.numero(), ana.codigo(), teclado.codigo(), 5);
        assertEquals(5, stock(teclado));
        tienda.modificarFactura(f.numero(), ana.codigo(), teclado.codigo(), 1);
        assertEquals(9, stock(teclado));

        tienda.modificarFactura(f.numero(), ana.codigo(), mouse.codigo(), 2);   // cambia de producto
        assertEquals(10, stock(teclado));
        assertEquals(3, stock(mouse));
        assertEquals(new BigDecimal("12.90"), tienda.facturas().get(0).precioUnitario());
    }

    @Test
    void modificarSinStockNoCambiaNada() {
        Factura f = tienda.crearFactura(ana.codigo(), mouse.codigo(), 2, HOY);   // queda 3
        assertThrows(IllegalStateException.class,
                () -> tienda.modificarFactura(f.numero(), ana.codigo(), mouse.codigo(), 6));
        assertEquals(3, stock(mouse));
        assertEquals(2, tienda.facturas().get(0).cantidad());
        tienda.modificarFactura(f.numero(), ana.codigo(), mouse.codigo(), 5);   // 3 + 2 devueltas
        assertEquals(0, stock(mouse));
    }

    @Test
    void eliminarFacturaDevuelveElStock() {
        Factura f = tienda.crearFactura(ana.codigo(), teclado.codigo(), 4, HOY);
        tienda.eliminarFactura(f.numero());
        assertEquals(10, stock(teclado));
        assertTrue(tienda.facturas().isEmpty());
    }

    @Test
    void noEliminaClientesNiProductosConFacturas() {
        tienda.crearFactura(ana.codigo(), teclado.codigo(), 1, HOY);
        assertThrows(IllegalStateException.class, () -> tienda.eliminarCliente(ana.codigo()));
        assertThrows(IllegalStateException.class, () -> tienda.eliminarProducto(teclado.codigo()));
        tienda.eliminarProducto(mouse.codigo());
        assertEquals(1, tienda.productos().size());
    }

    @Test
    void validaLosDatos() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(0, "", "Torres", "", ""));
        assertThrows(IllegalArgumentException.class, () -> new Cliente(0, "Ana", "Torres", "", "ana@"));
        assertThrows(IllegalArgumentException.class, () -> new Producto(0, "X", BigDecimal.ZERO, 1));
        assertThrows(IllegalArgumentException.class, () -> new Producto(0, "X", BigDecimal.ONE, -1));
    }
}
