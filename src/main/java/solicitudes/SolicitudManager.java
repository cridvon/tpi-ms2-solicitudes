package solicitudes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SolicitudManager {
    public ArrayList<Solicitud> l = new ArrayList<Solicitud>();
    public ArrayList<Responsable> resps = new ArrayList<Responsable>();
    int sig = 1;

    public SolicitudManager() {
        resps.add(new Responsable("Bedelía"));
        resps.add(new Responsable("Dirección de Carrera"));
        resps.add(new Responsable("Secretaría Académica"));
        cargar();
    }

    // devuelve el id (> 0) o un codigo de error (< 0)
    // -1 nombre, -2 email, -3 tipo, -4 prioridad, -5 descripcion
    public int registrar(String nombre, String email, String legajo, String tipo, int prioridad, String descripcion) {
        if (nombre == null || nombre.trim().equals("")) {
            return -1;
        }
        if (email == null || !email.contains("@") || !email.contains(".") || email.indexOf("@") == 0) {
            return -2;
        }
        if (!(tipo.equals("CERTIFICADO") || tipo.equals("EQUIVALENCIA") || tipo.equals("BAJA") || tipo.equals("CONSULTA"))) {
            return -3;
        }
        if (prioridad < 1 || prioridad > 3) {
            return -4;
        }
        if (descripcion == null || descripcion.trim().equals("")) {
            return -5;
        }
        Solicitud s = new Solicitud();
        s.id = sig;
        sig++;
        s.est = new Estudiante();
        s.est.nombre = nombre;
        s.est.email = email;
        s.est.legajo = legajo;
        s.tipo = tipo;
        s.prioridad = prioridad;
        s.descripcion = descripcion;
        s.estado = "PENDIENTE";
        if (tipo.equals("CERTIFICADO")) {
            s.responsable = "Bedelía";
        } else if (tipo.equals("EQUIVALENCIA")) {
            s.responsable = "Dirección de Carrera";
        } else if (tipo.equals("BAJA")) {
            s.responsable = "Secretaría Académica";
        } else {
            s.responsable = "SIN ASIGNAR";
        }
        l.add(s);
        guardar();
        return s.id;
    }

    // 0 ok, -1 no existe, -2 estado invalido, -3 transicion no permitida
    public int cambiarEstado(int id, String nuevo) {
        Solicitud s = null;
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).id == id) {
                s = l.get(i);
            }
        }
        if (s == null) {
            return -1;
        }
        if (!(nuevo.equals("PENDIENTE") || nuevo.equals("EN_PROCESO") || nuevo.equals("RESUELTA") || nuevo.equals("RECHAZADA"))) {
            return -2;
        }
        if (s.estado.equals("PENDIENTE")) {
            if (!(nuevo.equals("EN_PROCESO") || nuevo.equals("RECHAZADA"))) {
                return -3;
            }
        } else if (s.estado.equals("EN_PROCESO")) {
            if (!(nuevo.equals("RESUELTA") || nuevo.equals("RECHAZADA"))) {
                return -3;
            }
        } else {
            return -3;
        }
        s.estado = nuevo;
        guardar();
        return 0;
    }

    // 0 ok, -1 no existe, -2 responsable invalido, -3 solicitud cerrada
    public int asignarResponsable(int id, String r) {
        Solicitud s = null;
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).id == id) {
                s = l.get(i);
            }
        }
        if (s == null) {
            return -1;
        }
        boolean ok = false;
        for (int i = 0; i < resps.size(); i++) {
            if (resps.get(i).nombre.equals(r)) {
                ok = true;
            }
        }
        if (!ok) {
            return -2;
        }
        if (s.estado.equals("RESUELTA") || s.estado.equals("RECHAZADA")) {
            return -3;
        }
        s.responsable = r;
        guardar();
        return 0;
    }

    public ArrayList<Solicitud> buscarPorEstado(String e) {
        ArrayList<Solicitud> x = new ArrayList<Solicitud>();
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).estado.equals(e)) {
                x.add(l.get(i));
            }
        }
        return x;
    }

    public ArrayList<Solicitud> buscarPorTipo(String t) {
        ArrayList<Solicitud> x = new ArrayList<Solicitud>();
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).tipo.equals(t)) {
                x.add(l.get(i));
            }
        }
        return x;
    }

    public ArrayList<Solicitud> buscarPorResponsable(String r) {
        ArrayList<Solicitud> x = new ArrayList<Solicitud>();
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).responsable.equals(r)) {
                x.add(l.get(i));
            }
        }
        return x;
    }

    public ArrayList<Solicitud> buscarPorPrioridad(int p) {
        ArrayList<Solicitud> x = new ArrayList<Solicitud>();
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).prioridad == p) {
                x.add(l.get(i));
            }
        }
        return x;
    }

    public String reporte() {
        String t = "";
        t = t + "=== REPORTE DE SOLICITUDES ===\n";
        t = t + "Total: " + l.size() + "\n";
        int p = 0, e = 0, r = 0, x = 0;
        for (int i = 0; i < l.size(); i++) {
            Solicitud s = l.get(i);
            String pr;
            if (s.prioridad == 1) {
                pr = "ALTA";
            } else if (s.prioridad == 2) {
                pr = "MEDIA";
            } else {
                pr = "BAJA";
            }
            int plazo;
            if (s.tipo.equals("CERTIFICADO")) {
                if (s.prioridad == 1) {
                    plazo = 3;
                } else if (s.prioridad == 2) {
                    plazo = 5;
                } else {
                    plazo = 10;
                }
            } else if (s.tipo.equals("EQUIVALENCIA")) {
                if (s.prioridad == 1) {
                    plazo = 15;
                } else if (s.prioridad == 2) {
                    plazo = 20;
                } else {
                    plazo = 30;
                }
            } else if (s.tipo.equals("BAJA")) {
                if (s.prioridad == 1) {
                    plazo = 5;
                } else if (s.prioridad == 2) {
                    plazo = 7;
                } else {
                    plazo = 10;
                }
            } else {
                if (s.prioridad == 1) {
                    plazo = 2;
                } else if (s.prioridad == 2) {
                    plazo = 5;
                } else {
                    plazo = 7;
                }
            }
            t = t + "[" + s.id + "] " + s.tipo + " | Prioridad: " + pr + " | Estado: " + s.estado + " | Resp: " + s.responsable + " | Plazo: " + plazo + " días\n";
            t = t + "    Estudiante: " + s.est.nombre + " (" + s.est.email + ") legajo " + s.est.legajo + "\n";
            t = t + "    Detalle: " + s.descripcion + "\n";
            if (s.estado.equals("PENDIENTE")) {
                p++;
            } else if (s.estado.equals("EN_PROCESO")) {
                e++;
            } else if (s.estado.equals("RESUELTA")) {
                r++;
            } else {
                x++;
            }
        }
        t = t + "--- Resumen por estado ---\n";
        t = t + "PENDIENTE: " + p + "\n";
        t = t + "EN_PROCESO: " + e + "\n";
        t = t + "RESUELTA: " + r + "\n";
        t = t + "RECHAZADA: " + x + "\n";
        return t;
    }

    void guardar() {
        try {
            PrintWriter w = new PrintWriter("solicitudes.csv");
            for (int i = 0; i < l.size(); i++) {
                Solicitud s = l.get(i);
                w.println(s.id + ";" + s.est.nombre + ";" + s.est.email + ";" + s.est.legajo + ";" + s.tipo + ";" + s.prioridad + ";" + s.estado + ";" + s.responsable + ";" + s.descripcion);
            }
            w.close();
        } catch (Exception e) {
        }
    }

    void cargar() {
        File f = new File("solicitudes.csv");
        if (!f.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea = br.readLine();
            while (linea != null) {
                String[] c = linea.split(";");
                Solicitud s = new Solicitud();
                s.id = Integer.parseInt(c[0]);
                s.est = new Estudiante();
                s.est.nombre = c[1];
                s.est.email = c[2];
                s.est.legajo = c[3];
                s.tipo = c[4];
                s.prioridad = Integer.parseInt(c[5]);
                s.estado = c[6];
                s.responsable = c[7];
                s.descripcion = c[8];
                l.add(s);
                if (s.id >= sig) {
                    sig = s.id + 1;
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error al cargar");
        }
    }
}
