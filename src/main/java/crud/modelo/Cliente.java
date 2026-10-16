package crud.modelo;

/**
 * Cliente. El código lo asigna el sistema al registrarlo.
 */
public record Cliente(int codigo, String nombres, String apellidos, String telefono, String correo) {

    public Cliente {
        nombres = Validar.requerido(nombres, "Ingrese los nombres");
        apellidos = Validar.requerido(apellidos, "Ingrese los apellidos");
        telefono = Validar.opcional(telefono);
        correo = Validar.opcional(correo);
        if (!correo.isEmpty() && !correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new IllegalArgumentException("El correo no es válido");
        }
    }

    public Cliente conCodigo(int nuevoCodigo) {
        return new Cliente(nuevoCodigo, nombres, apellidos, telefono, correo);
    }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public String toString() {
        return codigo + " · " + nombreCompleto();
    }
}
