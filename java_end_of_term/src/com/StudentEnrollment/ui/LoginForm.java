package com.StudentEnrollment.ui;

import com.StudentEnrollment.entity.User;
import com.StudentEnrollment.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private UserService userService = new UserService();

    // UI 组件
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton resetButton;
    private JButton registerButton;  // 新增注册按钮
    private JLabel statusLabel;

    public LoginForm() {
        initUI();
    }

    private void initUI() {
        setTitle("学生登录系统");
        setSize(400, 380);  // 稍微调高以容纳注册按钮
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 主面板 - 使用垂直 BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 30, 50));

        // 标题
        JLabel titleLabel = new JLabel("学生成绩管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 150, 243));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // 账号行
        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JLabel accountLabel = new JLabel("账号：");
        accountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField = new JTextField(15);
        usernameField.setPreferredSize(new Dimension(180, 30));
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        accountPanel.add(accountLabel);
        accountPanel.add(usernameField);
        mainPanel.add(accountPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 密码行
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JLabel passwordLabel = new JLabel("密码：");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField = new JPasswordField(15);
        passwordField.setPreferredSize(new Dimension(180, 30));
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 按钮行（登录 + 重置）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        loginButton = new JButton("登录");
        loginButton.setPreferredSize(new Dimension(90, 35));
        resetButton = new JButton("重置");
        resetButton.setPreferredSize(new Dimension(90, 35));
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 注册按钮行（单独一行，居中）
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        registerButton.setForeground(new Color(33, 150, 243));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerPanel.add(registerButton);
        mainPanel.add(registerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 状态标签
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        add(mainPanel);

        // 添加事件监听
        addEventListeners();
    }

    private void addEventListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterDialog();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty()) {
            statusLabel.setText("请输入用户名！");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            statusLabel.setText("请输入密码！");
            passwordField.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setText("正在登录，请稍候...");
        statusLabel.setForeground(new Color(33, 150, 243));

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return userService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        statusLabel.setForeground(new Color(76, 175, 80));
                        statusLabel.setText("登录成功！正在进入系统...");

                        JOptionPane.showMessageDialog(LoginForm.this,
                                "欢迎 " + user.getUsername() + " ！\n" +
                                        "您的角色：" + ("admin".equals(user.getRole()) ? "管理员" : "普通用户"),
                                "登录成功",
                                JOptionPane.INFORMATION_MESSAGE);

                        new MainFrame(user).setVisible(true);
                        dispose();
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("用户名或密码错误，请重新输入！");
                        passwordField.setText("");
                        passwordField.requestFocus();
                        loginButton.setEnabled(true);
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("登录失败：" + ex.getMessage());
                    loginButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    // 打开注册对话框
    private void openRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "用户注册", true);
        registerDialog.setSize(350, 300);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // 标题
        JLabel titleLabel = new JLabel("新用户注册", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 150, 243));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 用户名
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JLabel usernameLabel = new JLabel("用户名：");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JTextField regUsernameField = new JTextField(12);
        regUsernameField.setPreferredSize(new Dimension(150, 28));
        usernamePanel.add(usernameLabel);
        usernamePanel.add(regUsernameField);
        panel.add(usernamePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 密码
        JPanel passwordPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JLabel passwordLabel1 = new JLabel("密    码：");
        passwordLabel1.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JPasswordField regPasswordField = new JPasswordField(12);
        regPasswordField.setPreferredSize(new Dimension(150, 28));
        passwordPanel1.add(passwordLabel1);
        passwordPanel1.add(regPasswordField);
        panel.add(passwordPanel1);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 角色选择
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JLabel roleLabel = new JLabel("角    色：");
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"普通用户", "管理员"});
        roleCombo.setPreferredSize(new Dimension(150, 28));
        rolePanel.add(roleLabel);
        rolePanel.add(roleCombo);
        panel.add(rolePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton confirmBtn = new JButton("注册");
        confirmBtn.setPreferredSize(new Dimension(80, 32));
        JButton cancelBtn = new JButton("取消");
        cancelBtn.setPreferredSize(new Dimension(80, 32));
        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel regStatusLabel = new JLabel("");
        regStatusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        regStatusLabel.setForeground(Color.RED);
        regStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(regStatusLabel);

        registerDialog.add(panel);

        // 确认注册事件
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = regUsernameField.getText().trim();
                String password = new String(regPasswordField.getPassword()).trim();
                String role = roleCombo.getSelectedItem().equals("管理员") ? "admin" : "user";

                if (username.isEmpty()) {
                    regStatusLabel.setText("请输入用户名！");
                    return;
                }
                if (password.isEmpty()) {
                    regStatusLabel.setText("请输入密码！");
                    return;
                }
                if (password.length() < 3) {
                    regStatusLabel.setText("密码长度不能少于3位！");
                    return;
                }

                boolean success = userService.register(username, password, role);
                if (success) {
                    JOptionPane.showMessageDialog(registerDialog,
                            "注册成功！请登录。",
                            "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                    registerDialog.dispose();
                    // 自动填充用户名到登录框
                    usernameField.setText(username);
                } else {
                    regStatusLabel.setText("注册失败，用户名可能已存在！");
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDialog.dispose();
            }
        });

        registerDialog.setVisible(true);
    }

    private void resetForm() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText("");
        usernameField.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}