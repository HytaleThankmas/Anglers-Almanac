package dev.rm20.anglersalmanac.Utils;

import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import dev.rm20.anglersalmanac.AnglersAlmanac;
import org.joml.Vector3d;

import java.util.ArrayList;

public class TriggerVolumeUtils {


    public static String getFishingZone(TriggerVolumeManager manager, Vector3d location) {
        if(location == null || manager == null) {
            return null;
        }

        manager.getSpatialIndex().applyPendingChanges();

        var candidates = new ArrayList<VolumeEntry>();
        manager.getSpatialIndex().collectCandidates(location, candidates);

        for (VolumeEntry volume : candidates) {
            if (volume.isEnabled() && volume.getShape().contains(volume.getPosition(), location)) {

                String zoneValue = volume.getRawTags().get("FishingZone");
                //AnglersAlmanac.LOGGER.atInfo().log(zoneValue);
                if (zoneValue != null) {
                    return zoneValue;
                }
            }
        }

        return null;
    }
}
