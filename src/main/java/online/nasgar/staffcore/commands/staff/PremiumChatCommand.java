package online.nasgar.staffcore.commands.staff;

import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.commands.BaseCommand;
import online.nasgar.staffcore.profile.Profile;
import online.nasgar.staffcore.profile.ProfileChat;
import online.nasgar.staffcore.utils.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PremiumChatCommand extends BaseCommand {

    public PremiumChatCommand() {
        super("premiumchat", Arrays.asList("pc"),"core.premiumchat");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Profile profile = Profile.getProfileByUUID(player.getUniqueId());

        if (profile.getChat() == ProfileChat.PREMIUM_CHAT) {
            profile.setChat(ProfileChat.NORMAL);

            StaffCore.getInstance().getMessageHandler().send(player, "PREMIUM_CHAT.DISABLED");
            return;
        }

        profile.setChat(ProfileChat.PREMIUM_CHAT);
        Tasks.runAsyncTask(profile::save);

        StaffCore.getInstance().getMessageHandler().send(player, "PREMIUM_CHAT.ENABLED");
    }
}
