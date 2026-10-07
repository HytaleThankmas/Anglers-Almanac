package dev.rm20.anglersalmanac.Inventory;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.rm20.anglersalmanac.Inventory.Filter.FishItemAddFilter;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FishBagComponent extends InventoryComponent {

    public static final int FISH_BAG_SECTION_ID = -42;
    public static final short DEFAULT_CAPACITY = 32;

    @Nonnull
    public static final BuilderCodec<FishBagComponent> CODEC = BuilderCodec.builder(FishBagComponent.class, FishBagComponent::new, InventoryComponent.CODEC)
            .afterDecode(FishBagComponent::afterDecode)
            .build();

    @Getter
    @Setter
    private static ComponentType<EntityStore, FishBagComponent> componentType;

    public FishBagComponent() {
    }

    public FishBagComponent(short capacity) {
        super(capacity);
        afterDecode();
    }

    @Override
    public void ensureCapacity(short capacity, @Nonnull List<ItemStack> remainder) {
        super.ensureCapacity(capacity, remainder);
        afterDecode();
    }

    private void afterDecode() {
        applyFishBagFilters(this.inventory);
    }

    public static <T extends ItemContainer> T applyFishBagFilters(T container) {
        if (container instanceof SimpleItemContainer simpleContainer) {
            simpleContainer.setGlobalFilter(FilterType.ALLOW_INPUT_ONLY);
            var addFilter = new FishItemAddFilter();
            for (short i = 0; i < simpleContainer.getCapacity(); i++) {
                simpleContainer.setSlotFilter(FilterActionType.ADD, i, addFilter);
                simpleContainer.setSlotFilter(FilterActionType.DROP, i, SlotFilter.DENY);
                simpleContainer.setSlotFilter(FilterActionType.REMOVE, i, SlotFilter.DENY);
            }
        }
        return container;
    }
    public int getSectionId() {
            return FISH_BAG_SECTION_ID;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        var fishBag = new FishBagComponent();
        fishBag.inventory = inventory.clone();
        return fishBag;
    }


    public static void ensurePlayerHasFishBag(Player player) {
        var accessor = player.getReference().getStore();

        if (accessor.getComponent(player.getReference(), FishBagComponent.getComponentType()) == null) {
            accessor.putComponent(
                    player.getReference(),
                    FishBagComponent.getComponentType(),
                    new FishBagComponent(FishBagComponent.DEFAULT_CAPACITY)
            );
        }
    }
}
