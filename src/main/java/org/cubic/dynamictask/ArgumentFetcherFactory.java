package org.cubic.dynamictask;

public interface ArgumentFetcherFactory<A extends ArgumentFetcher> {

    A newArgumentFetcher(Object[] args, Class<?>[] types);
}
