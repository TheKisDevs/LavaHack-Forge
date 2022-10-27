package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.module.misc.ItemRenamer;
import com.kisman.cc.util.StringUtils;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ItemNameCommand extends Command {

    public ItemNameCommand(){
        super("itemname");
    }

    @Override
    public String getDescription() {
        return "Changes the name of an Item when ItemRenamer is enabled";
    }

    @Override
    public String getSyntax() {
        return "itemname <original item name> <new item name>";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        if(args.length < 1){
            ChatUtility.error().printClientClassMessage("To few arguments: " + getSyntax());
            return;
        }
        String originalName = args[0];
        Item item = Item.REGISTRY.getObject(new ResourceLocation(originalName));
        if(item == null){
            ChatUtility.error().printClientClassMessage("Could not find item: " + originalName);
            return;
        }
        if(args.length < 2){
            String original = ItemRenamer.ORIGINALS.get(item);
            if(original == null)
                return;
            ItemRenamer.NAME_MAP.put(item, original);
            ChatUtility.complete().printClientClassMessage("Successfully reset: " + originalName);
            return;
        }
        String full = StringUtils.merge(args, 1, args.length).toString();
        ItemRenamer.NAME_MAP.put(item, full);
        ChatUtility.complete().printClientClassMessage("Successfully changed name to: " + full);
    }
}
