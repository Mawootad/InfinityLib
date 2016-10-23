package com.infinityraider.infinitylib.modules.playerstate;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.utility.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncState extends MessageBase<IMessage> {
    private EntityPlayer player;
    private byte state;

    public MessageSyncState() {
        super();
    }

    public MessageSyncState(EntityPlayer player, State state) {
        this();
        this.player = player;
        this.state =
                (byte) ((state.isInvisible() ? 1 : 0)
                        | ((state.isInvulnerable() ? 1 : 0) << 1)
                        | ((state.isEthereal() ? 1 : 0) << 2)
                        | ((state.isUndetectable() ? 1 : 0) << 3));
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(ctx.side == Side.CLIENT && this.player != null) {
            PlayerStateHandler.getInstance().getState(this.player)
                    .setInvisible(((this.state) & 1) == 1)
                    .setInvulnerable(((this.state >> 1) & 1) == 1)
                    .setEthereal(((this.state >> 2) & 1) == 1)
                    .setUndetectable(((this.state >> 3) & 1) == 1);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        LogHelper.debug("Decoding message: " + this.getClass().getName());
        this.player = this.readPlayerFromByteBuf(buf);
        this.state = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        LogHelper.debug("Encoding message: player (" + this.player == null ? "null" : this.player.getEntityId() + "), state (" + this.state + ")");
        this.writePlayerToByteBuf(buf, this.player);
        buf.writeByte(this.state);
    }
}
