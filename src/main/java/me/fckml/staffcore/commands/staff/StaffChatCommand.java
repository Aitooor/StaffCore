package me.fckml.staffcore.commands.staff;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.profile.ProfileChat;
import me.fckml.staffcore.utils.CC;
import me.fckml.staffcore.utils.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class StaffChatCommand extends BaseCommand {

    public StaffChatCommand() {
        super("staffchat", Arrays.asList("sc"),"core.staffchat");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Profile profile = Profile.getProfileByUUID(player.getUniqueId());

        if (profile.getChat() == ProfileChat.STAFF_CHAT) {
            profile.setChat(ProfileChat.NORMAL);

            sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("STAFF_CHAT.DISABLED")));
            return;
        }

        profile.setChat(ProfileChat.STAFF_CHAT);
        Tasks.runAsyncTask(profile::save);

        sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("STAFF_CHAT.ENABLED")));
    }
}
