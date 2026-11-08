package solicitudes.dominio;

public class Estudiante {
    private final String nombre;
    private final String email;
    private final String legajo;

    public Estudiante(String nombre, String email, String legajo) {
        this.nombre = nombre;
        this.email = email;
        this.legajo = legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getLegajo() {
        return legajo;
    }
}
