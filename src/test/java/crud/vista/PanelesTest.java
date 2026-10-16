package crud.vista;

import crud.controlador.ClienteControl;
import crud.controlador.FacturaControl;
import crud.controlador.ProductoControl;
import crud.servicio.Tienda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanelesTest {

    private final List<String> errores = new ArrayList<>();
    private boolean respuesta = true;
    private final Mensajes mensajes = new Mensajes() {
        @Override
        public void error(String mensaje) {
            errores.add(mensaje);
        }

        @Override
        public boolean confirmar(String pregunta) {
            return respuesta;
        }
    };

    private ClienteControl clientes;
    private ProductoControl productos;
    private FacturaControl facturas;

    @BeforeEach
    void preparar() {
        Tienda tienda = new Tienda();
        clientes = new ClienteControl(tienda);
        productos = new ProductoControl(tienda);
        facturas = new FacturaControl(tienda, Clock.systemDefaultZone());
        DatosEjemplo.cargar(clientes, productos, facturas);
    }

    @Test
    void registrarSeleccionarYModificarUnCliente() {
        PanelClientes panel = new PanelClientes(clientes, mensajes);
        assertEquals("Registro nuevo", panel.modo.getText());
        assertFalse(panel.eliminar.isEnabled());

        panel.nombres.setText("Luis");
        panel.apellidos.setText("Pérez");
        panel.guardar.doClick();
        assertEquals(4, panel.modelo.getRowCount());
        assertTrue(panel.modo.getText().startsWith("Editando el cliente 4"));

        panel.tabla.setRowSelectionInterval(0, 0);
        assertEquals("Ana Lucía", panel.nombres.getText());
        panel.telefono.setText("0999999999");
        panel.guardar.doClick();
        assertEquals("0999999999", clientes.listar().get(0).telefono());
        assertEquals(4, clientes.listar().size());
    }

    @Test
    void eliminarPideConfirmacionYRespetaLasFacturas() {
        PanelClientes panel = new PanelClientes(clientes, mensajes);
        panel.tabla.setRowSelectionInterval(0, 0);   // Ana tiene una factura
        panel.eliminar.doClick();
        assertTrue(errores.get(0).contains("tiene 1 factura"));

        panel.tabla.setRowSelectionInterval(2, 2);   // María José, sin facturas
        respuesta = false;
        panel.eliminar.doClick();
        assertEquals(3, clientes.listar().size());
        respuesta = true;
        panel.eliminar.doClick();
        assertEquals(2, clientes.listar().size());
        assertEquals("Registro nuevo", panel.modo.getText());
    }

    @Test
    void elBuscadorFiltraLaTabla() {
        PanelProductos panel = new PanelProductos(productos, mensajes);
        panel.buscar.setText("mOuSe");
        assertEquals(1, panel.tabla.getRowCount());
        panel.buscar.setText("");
        assertEquals(3, panel.tabla.getRowCount());
    }

    @Test
    void modificarUnaFacturaDesdeElPanel() {
        PanelFacturas panel = new PanelFacturas(facturas, mensajes);
        panel.tabla.setRowSelectionInterval(0, 0);   // 2 teclados de Ana
        panel.cantidad.setValue(5);
        panel.guardar.doClick();
        assertEquals(20, productos.listar().get(0).stock());   // 25 − 5
        panel.cantidad.setValue(100);
        panel.guardar.doClick();
        assertTrue(errores.get(0).startsWith("Stock insuficiente"));
    }
}
