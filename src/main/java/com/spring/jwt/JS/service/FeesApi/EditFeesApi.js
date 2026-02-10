import api from "../ApiConfig";

const EditFeesApi = {
 
  getAllFees: () => api.get("/api/v1/fees/all"),

 
  addFees: (data) => api.post("/api/v1/fees/add", data),


  updateFees: (feesId, data) =>
    api.patch(`/api/v1/fees/update?feesId=${feesId}`, data),

 
  deleteFees: (feesId) => api.delete(`/api/v1/fees/delete?feesId=${feesId}`),

 
  getFeesByStatus: (status) =>
    api.get(`/api/v1/fees/status?status=${status}`),
};

export default EditFeesApi;
