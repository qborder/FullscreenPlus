package net.itzplugins.fullscreenplus.fabric;

import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.fabricmc.api.ClientModInitializer;

/**
 * Main class for fabric version of Concentration
 *
 * @author DeeChael
 */
public class FullscreenPlusFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FullscreenPlus.init();
    }

}
