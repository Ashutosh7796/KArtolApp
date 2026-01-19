import React, { useState, useEffect } from "react";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import "./Paper.css";
import Header from "../../Components/Header/Header";
import Sidebar from "../../Components/SideBar/SideBar";
import PaperApi from "../../service/PapersApi/PaperApi";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Swal from "sweetalert2";
const Paper = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const toggleSidebar = () => setSidebarOpen((s) => !s);

  const [papers, setPapers] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);

  const token = localStorage.getItem("token");

  const fetchPapers = async () => {
    setLoading(true);
    try {
      const res = await PaperApi.getAllPapers(
        currentPage - 1, 
        itemsPerPage,
        token
      );

      setPapers(res.data?.content || res.data?.data || []);
      setTotalPages(res.data?.totalPages || 1);
    } catch (err) {
      console.error("Failed to fetch papers", err);
      toast.error("Failed to load papers");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPapers();
  }, [currentPage, itemsPerPage]);

  const formatDate = (isoStr) => {
    if (!isoStr) return "";
    const d = new Date(isoStr);
    return isNaN(d) ? isoStr : d.toLocaleString();
  };

  const exportRowXLSX = async (row) => {
    const result = await Swal.fire({
      title: "Export Paper?",
      text: `Do you want to export Paper ID ${row.paperId}?`,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, export",
    });

    if (!result.isConfirmed) return;

    try {
      const headers = [
        "Paper ID",
        "Title",
        "Description",
        "Start Time",
        "End Time",
        "Is Live",
        "Class",
        "No. of Questions",
        "Pattern Name",
      ];

      const values = [
        row.paperId,
        row.title,
        row.description,
        formatDate(row.startTime),
        formatDate(row.endTime),
        row.isLive ? "Yes" : "No",
        row.studentClass,
        row.noOfQuestions,
        row.patternName,
      ];

      const ws = XLSX.utils.aoa_to_sheet([headers, values]);

      ws["!cols"] = [
        { wpx: 80 },
        { wpx: 200 },
        { wpx: 220 },
        { wpx: 160 },
        { wpx: 160 },
        { wpx: 80 },
        { wpx: 80 },
        { wpx: 120 },
        { wpx: 180 },
      ];

      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, "Paper");

      const wbout = XLSX.write(wb, { bookType: "xlsx", type: "array" });
      saveAs(
        new Blob([wbout], { type: "application/octet-stream" }),
        `paper-${row.paperId}.xlsx`
      );

      toast.success("Paper exported successfully!");
    } catch (error) {
      console.error(error);
      toast.error("Export failed!");
    }
  };

  return (
    <>
      <Header />
      <ToastContainer
        position="top-center"
        autoClose={2000}
        hideProgressBar={false}
        closeOnClick
        pauseOnHover
        draggable
        theme="colored"
      />

      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="Papers-root">
        <div className="Papers-topbar">
          <button
            className="pp-hamburger"
            aria-label="Toggle menu"
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
          <h1>Papers</h1>
        </div>

        <div className="papers-container">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <table className="papers-table">
              <thead>
                <tr>
                  <th>Paper ID</th>
                  <th>Title</th>
                  <th>Description</th>
                  <th>Start Time</th>
                  <th>End Time</th>
                  <th>Is Live</th>
                  <th>Class</th>
                  <th>No. Q</th>
                  <th>Pattern</th>
                  <th>Export</th>
                </tr>
              </thead>
              <tbody>
                {papers.map((paper) => (
                  <tr key={paper.paperId}>
                    <td>{paper.paperId}</td>
                    <td>{paper.title}</td>
                    <td>{paper.description}</td>
                    <td>{formatDate(paper.startTime)}</td>
                    <td>{formatDate(paper.endTime)}</td>
                    <td>{paper.isLive ? "Yes" : "No"}</td>
                    <td>{paper.studentClass}</td>
                    <td>{paper.noOfQuestions}</td>
                    <td>{paper.patternName}</td>
                    <td>
                      <button
                        className="export-btn"
                        onClick={() => exportRowXLSX(paper)}
                      >
                        Export File
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {totalPages > 1 && (
            <div className="paper-pagination-wrapper">
              <div className="paper-pagination-controls">
              
                <button
                  className={`paper-pagination-btn ${
                    currentPage === 1 ? "paper-pagination-btn-disabled" : ""
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
                    className={`paper-pagination-page ${
                      currentPage === i + 1
                        ? "paper-pagination-page-active"
                        : ""
                    }`}
                  >
                    {i + 1}
                  </button>
                ))}

               
                {totalPages > 3 && (
                  <>
                    <span className="paper-pagination-dots">...</span>
                    <button
                      onClick={() => handlePageChange(totalPages)}
                      className={`paper-pagination-page ${
                        currentPage === totalPages
                          ? "paper-pagination-page-active"
                          : ""
                      }`}
                    >
                      {totalPages}
                    </button>
                  </>
                )}

                {/* Next */}
                <button
                  className={`paper-pagination-btn ${
                    currentPage === totalPages
                      ? "paper-pagination-btn-disabled"
                      : ""
                  }`}
                  disabled={currentPage === totalPages}
                  onClick={() => handlePageChange(currentPage + 1)}
                >
                  Next
                </button>
              </div>

              
              <div className="paper-pagination-dropdown">
                <select
                  className="paper-pagination-select"
                  value={itemsPerPage}
                  onChange={(e) => {
                    setItemsPerPage(Number(e.target.value));
                    setCurrentPage(1);
                  }}
                >
                  {[5, 10, 20, 50].map((count) => (
                    <option key={count} value={count}>
                      {count} rows
                    </option>
                  ))}
                </select>
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Paper;
