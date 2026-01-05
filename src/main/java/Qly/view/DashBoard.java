package Qly.view;

import Qly.controller.LoginController;
import Qly.dao.HoaDonDao;
import Qly.dao.NhapHangDao;
import Qly.dao.ProductDao;
import Qly.dao.StatsDao;
import Qly.dao.SupplierDao;
import Qly.model.User;
import Qly.view.panel.InventoryPanelBuilder;
import Qly.view.panel.InvoiceListPanelBuilder;
import Qly.view.panel.InvoicePanelBuilder;
import Qly.view.panel.NhapHangFormPanelBuilder;
import Qly.view.panel.NhapHangListPanelBuilder;
import Qly.view.panel.ProductManagementPanelBuilder;
import Qly.view.panel.SearchPanelBuilder;
import Qly.view.panel.StatsPanelBuilder;
import Qly.view.panel.SupplierPanelBuilder;

import javax.swing.*;
import java.awt.*;

public class DashBoard extends JFrame {
    private static final Color NAV_BG = Color.decode("#0F6D91");
    private static final Color NAV_BG_DARK = Color.decode("#0B4F66");
    private static final Color CONTENT_BG = new Color(242, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACCENT = Color.decode("#2CB67D");

    private final User currentUser;
    private JPanel contentBody;
    private JLabel roleBadge;

    private JButton btnKho, btnTraCuu;
    private JButton btnSanPham, btnHoaDon, btnLapHoaDon, btnNhapHang, btnLichSuNhapHang;
    private JButton btnNhaCungCap, btnThongKe, btnDangXuat;

    private final InventoryPanelBuilder inventoryPanelBuilder;
    private final SearchPanelBuilder searchPanelBuilder;
    private final ProductManagementPanelBuilder productManagementPanelBuilder;
    private final SupplierPanelBuilder supplierPanelBuilder;
    private final StatsPanelBuilder statsPanelBuilder;
    private final InvoiceListPanelBuilder invoiceListPanelBuilder;
    private final InvoicePanelBuilder invoicePanelBuilder;
    private final NhapHangListPanelBuilder nhapHangListPanelBuilder;
    private final NhapHangFormPanelBuilder nhapHangFormPanelBuilder;

    public DashBoard(User user) {
        this.currentUser = user;
        setTitle("Quản lý dụng cụ y tế - " + user.getRole());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        setContentPane(root);

        ProductDao productDao = new ProductDao();
        SupplierDao supplierDao = new SupplierDao();
        HoaDonDao hoaDonDao = new HoaDonDao();
        NhapHangDao nhapHangDao = new NhapHangDao();
        StatsDao statsDao = new StatsDao();
        inventoryPanelBuilder = new InventoryPanelBuilder(CARD_BG, NAV_BG_DARK, productDao);
        searchPanelBuilder = new SearchPanelBuilder(CARD_BG, NAV_BG_DARK, productDao);
        productManagementPanelBuilder = new ProductManagementPanelBuilder(CARD_BG, NAV_BG_DARK, productDao);
        supplierPanelBuilder = new SupplierPanelBuilder(CARD_BG, NAV_BG_DARK, supplierDao);
        invoiceListPanelBuilder = new InvoiceListPanelBuilder(CARD_BG, NAV_BG_DARK, hoaDonDao);
        invoicePanelBuilder = new InvoicePanelBuilder(CARD_BG, NAV_BG_DARK, hoaDonDao, productDao, user);
        nhapHangListPanelBuilder = new NhapHangListPanelBuilder(CARD_BG, NAV_BG_DARK, nhapHangDao);
        nhapHangFormPanelBuilder = new NhapHangFormPanelBuilder(CARD_BG, NAV_BG_DARK, nhapHangDao, productDao, user);
        statsPanelBuilder = new StatsPanelBuilder(CONTENT_BG, NAV_BG_DARK, statsDao);

        JPanel navPanel = buildNavigation(user);
        JPanel mainPanel = buildMainContent(user);

        root.add(navPanel, BorderLayout.WEST);
        root.add(mainPanel, BorderLayout.CENTER);

        wireActions();
        applyRolePermissions();
        showContent("Tổng quan", "Chọn tính năng để xem chi tiết dòng sản phẩm, đơn hàng và kho dụng cụ y tế.");
        setVisible(true);
    }

    private JPanel buildNavigation(User user) {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(NAV_BG);
        navPanel.setPreferredSize(new Dimension(280, getHeight()));
        navPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel brandLabel = new JLabel("Cửa hàng y tế");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandLabel.setForeground(Color.WHITE);

        JLabel greetingLabel = new JLabel("Chào mừng,  " + user.getRole());
        greetingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        greetingLabel.setForeground(new Color(220, 236, 245));

        roleBadge = new JLabel(user.getRole().toUpperCase());
        roleBadge.setForeground(NAV_BG);
        roleBadge.setBackground(Color.WHITE);
        roleBadge.setOpaque(true);
        roleBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleBadge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        roleBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(brandLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(greetingLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(roleBadge);

        navPanel.add(infoPanel);
        navPanel.add(Box.createVerticalStrut(20));

        btnKho = createNavButton("Xem kho hàng");
        btnTraCuu = createNavButton("Tra cứu sản phẩm");
        btnSanPham = createNavButton("Quản lý sản phẩm");
        btnHoaDon = createNavButton("Hóa đơn");
        btnLapHoaDon = createNavButton("Lập hóa đơn");
        btnNhapHang = createNavButton("Nhập hàng");
        btnLichSuNhapHang = createNavButton("Lịch sử nhập hàng");
        btnNhaCungCap = createNavButton("Nhà cung cấp");
        btnThongKe = createNavButton("Thống kê doanh thu");
        btnDangXuat = createNavButton("Đăng xuất");
        btnDangXuat.setBackground(new Color(209, 74, 74));
        btnDangXuat.setForeground(Color.WHITE);

        navPanel.add(btnKho);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnTraCuu);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnSanPham);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnHoaDon);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnLapHoaDon);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnNhapHang);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnLichSuNhapHang);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnNhaCungCap);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnThongKe);
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(btnDangXuat);
        navPanel.add(Box.createVerticalGlue());
        return navPanel;
    }

    private JPanel buildMainContent(User user) {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(CONTENT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("Bảng điều khiển");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(NAV_BG_DARK);

        JLabel subtitle = new JLabel("Theo dõi hoạt động cửa hàng dụng cụ y tế — " + user.getRole());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(91, 112, 131));

        JPanel textHolder = new JPanel();
        textHolder.setOpaque(false);
        textHolder.setLayout(new BoxLayout(textHolder, BoxLayout.Y_AXIS));
        textHolder.add(titleLabel);
        textHolder.add(Box.createVerticalStrut(4));
        textHolder.add(subtitle);

        header.add(textHolder, BorderLayout.WEST);

        JButton refreshButton = new JButton("Làm mới dữ liệu");
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(ACCENT);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> showContent("Tổng quan", "Dữ liệu đã được làm mới."));
        header.add(refreshButton, BorderLayout.EAST);

        JPanel statPanel = new JPanel(new GridLayout(1, 3, 18, 18));
        statPanel.setOpaque(false);
        statPanel.add(createStatCard("Tồn kho", "1.240", "Dụng cụ sẵn sàng"));
        statPanel.add(createStatCard("Đơn hàng mở", "32", "Đang xử lý"));
        statPanel.add(createStatCard("Nhà cung cấp", "14", "Đang hoạt động"));

        contentBody = new JPanel(new BorderLayout());
        contentBody.setOpaque(false);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 20));
        centerWrapper.setOpaque(false);
        centerWrapper.add(statPanel, BorderLayout.NORTH);
        centerWrapper.add(contentBody, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        return mainPanel;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBackground(NAV_BG_DARK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }
    private JPanel createStatCard(String title, String value, String detail) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(91, 112, 131));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(NAV_BG);

        JLabel detailLabel = new JLabel(detail);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailLabel.setForeground(new Color(120, 138, 150));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(detailLabel);
        return card;
    }

    private void wireActions() {
        btnKho.addActionListener(e -> setContent(inventoryPanelBuilder.build()));
        btnTraCuu.addActionListener(e -> setContent(searchPanelBuilder.build()));
        btnSanPham.addActionListener(e -> setContent(productManagementPanelBuilder.build()));
        btnHoaDon.addActionListener(e -> setContent(invoiceListPanelBuilder.build()));
        btnLapHoaDon.addActionListener(e -> setContent(invoicePanelBuilder.build()));
        btnNhapHang.addActionListener(e -> setContent(nhapHangFormPanelBuilder.build()));
        btnLichSuNhapHang.addActionListener(e -> setContent(nhapHangListPanelBuilder.build()));
        btnNhaCungCap.addActionListener(e -> setContent(supplierPanelBuilder.build()));
        btnThongKe.addActionListener(e -> setContent(statsPanelBuilder.build()));
        btnDangXuat.addActionListener(e -> logout());
    }

    private void showContent(String title, String description) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(NAV_BG_DARK);

        JLabel descriptionLabel = new JLabel("<html><p style='width:520px;'>" + description + "</p></html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(91, 112, 131));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descriptionLabel);
        setContent(card);
    }

    private void setContent(JPanel panel) {
        contentBody.removeAll();
        contentBody.add(panel, BorderLayout.CENTER);
        contentBody.revalidate();
        contentBody.repaint();
    }

    private void applyRolePermissions() {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        btnSanPham.setEnabled(isAdmin);
        btnHoaDon.setEnabled(true);
        btnLapHoaDon.setEnabled(true);
        btnNhapHang.setEnabled(isAdmin);
        btnLichSuNhapHang.setEnabled(true);
        btnNhaCungCap.setEnabled(isAdmin);
        btnThongKe.setEnabled(isAdmin);
        roleBadge.setText(isAdmin ? "ADMIN" : "STAFF");
    }

    private void logout() {
        dispose();
        Login loginView = new Login();
        new LoginController(loginView);
    }
    public static void main(String[] args) {

    }
    }


