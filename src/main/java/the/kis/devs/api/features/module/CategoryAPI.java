package the.kis.devs.api.features.module;

import com.kisman.cc.features.module.Category;

/**
 * @author _kisman_
 * @since 17:41 of 08.06.2022
 */
public enum CategoryAPI {
    COMBAT(Category.COMBAT),
    CLIENT(Category.CLIENT),
    MOVEMENT(Category.MOVEMENT),
    PLAYER(Category.PLAYER),
    RENDER(Category.RENDER),
    MISC(Category.MISC),
    EXPLOIT(Category.EXPLOIT);
    
    public final Category category;

    CategoryAPI(Category category) {
        this.category = category;
    }
}
