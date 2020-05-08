package enigma;

import static enigma.EnigmaException.*;

/**
 * Represents a permutation of a range of integers starting at 0 corresponding
 * to the characters of an alphabet.
 *
 * @author charlesellis
 */
class Permutation {

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles.replace("(", "");
        _cycles = _cycles.replace(")", "");
        _cycleArray = _cycles.split(" ");
        String tight = _cycles.replace(" ", "");
        for (int i = 0; i < tight.length(); i++) {
            for (int j = i + 1; j < tight.length() - 1; j++) {
                if (tight.charAt(i) == tight.charAt(j)) {
                    throw error("duplicate letters in perm");
                }
            }
        }
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
        cycle = cycle.replace("(", "");
        cycle = cycle.replace(")", "");
        _cycles += " " + cycle;
        String[] newCyclearray = new String[_cycleArray.length + 1];
        for (int i = 0; i < _cycleArray.length; i++) {
            newCyclearray[i] = _cycleArray[i];
        }
        newCyclearray[_cycleArray.length] = cycle;
        _cycleArray = newCyclearray;
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        char myChar = _alphabet.toChar(wrap(p));
        char perm = permute(myChar);
        return _alphabet.toInt(perm);
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        char myChar = _alphabet.toChar(wrap(c));
        return _alphabet.toInt(invert(myChar));
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        char newChar = p;
        for (int i = 0; i < _cycleArray.length; i++) {
            for (int ind = 0; ind < _cycleArray[i].length(); ind++) {
                if (_cycleArray[i].charAt(ind) == p) {
                    if (_cycleArray[i].length() == ind + 1) {
                        newChar = _cycleArray[i].charAt(0);
                    } else {
                        newChar = _cycleArray[i].charAt(ind + 1);
                    }
                }
            }
        }
        return newChar;
    }

    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        char newChar = c;
        for (int i = 0; i < _cycleArray.length; i++) {
            for (int ind = 0; ind < _cycleArray[i].length(); ind++) {
                if (_cycleArray[i].charAt(ind) == c) {
                    if (ind == 0) {
                        newChar = _cycleArray[i]
                                .charAt(_cycleArray[i].length() - 1);
                    } else {
                        newChar = _cycleArray[i].charAt(ind - 1);
                    }
                }
            }
        }
        return newChar;
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a
     * permutation for which no value maps to itself).
     */
    boolean derangement() {
        boolean bool = true;
        for (int i = 0; i < _cycleArray.length; i++) {
            if (_cycleArray[i].length() == 1) {
                bool = false;
            }
        }
        return bool;
    }

    /**
     * Alphabet of this permutation.
     */
    private Alphabet _alphabet;
    /**
     * Cycles of this permutation.
     */
    private String _cycles;
    /**
     * Array of cycles.
     */
    private String[] _cycleArray;

}
