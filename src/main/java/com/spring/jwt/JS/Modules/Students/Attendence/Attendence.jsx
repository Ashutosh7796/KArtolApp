import { useEffect, useState } from "react";
import "./Attendence.css";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
import AttendanceApi from "../../../service/StudentsApi/AttendanceApi";

const extractAttendanceData = (res) => {
  const data =
    res?.data?.data?.students ||
    res?.data?.data ||
    res?.data?.students ||
    res?.data ||
    [];

  return Array.isArray(data) ? data : [];
};

export default function Attendence() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const [classDropdown, setClassDropdown] = useState([]);
  const [className, setClassName] = useState("");

  const [yearDropdown, setYearDropdown] = useState([]);
  const [year, setYear] = useState("");

  const [studentDropdown, setStudentDropdown] = useState([]);
  const [student, setStudent] = useState("");

  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const toggleSidebar = (e) => {
    e?.stopPropagation();
    setSidebarOpen((prev) => !prev);
  };

  const closeSidebar = () => setSidebarOpen(false);

  useEffect(() => {
    AttendanceApi.getClassesDropdown()
      .then((res) => setClassDropdown(extractAttendanceData(res)))
      .catch(() => setClassDropdown([]));
  }, []);

  useEffect(() => {
    if (!className) {
      setYearDropdown([]);
      setYear("");
      return;
    }
    AttendanceApi.getBatchYearsDropdown(className)
      .then((res) => setYearDropdown(extractAttendanceData(res)))
      .catch(() => setYearDropdown([]));
  }, [className]);

  useEffect(() => {
    if (!className) {
      setStudentDropdown([]);
      setStudent("");
      return;
    }
    AttendanceApi.getStudentCountDropdown(className, year || undefined)
      .then((res) => setStudentDropdown(extractAttendanceData(res)))
      .catch(() => setStudentDropdown([]));
  }, [className, year]);

  useEffect(() => {
    const fetchAttendance = async () => {
      setLoading(true);
      try {
        let finalData = [];

        if (!className && !year && !student) {
          const res = await AttendanceApi.getAllStudentAttendance();
          finalData = extractAttendanceData(res);
        } else {
          const payload = {
            ...(className && { studentClass: className }),
            ...(year && { batch: year }),
            ...(student && { studentName: student }),
          };
          const res = await AttendanceApi.getStudentAttendance(payload);
          finalData = extractAttendanceData(res);
        }

        setStudents(finalData);
        setCurrentPage(1);
      } catch (err) {
        console.error(err);
        setStudents([]);
      } finally {
        setLoading(false);
      }
    };

    fetchAttendance();
  }, [className, year, student]);

  const totalPages = Math.ceil(students.length / itemsPerPage) || 1;
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentStudents = students.slice(startIndex, startIndex + itemsPerPage);

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) setCurrentPage(page);
  };

  return (
    <>
      <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} />
      <Header />

      <div className="attendence-page">
        <div className="attendence-header">
          <div className="attendence-header-left">
            <button
              className="attendence-hamburger"
              onClick={toggleSidebar}
              aria-expanded={sidebarOpen}
            >
              ☰
            </button>
            <span className="attendence-title">Attendance</span>
          </div>

          <div className="attendence-filters">
            <select
              className="attendence-filter-select"
              value={className}
              onChange={(e) => { setClassName(e.target.value); setCurrentPage(1); }}
            >
              <option value="">Select Classes</option>
              {classDropdown.map((c, i) => (
                <option key={i} value={c}>{c}</option>
              ))}
            </select>

            <select
              className="attendence-filter-select"
              value={year}
              onChange={(e) => { setYear(e.target.value); setCurrentPage(1); }}
            >
              <option value="">Select Years</option>
              {yearDropdown.map((y, i) => (
                <option key={i} value={y}>{y}</option>
              ))}
            </select>

            <select
              className="attendence-filter-select"
              value={student}
              onChange={(e) => { setStudent(e.target.value); setCurrentPage(1); }}
            >
              <option value="">Students Count</option>
              {studentDropdown.map((s, i) => (
                <option key={i} value={s.studentName || s}>
                  {s.studentName || s}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="attendence-content">
          <div className="attendence-table-container">
            {loading ? (
              <div className="attendence-no-data">Loading...</div>
            ) : currentStudents.length > 0 ? (
              <>
                <table className="attendence-table">
                  <thead className="attendence-table-head">
                    <tr className="attendence-table-row">
                      <th>Sr. No.</th>
                      <th>Student Name</th>
                      <th>Exam</th>
                      <th>Class</th>
                      <th>Parent Mobile</th>
                      <th>Avg Present %</th>
                    </tr>
                  </thead>

                  <tbody className="attendence-table-body">
                    {currentStudents.map((s, i) => (
                      <tr key={i} className="attendence-table-row">
                        <td className="attendence-table-cell" data-label="Sr">
                          {startIndex + i + 1}
                        </td>
                        <td className="attendence-table-cell" data-label="Name">
                          {s.studentName}
                        </td>
                        <td className="attendence-table-cell" data-label="Exam">
                          {s.exam}
                        </td>
                        <td className="attendence-table-cell" data-label="Class">
                          {s.studentClass}
                        </td>
                        <td className="attendence-table-cell" data-label="Mobile">
                          {s.mobileNumber}
                        </td>
                        <td className="attendence-table-cell" data-label="Avg %">
                          {s.averagePresentPercentage}%
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                <div className="attendence-pagination">
                  <div className="attendence-pagination-controls">
                    <button
                      className={`attendence-pagination-button ${currentPage === 1 ? "attendence-pagination-button-disabled" : ""}`}
                      disabled={currentPage === 1}
                      onClick={() => handlePageChange(currentPage - 1)}
                    >
                      Previous
                    </button>

                    {Array.from({ length: Math.min(totalPages, 3) }, (_, i) => (
                      <button
                        key={i + 1}
                        onClick={() => handlePageChange(i + 1)}
                        className={`attendence-pagination-page ${currentPage === i + 1 ? "attendence-pagination-page-active" : ""}`}
                      >
                        {i + 1}
                      </button>
                    ))}

                    {totalPages > 3 && (
                      <>
                        <span className="attendence-pagination-dots">...</span>
                        <button
                          onClick={() => handlePageChange(totalPages)}
                          className={`attendence-pagination-page ${currentPage === totalPages ? "attendence-pagination-page-active" : ""}`}
                        >
                          {totalPages}
                        </button>
                      </>
                    )}

                    <button
                      className={`attendence-pagination-button ${currentPage === totalPages ? "attendence-pagination-button-disabled" : ""}`}
                      disabled={currentPage === totalPages}
                      onClick={() => handlePageChange(currentPage + 1)}
                    >
                      Next
                    </button>
                  </div>

                  <div className="attendence-pagination-dropdown">
                    <select
                      className="attendence-pagination-select"
                      value={itemsPerPage}
                      onChange={(e) => { setItemsPerPage(Number(e.target.value)); setCurrentPage(1); }}
                    >
                      {[5, 10, 20, 50].map((count) => (
                        <option key={count} value={count}>{count}</option>
                      ))}
                    </select>
                  </div>
                </div>
              </>
            ) : (
              <div className="attendence-no-data">No data found</div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
