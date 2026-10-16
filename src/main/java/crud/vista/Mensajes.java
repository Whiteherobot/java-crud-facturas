package crud.vista;

/**
 * Cómo la vista se comunica con el usuario. En la ventana son diálogos; en las
 * pruebas se reemplaza para no abrir ventanas.
 */
interface Mensajes {

    void error(String mensaje);

    boolean confirmar(String pregunta);
}
