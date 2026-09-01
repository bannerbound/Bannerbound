package me.bannerbound.com;

import com.mojang.logging.LogUtils;
import me.bannerbound.com.api.network.BannerboundNetwork;
import me.bannerbound.com.api.registries.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Bannerbound.MODID)
public class Bannerbound {
    public static final String MODID = "bannerbound";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Bannerbound(IEventBus modEventBus, ModContainer modContainer) {
        Registry.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, BannerboundServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, BannerboundClientConfig.SPEC);

        modEventBus.register(BannerboundNetwork.class);
    }
}
