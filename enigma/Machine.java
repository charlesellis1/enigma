package enigma;
import java.util.Collection;

import static enigma.EnigmaException.*;

/**
 * Class that represents a complete enigma machine.
 *
 * @author charlesellis
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        if (_numRotors <= 1) {
            throw error("bad number of rotors");
        }
        if (_pawls < 0 || _pawls >= _numRotors) {
            throw error("bad number of pawls");
        }

    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        rotorList = new Rotor[rotors.length];
        for (int i = 0; i < rotors.length - 1; i++) {
            for (int j = i + 1; j < rotors.length; j++) {
                if (rotors[i].toString().equals(rotors[j].toString())) {
                    throw error("two rotors have same name");
                }
            }
        }
        int myIndex = 0;
        for (int i = 0; i < rotors.length; i++) {
            for (Rotor rotor : _allRotors) {
                String rotorName = rotor.name();
                if ((rotors[i]).equals(rotorName)) {
                    rotorList[myIndex] = rotor;
                    myIndex += 1;
                }
            }
        }

        doISpin = new boolean[rotorList.length];
        for (boolean bool : doISpin) {
            bool = false;
        }
        doISpin[rotorList.length - 1] = true;
        if (rotorList.length != rotors.length) {
            throw error("inserted rotors size doesn't match rotor list");
        }
        if (!rotorList[0].reflecting()) {
            throw error("first rotor isn't reflecting");
        }
        for (int i = 1; i < rotorList.length; i++) {
            if (rotorList[i].reflecting()) {
                throw error("Non 1-rotors is reflecting");
            }
        }
    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 characters in my alphabet. The first letter refers
     * to the leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw error("setting does not equal nR - 1");
        }
        for (int i = 1; i < _numRotors; i++) {
            rotorList[i].set(setting.charAt(i - 1));
        }
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /**
     * Update rotors on if they spin or not for the next call of convert.
     */
    void updateRotors() {
        for (int i = 1; i < rotorList.length - 1; i++) {
            Rotor me = rotorList[i];
            Rotor right = rotorList[i + 1];
            int n = 1;
            while (right.atNotch() && me.rotates()) {
                doISpin[i] = true;
                doISpin[i + 1] = true;
                right = rotorList[i + 1 - n];
                me = rotorList[i - n];
                n += 1;
            }
        }
    }
    /**
     * Spin rotors that have true in doISpin.
     */
    void spinRotors() {
        for (int i = 1; i < rotorList.length - 1; i++) {
            if (doISpin[i]) {
                rotorList[i].advance();
                doISpin[i] = false;
            }
        }
        rotorList[rotorList.length - 1].advance();
    }
    /**
     * Converts an integer to another integer, passed along through the rotors.
     * @param c int
     * @return integer
     */
    int convert(int c) {
        updateRotors();
        spinRotors();
        int pluggedC = _plugboard.permute(c);
        for (int i = numRotors() - 1; i >= 0; i--) {
            pluggedC = rotorList[i].convertForward(pluggedC);
        }
        for (int i = 1; i < numRotors(); i++) {
            pluggedC = rotorList[i].convertBackward(pluggedC);
        }
        pluggedC = _plugboard.permute(pluggedC);
        updateRotors();
        return pluggedC;
    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        String message = "";
        for (int i = 0; i < msg.length(); i++) {
            char myKey = msg.charAt(i);
            char converted = ' ';
            if (_alphabet.contains(myKey)) {
                converted = _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
            }
            message += converted;
        }
        return message;
    }

    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;
    /**
     * Number of rotors.
     */
    private int _numRotors;
    /**
     * Number of pawls.
     */
    private int _pawls;
    /**
     * Collection of all rotors.
     */
    private Collection<Rotor> _allRotors;
    /**
     * Rotor list of used rotors in order.
     */
    private Rotor[] rotorList;
    /**
     * Permutation of plugboard.
     */
    private Permutation _plugboard;
    /**
     * Boolean array checking if rotors spin.
     */
    private boolean[] doISpin;

}
