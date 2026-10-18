package com.StudentEnrollment.ui;

import com.StudentEnrollment.entity.StudentSort;
import com.StudentEnrollment.entity.Students;
import com.StudentEnrollment.entity.User;
import com.StudentEnrollment.service.StudentsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class MainFrame extends JFrame {

    private User currentUser;
    private StudentsService studentsService = new StudentsService();

    private JTabbedPane tabbedPane;
    private JTable queryTable;
    private DefaultTableModel tableModel;

    public MainFrame(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("学生成绩管理系统 - 欢迎 " + currentUser.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("系统");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        JMenuItem exitItem = new JMenuItem("退出系统");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        });
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();

        // 根据用户角色显示不同的选项卡
        boolean isAdmin = "admin".equals(currentUser.getRole());

        if (isAdmin) {
            tabbedPane.addTab("增加学生", createInsertPanel());
            tabbedPane.addTab("批量增加", createBatchInsertPanel());
            tabbedPane.addTab("修改学生", createUpdatePanel());
            tabbedPane.addTab("删除学生", createDeletePanel());
        }

        tabbedPane.addTab("查询学生", createQueryPanel());
        tabbedPane.addTab("成绩排名", createRankingPanel());
        tabbedPane.addTab("成绩统计", createStatisticsPanel());

        add(tabbedPane);

        // 底部状态栏
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("当前用户：" + currentUser.getUsername() +
                " | 角色：" + (isAdmin ? "管理员" : "普通用户"));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createInsertPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // 学号
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("学号："), gbc);
        JTextField studentIdField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("姓名："), gbc);
        JTextField nameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // 性别
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("性别："), gbc);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"男", "女"});
        gbc.gridx = 1;
        panel.add(genderCombo, gbc);

        // 年龄
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("年龄："), gbc);
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(18, 15, 60, 1));
        gbc.gridx = 1;
        panel.add(ageSpinner, gbc);

        // 专业
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("专业："), gbc);
        String[] majors = {"计算机科学与技术", "软件工程", "电子信息工程", "通信工程"};
        JComboBox<String> majorCombo = new JComboBox<>(majors);
        gbc.gridx = 1;
        panel.add(majorCombo, gbc);

        // 成绩1
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("科目1成绩："), gbc);
        JSpinner course1Spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        gbc.gridx = 1;
        panel.add(course1Spinner, gbc);

        // 成绩2
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("科目2成绩："), gbc);
        JSpinner course2Spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        gbc.gridx = 1;
        panel.add(course2Spinner, gbc);

        // 成绩3
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("科目3成绩："), gbc);
        JSpinner course3Spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        gbc.gridx = 1;
        panel.add(course3Spinner, gbc);

        // 按钮
        JButton insertBtn = new JButton("确认添加");
        insertBtn.setBackground(new Color(33, 150, 243));
        insertBtn.setForeground(Color.WHITE);
        insertBtn.setFocusPainted(false);

        JLabel resultLabel = new JLabel();
        resultLabel.setForeground(Color.RED);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(insertBtn, gbc);

        gbc.gridy = 9;
        panel.add(resultLabel, gbc);

        insertBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Students s = new Students();
                    s.setStudentId(studentIdField.getText().trim());
                    s.setName(nameField.getText().trim());
                    s.setGender((String) genderCombo.getSelectedItem());
                    s.setAge((Integer) ageSpinner.getValue());
                    s.setMajor((String) majorCombo.getSelectedItem());
                    s.setCourse1((Integer) course1Spinner.getValue());
                    s.setCourse2((Integer) course2Spinner.getValue());
                    s.setCourse3((Integer) course3Spinner.getValue());

                    int result = studentsService.insert(s);
                    if (result > 0) {
                        resultLabel.setForeground(new Color(76, 175, 80));
                        resultLabel.setText("添加成功！");
                        studentIdField.setText("");
                        nameField.setText("");
                    } else {
                        resultLabel.setForeground(Color.RED);
                        resultLabel.setText("添加失败，学号可能已存在！");
                    }
                } catch (Exception ex) {
                    resultLabel.setText("输入错误：" + ex.getMessage());
                }
            }
        });

        return panel;
    }

    private JPanel createBatchInsertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea textArea = new JTextArea(15, 50);
        textArea.setFont(new Font("宋体", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        String example = "示例格式（每行一条，用逗号分隔）：\n" +
                "2025001,张三,男,19,计算机科学与技术,85,92,78\n" +
                "2025002,李四,女,20,软件工程,90,88,95\n" +
                "2025003,王五,男,19,电子信息工程,76,81,89";
        textArea.setText(example);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton batchBtn = new JButton("批量添加");
        JLabel resultLabel = new JLabel();
        buttonPanel.add(batchBtn);
        buttonPanel.add(resultLabel);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        batchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] lines = textArea.getText().split("\n");
                List<Students> list = new java.util.ArrayList<>();
                int successCount = 0;

                for (String line : lines) {
                    if (line.trim().isEmpty() || line.startsWith("示例")) continue;
                    Students s = parseStudent(line);
                    if (s != null) {
                        list.add(s);
                    }
                }

                if (studentsService.batchInsert(list)) {
                    resultLabel.setForeground(new Color(76, 175, 80));
                    resultLabel.setText("批量添加成功！共添加 " + list.size() + " 条");
                } else {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("批量添加失败！");
                }
            }
        });

        return panel;
    }

    private JPanel createUpdatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // 学号（用于查询）
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("要修改的学号："), gbc);
        JTextField searchIdField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(searchIdField, gbc);

        JButton searchBtn = new JButton("查询");
        gbc.gridx = 2;
        panel.add(searchBtn, gbc);

        // 分隔线
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        panel.add(new JSeparator(), gbc);

        // 修改表单
        JTextField studentIdField = new JTextField(15);
        studentIdField.setEditable(false);
        JTextField nameField = new JTextField(15);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"男", "女"});
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(18, 15, 60, 1));
        JComboBox<String> majorCombo = new JComboBox<>(new String[]{"计算机科学与技术", "软件工程", "电子信息工程", "通信工程"});
        JSpinner course1Spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        JSpinner course2Spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        JSpinner course3Spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

        int row = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("学号："), gbc);
        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("姓名："), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("性别："), gbc);
        gbc.gridx = 1;
        panel.add(genderCombo, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("年龄："), gbc);
        gbc.gridx = 1;
        panel.add(ageSpinner, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("专业："), gbc);
        gbc.gridx = 1;
        panel.add(majorCombo, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("科目1成绩："), gbc);
        gbc.gridx = 1;
        panel.add(course1Spinner, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("科目2成绩："), gbc);
        gbc.gridx = 1;
        panel.add(course2Spinner, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("科目3成绩："), gbc);
        gbc.gridx = 1;
        panel.add(course3Spinner, gbc);

        JButton updateBtn = new JButton("确认修改");
        updateBtn.setBackground(new Color(33, 150, 243));
        updateBtn.setForeground(Color.WHITE);
        JLabel resultLabel = new JLabel();

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(updateBtn, gbc);
        row++;
        panel.add(resultLabel, gbc);

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = searchIdField.getText().trim();
                List<Students> list = studentsService.query(studentId);
                if (!list.isEmpty()) {
                    Students s = list.get(0);
                    studentIdField.setText(s.getStudentId());
                    nameField.setText(s.getName());
                    genderCombo.setSelectedItem(s.getGender());
                    ageSpinner.setValue(s.getAge());
                    majorCombo.setSelectedItem(s.getMajor());
                    course1Spinner.setValue(s.getCourse1());
                    course2Spinner.setValue(s.getCourse2());
                    course3Spinner.setValue(s.getCourse3());
                    resultLabel.setText("");
                } else {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("未找到学号为 " + studentId + " 的学生");
                }
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Students s = new Students();
                    s.setStudentId(studentIdField.getText().trim());
                    s.setName(nameField.getText().trim());
                    s.setGender((String) genderCombo.getSelectedItem());
                    s.setAge((Integer) ageSpinner.getValue());
                    s.setMajor((String) majorCombo.getSelectedItem());
                    s.setCourse1((Integer) course1Spinner.getValue());
                    s.setCourse2((Integer) course2Spinner.getValue());
                    s.setCourse3((Integer) course3Spinner.getValue());

                    int result = studentsService.update(s);
                    if (result > 0) {
                        resultLabel.setForeground(new Color(76, 175, 80));
                        resultLabel.setText("修改成功！");
                    } else {
                        resultLabel.setForeground(Color.RED);
                        resultLabel.setText("修改失败！");
                    }
                } catch (Exception ex) {
                    resultLabel.setText("输入错误：" + ex.getMessage());
                }
            }
        });

        return panel;
    }

    private JPanel createDeletePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("请输入要删除的学号："), gbc);

        JTextField studentIdField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        JButton deleteBtn = new JButton("删除");
        deleteBtn.setBackground(new Color(244, 67, 54));
        deleteBtn.setForeground(Color.WHITE);
        gbc.gridx = 2;
        panel.add(deleteBtn, gbc);

        JLabel resultLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        panel.add(resultLabel, gbc);

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText().trim();
                if (studentId.isEmpty()) {
                    resultLabel.setText("请输入学号！");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(panel,
                        "确定要删除学号为 " + studentId + " 的学生吗？",
                        "确认删除", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    int result = studentsService.delete(studentId);
                    if (result > 0) {
                        resultLabel.setForeground(new Color(76, 175, 80));
                        resultLabel.setText("删除成功！");
                        studentIdField.setText("");
                    } else {
                        resultLabel.setForeground(Color.RED);
                        resultLabel.setText("删除失败，学号不存在！");
                    }
                }
            }
        });

        return panel;
    }

    private JPanel createQueryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 查询条件面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("查询条件："));
        JTextField keywordField = new JTextField(20);
        searchPanel.add(keywordField);
        JButton searchBtn = new JButton("查询");
        searchPanel.add(searchBtn);
        JButton refreshBtn = new JButton("刷新全部");
        searchPanel.add(refreshBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        // 表格显示
        String[] columns = {"学号", "姓名", "性别", "年龄", "专业", "科目1", "科目2", "科目3"};
        tableModel = new DefaultTableModel(columns, 0);
        queryTable = new JTable(tableModel);
        queryTable.setFont(new Font("宋体", Font.PLAIN, 12));
        queryTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(queryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 状态栏
        JLabel statusLabel = new JLabel("共 0 条记录");
        panel.add(statusLabel, BorderLayout.SOUTH);

        // 查询方法
        Runnable performQuery = new Runnable() {
            @Override
            public void run() {
                String keyword = keywordField.getText().trim();
                List<Students> list = studentsService.query(keyword);
                tableModel.setRowCount(0);
                for (Students s : list) {
                    tableModel.addRow(new Object[]{
                            s.getStudentId(), s.getName(), s.getGender(), s.getAge(),
                            s.getMajor(), s.getCourse1(), s.getCourse2(), s.getCourse3()
                    });
                }
                statusLabel.setText("共 " + list.size() + " 条记录");
            }
        };

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performQuery.run();
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keywordField.setText("");
                performQuery.run();
            }
        });

        // 初始化加载全部数据
        performQuery.run();

        return panel;
    }

    private JPanel createRankingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshBtn = new JButton("刷新排名");
        JPanel topPanel = new JPanel();
        topPanel.add(refreshBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"排名", "学号", "姓名", "专业", "总成绩"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("宋体", Font.PLAIN, 12));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JLabel statusLabel = new JLabel();
        panel.add(statusLabel, BorderLayout.SOUTH);

        Runnable refreshRanking = new Runnable() {
            @Override
            public void run() {
                model.setRowCount(0);
                List<StudentSort> list = studentsService.getRankList();
                for (StudentSort s : list) {
                    model.addRow(new Object[]{
                            s.getRank(), s.getStudentId(), s.getName(), s.getMajor(), s.getTotalScore()
                    });
                }
                statusLabel.setText("共 " + list.size() + " 条记录");
            }
        };

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshRanking.run();
            }
        });

        refreshRanking.run();

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextArea statsArea = new JTextArea(20, 50);
        statsArea.setFont(new Font("宋体", Font.PLAIN, 14));
        statsArea.setEditable(false);

        JButton refreshBtn = new JButton("刷新统计");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(refreshBtn, gbc);
        gbc.gridy = 1;
        panel.add(new JScrollPane(statsArea), gbc);

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 重定向 System.out 以捕获 printStatistics 的输出
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                java.io.PrintStream ps = new java.io.PrintStream(baos);
                java.io.PrintStream old = System.out;
                System.setOut(ps);
                studentsService.printStatistics();
                System.out.flush();
                System.setOut(old);
                statsArea.setText(baos.toString());
            }
        });

        refreshBtn.doClick();

        return panel;
    }

    private Students parseStudent(String line) {
        try {
            String[] parts = line.trim().split("[，,]");
            if (parts.length < 8) return null;
            Students s = new Students();
            s.setStudentId(parts[0].trim());
            s.setName(parts[1].trim());
            s.setGender(parts[2].trim());
            s.setAge(Integer.parseInt(parts[3].trim()));
            s.setMajor(parts[4].trim());
            s.setCourse1(Integer.parseInt(parts[5].trim()));
            s.setCourse2(Integer.parseInt(parts[6].trim()));
            s.setCourse3(Integer.parseInt(parts[7].trim()));
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要退出登录吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "学生成绩管理系统\n版本 1.0\n\n"
                        + "管理员：可进行增删改查操作\n"
                        + "普通用户：仅可查询浏览",
                "关于", JOptionPane.INFORMATION_MESSAGE);
    }
}
