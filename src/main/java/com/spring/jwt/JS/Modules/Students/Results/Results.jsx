import { useState, useEffect } from "react";
import "./Results.css";
import ResultApi from "../../../service/StudentsApi/ResultApi";
import DetailedResult from "./DetailedResult";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";

export default function Results() {
  const [student, setStudent] = useState("Student Name");
  const [className, setClassName] = useState("Select Class");
  const [batch, setBatch] = useState("Select Batch");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const [students, setStudents] = useState([]);
  const [classes, setClasses] = useState([]);
  const [batchYears, setBatchYears] = useState([]);
  const [studentNames, setStudentNames] = useState([]);
  const [loading, setLoading] = useState(false);

  const toggleSidebar = (e) => {
    e?.stopPropagation();
    setSidebarOpen((s) => !s);
  };
  const closeSidebar = () => setSidebarOpen(false);

  useEffect(() => {
    ResultApi.getClasses()
      .then((res) => setClasses(res.data.data || []))
      .catch(() => setClasses([]));
  }, []);

  useEffect(() => {
    if (className !== "Select Class") {
      setLoading(true);

      ResultApi.getStudentsByClass(className)
        .then((res) => {
          const data = res.data.data || [];
          setStudentNames(data.map((s) => s.studentName));
        })
        .catch(() => setStudentNames([]));

      ResultApi.getBatchYears(className)
        .then((res) => setBatchYears(res.data.data || []))
        .catch(() => setBatchYears([]));

      setBatch("Select Batch");
      setStudent("Student Name");
      setStudents([]);
      setLoading(false);
    } else {
      setBatchYears([]);
      setStudentNames([]);
      setBatch("Select Batch");
      setStudent("Student Name");
      setStudents([]);
    }
  }, [className]);

  useEffect(() => {
    setLoading(true);

    if (className !== "Select Class" && batch !== "Select Batch") {
      ResultApi.getStudentResults(className, batch)
        .then((res) => setStudents(res.data.data || []))
        .catch(() => setStudents([]))
        .finally(() => setLoading(false));
    } else {
      ResultApi.getAllStudentResults()
        .then((res) => setStudents(res.data.data || []))
        .catch(() => setStudents([]))
        .finally(() => setLoading(false));
    }
  }, [className, batch]);

  const filteredStudents =
    student === "Student Name"
      ? students
      : students.filter((s) => s.studentName === student);

  const totalPages = Math.ceil(filteredStudents.length / itemsPerPage) || 1;
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentStudents = filteredStudents.slice(
    startIndex,
    startIndex + itemsPerPage
  );

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) setCurrentPage(page);
  };

  const handleStudentClick = async (studentData) => {
    if (!studentData?.id && !studentData?.userId) {
      alert("Student ID is missing, cannot fetch result.");
      return;
    }

    const userId = studentData.id || studentData.userId;

    setLoading(true);
    try {
      const res = await ResultApi.getResultByUserId(
        userId,
        batch !== "Select Batch" ? batch : undefined
      );
      const resultData = res.data.data;

      if (resultData && resultData.length > 0) {
        const matchedResult = resultData.find(
          (r) => r.rollNumber === studentData.rollNumber || r.userId === userId
        );
        setSelectedStudent(matchedResult || resultData[0]);
      } else {
        alert(
          `No results available for ${studentData.studentName}${
            batch !== "Select Batch" ? " in batch " + batch : ""
          }`
        );
      }
    } catch (error) {
      console.error("Failed to fetch student result", error);
      alert(`Error fetching results for ${studentData.studentName}`);
    } finally {
      setLoading(false);
    }
  };

  const handleBackClick = () => setSelectedStudent(null);

  if (selectedStudent) {
    if (loading) return <div className="results-no-data">Loading...</div>;
    return (
      <DetailedResult student={selectedStudent} onBackClick={handleBackClick} />
    );
  }

  return (
    <>
      <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} />
      <Header />

      <div className="results-page">
        <div className="results-header">
          <div className="results-header-left">
            <button
              type="button"
              className="results-hamburger"
              aria-label={sidebarOpen ? "Close menu" : "Open menu"}
              aria-expanded={sidebarOpen}
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
            <span className="results-title">Results</span>
          </div>

          <div className="results-filters">
            <select
              className="results-filter-select"
              value={className}
              onChange={(e) => {
                setClassName(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option>Select Class</option>
              {classes.map((cls) => (
                <option key={cls} value={cls}>
                  {cls}
                </option>
              ))}
            </select>

            <select
              className="results-filter-select"
              value={student}
              onChange={(e) => {
                setStudent(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option>Student Name</option>
              {studentNames.map((name) => (
                <option key={name} value={name}>
                  {name}
                </option>
              ))}
            </select>

            <select
              className="results-filter-select"
              value={batch}
              onChange={(e) => {
                setBatch(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option>Select Batch</option>
              {batchYears.map((b) => (
                <option key={b} value={b}>
                  {b}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="results-content">
          <div className="results-table-container">
            {loading ? (
              <div className="results-no-data">Loading...</div>
            ) : currentStudents.length > 0 ? (
              <>
                <table className="results-table">
                  <thead className="results-table-head">
                    <tr className="results-table-row">
                      <th className="results-table-header">Sr. No.</th>
                      <th className="results-table-header">Student Name</th>
                      <th className="results-table-header">Exam</th>
                      <th className="results-table-header">Date</th>
                      <th className="results-table-header">Marks</th>
                    </tr>
                  </thead>
                  <tbody className="results-table-body">
                    {currentStudents.map((s, idx) => (
                      <tr
                        key={`${s.studentName}-${s.exam}-${s.resultDate}-${idx}`}
                        className="results-table-row"
                      >
                        <td className="results-table-cell" data-label="Sr. No.">
                          {startIndex + idx + 1}
                        </td>
                        <td
                          className="results-table-cell"
                          data-label="Student Name"
                        >
                          <button
                            className="results-student-link"
                            onClick={() => handleStudentClick(s)}
                          >
                            {s.studentName}
                          </button>
                        </td>
                        <td className="results-table-cell" data-label="Exam">
                          {s.exam}
                        </td>
                        <td className="results-table-cell" data-label="Date">
                          {s.resultDate}
                        </td>
                        <td className="results-table-cell" data-label="Marks">
                          {s.marks}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                <div className="results-pagination">
                  <div className="results-pagination-controls">
                    <button
                      className={`results-pagination-button ${
                        currentPage === 1
                          ? "results-pagination-button-disabled"
                          : ""
                      }`}
                      disabled={currentPage === 1}
                      onClick={() => handlePageChange(currentPage - 1)}
                    >
                      Previous
                    </button>

                    {Array.from({ length: Math.min(totalPages, 3) }, (_, i) => (
                      <button
                        key={i + 1}
                        onClick={() => handlePageChange(i + 1)}
                        className={`results-pagination-page ${
                          currentPage === i + 1
                            ? "results-pagination-page-active"
                            : ""
                        }`}
                      >
                        {i + 1}
                      </button>
                    ))}

                    {totalPages > 3 && (
                      <>
                        <span className="results-pagination-dots">...</span>
                        <button
                          onClick={() => handlePageChange(totalPages)}
                          className={`results-pagination-page ${
                            currentPage === totalPages
                              ? "results-pagination-page-active"
                              : ""
                          }`}
                        >
                          {totalPages}
                        </button>
                      </>
                    )}

                    <button
                      className={`results-pagination-button ${
                        currentPage === totalPages
                          ? "results-pagination-button-disabled"
                          : ""
                      }`}
                      disabled={currentPage === totalPages}
                      onClick={() => handlePageChange(currentPage + 1)}
                    >
                      Next
                    </button>
                  </div>

                  <div className="results-pagination-dropdown">
                    <select
                      className="results-pagination-select"
                      value={itemsPerPage}
                      onChange={(e) => {
                        setItemsPerPage(Number(e.target.value));
                        setCurrentPage(1);
                      }}
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
              <div className="results-no-data">No data found</div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
