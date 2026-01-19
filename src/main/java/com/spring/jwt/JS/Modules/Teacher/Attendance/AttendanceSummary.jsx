import React, { useEffect, useState } from "react";
import Sidebar from "../../../Components/SideBar/SideBar";
import Header from "../../../Components/Header/Header";
import AttendanceSummaryApi from "../../../service/TeacherApi/AttedanceSummaryApi";
import { FaEdit, FaTrash } from "react-icons/fa";
import "./AttendanceSummary.css";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Swal from "sweetalert2";

const formatTimeToHHMMSS = (time) => {
  if (!time) return null;
  return time.length === 5 ? `${time}:00` : time;
};

const AttendanceSummary = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [selectedTeacher, setSelectedTeacher] = useState(null);
  const [selectedMonth, setSelectedMonth] = useState("");
  const [selectedYear, setSelectedYear] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [records, setRecords] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState(null);

  const years = [2023, 2024, 2025];
  const months = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  ];

  useEffect(() => {
    fetchRecords();
  }, []);

  const fetchRecords = async () => {
    setLoading(true);
    try {
      let response;

      if ((selectedMonth || selectedYear) && !selectedTeacher) {
        toast.warning("Teacher ID is required for Month/Year filters");
        setRecords([]);
        setSummary(null);
        setLoading(false);
        return;
      }

      if (!selectedTeacher && !selectedDate && !selectedMonth && !selectedYear) {
        response = await AttendanceSummaryApi.getAll();
      } else if (selectedDate) {
        response = await AttendanceSummaryApi.getByDate(selectedDate);
      } else if (selectedTeacher && selectedMonth) {
        response = await AttendanceSummaryApi.getByTeacherAndMonth(selectedTeacher, selectedMonth);
      } else if (selectedTeacher && selectedYear) {
        response = await AttendanceSummaryApi.getByYear(selectedTeacher, selectedYear);
      } else if (selectedTeacher) {
        response = await AttendanceSummaryApi.getByTeacherId(selectedTeacher);
      }

      const data = response?.data?.data || [];
      setRecords(data);
      setSummary(null);
      setCurrentPage(1);

      if (data.length === 0) {
        if (selectedTeacher && selectedMonth)
          toast.info(`No records found for Teacher ID ${selectedTeacher} and Month ${selectedMonth}`);
        else if (selectedTeacher && selectedYear)
          toast.info(`No records found for Teacher ID ${selectedTeacher} and Year ${selectedYear}`);
        else if (selectedTeacher)
          toast.info(`No records found for Teacher ID ${selectedTeacher}`);
        else if (selectedDate)
          toast.info(`No records found for Date ${selectedDate}`);
        else toast.info("No records found");
      }
    } catch (error) {
      console.error("ERROR:", error);
      toast.error("Failed to fetch records");
      setRecords([]);
      setSummary(null);
    }
    setLoading(false);
  };

  const fetchSummary = async () => {
    if (!selectedTeacher || !selectedMonth) {
      toast.warning("Select Teacher ID and Month to get summary");
      return;
    }

    setLoading(true);
    try {
      const res = await AttendanceSummaryApi.getSummary(selectedTeacher, selectedMonth);
      setSummary(res?.data?.data || null);
      setRecords([]);
    } catch (e) {
      console.error("Summary error:", e);
      toast.error(`No records found for Teacher ID ${selectedTeacher} and Month ${selectedMonth}`);
    }
    setLoading(false);
  };

  const resetFilters = () => {
    setSelectedTeacher("");
    setSelectedMonth("");
    setSelectedYear("");
    setSelectedDate("");
    setRecords([]);
    setSummary(null);
    setCurrentPage(1);
    fetchRecords();
  };

  const totalPages = Math.ceil(records.length / itemsPerPage) || 1;
  const paginatedRecords = records.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  const handleEdit = (record) => {
    setEditRecord({
      ...record,
      date: record.date || "",
      month: record.month || "",
      inTime: record.inTime || "",
      outTime: record.outTime || "",
    });
    setEditModalOpen(true);
  };

  const handleEditSubmit = async () => {
    try {
      const { attendanceId, teacherId, date, month, inTime, outTime } = editRecord;

      await AttendanceSummaryApi.updateRecord(attendanceId, {
        teacherId,
        date,
        month,
        inTime: formatTimeToHHMMSS(inTime),
        outTime: formatTimeToHHMMSS(outTime),
      });

      toast.success("Record updated successfully");
      setEditModalOpen(false);
      fetchRecords();
    } catch (error) {
      console.error("Update error:", error);
      toast.error(error?.response?.data?.message || "Failed to update record");
    }
  };

  const handleDelete = async (id) => {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "You want to delete this attendance record!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Yes, delete it!",
      cancelButtonText: "Cancel",
    });

    if (!result.isConfirmed) return;

    try {
      await AttendanceSummaryApi.deleteRecord(id);
      toast.success("Record deleted successfully");
      fetchRecords();
    } catch (error) {
      console.error("Delete error:", error);
      toast.error("Failed to delete record");
    }
  };

  const handleAddAttendance = async () => {
    if (!selectedTeacher) {
      toast.warning("Please enter Teacher ID to add attendance");
      return;
    }

    setLoading(true);
    try {
      const res = await AttendanceSummaryApi.addAttendance(selectedTeacher);
      toast.success(res.data?.message || "Attendance added successfully");
      fetchRecords();
    } catch (error) {
      console.error("Add Attendance error:", error);
      toast.error(error?.response?.data?.message || "Failed to add attendance");
    }
    setLoading(false);
  };

  return (
    <>
      <Header />
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <ToastContainer position="top-center" autoClose={2000} hideProgressBar />

      <div className="attendance-summary-header">
        <div className="attendance-summary-header-root">
          <div className="attendance-summary-left">
            <button
              className="hamburger"
              aria-label="menu"
              onClick={() => setSidebarOpen(!sidebarOpen)}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="14" viewBox="0 0 20 14" fill="none">
                <rect width="20" height="2" rx="1" fill="currentColor" />
                <rect y="6" width="12" height="2" rx="1" fill="currentColor" />
                <rect y="12" width="20" height="2" rx="1" fill="currentColor" />
              </svg>
            </button>
            <h1 className="attendance-summary-title">Attendance Summary</h1>
          </div>

          <div className="attendance-summary-filter-box">
            <input
              type="number"
              placeholder="Teacher ID"
              className="pill-select"
              value={selectedTeacher ?? ""}
              onChange={(e) => {
                const value = e.target.value;
                if (value.length > 1 && value.startsWith("0")) return;
                setSelectedTeacher(value === "" ? null : Number(value));
                setSelectedDate("");
              }}
              min="0"
              step="1"
            />
            <input
              type="text"
              placeholder="dd-mm-yyyy"
              className="pill-select"
              value={selectedDate}
              onChange={(e) => {
                setSelectedDate(e.target.value);
                setSelectedTeacher("");
                setSelectedMonth("");
                setSelectedYear("");
              }}
            />
            <select
              className="pill-select"
              value={selectedYear}
              onChange={(e) => setSelectedYear(e.target.value)}
              disabled={!selectedTeacher || selectedMonth}
            >
              <option value="">Select Year</option>
              {years.map((y) => <option key={y}>{y}</option>)}
            </select>
            <select
              className="pill-select"
              value={selectedMonth}
              onChange={(e) => setSelectedMonth(e.target.value)}
              disabled={!selectedTeacher || selectedYear}
            >
              <option value="">Select Month</option>
              {months.map((m) => <option key={m}>{m}</option>)}
            </select>
            <button className="btn search-btn" onClick={fetchRecords}>Search</button>
            <button className="btn summary-btn" onClick={fetchSummary}>Get Summary</button>
            <button className="btn add-btn" onClick={handleAddAttendance}>Add Attendance</button>
            <button className="btn" style={{ background: "#6b7280", color: "white" }} onClick={resetFilters}>Reset</button>
          </div>
        </div>

        <div className="attendance-summary-card">
          {loading ? (
            <p className="loading-text">Loading...</p>
          ) : summary ? (
            <div className="summary-box">
              <h2>📌 Summary</h2>
              <div className="summary-grid">
                <p><strong>Teacher Name:</strong> {summary.teacherName}</p>
                <p><strong>Month:</strong> {summary.month}</p>
                <p><strong>Total Days:</strong> {summary.totalDays}</p>
                <p><strong>Full Days:</strong> {summary.fullDays}</p>
                <p><strong>Half Days:</strong> {summary.halfDays}</p>
                <p><strong>Absent:</strong> {summary.absentDays}</p>
                <p className="percent-text"><strong>Attendance:</strong> {summary.attendancePercentage}%</p>
              </div>
              <button className="back-btn" onClick={() => { setSummary(null); fetchRecords(); }}>← Back</button>
            </div>
          ) : (
            <>
              <table className="attendance-summary-table">
                <thead>
                  <tr>
                    <th>Attendance ID</th>
                    <th>Teacher ID</th>
                    <th>Name</th>
                    <th>Date</th>
                    <th>Month</th>
                    <th>In</th>
                    <th>Out</th>
                    <th>Mark</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedRecords.length > 0 ? (
                    paginatedRecords.map((rec) => (
                      <tr key={rec.attendanceId}>
                        <td>{rec.attendanceId}</td>
                        <td>{rec.teacherId}</td>
                        <td>{rec.teacherName}</td>
                        <td>{rec.date}</td>
                        <td>{rec.month}</td>
                        <td>{rec.inTime || "--"}</td>
                        <td>{rec.outTime || "--"}</td>
                        <td>{rec.mark}</td>
                        <td>
                          <button className="action-btn edit-btn" onClick={() => handleEdit(rec)}><FaEdit /></button>
                          <button className="action-btn delete-btn" onClick={() => handleDelete(rec.attendanceId)}><FaTrash /></button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="9" className="no-data">
                        {selectedTeacher && selectedMonth
                          ? `No records for Teacher ID ${selectedTeacher} in ${selectedMonth}`
                          : selectedTeacher && selectedYear
                          ? `No records for Teacher ID ${selectedTeacher} in ${selectedYear}`
                          : selectedTeacher
                          ? `No records for Teacher ID ${selectedTeacher}`
                          : selectedDate
                          ? `No records for Date ${selectedDate}`
                          : "No records found"}
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>

              {records.length > 0 && (
                <div className="as-results-pagination">
                  <div className="as-results-pagination-controls">
                    <button
                      className={`as-results-pagination-btn ${currentPage === 1 ? "as-results-pagination-btn-disabled" : ""}`}
                      disabled={currentPage === 1}
                      onClick={() => setCurrentPage(currentPage - 1)}
                    >
                      Previous
                    </button>

                    {Array.from({ length: Math.min(totalPages, 3) }, (_, i) => (
                      <button
                        key={i + 1}
                        onClick={() => setCurrentPage(i + 1)}
                        className={`as-results-pagination-page ${currentPage === i + 1 ? "as-results-pagination-page-active" : ""}`}
                      >
                        {i + 1}
                      </button>
                    ))}

                    {totalPages > 3 && (
                      <>
                        <span className="as-results-pagination-dots">...</span>
                        <button
                          onClick={() => setCurrentPage(totalPages)}
                          className={`as-results-pagination-page ${currentPage === totalPages ? "as-results-pagination-page-active" : ""}`}
                        >
                          {totalPages}
                        </button>
                      </>
                    )}

                    <button
                      className={`as-results-pagination-btn ${currentPage === totalPages ? "as-results-pagination-btn-disabled" : ""}`}
                      disabled={currentPage === totalPages}
                      onClick={() => setCurrentPage(currentPage + 1)}
                    >
                      Next
                    </button>
                  </div>

                  <div className="as-results-pagination-dropdown">
                    <select
                      className="as-results-pagination-select"
                      value={itemsPerPage}
                      onChange={(e) => { setItemsPerPage(Number(e.target.value)); setCurrentPage(1); }}
                    >
                      {[5, 10, 20, 50].map((count) => (
                        <option key={count} value={count}>{count}</option>
                      ))}
                    </select>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* Edit Modal */}
      {editModalOpen && editRecord && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Edit Attendance</h3>

            <label>Attendance ID</label>
            <input type="number" value={editRecord.attendanceId} disabled className="disabled-field" />

            <label>Teacher ID</label>
            <input type="number" value={editRecord.teacherId} disabled className="disabled-field" />

            <label>Teacher Name</label>
            <input type="text" value={editRecord.teacherName} disabled className="disabled-field" />

            <label>Date</label>
            <input type="text" value={editRecord.date} placeholder="DD-MM-YYYY" onChange={(e) => setEditRecord({ ...editRecord, date: e.target.value })} className="editable-field" />

            <label>Month</label>
            <input type="text" value={editRecord.month} disabled className="disabled-field" />

            <label>In Time</label>
            <input type="time" value={editRecord.inTime} onChange={(e) => setEditRecord({ ...editRecord, inTime: e.target.value })} className="editable-field" />

            <label>Out Time</label>
            <input type="time" value={editRecord.outTime} onChange={(e) => setEditRecord({ ...editRecord, outTime: e.target.value })} className="editable-field" />

            <div className="modal-actions">
              <button className="btn save-btn" onClick={handleEditSubmit}>Save</button>
              <button className="btn cancel-btn" onClick={() => setEditModalOpen(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default AttendanceSummary;
