package net.Broken;

import net.Broken.Commands.Move;
import net.Broken.Commands.Music;
import net.Broken.Tools.AntiSpam;
import net.Broken.Tools.Command.CommandParser;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.Moderateur;
import net.Broken.Tools.PrivateMessage;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ExceptionEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Bot Listener
 */
public class BotListener extends ListenerAdapter {
    private AntiSpam antispam=new AntiSpam();
    private Moderateur modo = new Moderateur();
    private Logger logger = LogManager.getLogger();


    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Connection succees");
    }


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        logger.info(event.getUser().getName()+ "join the guild, move it!");
        new Move().exc(event.getMember(),event.getJDA().getRolesByName("Newbies",true),false,event.getGuild(),event.getGuild().getManager());
        TextChannel chanel = event.getGuild().getTextChannelsByName("accueil", true).get(0);
        if(chanel != null)
        chanel.sendMessage("Salut "+event.getUser().getAsMention()+"! Ecris ton nom, prénom, ta promotion et ton groupe ici! Un admin te donnera accées a ton groupe!").complete();
        MainBot.roleFlag = false;
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        logger.debug(event.getUser().getName()+" leave a role");
        if(!MainBot.roleFlag){

            if(event.getMember().getRoles().size() == 0){
                logger.info(event.getUser().getName()+ "have no roles, move it!");
                new Move().exc(event.getMember(),event.getGuild().getRolesByName("Populace",true),false,event.getGuild(),event.getGuild().getManager());
                MainBot.roleFlag = false;
            }
        }
        else
        {
            logger.debug("ignore it");
            MainBot.roleFlag = false;
        }

    }



    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        super.onGuildVoiceLeave(event);
        if(event.getGuild().getAudioManager().isConnected())
        {
            logger.debug("User disconnected from voice channel.");

            if(event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 1){
                logger.debug("I'm alone, close audio connection.");

                AudioM.getInstance(event.getGuild()).stop();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //                                                      ----------------------Test pour eviter eco de commande-------------------------


        try{
            if (event.getMessage().getContentRaw().startsWith("//") && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                //On a detecter que c'etait une commande
                //System.out.println(event.getMessage().getContent());
                MainBot.handleCommand(new CommandParser().parse(event.getMessage().getContentRaw(), event));

            }
            else if (!event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            {

                if(!event.isFromType(ChannelType.PRIVATE)) {
                    if (!event.getTextChannel().getName().equals("le_dongeon")) {
                        Guild serveur = event.getGuild();
                        GuildManager guildManager = serveur.getManager();
                        Member user = event.getMember();

                        // appel de la methode d'analyse de message de "Moderateur"
                        if (!event.getAuthor().getName().equals("Aethex") && event.getMessage().getContentRaw().length() > 0) {

                            if (modo.analyse(user, serveur, guildManager, event) == 1) {
                                antispam.extermine(user, serveur, guildManager, true, event);
                            }
                        } else if (event.getMessage().getContentRaw().length() == 0)
                            logger.error("Image detected, ignoring it.");

                    }
                }


            }
        }catch (Exception e){
            logger.catching(e);

            if(event.isFromType(ChannelType.PRIVATE))
                PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getInternalError(), logger);
            else
                event.getTextChannel().sendMessage(EmbedMessageUtils.getInternalError()).queue();
        }



    }

}
