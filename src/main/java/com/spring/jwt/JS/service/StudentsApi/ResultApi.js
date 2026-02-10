import api from "../ApiConfig";

const ResultApi = {
  getAllStudentResults: () => api.get("/api/v1/attendance/getAllStudentResults"),
  getClasses: () => api.get("/api/v1/attendance/classes"),
  getStudentsByClass: (className) => api.get(`/api/v1/attendance/students/${className}`),
  getBatchYears: (className) => api.get(`/api/v1/attendance/batchYears/${className}`),
  getStudentResults: (className, batch) => api.get(`/api/v1/attendance/studentResults?class=${className}&batch=${batch}`),
  getResultByUserId: (userId, batch) => api.get(`/api/v1/attendance/results/${userId}`, { params: { batch } }),
};

export default ResultApi;
