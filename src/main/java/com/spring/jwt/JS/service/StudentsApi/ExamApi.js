import api from "../ApiConfig";

const ExamApi = {
  getStudentCountDropdown: (className, batch) =>
    api.get("/api/v1/attendance/studentCountDropdown", { params: { class: className, batch } }),
  
  getClassesDropdown: () =>
    api.get("/api/v1/attendance/classesDropdown"),

  getBatchYearsDropdown: (className) =>
    api.get("/api/v1/attendance/batchYearsDropdown", { params: { class: className } }),

 getStudentExam: (params = {}) =>
  api.get("/api/v1/attendance/studentExams", { params }),

    getAllStudentExams: () =>
    api.get("/api/v1/attendance/getAllStudentExams"),

};

export default ExamApi;
