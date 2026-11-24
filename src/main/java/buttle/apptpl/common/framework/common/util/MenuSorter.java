package buttle.apptpl.common.framework.common.util;
import buttle.apptpl.common.framework.common.vo.Menu;
import java.util.*;

public class MenuSorter {

    public static List<Menu> sort(List<Menu> menuList) {
        Map<String, List<Menu>> menuMap = new HashMap<>();
        List<Menu> rootMenus = new ArrayList<>();

        for (Menu menu : menuList) {
            if (menu.getUpMenuId() == null) {
                rootMenus.add(menu);
            } else {
                menuMap.computeIfAbsent(menu.getUpMenuId(), k -> new ArrayList<>()).add(menu);
            }
        }

        rootMenus.sort(Comparator.comparing(Menu::getSortSn));
        List<Menu> sortedList = new ArrayList<>();
        for (Menu root : rootMenus) {
            sortedList.add(root);
            traverseAndSort(root.getMenuId(), menuMap, sortedList);
        }

        return sortedList;
    }

    private static void traverseAndSort(String parentId, Map<String, List<Menu>> menuMap, List<Menu> sortedList) {
        List<Menu> children = menuMap.get(parentId);
        if (children != null) {
            children.sort(Comparator.comparing(Menu::getSortSn));
            for (Menu child : children) {
                sortedList.add(child);
                traverseAndSort(child.getMenuId(), menuMap, sortedList);
            }
        }
    }
}
