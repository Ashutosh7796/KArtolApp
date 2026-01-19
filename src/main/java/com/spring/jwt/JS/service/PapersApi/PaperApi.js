import api from "../ApiConfig";
 
const PaperApi = {
  getAllPapers: (page = 0, size = 10, token) =>
    api.get(`/api/v1/papers/papers?page=${page}&size=${size}`, {
      headers: token
        ? { Authorization: `Bearer ${token}` }
        : {},
    }),
};

export default PaperApi;