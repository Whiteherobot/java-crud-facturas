package crud.vista;

import crud.controlador.ClienteControl;
import crud.controlador.FacturaControl;
import crud.controlador.ProductoControl;

/**
 * Registros para probar la aplicación sin cargar todo a mano.
 */
final class DatosEjemplo {

    private DatosEjemplo() {
    }

    static void cargar(ClienteControl clientes, ProductoControl productos, FacturaControl facturas) {
        var ana = clientes.guardar(0, "Ana Lucía", "Torres Vega", "0991234567", "ana.torres@gmail.com");
        var carlos = clientes.guardar(0, "Carlos", "Mendoza Ríos", "0987654321", "");
        clientes.guardar(0, "María José", "Ordóñez", "", "mjordonez@outlook.com");

        var teclado = productos.guardar(0, "Teclado USB", "18.50", 25);
        var mouse = productos.guardar(0, "Mouse inalámbrico", "12.90", 40);
        productos.guardar(0, "Monitor 24\"", "149.00", 6);

        facturas.guardar(0, ana, teclado, 2);
        facturas.guardar(0, carlos, mouse, 1);
    }
}
