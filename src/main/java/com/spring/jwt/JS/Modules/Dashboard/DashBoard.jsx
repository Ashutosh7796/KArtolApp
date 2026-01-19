import React, { useState, useEffect } from "react";
import "../../Modules/Dashboard/DashBoard.css";
import Sidebar from "../../Components/SideBar/SideBar";
import Header from "../../Components/Header/Header";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  CartesianGrid,
} from "recharts";
import TopperIcon from "../../assets/Icons/Topper_Student_Dash.svg";
import AverageIcon from "../../assets/Icons/Average_Student_Dash.svg";
import BelowAverageIcon from "../../assets/Icons/Below_Average_Student_Dash.svg";

import {
  getMonthlyChart,
  getPieChart,
  getStudentClasses,
  getStudentBatches,
  getBatchToppers,
  getBatchAverage,
  getBatchBelowAverage,
} from "../../service/DashboardApi";


import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Swal from "sweetalert2";
import "sweetalert2/dist/sweetalert2.min.css";

const Dashboard = () => {
  const [isSidebarOpen, setSidebarOpen] = useState(false);
  const [activeModal, setActiveModal] = useState(null);

  const [dashClass, setDashClass] = useState("");
  const [dashYear, setDashYear] = useState("");

  const [selectedClass, setSelectedClass] = useState("");
  const [selectedBatch, setSelectedBatch] = useState("");

  const [classes, setClasses] = useState([]);
  const [batches, setBatches] = useState([]);

  const [topperStudents, setTopperStudents] = useState([]);
  const [averageStudents, setAverageStudents] = useState([]);
  const [belowAverageStudents, setBelowAverageStudents] = useState([]);

  const [monthlyChartData, setMonthlyChartData] = useState([]);
  const [pieChartData, setPieChartData] = useState([]);

  const [loading, setLoading] = useState(false);

  const COLORS = ["#C77DFF", "#5A189A"];

  const toggleSidebar = () => setSidebarOpen((s) => !s);
  const closeSidebar = () => setSidebarOpen(false);

  
  const fetchClassesAndBatches = async () => {
    try {
      const [clsRes, batchRes] = await Promise.all([
        getStudentClasses(),
        getStudentBatches(),
      ]);
      const cls = clsRes?.data?.data || [];
      const bat = batchRes?.data?.data || [];

      setClasses(cls);
      setBatches(bat);
      setSelectedClass("");
      setSelectedBatch("");
      setDashClass("");

      toast.success("Classes and batches loaded successfully");
    } catch (err) {
      console.error(err);
      toast.error("Failed to fetch classes or batches");
      Swal.fire("Oops!", "Failed to fetch classes or batches", "error");
    }
  };

  const fetchDashboardStudents = async (studentClass, batch) => {
    try {
      setLoading(true);

      const [top, avg, below] = await Promise.allSettled([
        getBatchToppers(studentClass, batch),
        getBatchAverage(studentClass, batch),
        getBatchBelowAverage(studentClass, batch),
      ]);

      setTopperStudents(
        top.status === "fulfilled"
          ? top.value.data.data.map((s) => ({
              name: `${s.name} ${s.lastName}`,
              percentage: `${s.percentage}%`,
            }))
          : []
      );

      setAverageStudents(
        avg.status === "fulfilled"
          ? avg.value.data.data.map((s) => ({
              name: `${s.name} ${s.lastName}`,
              percentage: `${s.percentage}%`,
            }))
          : []
      );

      setBelowAverageStudents(
        below.status === "fulfilled"
          ? below.value.data.data.map((s) => ({
              name: `${s.name} ${s.lastName}`,
              percentage: `${s.percentage}%`,
            }))
          : []
      );

      if (
        top.status !== "fulfilled" &&
        avg.status !== "fulfilled" &&
        below.status !== "fulfilled"
      ) {
        toast.warn("No student data available");
        Swal.fire("Info", "No student data available", "info");
      } else {
        toast.success("Student data loaded successfully");
      }
    } catch (err) {
      console.error(err);
      toast.error("Error fetching student data");
      Swal.fire("Error", "Failed to fetch student data", "error");
    } finally {
      setLoading(false);
    }
  };

  const fetchMonthlyChart = async (studentClass, batch) => {
    try {
      const res = await getMonthlyChart(studentClass, batch);
      const apiData = res?.data?.data || {};

      const months = [
        "JAN",
        "FEB",
        "MAR",
        "APR",
        "MAY",
        "JUN",
        "JUL",
        "AUG",
        "SEP",
        "OCT",
        "NOV",
        "DEC",
      ];

      const data = months.map((m) => {
        const obj = { month: m };
        if (studentClass === "11" || studentClass === "")
          obj.class11 = apiData["11"]?.[m] || 0;
        if (studentClass === "12" || studentClass === "")
          obj.class12 = apiData["12"]?.[m] || 0;
        return obj;
      });

      const hasData = data.some((d) => d.class11 || d.class12);
      setMonthlyChartData(hasData ? data : []);

      if (!hasData)
        Swal.fire("Info", "No monthly chart data available", "info");
    } catch (err) {
      console.error(err);
      toast.error("Failed to fetch monthly chart data");
      Swal.fire("Error", "Failed to fetch monthly chart data", "error");
      setMonthlyChartData([]);
    }
  };

  useEffect(() => {
    const fetchPie = async () => {
      try {
        const res = await getPieChart();
        const raw = res?.data?.data || {};
        setPieChartData([
          { name: "11th Class", value: raw["Students Class 11"] || 0 },
          { name: "12th Class", value: raw["Students Class 12"] || 0 },
        ]);
        toast.success("Pie chart loaded successfully");
      } catch (err) {
        console.error(err);
        toast.error("Failed to fetch pie chart data");
        Swal.fire("Error", "Failed to fetch pie chart data", "error");
      }
    };
    fetchPie();
  }, []);

  useEffect(() => {
    fetchClassesAndBatches();
  }, []);
  useEffect(() => {
    if (dashClass && dashYear) fetchDashboardStudents(dashClass, dashYear);
  }, [dashClass, dashYear]);
  useEffect(() => {
    fetchMonthlyChart(selectedClass, selectedBatch);
  }, [selectedClass, selectedBatch]);


  const renderTable = (students, title, range) => (
    <div className="modal" onClick={() => setActiveModal(null)}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>{title}</h3>
        <p>{range}</p>
        {loading ? (
          <p style={{ textAlign: "center" }}>Loading...</p>
        ) : students.length > 0 ? (
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr>
                <th>Student Name</th>
                <th>Percentage</th>
              </tr>
            </thead>
            <tbody>
              {students.map((s, i) => (
                <tr key={i}>
                  <td>{s.name}</td>
                  <td>{s.percentage}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p style={{ textAlign: "center" }}>No data available</p>
        )}
        <div style={{ textAlign: "center", marginTop: 12 }}>
          <button className="close-btn" onClick={() => setActiveModal(null)}>
            Close
          </button>
        </div>
      </div>
    </div>
  );

  const CustomTooltip = ({ active, payload, label }) => {
    if (!active || !payload || payload.length === 0) return null;
    return (
      <div className="custom-tooltip">
        <div style={{ fontWeight: 700, marginBottom: 6 }}>{label}</div>
        {payload.map((pl, i) => {
          const short = pl.name.includes("11") ? "11th Std" : "12th Std";
          return (
            <div key={i} style={{ color: pl.fill, fontWeight: 600 }}>
              {short} - {pl.value}%
            </div>
          );
        })}
      </div>
    );
  };

  
  return (
    <div className="dashboard-container">
     <Sidebar isOpen={isSidebarOpen} onClose={closeSidebar} />

      <div className="main-content">
        <Header />
        <ToastContainer
          position="top-center"
          autoClose={2000}
          hideProgressBar
          closeOnClick
          pauseOnHover
          draggable
          theme="colored"
        />

        <div className="page-wrap">
          
          <div className="dashboard-header">
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
                <rect y="6" width="12" height="2" rx="1" fill="currentColor" />
                <rect y="12" width="20" height="2" rx="1" fill="currentColor" />
              </svg>
            </button>
            <h2>Dashboard</h2>
            <div style={{ marginLeft: "auto", display: "flex", gap: "10px" }}>
              <div className="filters">
                <select
                  value={dashClass}
                  onChange={(e) => setDashClass(e.target.value)}
                >
                  <option value="">Class</option>
                  <option value="11">11</option>
                  <option value="12">12</option>
                </select>
              </div>
              <div className="filters">
                <select
                  value={dashYear}
                  onChange={(e) => setDashYear(e.target.value)}
                >
                  <option value="">Year</option>
                  <option value="2025">2025</option>
                  <option value="2024">2024</option>
                  <option value="2023">2023</option>
                </select>
              </div>
            </div>
          </div>

          
          <div className="cards">
            <div className="card topper">
              <h3>Topper Student</h3>
              <p>100% to 75%</p>
              <button
                onClick={() => {
                  if (!dashClass || !dashYear) {
                    Swal.fire(
                      "Info",
                      "Please select Class and Year first",
                      "warning"
                    );
                    return;
                  }
                  setActiveModal("topper");
                }}
              >
                View All
              </button>
              <div className="card-icon-wrap">
                <img src={TopperIcon} alt="topper" />
              </div>
            </div>

            <div className="card average">
              <h3>Average Student</h3>
              <p>75% to 50%</p>
              <button
                onClick={() => {
                  if (!dashClass || !dashYear) {
                    Swal.fire(
                      "Info",
                      "Please select Class and Year first",
                      "warning"
                    );
                    return;
                  }
                  setActiveModal("average");
                }}
              >
                View All
              </button>
              <div className="card-icon-wrap">
                <img src={AverageIcon} alt="average" />
              </div>
            </div>

            <div className="card below-average">
              <h3>Below Average Student</h3>
              <p>Below 50%</p>
              <button
                onClick={() => {
                  if (!dashClass || !dashYear) {
                    Swal.fire(
                      "Info",
                      "Please select Class and Year first",
                      "warning"
                    );
                    return;
                  }
                  setActiveModal("below");
                }}
              >
                View All
              </button>
              <div className="card-icon-wrap">
                <img src={BelowAverageIcon} alt="below" />
              </div>
            </div>
          </div>

          {activeModal === "topper" &&
            renderTable(topperStudents, "Topper Students", "100% to 75%")}
          {activeModal === "average" &&
            renderTable(averageStudents, "Average Students", "75% to 50%")}
          {activeModal === "below" &&
            renderTable(
              belowAverageStudents,
              "Below Average Students",
              "Below 50%"
            )}

          
          <div className="stats-header">
            <div className="section-title">Statistics</div>
            <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
              <div className="filters">
                <label>Class:</label>
                <select
                  value={selectedClass}
                  onChange={(e) => setSelectedClass(e.target.value)}
                >
                  <option value="">Select Class</option>
                  {classes.map((cls) => (
                    <option key={cls} value={cls}>
                      {cls}th
                    </option>
                  ))}
                </select>
              </div>
              <div className="filters">
                <label>Batch:</label>
                <select
                  value={selectedBatch}
                  onChange={(e) => setSelectedBatch(e.target.value)}
                >
                  <option value="">Select Batch</option>
                  {batches.map((batch) => (
                    <option key={batch} value={batch}>
                      {batch}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          <div className="stats">
            
            <div
              className="chart-card"
              aria-label="performance chart"
              style={{ position: "relative" }}
            >
              {monthlyChartData.length > 0 ? (
                <ResponsiveContainer width="100%" height={320}>
                  <BarChart
                    data={monthlyChartData}
                    margin={{ top: 40, right: 10, left: 10, bottom: 10 }}
                    barCategoryGap="30%"
                  >
                    <CartesianGrid stroke="#f1f1f1" vertical={false} />
                    <XAxis
                      dataKey="month"
                      tick={{ fontSize: 12, fill: "#6b7280" }}
                      axisLine={false}
                      tickLine={false}
                    />
                    <YAxis
                      axisLine={false}
                      tickLine={false}
                      tick={{ fontSize: 12, fill: "#6b7280" }}
                      domain={[0, 100]}
                      ticks={[0, 25, 50, 75, 100]}
                    />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend verticalAlign="top" align="right" />
                    {(selectedClass === "11" || selectedClass === "") && (
                      <Bar
                        dataKey="class11"
                        fill={COLORS[0]}
                        name="11th Class"
                        radius={[6, 6, 0, 0]}
                        barSize={18}
                      />
                    )}
                    {(selectedClass === "12" || selectedClass === "") && (
                      <Bar
                        dataKey="class12"
                        fill={COLORS[1]}
                        name="12th Class"
                        radius={[6, 6, 0, 0]}
                        barSize={18}
                      />
                    )}
                  </BarChart>
                </ResponsiveContainer>
              ) : (
                <div
                  style={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    fontSize: 16,
                    color: "#6b7280",
                    fontWeight: 600,
                  }}
                >
                  No data available
                </div>
              )}
            </div>

            
            <div className="chart-card small" style={{ position: "relative" }}>
              <div className="chart-header">Pie Chart</div>
              {pieChartData.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={pieChartData}
                      cx="50%"
                      cy="50%"
                      innerRadius={70}
                      outerRadius={100}
                      dataKey="value"
                      paddingAngle={2}
                    >
                      {pieChartData.map((entry, idx) => (
                        <Cell key={idx} fill={COLORS[idx % COLORS.length]} />
                      ))}
                    </Pie>
                    <Legend
                      layout="vertical"
                      verticalAlign="top"
                      align="right"
                    />
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <div
                  style={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    fontSize: 16,
                    color: "#6b7280",
                    fontWeight: 600,
                  }}
                >
                  No data available
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
