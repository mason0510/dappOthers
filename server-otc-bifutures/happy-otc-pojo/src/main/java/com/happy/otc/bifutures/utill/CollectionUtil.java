package com.happy.otc.bifutures.utill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\11\16 0016.
 */
public class CollectionUtil {
    public CollectionUtil() {
    }

    public static <E> List<E> asList(E... elements) {
        ArrayList list = new ArrayList(elements.length);
        if(elements.length > 0) {
            Object[] arr$ = elements;
            int len$ = elements.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Object e = arr$[i$];
                list.add(e);
            }
        }

        return list;
    }
}
