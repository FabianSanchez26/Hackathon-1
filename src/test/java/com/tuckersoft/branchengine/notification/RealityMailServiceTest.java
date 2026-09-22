package com.tuckersoft.branchengine.notification;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealityMailServiceTest {
    private final JavaMailSender sender = mock(JavaMailSender.class);
    private final RealityMailService service = new RealityMailService(sender, "qa@tuckersoft.test");

    private RealityReport report(String ending) {
        return new RealityReport(42L, "owner@example.test", "Ada Lovelace", "STEFAN-01",
                "OBEDIENCIA", "LEVE", "Mesa de Guion", "ADVANCE_MAIN_PATH",
                "NODE-CEREAL", "NODE-BUS", ending == null ? "ACTIVA" : "FINALIZADA",
                95, 5, ending, Instant.parse("2026-09-24T10:00:00Z"),
                "Stefan acepta la oferta de Mohan.");
    }

    @Test void sendsCompleteReportToOwner() {
        service.send(report(null), false);
        var capture = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(capture.capture());
        var message = capture.getValue();
        assertThat(message.getTo()).containsExactly("owner@example.test");
        assertThat(message.getFrom()).isEqualTo("qa@tuckersoft.test");
        assertThat(message.getSubject()).isEqualTo("[TUCKERSOFT] OBEDIENCIA en STEFAN-01 | Impacto LEVE");
        assertThat(message.getText()).contains("Hola Ada Lovelace", "Decision ID      : #42",
                "Jugador          : STEFAN-01", "Rama             : OBEDIENCIA",
                "Impacto          : LEVE", "Departamento     : Mesa de Guion",
                "Consecuencia     : ADVANCE_MAIN_PATH", "Nodo origen      : NODE-CEREAL",
                "Nodo destino     : NODE-BUS", "Estado partida   : ACTIVA",
                "Lucidez          : 95/100", "Nivel de control : 5/100",
                "Final            : -", "Registrada       : 2026-09-24T10:00:00Z",
                "Stefan acepta la oferta de Mohan.");
    }

    @Test void includesEndingWhenPresent() {
        service.send(report("ENDING_NETFLIX_CUT"), false);
        var capture = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(capture.capture());
        assertThat(capture.getValue().getText()).contains("Final            : ENDING_NETFLIX_CUT");
    }

    @Test void simulatedFailureThrowsWithoutContactingSmtp() {
        assertThatThrownBy(() -> service.send(report(null), true))
                .isInstanceOf(MailSendException.class).hasMessageContaining("MAIL_FAILURE");
        verifyNoInteractions(sender);
    }

    @Test void realFailurePropagatesForListenerToRecord() {
        var failure = new MailSendException("SMTP unavailable");
        doThrow(failure).when(sender).send(any(SimpleMailMessage.class));
        assertThatThrownBy(() -> service.send(report(null), false)).isSameAs(failure);
    }

    @Test void canSendAgainAfterFailure() {
        doThrow(new MailSendException("Temporary failure")).doNothing()
                .when(sender).send(any(SimpleMailMessage.class));
        assertThatThrownBy(() -> service.send(report(null), false)).isInstanceOf(MailSendException.class);
        service.send(report(null), false);
        verify(sender, times(2)).send(any(SimpleMailMessage.class));
    }
}
