package buttle.apptpl.common.framework.common.util;
import buttle.apptpl.common.framework.common.vo.Menu;

import java.util.*;
import java.util.stream.Collectors;

public class MenuTreeSorter {

    public static List<Menu> sort(List<Menu> menuList) {
        Map<String, List<Menu>> groupedByParentId = menuList.stream()
                .collect(Collectors.groupingBy(menu -> Optional.ofNullable(menu.getUpMenuId()).orElse("ROOT")));

        List<Menu> rootMenus = groupedByParentId.getOrDefault("ROOT", new ArrayList<>());

        rootMenus.sort(Comparator.comparing(Menu::getSortSn));
        for (Menu root : rootMenus) {
            setChildren(root, groupedByParentId);
        }

        return rootMenus;
    }

    private static void setChildren(Menu parent, Map<String, List<Menu>> groupedByParentId) {
        List<Menu> children = groupedByParentId.getOrDefault(parent.getMenuId(), new ArrayList<>());
        children.sort(Comparator.comparing(Menu::getSortSn));
        parent.setChildren(children);
        for (Menu child : children) {
            setChildren(child, groupedByParentId);
        }
    }

}
