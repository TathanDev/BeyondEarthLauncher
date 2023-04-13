package fr.tathan.launcher.game;

import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.openlauncherlib.NewForgeVersionDiscriminator;
import fr.theshark34.openlauncherlib.minecraft.GameType;

public class MinecraftInfos {
    public static final String GAME_VERSION = "1.19.2";
    public static final ForgeVersionBuilder.ForgeVersionType FORGE_VERSION_TYPE = ForgeVersionBuilder.ForgeVersionType.NEW;

    public static final GameType OLL_GAME_TYPE = GameType.V1_13_HIGHER_FORGE;
    public static final NewForgeVersionDiscriminator OLL_FORGE_DISCRIMINATOR = new NewForgeVersionDiscriminator(
            "36.2.2",
            MinecraftInfos.GAME_VERSION,
            "20210115.111550"
    );
    public static final String FORGE_VERSION = "1.19.2-43.2.1";
    public static  final String SERVER_NAME = "BeyondEarth";

    public static  final String MOD_INFOS = "https://raw.githubusercontent.com/TathanDev/BeyondEarthLauncher/main/src/main/java/fr/tathan/launcher/game/cursefile.json";



}

