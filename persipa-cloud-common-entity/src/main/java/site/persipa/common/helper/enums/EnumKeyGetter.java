package site.persipa.common.helper.enums;

/**
 * @author persipa
 */
public interface EnumKeyGetter<E extends Enum<E>, T> {

    T getKey(E enumValue);

}
