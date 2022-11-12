package the.kis.devs.discordbot.permission;

import net.dv8tion.jda.api.entities.Member;
import the.kis.devs.discordbot.util.Globals;

/**
 * @author _kisman_
 * @since 14:52 of 23.06.2022
 */
public interface IPermission extends Globals {
    boolean valid(Member member);
}
