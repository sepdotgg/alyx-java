package gg.sep.alyx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for working with {@link String}s.
 *
 * Extends Apache Commons {@link StringUtils}.
 */
@UtilityClass
public class Strings extends StringUtils {

    /**
     * Splits a string into a string array, split by spaces into words.
     *
     * Handles double quotes in the string, considering the inner portion
     * of the quotes a a single string.
     *
     * Double quotes are stripped from the beginning and end of each string.
     *
     * TODO: Handle escaped quotes.
     *
     * @param input The string to split.
     * @return Array of words in the string, split by spaces. Strings enclosed in double quotes
     *         within the string are considered a single word.
     */
    public String[] splitWithQuotes(final String input) {
        // trim beginning and ending whitespace
        final String cleanString = strip(input);
        final List<String> list = new ArrayList<>();
        final Matcher m = Pattern.compile("([^\"]\\S*|\".*?\")\\s*").matcher(cleanString);
        while (m.find()) {
            list.add(strip(m.group(1), "\""));
        }

        return list.toArray(new String[0]);
    }
}
