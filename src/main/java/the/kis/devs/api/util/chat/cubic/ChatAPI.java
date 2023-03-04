package the.kis.devs.api.util.chat.cubic;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.AbstractChatMessage;
import net.minecraft.util.text.ITextComponent;

/**
 * @author _kisman_
 * @since 21:14 of 15.06.2022
 */
public class ChatAPI extends AbstractChatMessageAPI {
    public AbstractChatMessage sender;
    public ChatAPI(AbstractChatMessage sender) {this.sender = sender;}
    @Override public void printClientMessage(ITextComponent textComponent) {sender.printClientMessage(textComponent);}
    @Override public void printClientMessage(String message) {sender.printClientMessage(message);}
    @Override public void printClientModuleMessage(String message, Module module) {sender.printClientModuleMessage(message, module);}
    @Override public void printClientModuleMessage(ITextComponent textComponent, Module module) {sender.printClientModuleMessage(textComponent, module);}
    @Override public void printClientModuleMessage(ITextComponent textComponent) {sender.printClientModuleMessage(textComponent);}
    @Override public void printClientModuleMessage(String message) {sender.printClientModuleMessage(message);}
    @Override public void printModuleMessage(ITextComponent textComponent) {sender.printModuleMessage(textComponent);}
    @Override public void printModuleMessage(String message) {sender.printModuleMessage(message);}
    @Override public void printClassMessage(ITextComponent textComponent) {sender.printClassMessage(textComponent);}
    @Override public void printClassMessage(String message) {sender.printClassMessage(message);}
    @Override public void printClientClassMessage(ITextComponent textComponent) {sender.printClientClassMessage(textComponent);}
    @Override public void printClientClassMessage(String message) {sender.printClientClassMessage(message);}
    @Override
    public String getClientPrefix() {return sender.getClientPrefix();}
}
