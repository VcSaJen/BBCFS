package com.vcsajen.bbcodeforsponge;

import com.google.inject.Inject;
import com.vcsajen.bbcodeforsponge.exception.AssetNotFoundException;
import org.apache.commons.lang3.mutable.MutableObject;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.kefirsf.bb.conf.Code;
import org.kefirsf.bb.conf.Configuration;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.kefirsf.bb.ConfigurationFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by VcSaJen on 08.02.2016.
 */
@Plugin(id = "com.vcsajen.bbcodeforsponge", name = "BBCodeForSponge", version = "1.0", authors={"VcSaJen"}, description = "Plugin for formatting chat in BBCode", dependencies = {}, url = "http://example.com/")
public class BBCodeForSponge {
    @Inject
    private Game game;

    @Inject
    private Logger logger;

    @Inject
    PluginContainer plugin;

    /*@Listener
    public void onMessageEvent(MessageEvent event, @First Player player){
        if (!player.hasPermission("bbcode")) return;
        if (!event.getMessage().isPresent()) return;
        String msg = event.getOriginalMessage().get().toPlain();

        msg = "Эм, "+msg+", мда.";

        event.setMessage(Text.of(msg));
    }*/

    private Map<String, Set<Code>> permissableCodes;
    private Set<Code> allPermCodes;
    private Configuration bbConfiguration;

    String[][] permissions = {{"b", "bold"},
            {"i", "i"},
            {"u", "u"},
            {"s", "s"},
            {"color", "color"},
            {"quote", "quote", "quote2"},
            {"url", "url1", "url2", "url3", "url4", "url5", "url6"},
            {"spoiler", "spoiler1", "spoiler2"},
            {"pre", "code"}};

    private Asset getAsset(String name)
    {
        return plugin.getAsset(name).orElseThrow(() -> new AssetNotFoundException(String.format("Asset %s is not found!", name)));
    }

