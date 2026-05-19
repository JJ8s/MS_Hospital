package hospital.ms_auth.exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String mensaje) {
        super(mensaje);
    }
}
