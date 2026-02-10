import React, { useState, useEffect } from "react";
import "./List.css";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
import ListApi from "../../../service/TeacherApi/ListApi";
import { ToastContainer, toast } from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import Swal from "sweetalert2";

export default function List() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [teachers, setTeachers] = useState([]);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const toggleSidebar = () => setSidebarOpen((s) => !s);

  useEffect(() => {
    fetchTeachers();
  }, []);

  const fetchTeachers = async () => {
    try {
      const res = await ListApi.getAll();
      const teachersArray = res.data?.data || [];
      const formattedArray = Array.isArray(teachersArray)
        ? teachersArray
        : [teachersArray];

      if (formattedArray.length === 0) {
        
        Swal.fire({
          icon: "info",
          title: "No teachers found",
          text: "There are no teacher records available.",
        });
      } else {
        toast.success(`Fetched ${formattedArray.length} teachers!`, {
          position: "top-center",
          autoClose: 2000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
        });
      }

      setTeachers(formattedArray);
    } catch (err) {
      console.error("Error fetching teachers:", err);
      toast.error("Failed to fetch teachers!", {
        position: "top-center",
        autoClose: 3000,
      });
    }
  };

  const totalPages = Math.ceil(teachers.length / itemsPerPage) || 1;
  const currentTeachers = teachers.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  return (
    <>
      <Header />
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      
      <ToastContainer />

      <div className="list-page">
        <div className="list-root">

          <header className="list-header">
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
            <h1 className="list-title">List</h1>
          </header>

          <div className="list-card">
            <div className="table-wrap">
              <table className="list-table" role="table" aria-label="list table">
                <thead>
                  <tr>
                    <th>Sr. No.</th>
                    <th>Teacher Name</th>
                    <th>Subject</th>
                    <th>Degree</th>
                    <th>Mobile Number</th>
                    <th>Status</th>
                  </tr>
                </thead>

                <tbody>
                  {currentTeachers.length === 0 ? (
                    <tr>
                      <td colSpan="6" style={{ textAlign: "center" }}>
                        No teachers found
                      </td>
                    </tr>
                  ) : (
                    currentTeachers.map((t, index) => (
                      <tr key={t.teacherId || index}>
                        <td>{(currentPage - 1) * itemsPerPage + index + 1}</td>
                        <td>{t.name}</td>
                        <td>{t.sub}</td>
                        <td>{t.deg}</td>
                        <td>{t.mobileNumber}</td>
                        <td>{t.status}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {teachers.length > 0 && (
              <div className="list-pagination">
                <div className="list-pagination-controls">
                  <button
                    className={`list-pagination-button ${currentPage === 1 ? "list-disabled" : ""}`}
                    disabled={currentPage === 1}
                    onClick={() => setCurrentPage(currentPage - 1)}
                  >
                    Previous
                  </button>

                  {Array.from({ length: Math.min(totalPages, 3) }, (_, i) => (
                    <button
                      key={i + 1}
                      className={`list-page-btn ${currentPage === i + 1 ? "list-page-active" : ""}`}
                      onClick={() => setCurrentPage(i + 1)}
                    >
                      {i + 1}
                    </button>
                  ))}

                  {totalPages > 3 && (
                    <>
                      <span className="list-pagination-dots">...</span>
                      <button
                        className={`list-page-btn ${currentPage === totalPages ? "list-page-active" : ""}`}
                        onClick={() => setCurrentPage(totalPages)}
                      >
                        {totalPages}
                      </button>
                    </>
                  )}

                  <button
                    className={`list-pagination-button ${currentPage === totalPages ? "list-disabled" : ""}`}
                    disabled={currentPage === totalPages}
                    onClick={() => setCurrentPage(currentPage + 1)}
                  >
                    Next
                  </button>
                </div>

                <div className="list-pagination-dropdown">
                  <select
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
            )}
          </div>

        </div>
      </div>
    </>
  );
}
