package com.kisman.cc.oldclickgui.csgo.layout;

import com.kisman.cc.oldclickgui.csgo.AbstractComponent;

import java.util.List;

public interface ILayoutManager {
    int[] getOptimalDiemension(List<AbstractComponent> components, int maxWidth);

    Layout buildLayout(List<AbstractComponent> components, int width, int height);
}