    private void initializeBBCodeParser()
    {
        try {
            bbConfiguration = ConfigurationFactory.getInstance().create(this.getAsset("bbcode.xml").getUrl().openStream());

            Set<Code> codes = new HashSet<>(bbConfiguration.getRootScope().getCodes());
            permissableCodes = new HashMap<>();
            allPermCodes = new HashSet<>();

            for (String[] perm: permissions) {
                Set<Code> codesForThisPerm = new HashSet<>();
                for (int i = 1; i<perm.length; i++) {
                    final int ii = i;
                    for (Code code: codes) {
                        if (code.getName().equals(perm[ii])) {
                            codesForThisPerm.add(code);
                            allPermCodes.add(code);
                        }
                    }
                }
                permissableCodes.put(perm[0], codesForThisPerm);
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String formatWithBBCode(String s, Player player)
    {
        String result0;

        Set<Code> codes = new HashSet<>(bbConfiguration.getRootScope().getCodes());

        codes.removeAll(allPermCodes);

        for (String[] perm: permissions) {
            if (player.hasPermission("bbcodeforsponge.bbcode."+perm[0]))
                codes.addAll(permissableCodes.get(perm[0]));
        }
        bbConfiguration.getRootScope().setCodes(codes);

        TextProcessor processor = BBProcessorFactory.getInstance().create(bbConfiguration);
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

    /**
     * Try to simulate niceness by trying to extract header (time, nickname, group, etc)
     * @param fullFormattedMsg Message formed from plugin intervention
     * @param originalBody Original text typed by user
     * @return Header, or empty Text if failed
     */
    private Text wrestHeaderOut(Text fullFormattedMsg, Text originalBody)
    {
        String original = TextSerializers.TEXT_XML.serialize(originalBody);
        String serFormatted = TextSerializers.TEXT_XML.serialize(fullFormattedMsg);

        original = original.replaceAll("<.*?>", "");

        String tmp = "";
        int i = serFormatted.length()-1;
        int k = 0;
        while ((serFormatted.charAt(i) == '>' || k>0) && i>=0) //Удаляем задние теги
        {
            if (serFormatted.charAt(i) == '>') k++;
            if (serFormatted.charAt(i) == '<') k--;
            tmp = serFormatted.charAt(i) + tmp;
            i--;
        }
        serFormatted = serFormatted.substring(0, i+1);

        if (serFormatted.endsWith(original))
        {
            serFormatted = serFormatted.substring(0, serFormatted.length()-original.length());
            return TextSerializers.TEXT_XML.deserialize(serFormatted + tmp);
        }
        return Text.EMPTY;
    }

    @Listener(order = Order.LATE)
    public void chatEvent(final MessageChannelEvent.Chat chat, @First final Player player){
        //if (!player.hasPermission("bbcodeforsponge.use")) return;

        /*Text mytext = chat.getFormatter().getBody().format();
        for (Text t: mytext.withChildren()) {
            player.sendMessage(Text.of("Content: "+((LiteralText)t).getContent()));
            //player.sendMessage(t);
        }*/

        long start = System.nanoTime();
        //--------------------------
        boolean playingNice;

        /*logger.debug("Header: ");
        chat.getFormatter().getHeader().getAll().forEach(t -> {logger.debug(t.getClass().getName() + " => " + t.getParameters().toString());});

        logger.debug("Body: ");
        chat.getFormatter().getBody().getAll().forEach(t -> {logger.debug(t.getClass().getName() + " => " + t.getParameters().toString());});

        logger.debug("Footer: ");
        chat.getFormatter().getFooter().getAll().forEach(t -> {logger.debug(t.getClass().getName() + " => " + t.getParameters().toString());});
*/
        MutableObject<Text> msg_ = new MutableObject<>();
        chat.getFormatter().getBody().forEach(MessageEvent.DefaultBodyApplier.class, applier -> msg_.setValue(Text.of(applier.getParameter("body"))));


        String msg;
        Text header = Text.EMPTY;
        //String msg = chat.getFormatter().getBody().format().toPlain();
        playingNice = msg_.getValue()!=null;
        if (playingNice)
            msg = msg_.getValue().toPlain(); //Nice
        else
        {
            header = wrestHeaderOut(chat.getFormatter().getBody().format(), chat.getRawMessage());
            if (header.isEmpty())
                msg = chat.getFormatter().getBody().format().toPlain();
            else msg = chat.getRawMessage().toPlain(); //Brute
        }

        /*String msg1 = chat.getMessage().toPlain();
        String msg2 = chat.getOriginalMessage().toPlain();
        String msg3 = chat.getRawMessage().toString();
        player.sendMessage(Text.of("Message: ", msg, "; OriginalMessage: ", msg2, "; RawMessage: ", msg3));*/

        //msg = "Эм, "+msg+", мда.";

        msg = formatWithBBCode(msg, player);


        //Text t = TextSerializers.JSON.deserialize("{text:\"Hover.\",hoverEvent:{action:show_text,value:\"Hello\\nthere.\"}}");

        //String s = TextSerializers.TEXT_XML.serialize(t);

        //player.sendMessage(Text.of(s));
        //player.sendMessage(TextSerializers.TEXT_XML.deserialize(s));

        //player.sendMessage(Text.of(msg));

        //chat.setMessage(TextSerializers.TEXT_XML.deserialize("&lt;"+player.getName()+"&gt; "+msg));

        Text formattedMsg = TextSerializers.TEXT_XML.deserialize(msg);

        if (playingNice)
            chat.getFormatter().getBody().forEach(MessageEvent.DefaultBodyApplier.class, applier -> applier.setParameter("body", formattedMsg));
        else
        {
            chat.setMessage(Text.join(header, formattedMsg));
            if (header.isEmpty())
                logger.warn("Some of installed chat plugins doesn't support new MessageEvent appliers! Manual extracting of header failed! Things can be broken.");
        }

        long end = System.nanoTime();
        logger.debug(Long.toString(end-start));
        //chat.setMessage(TextSerializers.TEXT_XML.deserialize("&lt;"+player.getName()+"&gt; <span onHover=\"show_text('&lt;u&gt;LOL&#xD;&#xA;LOL&lt;/u&gt;')\">[+++]</span>"));
        //chat.setMessage(t);
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent event)
    {
        PermissionService ps = Sponge.getServiceManager().provide(PermissionService.class).get();
        for (String[] perm: permissions) {
            ps.newDescriptionBuilder(this).ifPresent(db -> db
                    .assign(PermissionDescription.ROLE_USER, true)
                    .description(Text.of("Allows using BBCode tag ["+perm[0]+"] in chat"))
                    .id("bbcodeforsponge.bbcode."+perm[0])
                    .register());
        }
        ps.newDescriptionBuilder(this).ifPresent(db -> db
                .assign(PermissionDescription.ROLE_USER, true)
                .description(Text.of("Allows formatting chat with BBCode"))
                .id("bbcodeforsponge.use")
                .register());
        initializeBBCodeParser();
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













































