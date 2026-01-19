import api from "../ApiConfig";

const AttendanceApi = {
  getStudentCountDropdown: (className, batch) =>
    api.get("/api/v1/attendance/studentCountDropdown", { params: { class: className, batch } }),
  
  getClassesDropdown: () =>
    api.get("/api/v1/attendance/classesDropdown"),

  getBatchYearsDropdown: (className) =>
    api.get("/api/v1/attendance/batchYearsDropdown", { params: { class: className } }),

  getStudentAttendance: (params) =>
    api.get("/api/v1/attendance/studentAttendance", { params }),

   getAllStudentAttendance: () =>
    api.get("/api/v1/attendance/allStudentAttendance"),
};

export default AttendanceApi;
