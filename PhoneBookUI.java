package kioskApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 전화번호부 키오스크 애플리케이션 (Java Swing 기반)
 * * 기능: 이름/번호 추가, 삭제, 이름으로 검색, 전체 목록 조회.
 * Map<String, String>을 사용하여 데이터를 저장합니다.
 */
public class PhoneBookUI extends JFrame implements ActionListener {

    // --- Model (데이터 레이어) ---
    private Map<String, String> phoneBook;

    // --- View (UI 컴포넌트) ---
    private JTextField inputNameField;
    private JTextField inputNumberField;
    private JTextArea outputArea;
    private JButton addButton, deleteButton, searchButton, showAllButton;

    // --- 상수 ---
    private static final String NOT_FOUND_MSG = "검색된 이름이 없습니다.";

    /**
     * 생성자: 데이터 초기화 및 UI 구성
     */
    public PhoneBookUI() {
        // 1. Model 초기화 (데이터 레이어)
        phoneBook = new HashMap<>();
        initializeDefaultData();

        // 2. View 초기화 및 설정 (프레임)
        setTitle("전화번호부 관리 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // 주 레이아웃은 BorderLayout 사용

        // 3. UI 컴포넌트 초기화
        inputNameField = new JTextField(15);
        inputNumberField = new JTextField(15);
        outputArea = new JTextArea(15, 30);
        outputArea.setEditable(false); // 결과 출력 영역은 수정 불가
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // 버튼 초기화 및 리스너 연결
        addButton = new JButton("추가");
        deleteButton = new JButton("삭제");
        searchButton = new JButton("조회");
        showAllButton = new JButton("전체 보기");

        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        searchButton.addActionListener(this);
        showAllButton.addActionListener(this);

        // 4. UI 레이아웃 구성

        // 상단 입력 패널 (FlowLayout)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("이름 및 번호 입력"));
        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(inputNameField);
        inputPanel.add(new JLabel("번호:"));
        inputPanel.add(inputNumberField);

        // 중앙 제어 패널 (GridBagLayout - 버튼들을 체계적으로 배치)
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // 여백 설정

        // 버튼 배치
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; controlPanel.add(addButton, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 1; controlPanel.add(deleteButton, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; controlPanel.add(searchButton, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; controlPanel.add(showAllButton, gbc);

        // 결과 출력 영역 (스크롤 가능하도록 JScrollPane 사용)
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("결과 출력"));

        // 프레임에 컴포넌트 추가
        add(inputPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        
        // 프레임 크기 자동 설정 및 화면에 표시
        pack();
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setVisible(true);
        
        // 시작 시 전체 목록 표시
        displayAllEntries();
    }
    
    /**
     * 초기 테스트 데이터를 Map에 추가합니다.
     */
    private void initializeDefaultData() {
        phoneBook.put("김동현", "010-1234-5678");
        phoneBook.put("박지수", "010-9876-5432");
        phoneBook.put("이하늘", "010-5555-7777");
        phoneBook.put("최민호", "010-2222-3333");
    }

    /**
     * Map의 모든 엔트리를 JTextArea에 출력합니다.
     */
    private void displayAllEntries() {
        if (phoneBook.isEmpty()) {
            outputArea.setText("전화번호부에 등록된 항목이 없습니다.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("총 %d개 항목:\n", phoneBook.size()));
        sb.append("==================================\n");
        
        // keySet을 반복하여 이름과 번호를 출력
        phoneBook.forEach((name, number) -> 
            sb.append(String.format("이름: %-10s | 번호: %s\n", name, number))
        );

        outputArea.setText(sb.toString());
    }

    /**
     * Presenter/Controller 역할: 이벤트 처리 로직
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // 이름과 번호 입력 필드의 값 읽기
        String name = inputNameField.getText().trim();
        String number = inputNumberField.getText().trim();

        Object source = e.getSource();

        if (source == addButton) {
            // 항목 추가
            if (!name.isEmpty() && !number.isEmpty()) {
                addEntry(name, number);
            } else {
                showMessage("오류", "이름과 번호를 모두 입력해야 추가할 수 있습니다.", JOptionPane.WARNING_MESSAGE);
            }
        } else if (source == deleteButton) {
            // 항목 삭제
            if (!name.isEmpty()) {
                deleteEntry(name);
            } else {
                showMessage("오류", "삭제할 이름을 입력해야 합니다.", JOptionPane.WARNING_MESSAGE);
            }
        } else if (source == searchButton) {
            // 항목 조회
            if (!name.isEmpty()) {
                searchEntry(name);
            } else {
                showMessage("오류", "조회할 이름을 입력해야 합니다.", JOptionPane.WARNING_MESSAGE);
            }
        } else if (source == showAllButton) {
            // 전체 목록 보기
            displayAllEntries();
        }

        // 작업 후 입력 필드 초기화
        inputNameField.setText("");
        inputNumberField.setText("");
    }

    /**
     * 전화번호부 항목을 추가합니다.
     */
    private void addEntry(String name, String number) {
        String existingNumber = phoneBook.get(name);
        
        if (existingNumber != null) {
            // 이미 존재하는 이름인 경우 덮어쓰기 여부 확인
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "'" + name + "'은(는) 이미 등록되어 있습니다.\n기존 번호 (" + existingNumber + ")를 새 번호 (" + number + ")로 덮어쓰시겠습니까?", 
                "항목 덮어쓰기 확인", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.NO_OPTION) {
                outputArea.setText("추가/수정 작업이 취소되었습니다.");
                return;
            }
        }
        
        phoneBook.put(name, number);
        outputArea.setText(String.format("'%s' 항목이 성공적으로 %s되었습니다.\n(번호: %s)", name, (existingNumber != null ? "수정" : "추가"), number));
        // 전체 목록을 업데이트하여 보여줍니다.
        displayAllEntries();
    }

    /**
     * 전화번호부 항목을 삭제합니다.
     */
    private void deleteEntry(String name) {
        if (phoneBook.containsKey(name)) {
            phoneBook.remove(name);
            outputArea.setText(String.format("'%s' 항목이 성공적으로 삭제되었습니다.", name));
            displayAllEntries();
        } else {
            outputArea.setText(String.format("'%s' 이름은 전화번호부에 존재하지 않아 삭제할 수 없습니다.", name));
        }
    }

    /**
     * 이름(또는 이름의 일부)을 포함하는 항목을 검색하고 결과를 출력합니다.
     * 예: 입력 'ㄱ' -> '김동현' (이름에 'ㄱ'이 포함된 모든 항목)
     */
    private void searchEntry(String partialName) {
        // partialName이 키에 포함되어 있는지 확인하여 필터링
        Map<String, String> results = phoneBook.entrySet().stream()
                .filter(entry -> entry.getKey().contains(partialName)) // 대소문자 구분 O
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (results.isEmpty()) {
            outputArea.setText(NOT_FOUND_MSG + "\n(검색어: " + partialName + ")");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("'%s'로 검색된 항목 (%d개):\n", partialName, results.size()));
            sb.append("==================================\n");

            results.forEach((name, number) -> 
                sb.append(String.format("이름: %-10s | 번호: %s\n", name, number))
            );
            outputArea.setText(sb.toString());
        }
    }
    
    /**
     * 사용자 정의 메시지 박스를 출력합니다. (alert() 대체)
     */
    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    /**
     * 메인 메소드: 애플리케이션 실행
     */
    public static void main(String[] args) {
        // Event Queue에 UI 생성 작업을 넣어 안전하게 실행
        SwingUtilities.invokeLater(() -> {
            new PhoneBookUI();
        });
    }
}