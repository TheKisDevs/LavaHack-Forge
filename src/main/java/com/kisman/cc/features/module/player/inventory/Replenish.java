package com.kisman.cc.features.module.player.inventory;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.TimerUtils;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(
        name = "Replenish",
        submodule = true
)
public class Replenish extends Module {

    private final Setting mode = register(new Setting("Mode", this, "Amount", Arrays.asList("Amount", "Percent")));
    private final Setting amount = register(new Setting("Amount", this, 5, 0, 63, true).setVisible(() -> mode.getValString().equals("Amount")));
    private final Setting percent = register(new Setting("Percent", this, 10, 0, 99, true).setVisible(() -> mode.getValString().equals("Percent")));
    private final Setting delay = register(new Setting("Delay", this, 0, 0, 200, true));
    private final Setting stackThresholdMode = register(new Setting("StackThreshold", this, "Amount", Arrays.asList("Amount", "Percent")));
    private final Setting stackAmount = register(new Setting("Amount", this, 5, 0, 63, true).setVisible(() -> stackThresholdMode.getValString().equals("Amount")));
    private final Setting stackPercent = register(new Setting("Percent", this, 10, 0, 99, true).setVisible(() -> stackThresholdMode.getValString().equals("Percent")));
    private final Setting strict = register(new Setting("Strict", this, true));

    private final TimerUtils timer = timer();

    @Override
    public void onEnable(){
        timer.reset();
    }

    @Override
    public void update(){
        if(mc.world == null || mc.player == null || mc.currentScreen != null)
            return;

        int delay = this.delay.getValInt();

        if(delay > 0 && !timer.passedMillis(delay)){
            return;
        } else {
            timer.reset();
        }

        int threshold = mode.getValString().equals("Amount") ? amount.getValInt() : (int) (percent.getValInt() * 0.64);

        int stackThreshold = stackThresholdMode.getValString().equals("Amount") ? stackAmount.getValInt() : (int) (stackPercent.getValInt() * 0.64);

        Map<Integer, Integer> slots = getRefillSlots(threshold, stackThreshold);
        for(int i = 1; i < 10; i++){
            if(slots.get(i) == null)
                continue;
            refill(i, slots.get(i));
        }
    }

    private Map<Integer, Integer> getRefillSlots(int threshold, int stackThreshold){
        Map<Integer, Integer> slots = new HashMap<>();
        for(int i = 0; i < 9; i++){
            ItemStack barStack = mc.player.inventory.getStackInSlot(i);

            if (barStack.isEmpty() || barStack.getItem() == Items.AIR)
                continue;

            int ts = threshold;

            if(strict.getValBoolean())
                ts = threshold >= barStack.getMaxStackSize() ? barStack.getMaxStackSize() - 1 : threshold;

            for(int a = 9; a < 36; a++){
                final Item item = mc.player.inventoryContainer.getInventory().get(a).getItem();
                final ItemStack stack1 = mc.player.inventory.getStackInSlot(a);

                if(item == barStack.getItem() && barStack.getCount() <= ts && stack1.getCount() >= stackThreshold){
                    slots.put(i, a);
                    break;
                }
            }
        }
        return slots;
    }

    private void refill(int slotToRefill, int refillSlot){
        if(refillSlot == -1)
            return;
        mc.playerController.windowClick(0, refillSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slotToRefill < 9 ? slotToRefill + 36 : slotToRefill, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, refillSlot, 0, ClickType.PICKUP, mc.player);
    }
}
