package com.vcsajen.bbcodeforsponge;

import com.google.inject.Inject;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by VcSaJen on 08.02.2016.
 */
@Plugin(id = "bbcodeforsponge", name = "BBCodeForSponge", version = "1.0")
public class BBCodeForSponge {
    @Inject
    private Game game;

    /*@Listener
    public void onMessageEvent(MessageEvent event, @First Player player){
        if (!player.hasPermission("bbcode")) return;
        if (!event.getMessage().isPresent()) return;
        String msg = event.getOriginalMessage().get().toPlain();

        msg = "Эм, "+msg+", мда.";

        event.setMessage(Text.of(msg));
    }*/

    private String formatWithBBCode(String s)
    {
        String result0;
        TextProcessor processor = BBProcessorFactory.getInstance().createFromResource("com/vcsajen/bbcodeforsponge/bbcode.xml");
        result0=processor.process(s);

        Pattern p = Pattern.compile("onHover=\"show_text\\('(([\\s\\S]*?)<!--END-->)'\\)\">");
        Matcher m = p.matcher(result0);
        Deque<OneMatch> matches = new ArrayDeque<>();
        while (m.find()) {
            matches.push(new OneMatch(m.start(1), m.end(1), m.group(2)));
        }
        while (matches.peek()!=null)
        {
            OneMatch m1 = matches.pop();
            result0 = result0.substring(0, m1.getMatchStart()) + StringEscapeUtils.escapeXml10(m1.getText()) + result0.substring(m1.getMatchEnd());
        }

        return result0;
    }

    @Listener(order = Order.LAST)
    public void chatEvent(final MessageChannelEvent.Chat chat, @First final Player player){
        if (!player.hasPermission("bbcode")) return;

        String msg = chat.getRawMessage().toPlain();

        String msg1 = chat.getMessage().get().toPlain();
        String msg2 = chat.getOriginalMessage().get().toPlain();
        String msg3 = chat.getChannel().toString();

        //msg = "Эм, "+msg+", мда.";

        msg = formatWithBBCode(msg);



        //Text t = TextSerializers.JSON.deserialize("{text:\"Hover.\",hoverEvent:{action:show_text,value:\"Hello\\nthere.\"}}");

        //String s = TextSerializers.TEXT_XML.serialize(t);

        //player.sendMessage(Text.of(s));
        //player.sendMessage(TextSerializers.TEXT_XML.deserialize(s));

        //player.sendMessage(Text.of(msg));
        chat.setMessage(TextSerializers.TEXT_XML.deserialize("&lt;"+player.getName()+"&gt; "+msg));
        //chat.setMessage(TextSerializers.TEXT_XML.deserialize("&lt;"+player.getName()+"&gt; <span onHover=\"show_text('&lt;u&gt;LOL&#xD;&#xA;LOL&lt;/u&gt;')\">[+++]</span>"));
        //chat.setMessage(t);
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent event)
    {

    }

    private class OneMatch
    {
        private int matchStart;
        private int matchEnd;
        private String text;

        public OneMatch(int matchStart, int matchEnd, String text) {
            this.matchStart = matchStart;
            this.matchEnd = matchEnd;
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getMatchStart() {
            return matchStart;
        }

        public void setMatchStart(int matchStart) {
            this.matchStart = matchStart;
        }

        public int getMatchEnd() {
            return matchEnd;
        }

        public void setMatchEnd(int matchEnd) {
            this.matchEnd = matchEnd;
        }

        public int getLength() {
            return matchEnd-matchStart;
        }

        //WARNING! Will not preserve length when changing start
        public void setLength(int length) {
            this.matchEnd = matchStart+length;
        }

    }
}













































