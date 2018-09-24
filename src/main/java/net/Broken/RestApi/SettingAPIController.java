package net.Broken.RestApi;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.Settings.GetSettingsData;
import net.Broken.RestApi.Data.Settings.Value;
import net.Broken.Tools.SettingsUtils;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SettingAPIController {
    private Logger logger = LogManager.getLogger();



    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<GetSettingsData>> getSettings(@CookieValue("token") String token, @CookieValue("guild") String guild){
        SettingsUtils settingUtils = SettingsUtils.getInstance();
        if(settingUtils.checkPermission(token, guild)){
            Guild jdaGuild = MainBot.jda.getGuildById(guild);
            return new ResponseEntity<>( settingUtils.extractSettings(jdaGuild), HttpStatus.OK);
        }
        else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
