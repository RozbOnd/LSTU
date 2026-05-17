package TuringMachine.Table;

import lombok.Getter;

@Getter
public class Transition {
    String nextState;
    Character writeChar;
    Moves move;

    public Transition(String nextState, Character writeChar, Moves move) {
        this.nextState = nextState;
        this.writeChar = writeChar;
        this.move = move;
    }

    public Transition() {};
}
