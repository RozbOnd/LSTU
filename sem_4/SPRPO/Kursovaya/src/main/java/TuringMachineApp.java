import TuringMachine.Table.Moves;
import TuringMachine.Table.TransitionTable;
import TuringMachine.TuringMachine;
import lombok.extern.slf4j.Slf4j;
import utils.FileManager;
import utils.ReportGenerator;
import utils.tm.TMManager;
import utils.user.User;
import utils.user.UserManager;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

@Slf4j
public class TuringMachineApp extends JFrame {
    private final String USERS_FILE = "src/main/resources/users.json";
    private final String TM_FILE = "src/main/resources/TMs.json";

    private JTextField alphabetField;
    private JTextField blankField;
    private JTextField tapeField;
    private JTextField initialStateField;
    private JRadioButton singleHaltRadio;
    private JRadioButton dualHaltRadio;
    private ButtonGroup haltGroup;
    private JTable transitionTable;
    private DefaultTableModel tableModel;
    private JPanel tapePanel;
    private JScrollPane tapeScrollPane;
    private JLabel stateLabel;
    private JButton stepBackBtn;
    private JButton stepForwardBtn;
    private JButton runBtn;
    private JButton stopBtn;
    private JButton resetBtn;
    private JButton reportBtn;
    private JButton logBtn;

    private final int cellWidth = 40;
    private final int cellHeight = 40;
    private JMenuBar menuBar;

    private String currentUser;
    private TuringMachine tm;
    private boolean isRunning;
    private Timer runTimer;
    private boolean isInitialized = false;

    private FileManager fileManager;
    private UserManager userManager;
    private TMManager tmManager;

