package org.cubic.dynamictask;

public interface IAbstractTask<R> {

    R call(ArgumentFetcher arg);
}
