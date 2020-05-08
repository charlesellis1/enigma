package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/**
 * Enigma simulator.
 *
 * @author charlesellis
 */
public final class Main {

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }
    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        Machine enigma = readConfig();
        _setting = _input.nextLine();
        setUp(enigma, _setting);
        while (_input.hasNextLine()) {
            temp = _input.nextLine();
            Scanner tempScanner = new Scanner(temp);
            if (tempScanner.hasNext("[*]")) {
                _setting = temp;
                setUp(enigma, _setting);
            } else if (temp.isEmpty()) {
                _output.println();
            } else {
                _message = enigma.convert(temp);
                printMessageLine(_message);
            }


        }

    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            String myLetters = _config.nextLine();
            _alphabet = new Alphabet(myLetters);
            if (!_config.hasNextInt()) {
                throw error("config line 2a wrong format");
            }
            _numRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw error("config line 2b wrong format");
            }
            _numPawls = _config.nextInt();
            while (_config.hasNext()) {
                rotorName = _config.next();
                mnrNotches = _config.next();
                _type = mnrNotches.charAt(0);
                if (mnrNotches.length() > 1) {
                    _notches = mnrNotches.substring(1);
                }
                _allRotors.add(readRotor());
            }
            for (int i = 0; i < _allRotors.size(); i++) {
                for (int j = i + 1; j < _allRotors.size(); j++) {
                    if (_allRotors.get(i).name().toString()
                            .equals(_allRotors.get(j).name().toString())) {
                        throw error("two rotors have same name");
                    }
                }
            }
            return new Machine(_alphabet, _numRotors, _numPawls, _allRotors);


        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            myPerm = "";
            while (_config.hasNext("([(][^()]+[)])+")) {
                onePerm = _config.next();
                myPerm += onePerm + " ";
                if (myPerm.isEmpty()) {
                    throw error("bad rotor description");
                }
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
        Rotor thisRotor = null;
        if (_type == 'M') {
            thisRotor = new MovingRotor(rotorName,
                    new Permutation(myPerm, _alphabet), _notches);
        } else if (_type == 'N') {
            thisRotor = new FixedRotor(rotorName,
                    new Permutation(myPerm, _alphabet));
        } else if (_type == 'R') {
            thisRotor = new Reflector(rotorName,
                    new Permutation(myPerm, _alphabet));
        } else {
            throw error("rotor type is not M, N, or R");
        }
        return thisRotor;
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        Scanner s = new Scanner(settings);
        String p = s.next();
        if (p.charAt(0) != '*') {
            throw error("setting has no star");
        }
        rotorNameArray = new String[_numRotors];
        for (int i = 0; i < M.numRotors(); i++) {
            rotorNameArray[i] = s.next();
        }

        for (int i = 0; i < rotorNameArray.length; i++) {
            for (int j = i + 1; j < rotorNameArray.length - 1; j++) {
                if (rotorNameArray[i].equals(rotorNameArray[j])) {
                    throw error("duplicate rotor names");
                }
            }
        }
        for (int i = 0; i < rotorNameArray.length; i++) {
            boolean unchanged = true;
            for (int j = 0; j < _allRotors.size(); j++) {
                if (rotorNameArray[i].equals(_allRotors.get(j).name())) {
                    unchanged = false;
                }
            }
            if (unchanged) {
                throw error("rotor not in collection");
            }
        }
        M.insertRotors(rotorNameArray);
        String h = s.next();
        for (int i = 0; i < h.length(); i++) {
            if (!_alphabet.contains(h.charAt(i))) {
                throw error("wrong format");
            }
        }
        M.setRotors(h);
        _plugboard = "";
        while (s.hasNext()) {
            p = s.next();
            if (!p.contains("(")) {
                throw error("this should be a perm "
                        + "for plugboard, but it's not");
            }
            _plugboard += p + " ";
        }
        M.setPlugboard(new Permutation(_plugboard, _alphabet));
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        msg = msg.replaceAll(" ", "");
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            result += msg.charAt(i);
            if ((i + 1) % 5 == 0) {
                result += " ";
            }
        }
        _output.println(result);
    }

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;

    /**
     * Source of input messages.
     */
    private Scanner _input;

    /**
     * Source of machine configuration.
     */
    private Scanner _config;

    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;
    /**
     * ArrayList for all rotors.
     */
    private ArrayList<Rotor> _allRotors = new ArrayList<Rotor>();

    /**
     * Number of rotors.
     */
    private int _numRotors;
    /**
     * Number of pawls.
     */
    private int _numPawls;
    /**
     * Rotor name.
     */
    private String rotorName;
    /**
     * String of type, and notches.
     */
    private String mnrNotches;
    /**
     * String of notches.
     */
    private String _notches;
    /**
     * String of rotor permutation.
     */
    private String myPerm;
    /**
     * Temporary string to add to myPerm.
     */
    private String onePerm;
    /**
     * Type of rotor.
     */
    private char _type;
    /**
     * Rotor setting.
     */
    private String _setting;
    /**
     * Message to convert.
     */
    private String _message;
    /**
     * Temporary string.
     */
    private String temp;
    /**
     * Array of rotor names.
     */
    private String[] rotorNameArray;
    /**
     * String of plugboard cycles.
     */
    private String _plugboard;
}
