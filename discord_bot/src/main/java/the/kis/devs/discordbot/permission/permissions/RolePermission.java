package the.kis.devs.discordbot.permission.permissions;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import the.kis.devs.discordbot.permission.IPermission;

/**
 * @author _kisman_
 * @since 14:54 of 23.06.2022
 */
public class RolePermission implements IPermission {
    public final String roleID;

    public RolePermission(String roleID) {
        this.roleID = roleID;
    }

    @Override
    public boolean valid(Member member) {
        for(Role role : member.getRoles()) {
            if(role.getId().equals(roleID)) {
                return true;
            }
        }

        return false;
    }
}
