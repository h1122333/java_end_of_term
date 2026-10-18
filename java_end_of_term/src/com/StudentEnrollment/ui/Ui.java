package com.StudentEnrollment.ui;


import com.StudentEnrollment.entity.StudentSort;
import com.StudentEnrollment.entity.Students;
import com.StudentEnrollment.entity.User;
import com.StudentEnrollment.service.StudentsService;
import com.StudentEnrollment.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Ui {
    private static UserService userService = new UserService();
    private static StudentsService studentsService = new StudentsService();
    private static Scanner scanner = new Scanner(System.in);

    // 保存当前登录用户
    private static User currentUser = null;

    public static void main(String[] args) {
        while (true) {
            printLoginMenu();
            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) {
                regist();
            } else if ("2".equals(choice)) {
                if (login()) {
                    mainFun();
                }
            } else if ("3".equals(choice)) {
                System.out.println("感谢使用，再见！");
                break;
            } else {
                System.out.println("输入有误，请重新选择！");
            }
        }
    }

    private static void printLoginMenu() {
        System.out.println("*******************************");
        System.out.println("*         欢迎使用本系统         *");
        System.out.println("*-----------------------------*");
        System.out.println("*         1.注册               *");
        System.out.println("*         2.登录               *");
        System.out.println("*         3.退出系统            *");
        System.out.println("*******************************");
        System.out.print("请选择: ");
    }

    private static void regist() {
        System.out.print("请输入用户名: ");
        String user = scanner.nextLine();
        System.out.print("请输入密码: ");
        String pwd = scanner.nextLine();
        System.out.print("请选择角色（1-管理员，2-普通用户）：");
        String role = scanner.nextLine();
        role = role.equals("1") ? "admin" : "user";
        if (userService.register(user, pwd, role)) {
            System.out.println("注册成功！");
        } else {
            System.out.println("注册失败，用户名可能已存在！");
        }
    }

    private static boolean login() {
        System.out.println("请输入用户名和密码，格式 (用户名, 密码): 例如: 张三, 123456");
        String input = scanner.nextLine();
        String[] parts = input.split("[，,]");
        if (parts.length < 2) {
            System.out.println("格式错误！");
            return false;
        }
        User user = userService.login(parts[0].trim(), parts[1].trim());
        if (user != null) {
            currentUser = user;  // 保存当前登录用户
            System.out.println("登录成功！欢迎您：" + user.getUsername());
            System.out.println("您的角色：" + ("admin".equals(user.getRole()) ? "管理员" : "普通用户"));
            return true;
        } else {
            System.out.println("用户名或密码错误！");
            return false;
        }
    }

    private static void mainFun() {
        while (true) {
            printMainMenu();
            String choice = scanner.nextLine();

            // 判断当前用户角色
            boolean isAdmin = currentUser != null && "admin".equals(currentUser.getRole());

            switch (choice) {
                case "1":  // 插入
                    if (isAdmin) {
                        insert();
                    } else {
                        System.out.println("⚠️ 权限不足：只有管理员可以执行插入操作！");
                    }
                    break;
                case "2":  // 批量插入
                    if (isAdmin) {
                        batchInsert();
                    } else {
                        System.out.println("⚠️ 权限不足：只有管理员可以执行批量插入操作！");
                    }
                    break;
                case "3":  // 更新
                    if (isAdmin) {
                        update();
                    } else {
                        System.out.println("⚠️ 权限不足：只有管理员可以执行更新操作！");
                    }
                    break;
                case "4":  // 删除
                    if (isAdmin) {
                        delete();
                    } else {
                        System.out.println("⚠️ 权限不足：只有管理员可以执行删除操作！");
                    }
                    break;
                case "5":  // 查询0000
                    query();
                    break;
                case "6":  // 排名统计
                    ranking();
                    break;
                case "7":  // 成绩统计
                    studentsService.printStatistics();
                    break;
                case "8":  // 退出
                    System.out.println("退出系统！");
                    currentUser = null;  // 清空当前用户
                    return;
                default:
                    System.out.println("输入错误，请重新选择！");
            }
        }
    }

    private static void printMainMenu() {
        boolean isAdmin = currentUser != null && "admin".equals(currentUser.getRole());

        System.out.println("*******************************");
        System.out.println("*         功能菜单              *");
        System.out.println("*-----------------------------*");

        if (isAdmin) {
            // 管理员菜单（完整功能）
            System.out.println("*      【数据管理】            *");
            System.out.println("*         1.插入学生           *");
            System.out.println("*         2.批量插入           *");
            System.out.println("*         3.更新学生           *");
            System.out.println("*         4.删除学生           *");
            System.out.println("*      【数据浏览】            *");
            System.out.println("*         5.查询学生           *");
            System.out.println("*         6.排名统计           *");
            System.out.println("*         7.成绩统计           *");
        } else {
            // 普通用户菜单（仅查询功能）
            System.out.println("*      【数据浏览】            *");
            System.out.println("*         5.查询学生           *");
            System.out.println("*         6.排名统计           *");
            System.out.println("*         7.成绩统计           *");
        }

        System.out.println("*         8.退出系统            *");
        System.out.println("*******************************");
        System.out.print("请选择: ");
    }

    private static void insert() {
        System.out.println("请输入学生信息格式: 学号,姓名,性别,年龄,专业,成绩1,成绩2,成绩3");
        System.out.println("示例: 2025001,张伟,男,19,计算机科学与技术,85,92,78");
        String input = scanner.nextLine();
        Students s = parseStudent(input);
        if (s != null) {
            int result = studentsService.insert(s);
            System.out.println(result > 0 ? "插入成功！" : "插入失败！");
        }else {
            System.out.println("❌ 插入失败：输入格式错误，请确保包含 8 个字段，用中文逗号或英文逗号分隔！");
        }
    }

    private static void batchInsert() {
        System.out.println("请输入学生信息格式: 学号,姓名,性别,年龄,专业,成绩1,成绩2,成绩3");
        System.out.println("示例: 2025001,张伟,男,19,计算机科学与技术,85,92,78");
        System.out.print("请输入要插入的学生人数: ");
        try {
            int count = Integer.parseInt(scanner.nextLine().trim());
            List<Students> list = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                System.out.println("第 " + i + " 名学生信息:");
                String input = scanner.nextLine();
                Students s = parseStudent(input);
                if (s != null) list.add(s);
                else {
                    System.out.println("格式错误，跳过该条");
                }
            }
            if (studentsService.batchInsert(list)) {
                System.out.println("批量插入成功！");
            } else {
                System.out.println("批量插入失败！");
            }
        } catch (Exception e) {
            System.out.println("输入错误！");
        }
    }

    private static void update() {
        System.out.println("请输入学生信息格式: 学号,姓名,性别,年龄,专业,成绩1,成绩2,成绩3");
        System.out.println("示例: 2025001,张伟,男,19,计算机科学与技术,85,92,78");
        System.out.println("请输入要更新的学生完整信息 (学号不可改):");
        String input = scanner.nextLine();
        Students s = parseStudent(input);
        if (s != null) {
            int result = studentsService.update(s);
            System.out.println(result > 0 ? "更新成功！" : "更新失败！");
        }else {
            System.out.println("❌ 更新失败：输入格式错误，请确保包含 8 个字段！");
        }
    }

    private static void delete() {
        System.out.print("请输入要删除的学生学号: ");
        String id = scanner.nextLine().trim();
        int result = studentsService.delete(id);
        if (result > 0) {
            System.out.println("✅ 删除成功！");
        } else {
            System.out.println("❌ 删除失败：学号不存在或删除失败！");
        }
    }

    private static void query() {
        System.out.println("请输入查询条件 (支持学号(至少六位)/姓名/性别/专业/成绩模糊查询):");
        String keyword = scanner.nextLine().trim();
        List<Students> list = studentsService.query(keyword);
        printQueryResult(list);
    }

    private static void ranking() {
        List<StudentSort> list = studentsService.getRankList();

        System.out.println("\n=============================== 排名统计 ===============================");
        // 表头
        System.out.printf("%-12s %-10s %-20s %-10s %-8s\n", "学号", "姓名", "专业", "总成绩", "排名");
        System.out.println("----------------------------------------------------------------------");

        // 数据行
        for (StudentSort s : list) {
            System.out.printf("%-12s %-10s %-20s %-10d %-8d\n",
                    s.getStudentId(),
                    s.getName(),
                    s.getMajor(),
                    s.getTotalScore(),
                    s.getRank());
        }

        System.out.println("----------------------------------------------------------------------");
        System.out.println("数据总数: " + list.size());
        System.out.println("======================================================================\n");
    }

    private static Students parseStudent(String input) {
        try {
            String[] parts = input.split("[，,]");
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
            System.out.println("数据解析错误: " + e.getMessage());
            return null;
        }
    }

    private static void printQueryResult(List<Students> list) {
        if (list.isEmpty()) {
            System.out.println("未找到相关记录！");
            return;
        }
        System.out.println("==========================================================");
        System.out.printf("%-10s %-8s %-4s %-4s %-15s %-6s %-6s %-6s\n",
                "学号", "姓名", "性别", "年龄", "专业", "成绩1", "成绩2", "成绩3");
        System.out.println("----------------------------------------------------------");
        for (Students s : list) {
            System.out.println(s);
        }
        System.out.println("==========================================================");
        System.out.println("数据总数: " + list.size());
    }
}