package com.velocitypowered.crossstitch.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import io.netty.buffer.Unpooled;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandTreeS2CPacket.ArgumentNode.class)
public class CommandTreeSerializationMixin {
    private static final int MOD_ARGUMENT_INDICATOR = -256;

    @Redirect(method = "write(Lnet/minecraft/network/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/CommandTreeS2CPacket$ArgumentNode;write(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/command/argument/serialize/ArgumentSerializer$ArgumentTypeProperties;)V"))
    private <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void writeNode$wrapInVelocityModArgument(PacketByteBuf packetByteBuf, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
        Identifier id = Registry.COMMAND_ARGUMENT_TYPE.getId(properties.getSerializer());
        if (id == null) {
            packetByteBuf.writeVarInt(-1);
            return;
        }
        if (id.getNamespace().equals("minecraft") || id.getNamespace().equals("brigadier")) {
            CommandTreeS2CPacket.ArgumentNode.write(packetByteBuf, properties);
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        serializeWrappedArgumentType(packetByteBuf, id, properties.getSerializer(), properties);
    }

    @SuppressWarnings("unchecked")
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void serializeWrappedArgumentType(PacketByteBuf packetByteBuf, Identifier id, ArgumentSerializer<A, T> serializer, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
        packetByteBuf.writeVarInt(MOD_ARGUMENT_INDICATOR);

        packetByteBuf.writeVarInt(Registry.COMMAND_ARGUMENT_TYPE.getRawId(properties.getSerializer()));

        PacketByteBuf extraData = new PacketByteBuf(Unpooled.buffer());
        serializer.writePacket((T) properties, packetByteBuf);

        packetByteBuf.writeVarInt(extraData.readableBytes());
        packetByteBuf.writeBytes(extraData);
    }
}
