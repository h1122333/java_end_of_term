package com.StudentEnrollment.dao;


import com.StudentEnrollment.entity.StudentSort;
import com.StudentEnrollment.entity.Students;
import com.StudentEnrollment.utils.DbUtil;
import com.StudentEnrollment.utils.StringUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentsDao {

    // 增
    public int insert(Students s) {
        String sql = "INSERT INTO students(student_id, name, gender, age, major, course1, course2, course3) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, s);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 批量增 - 使用事务
    public boolean batchInsert(List<Students> list) {
        String sql = "INSERT INTO students(student_id, name, gender, age, major, course1, course2, course3) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Students s : list) {
                    setParams(ps, s);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 删
    public int delete(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 改
    public int update(Students s) {
        String sql = "UPDATE students SET name=?, gender=?, age=?, major=?, course1=?, course2=?, course3=? WHERE student_id=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getGender());
            ps.setInt(3, s.getAge());
            ps.setString(4, s.getMajor());
            ps.setInt(5, s.getCourse1());
            ps.setInt(6, s.getCourse2());
            ps.setInt(7, s.getCourse3());
            ps.setString(8, s.getStudentId());
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 查 - 模糊查询
    // 修改后的 queryStudents 方法 (更通用的综合查询)
    public List<Students> queryStudents(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1 ");
        List<String> conditions = new ArrayList<>();

        if (StringUtil.isNotEmpty(keyword)) {
            String trimmed = keyword.trim();

            // 1. 精确匹配性别
            if ("男".equals(trimmed) || "女".equals(trimmed)) {
                conditions.add("gender = '" + trimmed + "'");
            }
            // 2. 纯数字 (6位以上) -> 查学号
            else if (trimmed.matches("^\\d{6,}$")) {
                conditions.add("student_id LIKE '%" + trimmed + "%'");
            }
            // 3. 纯数字 (较短) -> 查成绩 (0-100)
            else if (trimmed.matches("^\\d+$")) {
                conditions.add("(course1=" + trimmed + " OR course2=" + trimmed + " OR course3=" + trimmed + ")");
            }
            // 4. 其他字符 -> 同时在【姓名】【专业】【学号】里模糊查找 (综合查询)
            else {
                // 这里包含了专业查询！输入 "数据" 会匹配 "数据科学" 或 "大数据" 等
                conditions.add("(name LIKE '%" + trimmed + "%' OR major LIKE '%" + trimmed + "%' OR student_id LIKE '%" + trimmed + "%')");
            }
        }

        // 拼接 WHERE 条件
        if (!conditions.isEmpty()) {
            sql.append(" AND (").append(String.join(" OR ", conditions)).append(")");
        }

//        System.out.println("生成的SQL: " + sql.toString()); // 调试用，方便看后台执行了什么

        List<Students> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(buildStudentFromRs(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 查询所有学生用于排名
    public List<StudentSort> queryAllForRank() {
        List<StudentSort> list = new ArrayList<>();
        String sql = "SELECT student_id, name, major, (course1 + course2 + course3) as total FROM students ORDER BY total DESC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new StudentSort(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("major"),
                        rs.getInt("total")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // 获取全校学生总数
    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 获取某科的平均分
    public double getAvgScore(String column) {
        String sql = "SELECT AVG(" + column + ") FROM students";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 获取某科的及格率
    public double getPassRate(String column) {
        int total = getTotalStudents();
        if (total == 0) return 0;

        String sql = "SELECT COUNT(*) FROM students WHERE " + column + " >= 60";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return (double) rs.getInt(1) / total * 100;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setParams(PreparedStatement ps, Students s) throws SQLException {
        ps.setString(1, s.getStudentId());
        ps.setString(2, s.getName());
        ps.setString(3, s.getGender());
        ps.setInt(4, s.getAge());
        ps.setString(5, s.getMajor());
        ps.setInt(6, s.getCourse1());
        ps.setInt(7, s.getCourse2());
        ps.setInt(8, s.getCourse3());
    }

    private Students buildStudentFromRs(ResultSet rs) throws SQLException {
        Students s = new Students();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getString("student_id"));
        s.setName(rs.getString("name"));
        s.setGender(rs.getString("gender"));
        s.setAge(rs.getInt("age"));
        s.setMajor(rs.getString("major"));
        s.setCourse1(rs.getInt("course1"));
        s.setCourse2(rs.getInt("course2"));
        s.setCourse3(rs.getInt("course3"));
        return s;
    }
}
