package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import socialnet.security.jwt.JwtUtils;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailSender emailSender;
    private final JwtUtils jwtUtils;
    @Value("${baseUrl}")
    private String baseUrl;
    public void passwordChangeConfirm(String authorization) {
        String email = jwtUtils.getUserEmail(authorization);
        String token = jwtUtils.generateJwtToken(email);
        String message = String.format("<p><a href=\"%schange-password?token=%s\">Confirm change [PASSWORD]!</a></p>", baseUrl, token);
        emailSender.send(email,"Подтверждение изменения пароля.", message);
    }
    public void shiftEmailConfirm(String authorization) {
        String email = jwtUtils.getUserEmail(authorization);
        String token = jwtUtils.generateJwtToken(email);
        String message = String.format("<p><a href=\"%sshift-email?token=%s\">Confirm change [EMAIL]!</a></p>", baseUrl,token);
        emailSender.send(email,"Подтверждение изменения email.", message);
    }
}
