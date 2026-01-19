import { useState, useEffect } from "react";
import "../../Modules/Fees/Fees.css";
import Header from "../../Components/Header/Header";
import Sidebar from "../../Components/SideBar/SideBar";
import FeesApi from "../../service/FeesApi/FeesAPi";

export default function Fees() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const [selectedStudent, setSelectedStudent] = useState("");
  const [selectedClass, setSelectedClass] = useState("");
  const [selectedBatch, setSelectedBatch] = useState("");

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const toggleSidebar = () => setSidebarOpen((s) => !s);
  const closeSidebar = () => setSidebarOpen(false);

  
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const response = await FeesApi.getAllFees();
        setData(Array.isArray(response.data.data) ? response.data.data : []);
      } catch (error) {
        console.error("Failed to fetch fees data:", error);
        setData([]);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const studentNames = [...new Set(data.map((row) => row.name))];
  const classes = [...new Set(data.map((row) => row.studentClass))];
  const batches = [...new Set(data.map((row) => row.batch))];

  
  const filteredData = data.filter((row) => {
    const studentMatch = selectedStudent === "" || row.name === selectedStudent;
    const classMatch = selectedClass === "" || row.studentClass === selectedClass;
    const batchMatch = selectedBatch === "" || row.batch === selectedBatch;
    return studentMatch && classMatch && batchMatch;
  });

  
  const totalPages = Math.ceil(filteredData.length / itemsPerPage) || 1;
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentData = filteredData.slice(startIndex, startIndex + itemsPerPage);

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  return (
    <>
      <Header />
      <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} />

      <div className="fees-page">
        <div className="fees-header">
          <div className="fees-header-left">
            <button
              className="fees-hamburger"
              aria-label="Open menu"
              aria-expanded={sidebarOpen}
              onClick={toggleSidebar}
              type="button"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="20"
                height="14"
                viewBox="0 0 20 14"
                fill="none"
                aria-hidden="true"
                focusable="false"
              >
                <rect width="20" height="2" rx="1" fill="currentColor" />
                <rect y="6" width="12" height="2" rx="1" fill="currentColor" />
                <rect y="12" width="20" height="2" rx="1" fill="currentColor" />
              </svg>
            </button>
            <h2 className="fees-title">Fees</h2>
          </div>

          
          <div className="fees-filters">
            <select
              value={selectedStudent}
              onChange={(e) => {
                setSelectedStudent(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option value="">Select Student</option>
              {studentNames.map((name) => (
                <option key={name} value={name}>{name}</option>
              ))}
            </select>

            <select
              value={selectedClass}
              onChange={(e) => {
                setSelectedClass(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option value="">Select Class</option>
              {classes.map((cls) => (
                <option key={cls} value={cls}>{cls}</option>
              ))}
            </select>

            <select
              value={selectedBatch}
              onChange={(e) => {
                setSelectedBatch(e.target.value);
                setCurrentPage(1);
              }}
            >
              <option value="">Select Batch</option>
              {batches.map((batch) => (
                <option key={batch} value={batch}>{batch}</option>
              ))}
            </select>
          </div>
        </div>

        <div className="fees-content">
          <div className="fees-table-container">
            {loading ? (
              <div className="fees-no-data">Loading...</div>
            ) : filteredData.length === 0 ? (
              <div className="fees-no-data">No records found</div>
            ) : (
              <>
                <table className="fees-table">
                  <thead className="fees-table-head">
                    <tr className="fees-table-row">
                      <th className="fees-table-header">ID</th>
                      <th className="fees-table-header">Student Name</th>
                      <th className="fees-table-header">Class</th>
                      <th className="fees-table-header">Fee</th>
                      <th className="fees-table-header">Type</th>
                      <th className="fees-table-header">Status</th>
                      <th className="fees-table-header">Date</th>
                      <th className="fees-table-header">Batch</th>
                    </tr>
                  </thead>
                  <tbody className="fees-table-body">
                    {currentData.map((row) => (
                      <tr key={row.feesId} className="fees-table-row">
                        <td className="fees-table-cell" data-label="ID">{row.feesId}</td>
                        <td className="fees-table-cell" data-label="Student Name">{row.name}</td>
                        <td className="fees-table-cell" data-label="Class">{row.studentClass}</td>
                        <td className="fees-table-cell" data-label="Fee">{row.fee}</td>
                        <td className="fees-table-cell" data-label="Type">{row.type}</td>
                        <td className="fees-table-cell" data-label="Status">{row.status}</td>
                        <td className="fees-table-cell" data-label="Date">{row.date}</td>
                        <td className="fees-table-cell" data-label="Batch">{row.batch}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>

              
                {filteredData.length > 0 && (
                  <div className="fees-pagination">
                    <div className="fees-pagination-controls">
                      <button
                        className="fees-pagination-button"
                        onClick={() => handlePageChange(currentPage - 1)}
                        disabled={currentPage === 1}
                      >
                        Previous
                      </button>

                      {Array.from({ length: Math.min(totalPages, 3) }, (_, i) => (
                        <button
                          key={i + 1}
                          onClick={() => handlePageChange(i + 1)}
                          className={`fees-pagination-page ${currentPage === i + 1 ? "fees-pagination-page-active" : ""}`}
                        >
                          {i + 1}
                        </button>
                      ))}

                      {totalPages > 3 && (
                        <>
                          <span className="fees-pagination-dots">...</span>
                          <button
                            onClick={() => handlePageChange(totalPages)}
                            className={`fees-pagination-page ${currentPage === totalPages ? "fees-pagination-page-active" : ""}`}
                          >
                            {totalPages}
                          </button>
                        </>
                      )}

                      <button
                        className="fees-pagination-button"
                        onClick={() => handlePageChange(currentPage + 1)}
                        disabled={currentPage === totalPages}
                      >
                        Next
                      </button>
                    </div>

                    <div className="fees-pagination-dropdown">
                      <select
                        className="fees-pagination-select"
                        value={itemsPerPage}
                        onChange={(e) => {
                          setItemsPerPage(Number(e.target.value));
                          setCurrentPage(1);
                        }}
                      >
                        {[5, 10, 20, 50].map((num) => (
                          <option key={num} value={num}>{num}</option>
                        ))}
                      </select>
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
