import React, { useState, useEffect } from "react";
import "./PaperPattern.css";
import Header from "../../Components/Header/Header";
import Sidebar from "../../Components/SideBar/SideBar";
import { FaEdit, FaTrash } from "react-icons/fa";
import PaperPatternApi from "../../service/PapersApi/PapersPatternApi";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Swal from "sweetalert2";

export default function PaperPattern() {
  const [form, setForm] = useState({
    subject: "",
    type: "",
    patternName: "",
    noOfQuestions: "",
    requiredQuestions: "",
    negativeMarks: "",
    marks: "",
    mcq: "",
    descriptive: "",
  });

  const [patterns, setPatterns] = useState([]);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [editId, setEditId] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  const toggleSidebar = () => setSidebarOpen((s) => !s);

  useEffect(() => {
    fetchPatterns();
  }, []);

  async function fetchPatterns() {
    try {
      const response = await PaperPatternApi.getAll();
      setPatterns(response.data || []);
    } catch (error) {
      console.error("Error fetching paper patterns:", error);
    }
  }

  function handleChange(e) {
    const { name, value } = e.target;

    if (name === "type") {
      setForm((s) => {
        const total = Number(s.noOfQuestions) || 0;
        let mcq = 0,
          desc = 0;
        if (value === "MCQ") mcq = total;
        else if (value === "DESCRIPTIVE") desc = total;
        else if (value === "MCQ_DESCRIPTIVE") {
          mcq = Math.floor(total / 2);
          desc = total - mcq;
        }
        return { ...s, type: value, mcq, descriptive: desc };
      });
    } else if (name === "noOfQuestions") {
      setForm((s) => {
        const total = Number(value) || 0;
        let mcq = 0,
          desc = 0;
        if (s.type === "MCQ") mcq = total;
        else if (s.type === "DESCRIPTIVE") desc = total;
        else if (s.type === "MCQ_DESCRIPTIVE") {
          mcq = Math.floor(total / 2);
          desc = total - mcq;
        }
        return { ...s, noOfQuestions: value, mcq, descriptive: desc };
      });
    } else {
      setForm((s) => ({ ...s, [name]: value }));
    }
  }

  // async function handleSubmit(e) {
  //   e.preventDefault();

  //   const payload = {
  //     subject: form.subject,
  //     type: form.type,
  //     patternName: form.patternName,
  //     noOfQuestion: Number(form.noOfQuestions),
  //     requiredQuestion: Number(form.requiredQuestions),
  //     negativeMarks: Number(form.negativeMarks),
  //     marks: Number(form.marks),
  //     mcq: Number(form.mcq),
  //     descriptive: Number(form.descriptive),
  //   };

  //   try {
  //     if (editId) {
  //       await PaperPatternApi.update(editId, payload);
  //       alert("Paper Pattern Updated Successfully!");
  //     } else {
  //       await PaperPatternApi.add(payload);
  //       alert("Paper Pattern Added Successfully!");
  //     }
  //     fetchPatterns();
  //     resetForm();
  //   } catch (error) {
  //     console.error("Error saving paper pattern:", error);
  //     alert("Failed to save paper pattern.");
  //   }
  // }

  async function handleSubmit(e) {
    e.preventDefault();

    // 🔴 VALIDATION
    if (
      !form.subject ||
      !form.type ||
      !form.patternName ||
      !form.noOfQuestions ||
      !form.requiredQuestions ||
      !form.marks
    ) {
      toast.warning("Please fill all required fields!");
      return;
    }

    
    if (Number(form.requiredQuestions) > Number(form.noOfQuestions)) {
      toast.warning(
        "Required questions cannot be greater than total questions!"
      );
      return;
    }

    const payload = {
      subject: form.subject,
      type: form.type,
      patternName: form.patternName,
      noOfQuestion: Number(form.noOfQuestions),
      requiredQuestion: Number(form.requiredQuestions),
      negativeMarks: Number(form.negativeMarks || 0),
      marks: Number(form.marks),
      mcq: Number(form.mcq || 0),
      descriptive: Number(form.descriptive || 0),
    };

    try {
      if (editId) {
        await PaperPatternApi.update(editId, payload);
        toast.success("Paper Pattern Updated Successfully!");
      } else {
        await PaperPatternApi.add(payload);
        toast.success("Paper Pattern Added Successfully!");
      }

      fetchPatterns();
      resetForm();
    } catch (error) {
      console.error(error);
      toast.error("Failed to save paper pattern.");
    }
  }

  function handleEditClick(pattern) {
    setEditId(pattern.paperPatternId);
    setForm({
      subject: pattern.subject || "",
      type: pattern.type,
      patternName: pattern.patternName,
      noOfQuestions: pattern.noOfQuestion,
      requiredQuestions: pattern.requiredQuestion,
      negativeMarks: pattern.negativeMarks,
      marks: pattern.marks,
      mcq: pattern.mcq,
      descriptive: pattern.descriptive,
    });
  }

  // async function handleDeleteClick(id) {
  //   if (!window.confirm("Are you sure you want to delete?")) return;

  //   try {
  //     await PaperPatternApi.delete(id);
  //     alert("Paper Pattern Deleted Successfully!");
  //     fetchPatterns();
  //   } catch (error) {
  //     console.error("Error deleting paper pattern:", error);
  //     alert("Failed to delete paper pattern.");
  //   }
  // }

  async function handleDeleteClick(id) {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "You want to delete this paper pattern!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Yes, delete it!",
      cancelButtonText: "Cancel",
    });

    if (!result.isConfirmed) return;

    try {
      await PaperPatternApi.delete(id);
      toast.success("Paper Pattern Deleted Successfully!");
      fetchPatterns();
    } catch (error) {
      console.error(error);
      toast.error("Failed to delete paper pattern.");
    }
  }

  function resetForm() {
    setForm({
      subject: "",
      type: "",
      patternName: "",
      noOfQuestions: "",
      requiredQuestions: "",
      negativeMarks: "",
      marks: "",
      mcq: "",
      descriptive: "",
    });
    setEditId(null);
  }

  
  const totalPages = Math.ceil(patterns.length / itemsPerPage);
  const reversedPatterns = [...patterns].reverse();
  const paginatedData = reversedPatterns.slice(
    (currentPage - 1) * itemsPerPage, 
    currentPage * itemsPerPage 
  );

  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  
  const subjects = React.useMemo(() => {
    return [...new Set(patterns.map((p) => p.subject).filter(Boolean))];
  }, [patterns]);

  return (
    <div className="pp-root">
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

      <div className="pp-page">
        <div className="pp-container">
          <header className="pp-header">
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
            <h1 className="pp-title">
              {editId ? "Edit Paper Pattern" : "Create Paper Pattern"}
            </h1>
          </header>

          {/* Form */}
          <div className="pp-card pp-form-card">
            <form className="pp-form-grid" onSubmit={handleSubmit}>
              <div className="pp-field">
                <label>Subject</label>
                <select
                  name="subject"
                  value={form.subject}
                  onChange={handleChange}
                >
                  <option value="">Select Subject</option>
                  {subjects.map((subj) => (
                    <option key={subj} value={subj}>
                      {subj}
                    </option>
                  ))}
                </select>
              </div>

              <div className="pp-field">
                <label>Type</label>
                <select name="type" value={form.type} onChange={handleChange}>
                  <option value="">Select Type</option>
                  <option value="MCQ">MCQ</option>
                  <option value="DESCRIPTIVE">DESCRIPTIVE</option>
                  <option value="MCQ_DESCRIPTIVE">MCQ_DESCRIPTIVE</option>
                </select>
              </div>
              <div className="pp-field">
                <label>Pattern Name</label>
                <input
                  name="patternName"
                  value={form.patternName}
                  onChange={handleChange}
                  placeholder="Pattern Name"
                />
              </div>
              <div className="pp-field">
                <label>No of Questions</label>
                <input
                  name="noOfQuestions"
                  type="number"
                  value={form.noOfQuestions}
                  onChange={handleChange}
                />
              </div>
              <div className="pp-field">
                <label>Required Questions</label>
                <input
                  name="requiredQuestions"
                  type="number"
                  value={form.requiredQuestions}
                  onChange={handleChange}
                />
              </div>
              <div className="pp-field">
                <label>Negative Marks</label>
                <input
                  name="negativeMarks"
                  type="number"
                  value={form.negativeMarks}
                  onChange={handleChange}
                />
              </div>
              <div className="pp-field">
                <label>Marks</label>
                <input
                  name="marks"
                  type="number"
                  value={form.marks}
                  onChange={handleChange}
                />
              </div>

              {form.type === "MCQ_DESCRIPTIVE" && (
                <>
                  <div className="pp-field">
                    <label>MCQ</label>
                    <input
                      name="mcq"
                      type="number"
                      value={form.mcq}
                      onChange={handleChange}
                    />
                  </div>
                  <div className="pp-field">
                    <label>Descriptive</label>
                    <input
                      name="descriptive"
                      type="number"
                      value={form.descriptive}
                      onChange={handleChange}
                    />
                  </div>
                </>
              )}
              {form.type === "MCQ" && (
                <div className="pp-field">
                  <label>MCQ</label>
                  <input
                    name="mcq"
                    type="number"
                    value={form.mcq}
                    onChange={handleChange}
                  />
                </div>
              )}
              {form.type === "DESCRIPTIVE" && (
                <div className="pp-field">
                  <label>Descriptive</label>
                  <input
                    name="descriptive"
                    type="number"
                    value={form.descriptive}
                    onChange={handleChange}
                  />
                </div>
              )}

              <div className="pp-submit-wrap">
                <button type="submit" className="pp-submit">
                  {editId ? "Update" : "Submit"}
                </button>
                {editId && (
                  <button
                    type="button"
                    className="pp-cancel"
                    onClick={resetForm}
                  >
                    Cancel
                  </button>
                )}
              </div>
            </form>
          </div>

          
          <div className="pp-card pp-table-card">
            <div className="pp-table-wrap">
              <table className="pp-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Subject</th>
                    <th>Type</th>
                    <th>Pattern Name</th>
                    <th>No. of Questions</th>
                    <th>Required Questions</th>
                    <th>Negative Marks</th>
                    <th>Marks</th>
                    <th>MCQ</th>
                    <th>Descriptive</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedData.length === 0 ? (
                    <tr>
                      <td colSpan="11" style={{ textAlign: "center" }}>
                        No data yet. Add a pattern.
                      </td>
                    </tr>
                  ) : (
                    paginatedData.map((p) => (
                      <tr key={p.paperPatternId}>
                        <td>{p.paperPatternId}</td>
                        <td>{p.subject}</td>
                        <td>{p.type}</td>
                        <td>{p.patternName}</td>
                        <td>{p.noOfQuestion}</td>
                        <td>{p.requiredQuestion}</td>
                        <td>{p.negativeMarks}</td>
                        <td>{p.marks}</td>
                        <td>{p.mcq}</td>
                        <td>{p.descriptive}</td>
                        {/* <td className="pp-action-buttons">
                          <button
                            className="pp-edit-button"
                            type="button"
                            onClick={() => handleEditClick(p)}
                          >
                            <FaEdit />
                          </button>
                          <button
                            className="pp-delete-button"
                            type="button"
                            onClick={() => handleDeleteClick(p.paperPatternId)}
                          >
                            <FaTrash />
                          </button>
                        </td> */}

                        <div className="pp-action-buttons">
                          <button
                            className="pp-edit-button"
                            onClick={() => handleEditClick(p)}
                          >
                            <FaEdit />
                          </button>

                          <button
                            className="pp-delete-button"
                            onClick={() => handleDeleteClick(p.paperPatternId)}
                          >
                            <FaTrash />
                          </button>
                        </div>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            
            {totalPages > 1 && (
              <div className="pp-pagination">
                <div className="pp-pagination-controls">
                  <button
                    className={`pp-pagination-button ${
                      currentPage === 1 ? "pp-pagination-button-disabled" : ""
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
                      className={`pp-pagination-page ${
                        currentPage === i + 1 ? "pp-pagination-page-active" : ""
                      }`}
                    >
                      {i + 1}
                    </button>
                  ))}

                  {totalPages > 3 && (
                    <>
                      <span className="pp-pagination-dots">...</span>
                      <button
                        onClick={() => handlePageChange(totalPages)}
                        className={`pp-pagination-page ${
                          currentPage === totalPages
                            ? "pp-pagination-page-active"
                            : ""
                        }`}
                      >
                        {totalPages}
                      </button>
                    </>
                  )}

                  <button
                    className={`pp-pagination-button ${
                      currentPage === totalPages
                        ? "pp-pagination-button-disabled"
                        : ""
                    }`}
                    disabled={currentPage === totalPages}
                    onClick={() => handlePageChange(currentPage + 1)}
                  >
                    Next
                  </button>
                </div>

                <div className="pp-pagination-dropdown">
                  <select
                    className="pp-pagination-select"
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
      </div>
    </div>
  );
}
