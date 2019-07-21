package gg.sep.alyx.util.result;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Contains the error value of a {@link Result}.
 *
 * @param <T> Type of the success value of the result.
 * @param <E> Type of the error value of the result.
 */
public final class Err<T, E> implements Result<T, E> {

    public final E error;

    private Err(final E error) {
        this.error = error;
    }

    /**
     * Constructs a new {@link Err} result with the provided error value.
     * @param error Error value.
     * @param <T> Type of the success value.
     * @param <E> Type of the error value.
     * @return New {@link Err} result with the provided error value.
     */
    public static <T, E> Err<T, E> of(final E error) {
        Objects.requireNonNull(error);
        return new Err<>(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOk() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErr() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> getOk() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<E> getErr() {
        return Optional.of(error);
    }

    @Override
    public T unwrap() throws ResultException {
        throw new ResultException(String.format("%s", error));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E unwrapErr() throws ResultException {
        return error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T unwrapOr(final T optb) {
        return optb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T unwrapOrElse(final Function<E, T> op) {
        return op.apply(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T expect(final String msg) throws ResultException {
        throw new ResultException(String.format("%s: %s", msg, error));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E expectErr(final String msg) throws ResultException {
        return error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> and(final Result<U, E> res) {

        return (Result<U, E>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> andThen(final Function<T, Result<U, E>> op) {
        return (Result<U, E>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> map(final Function<T, U> op) {
        return (Result<U, E>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F> Result<T, F> mapErr(final Function<E, F> op) {
        return Err.of(op.apply(error));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> U mapOrElse(final Function<E, U> fallback, final Function<T, U> op) {
        return fallback.apply(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F> Result<T, F> or(final Result<T, F> res) {
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F> Result<T, F> orElse(final Function<E, Result<T, F>> op) {
        return op.apply(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                throw new NoSuchElementException("No elements contained in Err.");
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Err) {
            return ((Err<?, ?>) obj).error.equals(error);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(error);
    }
}
