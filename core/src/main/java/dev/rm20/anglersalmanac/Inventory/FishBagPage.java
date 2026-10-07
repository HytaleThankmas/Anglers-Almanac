package dev.rm20.anglersalmanac.Inventory;

import com.google.protobuf.StringValue;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class FishBagPage extends InteractiveCustomUIPage<FishBagPage.FishBagEventData> {

    public FishBagPage(@Nonnull final PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, FishBagEventData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Almanac/FishBagPage.ui");

        final var playerEntityRef = playerRef.getReference();
        if (playerEntityRef == null) return;

        // Fetch the Fish Bag inventory
        var fishBag = store.getComponent(playerEntityRef, FishBagComponent.getComponentType());

        commandBuilder.clear("#FishGrid");

        if (fishBag != null) {
            ItemContainer bagInventory = fishBag.getInventory();
            int capacity = bagInventory.getCapacity();
            int itemsShown = 0;
            int totalValue = 0;
            // Iterate through the fish bag slots and populate the grid
            for (short i = 0; i < capacity; i++) {
                ItemStack stack = bagInventory.getItemStack(i);
                if (stack != null && !stack.isEmpty()) {
                    final var selector = "#FishGrid[" + itemsShown + "]";

                    commandBuilder.append("#FishGrid", "Almanac/Utils/FishBagSlot.ui");
                    commandBuilder.set(selector + " #FishIcon.ItemId", stack.getItemId());

                    int qty = stack.getQuantity();
                    commandBuilder.set(selector + " #FishQuantity.Text", qty > 1 ? String.valueOf(qty) : "");


                    int coinValue = FishMarket.getFishValue(stack) * qty;
                    totalValue += coinValue;

                    String tooltipMessage = Message.translation(stack.getItem().getTranslationKey()).getAnsiMessage() + ": Sell Value: " + coinValue + " Coins";

                    // Apply the message to the root Group's Tooltip property
                    commandBuilder.set(selector + " #HoverCatcher.TooltipText", tooltipMessage);
                    itemsShown++;
                }
            }
            commandBuilder.set("#SellAllButton.Text", "Sell all: " + totalValue + " Coins");

            commandBuilder.set("#SellAllButton.Disabled", totalValue <= 0);
        }


        // Bind the Sell All button event
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SellAllButton",
                EventData.of(FishBagEventData.ACTION, "sell_all"),
                false
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull FishBagEventData data) {
        if ("sell_all".equals(data.getAction())) {
            final var playerEntityRef = playerRef.getReference();
            if (playerEntityRef == null) return;

            final Player player = store.getComponent(playerEntityRef, Player.getComponentType());
            if (player != null) {
                // Call your FishMarket logic
                FishMarket.sellAllFish(player);

                // Refresh the UI so the bag appears empty
                refreshUI(ref, store);
            }
        }
    }

    private void refreshUI(@Nonnull final Ref<EntityStore> ref, @Nonnull final Store<EntityStore> store) {
        var commandBuilder = new UICommandBuilder();
        var eventBuilder = new UIEventBuilder();
        build(ref, commandBuilder, eventBuilder, store);
        sendUpdate(commandBuilder, eventBuilder, true);
    }

    /**
     * Event data capturing custom actions from the UI
     */
    public static class FishBagEventData {
        @Nonnull
        static final String ACTION = "Action";

        @Nonnull
        public static final BuilderCodec<FishBagEventData> CODEC = BuilderCodec.builder(FishBagEventData.class, FishBagEventData::new)
                .append(
                        new KeyedCodec<>(ACTION, Codec.STRING),
                        (data, s) -> data.action = s,
                        data -> data.action
                ).add()
                .build();

        private String action = "";

        public String getAction() {
            return action;
        }
    }
}