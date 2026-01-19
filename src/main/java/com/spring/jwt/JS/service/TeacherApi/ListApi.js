import api from "../ApiConfig";

const ListApi = {
  getAll: () => api.get(`/api/v1/teachers/allTeacher`),
};

export default ListApi;
