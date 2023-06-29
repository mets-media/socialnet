package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import socialnet.api.request.KafkaMessageRq;
import socialnet.api.request.TgApiRequest;
import socialnet.api.response.CommonRs;
import socialnet.api.response.TgApiRs;
import socialnet.api.response.TgMessagesRs;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.repository.TelegramBotRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.MailSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService {
    private final TelegramBotRepository telegramBotRepository;
    private final PersonRepository personRepository;
    private final MailSender mailSender;
    private final JwtUtils jwtUtils;
    private final KafkaService kafkaService;

    private static final String TG_MAIL_REGISTER_URL = "http://81.177.6.228:8086/api/v1/tg";

    public TgApiRs register(long telegramId, String email, String cmd) {
        boolean isRegister = telegramBotRepository.register(telegramId, email, cmd);

        if (!isRegister) {
            return makeResponse("fail", "Вы уже зарегистрированы", null);
        }

        return makeResponse("ok", null, "Регистрация прошла успешно");
    }

    public TgApiRs execCommand(TgApiRequest request) {
        String command = request.getCommand();

        if (command.equals("/register")) {
            return handleRegisterCommand(request);
        }

        if (command.equals("/login")) {
            return handleLoginCommand(request);
        }

        if (command.equals("/killyourself")) {
            return handleKillyourselfCommand(request);
        }

        if (command.equals("/token")) {
            return handleTokenCommand(request);
        }

        if (command.equals("/messages")) {
            return handleMessagesCommand(request);
        }

        return makeResponse("fail", "Неизвестная команда", null);
    }

    public void notificate(Notification notification) {
        try {
            Person from = personRepository.findById(notification.getEntityId());
            Long tgId = telegramBotRepository.getTelegramIdByPersonId(notification.getPersonId());

            if (tgId == null) {
                return;
            }

            kafkaService.sendMessage(KafkaMessageRq.builder()
                .toTgId(tgId)
                .from(from.getFirstName() + " " + from.getLastName())
                .type(notification.getNotificationType())
                .build()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private TgApiRs handleMessagesCommand(TgApiRequest request) {
        List<TgMessagesRs> messages = telegramBotRepository.getMessages(request.getId(), request.getData());

        String[] opp = request.getData().split(";");
        int offset = Integer.parseInt(opp[0]);
        int perPage = Integer.parseInt(opp[1]);

        CommonRs<?> rs = new CommonRs<>(messages);
        rs.setOffset(offset);
        rs.setPerPage(perPage);
        rs.setTotal(telegramBotRepository.getCountMessages(request.getId()));

        return makeResponse("ok", null, new JSONObject(rs).toString());
    }

    private TgApiRs handleTokenCommand(TgApiRequest request) {
        Person person = personRepository.findByTelegramId(request.getId());

        if (person == null) {
            return makeResponse("fail", "Error", null);
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", jwtUtils.generateJwtToken(person.getEmail()));
        map.put("id", String.valueOf(request.getId()));
        JSONObject jo = new JSONObject(map);

        return makeResponse("ok", null, jo.toString());
    }

    private TgApiRs handleKillyourselfCommand(TgApiRequest request) {
        boolean isUnregistered = telegramBotRepository.unregister(request.getId());

        if (isUnregistered) {
            return makeResponse("ok", null, "Бывай!");
        }

        return makeResponse("fail", "Какая-то фигня, так что сиди пока и не рыпайся.", null);
    }

    private TgApiRs handleRegisterCommand(TgApiRequest request) {
        try {
            mailSender.send(
                request.getData(),
                "Подтверждение регистрации",
                "Для подтверждения регистрации перейдите по ссылке: <a href='" + TG_MAIL_REGISTER_URL + "?id=" + request.getId()
                    + "&email=" + request.getData()
                    + "&cmd=" + request.getCommand().substring(1)
                    + "'>подтвердить регистрацию</a>"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return makeResponse("ok", null, "Письмо с подтверждением выслано на указанную почту. " +
            "Войдите в почту и перейдите по ссылке, затем нажмите 'Подтвердить регистрацию'");
    }

    private TgApiRs handleLoginCommand(TgApiRequest request) {
        Person person = personRepository.findByTelegramId(request.getId());

        if (person == null) {
            return makeResponse("fail", "Укажите свою почту:", null);
        }

        String token = jwtUtils.generateJwtToken(person.getEmail());
        String fullName = "[" + person.getFirstName() + " " + person.getLastName() + "]";

        Map<String, String> map = new HashMap<>();
        map.put("name", fullName);
        map.put("token", token);
        map.put("id", String.valueOf(request.getId()));
        map.put("userId", person.getId().toString());
        JSONObject jo = new JSONObject(map);

        return makeResponse("ok", null, jo.toString());
    }

    private TgApiRs makeResponse(String status, String error, String data) {
        return TgApiRs.builder().status(status).error(error).data(data).build();
    }
}
