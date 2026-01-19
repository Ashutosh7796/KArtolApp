import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
import "./MonthlySalary.css";
import { FaEdit } from "react-icons/fa";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
 
import MonthlySalaryApi from "../../../service/TeacherApi/MonthlysalaryApi";
 
export default function MonthlySalary() {
  const navigate = useNavigate();
 
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [payLoading, setPayLoading] = useState(false);
 
  const [isEditMode, setIsEditMode] = useState(false);
  const [editData, setEditData] = useState(null);
 
  const [activeTeachers, setActiveTeachers] = useState([]);
  const [structures, setStructures] = useState([]);
  const [selectedRecord, setSelectedRecord] = useState(null);
 
  const [filters, setFilters] = useState({
    teacherId: "",
    teacherName: "",
    month: "",
    year: "",
  });
 
  useEffect(() => {
    fetchSalaryRecords();
    fetchActiveTeachers();
  }, []);
 

  useEffect(() => {
    checkForUnpaidRecord();
  }, [filters, structures]);
 
  const checkForUnpaidRecord = () => {
    const { teacherId, month, year } = filters;
 
    if (!teacherId || !month || !year) {
      setSelectedRecord(null);
      return;
    }
 
    const record = structures.find(
      (item) =>
        String(item.teacherId) === String(teacherId) &&
        item.month === month &&
        String(item.year) === String(year) &&
        item.status === "UNPAID"
    );
 
    setSelectedRecord(record || null);
  };
 
 
  const fetchActiveTeachers = async () => {
    try {
      const res = await MonthlySalaryApi.getActiveTeachers();
      setActiveTeachers(res.data?.data || []);
    } catch {
      //alert("Failed to load active teachers");
      console.log("Failed to load active teachers");
    }
  };
 
  const fetchSalaryRecords = async () => {
    try {
      setLoading(true);
      const res = await MonthlySalaryApi.getSalaryRecords();
      setStructures(res.data?.data || []);
    } catch (err) {
      console.log("Failed to load salary records.");
    } finally {
      setLoading(false);
    }
  };
 
  //   const handleMarkAsPay = async () => {
  //     if (!selectedRecord) return;
 
  //     setPayLoading(true);
  //     try {
  //       await MonthlySalaryApi.markAsPay({
  //         teacherId: filters.teacherId,
  //         month: filters.month,
  //         year: filters.year,
  //       });
 
  //      toast.success("Salary marked as PAID successfully!");
 
  //       fetchSalaryRecords();
  //       setSelectedRecord(null);
  //     } catch (err) {
  // toast.error("Failed to mark salary as paid.");
  //     } finally {
  //       setPayLoading(false);
  //     }
  //   };
 
  const handleMarkAsPay = async () => {
    if (!selectedRecord) return;
 
    setPayLoading(true);
    try {
      await MonthlySalaryApi.markAsPay({
        teacherId: filters.teacherId,
        month: filters.month,
        year: filters.year,
      });
 
      toast.success("Salary marked as PAID successfully!");
      fetchSalaryRecords();
      setSelectedRecord(null);
    } catch (err) {
      toast.error("Failed to mark salary as paid.");
    } finally {
      setPayLoading(false);
    }
  };
 
  const onChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };
 
  const handleEdit = (item) => {
    setIsEditMode(true);
    setEditData({
      ...item,
      deduction: item.deduction || 0,
      status: item.status || "UNPAID",
    });
 
    setFilters({
      teacherId: item.teacherId,
      teacherName: item.teacherName,
      month: item.month,
      year: item.year,
    });
  };
 
  const handleEditChange = (e) => {
    setEditData({ ...editData, [e.target.name]: e.target.value });
  };
 
  //   const submitMonthlySalary = async (e) => {
  //     e.preventDefault();
  //     setLoading(true);
 
  //     try {
  //       await MonthlySalaryApi.generateSalary({
  //         teacherId: Number(filters.teacherId),
  //         month: filters.month,
  //         year: Number(filters.year),
  //       });
 
  //      toast.success("Salary generated successfully!");
 
  //       fetchSalaryRecords();
  //       setFilters({ teacherId: "", teacherName: "", month: "", year: "" });
  //     } catch (err) {
  // toast.error(err.response?.data?.message || "Failed to generate salary.");    } finally {
  //       setLoading(false);
  //     }
  //   };
 
  const submitMonthlySalary = async (e) => {
    e.preventDefault();
    setLoading(true);
 
    try {
      await MonthlySalaryApi.generateSalary({
        teacherId: Number(filters.teacherId),
        month: filters.month,
        year: Number(filters.year),
      });
 
      toast.success("Salary generated successfully!");
      fetchSalaryRecords();
      setFilters({ teacherId: "", teacherName: "", month: "", year: "" });
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to generate salary.");
    } finally {
      setLoading(false);
    }
  };
 
  // const updateMonthlySalary = async (e) => {
  //   e.preventDefault();
  //   setLoading(true);
 
  //   try {
  //     await MonthlySalaryApi.updateMonthlySalary(
  //       {
  //         teacherId: editData.teacherId,
  //         month: editData.month,
  //         year: editData.year,
  //       },
  //       {
  //         deduction: Number(editData.deduction),
  //         status: editData.status,
  //       }
  //     );
 
  //     toast.alert("Monthly salary updated successfully!");
  //     setIsEditMode(false);
  //     setEditData(null);
  //     setFilters({ teacherId: "", teacherName: "", month: "", year: "" });
  //     fetchSalaryRecords();
  //   } catch (err) {
  //     toast.alert(err.response?.data?.message || "Failed to update salary.");
  //   } finally {
  //     setLoading(false);
  //   }
  // };
 
  const updateMonthlySalary = async (e) => {
    e.preventDefault();
    setLoading(true);
 
    try {
      await MonthlySalaryApi.updateMonthlySalary(
        {
          teacherId: editData.teacherId,
          month: editData.month,
          year: editData.year,
        },
        {
          deduction: Number(editData.deduction),
          status: editData.status,
        }
      );
 
      toast.success("Monthly salary updated successfully!");
      setIsEditMode(false);
      setEditData(null);
      setFilters({ teacherId: "", teacherName: "", month: "", year: "" });
      fetchSalaryRecords();
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to update salary.");
    } finally {
      setLoading(false);
    }
  };
 
  return (
    <div className="monthly-page">
      <Header />
      <ToastContainer
        position="top-center"
        autoClose={3000}
        hideProgressBar={false}
        closeOnClick
        pauseOnHover
        draggable
        theme="colored"
      />
 
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
 
      <div className="monthly-wrapper">
        <div className="monthly-form-card">
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: "20px",
            }}
          >
            <h2 className="monthly-form-title">
              {isEditMode ? "Edit Monthly Salary" : "Generate Monthly Salary"}
            </h2>
 
         
          </div>
 
          <form
            className="monthly-form"
            onSubmit={isEditMode ? updateMonthlySalary : submitMonthlySalary}
          >
            <div className="monthly-group">
              <label>Teacher ID *</label>
              <input
                type="number"
                name="teacherId"
                required
                value={filters.teacherId}
                readOnly
              />
            </div>
 
            <div className="monthly-group">
              <label>Teacher Name *</label>
              <select
                name="teacherName"
                required
                value={filters.teacherName}
               onChange={(e) => {
  const selectedName = e.target.value;
  const teacher = activeTeachers.find(
    (t) => t.teacherName === selectedName
  );
 
  setFilters({
    ...filters,
    teacherName: selectedName,
    teacherId: teacher?.teacherId || "",
    month: teacher?.month || "",
    year: teacher?.year || "",
  });
}}
 
                disabled={isEditMode}
              >
                <option value="">Select Teacher</option>
                {activeTeachers.map((t) => (
                  <option key={t.teacherId} value={t.teacherName}>
                    {t.teacherName}
                  </option>
                ))}
              </select>
            </div>
 
            <div className="monthly-group">
              <label>Month *</label>
              <select
                name="month"
                required
                value={filters.month}
                onChange={onChange}
                disabled={isEditMode}
              >
                <option value="">Select Month</option>
                {[
                  "January",
                  "February",
                  "March",
                  "April",
                  "May",
                  "June",
                  "July",
                  "August",
                  "September",
                  "October",
                  "November",
                  "December",
                ].map((m) => (
                  <option key={m} value={m}>
                    {m}
                  </option>
                ))}
              </select>
            </div>
 
            <div className="monthly-group">
              <label>Year *</label>
              <select
                name="year"
                required
                value={filters.year}
                onChange={onChange}
                disabled={isEditMode}
              >
                <option value="">Select Year</option>
                {Array.from({ length: 6 }, (_, i) => 2023 + i).map((y) => (
                  <option key={y} value={y}>
                    {y}
                  </option>
                ))}
              </select>
            </div>
 
            {isEditMode && (
              <>
                <div className="monthly-group">
                  <label>Deduction *</label>
                  <input
                    type="number"
                    name="deduction"
                    value={editData?.deduction}
                    onChange={handleEditChange}
                  />
                </div>
 
                <div className="monthly-group">
                  <label>Status *</label>
                  <select
                    name="status"
                    value={editData?.status}
                    onChange={handleEditChange}
                  >
                    <option value="UNPAID">UNPAID</option>
                    <option value="PAID">PAID</option>
                  </select>
                </div>
              </>
            )}
 
            <div className="monthly-buttons">
              <button
                type="button"
                className="monthly-cancel-btn"
                onClick={() => navigate("/teacher/salary")}
              >
                Cancel
              </button>
 
              <button
                type="submit"
                className="monthly-save-btn"
                disabled={loading}
              >
                {loading
                  ? "Processing..."
                  : isEditMode
                  ? "Update Salary"
                  : "Generate Salary"}
              </button>
            </div>
          </form>
          {/* paybtn */}
          {selectedRecord && !isEditMode && (
            <div style={{ marginTop: "20px", textAlign: "center" }}>
              <button
                type="button"
                className="monthly-pay-btn"
                onClick={handleMarkAsPay}
                disabled={payLoading}
                style={{
                  backgroundColor: "#28a745",
                  color: "white",
                  border: "none",
                  padding: "12px 30px",
                  borderRadius: "5px",
                  cursor: "pointer",
                  fontWeight: "bold",
                  fontSize: "16px",
                  width: "100%",
                  maxWidth: "300px",
                }}
              >
                {payLoading ? "Processing..." : "Mark as Pay"}
              </button>
            </div>
          )}
        </div>
 
        <div className="salary-card">
          <table className="salary-table">
            <thead>
              <tr>
                <th>Sr</th>
                <th>Teacher ID</th>
                <th>Teacher Name</th>
                <th>Month</th>
                <th>Year</th>
                <th>Present</th>
                <th>Absent</th>
                <th>Late</th>
                <th>Half</th>
                <th>Total</th>
                <th>Per Day</th>
                <th>Calculated</th>
                <th>Deductions</th>
                <th>Final Salary</th>
                <th>Status</th>
                <th>Payment Date</th>
                <th>Actions</th>
              </tr>
            </thead>
 
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="17" style={{ textAlign: "center" }}>
                    Loading...
                  </td>
                </tr>
              ) : structures.length === 0 ? (
                <tr>
                  <td colSpan="17" style={{ textAlign: "center" }}>
                    No records found.
                  </td>
                </tr>
              ) : (
                structures.map((item, index) => (
                  <tr key={item.salaryId}>
                    <td>{index + 1}</td>
                    <td>{item.teacherId}</td>
                    <td>{item.teacherName}</td>
                    <td>{item.month}</td>
                    <td>{item.year}</td>
                    <td>{item.presentDays || 0}</td>
                    <td>{item.absentDays || 0}</td>
                    <td>{item.lateDays || 0}</td>
                    <td>{item.halfDays || 0}</td>
                    <td>{item.totalDays || 0}</td>
                    <td>₹{item.perDaySalary || 0}</td>
                    <td>₹{item.calculatedSalary || 0}</td>
                    <td>₹{item.deduction || 0}</td>
                    <td>₹{item.finalSalary || 0}</td>
                    <td>
                      <span
                        className={
                          item.status === "PAID"
                            ? "status-active"
                            : "status-inactive"
                        }
                      >
                        {item.status}
                      </span>
                    </td>
                    <td>{item.paymentDate || "-"}</td>
 
                    <td className="salary-action-buttons">
                      <button
                        className="salary-edit-icon-btn"
                        onClick={() => handleEdit(item)}
                      >
                        <FaEdit />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}