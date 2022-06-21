package ru.aasmc.unsafe_sparkdata.util;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class OrderedBag<T> {
    private List<T> list;

    public OrderedBag(Object[] args) {
        this.list = new ArrayList(asList(args));
    }

    public T takeAndRemove() {
        return list.remove(0);
    }

    public int size() {
        return list.size();
    }
}
