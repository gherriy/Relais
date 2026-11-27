package fr.gherriy.relais.api.commun;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import fr.gherriy.relais.api.compte.EmailDejaUtiliseException;

@RestControllerAdvice
public class GestionnaireErreurs extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmailDejaUtiliseException.class)
    ProblemDetail emailDejaUtilise(EmailDejaUtiliseException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        probleme.setTitle("Email déjà utilisé");
        return probleme;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(erreur -> erreurs.putIfAbsent(erreur.getField(), erreur.getDefaultMessage()));

        ProblemDetail probleme = exception.getBody();
        probleme.setDetail("Un ou plusieurs champs sont invalides");
        probleme.setProperty("erreurs", erreurs);
        return handleExceptionInternal(exception, probleme, headers, status, request);
    }
}
