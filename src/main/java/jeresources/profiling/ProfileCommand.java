package jeresources.profiling;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ProfileCommand {
    private static final String COMMAND_NAME = "jer_profile";
    private static final String CHUNK_PARAM = "chunk count";
    private static final String DIM_PARAM = "all dimensions";

    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> profileCommand = Commands.literal(COMMAND_NAME)
            .requires(source -> source.hasPermission(4) && Minecraft.getInstance().hasSingleplayerServer());

        profileCommand.then(
            Commands.argument(CHUNK_PARAM, IntegerArgumentType.integer(1))
            .then(Commands.argument(DIM_PARAM, BoolArgumentType.bool())
            .executes((context -> Profiler.init(
                context.getSource().getEntity(),
                IntegerArgumentType.getInteger(context, CHUNK_PARAM),
                BoolArgumentType.getBool(context, DIM_PARAM)
            ) ? Command.SINGLE_SUCCESS : 0))));

        profileCommand.then(
            Commands.literal("stop")
            .executes(context -> Profiler.stop(context.getSource().getEntity()) ? Command.SINGLE_SUCCESS : 0));

        dispatcher.register(profileCommand);
    }
}
