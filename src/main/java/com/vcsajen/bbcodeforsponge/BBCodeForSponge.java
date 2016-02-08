package com.vcsajen.bbcodeforsponge;

import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by VcSaJen on 08.02.2016.
 */
@Plugin(id = "bbcodeforsponge", name = "BBCodeForSponge", version = "1.0")
public class BBCodeForSponge {
    @Inject
    private Game game;

    @Listener
    public void onGameInitialization(GameInitializationEvent event)
    {

    }
}
