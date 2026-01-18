package org.raflab.studsluzbadesktopclient.app;

import javafx.application.Platform;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.net.ConnectException;

@Component
public class GlobalExceptionHandler {

    public static boolean isServerUnavailable(Throwable error) {
        if (error instanceof WebClientRequestException) {
            Throwable cause = error.getCause();
            return cause instanceof ConnectException;
        }
        return false;
    }

    public static void handleServerError(Throwable error) {
        Platform.runLater(() -> {
            if (isServerUnavailable(error)) {
                AlertHelper.showError(
                        "Server nije dostupan",
                        "Molimo Vas proverite da li je server aktivan.\n\n"
                );
            } else {
                AlertHelper.showException("Greska u komunikaciji", (Exception) error);
            }
        });
    }

    public static <T> Mono<T> wrapWithErrorHandling(Mono<T> operation) {
        return operation.onErrorResume(error -> {
            handleServerError(error);
            return Mono.empty();
        });
    }

    public static <T> Flux<T> wrapFluxWithErrorHandling(Flux<T> operation) {
        return operation.onErrorResume(error -> {
            handleServerError(error);
            return Flux.empty();
        });
    }
}