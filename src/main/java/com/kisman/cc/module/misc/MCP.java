// package com.kisman.cc.module.misc;

// import com.kisman.cc.module.Category;
// import com.kisman.cc.module.Module;
// import com.kisman.cc.util.InventoryUtil;
// import net.minecraft.item.ItemEnderPearl;
// import net.minecraft.util.math.RayTraceResult;
// import org.lwjgl.input.Mouse;

// public class MCP extends Module{
//     public MCP() {
//         super("MCP", "MCP", Category.MISC);
//     }

//     public void update() {
//         RayTraceResult.Type type = mc.objectMouseOver.typeOfHit;

//         if (type.equals(RayTraceResult.Type.MISS) && Mouse.isButtonDown(2)) {
//             int oldSlot = mc.player.inventory.currentItem;

//             int pearlSlot = InventoryUtil.findFirstItemSlot(ItemEnderPearl.class, 0, 8);

//             if (pearlSlot != -1) {

//                 mc.player.inventory.currentItem = pearlSlot;
//                 mc.rightClickMouse();
//                 mc.player.inventory.currentItem = oldSlot;
//             }
//         }
//     }
// }
