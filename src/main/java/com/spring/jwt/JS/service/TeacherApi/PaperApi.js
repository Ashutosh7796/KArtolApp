import api from "../ApiConfig";
 
const PaperDropdownApi = {
  getSubjects: () =>
    api.get("/api/v1/dropdown/subjects"),
 
  getClasses: (subject) =>
    api.get("/api/v1/dropdown/classes", {
      params: { subject },
    }),
 
  getIsLiveOptions: (subject, studentClass) =>
    api.get("/api/v1/dropdown/isLiveOptions", {
      params: { subject, studentClass },
    }),
 
  getPapers: (subject, studentClass, isLive) =>
    api.get("/api/v1/dropdown/paper", {
      params: { subject, studentClass, isLive },
    }),
 
  getAllPapers: () =>
    api.get("/api/v1/dropdown/getAllPaper"),
};
 
export default PaperDropdownApi;
 