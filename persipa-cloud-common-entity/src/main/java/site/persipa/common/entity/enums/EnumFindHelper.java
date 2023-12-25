package site.persipa.common.entity.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author persipa
 */
public class EnumFindHelper<E extends Enum<E>, T> {

    protected Map<T, E> map = new HashMap<>();

    public EnumFindHelper(Class<E> clazz, EnumKeyGetter<E, T> keyGetter) {
        EnumSet<E> enumSet = EnumSet.allOf(clazz);
        if (!enumSet.isEmpty()) {
            this.map = enumSet.stream()
                    .collect(Collectors.toMap(keyGetter::getKey, Function.identity(),
                            (k1, k2) -> k1));
        }
    }

    public E find(T key, E defaultValue) {
        return this.map.getOrDefault(key, defaultValue);
    }
}
