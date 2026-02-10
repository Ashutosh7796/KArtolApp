import api from "../ApiConfig";


const SalaryStructureApi = {
  
  getAllTeachers: () => 
    api.get(`/api/v1/teachers/allTeacher`),

 
  getSalaryStructures: () => 
    api.get(`/api/v1/teacherSalary/structures`),

  
  addSalaryStructure: (payload) => 
    api.post(`/api/v1/teacherSalary/structure`, payload),

  updateSalaryStructure: (teacherId, payload) =>
    api.patch(`/api/v1/teacherSalary/update?teacherId=${teacherId}`, payload),

  deleteSalaryStructure: (teacherId) =>
    api.delete(`/api/v1/teacherSalary/deleteStructure?teacherId=${teacherId}`),
};

export default SalaryStructureApi;
