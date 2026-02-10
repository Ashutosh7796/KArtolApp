import api from "../ApiConfig";
 
const PaperPatternApi = {

  getAll: () => api.get(`api/v1/paper-patterns`),

 
  add: (payload) => api.post(`api/v1/paper-patterns/add`, payload),

 
  update: (id, payload) => api.patch(`api/v1/paper-patterns/${id}`, payload),


  delete: (id) => api.delete(`api/v1/paper-patterns/${id}`),
};
 
export default PaperPatternApi;