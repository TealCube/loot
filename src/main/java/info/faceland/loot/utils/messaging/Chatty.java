package info.faceland.loot.utils.messaging;

import info.faceland.utils.TextUtils;
import org.bukkit.command.CommandSender;

public final class Chatty {

    private Chatty() {
        // do nothing
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(TextUtils.color(message));
    }

    public static void sendMessage(CommandSender sender, String message, String[][] args) {
        sender.sendMessage(TextUtils.color(TextUtils.args(message, args)));
    }

}
