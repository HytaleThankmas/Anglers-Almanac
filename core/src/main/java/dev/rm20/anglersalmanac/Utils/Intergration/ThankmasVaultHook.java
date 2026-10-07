package dev.rm20.anglersalmanac.Utils.Intergration;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.rm20.anglersalmanac.AnglersAlmanac;
import java.lang.reflect.Method;
import java.util.Optional;

public class ThankmasVaultHook {
    private static final String GROUP_NAME = "dev.rm20";
    private static final String MOD_NAME = "ThankmasVault";
    private Method ensureMethod;
    private Method depositMethod;
    private Method withdrawMethod;
    private Method getBalanceMethod;
    private Method hasEnoughMethod;
    public boolean enabled = false;
    public ThankmasVaultHook() {
        try {
            PluginManager pm = PluginManager.get();
            PluginIdentifier targetId = new PluginIdentifier(GROUP_NAME, MOD_NAME);
            if (!pm.hasPlugin(targetId, SemverRange.WILDCARD)) {
                AnglersAlmanac.LOGGER.atInfo().log("ThankmasVault economy plugin not detected.");
                return;
            }
            Optional<Object> pluginOptional = Optional.ofNullable(pm.getPlugin(targetId));
            if (pluginOptional.isEmpty()) return;
            Object pluginInstance = pluginOptional.get();
            ClassLoader targetLoader = pluginInstance.getClass().getClassLoader();
            // Load EconomyAPI using ThankmasVault's ClassLoader
            Class<?> apiClass = Class.forName("dev.rm20.thankmasvault.economy.EconomyAPI", true, targetLoader);
            // Reflection lookup for methods (uses ComponentAccessor interface)
            this.ensureMethod = apiClass.getMethod("ensureWallet", Ref.class, ComponentAccessor.class, int.class);;
            this.depositMethod = apiClass.getMethod("deposit", Ref.class, ComponentAccessor.class, int.class);
            this.withdrawMethod = apiClass.getMethod("withdraw", Ref.class, ComponentAccessor.class, int.class);
            this.getBalanceMethod = apiClass.getMethod("getBalance", Ref.class, ComponentAccessor.class);
            this.hasEnoughMethod = apiClass.getMethod("hasEnough", Ref.class, ComponentAccessor.class, int.class);
            this.enabled = true;
            AnglersAlmanac.LOGGER.atInfo().log("Successfully hooked into ThankmasVault Economy API.");
        } catch (ClassNotFoundException e) {
            AnglersAlmanac.LOGGER.atWarning().log("ThankmasVault found, but EconomyAPI class was missing.");
        } catch (NoSuchMethodException e) {
            AnglersAlmanac.LOGGER.atWarning().log("ThankmasVault EconomyAPI found, but method signatures did not match: " + e.getMessage());
        } catch (Throwable t) {
            AnglersAlmanac.LOGGER.atWarning().log("General failure loading ThankmasVault integration: " + t.getMessage());
        }
    }
    public void ensureWallet(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor, int amount)
    {
        if (!enabled || getBalanceMethod == null) return;
        try {
            ensureMethod.invoke(null, ref, accessor, amount);

        } catch (Exception e) {
        }
    }

    public boolean deposit(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor, int amount) {
        if (!enabled || depositMethod == null) return false;
        try {
            Object result = depositMethod.invoke(null, ref, accessor, amount);
            return (result instanceof Boolean) && (Boolean) result;
        } catch (Exception e) {
            AnglersAlmanac.LOGGER.atSevere().withCause(e).log("Failed to deposit coins via ThankmasVault");
            return false;
        }
    }
    public boolean withdraw(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor, int amount) {
        if (!enabled || withdrawMethod == null) return false;
        try {
            Object result = withdrawMethod.invoke(null, ref, accessor, amount);
            return (result instanceof Boolean) && (Boolean) result;
        } catch (Exception e) {
            AnglersAlmanac.LOGGER.atSevere().withCause(e).log("Failed to withdraw coins via ThankmasVault");
            return false;
        }
    }
    public int getBalance(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor) {
        if (!enabled || getBalanceMethod == null) return 0;
        try {
            Object result = getBalanceMethod.invoke(null, ref, accessor);
            return (result instanceof Integer) ? (Integer) result : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    public boolean hasEnough(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor, int amount) {
        if (!enabled || hasEnoughMethod == null) return false;
        try {
            Object result = hasEnoughMethod.invoke(null, ref, accessor, amount);
            return (result instanceof Boolean) && (Boolean) result;
        } catch (Exception e) {
            return false;
        }
    }
}