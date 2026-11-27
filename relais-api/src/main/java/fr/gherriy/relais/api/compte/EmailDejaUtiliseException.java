package fr.gherriy.relais.api.compte;

public class EmailDejaUtiliseException extends RuntimeException {
    public EmailDejaUtiliseException() {
        super("Un compte existe déjà avec cet email" );
    }
    public EmailDejaUtiliseException(Throwable cause) {
        super("Un compte existe déjà avec cet email", cause);
    }
    
}
