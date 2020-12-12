package com.charlesbot.slack;

import com.google.common.collect.Multimap;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MultimapCollector {

    public static <K, V, A extends Multimap<K, V>> Collector<Entry<K, V>, A, A> toMultimap(Supplier<A> supplier) {
        return Collector.of(supplier, (acc, entry) -> acc.put(entry.getKey(), entry.getValue()), (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        });
    }
}
