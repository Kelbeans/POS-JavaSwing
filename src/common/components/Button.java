package common.components;

import common.helperMethods.DollarConversion;
import config.SocketConfig;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Button {

    private final Pane pane;
    private DollarConversion dollarConversion;
    private SocketConfig clientSideVj;

    // Color constants for better maintainability
    private static final Color DEFAULT_BG = new Color(0xFAF9EE);
    private static final Color DEFAULT_FG = Color.BLACK;
    private static final Color BORDER_COLOR = Color.DARK_GRAY;

    // Button color mapping
    private static final Map<String, ButtonColors> BUTTON_COLOR_MAP = new HashMap<>();

    static {
        BUTTON_COLOR_MAP.put("VOID TRANSACTION", new ButtonColors(
                new Color(0xAF3E3E), new Color(0x8B2F2F), Color.WHITE));
        BUTTON_COLOR_MAP.put("NEXT DOLLAR", new ButtonColors(
                new Color(0x748DAE), new Color(0x5A6B85), Color.WHITE));
        BUTTON_COLOR_MAP.put("EXACT DOLLAR", new ButtonColors(
                new Color(0xA2AF9B), new Color(0x7F8C78), Color.WHITE));
        BUTTON_COLOR_MAP.put("VOID ITEM", new ButtonColors(
                new Color(0x7C444F), new Color(0x5F2F38), Color.WHITE));
        BUTTON_COLOR_MAP.put("QUANTITY CHANGE", new ButtonColors(
                new Color(0xDE8F5F), new Color(0xB56F3F), Color.WHITE));
    }

    // Inner class to hold button color information
    private static class ButtonColors {
        final Color normal;
        final Color hover;
        final Color text;

        ButtonColors(Color normal, Color hover, Color text) {
            this.normal = normal;
            this.hover = hover;
            this.text = text;
        }
    }

    public Button(Pane pane) {
        this.pane = pane;
        // Initialize socket connection once
        this.clientSideVj = new SocketConfig("localhost", 8080);
    }

    public AbstractButton testButtonPanel(String buttonName, ArrayList<Integer> buttonStyle) {
        JButton button = createStyledButton(buttonName, buttonStyle);
        addHoverEffect(button, buttonName);
        addClickAction(button, buttonName);

        return button;
    }

    private JButton createStyledButton(String buttonName, ArrayList<Integer> buttonStyle) {
        JButton button = new JButton(buttonName);
        button.setBounds(buttonStyle.get(0), buttonStyle.get(1), 200, 200);
        button.setBorder(new LineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false); // Remove focus border for cleaner look
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));

        // Set initial colors
        setButtonColors(button, buttonName, false);

        return button;
    }

    private void setButtonColors(JButton button, String buttonName, boolean isHovered) {
        ButtonColors colors = BUTTON_COLOR_MAP.get(buttonName.toUpperCase());

        if (colors != null) {
            button.setBackground(isHovered ? colors.hover : colors.normal);
            button.setForeground(colors.text);
        } else {
            // Default colors for regular buttons
            Color bgColor = isHovered ? new Color(0xE5E4D9) : DEFAULT_BG;
            button.setBackground(bgColor);
            button.setForeground(DEFAULT_FG);
        }

        button.setOpaque(true);
    }

    private void addHoverEffect(JButton button, String buttonName) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setButtonColors(button, buttonName, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setButtonColors(button, buttonName, false);
            }
        });
    }

    private void addClickAction(JButton button, String buttonName) {
        button.addActionListener(e -> {
            System.out.println(buttonName + " is clicked");
            handleButtonClick(buttonName);
        });
    }

    private void handleButtonClick(String buttonName) {
        String upperButtonName = buttonName.toUpperCase();

        // Send log for all buttons
        clientSideVj.sendLogAsync(buttonName + " is Clicked");

        // Handle specific button logic
        switch (upperButtonName) {
            case "NEXT DOLLAR":
                if (dollarConversion == null) {
                    dollarConversion = new DollarConversion();
                }
                break;
            // Add specific logic for other buttons here if needed
        }

        // Add text to screen for all buttons
        pane.addTextToScreen(buttonName, null);
    }
}