package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socialnet.api.request.MessageCommonWs;
import socialnet.api.request.MessageWs;
import socialnet.model.Dialog;
import socialnet.model.Message;
import socialnet.repository.DialogsRepository;
import socialnet.repository.MessageRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final DialogsRepository dialogsRepository;

    public MessageWs processMessage(Long dialogId, MessageWs messageWebSocket) {
        Dialog dialog = dialogsRepository.findByDialogId(dialogId);
        dialog.setLastActiveTime(Timestamp.valueOf(LocalDateTime.now()));
        long recipientId;
        if (Objects.equals(messageWebSocket.getAuthorId(), dialog.getFirstPersonId())) {
            recipientId = dialog.getSecondPersonId();
        } else {
            recipientId = dialog.getFirstPersonId();
        }
        messageWebSocket.setRecipientId(recipientId);
        Message message = Message.builder()
                .isDeleted(false)
                .messageText(messageWebSocket.getMessageText())
                .readStatus(messageWebSocket.getReadStatus())
                .time(new Timestamp(messageWebSocket.getTime()))
                .dialogId(messageWebSocket.getDialogId())
                .authorId(messageWebSocket.getAuthorId())
                .recipientId(messageWebSocket.getRecipientId())
                .build();
        long savedMessageId = messageRepository.save(message);
        dialog.setLastMessageId(savedMessageId);
        dialogsRepository.update(dialog);

        return messageWebSocket;
    }

    public void deleteMessages(MessageCommonWs messageCommonWs) {
        for (Long messageId : messageCommonWs.getMessageIds()) {
            messageRepository.markDeleted(messageId, true);
        }
    }

    public void recoverMessages(MessageCommonWs messageCommonWs) {
        messageRepository.markDeleted(messageCommonWs.getMessageId(), false);
    }

    public void editMessage(MessageCommonWs messageCommonWs) {
        Message message = messageRepository.findByAuthorId(messageCommonWs.getUserId());
        messageRepository.updateTextById(messageCommonWs.getMessageText(), messageCommonWs.getMessageId());
        messageCommonWs.setDialogId(message.getDialogId());
    }
}
