package socialnet.model.enums;

import lombok.Getter;

@Getter
public enum PersonOnlineStatus {
    ONLINE(true), OFFLINE(false);

    private final boolean isOnline;

    PersonOnlineStatus(boolean state) {
        this.isOnline = state;
    }
}
