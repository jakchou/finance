package com.defaulttenant.hotswap.util;

import java.util.function.Supplier;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import com.defaulttenant.hotswap.exception.*;

public class LogicCallUtils {
    public static <T> T callWithError(Supplier<T> action, boolean handleError) {
        try {
            return (T) action.get();
        } catch (RuntimeException e) {
            if (handleError) {
                throw new LcapAbortException(e);
            } else {
                return (T) new com.defaulttenant.hotswap.domain.ui.Error(e.getClass().getName(), e.getMessage());
            }
        }
    }

    public static void abort() {
        throw new LcapAbortException();
    }

    public static <T> T fromDependenceType(Object obj) {
        Class<T> tClass = null;
        if (obj instanceof Integer) {
            tClass = (Class<T>) Long.class;
        } else if (obj instanceof Double) {
            tClass = (Class<T>) BigDecimal.class;
        } else if (obj instanceof List) {
            tClass = (Class<T>) List.class;
        } else if (obj instanceof Map) {
            tClass = (Class<T>) Map.class;
        }

        return fromDependenceType(obj, tClass);
    }

    public static <T> T fromDependenceType(Object obj, Class<T> tClass) {
        if (obj instanceof Integer) {
            return tClass.cast(((Integer) obj).longValue());
        } else if (obj instanceof Double) {
            return tClass.cast(new BigDecimal(((Double) obj)));
        } else if (obj instanceof DependenceArrayListWrapper) {
            DependenceArrayListWrapper listWrapper = (DependenceArrayListWrapper) obj;
            return (T) (listWrapper.isTargetDeprecatedType() ? new DependenceArrayListWrapper(listWrapper.target, true) : listWrapper.target);
        } else if (obj instanceof List) {
            return (T) new DependenceArrayListWrapper((List) obj, true);
        } else if (obj instanceof DependenceHashMapWrapper) {
             DependenceHashMapWrapper mapWrapper = (DependenceHashMapWrapper) obj;
             return (T) (mapWrapper.isTargetDeprecatedType() ? new DependenceHashMapWrapper<>(mapWrapper.target, true) : mapWrapper.target);
         } else if (obj instanceof Map) {
             return (T) new DependenceHashMapWrapper<>((Map) obj, true);
         }

        return (T) obj;
    }

    public static <T> T toDependenceType(Object obj) {
        Class<T> tClass = null;
        if (obj instanceof Long) {
            tClass = (Class<T>) Integer.class;
        } else if (obj instanceof BigDecimal) {
            tClass = (Class<T>) Double.class;
        } else if (obj instanceof List) {
            tClass = (Class<T>) List.class;
        } else if (obj instanceof Map) {
            tClass = (Class<T>) Map.class;
        }

        return toDependenceType(obj, tClass);
    }

    public static <T> T toDependenceType(Object obj, Class<T> tClass) {
        if (obj instanceof Long) {
            Long longValue = (Long) obj;
            precondition(longValue <= Integer.MAX_VALUE, "Out Of Integer Bounds: %s", obj);
            return tClass.cast(longValue.intValue());
        } else if (obj instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) obj;
            precondition(bigDecimalValue.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) <= 0, "Out Of Integer Bounds: %s", obj);
            return tClass.cast(bigDecimalValue.doubleValue());
        } else if (obj instanceof DependenceArrayListWrapper) {
            DependenceArrayListWrapper listWrapper = (DependenceArrayListWrapper) obj;
            return (T) (listWrapper.isTargetDeprecatedType() ? listWrapper.target : new DependenceArrayListWrapper(listWrapper.target, false));
        } else if (obj instanceof List) {
            return (T) new DependenceArrayListWrapper((List) obj, false);
        } else if (obj instanceof DependenceHashMapWrapper) {
             DependenceHashMapWrapper mapWrapper = (DependenceHashMapWrapper) obj;
             return (T) (mapWrapper.isTargetDeprecatedType() ? mapWrapper.target : new DependenceHashMapWrapper<>(mapWrapper.target, false));
         } else if (obj instanceof Map) {
             return (T) new DependenceHashMapWrapper<>((Map) obj, false);
         }

        return (T) obj;
    }

    private static void precondition(boolean condition, String msg, Object... args) {
        if (!condition) {
            throw new HttpCodeException(500, String.format(msg, args));
        }
    }
}
