package Qly.view;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import Qly.controller.LoginController;

// Giao diện đăng nhập với bố cục hero + form hiện đại.
public class Login extends JFrame {
    private static final Color PRIMARY = Color.decode("#163172");
    private static final Color SECONDARY = Color.decode("#1E56A0");
    private static final Color ACCENT = Color.decode("#FF7F50");
    private static final Color CANVAS = new Color(243, 246, 253);
    private static final Border BASE_FIELD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(205, 210, 222), 2, true),
        BorderFactory.createEmptyBorder(10, 12, 10, 12));
    private static final Border FOCUSED_FIELD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(ACCENT, 2, true),
        BorderFactory.createEmptyBorder(10, 12, 10, 12));

    private JTextField Txtusername;
    private JPasswordField Txtpassword;
    private JButton Blogin;

    public Login() {
        setTitle("Quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 520));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CANVAS);
        setContentPane(root);

        // Cột bên trái hiển thị thương hiệu với gradient.
        JPanel heroPanel = new GradientPanel();
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        heroPanel.setPreferredSize(new Dimension(420, Integer.MAX_VALUE));

        JLabel brandLabel = new JLabel("Quản lý cửa hàng");
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));

        JLabel sloganLabel = new JLabel("Đăng nhập để tiếp tục");
        sloganLabel.setForeground(new Color(255, 255, 255, 200));
        sloganLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        sloganLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        heroPanel.add(brandLabel);
        heroPanel.add(sloganLabel);

        // Phần form đăng nhập đặt trong thẻ trắng ở bên phải.
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setOpaque(false);
        formWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formWrapper.setPreferredSize(new Dimension(460, 0));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(227, 231, 241), 1, true),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(360, Integer.MAX_VALUE));

        JLabel welcomeLabel = new JLabel("Xin chào!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(PRIMARY);

        JLabel subtitleLabel = new JLabel("Vui lòng nhập thông tin đăng nhập");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        formPanel.add(welcomeLabel);
        formPanel.add(subtitleLabel);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Txtusername = createStyledTextField("Tên đăng nhập");
        Txtpassword = createStyledPasswordField("Mật khẩu");

        Blogin = new JButton("Đăng nhập");
        Blogin.setBackground(ACCENT);
        Blogin.setForeground(Color.WHITE);
        Blogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        Blogin.setFocusPainted(false);
        Blogin.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        Blogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Blogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        Blogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        formPanel.add(createLabeledField("Tên đăng nhập", Txtusername));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createLabeledField("Mật khẩu", Txtpassword));
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(Blogin);

        formCard.add(formPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formWrapper.add(formCard, gbc);

        root.add(heroPanel, BorderLayout.WEST);
        root.add(formWrapper, BorderLayout.CENTER);
        pack();
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public String getUsername() {
        return Txtusername.getText();
    }
    public String getPassword() {
        return Txtpassword.getText();
    }
    public JButton getBlogin() {
        return Blogin;
    }
    public static void main(String[] args) {
        Login lg = new Login();
        new LoginController(lg);
    }

    // TextField với border đổi màu khi focus.
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(new Color(33, 37, 41));
        field.setCaretColor(PRIMARY);
        field.setBorder(BASE_FIELD_BORDER);
        field.setBackground(Color.WHITE);
        field.setToolTipText(placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(0, 46));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        attachFocusBorder(field);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(new Color(33, 37, 41));
        field.setCaretColor(PRIMARY);
        field.setBorder(BASE_FIELD_BORDER);
        field.setBackground(Color.WHITE);
        field.setToolTipText(placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(0, 46));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        attachFocusBorder(field);
        return field;
    }

    // Ghép label và input theo chiều dọc để tái sử dụng.
    private JPanel createLabeledField(String labelText, JComponent component) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        container.add(label);
        container.add(component);
        Dimension preferred = container.getPreferredSize();
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferred.height));
        return container;
    }

    // Tạo hiệu ứng viền nổi bật khi người dùng focus.
    private void attachFocusBorder(JComponent component) {
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                component.setBorder(FOCUSED_FIELD_BORDER);
            }

            @Override
            public void focusLost(FocusEvent e) {
                component.setBorder(BASE_FIELD_BORDER);
            }
        });
    }

    // Panel vẽ nền gradient cam - xanh biển.
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), getHeight(), SECONDARY);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
