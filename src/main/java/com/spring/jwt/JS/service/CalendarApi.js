import api from './ApiConfig';

export const createCalendarEvents = (payload) => {
 return api.post(`/api/v1/calendar-events`,payload);
};

export const updateCalendarEvent=(id,payload)=>{
    return api.put(`/api/v1/calendar-events/${id}`,payload);
};

export const getMonthEvents = (year, month) => {
  return api.get("/api/v1/calendar-events/month", {
    params: { year, month },
  });
};
 
export const getUpcomingEvents = () => {
  return api.get("/api/v1/calendar-events/calendar/upcoming");
};
 




