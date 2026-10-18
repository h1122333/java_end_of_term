package com.StudentEnrollment.service;


import com.StudentEnrollment.dao.StudentsDao;
import com.StudentEnrollment.entity.StudentSort;
import com.StudentEnrollment.entity.Students;

import java.util.List;

public class StudentsService {
    private StudentsDao studentsDao = new StudentsDao();

    public int insert(Students s) {
        return studentsDao.insert(s);
    }

    public boolean batchInsert(List<Students> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("没有有效的学生数据，批量插入失败！");
            return false;  // 空列表应该返回 false
        }
        return studentsDao.batchInsert(list);
    }

    public int delete(String studentId) {
        return studentsDao.delete(studentId);
    }

    public int update(Students s) {
        return studentsDao.update(s);
    }

    public List<com.StudentEnrollment.entity.Students> query(String keyword) {
        return studentsDao.queryStudents(keyword);
    }

    public List<com.StudentEnrollment.entity.StudentSort> getRankList() {
        List<StudentSort> list = studentsDao.queryAllForRank();
        // 计算排名 (同分同名次)
        for (int i = 0; i < list.size(); i++) {
            int rank = i + 1;
            if (i > 0 && list.get(i).getTotalScore() == list.get(i-1).getTotalScore()) {
                rank = list.get(i-1).getRank();
            }
            list.get(i).setRank(rank);
        }
        return list;
    }
    public void printStatistics() {
        System.out.println("\n========== 成绩统计 ==========");
        System.out.println("【全校各科成绩统计】");
        System.out.printf("%-10s %-15s %-15s\n", "科目", "平均分", "及格率");
        System.out.println("------------------------------------------");

        String[] columns = {"course1", "course2", "course3"};
        String[] names = {"成绩1", "成绩2", "成绩3"};

        for (int i = 0; i < 3; i++) {
            double avg = studentsDao.getAvgScore(columns[i]);
            double passRate = studentsDao.getPassRate(columns[i]);
            // 直接把 % 拼接到字符串中
//            System.out.printf("%-10s %-15.2f %-14.2f%%\n", names[i], avg, passRate);
            // 注意上面这行，%% 是正确的写法，如果你那里显示有问题，换成下面这行试试：
             System.out.println(String.format("%-10s %-15.2f %-14.2f%%", names[i], avg, passRate));
        }
        System.out.println("==========================================");
    }

}
