package solicitudes.dominio;

/** Solicitud académica. Protege sus propias reglas: transiciones de estado y asignación. */
public class Solicitud {
    private final int id;
    private final Estudiante estudiante;
    private final Tipo tipo;
    private final Prioridad prioridad;
    private final String descripcion;
    private Estado estado;
    private Responsable responsable;

    /** Solicitud nueva: nace PENDIENTE y con el responsable por defecto de su tipo. */
    public Solicitud(int id, Estudiante estudiante, Tipo tipo, Prioridad prioridad, String descripcion) {
        this(id, estudiante, tipo, prioridad, descripcion, Estado.PENDIENTE, tipo.responsableInicial());
    }

    /** Reconstruye una solicitud ya existente (por ejemplo, al leerla del almacenamiento). */
    public Solicitud(int id, Estudiante estudiante, Tipo tipo, Prioridad prioridad, String descripcion,
                     Estado estado, Responsable responsable) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.estado = estado;
        this.responsable = responsable;
    }

    public void cambiarEstado(Estado nuevo) {
        if (!estado.puedePasarA(nuevo)) {
            throw new ReglaDeNegocioException("transición de estado no permitida");
        }
        estado = nuevo;
    }

    public void asignarA(Responsable nuevo) {
        if (estado.esFinal()) {
            throw new ReglaDeNegocioException("la solicitud está cerrada");
        }
        responsable = nuevo;
    }

    public int plazoDias() {
        return tipo.plazoDias(prioridad);
    }

    public int getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Estado getEstado() {
        return estado;
    }

    public Responsable getResponsable() {
        return responsable;
    }
}
