import { useState, useEffect } from "react";
import "../Exams/Exams.css";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
import ExamApi from "./../../../service/StudentsApi/ExamApi";

export default function Exam() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const [classes, setClasses] = useState([]);
  const [className, setClassName] = useState("Select Class");

  const [studentDropdown, setStudentDropdown] = useState([]);
  const [student, setStudent] = useState("Student Count");

  const [years, setYears] = useState([]);
  const [year, setYear] = useState("Select Year");

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
    ExamApi.getClassesDropdown()
      .then((res) => {
        const data = Array.isArray(res.data) ? res.data : res.data?.data || [];
        setClasses(data);
      })
      .catch(() => setClasses([]));
  }, []);

  useEffect(() => {
    if (className === "Select Class") {
      setStudentDropdown([]);
      setStudent("Student Count");
      setYears([]);
      setYear("Select Year");
      setCurrentPage(1);
      return;
    }

    ExamApi.getBatchYearsDropdown(className)
      .then((res) => {
        const data = Array.isArray(res.data) ? res.data : res.data?.data || [];
        setYears(data);
        setYear("Select Year");
      })
      .catch(() => {
        setYears([]);
        setYear("Select Year");
      });

    setStudentDropdown([]);
    setStudent("Student Count");
    setCurrentPage(1);
  }, [className]);

  useEffect(() => {
    if (className === "Select Class" || year === "Select Year") {
      setStudentDropdown([]);
      setStudent("Student Count");
      return;
    }

    ExamApi.getStudentCountDropdown(className, year)
      .then((res) => {
        const data = Array.isArray(res.data) ? res.data : res.data?.data || [];
        setStudentDropdown(data);
        setStudent("Student Count");
      })
      .catch(() => {
        setStudentDropdown([]);
        setStudent("Student Count");
      });
  }, [className, year]);

  useEffect(() => {
    setLoading(true);

    if (className === "Select Class" || year === "Select Year") {
      ExamApi.getAllStudentExams()
        .then((res) => {
          const data = Array.isArray(res.data)
            ? res.data
            : res.data?.data || [];
          setStudents(data);
          setCurrentPage(1);
        })
        .catch(() => setStudents([]))
        .finally(() => setLoading(false));
      return;
    }

    const payload = {
      class: className,
      batch: year,
      ...(student !== "Student Count" && { studentName: student }),
    };

    ExamApi.getStudentExam(payload)
      .then((res) => {
        const data = Array.isArray(res.data) ? res.data : res.data?.data || [];
        setStudents(data);
        setCurrentPage(1);
      })
      .catch(() => setStudents([]))
      .finally(() => setLoading(false));
  }, [className, student, year]);

  const totalPages = Math.ceil(students.length / itemsPerPage) || 1;
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentStudents = students.slice(startIndex, startIndex + itemsPerPage);

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) setCurrentPage(page);
  };

  useEffect(() => {
    setCurrentPage(1);
  }, [itemsPerPage]);


  const getPaginationPages = () => {
    let start = Math.max(1, currentPage - 1);
    let end = Math.min(totalPages, start + 2);

    if (end - start < 2) {
      start = Math.max(1, end - 2);
    }

    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  };

  return (
    <>
      <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} />
      <Header />

      <div className="exam-page">
        <div className="exam-header">
          <div className="exam-header-left">
            <button
              type="button"
              className="exam-hamburger"
              aria-label={sidebarOpen ? "Close menu" : "Open menu"}
              aria-expanded={sidebarOpen}
              onClick={toggleSidebar}
            >
              ☰
            </button>
            <span className="exam-title">Exams</span>
          </div>

          <div className="exam-filters">
            <select
              className="exam-filter-select"
              value={className}
              onChange={(e) => {
                setClassName(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option>Select Class</option>
              {classes.map((c, i) => (
                <option key={i} value={c}>
                  {c}
                </option>
              ))}
            </select>

            <select
              className="exam-filter-select"
              value={year}
              onChange={(e) => {
                setYear(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option>Select Year</option>
              {years.map((y, i) => (
                <option key={i} value={y}>
                  {y}
                </option>
              ))}
            </select>

            <select
              className="exam-filter-select"
              value={student}
              onChange={(e) => {
                setStudent(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option>Student Count</option>
              {studentDropdown.map((s, i) => (
                <option key={i} value={s.studentName || s}>
                  {s.studentName || s}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="exam-content">
          <div className="exam-table-container">
            {loading ? (
              <div className="exam-no-data">Loading...</div>
            ) : currentStudents.length > 0 ? (
              <>
                <table className="exam-table">
                  <thead className="exam-table-head">
                    <tr className="exam-table-row">
                      <th className="exam-table-header">Sr. No.</th>
                      <th className="exam-table-header">Student Name</th>
                      <th className="exam-table-header">Exam</th>
                      <th className="exam-table-header">Date</th>
                    </tr>
                  </thead>
                  <tbody className="exam-table-body">
                    {currentStudents.map((s, i) => (
                      <tr key={s.id || i} className="exam-table-row">
                        <td className="exam-table-cell" data-label="Sr">
                          {startIndex + i + 1}
                        </td>
                        <td className="exam-table-cell" data-label="Name">
                          {s.studentName}
                        </td>
                        <td className="exam-table-cell" data-label="Exam">
                          {s.exam}
                        </td>
                        <td className="exam-table-cell" data-label="Date">
                          {s.startDate}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                {/* ✅ Pagination: Buttons left, dropdown right */}
                <div
                  className="exam-pagination-container"
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    marginTop: "10px",
                  }}
                >
                  <div
                    className="exam-pagination-controls"
                    style={{
                      display: "flex",
                      gap: "5px",
                      alignItems: "center",
                    }}
                  >
                    <button
                      className={`exam-pagination-button ${
                        currentPage === 1
                          ? "exam-pagination-button-disabled"
                          : ""
                      }`}
                      disabled={currentPage === 1}
                      onClick={() => handlePageChange(currentPage - 1)}
                    >
                      Previous
                    </button>

                    {getPaginationPages().map((page) => (
                      <button
                        key={page}
                        onClick={() => handlePageChange(page)}
                        className={`exam-pagination-page ${
                          currentPage === page
                            ? "exam-pagination-page-active"
                            : ""
                        }`}
                      >
                        {page}
                      </button>
                    ))}

                    <button
                      className={`exam-pagination-button ${
                        currentPage === totalPages
                          ? "exam-pagination-button-disabled"
                          : ""
                      }`}
                      disabled={currentPage === totalPages}
                      onClick={() => handlePageChange(currentPage + 1)}
                    >
                      Next
                    </button>
                  </div>

                  <div className="exam-pagination-dropdown">
                    <select
                      className="exam-pagination-select"
                      value={itemsPerPage}
                      onChange={(e) => setItemsPerPage(Number(e.target.value))}
                    >
                      {[5, 10, 20, 50].map((count) => (
                        <option key={count} value={count}>
                          {count}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              </>
            ) : (
              <div className="exam-no-data">No exams found</div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
