package gg.sep.alyx.util.result;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link Result} interface and its associated {@link Ok} and {@link Err} variations.
 */
class ResultTest {

    @Test
    void testStaticConstructorEmptyString() {
        assertDoesNotThrow(() -> Ok.of(""));
        assertDoesNotThrow(() -> Err.of(""));
    }

    @Test
    void testStaticConstructorNullThrowsNPE() {
        assertThrows(NullPointerException.class, () -> Ok.of(null));
        assertThrows(NullPointerException.class, () -> Err.of(null));
    }

    @Test
    void testIsOk() {
        assertTrue(Ok.of(2).isOk());
        assertFalse(Err.of(3).isOk());
    }

    @Test
    void testIsErr() {
        assertFalse(Ok.of(2).isErr());
        assertTrue(Err.of(3).isErr());
    }

    @Test
    void testGetOk() {
        assertEquals(Optional.of(2), Ok.of(2).getOk());
        assertEquals(Optional.empty(), Err.of(3).getOk());
    }

    @Test
    void testGetErr() {
        assertEquals(Optional.empty(), Ok.of(2).getErr());
        assertEquals(Optional.of(3), Err.of(3).getErr());
    }

    @Test
    void testUnwrap() {
        assertEquals(2, Ok.of(2).unwrap());

        final ResultException thrown = assertThrows(ResultException.class, () -> Err.of(3).unwrap());
        assertEquals("3", thrown.getMessage());
    }

    @Test
    void testUnwrapErr() {
        final ResultException thrown = assertThrows(ResultException.class, () -> Ok.of(2).unwrapErr());
        assertEquals(thrown.getMessage(), "2");

        assertEquals(3, Err.of(3).unwrapErr());
    }

    @Test
    void testUnwrapOr() {
        assertEquals(2, Ok.of(2).unwrapOr(4));
        assertEquals(5, Err.of(3).unwrapOr(5));
    }

    @Test
    void testUnwrapOrElse() {
        assertEquals(2, Ok.of(2).unwrapOrElse(e -> 4));
        assertEquals(5, Err.of(3).unwrapOrElse(e -> 5));
    }

    @Test
    void testExpect() {
        assertEquals(2, Ok.of(2).expect("expectMsg"));

        final ResultException thrown = assertThrows(ResultException.class,
            () -> Err.of(3).expect("expectMsg"));
        assertEquals("expectMsg: 3", thrown.getMessage());
    }

    @Test
    void testExpectErr() {
        final ResultException thrown = assertThrows(ResultException.class,
            () -> Ok.of(2).expectErr("expectErrMsg"));
        assertEquals("expectErrMsg: 2", thrown.getMessage());

        assertEquals(3, Err.of(3).expectErr("expectErrMsg"));
    }

    /**
     * Test cases from Rust Docs.
     * See: https://doc.rust-lang.org/std/result/enum.Result.html#method.and
     */
    @Test
    void testAnd() {
        final Result<Integer, String> okInt = Ok.of(2);
        final Result<String, String> okStr = Ok.of("foo");
        final Result<String, String> okDiffType = Ok.of("different result type");

        final Result<Integer, String> errInt = Err.of("not a 2");
        final Result<String, String> earlyError = Err.of("early error");
        final Result<String, String> lateError = Err.of("late error");

        assertSame(lateError, okInt.and(lateError));
        assertSame(earlyError, earlyError.and(okStr));
        assertSame(errInt, errInt.and(lateError));
        assertSame(okDiffType, okInt.and(okDiffType));
    }

    private Result<Integer, Integer> resOkMultiply(final Integer i) {
        return Ok.of(i * i);
    }

    private Result<Integer, Integer> resErrSame(final Integer i) {
        return Err.of(i);
    }

    /**
     * Test cases from Rust Docs.
     * See: https://doc.rust-lang.org/std/result/enum.Result.html#method.and_then
     */
    @Test
    void testAndThen() {
        assertEquals(Ok.of(16),
            Ok.<Integer, Integer>of(2)
                .andThen(this::resOkMultiply) // succeeds: Ok(4)
                .andThen(this::resOkMultiply)); // succeeds: Ok(16)

        assertEquals(Err.of(4),
            Ok.<Integer, Integer>of(2)
                .andThen(this::resOkMultiply) // succeeds: Ok(4)
                .andThen(this::resErrSame)); // recasts: Err(4)

        assertEquals(Err.of(2),
            Ok.<Integer, Integer>of(2)
                .andThen(this::resErrSame) // fails: Err(2)
                .andThen(this::resOkMultiply) // recasts: Err(2)
        );

        assertEquals(Err.of(3),
            Err.<Integer, Integer>of(3)
                .andThen(this::resOkMultiply) // recasts: Err(3)
                .andThen(this::resOkMultiply)); // recasts: Err(3)
    }

