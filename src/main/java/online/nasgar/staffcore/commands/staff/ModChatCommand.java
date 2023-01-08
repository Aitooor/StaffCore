package online.nasgar.staffcore.commands.staff;

import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.commands.BaseCommand;
import online.nasgar.staffcore.profile.Profile;
import online.nasgar.staffcore.profile.ProfileChat;
import online.nasgar.staffcore.utils.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ModChatCommand extends BaseCommand {

    public ModChatCommand() {
        super("modchat", Arrays.asList("md"),"core.modchat");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Profile profile = Profile.getProfileByUUID(player.getUniqueId());

        if (profile.getChat() == ProfileChat.MOD_CHAT) {
            profile.setChat(ProfileChat.NORMAL);

            StaffCore.getInstance().getMessageHandler().send(player, "MOD_CHAT.DISABLED");
            return;
        }

        profile.setChat(ProfileChat.MOD_CHAT);
        Tasks.runAsyncTask(profile::save);

        StaffCore.getInstance().getMessageHandler().send(player, "MOD_CHAT.ENABLED");
    }
}
