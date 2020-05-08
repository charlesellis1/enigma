package enigma;

import static enigma.EnigmaException.*;

/**
 * Class that represents a rotating rotor in the enigma machine.
 *
 * @author charlesellis
 */
class MovingRotor extends Rotor {

    /**
     * A rotor named NAME whose permutation in its default setting is
     * PERM, and whose notches are at the positions indicated in NOTCHES.
     * The Rotor is initally in its 0 setting (first character of its
     * alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _permutation = perm;
        _alphabet = _permutation.alphabet();
        _notches = notches;
    }

    /**
     * Return notches.
     */
    public String getNotches() {
        return _notches;
    }

    /**
     * Return true if at notch.
     */
    boolean atNotch() {
        boolean bool = false;
        String[] notchArray = _notches.split("");
        for (int i = 0; i < notchArray.length; i++) {
            int c = _alphabet.toInt(notchArray[i].charAt(0));
            if (super.setting() == c) {
                bool = true;
            }
        }
        return bool;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        set(setting() + 1);
        if (setting() == size()) {
            set(0);
        }
    }
    /**
     * Notches of moving rotor.
     */
    private String _notches;
    /**
     * Alphabet of this rotor.
     */
    private Alphabet _alphabet;
    /**
     * Permutation of this rotor.
     */
    private Permutation _permutation;


}
