package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.model.enums.EmailTemplate;
import az.aladdin.blogplatform.services.abstraction.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Transactional
    public void sendEmail(String receiver, EmailTemplate template, Map<String, String> placeholders) {
        log.info("Preparing to send email to {}", receiver);
        try {
            String subject = parseSubject(template, placeholders);
            String body = parseBody(template, placeholders);

            log.debug("Email subject parsed: {}", subject);
            log.debug("Email body parsed (first 100 chars): {}", body.length() > 100 ? body.substring(0, 100) + "..." : body);

            MimeMessage message = prepareMessage(receiver, subject, body);
            mailSender.send(message);

            log.info("Email successfully sent to {}", receiver);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", receiver, e.getMessage(), e);
        }
    }

    protected MimeMessage prepareMessage(String to, String subject, String body) throws MessagingException {
        log.debug("Preparing MimeMessage for recipient: {}", to);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        log.debug("MimeMessage prepared successfully for recipient: {}", to);
        return message;
    }

    protected String parseSubject(EmailTemplate template, Map<String, String> placeholders) {
        log.debug("Parsing email subject template");
        return processPlaceholders(template.getSubject(), placeholders);
    }

    protected String parseBody(EmailTemplate template, Map<String, String> placeholders) {
        log.debug("Parsing email body template");
        return processPlaceholders(template.getBody(), placeholders);
    }

    public static String processPlaceholders(String text, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            text = text.replace(placeholder, entry.getValue());
        }
        return text;
    }
}
