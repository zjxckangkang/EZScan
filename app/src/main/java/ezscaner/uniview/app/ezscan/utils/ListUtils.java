package ezscaner.uniview.app.ezscan.utils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 颜德情
 * @version 1.0
 * @date 2014-9-2
 */
public class ListUtils {
    public static <T> boolean isListEmpty(List<T> ts) {
        if (null == ts || 0 == ts.size()) {
            return true;
        } else {
            return false;
        }

    }

    public static <T> List<T> reverseList(List<T> ts) {
        List<T> newTs = new ArrayList<>();
        for (int i = ts.size()-1; i >= 0; i--) {
            newTs.add(ts.get(i));
        }
        return newTs;
    }
}
