package enigma;
import static enigma.EnigmaException.*;

/**
 * An alphabet of encodable characters.  Provides a mapping from characters
 * to and from indices into the alphabet.
 *
 * @author charlesellis
 */
class Alphabet {

    /**
     * A new alphabet containing CHARS.  Character number #k has index
     * K (numbering from 0). No character may be duplicated.
     */
    Alphabet(String chars) {
        _chars = chars.toUpperCase();
        for (int i = 0; i < _chars.length(); i++) {
            for (int j = i + 1; j < _chars.length(); j++) {
                if (_chars.charAt(i) == _chars.charAt(j)) {
                    throw error("duplicate "
                            + "letters in alphabet");
                }
            }
        }
        myArray = new char[_chars.length()];
        for (int i = 0; i < _chars.length(); i++) {
            myArray[i] = _chars.charAt(i);
        }
    }

    /**
     * A default alphabet of all upper-case characters.
     */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /**
     * Returns the size of the alphabet.
     */
    int size() {
        return myArray.length;
    }

    /**
     * Returns true if preprocess(CH) is in this alphabet.
     */
    boolean contains(char ch) {
        boolean myBool = false;
        for (int i = 0; i < myArray.length; i++) {
            if (ch == myArray[i]) {
                myBool = true;
            }
        }
        return myBool;
    }

    /**
     * Returns character number INDEX in the alphabet, where
     * 0 <= INDEX < size().
     */
    char toChar(int index) {
        if (0 <= index && index < myArray.length) {
            return myArray[index];
        } else {
            throw error("index not in range");
        }
    }

    /**
     * Returns the index of character preprocess(CH), which must be in
     * the alphabet. This is the inverse of toChar().
     */
    int toInt(char ch) {
        int ans = -1;
        for (int i = 0; i < size(); i++) {
            if (myArray[i] == ch) {
                ans = i;
            }
        }
        if (ans == -1) {
            throw error("character "
                    + "not in alpha");
        }
        return ans;
    }

    /**
     * Array of characters in alphabet.
     */
    private char[] myArray;

    /**
     * String of characters in alphabet.
     */
    private String _chars;
}
