package dev.rm20.anglersalmanac.Events;

import com.hypixel.hytale.server.core.event.events.BootEvent;
import dev.rm20.anglersalmanac.AnglersAlmanac;
import dev.rm20.anglersalmanac.Registration.EventInfo;
import dev.rm20.anglersalmanac.Utils.Intergration.MMOSkillTree;
import dev.rm20.anglersalmanac.Utils.Intergration.ThankmasVaultHook;

@EventInfo(BootEvent.class)
public class OnPluginSetupEvent {

    public static void handle(BootEvent event) {
        if(AnglersAlmanac.getInstance().skillTree==null){
            AnglersAlmanac.getInstance().skillTree= new MMOSkillTree();
            AnglersAlmanac.getInstance().ECONOMY_HOOK = new ThankmasVaultHook();
        }
    }
}
