package uk.olivercrebbin.latexworksheetcreator.Program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
public static String seperator = "±±±§§§±±±±§§§§";
    public static List<String> findCustomAreas(String input){
        // Example text containing the pattern

        // 1. Define the Regex Pattern
        // \\[ matches a literal [
        // {4} means "exactly 4 times"
        // (.*?) is a capture group for the text inside (non-greedy)
        String regex = "\\[{4}(.*?)\\]{4}";

        // 2. Compile the pattern
        // Pattern.DOTALL allows the dot (.) to match newlines if the tags span multiple lines
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        // 3. Find and print matches
        List<String> foundItems = new ArrayList<>();

        while (matcher.find()) {
            // group(0) is the whole string: [[[[text]]]]
            // group(1) is just the capture group inside: text
            String content = matcher.group(1);

            foundItems.add(content);
        }

       return foundItems;
    }
    public static <T> boolean swapWithPrevious(List<T> list, T objectToMove) {
        // 1. Safety Checks: Ensure list and object are not null
        if (list == null || list.isEmpty() || objectToMove == null) {
            return false;
        }

        // 2. Find the index of the object
        int index = list.indexOf(objectToMove);

        // 3. Logic Check:
        // index == -1 : Object not found in list
        // index == 0  : Object is at the top, cannot move up
        if (index > 0) {
            Collections.swap(list, index, index - 1);
            return true;
        }

        return false;
    }
    public static <T> boolean swapWithNext(List<T> list, T objectToMove) {
        // 1. Safety Checks: Ensure list and object are not null
        if (list == null || list.isEmpty() || objectToMove == null) {
            return false;
        }

        // 2. Find the index of the object
        int index = list.indexOf(objectToMove);

        // 3. Logic Check:
        // index == -1 : Object not found in list
        // index == 0  : Object is at the top, cannot move up
        if (index < list.size()-1) {
            Collections.swap(list, index, index + 1);
            return true;
        }

        return false;
    }

}
