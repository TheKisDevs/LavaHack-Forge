package the.kis.devs.api.util.chat.cubic;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.ReflectUtil;
import com.kisman.cc.util.chat.cubic.AbstractChatMessage;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import the.kis.devs.api.features.module.ModuleManagerAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * @author _kisman_
 * @since 20:44 of 15.06.2022
 */
public class ChatUtilityAPI {
    static final Map<Class<?>, Module> moduleMapping;

    private static final AbstractChatMessageAPI COMPLETE = new ChatAPI(ChatUtility.complete());
    private static final AbstractChatMessageAPI ERROR = new ChatAPI(ChatUtility.error());
    private static final AbstractChatMessageAPI INFO = new ChatAPI(ChatUtility.info());
    private static final AbstractChatMessageAPI MESSAGE = new ChatAPI(ChatUtility.message());
    private static final AbstractChatMessageAPI WARNING = new ChatAPI(ChatUtility.warning());

    static {
        moduleMapping = new HashMap<>(ModuleManagerAPI.getModules().size());
        initMappings();
    }

    private static void initMappings(){
        int size = Kisman.instance.moduleManager.modules.size();
        for(int i = 0; i < size; i++){
            Module m = Kisman.instance.moduleManager.modules.get(i);
            moduleMapping.put(m.getClass(), m);
        }
    }

    public static AbstractChatMessage complete(){
        COMPLETE.updateCaller(ReflectUtil.getCallerClass());
        return COMPLETE;
    }

    public static AbstractChatMessage error(){
        ERROR.updateCaller(ReflectUtil.getCallerClass());
        return ERROR;
    }

    public static AbstractChatMessage info(){
        INFO.updateCaller(ReflectUtil.getCallerClass());
        return INFO;
    }

    public static AbstractChatMessage message(){
        MESSAGE.updateCaller(ReflectUtil.getCallerClass());
        return MESSAGE;
    }

    public static AbstractChatMessage warning(){
        WARNING.updateCaller(ReflectUtil.getCallerClass());
        return WARNING;
    }
}
