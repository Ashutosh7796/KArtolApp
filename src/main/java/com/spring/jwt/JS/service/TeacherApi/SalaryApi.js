import api from "../ApiConfig";
 
const SalaryApi = {
  getAllSalaryRecords: (token) =>
    api.get(`/api/v1/teacherSalary/salaryRecords`, {
      headers: token
        ? { Authorization: `Bearer ${token}` }
        : {},
    }),
 
};
 
export default SalaryApi;