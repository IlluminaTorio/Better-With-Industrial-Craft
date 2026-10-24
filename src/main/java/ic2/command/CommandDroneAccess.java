package ic2.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import ic2.net.GrantAccessMessage;
import ic2.util.DroneAccessSavedData;
import java.util.Set;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.world.World;

public class CommandDroneAccess
implements CommandManager.CommandRegistry {
    private void execute(CommandSource source, int action, String target) {
        Player sender = source.getSender();
        if (sender == null) {
            source.sendMessage("Only players can manage drone access.");
            return;
        }
        World world = source.getWorld();
        if (world == null || world.isClientSide) {
            GrantAccessMessage message = new GrantAccessMessage();
            message.action = action;
            message.target = target == null ? "" : target;
            ic2.net.IC2Network.sendToServer(message);
            return;
        }
        CommandDroneAccess.apply(world, sender.username, action, target);
    }

    public static void apply(World world, String owner, int action, String target) {
        DroneAccessSavedData data = DroneAccessSavedData.get(world);
        if (data == null || owner == null) {
            return;
        }
        if (action == GrantAccessMessage.ACTION_GRANT) {
            if (target == null || target.isEmpty()) {
                return;
            }
            data.grant(owner, target);
            Player targetPlayer = CommandDroneAccess.findPlayer(world, target);
            if (targetPlayer != null) {
                targetPlayer.sendMessage(TextFormatting.Base.LIME, owner + " granted you access to their drone depots.");
            }
            return;
        }
        if (action == GrantAccessMessage.ACTION_REVOKE) {
            if (target == null || target.isEmpty()) {
                return;
            }
            boolean removed = data.revoke(owner, target);
            Player targetPlayer = CommandDroneAccess.findPlayer(world, target);
            if (targetPlayer != null) {
                targetPlayer.sendMessage(TextFormatting.Base.GRAY, owner + (removed ? " revoked your access to their drone depots." : " has no active grant for you."));
            }
            return;
        }
        Set<String> grants = data.listGrants(owner);
        Player self = CommandDroneAccess.findPlayer(world, owner);
        if (self == null) {
            return;
        }
        if (grants.isEmpty()) {
            self.sendMessage(TextFormatting.Base.GRAY, "No players have access to your drone depots. Use /ic2grant <player>.");
            return;
        }
        self.sendMessage(TextFormatting.Base.LIME, "Players with access to your drone depots: " + TextFormatting.Base.WHITE + String.join(", ", grants));
    }

    private static Player findPlayer(World world, String name) {
        if (world == null || name == null) {
            return null;
        }
        for (Player player : world.players) {
            if (!name.equals(player.username)) continue;
            return player;
        }
        return null;
    }

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            (ArgumentBuilderLiteral)((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("ic2grant"))
                .then(ArgumentBuilderRequired.argument("player", ArgumentTypeString.word()).executes(c -> {
                    this.execute((CommandSource)c.getSource(), GrantAccessMessage.ACTION_GRANT, (String)c.getArgument("player", String.class));
                    return 1;
                }))
        );
        dispatcher.register(
            (ArgumentBuilderLiteral)((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("ic2revoke"))
                .then(ArgumentBuilderRequired.argument("player", ArgumentTypeString.word()).executes(c -> {
                    this.execute((CommandSource)c.getSource(), GrantAccessMessage.ACTION_REVOKE, (String)c.getArgument("player", String.class));
                    return 1;
                }))
        );
        dispatcher.register(
            (ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("ic2grants").executes(c -> {
                this.execute((CommandSource)c.getSource(), GrantAccessMessage.ACTION_LIST, null);
                return 1;
            })
        );
    }
}
