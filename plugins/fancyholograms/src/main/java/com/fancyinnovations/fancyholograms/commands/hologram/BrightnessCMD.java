package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.NumberHelper;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BrightnessCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            MessageHelper.error(player, "This command can only be used on display holograms");
            return false;
        }

        if (args.length < 5) {
            MessageHelper.error(player, "You must provide a brightness type and value.");
            return false;
        }

        final var brightnessType = args[3];

        if (!brightnessType.equalsIgnoreCase("block") && !brightnessType.equalsIgnoreCase("sky")) {
            MessageHelper.error(player, "Invalid brightness type, valid options are BLOCK or SKY");
            return false;
        }

        final var parsedNumber = NumberHelper.parseInt(args[4]);

        if (parsedNumber.isEmpty()) {
            MessageHelper.error(player, "Invalid brightness value.");
            return false;
        }

        final var brightnessValue = parsedNumber.get();

        if (brightnessValue < 0 || brightnessValue > 15) {
            MessageHelper.error(player, "Invalid brightness value, must be between 0 and 15");
            return false;
        }

        final var currentBrightness = displayData.getBrightness();
        final var blockBrightness = brightnessType.equalsIgnoreCase("block") ? brightnessValue :
                currentBrightness == null ? 0 : currentBrightness.getBlockLight();
        final var skyBrightness = brightnessType.equalsIgnoreCase("sky") ? brightnessValue :
                currentBrightness == null ? 0 : currentBrightness.getSkyLight();

        displayData.setBrightness(new Display.Brightness(blockBrightness, skyBrightness));

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Changed " + brightnessType.toLowerCase() + " brightness to " + brightnessValue);
        return true;
    }
}
