package dev.jcsoftware.hideplayeritems.processor;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.datafixers.util.Pair;
import dev.jcsoftware.hideplayeritems.EquipmentPacketProcessor;
import dev.jcsoftware.hideplayeritems.NMSHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeldEquipmentPacketProcessor extends EquipmentPacketProcessor {

    private Object handSlotEnum = null;
    private Object offhandSlotEnum = null;

    public HeldEquipmentPacketProcessor() {
        try {
            Class<?> clazz = NMSHelper.getNMSClass("EnumItemSlot");
            handSlotEnum = clazz.getField("MAINHAND").get(null);
            offhandSlotEnum = clazz.getField("OFFHAND").get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void process(PacketEvent event) {
        Object rawPacket = event.getPacket().getHandle();

        try {
            Field pairField = rawPacket.getClass().getDeclaredField("b");
            pairField.setAccessible(true);

            List<Pair<Object, Object>> newPairList = new ArrayList<>();
            List<Pair<Object, Object>> existingPairList = (List<Pair<Object, Object>>) pairField.get(rawPacket);

            for (Pair<Object, Object> pair : existingPairList) {
                Object itemSlotObject = pair.getFirst();

                Pair<Object, Object> replaced = pair;

                if (Objects.equals(handSlotEnum, itemSlotObject) ||
                        Objects.equals(offhandSlotEnum, itemSlotObject)) {
                    replaced = new Pair<>(
                            itemSlotObject,
                            NMSHelper.toMinecraftItemStack(new ItemStack(Material.AIR))
                    );
                }

                newPairList.add(replaced);
            }

            pairField.set(rawPacket, newPairList);
            pairField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        event.setPacket(PacketContainer.fromPacket(rawPacket));
    }
}
