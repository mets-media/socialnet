package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socialnet.api.request.DialogUserShortListDto;
import socialnet.api.response.*;
import socialnet.mappers.DialogMapper;
import socialnet.mappers.MessageMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.Dialog;
import socialnet.model.Message;
import socialnet.model.Person;
import socialnet.model.enums.MessageReadStatus;
import socialnet.repository.DialogsRepository;
import socialnet.repository.MessageRepository;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DialogsService {

    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final DialogsRepository dialogsRepository;
    private final MessageRepository messageRepository;

    public CommonRs<List<DialogRs>> getDialogs(String token) {
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.findByEmail(userEmail);
        List<Dialog> ownDialogs = dialogsRepository.findByAuthorId(person.getId());
        List<Dialog> companionDialogs = dialogsRepository.findByRecipientId(person.getId());
        List<Dialog> dialogsModelAll = new ArrayList<>();
        dialogsModelAll.addAll(ownDialogs);
        dialogsModelAll.addAll(companionDialogs);
        List<DialogRs> dialogList = new ArrayList<>();

        for (Dialog dialog : dialogsModelAll) {
            DialogRs dialogRs = DialogMapper.INSTANCE.toDTO(dialog);
            Message lastMessage = messageRepository.findById(dialog.getLastMessageId());

            if (lastMessage == null) {
                lastMessage = Message.builder()
                    .dialogId(dialog.getId())
                    .authorId(dialog.getFirstPersonId())
                    .recipientId(dialog.getSecondPersonId())
                    .readStatus("READ")
                    .messageText("Напишите своё первое сообщение")
                    .build();
            }

            MessageRs messageRs = MessageMapper.INSTANCE.toDTO(lastMessage);
            PersonRs recipient;

            if (Objects.equals(person.getId(), dialog.getFirstPersonId())) {
                recipient = PersonMapper.INSTANCE.toDTO(personRepository.findById(dialog.getSecondPersonId()));
            } else {
                recipient = PersonMapper.INSTANCE.toDTO(personRepository.findById(dialog.getFirstPersonId()));
            }

            messageRs.setRecipient(recipient);
            messageRs.setIsSentByMe(lastMessage.getAuthorId().equals(person.getId()));
            dialogRs.setLastMessage(messageRs);
            dialogRs.setReadStatus(lastMessage.getReadStatus());
            dialogRs.setUnreadCount(messageRepository.findCountByDialogIdAndReadStatus(dialog.getId(), MessageReadStatus.UNREAD.name()));
            dialogList.add(dialogRs);
        }

        CommonRs<List<DialogRs>> result = new CommonRs<>();
        result.setData(dialogList);
        result.setTotal((long) dialogList.size());
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    public CommonRs<ComplexRs> getUnreadedMessages(String token) {
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.findByEmail(userEmail);
        long count = messageRepository.findCountByPersonIdAndReadStatus(person.getId(), MessageReadStatus.UNREAD.name());

        ComplexRs complexRs = new ComplexRs();
        complexRs.setCount(count);

        CommonRs<ComplexRs> result = new CommonRs<>();
        result.setData(complexRs);
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    public CommonRs<List<MessageRs>> getMessagesFromDialog(String token, Long dialogId, Integer offset, Integer perPage) {
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.findByEmail(userEmail);
        List<Message> messagesModel = messageRepository.findByDialogId(dialogId, offset, perPage);
        List<MessageRs> messagesDto = new ArrayList<>();

        for (Message messageModel : messagesModel) {
            MessageRs messageDto = MessageMapper.INSTANCE.toDTO(messageModel);
            Person recipientModel = personRepository.findById(messageModel.getRecipientId());
            PersonRs recipientDto = PersonMapper.INSTANCE.toDTO(recipientModel);
            messageDto.setRecipient(recipientDto);
            messageDto.setIsSentByMe(messageModel.getAuthorId().equals(person.getId()));
            messagesDto.add(messageDto);
        }

        if (messagesModel.isEmpty()) {
            Dialog dialog = dialogsRepository.findByDialogId(dialogId);
            Person recipient = personRepository.findById(dialog.getSecondPersonId());

            Message msg = Message.builder()
                .dialogId(Long.MAX_VALUE)
                .authorId(person.getId())
                .recipientId(recipient.getId())
                .readStatus("READ")
                .messageText("Напишите своё первое сообщение")
                .build();

            MessageRs messageDto = MessageMapper.INSTANCE.toDTO(msg);
            PersonRs recipientDto = PersonMapper.INSTANCE.toDTO(recipient);
            messageDto.setRecipient(recipientDto);
            messageDto.setIsSentByMe(msg.getAuthorId().equals(person.getId()));
            messagesDto.add(messageDto);
        }

        CommonRs<List<MessageRs>> result = new CommonRs<>();
        result.setData(messagesDto);
        result.setTotal(messageRepository.countByDialogId(dialogId));
        result.setOffset(offset);
        result.setPerPage(perPage);

        return result;
    }

    public CommonRs<ComplexRs> readMessagesInDialog(Long dialogId) {
        int count = messageRepository.updateReadStatusByDialogId(dialogId, MessageReadStatus.READ.name(), MessageReadStatus.UNREAD.name());
        ComplexRs complexRs = ComplexRs.builder().count((long) count).build();
        CommonRs<ComplexRs> result = new CommonRs<>();
        result.setData(complexRs);
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    public CommonRs<ComplexRs> registerDialog(String token, DialogUserShortListDto dialogUserShortListDto) {
        Person me = personRepository.findByEmail(jwtUtils.getUserEmail(token));
        long recipientId = dialogUserShortListDto.getUserIds().get(0);
        Dialog dialog = dialogsRepository.findByAutorOrRecipient(me.getId(), recipientId);
        long dialogId;

        if (dialog == null) {
            dialog = Dialog.builder()
                .lastActiveTime(new Timestamp(System.currentTimeMillis()))
                .firstPersonId(me.getId())
                .secondPersonId(recipientId)
                .lastMessageId(Long.MAX_VALUE)
                .build();

            dialogId = dialogsRepository.save(dialog);
        } else {
            dialogId = dialog.getId();
        }

        ComplexRs complexRs = new ComplexRs();
        complexRs.setId((int) dialogId);

        CommonRs<ComplexRs> commonRs = new CommonRs<>();
        commonRs.setData(complexRs);

        return commonRs;
    }
}
