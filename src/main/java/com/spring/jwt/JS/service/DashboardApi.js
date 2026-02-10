import api from "./ApiConfig";


export const getMonthlyChart = (studentClass, batch) =>
  api.get(`/api/v1/ceo/monthly?studentClass=${studentClass}&batch=${batch}`);

export const getPieChart = () =>
  api.get(`/api/v1/ceo/pieChart`);

export const getStudentClasses = () =>
  api.get(`/api/v1/ceo/studentClass`);

export const getStudentBatches = () =>
  api.get(`/api/v1/ceo/studentBatch`);


export const getBatchToppers = (studentClass, batch) =>
  api.get(`/api/v1/ceo/batchToppers?studentClass=${studentClass}&batch=${batch}`);

export const getBatchAverage = (studentClass, batch) =>
  api.get(`/api/v1/ceo/batchAverage?studentClass=${studentClass}&batch=${batch}`);

export const getBatchBelowAverage = (studentClass, batch) =>
  api.get(`/api/v1/ceo/batchBelowAverage?studentClass=${studentClass}&batch=${batch}`);
