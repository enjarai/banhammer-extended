package eu.pb4.banhammer.commands;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.UUID;

public class ConfirmationHandler {
    public Dictionary<UUID, String> confirmations = new Hashtable<UUID, String>();

    public boolean ifConfirmedOrConfirm(UUID uuid, String content) {
        if (Objects.equals(this.confirmations.get(uuid), content)) {
            this.confirmations.remove(uuid);
            return true;
        } else {
            this.confirmations.put(uuid, content);
            return false;
        }
    }
}
