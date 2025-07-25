package com.fancyinnovations.fancyholograms.commands.hologram;

import com.google.common.base.Enums;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class TextAlignmentCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.text_alignment"))) {
            MessageHelper.error(player, "You don't have the required permission to edit a hologram");
            return false;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        final var alignment = Enums.getIfPresent(TextDisplay.TextAlignment.class, args[3].toUpperCase(Locale.ROOT)).orNull();

        if (alignment == null) {
            MessageHelper.error(player, "Could not parse text alignment");
            return false;
        }

        if (textData.getTextAlignment() == alignment) {
            MessageHelper.warning(player, "This hologram already has this text alignment");
            return false;
        }

        final var copied = textData.copy(textData.getName());
        copied.setTextAlignment(alignment);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT_ALIGNMENT)) {
            return false;
        }

        if (textData.getTextAlignment() == alignment) {
            MessageHelper.warning(player, "This hologram already has this text alignment");
            return false;
        }

        textData.setTextAlignment(((TextHologramData) copied).getTextAlignment());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Changed text alignment");
        return true;
    }
}
