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

public class AllEquipmentPacketProcessor extends EquipmentPacketProcessor {
    public void process(PacketEvent event) {
        Object rawPacket = event.getPacket().getHandle();

        try {
            Field pairField = rawPacket.getClass().getDeclaredField("b");
            pairField.setAccessible(true);

            List<Pair<Object, Object>> newPairList = new ArrayList<>();
            List<Pair<Object, Object>> existingPairList = (List<Pair<Object, Object>>) pairField.get(rawPacket);

            for (Pair<Object, Object> pair : existingPairList) {
                Object itemSlotObject = pair.getFirst();

                newPairList.add(new Pair<>(
                        itemSlotObject,
                        NMSHelper.toMinecraftItemStack(new ItemStack(Material.AIR))
                ));
            }

            pairField.set(rawPacket, newPairList);
            pairField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        event.setPacket(PacketContainer.fromPacket(rawPacket));
    }
}
