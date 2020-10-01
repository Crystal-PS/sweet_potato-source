package io.github.teddyxlandlee.sweet_potato.screen;

import io.github.teddyxlandlee.annotation.Unused_InsteadOf;
import io.github.teddyxlandlee.debug.Debug;
import io.github.teddyxlandlee.sweet_potato.SPMMain;
import io.github.teddyxlandlee.sweet_potato.util.DeprecatedGrindingResultSlot;
import io.github.teddyxlandlee.sweet_potato.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import javax.annotation.Nullable;

@Deprecated
public class DeprecatedGrinderScreenHandler$4 extends AbstractScreenHandler {
    //protected final Inventory inventory;
    //protected final PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(2);
    protected final PlayerEntity player;

    public DeprecatedGrinderScreenHandler$4(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(SPMMain.GRINDER_SCREEN_HANDLER_TYPE, syncId, playerInventory, new SimpleInventory(2), buf);
        this.player = playerInventory.player;
        if (this.e instanceof Inventory) {
            this.inventory = (Inventory) this.e;
            Debug.debug(this, "this.inventory.size(): " + this.inventory.size());
        }
    }

    public DeprecatedGrinderScreenHandler$4(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(type, syncId, playerInventory, inventory);

        this.player = playerInventory.player;
        this.propertyDelegate = propertyDelegate;
        //this.inventory = inventory;
        checkDataCount(propertyDelegate, 3);
        this.addSlot(new Slot(this.inventory, 0, 40, 35), RenderType.DEFAULT_SLOT);
        this.addSlot(new DeprecatedGrindingResultSlot(this.player, this.inventory, 1, 116, 35), RenderType.DEFAULT_SLOT);

        // Player Inventory
        /*int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }*/
        this.addPlayerInv(8, 84);
        Debug.debug(this, "Successfully create player inventory");

        this.addProperties(propertyDelegate);
    }

    @Unused_InsteadOf @Deprecated
    protected static void checkSize(Inventory inventory, int expectedSize) {
        int i = inventory.size();
        if (i < expectedSize) {
            throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + expectedSize);
        }
    }

    protected static void checkDataCount(PropertyDelegate data, int expectedCount) {
        int i = data.size();
        if (i < expectedCount) {
            throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + expectedCount);
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {  // Not void stack
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 1) {
                if (!this.insertItem(itemStack2, 3, 39, true))
                    return ItemStack.EMPTY;
                slot.onStackChanged(itemStack2, itemStack);
            } else if (index != 0) {
                if (Util.grindable(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false))
                        return ItemStack.EMPTY;
                } else if (index >= 2 && index < 29) {
                    if (!this.insertItem(itemStack2, 29, 38, false))
                        return ItemStack.EMPTY;
                } else if (index >= 29 && index < 38 && !this.insertItem(itemStack2, 2, 29, false))
                    return ItemStack.EMPTY;
            } else if (!this.insertItem(itemStack2, 2, 38, false))
                return ItemStack.EMPTY;

            if (itemStack2.isEmpty())
                slot.setStack(ItemStack.EMPTY);
            else
                slot.markDirty();

            if (itemStack2.getCount() == itemStack.getCount())
                return ItemStack.EMPTY;

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Environment(EnvType.CLIENT)
    public int getGrindProgress() {
        int grindTime = this.propertyDelegate.get(0);
        int grindTimeTotal = this.propertyDelegate.get(1);
        return grindTimeTotal != 0 && grindTime != 0 ? grindTime * 22 / grindTimeTotal : 0;
    }

    @Environment(EnvType.CLIENT)
    //@Deprecated
    public int simpleGrindProgress() {
        return this.propertyDelegate.get(0) >> 1;
    }

    public int getIngredientData() {
        return this.propertyDelegate.get(2);
    }
}
