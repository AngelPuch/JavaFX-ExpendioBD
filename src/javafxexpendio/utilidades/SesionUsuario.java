package javafxexpendio.utilidades;

import javafxexpendio.modelo.pojo.Usuario;

public class SesionUsuario {
    private static SesionUsuario instancia;
    private Usuario usuarioLogueado;

    private SesionUsuario() {
    }

    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
    
    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }

    /**
     * Devuelve el nombre del rol del usuario logueado o 'login_checker' si no hay nadie logueado.
     * Esto se usará para buscar las credenciales correctas en el archivo .properties.
     * @return El rol del usuario como String.
     */
    public String getRolUsuario() {
        if (usuarioLogueado != null && usuarioLogueado.getTipoUsuario() != null) {
            return usuarioLogueado.getTipoUsuario(); // ej: "administrador", "empleado"
        }
        // Si no hay sesión, se usa el usuario por defecto para el login
        return "login_checker";
    }
}