package com.tuckersoft.branchengine.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class RealityMailService {
    private final JavaMailSender mailSender;
    private final String sender;

    public RealityMailService(JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String sender) {
        this.mailSender = mailSender;
        this.sender = sender;
    }

    public String subject(RealityReport report) {
        return "[TUCKERSOFT] " + report.branchType() + " en " + report.playerTag()
                + " | Impacto " + report.impactLevel();
    }

    // Called by the future AFTER_COMMIT listener, never by DecisionService.
    // Exceptions intentionally propagate so the listener records FAILED.
    public void send(RealityReport report, boolean simulateMailFailure) {
        if (simulateMailFailure) {
            throw new MailSendException("Fallo SMTP simulado: MAIL_FAILURE");
        }
        var message = new SimpleMailMessage();
        if (sender != null && !sender.isBlank()) {
            message.setFrom(sender);
        }
        message.setTo(report.recipientEmail());
        message.setSubject(subject(report));
        message.setText(body(report));
        mailSender.send(message);
    }

    private String body(RealityReport r) {
        return """
                Hola %s,

                Una partida de prueba acaba de ramificarse.

                Decision ID      : #%s
                Jugador          : %s
                Rama             : %s
                Impacto          : %s
                Departamento     : %s
                Consecuencia     : %s
                Nodo origen      : %s
                Nodo destino     : %s
                Estado partida   : %s
                Lucidez          : %s/100
                Nivel de control : %s/100
                Final            : %s
                Registrada       : %s

                Decisión original del jugador:
                "%s"

                — Tuckersoft Branch Engine, 1984
                """.formatted(r.displayName(), r.decisionId(), r.playerTag(),
                r.branchType(), r.impactLevel(), r.handlerUnit(), r.outcomeCode(),
                r.sourceNodeCode(), r.resolvedNodeCode(), r.playthroughStatus(),
                r.lucidity(), r.controlLevel(), r.endingCode() == null ? "-" : r.endingCode(),
                r.createdAt(), r.rawInput());
    }
}
