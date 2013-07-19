package cubex2.mods.morefurnaces;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cubex2.mods.morefurnaces.blocks.BlockMoreFurnaces;
import cubex2.mods.morefurnaces.items.ItemMoreFurnaces;
import cubex2.mods.morefurnaces.network.PacketHandler;
import cubex2.mods.morefurnaces.proxies.CommonProxy;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION)
@NetworkMod(channels = { ModInformation.CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class MoreFurnaces
{
    public static BlockMoreFurnaces blockFurnaces;

    @SidedProxy(clientSide = "cubex2.mods.morefurnaces.proxies.ClientProxy", serverSide = "cubex2.mods.morefurnaces.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Instance(ModInformation.ID)
    public static MoreFurnaces instance;

    private int blockId;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException
    {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            cfg.load();
            blockId = cfg.getBlock("id", 203).getInt(203);
        } catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "MoreFurnaces has a problem loading it's configuration");
        } finally
        {
            cfg.save();
        }
        ModMetadata md = event.getModMetadata();
        md.autogenerated = false;
        md.description = "Three new furnaces: Iron, Gold, Obsidian and Diamond Furnace.";
        md.authorList = Arrays.asList("CubeX2");
        md.url = "http://www.minecraftforum.net/topic/506109-";
    }

    @EventHandler
    public void load(FMLInitializationEvent evt)
    {
        blockFurnaces = new BlockMoreFurnaces(blockId);
        GameRegistry.registerBlock(blockFurnaces, ItemMoreFurnaces.class, "furnaceBlock");
        for (FurnaceType typ : FurnaceType.values())
        {
            GameRegistry.registerTileEntity(typ.clazz, "CubeX2 " + typ.friendlyName);
            LanguageRegistry.instance().addStringLocalization(typ.name() + "_furnace.name", "en_US", typ.friendlyName);
        }
        FurnaceType.generateRecipes(blockFurnaces);
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);
        proxy.registerRenderInformation();
    }
}
