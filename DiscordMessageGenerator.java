import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class DiscordMessageGenerator {
    private static final String[] SECTIONS = {
            "Designer", "Credits", "Versions", "Rates", "Lag Info", "Video Links",
            "Files", "Description", "Positives", "Negatives", "Design Specifications", "Instructions"
    };

    private final Map<String, JPanel> sectionInputs = new LinkedHashMap<>();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel inputPanel = new JPanel(cardLayout);
    private final JTextArea previewArea = new JTextArea();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DiscordMessageGenerator::new);
    }

    public DiscordMessageGenerator() {
        JFrame frame = new JFrame("Discord Message Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        // Top: section buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, SECTIONS.length));
        for (String section : SECTIONS) {
            JButton btn = new JButton(section);
            btn.addActionListener(e -> cardLayout.show(inputPanel, section));
            buttonPanel.add(btn);
        }
        frame.add(buttonPanel, BorderLayout.NORTH);

        // Left: dynamic input panels
        for (String section : SECTIONS) {
            JPanel panel = createInputSection(section);
            sectionInputs.put(section, panel);
            inputPanel.add(panel, section);
        }
        JScrollPane inputScroll = new JScrollPane(inputPanel);
        inputScroll.setPreferredSize(new Dimension(350, 600));
        frame.add(inputScroll, BorderLayout.WEST);

        // Center: preview area
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane previewScroll = new JScrollPane(previewArea);
        frame.add(previewScroll, BorderLayout.CENTER);

        // Bottom: control buttons
        JPanel controlPanel = new JPanel();
        JButton generateButton = new JButton("Generate Preview");
        JButton copyButton = new JButton("Copy to Clipboard");
        JButton clearSectionButton = new JButton("Clear Section");
        JButton clearButton = new JButton("Clear All");

        generateButton.addActionListener(this::generatePreview);
        copyButton.addActionListener(e -> copyToClipboard());
        clearSectionButton.addActionListener(e -> clearSection());
        clearButton.addActionListener(e -> clearAll());

        controlPanel.add(generateButton);
        controlPanel.add(copyButton);
        controlPanel.add(clearSectionButton);
        controlPanel.add(clearButton);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createInputSection(String section) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        switch (section) {
            case "Lag Info" -> {
                panel.add(new JLabel("Test environment:"));
                panel.add(new JTextField());
                panel.add(new JLabel("Idle:"));
                panel.add(new JTextField());
                panel.add(new JLabel("Active:"));
                panel.add(new JTextField());
            }
            case "Instructions" -> {
                panel.add(new JLabel("Notes:"));
                panel.add(new JTextArea(3, 25));
                panel.add(new JLabel("Build:"));
                panel.add(new JTextArea(3, 25));
                panel.add(new JLabel("How to use:"));
                panel.add(new JTextArea(5, 25));
            }
            default -> {
                panel.add(new JLabel(section + ":"));
                panel.add(new JTextArea(4, 25));
            }
        }
        return panel;
    }

    private void generatePreview(ActionEvent e) {
        StringBuilder sb = new StringBuilder();
        for (String section : SECTIONS) {
            sb.append("## ").append(section).append("\n");
            JPanel panel = sectionInputs.get(section);
            Component[] comps = panel.getComponents();
            if (section.equals("Lag Info")) {
                sb.append("- Test environment: ").append(getText((JTextField)comps[1])).append("\n");
                sb.append("- Idle: ").append(getText((JTextField)comps[3])).append("\n");
                sb.append("- Active: ").append(getText((JTextField)comps[5])).append("\n\n");
            } else if (section.equals("Instructions")) {
                sb.append("### Notes\n"); appendList(sb, (JTextArea)comps[1]);
                sb.append("\n### Build\n"); appendList(sb, (JTextArea)comps[3]);
                sb.append("\n### How to use\n"); appendNumbered(sb, (JTextArea)comps[5]);
                sb.append("\n");
            } else {
                appendList(sb, (JTextArea)comps[1]);
                sb.append("\n");
            }
        }
        previewArea.setText(sb.toString().trim());
    }

    private String getText(JTextField field) {
        return field.getText().trim();
    }

    private void appendList(StringBuilder sb, JTextArea area) {
        for (String line : area.getText().split("\n")) {
            if (!line.isBlank()) sb.append("- ").append(line.trim()).append("\n");
        }
    }

    private void appendNumbered(StringBuilder sb, JTextArea area) {
        int count = 1;
        for (String line : area.getText().split("\n")) {
            if (!line.isBlank()) sb.append(count++).append(". ").append(line.trim()).append("\n");
        }
    }

    private void copyToClipboard() {
        StringSelection sel = new StringSelection(previewArea.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
        JOptionPane.showMessageDialog(null, "Copied to clipboard!");
    }

    private void clearSection() {
        int ok = JOptionPane.showConfirmDialog(null, "Clear current section?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            for (String section : SECTIONS) {
                JPanel panel = sectionInputs.get(section);
                if (panel.isVisible()) {
                    for (Component c : panel.getComponents()) {
                        if (c instanceof JTextField) ((JTextField)c).setText("");
                        else if (c instanceof JTextArea) ((JTextArea)c).setText("");
                    }
                    break;
                }
            }
        }
    }

    private void clearAll() {
        int ok = JOptionPane.showConfirmDialog(null, "Clear all sections and preview?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            sectionInputs.values().forEach(panel -> {
                for (Component c : panel.getComponents()) {
                    if (c instanceof JTextField) ((JTextField)c).setText("");
                    else if (c instanceof JTextArea) ((JTextArea)c).setText("");
                }
            });
            previewArea.setText("");
        }
    }
}
