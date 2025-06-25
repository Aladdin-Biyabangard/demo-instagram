package az.aladdin.blogplatform.model.enums;

import lombok.Getter;

@Getter
public enum EmailTemplate {

    VERIFICATION("Here's the 6-digit verification code you requested\n",
            """
                    Hi {userName},
                    Use the code below to finish your sign up.
                    {code}
                    This code expires in 15 minutes.
                    Ignore this email if you have not made the request."""
    ),

    PASSWORD_RESET("You have requested to reset your password\n",
            """
                    Hi {userName},
                    Use the link below to reset your password:
                    {link}
                    Ignore this email if you do remember your password, or you have not made the request."""
    );

    private final String subject;
    private final String body;


    EmailTemplate(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

}