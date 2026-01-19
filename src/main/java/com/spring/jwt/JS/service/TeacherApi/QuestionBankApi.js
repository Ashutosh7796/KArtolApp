import api from "../ApiConfig";

export const getAllQuestionBank = async () => {
  const res = await api.get("/api/v1/dropdown/getAllQuestionBank");
  return res.data.data;
};

export const getAllStudentClass = async () => {
  const res = await api.get("/api/v1/dropdown/getAllStudentClass");
  return res.data.data;
};

export const getTeachersByClass = async (studentClass) => {
  if (!studentClass) throw new Error("Student class is required");

  const res = await api.get(
    `/api/v1/dropdown/questionBankTeachers?studentClass=${studentClass}`
  );

  return res.data.data.map(([id, name]) => ({
    teacherId: id,
    teacherName: name,
  }));
};

export const getSubjectsByClassAndTeacher = async (
  studentClass,
  teacherId
) => {
  if (!studentClass || !teacherId)
    throw new Error("Class and teacherId are required");

  const res = await api.get(
    `/api/v1/dropdown/questionBankSubjects?studentClass=${studentClass}&teacherId=${teacherId}`
  );

  return res.data.data;
};

export const getQuestionPaper = async ({
  studentClass,
  teacherId,
  subject,
}) => {
  if (!studentClass || !teacherId || !subject)
    throw new Error("Missing required params");

  const res = await api.get(
    `/api/v1/dropdown/questionBank?studentClass=${studentClass}&teacherId=${teacherId}&subject=${subject}`
  );

  return res.data.data;
};
