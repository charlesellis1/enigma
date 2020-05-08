package enigma;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static enigma.TestUtils.*;
import static org.junit.Assert.*;


/**
 * The suite of all JUnit tests for the Machine class.
 *
 * @author charlesellis
 */

public class MachineTest {

    @Test
    public void speckTest() {
        Alphabet myAlphabet = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        ArrayList<Rotor> col = new ArrayList<Rotor>();

        Permutation plug = new Permutation("(YF) (ZH)", myAlphabet);
        Permutation p1 = new Permutation("(AELTPHQXRU) (BKNW) "
                + "(CMOY) (DFG) (IV) (JZ) (S)", myAlphabet);
        Permutation p4 = new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) "
                + "(DV) (KU)", myAlphabet);
        Permutation p3 = new Permutation("(ABDHPEJT) "
                + "(CFLVMZOYQIRWUKXSG) (N)", myAlphabet);
        Permutation pBeta = new Permutation("(ALBEVFCYOD"
                + "JWUGNMQTZSKPR) (HIX)", myAlphabet);
        Permutation pB = new Permutation("(AE) (BN) (CK) (DQ) (FU) "
                + "(GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", myAlphabet);
        Rotor i = new MovingRotor("I", p1, "Q");
        Rotor iv = new MovingRotor("IV", p4, "J");
        Rotor iii = new MovingRotor("III", p3, "V");
        Rotor beta = new FixedRotor("Beta", pBeta);
        Rotor B = new Reflector("B", pB);
        col.add(0, B);
        col.add(1, beta);
        col.add(2, iii);
        col.add(3, iv);
        col.add(4, i);

        Machine enigma = new Machine(myAlphabet, 5, 3, col);

        i.set('E');
        iv.set('L');
        iii.set('X');
        beta.set('A');
        enigma.setPlugboard(plug);

        String[] rString = new String[]{"B", "Beta", "III", "IV", "I"};
        enigma.insertRotors(rString);
        assertEquals('Z', myAlphabet.toChar(enigma.convert(24)));
    }

    @Test
    public void smallTest() {
        Alphabet myAlphabet = new Alphabet("ABC");
        ArrayList<Rotor> col = new ArrayList<Rotor>();

        Permutation plug = new Permutation("", myAlphabet);
        Permutation p1 = new Permutation("(ABC)", myAlphabet);
        Permutation p2 = new Permutation("(ABC)", myAlphabet);
        Permutation p3 = new Permutation("(ABC)", myAlphabet);
        Permutation p4 = new Permutation("(ABC)", myAlphabet);
        Rotor i = new FixedRotor("I", p1);
        Rotor ii = new MovingRotor("IV", p2, "C");
        Rotor iii = new MovingRotor("III", p3, "C");
        Rotor iv = new MovingRotor("IV", p4, "C");
        col.add(0, i);
        col.add(1, ii);
        col.add(2, iii);
        col.add(3, iv);

        Machine enigma = new Machine(myAlphabet, 4, 3, col);

        i.set('A');
        ii.set('A');
        iii.set('A');
        iv.set('A');
        enigma.setPlugboard(plug);

        String[] rString = new String[]{"I", "II", "III", "IV"};
        enigma.insertRotors(rString);

        System.out.println(enigma.numRotors());
        System.out.println(enigma.convert("ABC"));


    }

    private ArrayList<Rotor> rotors = ALL_ROTORS;
    Machine machine = new Machine(UPPER, 5, 3, rotors);
    private String[] insert = {"B", "BETA", "III", "IV", "I"};

    /* ***** TESTS ***** */
    @Test
    public void testInsertRotors() {
        machine.insertRotors(insert);
//        assertEquals("Wrong rotor at 0", rotors.get(0), machine.allRotors()[0]);
//        assertEquals("Wrong rotor at 4", rotors.get(4), machine.allRotors()[4]);
    }

//    @Test
//    public void testSetRotors() {
//        setMachine(UPPER, 5, 3, rotors);
//        machine.insertRotors(insert);
//        machine.setRotors("AXLE");
//        assertEquals("Wrong setting at 1", 0, machine.allRotors()[1].setting());
//        assertEquals("Wrong setting at 2", 23, machine.allRotors()[2].setting());
//        assertEquals("Wrong setting at 3", 11, machine.allRotors()[3].setting());
//        assertEquals("Wrong setting at 4", 4, machine.allRotors()[4].setting());
//    }

    @Test
    public void testConvert() {
        Machine machine = new Machine(UPPER, 5, 3, rotors);
        machine.insertRotors(insert);
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", UPPER));
        assertEquals("Wrong convert", "QVPQ", machine.convert("FROM"));
        machine = new Machine(UPPER, 5, 3, rotors);
        machine.insertRotors(insert);
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", UPPER));
        assertEquals("Wrong convert", "FROM", machine.convert("QVPQ"));
    }

    /** A shortened implementation of ALLROTORS. */
    static final ArrayList<Rotor> ALL_ROTORS = new ArrayList<>();
    static {
        ALL_ROTORS.add(new Reflector("B", new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", UPPER)));
        ALL_ROTORS.add(new FixedRotor("BETA", new Permutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", UPPER)));
        ALL_ROTORS.add(new MovingRotor("I", new Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER), "Q"));
        ALL_ROTORS.add(new MovingRotor("II", new Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)", UPPER), "E"));
        ALL_ROTORS.add(new MovingRotor("III", new Permutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER), "V"));
        ALL_ROTORS.add(new MovingRotor("IV", new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", UPPER), "J"));
    }
}

