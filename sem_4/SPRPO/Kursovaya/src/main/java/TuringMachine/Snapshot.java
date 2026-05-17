package TuringMachine;

import lombok.Getter;

@Getter
public class Snapshot {
    Tape tape;
    String state;

    public Snapshot() {};

    public Snapshot(Tape tape, String state) {
        this.tape = new Tape(tape);
        this.state = state;
    }
}
