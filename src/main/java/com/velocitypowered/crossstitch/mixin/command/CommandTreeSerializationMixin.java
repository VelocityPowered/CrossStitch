package com.velocitypowered.crossstitch.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import io.netty.buffer.Unpooled;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandTreeS2CPacket.class)
public class CommandTreeSerializationMixin {
    private static final Identifier MOD_ARGUMENT_INDICATOR = new Identifier("crossstitch:mod_argument");

    @Redirect(method = "writeNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/argument/ArgumentTypes;toPacket(Lnet/minecraft/network/PacketByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"))
    private static void writeNode$wrapInVelocityModArgument(PacketByteBuf packetByteBuf, ArgumentType<?> type) {
        ArgumentTypes.Entry<?> entry = ArgumentTypes.classMap.get(type.getClass());
        if (entry == null || entry.id.getNamespace().equals("minecraft") || entry.id.getNamespace().equals("brigadier")) {
            packetByteBuf.writeIdentifier(new Identifier(""));
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        serializeWrappedArgumentType(packetByteBuf, type, entry);
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
