package com.velocitypowered.crossstitch.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import io.netty.buffer.Unpooled;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CommandTreeS2CPacket.class)
public class CommandTreeSerializationMixin {
    private static final Identifier MOD_ARGUMENT_INDICATOR = new Identifier("crossstitch:mod_argument");

    @Inject(cancellable = true, method = "writeNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/argument/ArgumentTypes;toPacket(Lnet/minecraft/network/PacketByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"))
    private static void writeNode$wrapInVelocityModArgument(PacketByteBuf packetByteBuf, CommandNode<CommandSource> commandNode, Map<CommandNode<CommandSource>, Integer> map, CallbackInfo ci) {
        ArgumentCommandNode<CommandSource, ?> argumentCommandNode = (ArgumentCommandNode<CommandSource, ?>) commandNode;
        ArgumentType<?> argumentType = argumentCommandNode.getType();
        ArgumentTypes.Entry<?> entry = ArgumentTypes.classMap.get(argumentType.getClass());
        if (entry == null || entry.id.getNamespace().equals("minecraft") || entry.id.getNamespace().equals("brigadier")) {
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        serializeWrappedArgumentType(packetByteBuf, argumentType, entry);
        ci.cancel();
    }

    private static void serializeWrappedArgumentType(PacketByteBuf packetByteBuf, ArgumentType argumentType, ArgumentTypes.Entry entry) {
        packetByteBuf.writeIdentifier(MOD_ARGUMENT_INDICATOR);

        packetByteBuf.writeIdentifier(entry.id);

        PacketByteBuf extraData = new PacketByteBuf(Unpooled.buffer());
        entry.serializer.toPacket(argumentType, extraData);

        packetByteBuf.writeVarInt(extraData.readableBytes());
        packetByteBuf.writeBytes(extraData);
    }
}
