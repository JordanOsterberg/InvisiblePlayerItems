package dev.jcsoftware.hideplayeritems;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.jcsoftware.hideplayeritems.processor.AllEquipmentPacketProcessor;
import dev.jcsoftware.hideplayeritems.processor.HeldEquipmentPacketProcessor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HidePlayerItemsPlugin extends JavaPlugin {

    private EquipmentPacketProcessor equipmentPacketProcessor;

    @Override
    public void onEnable() {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (equipmentPacketProcessor == null) return;
                equipmentPacketProcessor.process(event);
            }
        });
    }

    public void setEquipmentPacketProcessor(EquipmentPacketProcessor equipmentPacketProcessor) {
        this.equipmentPacketProcessor = equipmentPacketProcessor;
        if (equipmentPacketProcessor != null) {
            equipmentPacketProcessor.onEnable();
        } else {
            EquipmentPacketProcessor.refreshEquipmentOfAllPlayers();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String context = args.length < 1 ? "none" : args[0];

        if (context.equalsIgnoreCase("none")) {
            setEquipmentPacketProcessor(null);
            sender.sendMessage(ChatColor.GREEN + "No longer hiding any equipment.");
        } else if (context.equalsIgnoreCase("held")) {
            setEquipmentPacketProcessor(new HeldEquipmentPacketProcessor());
            sender.sendMessage(ChatColor.YELLOW + "Now hiding only held equipment.");
        } else if (context.equalsIgnoreCase("all")) {
            setEquipmentPacketProcessor(new AllEquipmentPacketProcessor());
            sender.sendMessage(ChatColor.RED + "Now hiding all equipment.");
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "/hide [none | held | all]");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            return Arrays.asList("none", "held", "all");
        }

        return new ArrayList<>();
    }

}
