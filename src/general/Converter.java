package general;

import java.util.*;

public final class Converter {
    /**
     * Convert-Methode: Wandelt die LinkedList 'values' in ein
     * Sting-Array um
     *
     * @param values
     * @return String[] mit Inhalt aus der LinkedList 'values'
     */
    public static String[] convert (List<String> values) {
        String[] items = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            items[i] = values.get(i);
        }
        return items;
    }

    /** Wandelt ein String[] in eine List<String> um */
    public static java.util.List<String> convertToList (String[] values) {
        return Arrays.asList(values);
    }

    /** Wandelt ein String[] in ein ArrayList<String> um */
    public static ArrayList<String> convertToArrayList (String[] values) {
        return (ArrayList<String>) Arrays.asList(values);
    }

    /** Wandelt ein String[] in eine LinkedList<String> um */
    public static LinkedList<String> convertToLinkedList (String[] values) {
        return (LinkedList<String>) Arrays.asList(values);
    }
}
