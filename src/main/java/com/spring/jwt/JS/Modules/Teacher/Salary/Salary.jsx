import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FaEdit } from "react-icons/fa";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
import SalaryApi from "../../../service/TeacherApi/SalaryApi";
import "./Salary.css";
 
export default function Salary() {
  const navigate = useNavigate();
 
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [structures, setStructures] = useState([]);
  const [loading, setLoading] = useState(false);
 
  const toggleSidebar = () => setSidebarOpen((s) => !s);
 

 
  const fetchStructures = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      const res = await SalaryApi.getAllSalaryRecords(token);
 
      console.log("Salary API Response:", res.data);
      setStructures(res.data?.data || []);
    } catch (err) {
      console.error(" Salary API Error", err);
    } finally {
      setLoading(false);
    }
  };
 
  useEffect(() => {
    fetchStructures();
  }, []);
 
  return (
    <div className="salary-page">
      <Header />
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
 
      <div className="salary-wrapper">
        <div className="salary-header">
          <button
              className="salary-hamburger"
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
                <rect y="6" width="12" height="2" rx="1" fill="currentColor" />
                <rect y="12" width="20" height="2" rx="1" fill="currentColor" />
              </svg>
            </button>
 
          <h1 className="salary-title">Salary</h1>
 
          <div className="salary-btn-group">
            <button
              className="salary-add-btn"
              onClick={() => navigate("/teacher/salary/monthly")}
            >
              Monthly Salary
            </button>
 
            <button
              className="salary-add-btn"
              onClick={() => navigate("/teacher/salary/add")}
            >
              Add Salary Structure
            </button>
          </div>
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
                <th>Deduction</th>
                <th>Final</th>
                <th>Status</th>
                <th>Payment</th>
                {/* <th>Actions</th> */}
              </tr>
            </thead>
 
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="17" className="center">
                    Loading...
                  </td>
                </tr>
              ) : structures.length === 0 ? (
                <tr>
                  <td colSpan="17" className="center">
                    No records found
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
                    <td>{item.presentDays}</td>
                    <td>{item.absentDays}</td>
                    <td>{item.lateDays}</td>
                    <td>{item.halfDays}</td>
                    <td>{item.totalDays}</td>
                    <td>₹{item.perDaySalary}</td>
                    <td>₹{item.calculatedSalary}</td>
                    <td>₹{item.deduction}</td>
                    <td>₹{item.finalSalary}</td>
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
                    <td>
                      {/* <div className="salary-action-buttons">
                        <button className="salary-edit-icon-btn">
                          <FaEdit />
                        </button>
                      </div> */}
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