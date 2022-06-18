package com.velocitypowered.crossstitch.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import io.netty.buffer.Unpooled;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(targets = "net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket$ArgumentNode")
public class CommandTreeSerializationMixin {
    @Shadow @Final private ArgumentSerializer.ArgumentTypeProperties<?> properties;
    private static final int MOD_ARGUMENT_INDICATOR = -256;

    @Inject(method = "write(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/command/argument/serialize/ArgumentSerializer;Lnet/minecraft/command/argument/serialize/ArgumentSerializer$ArgumentTypeProperties;)V",
            at = @At("HEAD"), cancellable = true)
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void writeNode$wrapInVelocityModArgument(PacketByteBuf buf, ArgumentSerializer<A, T> serializer, ArgumentSerializer.ArgumentTypeProperties<A> properties, CallbackInfo ci) {
        Optional<RegistryKey<ArgumentSerializer<?, ?>>> entry = Registry.COMMAND_ARGUMENT_TYPE.getKey(serializer);

        if (entry.isEmpty()) {
            return;
        }
        RegistryKey<ArgumentSerializer<?, ?>> keyed = entry.get();

        if (keyed.getValue().getNamespace().equals("minecraft") || keyed.getValue().getNamespace().equals("brigadier")) {
           return;
        }
        ci.cancel();

        // Not a standard Minecraft argument type - so we need to wrap it
        serializeWrappedArgumentType(buf, serializer, properties);
    }

    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void serializeWrappedArgumentType(PacketByteBuf packetByteBuf, ArgumentSerializer<A, T> serializer, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
        packetByteBuf.writeVarInt(MOD_ARGUMENT_INDICATOR);
        packetByteBuf.writeVarInt(Registry.COMMAND_ARGUMENT_TYPE.getRawId(serializer));

        PacketByteBuf extraData = new PacketByteBuf(Unpooled.buffer());
        serializer.writePacket((T) properties, extraData);

        packetByteBuf.writeVarInt(extraData.readableBytes());
        packetByteBuf.writeBytes(extraData);
    }
}
