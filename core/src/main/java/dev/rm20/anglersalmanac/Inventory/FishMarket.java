package dev.rm20.anglersalmanac.Inventory;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import dev.rm20.anglersalmanac.AnglersAlmanac;
import dev.rm20.anglersalmanac.Models.FishLoot;
import dev.rm20.anglersalmanac.Models.FishLootManager;
import dev.rm20.thankmasvault.economy.EconomyAPI;

import java.awt.*;

import static dev.rm20.thankmasvault.economy.EconomyAPIKt.addCoins;

public class FishMarket {
    public static void sellAllFish(Player player){
        sellAllFish(player,null);
    }
    public static void sellAllFish(Player player, CommandContext commandContext) {
        var accessor = player.getReference().getStore();
        FishBagComponent fishBag = accessor.getComponent(player.getReference(), FishBagComponent.getComponentType());

        if (fishBag == null) {
            return;
        }

        var container = fishBag.getInventory();
        int totalEarned = 0;
        int fishSold = 0;

        for (short i = 0; i < container.getCapacity(); i++) {
            ItemStack stack = container.getItemStack(i);

            if (stack != null) {
                int pricePerItem = getFishValue(stack);
                totalEarned += (pricePerItem * stack.getQuantity());
                fishSold += stack.getQuantity();
            }
        }

        if (AnglersAlmanac.getInstance().ECONOMY_HOOK!= null && AnglersAlmanac.getInstance().ECONOMY_HOOK.enabled) {
            AnglersAlmanac.getInstance().ECONOMY_HOOK.ensureWallet(player.getReference(),accessor,0);
            if(!AnglersAlmanac.getInstance().ECONOMY_HOOK.deposit(player.getReference(), accessor, totalEarned))
            {
                AnglersAlmanac.LOGGER.atWarning().log("Failed to deposit coins");
                return;
            }
        } else {
            AnglersAlmanac.LOGGER.atWarning().log("Economy plugin not available to deposit coins.");
            return;
        }
        if(commandContext == null){
            if (fishSold > 0) {
                Ref<EntityStore> playerRef = player.getReference();
                if (playerRef == null) return;
                PlayerRef playerRef1 = playerRef.getStore().getComponent(playerRef, PlayerRef.getComponentType());
                if (playerRef1 == null) return;
                Message titleMessage = Message.join(Message.raw("You earned " + totalEarned + " coins!"));
                titleMessage.color(Color.RED);
                Message subtitleMessage = Message.translation("by selling " + fishSold + " fish");
                try {
                    var packetHandler = playerRef1.getPacketHandler();
                    NotificationUtil.sendNotification(
                            packetHandler,
                            titleMessage,
                            subtitleMessage,
                            NotificationStyle.Success);
                } catch (Exception e) {
                    AnglersAlmanac.LOGGER.atWarning().log("Failed to send notification to " + playerRef1.getUsername() + ": " + e.getMessage());
                }
            }
        }
        else
        {
            if (fishSold > 0) {
                commandContext.sendMessage(Message.raw("You sold " + fishSold + " fish for " + totalEarned + " coins!"));

            } else {
                commandContext.sendMessage(Message.raw("Your fish bag is empty!"));
            }
        }

        container.clear();
    }

    public static int getFishValue(ItemStack itemStack) {
        FishLoot fishData = FishLootManager.getInternalFishData(itemStack.getItemId());

        if (fishData == null) {
            return 0;
        }
        double clampedDiff = Math.min(fishData.getMinigameStats().difficulty, 8.0);
        double clampedStam = Math.min(fishData.getMinigameStats().stamina, 200.0);

        double normDiff = (clampedDiff / 8.0) * 100.0;
        double normStam = (clampedStam / 200.0) * 100.0;
        double combinedStats = (normDiff + normStam) / 2.0;

        double weightModifier;
        if (fishData.getWeight() > 10.0) {
            weightModifier = Math.max(1.0 - (fishData.getWeight() / 500.0), 0.7);
        } else {
            weightModifier = Math.max(1.0 - ((10.0 - fishData.getWeight()) / 20.0), 0.85);
        }

        String rarity = fishData.getRarity().toLowerCase();

        double rarityMultiplier = switch (rarity) {
            case "junk" -> 0.5;
            case "common" -> 1.0;
            case "uncommon" -> 1.1;
            case "rare" -> 1.2;
            case "epic" -> 1.3;
            case "legendary" -> 1.5;
            default -> 1.0;
        };

        return (int) Math.round(combinedStats * weightModifier * rarityMultiplier);
    }



}
