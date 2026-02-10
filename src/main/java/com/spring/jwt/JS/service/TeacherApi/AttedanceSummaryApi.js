import api from "../ApiConfig";

const AttendanceSummaryApi = {

  addAttendance: (teacherId) =>
    api.post(`/api/v1/teacherAttendance/add`, {
      teacherId: Number(teacherId)
    }),

  
  getAll: () => api.get(`/api/v1/teacherAttendance/all`),

 
  getByTeacherId: (teacherId) =>
    api.get(`/api/v1/teacherAttendance/teacher`, {
      params: { teacherId },
    }),

    
  getByTeacherAndMonth: (teacherId, month) =>
    api.get(`/api/v1/teacherAttendance/teacher/month`, {
      params: { teacherId, month },
    }),

 
  getByYear: (teacherId, year) =>
    api.get(`/api/v1/teacherAttendance/teacher/year`, {
      params: { teacherId, year },
    }),
    

  getSummary: (teacherId, month) =>
    api.get(`/api/v1/teacherAttendance/summary`, {
      params: { teacherId, month },
    }),

  
  getByDate: (date) =>
    api.get(`/api/v1/teacherAttendance/date`, {
      params: { date },
    }),


  updateRecord: (attendanceId, data) =>
    api.patch(`/api/v1/teacherAttendance/update`, data, { params: { attendanceId } }),

  deleteRecord: (attendanceId) =>
    api.delete(`/api/v1/teacherAttendance/delete`, { params: { attendanceId } }),
};

export default AttendanceSummaryApi;
