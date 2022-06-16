package com.matyrobbrt.igde.network.message.tag;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public record TagData(BooleanHolder replace, Collection<TagEntry> added, Collection<TagEntry> removed) {

    public static TagData immutable(boolean replace, Collection<TagEntry> added, Collection<TagEntry> removed) {
        return new TagData(new BooleanHolder() {
            @Override
            public void set(boolean value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean get() {
                return replace;
            }
        }, Set.copyOf(added), Set.copyOf(removed));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagData tagData = (TagData) o;
        return replace.get() == tagData.replace.get() && areCollectionsEqual(tagData.added, added) && areCollectionsEqual(tagData.removed, removed);
    }

    public static <T> boolean areCollectionsEqual(Collection<T> c1, Collection<T> c2) {
        if (c1 == c2) return true;
        if (c1 == null || c2 == null || c1.size() != c2.size()) {
            return false;
        }
        Collection<T> tmp = new ArrayList<>(c1);
        for (T item : c2) {
            if (!tmp.remove(item)) {
                return false;
            }
        }
        return tmp.isEmpty();
    }

    public TagData copy() {
        return new TagData(new BooleanHolder() {
            private boolean val = replace().get();
            @Override
            public void set(boolean value) {
                val = value;
            }

            @Override
            public boolean get() {
                return val;
            }
        }, Sets.newHashSet(added), Sets.newHashSet(removed));
    }

    @Override
    public int hashCode() {
        return Objects.hash(replace, added, removed);
    }

    public interface BooleanHolder {
        void set(boolean value);
        boolean get();
    }
}
