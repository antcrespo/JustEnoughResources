package jeresources.json;

import com.google.gson.*;
import jeresources.api.distributions.DistributionBase;
import jeresources.api.distributions.DistributionCustom;
import jeresources.api.messages.RegisterOreMessage;
import jeresources.api.utils.DistributionHelpers;
import jeresources.config.ConfigHandler;
import jeresources.registry.OreRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OreAdapter
{
    public static boolean hasOreEntry()
    {
        return new File(ConfigHandler.getConfigDir(), "ores.json").exists();
    }

    public static boolean readEntrys()
    {
        JsonParser parser = new JsonParser();
        try
        {
            JsonElement base = parser.parse(new FileReader(new File(ConfigHandler.getConfigDir(), "ores.json")));
            if (!base.isJsonArray() || base.getAsJsonArray().size() == 0) return false;
            JsonArray array = base.getAsJsonArray();
            for (int i = 0; i < array.size(); i++)
            {
                JsonObject obj = array.get(i).getAsJsonObject();
                String ore = obj.get("ore").getAsString();
                String distrib = obj.get("distrib").getAsString();
                JsonElement dropsElement = obj.get("drops");
                String drops = dropsElement != null ? dropsElement.getAsString() : "";
                JsonElement silk = obj.get("silktouch");
                boolean silktouch = silk != null && silk.getAsBoolean();

                ItemStack oreStack = new ItemStack(GameRegistry.findBlock(ore.split(":")[0], ore.split(":")[1]));
                List<DistributionHelpers.OrePoint> points = new ArrayList<>();
                for (String point : distrib.split(";"))
                {
                    String[] split = point.split(",");
                    if (split.length == 2)
                        points.add(new DistributionHelpers.OrePoint(Integer.parseInt(split[0]), Float.parseFloat(split[1])));
                }
                DistributionBase distribution = new DistributionCustom(DistributionHelpers.getDistributionFromPoints(points.toArray(new DistributionHelpers.OrePoint[points.size()])));
                List<ItemStack> dropsList = new ArrayList<>();
                if (!drops.isEmpty())
                {
                    for (String drop : drops.split(","))
                    {
                        String[] dropSplit = drop.split(":");
                        int meta = dropSplit.length < 4 ? 0 : Integer.parseInt(dropSplit[3]);
                        int size = dropSplit.length < 3 ? 1 : dropSplit.length < 4 ? Integer.parseInt(dropSplit[2]) : Integer.parseInt(dropSplit[3]);
                        dropsList.add(new ItemStack(GameRegistry.findItem(dropSplit[0], dropSplit[1]), size, meta));
                    }
                }

                OreRegistry.registerOre(new RegisterOreMessage(oreStack, distribution, silktouch, dropsList.toArray(new ItemStack[dropsList.size()])));
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