    @Test
    void testMap() {
        final Result<Integer, Integer> err = Err.of(2);
        assertEquals(Ok.of(16), Ok.of(2).map(i -> i * i).map(i -> i * i));
        assertSame(err, err.map(i -> i).map(i -> i * i));
    }

    @Test
    void testMapErr() {
        final Result<Integer, Integer> ok = Ok.of(2);
        assertEquals(Err.of(16), Err.of(2).mapErr(i -> i * i).mapErr(i -> i * i));
        assertSame(ok, ok.mapErr(i -> i).mapErr(i -> i * i));
    }

    @Test
    void testMapOrElse() {
        assertEquals(Integer.valueOf(4), Ok.<Integer, Integer>of(2).mapOrElse(e -> e + 3, i -> i * i));
        assertEquals(Integer.valueOf(5), Err.<Integer, Integer>of(2).mapOrElse(e -> e + 3, i -> i * i));
    }

    /**
     * Test cases from Rust Docs.
     * See: https://doc.rust-lang.org/std/result/enum.Result.html#method.or
     */
    @Test
    void testOr() {
        final Result<Integer, String> okInt = Ok.of(2);
        final Result<Integer, String> okDiffValue = Ok.of(100);

        final Result<Integer, String> errInt = Err.of("not a 2");
        final Result<Integer, String> earlyError = Err.of("early error");
        final Result<Integer, String> lateError = Err.of("late error");

        assertSame(okInt, okInt.or(lateError));
        assertSame(okInt, earlyError.or(okInt));
        assertSame(lateError, errInt.or(lateError));
        assertSame(okInt, okInt.or(okDiffValue));
    }

    /**
     * Test cases from Rust Docs.
     * See: https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else
     */
    @Test
    void testOrElse() {
        assertEquals(Ok.of(2),
            Ok.<Integer, Integer>of(2)
                .orElse(this::resOkMultiply) // recast: Ok(2)
                .orElse(this::resOkMultiply)); // recast: OK(2)

        assertEquals(Ok.of(2),
            Ok.<Integer, Integer>of(2)
                .orElse(this::resErrSame) // recast: Ok(2)
                .orElse(this::resOkMultiply)); // recast: Ok(2)

        assertEquals(Ok.of(9),
            Err.<Integer, Integer>of(3)
                .orElse(this::resOkMultiply) // succeeds: Ok(9)
                .orElse(this::resErrSame) // recasts: Ok(9)
        );

        assertEquals(Err.of(3),
            Err.<Integer, Integer>of(3)
                .orElse(this::resErrSame) // recasts: Err(3)
                .orElse(this::resErrSame)); // recasts: Err(3)
    }

    @Test
    void testEquals() {
        final Result<Integer, ?> ok1 = Ok.of(2);
        final Result<Integer, ?> ok2 = Ok.of(4);
        final Result<?, Integer> err1 = Err.of(2);
        final Result<?, Integer> err2 = Err.of(4);

        // same object
        assertEquals(ok1, ok1);
        assertEquals(err1, err1);

        // different object, same value
        assertEquals(Ok.of(2), ok1);
        assertEquals(Err.of(4), err2);

        // different type, same value
        assertNotEquals(err1, ok1);
        assertNotEquals(ok2, err2);

        // different type, different value
        assertNotEquals(err1, ok2);
        assertNotEquals(ok2, err1);
    }

    @Test
    void testHashCode() {
        final Result<Integer, ?> ok1 = Ok.of(2);
        final Result<Integer, ?> ok2 = Ok.of(4);
        final Result<?, Integer> err1 = Err.of(2);
        final Result<?, Integer> err2 = Err.of(4);

        // same object
        assertEquals(ok1.hashCode(), ok1.hashCode());
        assertEquals(err1.hashCode(), err1.hashCode());

        // different object, same value
        assertEquals(ok1.hashCode(), Ok.of(2).hashCode());
        assertEquals(err2.hashCode(), Err.of(4).hashCode());

        // different type, same value
        assertEquals(ok1.hashCode(), err1.hashCode());
        assertEquals(err2.hashCode(), ok2.hashCode());

        // different type, different value
        assertNotEquals(ok1.hashCode(), err2.hashCode());
        assertNotEquals(err1.hashCode(), ok2.hashCode());
    }

    @Test
    void testIterator() {
        final Result<Integer, Integer> ok = Ok.of(2);
        final Result<Integer, Integer> err = Err.of(3);

        final Iterator<Integer> okIter = ok.iterator();
        final Iterator<Integer> errIter = err.iterator();

        assertTrue(okIter.hasNext());
        assertEquals(2, okIter.next());
        // next should now be false
        assertFalse(okIter.hasNext());
        assertThrows(NoSuchElementException.class, okIter::next);

        assertFalse(errIter.hasNext());
        assertThrows(NoSuchElementException.class, errIter::next);
        assertFalse(errIter.hasNext());
    }
}