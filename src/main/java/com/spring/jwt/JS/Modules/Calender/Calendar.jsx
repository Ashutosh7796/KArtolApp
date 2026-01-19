import React, { useState, useEffect, useRef } from "react";
import "../../Modules/Calender/Calendar.css";
import Header from "../../Components/Header/Header";
import Sidebar from "../../Components/SideBar/SideBar";
import {
  createCalendarEvents,
  updateCalendarEvent,
  getMonthEvents,
  getUpcomingEvents,
} from "../../service/CalendarApi";

const generateRecurrenceId = () =>
  "r-" + Math.random().toString(36).substring(2, 10) + Date.now().toString(36);

const TYPE_COLOR_MAP = {
  MEETING: "#6C5CE7",
  EXAM: "#5A189A",
  HOLIDAY: "#10B981",
  EVENT: "#2563EB",
  REMINDER: "#EF4444",
  OTHER: "#F59E0B",
};

const Calendar = () => {
  const [currentView, setCurrentView] = useState("day");
  const [currentDate, setCurrentDate] = useState(new Date());
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [events, setEvents] = useState({});
  const [toast, setToast] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [modalInitialForm, setModalInitialForm] = useState(null);
  const [modalMode, setModalMode] = useState("create");
  const [editingEventId, setEditingEventId] = useState(null);
  const [editingOriginalDate, setEditingOriginalDate] = useState(null);
  const [editSeriesChoice, setEditSeriesChoice] = useState("single");
  const [upcomingEvents, setUpcomingEvents] = useState([]);
  const [upcomingExams, setUpcomingExams] = useState([]);

  const toggleSidebar = () => setSidebarOpen((s) => !s);

  useEffect(() => {
    if (!toast) return;
    const id = setTimeout(() => setToast(null), 3000);
    return () => clearTimeout(id);
  }, [toast]);

  const showSuccess = (m) => setToast({ type: "success", message: m });
  const showError = (m) => setToast({ type: "error", message: m });

  const formatMonthYear = (date) =>
    date.toLocaleDateString("en-US", { month: "long", year: "numeric" });

  const getLocalDateKey = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  function formatDateInput(d) {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${day}`;
  }

  function buildLocalDateTime(dateObj, hh, mm) {
    const y = dateObj.getFullYear();
    const m = String(dateObj.getMonth() + 1).padStart(2, "0");
    const d = String(dateObj.getDate()).padStart(2, "0");
    const h = String(hh).padStart(2, "0");
    const min = String(mm).padStart(2, "0");

    return `${y}-${m}-${d}T${h}:${min}:00`;
  }

  function formatTimeForSlot(time24) {
    if (!time24) return "All Day";
    const [hhRaw, mm] = time24.split(":").map((x) => parseInt(x, 10));
    let hh = hhRaw;
    const suffix = hh >= 12 ? "PM" : "AM";
    hh = hh % 12 || 12;
    return `${hh}:${String(mm).padStart(2, "0")} ${suffix}`;
  }

  useEffect(() => {
    const fetchMonthEvents = async () => {
      try {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth() + 1;

        const res = await getMonthEvents(year, month);

        const data = res.data || [];

        const grouped = {};
        data.forEach((ev) => {
          const dateKey = ev.startDateTime.split("T")[0];
          if (!grouped[dateKey]) grouped[dateKey] = [];
          grouped[dateKey].push({
            title: ev.title,
            description: ev.description,
            startTime: ev.startDateTime.slice(11, 16),
            endTime: ev.endDateTime.slice(11, 16),
            time: `${formatTimeForSlot(
              ev.startDateTime.slice(11, 16)
            )} - ${formatTimeForSlot(ev.endDateTime.slice(11, 16))}`,
            color: ev.colorCode,
            serverId: ev.id,
            recurrenceId: ev.recurrenceId,
            recurrence: ev.recurrence,
            selectedWeekdays: ev.selectedWeekdays,
            eventType: ev.eventType,
          });
        });

        setEvents(grouped);
      } catch (err) {
        console.error("Month events fetch error", err);
      }
    };

    fetchMonthEvents();
  }, [currentDate]);

  useEffect(() => {
    const fetchUpcoming = async () => {
      try {
        const res = await getUpcomingEvents();
        setUpcomingEvents(res.data?.events || []);
        setUpcomingExams(res.data?.exams || []);
      } catch (err) {
        console.error("Upcoming fetch error", err);
      }
    };

    fetchUpcoming();
  }, []);

  const openCreateModalForDate = (date, defaultHour = "09:00") => {
    setModalInitialForm({
      title: "",
      description: "",
      date: formatDateInput(date),
      startTime: defaultHour,
      endTime: defaultHour === "09:00" ? "10:00" : defaultHour,
      eventType: "",
      colorCode: "",
      recurrence: "NONE",
      recurrenceId: generateRecurrenceId(),
      selectedWeekdays: [false, false, false, false, false, false, false],
    });
    setModalMode("create");
    setEditingEventId(null);
    setEditingOriginalDate(null);
    setEditSeriesChoice("single");
    setShowModal(true);
  };

  const openEditModalForEvent = (eventObj, dateKey) => {
    setModalInitialForm({
      title: eventObj.title,
      description: eventObj.description,
      date: dateKey,
      startTime: eventObj.startTime || "09:00",
      endTime: eventObj.endTime || "10:00",
      eventType: eventObj.eventType ?? "",
      colorCode: eventObj.color || "#C77DFF",
      recurrence: eventObj.recurrence || "NONE",
      recurrenceId: eventObj.recurrenceId || generateRecurrenceId(),
      selectedWeekdays: eventObj.selectedWeekdays || [
        false,
        false,
        false,
        false,
        false,
        false,
        false,
      ],
    });
    setModalMode("edit");
    setEditingEventId(eventObj.serverId);
    setEditingOriginalDate(dateKey);
    setEditSeriesChoice("single");
    setShowModal(true);
  };

  const handleModalSaved = (dateKey, savedEvent) => {
    setEvents((prev) => {
      const next = { ...prev };

      if (
        modalMode === "edit" &&
        editSeriesChoice === "all" &&
        savedEvent.recurrenceId
      ) {
        Object.keys(next).forEach((k) => {
          next[k] = (next[k] || []).map((ev) =>
            ev.recurrenceId === savedEvent.recurrenceId
              ? { ...ev, ...savedEvent, serverId: ev.serverId }
              : ev
          );
        });
        return next;
      }

      if (modalMode === "edit" && editingOriginalDate) {
        const origArr = Array.isArray(next[editingOriginalDate])
          ? [...next[editingOriginalDate]]
          : [];
        const afterRemoval = origArr.filter(
          (ev) => ev.serverId !== savedEvent.serverId
        );
        if (afterRemoval.length) next[editingOriginalDate] = afterRemoval;
        else delete next[editingOriginalDate];
      }

      const existing = Array.isArray(next[dateKey]) ? [...next[dateKey]] : [];

      if (modalMode === "edit") {
        const idx = existing.findIndex(
          (ev) => ev.serverId === savedEvent.serverId
        );
        if (idx >= 0) existing[idx] = savedEvent;
        else existing.push(savedEvent);
      } else {
        existing.push(savedEvent);
      }

      next[dateKey] = existing;
      return next;
    });

    if (
      modalMode === "edit" &&
      editingOriginalDate &&
      editingOriginalDate !== dateKey
    ) {
      setCurrentDate(new Date(dateKey));
    } else if (modalMode === "create") {
      try {
        const [y, m, d] = dateKey.split("-").map((n) => parseInt(n, 10));
        setCurrentDate(new Date(y, m - 1, d));
      } catch (e) {}
    }

    setEditingOriginalDate(null);
    showSuccess(modalMode === "edit" ? "Event updated" : "Event created");
  };

  const DAY_START_HOUR = 0;
  const DAY_END_HOUR = 24;
  const HOUR_ROW_PX = 60;
  const PIXELS_PER_MIN = HOUR_ROW_PX / 60;

  const weekdayIdxOf = (date) => (date.getDay() + 6) % 7;

  const getWeekDatesFor = (date) => {
    const d = new Date(date);
    const monday = new Date(d);
    monday.setDate(d.getDate() - ((d.getDay() + 6) % 7));
    const dates = [];
    for (let i = 0; i < 7; i++) {
      const dd = new Date(monday);
      dd.setDate(monday.getDate() + i);
      dates.push(dd);
    }
    return dates;
  };

  const getAllDatesInMonthFor = (date) => {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = d.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const arr = [];
    for (let i = 1; i <= daysInMonth; i++) arr.push(new Date(year, month, i));
    return arr;
  };

  const DayView = () => {
    const formattedDay = `${currentDate.getDate()} ${currentDate.toLocaleString(
      "en-GB",
      { month: "long" }
    )} ${currentDate.getFullYear()}`;

    const timeSlots = [];
    for (let i = DAY_START_HOUR; i < DAY_END_HOUR; i++) {
      const displayHour = i % 12 === 0 ? 12 : i % 12;
      const suffix = i >= 12 ? "PM" : "AM";
      timeSlots.push({
        hour: String(i).padStart(2, "0"),
        displayTime: `${displayHour}:00 ${suffix}`,
        inputTime: `${String(i).padStart(2, "0")}:00`,
      });
    }

    const dayKey = getLocalDateKey(currentDate);
    const dayEvents = events[dayKey] || [];

    const computeStyleForEvent = (ev) => {
      const toMinutes = (hhmm) => {
        if (!hhmm) return 0;
        const [hh, mm] = hhmm.split(":").map((n) => parseInt(n, 10));
        return hh * 60 + mm;
      };
      const startMin = toMinutes(ev.startTime || "09:00");
      const endMin = toMinutes(ev.endTime || "10:00");

      const dayStartMin = DAY_START_HOUR * 60;
      const dayEndMin = DAY_END_HOUR * 60;

      const clampedStart = Math.max(startMin, dayStartMin);
      const clampedEnd = Math.min(endMin, dayEndMin);

      const durationMin = Math.max(0, clampedEnd - clampedStart);

      const topPx = Math.round((clampedStart - dayStartMin) * PIXELS_PER_MIN);
      const heightPx = Math.max(8, Math.round(durationMin * PIXELS_PER_MIN));

      return {
        position: "absolute",
        left: 8,
        right: 8,
        top: `${topPx}px`,
        height: `${heightPx}px`,
        overflow: "hidden",
        borderRadius: 8,
        boxShadow: "0 2px 6px rgba(0,0,0,0.06)",
        padding: "6px 8px",
        cursor: "pointer",
        boxSizing: "border-box",
        display: "flex",
        flexDirection: "column",
        justifyContent: "flex-start",
      };
    };

    return (
      <div className="calendar-content day-view-layout">
        <div className="day-header-bar">{formattedDay}</div>
        <div className="day-grid">
          <div className="time-column">
            {timeSlots.map((slot, index) => (
              <div key={index} className="day-time-slot">
                {slot.displayTime}
              </div>
            ))}
          </div>

          <div className="day-events-column">
            {timeSlots.map((slot, idx) => (
              <div
                key={`row-${idx}`}
                style={{
                  height: `${HOUR_ROW_PX}px`,
                  borderBottom:
                    idx === timeSlots.length - 1 ? "none" : "1px solid #eee",
                  boxSizing: "border-box",
                }}
              />
            ))}

            {dayEvents.map((event, i) => {
              const styleForEvent = computeStyleForEvent(event);
              return (
                <div
                  key={event.serverId ?? i}
                  className="event-slot-day"
                  style={{
                    ...styleForEvent,
                    backgroundColor: event.color || "#C77DFF",
                    color: "#fff",
                    zIndex: 2,
                  }}
                  onClick={(e) => {
                    e.stopPropagation();
                    openEditModalForEvent(event, dayKey);
                  }}
                >
                  <div className="event-time">{event.time}</div>
                  <div className="event-title">{event.title}</div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    );
  };

  const WeekView = () => {
    const startOfWeek = new Date(currentDate);
    startOfWeek.setDate(
      currentDate.getDate() - ((currentDate.getDay() + 6) % 7)
    );

    const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

    const fmt = (d) =>
      `${d.getDate()} ${d.toLocaleString("en-GB", {
        month: "long",
      })}, ${d.getFullYear()}`;

    const weekEnd = new Date(startOfWeek);
    weekEnd.setDate(startOfWeek.getDate() + 6);

    return (
      <div className="calendar-content week-view">
        <div className="week-range-bar">
          {fmt(startOfWeek)} — {fmt(weekEnd)}
        </div>

        <div className="week-grid-layout">
          {days.map((day, index) => {
            const dayDate = new Date(startOfWeek);
            dayDate.setDate(startOfWeek.getDate() + index);
            const dayKey = getLocalDateKey(dayDate);
            const dayEvents = events[dayKey] || [];

            return (
              <div key={index} className="week-row-layout">
                <div className="week-day-cell">{day}</div>
                <div
                  className="week-event-cell"
                  onClick={() => openCreateModalForDate(dayDate)}
                  style={{ cursor: "pointer" }}
                >
                  {dayEvents.map((event, i) => (
                    <div
                      key={i}
                      className="event-slot-week"
                      style={{ backgroundColor: event.color, color: "#fff" }}
                      onClick={(e) => {
                        e.stopPropagation();
                        openEditModalForEvent(event, dayKey);
                      }}
                    >
                      {event.time && (
                        <div className="event-time">{event.time}</div>
                      )}
                      <div className="event-title">{event.title}</div>
                    </div>
                  ))}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    );
  };

  const MonthView = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const startOffset = firstDay === 0 ? 6 : firstDay - 1;
    const totalCells = 42;
    const cells = [];

    for (let i = 0; i < totalCells; i++) {
      const dayNumber = i - startOffset + 1;
      const isCurrentMonth = dayNumber > 0 && dayNumber <= daysInMonth;
      let dateKey = null;
      let dayEvents = [];
      if (isCurrentMonth) {
        dateKey = `${year}-${String(month + 1).padStart(2, "0")}-${String(
          dayNumber
        ).padStart(2, "0")}`;
        dayEvents = events[dateKey] || [];
      }

      cells.push(
        <div
          key={i}
          className={`month-day-cell ${!isCurrentMonth ? "outside-month" : ""}`}
          onClick={() =>
            isCurrentMonth &&
            openCreateModalForDate(new Date(year, month, dayNumber))
          }
          style={{ cursor: isCurrentMonth ? "pointer" : "default" }}
        >
          <div className="month-date">{isCurrentMonth ? dayNumber : ""}</div>
          {dayEvents.map((event, j) => (
            <div
              key={j}
              className="event-month-view"
              style={{ backgroundColor: event.color }}
              onClick={(e) => {
                e.stopPropagation();
                openEditModalForEvent(event, dateKey);
              }}
            >
              {event.title}
            </div>
          ))}
        </div>
      );
    }

    const weekdays = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sun"];

    return (
      <div className="calendar-content month-view">
        {/* Weekday header row */}
        <div className="month-weekdays-row">
          {weekdays.map((wd) => (
            <div key={wd} className="month-weekday-cell">
              {wd}
            </div>
          ))}
        </div>

        <div className="month-grid">{cells}</div>
      </div>
    );
  };

  const renderView = () => {
    switch (currentView) {
      case "day":
        return <DayView />;
      case "week":
        return <WeekView />;
      case "month":
        return <MonthView />;
      default:
        return <WeekView />;
    }
  };

  const EventModal = ({
    initialForm,
    onClose,
    onSaved,
    modalMode,
    editingEventId,
    editSeriesChoice,
    setEditSeriesChoice,
  }) => {
    const [local, setLocal] = useState(initialForm || {});
    const [saving, setSaving] = useState(false);
    const titleRef = useRef(null);

    const weekdayNames = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

    const parse24To12Parts = (time24) => {
      if (!time24) return { hour12: "09", minute: "00", ampm: "AM" };
      const [hhStr, mmStr] = time24.split(":");
      let hh = parseInt(hhStr, 10);
      const ampm = hh >= 12 ? "PM" : "AM";
      hh = hh % 12 || 12;
      return {
        hour12: String(hh).padStart(2, "0"),
        minute: String(mmStr).padStart(2, "0"),
        ampm,
      };
    };

    const build24From12 = (hour12, minute, ampm) => {
      let h = parseInt(hour12, 10);
      if (ampm === "PM" && h !== 12) h += 12;
      if (ampm === "AM" && h === 12) h = 0;
      return `${String(h).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
    };

    useEffect(() => {
      const init = initialForm || {};
      const s = init.startTime || "09:00";
      const e = init.endTime || "10:00";
      const sp = parse24To12Parts(s);
      const ep = parse24To12Parts(e);

      const initialType = init.eventType ? init.eventType.toUpperCase() : "";
      const initialColor =
        (initialType && TYPE_COLOR_MAP[initialType]) ||
        init.colorCode ||
        "#6C5CE7";

      setLocal({
        ...init,
        startTime: s,
        endTime: e,
        startHour12: sp.hour12,
        startMinute: sp.minute,
        startAMPM: sp.ampm,
        endHour12: ep.hour12,
        endMinute: ep.minute,
        endAMPM: ep.ampm,
        displayStart: `${sp.hour12}:${sp.minute} ${sp.ampm}`,
        displayEnd: `${ep.hour12}:${ep.minute} ${ep.ampm}`,
        eventType: init.eventType ?? "",
        colorCode: initialColor,
        recurrenceId: init.recurrenceId || generateRecurrenceId(),
        recurrence: init.recurrence || "NONE",
        selectedWeekdays: init.selectedWeekdays || [
          false,
          false,
          false,
          false,
          false,
          false,
          false,
        ],
      });

      setTimeout(() => {
        if (titleRef.current) titleRef.current.focus();
      }, 0);
    }, [initialForm]);

    const change = (field, value) => {
      const next = { ...local, [field]: value };

      if (field.startsWith("start")) {
        const hh = next.startHour12 || "09";
        const mm = next.startMinute || "00";
        const ap = next.startAMPM || "AM";
        next.startTime = build24From12(hh, mm, ap);
        next.displayStart = `${hh}:${mm} ${ap}`;
      }
      if (field.startsWith("end")) {
        const hh = next.endHour12 || "10";
        const mm = next.endMinute || "00";
        const ap = next.endAMPM || "AM";
        next.endTime = build24From12(hh, mm, ap);
        next.displayEnd = `${hh}:${mm} ${ap}`;
      }

      if (field === "eventType") {
        const up = (value || "").toUpperCase();
        next.eventType = up;
        next.colorCode = TYPE_COLOR_MAP[up] || TYPE_COLOR_MAP.MEETING;
      }

      setLocal(next);
    };

    const toggleWeekday = (index) => {
      const arr = Array.isArray(local.selectedWeekdays)
        ? [...local.selectedWeekdays]
        : [false, false, false, false, false, false, false];
      arr[index] = !arr[index];
      setLocal({ ...local, selectedWeekdays: arr });
    };

    const handleSubmit = async (ev) => {
      ev.preventDefault();
      if (!local.title || !local.title.trim()) {
        setToast({ type: "error", message: "Title is required" });
        return;
      }
      if (!local.eventType) {
        setToast({ type: "error", message: "Please select an event type" });
        return;
      }

      setSaving(true);

      try {
        const start24 =
          local.startTime ||
          build24From12(
            local.startHour12 || "09",
            local.startMinute || "00",
            local.startAMPM || "AM"
          );
        const end24 =
          local.endTime ||
          build24From12(
            local.endHour12 || "10",
            local.endMinute || "00",
            local.endAMPM || "AM"
          );

        const [y, m, d] = (local.date || "")
          .split("-")
          .map((n) => parseInt(n, 10));
        const dateObj = new Date(y, (m || 1) - 1, d || 1);

        const [sh, sm] = start24.split(":").map((n) => parseInt(n, 10));
        const [eh, em] = end24.split(":").map((n) => parseInt(n, 10));

        const startMinutes = sh * 60 + sm;
        const endMinutes = eh * 60 + em;
        if (endMinutes <= startMinutes) {
          setToast({
            type: "error",
            message: "End time must be after start time",
          });
          setSaving(false);
          return;
        }

        let selectedWeekdays = Array.isArray(local.selectedWeekdays)
          ? [...local.selectedWeekdays]
          : [false, false, false, false, false, false, false];
        if (!selectedWeekdays.some(Boolean)) {
          selectedWeekdays = [true, true, true, true, true, true, true];
        }

        if (modalMode === "edit" && editingEventId) {
          if (editSeriesChoice === "all" && local.recurrenceId) {
            const next = JSON.parse(JSON.stringify(events || {}));
            const matches = [];
            Object.entries(events).forEach(([dateKey, arr]) => {
              (arr || []).forEach((evItem) => {
                if (evItem.recurrenceId === local.recurrenceId) {
                  matches.push({ dateKey, evItem });
                }
              });
            });

            const recurrenceType = (local.recurrence || "NONE").toUpperCase();
            let datesToEnsure = [];
            if (recurrenceType === "WEEK") {
              let week = getWeekDatesFor(dateObj);
              week = week.filter((dt) => selectedWeekdays[weekdayIdxOf(dt)]);
              datesToEnsure = week;
            } else if (recurrenceType === "MONTH") {
              let monthDates = getAllDatesInMonthFor(dateObj);
              monthDates = monthDates.filter(
                (dt) => selectedWeekdays[weekdayIdxOf(dt)]
              );
              datesToEnsure = monthDates;
            } else {
              if (selectedWeekdays[weekdayIdxOf(dateObj)])
                datesToEnsure = [dateObj];
            }
            for (const match of matches) {
              const [yy, mmth, dd] = match.dateKey
                .split("-")
                .map((n) => parseInt(n, 10));
              const dt = new Date(yy, mmth - 1, dd);
              const widx = weekdayIdxOf(dt);
              if (!selectedWeekdays[widx]) {
                next[match.dateKey] = (next[match.dateKey] || []).filter(
                  (e) => e.serverId !== match.evItem.serverId
                );
                if (!next[match.dateKey] || !next[match.dateKey].length)
                  delete next[match.dateKey];
              } else {
                const payload = {
                  id: match.evItem.serverId,
                  title: local.title,
                  description: local.description || "",
                  startDateTime: buildLocalDateTime(dt, sh, sm),
                  endDateTime: buildLocalDateTime(dt, eh, em),
                  eventType: local.eventType || "MEETING",
                  colorCode:
                    local.colorCode ||
                    TYPE_COLOR_MAP[local.eventType] ||
                    TYPE_COLOR_MAP.MEETING,
                };
                try {
                  if (match.evItem.serverId) {
                    await updateCalendarEvent(match.evItem.serverId, payload);
                  }
                } catch (err) {
                  console.error(
                    "Update occurrence failed for",
                    match.dateKey,
                    err
                  );
                }

                next[match.dateKey] = (next[match.dateKey] || []).map((e) =>
                  e.serverId === match.evItem.serverId
                    ? {
                        ...e,
                        title: local.title,
                        description: local.description ?? e.description,
                        startTime: start24,
                        endTime: end24,
                        time: `${formatTimeForSlot(
                          start24
                        )} - ${formatTimeForSlot(end24)}`,
                        color: local.colorCode || e.color,
                        recurrence: local.recurrence || e.recurrence,
                        selectedWeekdays,
                        eventType: local.eventType ?? e.eventType,
                      }
                    : e
                );
              }
            }

            for (const dt of datesToEnsure) {
              const key = getLocalDateKey(dt);
              const alreadyExists = (next[key] || []).some(
                (e) => e.recurrenceId === local.recurrenceId
              );
              if (alreadyExists) continue;

              const payload = {
                id: 0,
                title: local.title,
                description: local.description || "",
                startDateTime: buildLocalDateTime(dt, sh, sm),
                endDateTime: buildLocalDateTime(dt, eh, em),
                eventType: local.eventType || "MEETING",
                colorCode: local.colorCode || TYPE_COLOR_MAP.MEETING,
              };
              try {
                const resp = await createCalendarEvents(payload);
                const saved = {
                  title: local.title,
                  description: local.description,
                  time: `${formatTimeForSlot(start24)} - ${formatTimeForSlot(
                    end24
                  )}`,
                  color: payload.colorCode,
                  serverId: resp?.data?.id ?? null,
                  startTime: start24,
                  endTime: end24,
                  recurrenceId: local.recurrenceId,
                  recurrence: local.recurrence || "MONTH",
                  selectedWeekdays,
                  eventType: local.eventType ?? "",
                };
                next[key] = [...(next[key] || []), saved];
              } catch (err) {
                console.error("Create occurrence failed for", key, err);
              }
            }

            Object.keys(next).forEach((k) => {
              next[k] = (next[k] || []).map((ev) =>
                ev.recurrenceId === local.recurrenceId
                  ? {
                      ...ev,
                      title: local.title,
                      description: local.description || ev.description,
                      startTime: start24,
                      endTime: end24,
                      time: `${formatTimeForSlot(
                        start24
                      )} - ${formatTimeForSlot(end24)}`,
                      color: local.colorCode || ev.color,
                      recurrence: local.recurrence || ev.recurrence,
                      selectedWeekdays,
                      eventType: local.eventType ?? ev.eventType,
                    }
                  : ev
              );
            });
            setEvents(next);
            onClose();
            setSaving(false);
            showSuccess("All events in series updated");
            return;
          }

          const payload = {
            id: editingEventId,
            title: local.title,
            description: local.description || "",
            startDateTime: buildLocalDateTime(dateObj, sh, sm),
            endDateTime: buildLocalDateTime(dateObj, eh, em),
            eventType: local.eventType || "MEETING",
            colorCode: local.colorCode || TYPE_COLOR_MAP.MEETING,
          };
          try {
            const resp = await updateCalendarEvent(editingEventId, payload);
            if (resp && (resp.status === 200 || resp.status === 201)) {
              const saved = {
                title: local.title,
                description: local.description,
                time: `${formatTimeForSlot(start24)} - ${formatTimeForSlot(
                  end24
                )}`,
                color: payload.colorCode,
                serverId: resp.data?.id ?? editingEventId,
                startTime: start24,
                endTime: end24,
                recurrenceId: local.recurrenceId,
                recurrence: local.recurrence,
                selectedWeekdays: local.selectedWeekdays,
                eventType: local.eventType ?? "",
              };
              onSaved(local.date, saved);
              onClose();
              setSaving(false);
              return;
            } else {
              showError(`Save failed (${resp?.status})`);
            }
          } catch (err) {
            console.error("Update single occurrence failed", err);
            showError("Network/error updating event");
          }
        } else {
          const recurrence = (local.recurrence || "NONE").toUpperCase();
          let selWeekdays = Array.isArray(local.selectedWeekdays)
            ? [...local.selectedWeekdays]
            : [false, false, false, false, false, false, false];
          if (!selWeekdays.some(Boolean))
            selWeekdays = [true, true, true, true, true, true, true];

          let datesToCreate = [];
          if (recurrence === "WEEK") {
            let weekDates = getWeekDatesFor(dateObj);
            weekDates = weekDates.filter((dt) => selWeekdays[weekdayIdxOf(dt)]);
            datesToCreate = weekDates;
          } else if (recurrence === "MONTH") {
            let monthDates = getAllDatesInMonthFor(dateObj);
            monthDates = monthDates.filter(
              (dt) => selWeekdays[weekdayIdxOf(dt)]
            );
            datesToCreate = monthDates;
          } else {
            datesToCreate = [dateObj];
          }

          for (const dt of datesToCreate) {
            const payload = {
              id: 0,
              title: local.title,
              description: local.description || "",
              startDateTime: buildLocalDateTime(dt, sh, sm),
              endDateTime: buildLocalDateTime(dt, eh, em),
              eventType: local.eventType || "MEETING",
              colorCode: local.colorCode || TYPE_COLOR_MAP.MEETING,
            };

            try {
              const resp = await createCalendarEvents(payload);
              if (resp && (resp.status === 200 || resp.status === 201)) {
                const saved = {
                  title: local.title,
                  description: local.description,
                  time: `${formatTimeForSlot(start24)} - ${formatTimeForSlot(
                    end24
                  )}`,
                  color: payload.colorCode,
                  serverId: resp.data?.id ?? null,
                  startTime: start24,
                  endTime: end24,
                  recurrenceId: local.recurrenceId,
                  recurrence,
                  selectedWeekdays: local.selectedWeekdays,
                };
                const key = getLocalDateKey(dt);
                onSaved(key, saved);
              } else {
                showError(
                  `Save failed for ${formatDateInput(dt)} (${resp?.status})`
                );
              }
            } catch (err) {
              console.error("Create event error for", dt, err);
              showError(`Network/error creating for ${formatDateInput(dt)}`);
            }
          }

          onClose();
          setSaving(false);
          return;
        }
      } catch (err) {
        console.error("Modal save error", err);
        showError(err?.message || "Error saving event.");
      } finally {
        setSaving(false);
      }
    };

    const hourOptions12 = Array.from({ length: 12 }, (_, i) =>
      String(i + 1).padStart(2, "0")
    );
    const minuteOptions = ["00", "15", "30", "45"];

    return (
      <div
        className="event-modal-backdrop"
        onClick={onClose}
        role="dialog"
        aria-modal="true"
      >
        <div className="event-modal-card" onClick={(e) => e.stopPropagation()}>
          <h3 className="event-modal-title">
            {modalMode === "edit" ? "Edit Event" : "Create Event"}
          </h3>

          <form onSubmit={handleSubmit} className="event-modal-form">
            <div className="event-field type-field">
              <label className="event-label">Type</label>
              <div className="type-control">
                <select
                  className="event-input"
                  value={local.eventType ?? ""}
                  onChange={(e) => change("eventType", e.target.value)}
                >
                  <option value="" disabled>
                    Select type
                  </option>
                  <option value="MEETING">MEETING</option>
                  <option value="EXAM">EXAM</option>
                  <option value="HOLIDAY">HOLIDAY</option>
                  <option value="EVENT">EVENT</option>
                  <option value="REMINDER">REMINDER</option>
                  <option value="OTHER">OTHER</option>
                </select>
              </div>
            </div>

            <div className="event-field">
              <label className="event-label">Title *</label>
              <input
                ref={titleRef}
                className="event-input"
                value={local.title || ""}
                onChange={(e) => change("title", e.target.value)}
              />
            </div>

            <div className="event-row">
              <div className="event-col">
                <label className="event-label">Date</label>
                <input
                  type="date"
                  className="event-input"
                  value={local.date || ""}
                  onChange={(e) => change("date", e.target.value)}
                />
              </div>

              <div className="event-col small">
                <label className="event-label">Recurrence</label>
                <select
                  className="event-input"
                  value={local.recurrence || "NONE"}
                  onChange={(e) => change("recurrence", e.target.value)}
                >
                  <option value="NONE">Single day</option>
                  <option value="WEEK">This Week</option>
                  <option value="MONTH">This Month</option>
                </select>
              </div>

              <div className="event-col small">
                <label className="event-label">Start Time</label>
                <div className="time-group">
                  <select
                    className="time-select"
                    value={local.startHour12 || "09"}
                    onChange={(e) => change("startHour12", e.target.value)}
                    aria-label="Start hour"
                  >
                    {hourOptions12.map((h) => (
                      <option key={h} value={h}>
                        {h}
                      </option>
                    ))}
                  </select>

                  <select
                    className="time-select"
                    value={local.startMinute || "00"}
                    onChange={(e) => change("startMinute", e.target.value)}
                    aria-label="Start minute"
                  >
                    {minuteOptions.map((m) => (
                      <option key={m} value={m}>
                        {m}
                      </option>
                    ))}
                  </select>

                  <select
                    className="time-select"
                    value={local.startAMPM || "AM"}
                    onChange={(e) => change("startAMPM", e.target.value)}
                    aria-label="Start AM/PM"
                  >
                    <option value="AM">AM</option>
                    <option value="PM">PM</option>
                  </select>
                </div>
              </div>

              <div className="event-col small">
                <label className="event-label">End Time</label>
                <div className="time-group">
                  <select
                    className="time-select"
                    value={local.endHour12 || "10"}
                    onChange={(e) => change("endHour12", e.target.value)}
                    aria-label="End hour"
                  >
                    {hourOptions12.map((h) => (
                      <option key={h} value={h}>
                        {h}
                      </option>
                    ))}
                  </select>

                  <select
                    className="time-select"
                    value={local.endMinute || "00"}
                    onChange={(e) => change("endMinute", e.target.value)}
                    aria-label="End minute"
                  >
                    {minuteOptions.map((m) => (
                      <option key={m} value={m}>
                        {m}
                      </option>
                    ))}
                  </select>

                  <select
                    className="time-select"
                    value={local.endAMPM || "AM"}
                    onChange={(e) => change("endAMPM", e.target.value)}
                    aria-label="End AM/PM"
                  >
                    <option value="AM">AM</option>
                    <option value="PM">PM</option>
                  </select>
                </div>
              </div>
            </div>

            <div className="event-field">
              <label className="event-label">Pick weekdays (optional)</label>
              <div
                className="weekday-checkbox-row"
                role="group"
                aria-label="Weekday selection"
              >
                {weekdayNames.map((w, idx) => {
                  const checked = Array.isArray(local.selectedWeekdays)
                    ? !!local.selectedWeekdays[idx]
                    : false;
                  return (
                    <label
                      key={w}
                      className={`weekday-checkbox ${checked ? "checked" : ""}`}
                    >
                      <input
                        type="checkbox"
                        checked={checked}
                        onChange={() => toggleWeekday(idx)}
                        aria-label={w}
                      />
                      <span className="weekday-label">{w}</span>
                    </label>
                  );
                })}
              </div>
              <div style={{ marginTop: 6, fontSize: 12, color: "#6b7280" }}>
                Tip: leave all unchecked to create for every day (default
                behavior).
              </div>
            </div>

            {modalMode === "edit" &&
              local.recurrenceId &&
              local.recurrence &&
              local.recurrence !== "NONE" && (
                <div className="event-field">
                  <label className="event-label">Apply changes to</label>
                  <div
                    style={{
                      display: "flex",
                      gap: 12,
                      alignItems: "center",
                      marginTop: 6,
                    }}
                  >
                    <label
                      style={{ display: "flex", gap: 6, alignItems: "center" }}
                    >
                      <input
                        type="radio"
                        name="edit-scope"
                        checked={editSeriesChoice === "single"}
                        onChange={() => setEditSeriesChoice("single")}
                      />
                      <span>This Event only</span>
                    </label>

                    <label
                      style={{ display: "flex", gap: 6, alignItems: "center" }}
                    >
                      <input
                        type="radio"
                        name="edit-scope"
                        checked={editSeriesChoice === "all"}
                        onChange={() => setEditSeriesChoice("all")}
                      />
                      <span>All repeating events </span>
                    </label>
                  </div>
                  <div style={{ marginTop: 6, fontSize: 12, color: "#6b7280" }}>
                    Choose whether to update only this date or every occurrence
                    created with the same series.
                  </div>
                </div>
              )}

            <div className="event-field">
              <label className="event-label">Description</label>
              <textarea
                className="event-textarea"
                value={local.description || ""}
                onChange={(e) => change("description", e.target.value)}
                rows={4}
              />
            </div>

            <div className="event-footer">
              <div className="event-actions">
                <button
                  type="button"
                  className="btn btn-cancel"
                  onClick={onClose}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-save"
                  disabled={saving}
                >
                  {saving ? "Saving..." : "Save"}
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    );
  };

  return (
    <div className="dashboard-container">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <div className="main-content">
        <Header />
        <div className="page-wrap">
          <div
            className="calender-header"
            style={{ display: "flex", alignItems: "center", gap: 12 }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
              <button
                className="hamburger"
                aria-label="menu"
                onClick={toggleSidebar}
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="20"
                  height="14"
                  viewBox="0 0 20 14"
                  fill="none"
                >
                  <rect width="20" height="2" rx="1" fill="currentColor" />
                  <rect
                    y="6"
                    width="12"
                    height="2"
                    rx="1"
                    fill="currentColor"
                  />
                  <rect
                    y="12"
                    width="20"
                    height="2"
                    rx="1"
                    fill="currentColor"
                  />
                </svg>
              </button>
              <div className="calender-head">Calendar</div>
            </div>

            <div style={{ marginLeft: "auto" }}>
              <button
                onClick={() => openCreateModalForDate(currentDate)}
                style={{
                  padding: "8px 12px",
                  borderRadius: 8,
                  background: "#111827",
                  color: "#fff",
                  border: "none",
                  cursor: "pointer",
                }}
              >
                + Create Event
              </button>
            </div>
          </div>

          <div className="calendar-container">
            <div className="calendar-header-nav">
              <div className="navigation">
                <span
                  className="arrow"
                  onClick={() => {
                    const d = new Date(currentDate);
                    d.setMonth(currentDate.getMonth() - 1);
                    setCurrentDate(d);
                  }}
                >
                  ←
                </span>
                <span className="current-display">
                  {formatMonthYear(currentDate)}
                </span>
                <span
                  className="arrow"
                  onClick={() => {
                    const d = new Date(currentDate);
                    d.setMonth(currentDate.getMonth() + 1);
                    setCurrentDate(d);
                  }}
                >
                  →
                </span>
              </div>
              <div className="view-switcher">
                <button
                  className={`view-button ${
                    currentView === "day" ? "active" : ""
                  }`}
                  onClick={() => setCurrentView("day")}
                >
                  Day
                </button>
                <button
                  className={`view-button ${
                    currentView === "week" ? "active" : ""
                  }`}
                  onClick={() => setCurrentView("week")}
                >
                  Week
                </button>
                <button
                  className={`view-button ${
                    currentView === "month" ? "active" : ""
                  }`}
                  onClick={() => setCurrentView("month")}
                >
                  Month
                </button>
              </div>
            </div>

            {renderView()}
          </div>
        </div>
      </div>

      {showModal && (
        <EventModal
          initialForm={modalInitialForm}
          onClose={() => setShowModal(false)}
          onSaved={(dateKey, savedEvent) =>
            handleModalSaved(dateKey, savedEvent)
          }
          modalMode={modalMode}
          editingEventId={editingEventId}
          editSeriesChoice={editSeriesChoice}
          setEditSeriesChoice={setEditSeriesChoice}
        />
      )}

      {toast && (
        <div
          className={`floating-toast ${
            toast.type === "success" ? "success" : "error"
          }`}
          role="status"
          aria-live="polite"
        >
          <div className="toast-icon">
            {toast.type === "success" ? "✓" : "!"}
          </div>
          <div className="toast-text">{toast.message}</div>
        </div>
      )}
    </div>
  );
};

export default Calendar;
