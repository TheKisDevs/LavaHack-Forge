package org.cubic.dynamictask;

public interface ICustomAbstractTask<R, A extends ArgumentFetcher> {

    R call(A arg);
}