    public TuringMachineApp() {
        setVisible(false);
        setMinimumSize(new Dimension(1000, 600));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                log.info("Saving files before exit");
                fileManager.saveUsers(userManager.getUsers(), USERS_FILE);
                fileManager.saveTMs(tmManager.getTurMachs(), TM_FILE);
                dispose();
                System.exit(0);
            }
        });

        initServices();

        if (!showLoginDialog()) {
            fileManager.saveUsers(userManager.getUsers(), USERS_FILE);
            System.exit(0);
        }

        setTitle("Эмулятор машины Тьюринга — " + currentUser);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        initComponents();
        initDefaultData();
        setVisible(true);
    }

    private void initServices() {
        fileManager = new FileManager();
        Map<String, User> users = fileManager.loadUsers(USERS_FILE);
        log.info("Loaded " + users.size() + " users");
        List<TuringMachine> turMachs = fileManager.loadTMs(TM_FILE);
        log.info("Loaded " + turMachs.size() + " machines");
        userManager = new UserManager(users);
        tmManager = new TMManager(turMachs);
    }

    private boolean showLoginDialog() {
        JPanel loginPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField loginField = new JTextField();
        JPasswordField passField = new JPasswordField();

        loginPanel.add(new JLabel("Логин:"));
        loginPanel.add(loginField);
        loginPanel.add(new JLabel("Пароль:"));
        loginPanel.add(passField);

        while (true) {
            int option = JOptionPane.showOptionDialog(
                    null,
                    loginPanel,
                    "Вход / Регистрация",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"Войти", "Регистрация", "Выход"},
                    "Войти"
            );

            if (option == JOptionPane.CLOSED_OPTION || option == 2) {
                return false;
            }

            String login = loginField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Логин и пароль не могут быть пустыми", "Ошибка", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (option == 0) { // Вход
                if (userManager.login(login, password)) {
                    currentUser = login;
                    log.info("{} logged in", login);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Неверный логин или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else if (option == 1) { // Регистрация
                if (userManager.register(login, password)) {
                    JOptionPane.showMessageDialog(null,
                            "Регистрация успешна. Теперь войдите.");
                    log.info("New user: {}", login);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Пользователь с таким логином уже существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveItem.addActionListener(e -> saveMachine());

        JMenuItem loadItem = new JMenuItem("Загрузить");
        loadItem.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        loadItem.addActionListener(e -> loadMachine());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> {
            fileManager.saveUsers(userManager.getUsers(), USERS_FILE);
            fileManager.saveTMs(tmManager.getTurMachs(), TM_FILE);
            System.exit(0);
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        settingsPanel.add(new JLabel("Алфавит (символы подряд):"), gbc);
        gbc.gridx = 1;
        alphabetField = new JTextField(15);
        settingsPanel.add(alphabetField, gbc);

        gbc.gridx = 2;
        settingsPanel.add(new JLabel("Пустой символ:"), gbc);
        gbc.gridx = 3;
        blankField = new JTextField(3);
        settingsPanel.add(blankField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        settingsPanel.add(new JLabel("Начальная лента:"), gbc);
        gbc.gridx = 1;
        tapeField = new JTextField(15);
        settingsPanel.add(tapeField, gbc);

        gbc.gridx = 2;
        settingsPanel.add(new JLabel("Начальное состояние:"), gbc);
        gbc.gridx = 3;
        initialStateField = new JTextField("q0", 5);
        settingsPanel.add(initialStateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        settingsPanel.add(new JLabel("Конечное состояние:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        JPanel haltPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        singleHaltRadio = new JRadioButton("qt (остановка)");
        dualHaltRadio = new JRadioButton("qy и qn (допуск/отверг)");
        haltGroup = new ButtonGroup();
        haltGroup.add(singleHaltRadio);
        haltGroup.add(dualHaltRadio);
        singleHaltRadio.setSelected(true);
        haltPanel.add(singleHaltRadio);
        haltPanel.add(dualHaltRadio);
        settingsPanel.add(haltPanel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        JButton applySettingsBtn = new JButton("Применить настройки и сбросить");
        applySettingsBtn.addActionListener(e -> resetMachine());
        settingsPanel.add(applySettingsBtn, gbc);

        transitionTable = new JTable();
        transitionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane tableScroll = new JScrollPane(transitionTable);
        tableScroll.setPreferredSize(new Dimension(900, 250));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Таблица переходов"));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        JPanel tableButtons = new JPanel(new FlowLayout());
        JButton addStateBtn = new JButton("Добавить состояние");
        addStateBtn.addActionListener(e -> addNewState());
        JButton delStateBtn = new JButton("Удалить выбранное состояние");
        delStateBtn.addActionListener(e -> removeSelectedState());
        JButton addCharBtn = new JButton("Добавить символ");
        addCharBtn.addActionListener(e -> addNewChar());
        JButton delCharBtn = new JButton("Удалить выбранный символ");
        delCharBtn.addActionListener(e -> removeSelectedChar());
        tableButtons.add(addStateBtn);
        tableButtons.add(delStateBtn);
        tableButtons.add(addCharBtn);
        tableButtons.add(delCharBtn);
        tablePanel.add(tableButtons, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);

        tapePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawFullTape(g);
            }
        };
        tapePanel.setBackground(Color.WHITE);
        tapePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        tapeScrollPane = new JScrollPane(tapePanel);
        tapeScrollPane.setPreferredSize(new Dimension(900, 100));

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.add(tapeScrollPane, BorderLayout.CENTER);
        stateLabel = new JLabel("Состояние: ");
        stateLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        displayPanel.add(stateLabel, BorderLayout.SOUTH);
        add(displayPanel, BorderLayout.SOUTH);

        JPanel controlPanel = new JPanel(new FlowLayout());
        stepBackBtn = new JButton("< Шаг назад");
        stepForwardBtn = new JButton("Шаг вперед >");
        runBtn = new JButton("Выполнить до остановки");
        stopBtn = new JButton("Стоп");
        resetBtn = new JButton("Сброс");
        reportBtn = new JButton("Отчёт");

        controlPanel.add(stepBackBtn);
        controlPanel.add(stepForwardBtn);
        controlPanel.add(runBtn);
        controlPanel.add(stopBtn);
        controlPanel.add(resetBtn);
        controlPanel.add(reportBtn);

        stepBackBtn.addActionListener(e -> stepBack());
        stepForwardBtn.addActionListener(e -> stepForward());
        runBtn.addActionListener(e -> runToHalt());
        stopBtn.addActionListener(e -> stopRunning());
        resetBtn.addActionListener(e -> resetMachine());
        reportBtn.addActionListener(e -> generateReport());

        disableAllControls();

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(settingsPanel, BorderLayout.CENTER);
        topContainer.add(controlPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        runTimer = new Timer(200, e -> {
            if (isRunning) {
                if (tm.isHalt()) {
                    stopRunning();
                } else {
                    stepForward();
                }
            }
        });
    }

    private void initDefaultData() {
        alphabetField.setText("01#");
        blankField.setText("#");
        tapeField.setText("101");
        initialStateField.setText("q0");
        singleHaltRadio.setSelected(true);

        tableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };
        transitionTable.setColumnSelectionAllowed(true);
        transitionTable.setRowSelectionAllowed(true);
        transitionTable.setModel(tableModel);

        addCharCol(' ');
        addCharCol('0');
        addCharCol('1');
        addCharCol('#');
        addStateRow("q0");
        addStateRow("q1");
    }

    // ================= Работа с таблицей =================

    private void addStateRow(String stateName) {
        Vector<Object> row = new Vector<>();
        row.add(stateName);
        for (int i = 1; i < tableModel.getColumnCount(); i++) {
            row.add("");
        }
        tableModel.addRow(row);
    }

    private void delState(int selectedRow) {
        tableModel.removeRow(selectedRow);
    }

    private void addCharCol(Character newChar) {
        Vector<Object> colData = new Vector<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            colData.add("");
        }
        tableModel.addColumn(String.valueOf(newChar), colData);
    }

    private void delChar(int selectedCol) {
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            if (i == selectedCol) continue;
            columnNames.add(tableModel.getColumnName(i));
        }

        Vector<Vector<Object>> data = new Vector<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(tableModel.getValueAt(row, 0)); // состояние
            for (int col = 1; col < tableModel.getColumnCount(); col++) {
                if (col == selectedCol) continue;
                rowData.add(tableModel.getValueAt(row, col));
            }
            data.add(rowData);
        }

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };
        transitionTable.setModel(tableModel);
    }

    private void addNewState() {
        JDialog dialog = new JDialog(this, "Добавить состояние", true);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Состояние:"));
        JTextField nameField = new JTextField(20);
        inputPanel.add(nameField);
        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Добавить");
        JButton cancelBtn = new JButton("Отмена");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите имя состояния", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(name)) {
                    JOptionPane.showMessageDialog(dialog, "Состояние '" + name + "' уже существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            addStateRow(name);
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addNewChar() {
        JDialog dialog = new JDialog(this, "Добавить символ", true);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Символ:"));
        JTextField nameField = new JTextField(5);
        inputPanel.add(nameField);
        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Добавить");
        JButton cancelBtn = new JButton("Отмена");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            String input = nameField.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите символ", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (input.length() != 1) {
                JOptionPane.showMessageDialog(dialog, "Должен быть ровно один символ", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            char c = input.charAt(0);
            String alphabet = alphabetField.getText().trim();
            if (!alphabet.isEmpty() && !alphabet.contains(input)) {
                JOptionPane.showMessageDialog(dialog, "Символ '" + c + "' отсутствует в алфавите", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = 1; i < tableModel.getColumnCount(); i++) {
                if (tableModel.getColumnName(i).equals(String.valueOf(c))) {
                    JOptionPane.showMessageDialog(dialog, "Столбец '" + c + "' уже существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            addCharCol(c);
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void removeSelectedState() {
        int row = transitionTable.getSelectedRow();
        if (row >= 0) delState(row);
    }

    private void removeSelectedChar() {
        int col = transitionTable.getSelectedColumn();
        if (col > 0) delChar(col);
    }

    // ================= Валидация =================

    private Set<Character> alphabetValidate(String alphabetStr) {
        if (alphabetStr.isEmpty()) return null;
        Set<Character> alphabet = new HashSet<>();
        for (char c : alphabetStr.toCharArray()) {
            if (alphabet.contains(c)) return null;
            alphabet.add(c);
        }
        return alphabet;
    }

    private Character validateBlank(String blankStr, Set<Character> alphabet) {
        if (blankStr.length() != 1) return null;
        char blank = blankStr.charAt(0);
        if (!alphabet.contains(blank)) return null;
        return blank;
    }

    private String validateTape(String tapeStr, Set<Character> alphabet, Character blank) {
        if (tapeStr.isEmpty()) return null;
        for (char c : tapeStr.toCharArray()) {
            if (!alphabet.contains(c) && c != blank) return null;
        }
        return tapeStr;
    }

    private String validateInitialState(String state) {
        return state.isEmpty() ? null : state;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
        log.error(msg);
    }

    // ================= Управление машиной =================

    private boolean resetMachine() {
        stopRunning();

        Set<Character> alphabet = alphabetValidate(alphabetField.getText());
        if (alphabet == null) {
            showError("Алфавит должен содержать уникальные символы.");
            return false;
        }
        Character blank = validateBlank(blankField.getText(), alphabet);
        if (blank == null) {
            showError("Пустой символ должен быть одним символом, присутствующим в алфавите.");
            return false;
        }
        String tape = validateTape(tapeField.getText(), alphabet, blank);
        if (tape == null) {
            showError("Начальная лента содержит символы не из алфавита.");
            return false;
        }
        String curState = validateInitialState(initialStateField.getText());
        if (curState == null) {
            showError("Начальное состояние не может быть пустым.");
            return false;
        }

        Set<String> haltStates = new HashSet<>();
        if (singleHaltRadio.isSelected()) {
            haltStates.add("qt");
        } else {
            haltStates.add("qy");
            haltStates.add("qn");
        }

        TransitionTable newTable = new TransitionTable();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String state = (String) tableModel.getValueAt(i, 0);
            for (int j = 1; j < tableModel.getColumnCount(); j++) {
                String columnName = tableModel.getColumnName(j);
                String curCell = (String) tableModel.getValueAt(i, j);
                if (curCell == null || curCell.trim().isEmpty()) continue;

                String[] parts = curCell.split(",");
                if (parts.length != 3) {
                    showError("Ошибка в ячейке [" + state + ", " + columnName + "]: формат 'следСостояние,символ,направление'");
                    return false;
                }
                String nextState = parts[0].trim();
                String writeSymStr = parts[1].trim();
                String dirStr = parts[2].trim().toUpperCase();

                if (nextState.isEmpty()) {
                    showError("Пустое следующее состояние в ячейке [" + state + ", " + columnName + "]");
                    return false;
                }
                if (writeSymStr.length() != 1) {
                    showError("Записываемый символ должен быть одним символом в ячейке [" + state + ", " + columnName + "]");
                    return false;
                }
                char writeChar = writeSymStr.charAt(0);
                if (!alphabet.contains(writeChar)) {
                    showError("Записываемый символ '" + writeChar + "' отсутствует в алфавите.");
                    return false;
                }

                boolean validState = haltStates.contains(nextState);
                for (int k = 0; k < tableModel.getRowCount(); k++) {
                    if (tableModel.getValueAt(k, 0).equals(nextState)) {
                        validState = true;
                        break;
                    }
                }
                if (!validState) {
                    showError("Состояние '" + nextState + "' не определено в таблице.");
                    return false;
                }

                Moves move;
                switch (dirStr) {
                    case "R": move = Moves.RIGHT; break;
                    case "L": move = Moves.LEFT; break;
                    case "N": move = Moves.STAY; break;
                    default:
                        showError("Направление должно быть R, L или N в ячейке [" + state + ", " + columnName + "]");
                        return false;
                }
                newTable.addTransition(state, columnName.charAt(0), nextState, writeChar, move);
            }
        }

        tm = new TuringMachine(alphabet, tape, blank, curState, haltStates, currentUser);
        tm.setTransitionTable(newTable);
        isInitialized = true;
        updateUIFromMachine();
        log.info("Initialized Turing Machine. Begin state: " + curState);
        return true;
    }

    private void stepForward() {
        if (tm == null || tm.isHalt()) return;
        try {
            tm.nextMove();
            updateUIFromMachine();
        } catch (Exception e) {
            showError("Ошибка выполнения шага: " + e.getMessage());
            log.error("Move error", e);
        }
    }

    private void stepBack() {
        if (tm == null || tm.getHistory().isEmpty()) return;
        tm.previousMove();
        updateUIFromMachine();
    }

    private void runToHalt() {
        if (tm == null || tm.isHalt()) return;
        isRunning = true;
        runTimer.start();
        stepBackBtn.setEnabled(false);
        stepForwardBtn.setEnabled(false);
        runBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    private void stopRunning() {
        runTimer.stop();
        isRunning = false;
        updateUIFromMachine();
    }

    // ================= Обновление UI =================

    private void updateUIFromMachine() {
        if (tm == null) {
            stateLabel.setText("Состояние: —");
            return;
        }

        String status = tm.isHalt() ? " (ОСТАНОВЛЕНА)" : "";
        stateLabel.setText("Состояние: " + tm.getState() + status);

        List<Character> fullTape = tm.getFullTape();
        int width = fullTape.size() * cellWidth + 10;
        int height = cellHeight + 10;
        tapePanel.setPreferredSize(new Dimension(width, height));
        tapePanel.revalidate();
        tapePanel.repaint();

        int headIndex = tm.getHeadAbsolutePosition();
        if (headIndex >= 0) {
            int visibleWidth = tapeScrollPane.getViewport().getWidth();
            final int scrollX = Math.max(0, headIndex * cellWidth - visibleWidth / 2);
            SwingUtilities.invokeLater(() ->
                    tapeScrollPane.getHorizontalScrollBar().setValue(scrollX));
        }

        stepBackBtn.setEnabled(!tm.getHistory().isEmpty());
        stepForwardBtn.setEnabled(!tm.isHalt());
        runBtn.setEnabled(!tm.isHalt() && !isRunning);
        stopBtn.setEnabled(isRunning);
        resetBtn.setEnabled(!isRunning);
    }

    private void drawFullTape(Graphics g) {
        if (tm == null) return;
        Graphics2D g2 = (Graphics2D) g;
        List<Character> tape = tm.getFullTape();
        int headPos = tm.getHeadAbsolutePosition();

        for (int i = 0; i < tape.size(); i++) {
            int x = i * cellWidth + 5;
            int y = 5;
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, cellWidth, cellHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, cellWidth, cellHeight);
            char ch = tape.get(i);
            g2.drawString(String.valueOf(ch), x + cellWidth / 2 - 5, y + cellHeight / 2 + 5);
            if (i == headPos) {
                g2.setColor(Color.RED);
                g2.drawRect(x, y, cellWidth, cellHeight);
                g2.drawLine(x, y - 5, x + cellWidth, y - 5);
            }
        }
    }

    private void disableAllControls() {
        stepBackBtn.setEnabled(false);
        stepForwardBtn.setEnabled(false);
        runBtn.setEnabled(false);
        stopBtn.setEnabled(false);
    }

    // ================= Сохранение/загрузка =================

    private void saveMachine() {
        if (!isInitialized) {
            JOptionPane.showMessageDialog(this, "Сначала создайте машину (нажмите «Применить настройки»).");
            return;
        }
        JDialog dialog = new JDialog(this, "Сохранить машину", true);
        dialog.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Название:"));
        JTextField nameField = new JTextField(20);
        inputPanel.add(nameField);
        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            resetMachine();
            tm.setName(name);
            tmManager.addMachine(new TuringMachine(tm));
            dialog.dispose();
            log.info("Turing Machine '" + name + "' saved for user " + currentUser);
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadMachine() {
        List<TuringMachine> saved = tmManager.getUserTMs(currentUser);
        if (saved.isEmpty()) {
            JOptionPane.showMessageDialog(this, "У вас нет сохранённых машин.");
            return;
        }

        JDialog dialog = new JDialog(this, "Загрузить машину", true);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (TuringMachine m : saved) {
            listModel.addElement(m.getName());
        }
        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dialog.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loadBtn = new JButton("Загрузить");
        JButton cancelBtn = new JButton("Отмена");
        buttonPanel.add(loadBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) {
                loadMachineFromObject(saved.get(idx));
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Выберите машину", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int idx = list.locationToIndex(evt.getPoint());
                    if (idx != -1) {
                        loadMachineFromObject(saved.get(idx));
                        resetMachine();
                        dialog.dispose();
                    }
                }
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadMachineFromObject(TuringMachine loaded) {
        tm = new TuringMachine(loaded);
        isInitialized = true;
        populateGUIFromMachine();
        updateUIFromMachine();
        log.info("Loaded Turing Machine " + tm.getName() + " by user " + currentUser);
    }

    private void populateGUIFromMachine() {
        String alph = new String();
        for (char c : tm.getAlphabet()) alph += c;
        alphabetField.setText(alph);

        blankField.setText(String.valueOf(tm.TapeClassReturn().getBlank()));
        tapeField.setText(tm.getInitialTape());
        initialStateField.setText(tm.getCurrentState());

        if (tm.getHaltStates().contains("qt")) {
            singleHaltRadio.setSelected(true);
        } else {
            dualHaltRadio.setSelected(true);
        }

        rebuildTableFromMachine();
    }

    private void rebuildTableFromMachine() {
        Set<String> states = new LinkedHashSet<>();
        Map<String, Map<Character, String>> transitions = new HashMap<>();

        for (var entry : tm.getTable().getTableMap().entrySet()) {
            String state = entry.getKey().getState();
            char sym = entry.getKey().getCurrentChar();
            String val = entry.getValue().getNextState() + "," + entry.getValue().getWriteChar() + "," +
                    (entry.getValue().getMove() == Moves.RIGHT ? "R" :
                            entry.getValue().getMove() == Moves.LEFT ? "L" : "N");
            states.add(state);
            transitions.computeIfAbsent(state, k -> new HashMap<>()).put(sym, val);
        }
        for (String hs : tm.getHaltStates()) states.add(hs);

        Vector<String> colNames = new Vector<>();
        colNames.add(" ");
        Set<Character> symbols = new LinkedHashSet<>(tm.getAlphabet());
        symbols.add(tm.TapeClassReturn().getBlank());
        for (var e : transitions.values()) {
            symbols.addAll(e.keySet());
        }
        for (char c : symbols) colNames.add(String.valueOf(c));

        Vector<Vector<Object>> data = new Vector<>();
        for (String st : states) {
            Vector<Object> row = new Vector<>();
            row.add(st);
            for (int j = 1; j < colNames.size(); j++) {
                String cell = transitions.getOrDefault(st, Collections.emptyMap()).get(colNames.get(j).charAt(0));
                row.add(cell != null ? cell : "");
            }
            data.add(row);
        }

        tableModel = new DefaultTableModel(data, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };
        transitionTable.setModel(tableModel);
    }

    // ================= Отчёт =================

    private void generateReport() {
        if (!isInitialized || tm == null) {
            JOptionPane.showMessageDialog(this, "Нет данных для отчёта.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("report.pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                ReportGenerator.createPDF(file, currentUser, tm);
                JOptionPane.showMessageDialog(this, "Отчёт сохранён: " + file.getName());
                log.info("Report saved to file: " + file.getAbsolutePath());
            } catch (Exception ex) {
                showError("Ошибка создания отчёта: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TuringMachineApp());
    }
}