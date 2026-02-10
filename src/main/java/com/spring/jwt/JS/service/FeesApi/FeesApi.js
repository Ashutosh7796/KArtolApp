import api from "../ApiConfig";

const FeesApi = {
  getAllFees: () => api.get("/api/v1/fees/all"),
};
export default FeesApi;