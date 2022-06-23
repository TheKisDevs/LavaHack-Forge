package the.kis.devs.api.util.chat.cubic;

import com.kisman.cc.util.chat.cubic.AbstractChatMessage;
import net.minecraft.util.text.ITextComponent;

/**
 * @author _kisman_
 * @since 21:11 of 15.06.2022
 */
public abstract class AbstractChatMessageAPI extends AbstractChatMessage {
    void updateCaller(Class<?> caller){}
    public abstract void printClientMessage(ITextComponent textComponent);
    public abstract void printClientMessage(String message);
    public abstract void printClientModuleMessage(ITextComponent textComponent);
    public abstract void printClientModuleMessage(String message);
    public abstract void printModuleMessage(ITextComponent textComponent);
    public abstract void printModuleMessage(String message);
    public abstract void printClassMessage(ITextComponent textComponent);
    public abstract void printClassMessage(String message);
    public abstract void printClientClassMessage(ITextComponent textComponent);
    public abstract void printClientClassMessage(String message);
}
