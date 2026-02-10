import axios from 'axios';

const api = axios.create({
  // baseURL: 'https://lms.prodchunca.in.net/',
    // baseURL: 'https://unadulterate-karlene-condensible.ngrok-free.dev',
    // baseURL: 'https://undemised-purulently-lamonica.ngrok-free.dev',
    baseURL: 'https://nonproblematical-zoraida-unvisible.ngrok-free.dev',
  headers: { 'Content-Type': 'application/json',
    "ngrok-skip-browser-warning":"true"
   },
});


api.interceptors.response.use(
  (response) => response, 
  (error) => {
    if (error.response) {
      const status = error.response.status;

      if (status === 400) {
        return Promise.reject({
          status: 400,
          message: error.response.data?.message || 'Bad Request (400)',
        });
      }

      return Promise.reject({
        status,
        message: error.response.data?.message || `Error ${status}`,
      });
    }

    return Promise.reject({ status: 0, message: 'Network error' });
  }
);

export default api;
