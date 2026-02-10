import api from "../ApiConfig";

const MonthlySalaryApi = {
  getActiveTeachers: () =>
    api.get(`/api/v1/teacherSalary/activeTeachers`),

  getSalaryRecords: () =>
    api.get(`/api/v1/teacherSalary/salaryRecords`),

  generateSalary: (payload) =>
    api.post(`/api/v1/teacherSalary/generate`, payload),

  updateMonthlySalary: ({ teacherId, month, year }, body) =>
    api.patch(
      `/api/v1/teacherSalary/updateMonthly?teacherId=${teacherId}&month=${month}&year=${year}`,
      body
    ),

  markAsPay: ({ teacherId, month, year }) =>
    api.post(
      `/api/v1/teacherSalary/pay?teacherId=${teacherId}&month=${month}&year=${year}`
    ),
};

export default MonthlySalaryApi;
