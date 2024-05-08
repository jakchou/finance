package com.defaulttenant.hotswap.util;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 *
 */
public class DependenceArrayListWrapper<E> extends ArrayList<E> {
    protected transient List target;
    protected boolean targetDeprecatedType = true;

    public DependenceArrayListWrapper(List list) {
        this.target = list;
    }

    public DependenceArrayListWrapper(List list, boolean targetDeprecatedType) {
        this.target = list;
        this.targetDeprecatedType = targetDeprecatedType;
    }

    public boolean isTargetDeprecatedType() {
        return this.targetDeprecatedType;
    }

    @Override
    public int size() {
        return target.size();
    }

    @Override
    public boolean isEmpty() {
        return target.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return target.contains(valueTypeConvert(o, true));
    }

    @Override
    public int indexOf(Object o) {
        return target.indexOf(valueTypeConvert(o, true));
    }

    @Override
    public int lastIndexOf(Object o) {
        return target.lastIndexOf(valueTypeConvert(o, true));
    }

    @Override
    public Object[] toArray() {
        return ((List) this.clone()).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return ((List<T>) this.clone()).toArray(a);
    }

    @Override
    public E get(int index) {
        return (E) valueTypeConvert(target.get(index), false);
    }

    @Override
    public E set(int index, E element) {
        return (E) valueTypeConvert(target.set(index, valueTypeConvert(target.get(index), true)), false);
    }

    @Override
    public boolean add(E e) {
        return target.add(valueTypeConvert(e, true));
    }

    @Override
    public void add(int index, E element) {
        target.add(index, valueTypeConvert(element, true));
    }

    @Override
    public E remove(int index) {
        return (E) valueTypeConvert(target.remove(index), false);
    }

    @Override
    public boolean remove(Object o) {
        return target.remove(valueTypeConvert(o, true));
    }

    @Override
    public void clear() {
        target.clear();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return target.addAll(listCopy(c, true));
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return target.addAll(index, listCopy(c, true));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return target.removeAll(listCopy(c, true));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return target.retainAll(listCopy(c, true));
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return listCopy(target, false).listIterator(index);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listCopy(target, false).listIterator();
    }

    @Override
    public Iterator<E> iterator() {
        return listCopy(target, false).iterator();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return listCopy(target, false).subList(fromIndex, toIndex);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        target.forEach(e-> action.accept((E) valueTypeConvert(e, false)));
    }

    @Override
    public Spliterator<E> spliterator() {
        return listCopy(target, false).spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return target.removeIf(e->filter.test((E) valueTypeConvert(e, false)));
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        target.replaceAll(e->operator.apply((E) valueTypeConvert(e, false)));
    }

    @Override
    public void sort(Comparator<? super E> c) {
        target.sort((e1, e2)->c.compare((E) valueTypeConvert(e1, false),
                (E) valueTypeConvert(e2, false)));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return target.containsAll(listCopy(c, true));
    }

    @Override
    public Stream<E> stream() {
        return listCopy(target, false).stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return listCopy(target, false).parallelStream();
    }

    private List listCopy(Collection source, boolean toTargetType) {
        List result = new ArrayList(source.size());
        if (!CollectionUtils.isEmpty(source)) {
            for (Object o : source) {
                result.add(valueTypeConvert(o, toTargetType));
            }
        }

        return result;
    }

    private Object valueTypeConvert(Object obj, boolean toTargetType) {
        if ((toTargetType && targetDeprecatedType) || (!toTargetType && !targetDeprecatedType)) {
            return LogicCallUtils.toDependenceType(obj);
        } else {
            return LogicCallUtils.fromDependenceType(obj);
        }
    }
}
