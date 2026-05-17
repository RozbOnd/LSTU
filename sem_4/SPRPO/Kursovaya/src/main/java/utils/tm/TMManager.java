package utils.tm;

import TuringMachine.TuringMachine;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TMManager {
    @Getter
    private List<TuringMachine> turMachs;

    public TMManager(List<TuringMachine> turMachs) {
        this.turMachs = new ArrayList<>(turMachs);
    }

    public void addMachine(TuringMachine TM) {
        turMachs.add(new TuringMachine(TM));
    }

    public boolean deleteMachine(String username, String machineName) {
        TuringMachine curTM = turMachs.stream()
                .filter(l -> (l.getOwner().equals(username) &&
                        l.getName().equals(machineName)))
                .toList().getFirst();
        if (curTM == null) return false;
        turMachs.remove(curTM);
        return true;
    }

    public List<TuringMachine> getUserTMs(String username) {
        return turMachs.stream()
                .filter(l -> l.getOwner()
                        .equals(username)).toList();
    }
}
