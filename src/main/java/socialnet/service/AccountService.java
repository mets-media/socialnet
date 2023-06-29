package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import socialnet.api.request.RegisterRq;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.RegisterRs;
import socialnet.exception.RegisterException;
import socialnet.model.Captcha;
import socialnet.model.Person;
import socialnet.repository.CaptchaRepository;
import socialnet.repository.PersonRepository;
import socialnet.repository.PersonSettingRepository;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final PersonRepository personRepository;
    private final PersonSettingRepository personSettingRepository;
    private final CaptchaRepository captchaRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterRs getRegisterData(RegisterRq regRequest) {
        validateFields(regRequest);

        personRepository.save(getPerson(regRequest));
        personSettingRepository.insert(regRequest.getEmail());

        RegisterRs registerRs = new RegisterRs();
        ComplexRs complexRs = ComplexRs.builder().message("OK").build();
        registerRs.setData(complexRs);
        registerRs.setEmail(regRequest.getEmail());
        registerRs.setTimestamp(System.currentTimeMillis());

        return registerRs;
    }

    private Person getPerson(RegisterRq regRequest) {
        Person person = new Person();
        person.setEmail(regRequest.getEmail());
        person.setFirstName(regRequest.getFirstName());
        person.setLastName(regRequest.getLastName());
        person.setPassword(passwordEncoder.encode(regRequest.getPasswd1()));
        person.setRegDate(new Timestamp(System.currentTimeMillis()));
        person.setIsApproved(true);
        person.setIsBlocked(false);
        person.setIsDeleted(false);
        person.setTelegramId(null);

        return person;
    }

    @SneakyThrows
    private void validateFields(RegisterRq regRequest) {
        if (!regRequest.getPasswd1().equals(regRequest.getPasswd2())) {
            throw new RegisterException("Password not equals");
        }

        if (personRepository.findByEmail(regRequest.getEmail()) != null) {
            throw new RegisterException("Email already exists");
        }

        Captcha captcha = captchaRepository.findBySecretCode(regRequest.getCodeSecret());

        if (captcha == null) {
            throw new RegisterException("Wrong code");
        }

        if (!regRequest.getCode().equals(captcha.getCode())) {
            throw new RegisterException("Wrong code");
        }
    }
}
