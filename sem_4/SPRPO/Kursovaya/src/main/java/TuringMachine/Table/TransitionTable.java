package TuringMachine.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TransitionTable {
    private Map<TransitionKey, Transition> table = new HashMap<>();

    public TransitionTable() {}

    public TransitionTable(Map<TransitionKey, Transition> transitions) {
        this.table = new HashMap<>(transitions);
    }

    @JsonIgnore
    public Transition getTransition(String state, Character currentChar) {
        return table.get(new TransitionKey(state, currentChar));
    }

    public void addTransition(String state, Character currentChar,
                              String nextState, Character writeChar,
                              Moves move) {
        table.put(new TransitionKey(state, currentChar),
                new Transition(nextState, writeChar, move));
    }

    public void removeTransition(String state, Character currentChar) {
        table.remove(new TransitionKey(state, currentChar));
    }

    public void setTable(Map<TransitionKey, Transition> transitions) {
        table.clear();
        table.putAll(transitions);
    }

    @JsonIgnore
    public Map<TransitionKey, Transition> getTableMap() {
        return table;
    }
}