package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.enums.EmailTemplate;

import java.util.Map;

public interface EmailService {

    void sendEmail(String receiver, EmailTemplate template, Map<String, String> placeholders);


}
