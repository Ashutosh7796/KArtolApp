import api from "../ApiConfig";

const ProfileApi = {
  getProfile: (userId) =>
    api.get("/api/v1/users/ceo/profile", {
      params: { userId },
    }),

  updateProfile: (userId, payload) =>
    api.patch("/api/v1/users/ceo/editProfile", payload, {
      params: { userId },
    }),
};

export default ProfileApi;
