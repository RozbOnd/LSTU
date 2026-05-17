package TuringMachine;

import TuringMachine.Table.Moves;
import TuringMachine.Table.Transition;
import TuringMachine.Table.TransitionTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class TuringMachine {
    private Set<Character> alphabet;
    private Tape tape;
    private TransitionTable table;
    private boolean halted = false;
    private String currentState;
    private Set<String> haltStates;
    private List<Snapshot> history = new ArrayList<>();
    private String owner;
    private String name = "";
    private String initialTape;

    public TuringMachine(Set<Character> alphabet, String tape, Character blank,
                         String currentState, Set<String> haltStates, String owner) {
        this.alphabet = alphabet;
        this.tape = new Tape(tape, blank);
        this.currentState = currentState;
        this.haltStates = haltStates;
        this.table = new TransitionTable();
        this.owner = owner;
        this.initialTape = tape;
    }

    public TuringMachine() {};

    public TuringMachine(TuringMachine other) {
        this.alphabet = other.alphabet;
        this.tape = other.tape;
        this.currentState = other.currentState;
        this.haltStates = other.haltStates;
        this.table = other.table;
        this.owner = other.owner;
        this.name = other.name;
        this.halted = other.halted;
        this.history = other.history;
        this.initialTape = other.initialTape;
    }

    public void setTransitionTable(TransitionTable table) {
        this.table = table;
    }

    public void addTransition(String state, Character currentChar,
                              String nextState, Character writeChar,
                              Moves move) {
        table.addTransition(state, currentChar,
                            nextState, writeChar, move);
    }

    public void removeTransition(String state, Character currentChar) {
        table.removeTransition(state, currentChar);
    }

    public void nextMove() {
        history.add(new Snapshot(tape, currentState));
        if (halted) return;

        Character currentChar = tape.read();
        Transition transition = table.getTransition(currentState, currentChar);
        if (transition == null) {
            halted = true;
            return;
        }
        tape.write(transition.getWriteChar());
        Moves move = transition.getMove();
        switch (move) {
            case LEFT: tape.leftMove(); break;
            case RIGHT: tape.rightMove(); break;
            case STAY: break;
        }
        currentState = transition.getNextState();
        if (haltStates.contains(currentState)) {
            halted = true;
        }
    }

    public void previousMove() {
        if (history.isEmpty()) return;
        Snapshot previous = history.remove(history.size() - 1);
        this.tape = previous.tape;
        this.currentState = previous.state;
        halted = false;
    }

    public void runToEnd() {
        while (!halted) nextMove();
    }

    @JsonIgnore
    public boolean isHalt() {
        return halted;
    }

    @JsonIgnore
    public List<Character> getFullTape() {
        return tape.getFullTape();
    }

    public Tape TapeClassReturn() { return tape;}

    @JsonIgnore
    public String getState() {
        return currentState;
    }

    @JsonIgnore
    public int getHeadAbsolutePosition() {
        return tape.getHeadPos();
    }

    public List<Snapshot> getHistory() {
        return history;
    }

    public String getInitialTape() {
        return initialTape;
    }

    public void clearHistory() {history.clear();}
}